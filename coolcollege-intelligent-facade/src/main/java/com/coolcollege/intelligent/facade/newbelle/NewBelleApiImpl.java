package com.coolcollege.intelligent.facade.newbelle;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.facade.dto.newbelle.TaskGoodsDetailDTO;
import com.coolcollege.intelligent.facade.open.api.newbelle.NewBelleApi;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = NewBelleApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class NewBelleApiImpl implements NewBelleApi {
    @Override
    public OpenApiResponseVO sendProductFeedback(TaskGoodsDetailDTO param) {
        return null;
    }
}
