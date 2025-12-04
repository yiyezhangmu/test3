package com.coolcollege.intelligent.facade.enterprise;

import com.coolcollege.intelligent.facade.dto.enterprise.EnterpriseConfigDTO;
import com.coolstore.base.dto.ResultDTO;

/**
 * 企业配置RPC接口
 * @author zhangnan
 * @date 2021-11-25 15:18
 */
public interface EnterpriseConfigFacade {

    /**
     * 根据企业id查询配置
     * @param enterpriseId 企业id
     * @return ResultDTO<EnterpriseConfigDTO>
     */
    ResultDTO<EnterpriseConfigDTO> getConfigByEnterprise(String enterpriseId);
}
