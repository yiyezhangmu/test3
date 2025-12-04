package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 店务AI结果DTO
 * </p>
 *
 * @author wangff
 * @since 2025/6/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreWorkAIResolveDTO {

    /**
     * 项id
     */
    private Long columnId;

    /**
     * AI状态
     */
    private Integer aiStatus;

    /**
     * AI失败原因
     */
    private String aiFailReason;

    /**
     * AI结果
     */
    private AIResolveDTO aiResolveDTO;
}
