package com.coolcollege.intelligent.controller.video.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/29
 */
@Data
@ApiModel
public class DevicePositionRequest{
    @ApiModelProperty("ID")
    private Long id;

    @NotBlank(message = "设备Id不能为空")
    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("预置位名称")
    @NotBlank(message = "预置位名称不能为空")
    private String devicePositionName;
}
