package com.coolcollege.intelligent.model.aianalysis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * AI分析请求DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Data
public class AiAnalysisRequestDTO {
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 重试ruleId
     */
    private List<Long> retryRuleIds;

    /**
     * 推送日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime pushTime;
}
