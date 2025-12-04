package com.coolcollege.intelligent.model.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/12/16 11:38
 * @Version 1.0
 */
@Data
@ApiModel
public class DeviceCaptureLibDTO {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("设备ID")
    private String deviceId;
    @ApiModelProperty("文件名称")
    private String name;
    @ApiModelProperty("视频缩略图快照地址")
    private String snapshotUrl;
    @ApiModelProperty("文件地址")
    private String fileUrl;
    @ApiModelProperty("文件类型 照片:photo 视频:video")
    private String fileType;
    @ApiModelProperty("创建人id")
    private String createUserId;
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @ApiModelProperty("创建时间")
    private Date createTime;
}
