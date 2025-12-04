package com.coolcollege.intelligent.service.enterprise.setting.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.FieldUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.FieldDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseLicenseSettingRequest;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseStoreSettingRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreSettingVO;
import com.coolcollege.intelligent.model.enums.StoreInfoIntegrityEnum;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Description: 门店基础信息配置
 * @Author chenyupeng
 * @Date 2021/6/29
 * @Version 1.0
 */
@Service
@Slf4j
public class EnterpriseStoreSettingServiceImpl implements EnterpriseStoreSettingService {

    @Autowired
    EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;

    static final String PREFIX ="extend_field_";

    static final Integer EXTEND_MAX_NUM = 10;

    @Override
    public String updateExtendFieldInfo(String enterpriseId, ExtendFieldInfoDTO extendFieldInfoDTO) {
        EnterpriseStoreSettingDO enterpriseStoreSettingDO =  enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        //没有则新增配置
        if(enterpriseStoreSettingDO == null){
            enterpriseStoreSettingDO = new EnterpriseStoreSettingDO();
            List<ExtendFieldInfoDTO> extendFieldInfoDTOList = new ArrayList<>();
            extendFieldInfoDTO.setExtendFieldKey(PREFIX+getExtendFieldKey());
            extendFieldInfoDTOList.add(extendFieldInfoDTO);
            enterpriseStoreSettingDO.setExtendFieldInfo(JSONObject.toJSONString(extendFieldInfoDTOList));
            enterpriseStoreSettingDO.setEnterpriseId(enterpriseId);
            enterpriseStoreSettingDO.setCreateTime(System.currentTimeMillis());
            enterpriseStoreSettingDO.setStoreLicenseEffectiveTime(Constants.THIRTY_DAY);
            enterpriseStoreSettingDO.setUserLicenseEffectiveTime(Constants.THIRTY_DAY);
            enterpriseStoreSettingMapper.insertOrUpdate(enterpriseId,enterpriseStoreSettingDO);
            return extendFieldInfoDTO.getExtendFieldKey();
        }
        String extendFieldInfo = enterpriseStoreSettingDO.getExtendFieldInfo();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        //基础信息没有此字段则新增
        if(StringUtils.isEmpty(extendFieldInfo)){
            extendFieldInfoDTOList = new ArrayList<>();
            extendFieldInfoDTO.setExtendFieldKey(PREFIX+getExtendFieldKey());
            extendFieldInfoDTOList.add(extendFieldInfoDTO);
        }else {
            try {
                extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo,ExtendFieldInfoDTO.class);
            } catch (Exception e) {
                log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
            }
            //如果没有key代表新增,有就修改
            if(StringUtils.isEmpty(extendFieldInfoDTO.getExtendFieldKey())){
                extendFieldInfoDTO.setExtendFieldKey(PREFIX+getExtendFieldKey());
                //最多有10个自定义字段
                if(extendFieldInfoDTOList.size()>=EXTEND_MAX_NUM){
                    throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "最多有10个自定义字段");
                }
                extendFieldInfoDTOList.add(extendFieldInfoDTO);
            }else {
                //通过key找到对应的字段然后修改name
                for (ExtendFieldInfoDTO fieldInfoDTO : extendFieldInfoDTOList) {
                    if(StringUtils.equals(fieldInfoDTO.getExtendFieldKey(),extendFieldInfoDTO.getExtendFieldKey())){
                        fieldInfoDTO.setExtendFieldName(extendFieldInfoDTO.getExtendFieldName());
                        break;
                    }
                }
            }
        }
        extendFieldInfo = JSONObject.toJSONString(extendFieldInfoDTOList);
        enterpriseStoreSettingMapper.updateExtendField(enterpriseId,extendFieldInfo);
        return extendFieldInfoDTO.getExtendFieldKey();
    }

    @Override
    public Integer deleteExtendFieldInfo(String enterpriseId, String extendFieldKey) {
        EnterpriseStoreSettingDO enterpriseStoreSettingDO =  enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        if(enterpriseStoreSettingDO == null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "当前门店没有基础配置信息");
        }
        String extendFieldInfo = enterpriseStoreSettingDO.getExtendFieldInfo();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList;
        try {
            extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo,ExtendFieldInfoDTO.class);
        } catch (Exception e) {
            log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息格式转换错误");
        }
        //筛选掉extendFieldKey相等的
        extendFieldInfoDTOList = extendFieldInfoDTOList.stream()
                .filter(e -> !StringUtils.equals(e.getExtendFieldKey(),extendFieldKey))
                .collect(Collectors.toList());
        extendFieldInfo = JSONObject.toJSONString(extendFieldInfoDTOList);

        return enterpriseStoreSettingMapper.updateExtendField(enterpriseId,extendFieldInfo);
    }

    @Override
    public String queryExtendFieldInfo(String enterpriseId) {
        EnterpriseStoreSettingDO enterpriseStoreSettingDO =  enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        if(enterpriseStoreSettingDO != null){
            return enterpriseStoreSettingDO.getExtendFieldInfo() == null ? "" : enterpriseStoreSettingDO.getExtendFieldInfo();
        }else {
            log.error("获取门店基础配置异常！门店Id:{}",enterpriseId);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "获取门店基础配置异常");
        }
    }

    @Override
    public EnterpriseStoreSettingDO getEnterpriseStoreSetting(String enterpriseId) {
        return enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
    }

    @Override
    public EnterpriseStoreSettingVO getEnterpriseStoreSettingVO(String enterpriseId) {
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(enterpriseId);
        if(storeSettingDO == null){
            return null;
        }
        EnterpriseStoreSettingVO enterpriseStoreSettingVO = new EnterpriseStoreSettingVO();
        enterpriseStoreSettingVO.setEnterpriseId(enterpriseId);
        enterpriseStoreSettingVO.setStoreLicenseEffectiveTime(storeSettingDO.getStoreLicenseEffectiveTime());
        enterpriseStoreSettingVO.setUserLicenseEffectiveTime(storeSettingDO.getUserLicenseEffectiveTime());

        String needUploadLicenseUser = storeSettingDO.getNeedUploadLicenseUser();
        if (StringUtils.isNotBlank(needUploadLicenseUser)){
            List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(needUploadLicenseUser, StoreWorkCommonDTO.class);
            enterpriseStoreSettingVO.setNeedUploadLicenseUser(storeWorkCommonDTOS);
        }
        if (StringUtils.isNotBlank(storeSettingDO.getNoNeedUploadLicenseUser())){
            List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(storeSettingDO.getNoNeedUploadLicenseUser(), StoreWorkCommonDTO.class);
            enterpriseStoreSettingVO.setNoNeedUploadLicenseUser(storeWorkCommonDTOS);
        }
        if (StringUtils.isNotBlank(storeSettingDO.getNoNeedUploadLicenseRegion())){
            List<StoreWorkCommonDTO> storeWorkCommonDTOS = JSONObject.parseArray(storeSettingDO.getNoNeedUploadLicenseRegion(), StoreWorkCommonDTO.class);
            enterpriseStoreSettingVO.setNoNeedUploadLicenseRegion(storeWorkCommonDTOS);
        }
        return enterpriseStoreSettingVO;
    }

    @Override
    public void updateStoreTimeSetting(String enterpriseId, EnterpriseStoreSettingRequest enterpriseStoreSettingRequest) {
        enterpriseStoreSettingMapper.updateStoreTimeSetting(enterpriseId,
                enterpriseStoreSettingRequest.getStoreLicenseEffectiveTime(), enterpriseStoreSettingRequest.getUserLicenseEffectiveTime());
    }

    @Override
    public void updateLicenseSetting(String enterpriseId, EnterpriseLicenseSettingRequest request) {
        List<StoreWorkCommonDTO> needUploadLicenseUser = request.getNeedUploadLicenseUser();
        List<StoreWorkCommonDTO> noNeedUploadLicenseRegion = request.getNoNeedUploadLicenseRegion();
        List<StoreWorkCommonDTO> noNeedUploadLicenseUser = request.getNoNeedUploadLicenseUser();

        String needUploadLicenseUserStr=JSONObject.toJSONString(needUploadLicenseUser);
        String noNeedUploadLicenseRegionStr=JSONObject.toJSONString(noNeedUploadLicenseRegion);
        String noNeedUploadLicenseUserStr=JSONObject.toJSONString(noNeedUploadLicenseUser);

        enterpriseStoreSettingMapper.updateLicensesSetting(enterpriseId,needUploadLicenseUserStr,noNeedUploadLicenseRegionStr,noNeedUploadLicenseUserStr);
    }

    @Override
    public String getStorePerfection(Object store, String perfectionField) {
        try {
            if (StringUtils.isBlank(perfectionField)) {
                return StoreInfoIntegrityEnum.NOT_SET.getCode();
            }
            List<FieldDTO> perfectionFieldList = JSONArray.parseArray(perfectionField, FieldDTO.class);
            List<String> fields = perfectionFieldList.stream()
                    .filter(file -> file.getField() != null)
                    .map(FieldDTO::getField)
                    .collect(Collectors.toList());
            if (FieldUtils.isPerfect(fields, store)) {
                return StoreInfoIntegrityEnum.PERFECT.getCode();
            }
        } catch (Exception e) {
            log.error("设置门店完善异常", e);
        }
        return StoreInfoIntegrityEnum.IMPERFECT.getCode();
    }

    private long getExtendFieldKey() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }
}
