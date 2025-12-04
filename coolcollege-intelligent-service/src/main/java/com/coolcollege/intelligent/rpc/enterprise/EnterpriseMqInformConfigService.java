package com.coolcollege.intelligent.rpc.enterprise;

import cn.hutool.json.JSONUtil;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.dto.ResultCodeDTO;
import com.coolcollege.intelligent.rpc.api.EnterpriseMqInformConfigApi;
import com.coolstore.base.dto.ResultDTO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/8/14 18:55
 */

@Slf4j
@Service
public class EnterpriseMqInformConfigService {

    @SofaReference(uniqueId = ConfigConstants.ENTERPRISE_MQ_CONFIG_API_FACADE_UNIQUE_ID, interfaceType = EnterpriseMqInformConfigApi.class,
            binding = @SofaReferenceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE))
    private EnterpriseMqInformConfigApi enterpriseMqInformConfigApi;

    public EnterpriseMqInformConfigDTO queryById(String enterpriseId) throws ApiException {
        ResultDTO<EnterpriseMqInformConfigDTO> enterpriseMqInformConfigDTOResultDTO = enterpriseMqInformConfigApi.queryById(enterpriseId);
        log.info("enterpriseMqInformConfigDTOResultDTO:{}", JSONUtil.toJsonStr(enterpriseMqInformConfigDTOResultDTO));
        if (ResultCodeDTO.SUCCESS.getCode()!=(enterpriseMqInformConfigDTOResultDTO.getCode())) {
            throw new ApiException(String.valueOf(enterpriseMqInformConfigDTOResultDTO.getCode()),enterpriseMqInformConfigDTOResultDTO.getMessage());
        }
        return enterpriseMqInformConfigDTOResultDTO.getData();
    }
    public EnterpriseMqInformConfigDTO queryByStatus(String enterpriseId, Integer status) throws ApiException {
        ResultDTO<EnterpriseMqInformConfigDTO> dto = enterpriseMqInformConfigApi.queryByStatus(enterpriseId, status);
        log.info("enterpriseMqInformConfigDTOResultDTO:{}", JSONUtil.toJsonStr(dto));
        if (ResultCodeDTO.SUCCESS.getCode()!=(dto.getCode())) {
            throw new ApiException(String.valueOf(dto.getCode()),dto.getMessage());
        }
        return dto.getData();
    }
}
