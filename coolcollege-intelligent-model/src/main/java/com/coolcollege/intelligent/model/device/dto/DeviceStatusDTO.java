package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * 设备状态DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/13
 */
@Data
@AllArgsConstructor
public class DeviceStatusDTO {
    /**
     * 是否登录
     */
    private Boolean login;

    /**
     * 设备类型
     */
    private String deviceType;
}
