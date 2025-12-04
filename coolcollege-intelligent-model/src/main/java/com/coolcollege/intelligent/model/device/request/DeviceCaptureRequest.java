package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/12/16 10:57
 * @Version 1.0
 */
@Data
@ApiModel
public class DeviceCaptureRequest {

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

    private List<Long> deviceCaptureIds;

}
