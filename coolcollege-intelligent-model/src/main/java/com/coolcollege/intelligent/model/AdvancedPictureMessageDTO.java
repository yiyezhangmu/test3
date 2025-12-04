package com.coolcollege.intelligent.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author byd
 * @date 2025-11-06 19:29
 */
@Data
@ApiModel(description = "即时抽帧消息DTO")
public class AdvancedPictureMessageDTO {

    @ApiModelProperty(value = "消息类型，即时抽帧(advanced_picture)")
    private String messageType;

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "文件ID")
    private String fileId;

    @ApiModelProperty(value = "图片地址")
    private String picUrl;

    @ApiModelProperty(value = "设备序列号")
    private String deviceSerial;

    @ApiModelProperty(value = "通道号")
    private String channelNo;

    @ApiModelProperty(value = "错误码")
    private String errorCode;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;
}