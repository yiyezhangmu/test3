package com.coolcollege.intelligent.facade.open.api.ai;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisRuleService;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * AI店报API实现类
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(uniqueId = IntelligentFacadeConstants.AI_ANALYSIS_UNIQUE_ID, interfaceType = AiAnalysisApi.class,
        bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class AiAnalysisApiImpl implements AiAnalysisApi {
    @Resource
    private AiAnalysisRuleService aiAnalysisRuleService;


    @Override
    public OpenApiResponseVO submitCaptureTask(String enterpriseId, LocalDate date) {
        aiAnalysisRuleService.submitCaptureTask(enterpriseId, date, null);
        return OpenApiResponseVO.success(true);
    }

    @Override
    public OpenApiResponseVO aiAnalysis(String enterpriseId, LocalDate date) {
        aiAnalysisRuleService.aiAnalysis(enterpriseId, date, null);
        return OpenApiResponseVO.success(true);
    }

    @Override
    public OpenApiResponseVO reportPush(String enterpriseId, LocalDateTime time) {
        aiAnalysisRuleService.reportPush(enterpriseId, time);
        return OpenApiResponseVO.success(true);
    }
}
