package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wxp
 * @Date 2024/12/27 15:30
 * @Version 1.0
 */
@Data
@ApiModel
public class DeviceRecordQueryRequest {

    @ApiModelProperty("设备序列号")
    private String deviceSerial;
    @ApiModelProperty("设备通道号")
    private String channelNo;
    @ApiModelProperty("开始时间-时间戳")
    private Long startTime;
    @ApiModelProperty("结束时间-时间戳")
    private Long endTime;


}
