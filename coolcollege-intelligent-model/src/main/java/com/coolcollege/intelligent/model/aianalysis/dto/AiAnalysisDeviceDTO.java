package com.coolcollege.intelligent.model.aianalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI分析报告设备DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisDeviceDTO {

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道号
     */
    private String channelNo;

    /**
     * 门店场景id
     */
    private Long storeSceneId;
}
