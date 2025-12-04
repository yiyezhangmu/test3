package com.coolcollege.intelligent.model.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI结果DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResultDTO {
    /**
     * 分析结果
     */
    private String aiResult;
}
