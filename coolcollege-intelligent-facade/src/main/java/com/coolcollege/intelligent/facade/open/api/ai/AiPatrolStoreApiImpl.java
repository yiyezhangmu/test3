package com.coolcollege.intelligent.facade.open.api.ai;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.service.patrolstore.impl.PatrolStoreAiAuditServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p>
 * AI巡店API实现类
 * </p>
 *
 * @author wxp
 * @since 2025/7/16
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.AI_PATROLSTORE_UNIQUE_ID ,interfaceType = AiPatrolStoreApi.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class AiPatrolStoreApiImpl implements AiPatrolStoreApi {

    @Resource
    private PatrolStoreAiAuditServiceImpl patrolStoreAiAuditService;

    @Override
    public BaseResultDTO handleTimeoutAiPatrolCheck(String enterpriseId) {
        log.info("AI巡店结果超时处理开始 enterpriseId:{}", enterpriseId );
        patrolStoreAiAuditService.handleTimeoutAiPatrolCheck(enterpriseId);
        return BaseResultDTO.SuccessResult();
    }
}
