package com.coolcollege.intelligent.model.aianalysis.dto;

import com.coolcollege.intelligent.model.device.dto.CapturePictureDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * AI店报抓图DTO
 * </p>
 *
 * @author wangff
 * @since 2025/8/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisPictureCaptureDTO {
    /**
     * 规则id
     */
    private Long ruleId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道号
     */
    private String channelNo;

    /**
     * 设备场景id
     */
    private Long storeSceneId;

    /**
     * 抓图DTO
     */
    private List<CapturePictureDTO> capturePictureList;
}
