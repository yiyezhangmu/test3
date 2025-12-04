package com.coolcollege.intelligent.rpc.config;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.*;
import com.coolcollege.intelligent.rpc.api.EnterpriseSettingServiceApi;
import org.springframework.stereotype.Service;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseSettingServiceApiImpl
 * @Description: 企业配置
 * @date 2022-08-10 17:14
 */
@Service
public class EnterpriseSettingRpcService {

    @SofaReference(uniqueId = ConfigConstants.ENTERPRISE_SETTING_API_FACADE_UNIQUE_ID, interfaceType = EnterpriseSettingServiceApi.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private EnterpriseSettingServiceApi enterpriseSettingServiceApi;

    public EnterpriseQuestionSettingsDTO getQuestionSetting(String enterpriseId){
        BaseResultDTO<EnterpriseQuestionSettingsDTO> questionSetting = enterpriseSettingServiceApi.getQuestionSetting(enterpriseId);
        if(questionSetting.isSuccess()){
            EnterpriseQuestionSettingsDTO result = questionSetting.getData();
            return result;
        }
        return null;
    }

    public EnterpriseSettingsDTO getEnterpriseSetting(String enterpriseId){
        BaseResultDTO<EnterpriseSettingsDTO> questionSetting = enterpriseSettingServiceApi.getEnterpriseSetting(enterpriseId);
        if(questionSetting.isSuccess()){
            EnterpriseSettingsDTO result = questionSetting.getData();
            return result;
        }
        return null;
    }

    public EnterpriseStoreCheckSettingsDTO getEnterpriseStoreCheckSettings(String enterpriseId){
        BaseResultDTO<EnterpriseStoreCheckSettingsDTO> questionSetting = enterpriseSettingServiceApi.getEnterpriseStoreCheckSettings(enterpriseId);
        if(questionSetting.isSuccess()){
            EnterpriseStoreCheckSettingsDTO result = questionSetting.getData();
            return result;
        }
        return null;
    }

    public EnterpriseStoreWorkSettingsDTO getStoreWorkSetting(String enterpriseId){
        BaseResultDTO<EnterpriseStoreWorkSettingsDTO> questionSetting = enterpriseSettingServiceApi.getStoreWorkSetting(enterpriseId);
        if(questionSetting.isSuccess()){
            EnterpriseStoreWorkSettingsDTO result = questionSetting.getData();
            return result;
        }
        return null;
    }

    public EnterpriseSafetyCheckSettingsDTO getSafetyCheckSettings(String enterpriseId){
        BaseResultDTO<EnterpriseSafetyCheckSettingsDTO> safetyCheckSettings = enterpriseSettingServiceApi.getSafetyCheckSettings(enterpriseId);
        if(safetyCheckSettings.isSuccess()){
            EnterpriseSafetyCheckSettingsDTO result = safetyCheckSettings.getData();
            return result;
        }
        return null;
    }

    public EnterpriseThemeColorSettingInfoRpcDTO getThemeColorSettingDetail(String enterpriseId){
        BaseResultDTO<EnterpriseThemeColorSettingInfoRpcDTO> themeColorSettingDetail = enterpriseSettingServiceApi.getThemeColorSettingDetail(enterpriseId);
        if(themeColorSettingDetail.isSuccess()){
            EnterpriseThemeColorSettingInfoRpcDTO result = themeColorSettingDetail.getData();
            return result;
        }
        return null;
    }

    public Integer updateThemeColorSetting(String enterpriseId, EnterpriseThemeColorSettingsAddRpcDTO themeColorSettingsAddRpcDTO) {
        themeColorSettingsAddRpcDTO.setEnterpriseId(enterpriseId);
        BaseResultDTO<Integer> resultDTO = enterpriseSettingServiceApi.updateThemeColorSetting(themeColorSettingsAddRpcDTO);
        if (!resultDTO.isSuccess()) {
            return null;
        }
        return resultDTO.getData();
    }
}
