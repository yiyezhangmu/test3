package com.coolcollege.intelligent.facade.open.api.question;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionOrderDTO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * @author byd
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = YunDaApi.class, bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class YunDaApiImpl implements YunDaApi {

    @Resource
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    @Override
    @ShenyuSofaClient(path = "/question/sendQuestionOrder")
    public OpenApiResponseVO<Boolean> sendQuestionOrder(QuestionOrderDTO questionOrderDTO) {
        try {
            coolCollegeIntegrationApiService.sendYunDaMsg(questionOrderDTO.getEid(), questionOrderDTO.getJobNumList(), questionOrderDTO.getQuestionOrderCode());
        }catch (ServiceException e){
            log.error("ServiceException", e);
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        }catch (Exception e){
            log.error("Exception", e);
            return OpenApiResponseVO.fail();
        }
        return OpenApiResponseVO.success(true);
    }
}
