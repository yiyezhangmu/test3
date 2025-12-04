package com.coolcollege.intelligent.model.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/28
 */
@Data
public class DevicePositionDTO {

    @ApiModelProperty("预置位id")
    private Long id;

    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("预制位名称")
    private String devicePositionName;

    @ApiModelProperty("预置位索引")
    private String positoinIndex;

}
