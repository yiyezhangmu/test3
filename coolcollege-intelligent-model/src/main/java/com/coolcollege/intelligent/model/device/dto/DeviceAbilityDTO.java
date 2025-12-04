package com.coolcollege.intelligent.model.device.dto;

import lombok.Data;

/**
 * <p>
 * 设备能力集DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/8
 */
@Data
public class DeviceAbilityDTO {
    /**
     * 支持云台控制
     */
    private Boolean supportPtz;

    /**
     * 支持抓拍
     */
    private Boolean supportCapture;

    public Integer getSupportCaptureInt() {
        return Boolean.TRUE.equals(supportCapture) ? 1 : 0;
    }
}
