package com.coolcollege.intelligent.facade.enterprise.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.enterprise.EnterpriseConfigDTO;
import com.coolcollege.intelligent.facade.enterprise.EnterpriseConfigFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 企业配置RPC接口实现
 * @author zhangnan
 * @date 2021-11-25 15:22
 */
@SofaService(uniqueId = IntelligentFacadeConstants.ENTERPRISE_CONFIG_FACADE_FACADE_UNIQUE_ID ,interfaceType = EnterpriseConfigFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class EnterpriseConfigFacadeImpl implements EnterpriseConfigFacade {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Override
    public ResultDTO<EnterpriseConfigDTO> getConfigByEnterprise(String enterpriseId) {
        if(StringUtils.isBlank(enterpriseId)) {
            return ResultDTO.successResult();
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        if(configDO == null){
            return ResultDTO.successResult();
        }
        return ResultDTO.successResult(this.parseEnterpriseConfigDoToDto(configDO));
    }

    /**
     * 企业配置DO转DTO
     * @param configDO EnterpriseConfigDO
     * @return EnterpriseConfigDTO
     */
    private EnterpriseConfigDTO parseEnterpriseConfigDoToDto(EnterpriseConfigDO configDO) {
        EnterpriseConfigDTO configDTO = new EnterpriseConfigDTO();
        configDTO.setCurrentPackage(configDO.getCurrentPackage());
        configDTO.setEnterpriseId(configDO.getEnterpriseId());
        configDTO.setStaffCount(configDO.getStaffCount());
        configDTO.setDbSourceName(configDO.getDbSourceName());
        configDTO.setDbServer(configDO.getDbServer());
        configDTO.setDbPort(configDO.getDbPort());
        configDTO.setDbName(configDO.getDbName());
        configDTO.setDbUser(configDO.getDbUser());
        configDTO.setDbPwd(configDO.getDbPwd());
        configDTO.setLicense(configDO.getLicense());
        configDTO.setLicenseExpires(configDO.getLicenseExpires());
        configDTO.setLicenseType(configDO.getLicenseType());
        configDTO.setDingCorpId(configDO.getDingCorpId());
        configDTO.setDingCorpSecret(configDO.getDingCorpSecret());
        configDTO.setCreateTime(configDO.getCreateTime());
        configDTO.setCreateUser(configDO.getCreateUser());
        configDTO.setMainCorpId(configDO.getMainCorpId());
        configDTO.setAppType(configDO.getAppType());
        return configDTO;
    }
}
