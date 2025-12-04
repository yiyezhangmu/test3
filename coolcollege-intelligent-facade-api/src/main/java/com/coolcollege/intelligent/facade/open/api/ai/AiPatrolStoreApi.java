package com.coolcollege.intelligent.facade.open.api.ai;

import com.coolcollege.intelligent.facade.dto.BaseResultDTO;

/**
 * <p>
 * AI巡店API
 * </p>
 *
 * @author wxp
 * @since 2025/7/16
 */
public interface AiPatrolStoreApi {

    /**
     * AI巡店结果超时处理
     */
    BaseResultDTO handleTimeoutAiPatrolCheck(String enterpriseId);

}
