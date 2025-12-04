package com.coolcollege.intelligent.facade.open.api.ai;

import com.coolstore.base.response.rpc.OpenApiResponseVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * AI店报API
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
public interface AiAnalysisApi {

    /**
     * 提交抓图任务
     */
    OpenApiResponseVO submitCaptureTask(String enterpriseId, LocalDate date);

    /**
     * 进行AI分析并生成店报
     */
    OpenApiResponseVO aiAnalysis(String enterpriseId, LocalDate date);

    /**
     * 报告推送
     */
    OpenApiResponseVO reportPush(String enterpriseId, LocalDateTime time);
}
