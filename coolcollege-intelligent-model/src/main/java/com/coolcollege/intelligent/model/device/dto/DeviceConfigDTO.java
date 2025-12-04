package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备配置DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceConfigDTO {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 通道号
     */
    private String channelNo;

    /**
     * 画面上下翻转
     */
    private Boolean flip;

    /**
     * 画面水平镜像
     */
    private Boolean mirror;
    
    /**
     * 设备名称
     */
    private String deviceName;
}
