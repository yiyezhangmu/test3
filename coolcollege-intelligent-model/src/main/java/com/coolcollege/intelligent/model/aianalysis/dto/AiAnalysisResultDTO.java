package com.coolcollege.intelligent.model.aianalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI分析结果DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisResultDTO {
    /**
     * 门店id
     */
    private String storeId;

    /**
     * AI分析结果
     */
    private String result;
}
