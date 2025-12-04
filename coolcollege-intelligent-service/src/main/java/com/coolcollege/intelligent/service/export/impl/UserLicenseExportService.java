package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.license.LicenseStatusEnum;
import com.coolcollege.intelligent.common.enums.license.LicenseTypeSourceEnum;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.license.LicenseExportRequest;
import com.coolcollege.intelligent.model.license.vo.LicenseImgExportVO;
import com.coolcollege.intelligent.model.license.vo.UserLicenseExportVO;
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
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
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
public class UserLicenseExportService implements BaseExportService {


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
    @Lazy
    private UserPersonInfoService userPersonInfoService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;


    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        LicenseExportRequest query = (LicenseExportRequest) request;
        List<StoreAreaDTO> storeAreaDTOS = storeMapper.queryByRegionIdAndStoreName(enterpriseId,query);
        return Long.valueOf(storeAreaDTOS.size());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.USER_LICENSE_REPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        LicenseExportRequest query = JSONObject.toJavaObject(request, LicenseExportRequest.class);
        List<UserLicenseExportVO> vos = Lists.newArrayList();
        //区域下门店
        PageHelper.startPage(pageNum, pageSize);
        List<StoreAreaDTO> storeAreaDTOS  = storeMapper.queryByRegionIdAndStoreName(enterpriseId,query);
        DataSourceHelper.reset();
        //根据企业id查出当前的企业配置
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreSettingDO enterpriseStoreSetting = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //查询企业的证照类型
        List<LicenseTypeDTO> licenseTypes = licenseTypeService.getStoreLicenseTypesBySourceOrId(enterpriseConfigDO,LicenseTypeSourceEnum.USER.getSource(),query.getLicenseTypeId());

        //查询无需上传人员
        List<String> noNeedUploadUserIds=new ArrayList<>();
        String noNeedUploadLicenseUser = enterpriseStoreSetting.getNoNeedUploadLicenseUser();
        if (StringUtils.isNotBlank(noNeedUploadLicenseUser)){
            List<StoreWorkCommonDTO> commonDTOS = JSONObject.parseArray(noNeedUploadLicenseUser, StoreWorkCommonDTO.class);
            noNeedUploadUserIds = userPersonInfoService.getUserIdListByCommonDTO(enterpriseId,commonDTOS);
        }

        //证照过期时间
        Integer effectiveTime = enterpriseStoreSetting.getUserLicenseEffectiveTime();
        //查寻门店的下的人
        List<String> regionPath = storeAreaDTOS.stream().map(c -> c.getRegionPath()).collect(Collectors.toList());

        List<EnterpriseUserDO> userDOS = enterpriseUserMapper.selectUserByRegionPaths(enterpriseId, regionPath);
        if (CollectionUtils.isEmpty(userDOS)){
            return vos;
        }
        List<String> userIds = userDOS.stream().map(c -> c.getUserId()).collect(Collectors.toList());
        //门店下人员
        Map<String,Map<String,String>> storeIdAndUserIdMap=new HashMap<>();
        //根据regionPath分组
        for (StoreAreaDTO store : storeAreaDTOS) {
            Map<String, String> userMap = Maps.newHashMap();
            userDOS.stream()
                    .filter(user -> user.getUserRegionIds().contains(store.getRegionPath()))
                    .forEach(user -> userMap.put(user.getUserId(), user.getName()));
            storeIdAndUserIdMap.put(store.getStoreId(), userMap);
        }
        //查询这些用户证照
        LicenseQueryDTO licenseQueryDTO = new LicenseQueryDTO();
        licenseQueryDTO.setUserIds(userIds);
        licenseQueryDTO.setEnterpriseId(enterpriseId);
        licenseQueryDTO.setDbName(enterpriseConfigDO.getDbName());
        licenseQueryDTO.setSource(LicenseTypeSourceEnum.USER.getSource());
        licenseQueryDTO.setGetPicture(Boolean.TRUE);
        List<LicenseDTO> licenseDTOS = licenseApiService.queryLicenseByQuery(licenseQueryDTO);


        //根据人分组
        Map<String, List<LicenseDTO>> storeIdAndLicenses = licenseDTOS.stream().collect(Collectors.groupingBy(c -> c.getUserId()));

        //查询用户职位
        List<EntUserRoleDTO> entUserRoleDTOS = enterpriseUserRoleMapper.selectUserRoleByUserIds(enterpriseId, userIds);
        Map<String, List<EntUserRoleDTO>> userRoleDTOMap =  entUserRoleDTOS.stream().collect(Collectors.groupingBy(c -> c.getUserId()));

        //构建最小行
        for (StoreAreaDTO storeAreaDTO : storeAreaDTOS) {
            //拿门店下的人
            Map<String, String> userMap = storeIdAndUserIdMap.get(storeAreaDTO.getStoreId());
            Set<Map.Entry<String, String>> entries = userMap.entrySet();
            if (CollectionUtils.isEmpty(entries)){
                continue;
            }
            for (Map.Entry<String, String> user : entries) {
                String userId = user.getKey();
                boolean isNeedUpload = false;
                if (noNeedUploadUserIds.contains(userId)){
                    isNeedUpload=true;
                }
                for (LicenseTypeDTO licenseType : licenseTypes) {
                    List<LicenseDTO> licenseDTOS1 = storeIdAndLicenses.get(userId);
                    LicenseDTO curLicense=null;
                    if (CollectionUtils.isNotEmpty(licenseDTOS1)){
                        curLicense = licenseDTOS1.stream().filter(c -> c.getLicenseTypeId().equals(licenseType.getLicenseTypeId().toString())).findFirst().orElse(null);
                    }
                    UserLicenseExportVO vo = buildLicenseExportVO(user.getValue(),userId,userRoleDTOMap,storeAreaDTO.getStoreName(), licenseType,curLicense, effectiveTime,isNeedUpload);
                    vos.add(vo);
                }
            }
        }
        return vos;
    }

    private UserLicenseExportVO buildLicenseExportVO(String userName,String userId, Map<String, List<EntUserRoleDTO>> userRoleDTOMap,String storeName, LicenseTypeDTO licenseType,LicenseDTO licenseDTO,Integer plusDay,boolean isNeedUpload){
        UserLicenseExportVO vo = new UserLicenseExportVO();
        vo.setUserName(userName);
        vo.setUserId(userId);
        vo.setStoreName(storeName);
        vo.setLicenseTypeName(licenseType.getName());
        if (userRoleDTOMap.containsKey(userId)){
            List<EntUserRoleDTO> entUserRoleDTOS = userRoleDTOMap.get(userId);
            if (CollectionUtils.isNotEmpty(entUserRoleDTOS)){
                //职位之间,拼接
                String roleName = entUserRoleDTOS.stream().collect(Collectors.mapping(EntUserRoleDTO::getRoleName, Collectors.joining(",")));
                vo.setUserPosition(roleName);
            }
        }
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
            if (Objects.nonNull(licenseDTO.getExpiryEndDate())){
                vo.setEndDate(licenseDTO.getExpiryEndDate());
            }
        }else{
            vo.setLicenseStatus(LicenseStatusEnum.MISSING.getMsg());
        }
        if (isNeedUpload){
            vo.setLicenseStatus(LicenseStatusEnum.NO_NEED_UPLOAD.getMsg());
        }
        return vo;
    }

}
