package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OpenDevicePageVO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("通道号")
    private String channelNo;

}
