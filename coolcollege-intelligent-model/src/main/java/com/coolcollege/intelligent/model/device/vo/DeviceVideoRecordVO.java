package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备录像文件VO
 * </p>
 *
 * @author wangff
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceVideoRecordVO {

    @ApiModelProperty("用户id 即客户端id tp回放用户id，用于区分并限制同时观看回放视频的用户数量")
    private String userId;

    @ApiModelProperty("回放源,0-系统自动选择，1-云存储，2-本地录像")
    private Integer recType;

    @ApiModelProperty("文件开始时间，毫秒时间戳")
    private Long startTime;

    @ApiModelProperty("文件结束时间")
    private Long endTime;

    @ApiModelProperty("设备序列号")
    private String deviceSerial;

    @ApiModelProperty("设备通道号")
    private String channelNo;

    @ApiModelProperty("文件类型 0:ALARM 1:TIMING 2:IO")
    private String localType;
}
