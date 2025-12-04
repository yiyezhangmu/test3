package com.coolcollege.intelligent.rpc.config;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dto.BaseResultDTO;
import com.coolcollege.intelligent.dto.MetaTableDetailDTO;
import com.coolcollege.intelligent.rpc.api.MetaTableServiceApi;
import org.springframework.stereotype.Service;

/**
 * @author wxp
 * @FileName: MetaTableRpcService
 * @Description: 检查表模板配置
 * @date 2022-09-27 17:14
 */
@Service
public class MetaTableRpcService {

    @SofaReference(uniqueId = ConfigConstants.METATABLE_API_FACADE_UNIQUE_ID, interfaceType = MetaTableServiceApi.class, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private MetaTableServiceApi metaTableServiceApi;

    public MetaTableDetailDTO getMetaTableDetail(Long id){
        BaseResultDTO<MetaTableDetailDTO> metaTableDetailDTO = metaTableServiceApi.getMetaTableDetail(id);
        if(metaTableDetailDTO.isSuccess()){
            MetaTableDetailDTO result = metaTableDetailDTO.getData();
            return result;
        }
        return null;
    }

}
