package com.coolcollege.intelligent.model.device.request;

import com.coolstore.base.enums.VideoProtocolTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/12/27 15:30
 * @Version 1.0
 */
@Data
@ApiModel
public class DeviceDownloadCenterRequest {
    @ApiModelProperty(name = "文件名称")
    private String fileName;
    @ApiModelProperty(name = "门店ID")
    private String storeId;
    @ApiModelProperty(name = "开始时间")
    private Date startTime;
    @ApiModelProperty(name = "结束时间")
    private Date endTime;
    @ApiModelProperty(name = "时长 秒")
    private Long duration;
    @ApiModelProperty(name = "设备ID")
    private String deviceId;
    @ApiModelProperty(name = "通道号")
    private String ChannelNo;

    @ApiModelProperty(name = "视频协议类型")
    private VideoProtocolTypeEnum protocolTypeEnum;
}
