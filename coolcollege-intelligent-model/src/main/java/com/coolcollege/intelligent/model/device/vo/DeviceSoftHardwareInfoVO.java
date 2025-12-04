package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备软硬件信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/8/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceSoftHardwareInfoVO {
    @ApiModelProperty("设备型号")
    private String deviceModel;

    @ApiModelProperty("IP")
    private String ip;

    @ApiModelProperty("MAC地址")
    private String mac;

    @ApiModelProperty("硬件版本")
    private String hardwareVersion;

    @ApiModelProperty("固件/软件版本")
    private String firmwareVersion;

    @ApiModelProperty("视频码流")
    private String vencType;
}
