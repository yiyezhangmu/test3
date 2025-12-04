package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 设备删除Request
 * </p>
 *
 * @author wangff
 * @since 2025/7/23
 */
@Data
public class DeviceDeleteRequest {
    @ApiModelProperty("设备序列号")
    @NotBlank(message = "设备序列号不能为空")
    private String deviceId;

    @ApiModelProperty("通道号，IPC设备不填")
    private String channelNo;
}
