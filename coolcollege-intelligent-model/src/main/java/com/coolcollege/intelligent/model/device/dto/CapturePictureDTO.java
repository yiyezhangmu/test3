package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>
 * 抓拍图片DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CapturePictureDTO {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道号
     */
    private String channelNo;

    /**
     * 抓拍时间
     */
    private LocalDateTime captureTime;

    /**
     * 图片url
     */
    private String url;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误原因
     */
    private String errorMsg;
}
