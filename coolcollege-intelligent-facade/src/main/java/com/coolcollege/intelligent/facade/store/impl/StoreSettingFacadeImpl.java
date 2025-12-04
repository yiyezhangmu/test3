package com.coolcollege.intelligent.facade.store.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.store.EnterpriseStoreSettingDTO;
import com.coolcollege.intelligent.facade.request.StoreSettingRequest;
import com.coolcollege.intelligent.facade.store.StoreSettingFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseStoreSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 门店信息RPC接口实现
 * @author zhangnan
 * @date 2021-11-19 11:21
 */
@SofaService(uniqueId = IntelligentFacadeConstants.STORE_SETTING_FACADE_FACADE_UNIQUE_ID, interfaceType = StoreSettingFacade.class,
        bindings = {@SofaServiceBinding(bindingType = IntelligentFacadeConstants.SOFA_BINDING_TYPE)})
@Component
public class StoreSettingFacadeImpl implements StoreSettingFacade {

    @Autowired
    private EnterpriseStoreSettingService enterpriseStoreSettingService;

    @Override
    public ResultDTO<EnterpriseStoreSettingDTO> getStoreLicenseSetting(StoreSettingRequest request) {
        if(StringUtils.isBlank(request.getEnterpriseId())){
            return ResultDTO.failResult("企业id不能为空");
        }
        // 根据企业id切库
        DataSourceHelper.reset();
        EnterpriseStoreSettingDO enterpriseStoreSetting = enterpriseStoreSettingService.getEnterpriseStoreSetting(request.getEnterpriseId());
        EnterpriseStoreSettingDTO enterpriseStoreSettingDTO = new EnterpriseStoreSettingDTO();
        if(enterpriseStoreSetting != null){
            enterpriseStoreSettingDTO.setStoreLicenseEffectiveTime(enterpriseStoreSetting.getStoreLicenseEffectiveTime());
            enterpriseStoreSettingDTO.setUserLicenseEffectiveTime(enterpriseStoreSetting.getUserLicenseEffectiveTime());
            enterpriseStoreSettingDTO.setNeedUploadLicenseUser(enterpriseStoreSetting.getNeedUploadLicenseUser());
            enterpriseStoreSettingDTO.setNoNeedUploadLicenseUser(enterpriseStoreSetting.getNoNeedUploadLicenseUser());
            enterpriseStoreSettingDTO.setNoNeedUploadLicenseRegion(enterpriseStoreSetting.getNoNeedUploadLicenseRegion());
        }else {
            enterpriseStoreSettingDTO.setStoreLicenseEffectiveTime(Constants.THIRTY_DAY);
            enterpriseStoreSettingDTO.setUserLicenseEffectiveTime(Constants.THIRTY_DAY);
        }
        return ResultDTO.successResult(enterpriseStoreSettingDTO);
    }
}
