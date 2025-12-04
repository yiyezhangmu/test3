package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.vo.BaseEntityTypeConstants;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.license.LicenseStatusEnum;
import com.coolcollege.intelligent.common.enums.license.LicenseTypeSourceEnum;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.license.ExtendFieldDTO;
import com.coolcollege.intelligent.model.license.StoreLicenseExportRequest;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.rpc.license.LicenseTypeApiService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.export.MyFileLoader;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.license.client.dto.*;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户导出service
 * @author ：xugangkun
 * @date ：2021/7/23 10:22
 */
@Service
@Slf4j
public class StoreLicenseExportServiceV2 implements BaseExportService {


    @Resource
    private StoreMapper storeMapper;

    @Resource
    private LicenseTypeApiService licenseTypeService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;

    @Resource
    private LicenseApiService licenseApiService;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private UserPersonInfoService userPersonInfoService;
    @Resource
    private RegionDao regionDao;
    @Resource
    private MyFileLoader myFileLoader;


    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        StoreLicenseExportRequest query = (StoreLicenseExportRequest) request;
        return storeMapper.getLicenseStoreCount(enterpriseId,query);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.STORE_LICENSE_EXPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        DataSourceHelper.reset();
        //根据企业id查出当前的企业配置
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreSettingDO enterpriseStoreSetting = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        StoreLicenseExportRequest query = JSONObject.toJavaObject(request, StoreLicenseExportRequest.class);
        List<Map<String, Object>> vos = Lists.newArrayList();
        //区域下门店
        PageHelper.startPage(pageNum, pageSize);
        List<StoreAreaDTO> storeAreaDTOS=   storeMapper.getLicenseStoreList(enterpriseId,query);
        List<String> storeIds = storeAreaDTOS.stream().map(StoreAreaDTO::getStoreId).collect(Collectors.toList());
        List<Long> regionIds = storeAreaDTOS.stream().map(StoreAreaDTO::getRegionPath).flatMap(c -> Arrays.stream(c.split("/")).filter(StringUtils::isNotBlank)).map(Long::valueOf).distinct().collect(Collectors.toList());
        Map<Long, String> regionNameMap = regionDao.getRegionNameMap(enterpriseId, regionIds);
        //查询企业的证照类型
        List<LicenseTypeDTO> licenseTypes = licenseTypeService.getStoreLicenseTypesBySourceOrId(enterpriseConfigDO,LicenseTypeSourceEnum.STORE.getSource(),query.getLicenseTypeId());

        Integer effectiveTime=enterpriseStoreSetting.getStoreLicenseEffectiveTime();
        LicenseQueryDTO licenseQueryDTO = new LicenseQueryDTO();
        licenseQueryDTO.setStoreIds(storeIds);
        licenseQueryDTO.setEnterpriseId(enterpriseId);
        licenseQueryDTO.setDbName(enterpriseConfigDO.getDbName());
        licenseQueryDTO.setSource(LicenseTypeSourceEnum.STORE.getSource());
        licenseQueryDTO.setGetPicture(Boolean.FALSE);
        List<LicenseDTO> licenseDTOS = licenseApiService.queryLicenseByQuery(licenseQueryDTO);
        POICacheManager.setFileLoader(myFileLoader);
        //无需上传门店
        List<String> noNeedStoreIds = getNoNeedUploadStoreIdList(enterpriseId, enterpriseStoreSetting.getNoNeedUploadLicenseRegion());

        //根据店分组
        Map<String, List<LicenseDTO>> storeIdAndLicenses = licenseDTOS.stream().collect(Collectors.groupingBy(LicenseDTO::getStoreId));
        //构建最小行
        for (StoreAreaDTO storeAreaDTO : storeAreaDTOS) {
            Map<String,  Object> vo = new HashMap<>();
            vo.put("storeName", storeAreaDTO.getStoreName());
            vo.put("storeStatus", StoreStatusEnum.getName(storeAreaDTO.getStoreStatus()));
            List<Long> parentRegionIds = Arrays.stream(storeAreaDTO.getRegionPath().split("/")).filter(o->StringUtils.isNotBlank(o) && !Constants.ROOT_DEPT_ID_STR.equals(o)).map(Long::valueOf).collect(Collectors.toList());
            Long firstRegionId = CollectionUtils.isNotEmpty(parentRegionIds) ? parentRegionIds.get(0) : null;
            vo.put("regionName", regionNameMap.get(firstRegionId));
            vo.put("regionPathName", getFullRegionName(storeAreaDTO.getRegionPath(), regionNameMap));
            //拿去门店下证照
            List<LicenseDTO> curStoreLicense = ListUtils.emptyIfNull(storeIdAndLicenses.get(storeAreaDTO.getStoreId()));
            Map<Long, LicenseDTO> typeAndLicenses = curStoreLicense.stream().collect(Collectors.toMap(c -> Long.valueOf(c.getLicenseTypeId()), c -> c));
            boolean isNoNeedUpload = false;
            //判断本店是否在无需上传门店中
            if (noNeedStoreIds.contains(storeAreaDTO.getStoreId())){
                isNoNeedUpload=true;
            }
            for (LicenseTypeDTO licenseType : licenseTypes) {
                List<LicenseTypeExtendFieldDTO> extendFieldList = licenseType.getExtendFieldList();
                LicenseDTO curLicense = typeAndLicenses.get(licenseType.getLicenseTypeId());
                vo.put("licenseStatus_" + licenseType.getLicenseTypeId(), getLicenseStatus(curLicense, effectiveTime, isNoNeedUpload));
                if(Objects.isNull(curLicense)){
                    continue;
                }
                vo.put("endDate_"+licenseType.getLicenseTypeId(), getExpiryDate(curLicense));
                //vo.put("LicenseType_"+licenseType.getLicenseTypeId(), getImageFromNetByUrl(picture));
                String extendFieldInfo = curLicense.getExtendFieldInfo();
                Map<String,  String> fieldMap = new HashMap<>();
                if(CollectionUtils.isNotEmpty(extendFieldList)){
                    if(StringUtils.isNotBlank(extendFieldInfo)){
                        fieldMap = JSONObject.parseArray(extendFieldInfo, ExtendFieldDTO.class).stream().collect(Collectors.toMap(ExtendFieldDTO::getId, ExtendFieldDTO::getValue, (a, b) -> a));
                    }
                    for (LicenseTypeExtendFieldDTO extendField : extendFieldList){
                        if(Objects.nonNull(extendField)){
                            String extendValue = fieldMap.get(String.valueOf(extendField.getId()));
                            if(CollectionUtils.isNotEmpty(extendField.getCaseItems())){
                                String value = "";
                                Map<String, String> itemValue = extendField.getCaseItems().stream().collect(Collectors.toMap(LicenseTypeExtendFieldDTO.CaseItemDTO::getId, LicenseTypeExtendFieldDTO.CaseItemDTO::getName));
                                if(StringUtil.checkIsArray(extendValue)){
                                    List<String> extendValueList = Lists.newArrayList();
                                    for (Object o : JSONObject.parseArray(extendValue)) {
                                        extendValueList.add(itemValue.get(o.toString()));
                                    }
                                    value = String.join(Constants.COMMA, extendValueList);
                                }else{
                                    value = itemValue.get(extendValue);
                                }
                                if(StringUtils.isBlank(value)){
                                    value = extendValue;
                                }
                                vo.put("ExtendField_"+extendField.getId(), value);
                            }else{
                                vo.put("ExtendField_"+extendField.getId(), extendValue);
                            }
                        }
                    }
                }

            }
            vos.add(vo);
        }
        return vos;
    }

    private String getFullRegionName(String regionPath, Map<Long, String> regionNameMap) {
        if(StringUtils.isBlank(regionPath)){
            return  "";
        }
        List<Long> regionIds = Arrays.stream(regionPath.split("/")).filter(StringUtils::isNotBlank).map(Long::valueOf).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (Long regionId : regionIds) {
            if (Objects.nonNull(regionId)) {
                String name = regionNameMap.getOrDefault(regionId, ""); // 如果ID没有对应的name，则使用"Unknown"
                sb.append(name).append("-");
            }
        }
        return sb.substring(0, sb.length()-1);
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        StoreLicenseExportRequest query = JSONObject.toJavaObject(request, StoreLicenseExportRequest.class);
        int orderNum = 1;
        List<ExcelExportEntity> beanList = new ArrayList<>();
        DataSourceHelper.reset();
        //根据企业id查出当前的企业配置
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(query.getEnterpriseId());
        List<LicenseTypeDTO> licenseTypes = licenseTypeService.getStoreLicenseTypesBySourceOrId(enterpriseConfigDO,LicenseTypeSourceEnum.STORE.getSource(),query.getLicenseTypeId());
        beanList.add(buildExcelExportEntity("门店名称", "storeName", orderNum++, null));
        beanList.add(buildExcelExportEntity("门店状态", "storeStatus", orderNum++, null));
        beanList.add(buildExcelExportEntity("区域名称\n" +
                "(一级区域）\n", "regionName", orderNum++, null));
        beanList.add(buildExcelExportEntity("区域全路径", "regionPathName", orderNum++, null));
        for (LicenseTypeDTO licenseType : licenseTypes) {
            ExcelExportEntity pictureEntity = buildPictureExcelExportEntity("图片", "LicenseType_" + licenseType.getLicenseTypeId(), orderNum++, licenseType.getName());
            //beanList.add(pictureEntity);
            beanList.add(buildExcelExportEntity("证照状态", "licenseStatus_"+ licenseType.getLicenseTypeId(), orderNum++, licenseType.getName()));
            beanList.add(buildExcelExportEntity("过期时间", "endDate_" +licenseType.getLicenseTypeId(), orderNum++, licenseType.getName()));
            if (CollectionUtils.isNotEmpty(licenseType.getExtendFieldList())) {
                for (LicenseTypeExtendFieldDTO extendField : licenseType.getExtendFieldList()) {
                    beanList.add(buildExcelExportEntity(extendField.getName(), "ExtendField_"+extendField.getId(), orderNum++, licenseType.getName()));
                }
            }
        }
        return beanList;
    }

    private ExcelExportEntity buildPictureExcelExportEntity(String name, String key, Integer orderNum, String groupName){
        ExcelExportEntity pictureEntity = new ExcelExportEntity(name, key);
        pictureEntity.setOrderNum(orderNum);
        if(StringUtils.isNotBlank(groupName)){
            pictureEntity.setGroupName(groupName);
            pictureEntity.setNeedMerge(true);
            pictureEntity.setMergeVertical(true);
        }
        pictureEntity.setType(BaseEntityTypeConstants.IMAGE_TYPE);
        pictureEntity.setExportImageType(1);
        pictureEntity.setWidth(20);
        pictureEntity.setHeight(20);
        return pictureEntity;
    }

    private ExcelExportEntity buildExcelExportEntity(String name, String key, Integer orderNum, String groupName){
        ExcelExportEntity excelExportEntity = new ExcelExportEntity(name, key);
        excelExportEntity.setOrderNum(orderNum);
        if(StringUtils.isNotBlank(groupName)){
            excelExportEntity.setGroupName(groupName);
            excelExportEntity.setNeedMerge(true);
        }
        return excelExportEntity;
    }

    private String getLicenseStatus(LicenseDTO licenseDTO, Integer plusDay, boolean isNeedUpload){
        if (isNeedUpload){
            return LicenseStatusEnum.NO_NEED_UPLOAD.getMsg();
        }
        if (licenseDTO != null){
            return LicenseStatusEnum.getLicenseStatus(licenseDTO.getExpiryType(), licenseDTO.getExpiryEndDate(), plusDay).getMsg();
        }
        return LicenseStatusEnum.MISSING.getMsg();
    }

    private String getExpiryDate(LicenseDTO licenseDTO){
        if(Objects.isNull(licenseDTO)){
            return null;
        }
        if("long".equals(licenseDTO.getExpiryType())){
            return "长期有效";
        }
        if(Objects.isNull(licenseDTO.getExpiryEndDate())){
            return null;
        }
        return DateUtil.formatDateTime(licenseDTO.getExpiryEndDate());
    }

    public List<String> getNoNeedUploadStoreIdList(String eid,String needUploadLicenseUser) {
        List<String> storeIds = new ArrayList<>();
        if (StringUtils.isNotBlank(needUploadLicenseUser)){
            List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(needUploadLicenseUser, StoreWorkCommonDTO.class);
            List<String> regionIds = storeWorkCommonDTOS.stream().filter(c -> UnifyTaskConstant.StoreType.REGION.equals(c.getType())).map(c -> c.getValue()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(regionIds)){
                //查询区域下门店
                List<StoreAreaDTO> storeAreaDTOS = storeMapper.listStoreByRegionIdList(eid, regionIds);
                storeIds.addAll(storeAreaDTOS.stream().map(c -> c.getStoreId()).collect(Collectors.toList()));
            }
            storeIds.addAll(storeWorkCommonDTOS.stream().filter(c -> UnifyTaskConstant.StoreType.STORE.equals(c.getType())).map(c -> c.getValue()).collect(Collectors.toList()));
        }
        return storeIds;
    }

}
