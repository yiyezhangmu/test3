package com.coolcollege.intelligent.model.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCancelAuthDTO {

    @NotNull
    @ApiModelProperty("appId")
    private String appId;

    @NotNull
    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;
}
