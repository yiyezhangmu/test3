package com.coolcollege.intelligent.model.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 国标设备申请licenseDTO
 * </p>
 *
 * @author wangff
 * @since 2025/8/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceGBLicenseApplyDTO {
    /**
     * IPC/NVR
     */
    private String deviceCategory;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备密码
     */
    private String password;

    /**
     * 设备序列号
     */
    private String deviceSerial;
}
