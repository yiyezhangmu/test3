package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * 设备用户信息DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/9
 */
@Data
@AllArgsConstructor
public class DeviceUserDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
