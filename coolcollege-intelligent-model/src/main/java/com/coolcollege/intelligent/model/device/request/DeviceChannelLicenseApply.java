package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeviceChannelLicenseApply {

    /**
     * 通道号
     */
    @NotBlank(message = "通道号 不能为空")
    private String channelNo;

    /**
     * 通道名称
     */
    @NotBlank(message = "通道名称 不能为空")
    private String channelName;

    @NotBlank(message = "设备国标编号 不能为空")
    private String deviceCode;

    @NotBlank(message = "国标licenseId 不能为空")
    private String licenseId;

}
