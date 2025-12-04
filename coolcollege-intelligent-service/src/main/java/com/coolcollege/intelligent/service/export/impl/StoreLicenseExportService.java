package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.license.LicenseStatusEnum;
import com.coolcollege.intelligent.common.enums.license.LicenseTypeSourceEnum;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.license.LicenseExportRequest;
import com.coolcollege.intelligent.model.license.vo.LicenseImgExportVO;
import com.coolcollege.intelligent.model.license.vo.StoreLicenseExportVO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.rpc.license.LicenseTypeApiService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.license.client.dto.LicenseDTO;
import com.coolstore.license.client.dto.LicenseQueryDTO;
import com.coolstore.license.client.dto.LicenseTypeDTO;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户导出service
 * @author ：xugangkun
 * @date ：2021/7/23 10:22
 */
@Service
@Slf4j
public class StoreLicenseExportService implements BaseExportService {


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

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        LicenseExportRequest query = (LicenseExportRequest) request;
        List<StoreAreaDTO> storeAreaDTOS   =  storeMapper.queryByRegionIdAndStoreName(enterpriseId,query);
        return Long.valueOf(storeAreaDTOS.size());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.STORE_LICENSE_REPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        LicenseExportRequest query = JSONObject.toJavaObject(request, LicenseExportRequest.class);
        List<StoreLicenseExportVO> vos = Lists.newArrayList();
        //区域下门店
        PageHelper.startPage(pageNum, pageSize);
        List<StoreAreaDTO> storeAreaDTOS=   storeMapper.queryByRegionIdAndStoreName(enterpriseId,query);
        List<String> storeIds = storeAreaDTOS.stream().map(c -> c.getStoreId()).collect(Collectors.toList());
        DataSourceHelper.reset();
        //根据企业id查出当前的企业配置
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreSettingDO enterpriseStoreSetting = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //查询企业的证照类型
        List<LicenseTypeDTO> licenseTypes = licenseTypeService.getStoreLicenseTypesBySourceOrId(enterpriseConfigDO,LicenseTypeSourceEnum.STORE.getSource(),query.getLicenseTypeId());

        Integer effectiveTime=enterpriseStoreSetting.getStoreLicenseEffectiveTime();
        LicenseQueryDTO licenseQueryDTO = new LicenseQueryDTO();
        licenseQueryDTO.setStoreIds(storeIds);
        licenseQueryDTO.setEnterpriseId(enterpriseId);
        licenseQueryDTO.setDbName(enterpriseConfigDO.getDbName());
        licenseQueryDTO.setSource(LicenseTypeSourceEnum.STORE.getSource());
        licenseQueryDTO.setGetPicture(Boolean.TRUE);
        List<LicenseDTO> licenseDTOS = licenseApiService.queryLicenseByQuery(licenseQueryDTO);

        //无需上传门店
        List<String> noNeedStoreIds = getNoNeedUploadStoreIdList(enterpriseId, enterpriseStoreSetting.getNoNeedUploadLicenseRegion());

        //根据店分组
        Map<String, List<LicenseDTO>> storeIdAndLicenses = licenseDTOS.stream().collect(Collectors.groupingBy(c -> c.getStoreId()));
        //构建最小行
        for (StoreAreaDTO storeAreaDTO : storeAreaDTOS) {
            //拿去门店下证照
            List<LicenseDTO> curStoreLicense = ListUtils.emptyIfNull(storeIdAndLicenses.get(storeAreaDTO.getStoreId()));
            Map<Long, LicenseDTO> typeAndLicenses = curStoreLicense.stream().collect(Collectors.toMap(c -> Long.valueOf(c.getLicenseTypeId()), c -> c));
            boolean isNoNeedUpload = false;
            //判断本店是否在无需上传门店中
            if (noNeedStoreIds.contains(storeAreaDTO.getStoreId())){
                isNoNeedUpload=true;
            }
            for (LicenseTypeDTO licenseType : licenseTypes) {
                LicenseDTO curLicense = typeAndLicenses.get(licenseType.getLicenseTypeId());
                StoreLicenseExportVO vo = buildLicenseExportVO(storeAreaDTO.getStoreName(), licenseType,curLicense, effectiveTime,isNoNeedUpload);
                vos.add(vo);
            }
        }
        return vos;
    }

    private StoreLicenseExportVO buildLicenseExportVO(String name, LicenseTypeDTO licenseType, LicenseDTO licenseDTO, Integer plusDay, boolean isNeedUpload){
        StoreLicenseExportVO vo = new StoreLicenseExportVO();
        vo.setName(name);
        vo.setLicenseTypeName(licenseType.getName());
        if (licenseDTO != null){
            vo.setLicenseStatus(LicenseStatusEnum.getLicenseStatus(licenseDTO.getExpiryEndDate(), plusDay).getMsg());
            if (StringUtils.isNotBlank(licenseDTO.getPicture())){
                List<String> imgs = Lists.newArrayList(licenseDTO.getPicture().split(","));
                List<LicenseImgExportVO> pic=new ArrayList<>();
                imgs.stream().forEach(c->{
                    try {
                        byte[] imageFromNetByUrl = FileUtil.getImageFromNetByUrl(c);
                        LicenseImgExportVO licenseImgExportVO = new LicenseImgExportVO();
                        licenseImgExportVO.setImg(imageFromNetByUrl);
                        pic.add(licenseImgExportVO);
                    }catch (Exception e){
                        log.error("获取图片失败:{}:{}",c,e);
                    }
                });
                vo.setLicenseImgUrl(pic);
            }
            if (licenseDTO.getExpiryEndDate() != null){
                vo.setEndDate(DateUtil.formatDateTime(licenseDTO.getExpiryEndDate()));
            }
        }else{
            vo.setLicenseStatus(LicenseStatusEnum.MISSING.getMsg());
        }
        if (isNeedUpload){
            vo.setLicenseStatus(LicenseStatusEnum.NO_NEED_UPLOAD.getMsg());
        }
        return vo;
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
