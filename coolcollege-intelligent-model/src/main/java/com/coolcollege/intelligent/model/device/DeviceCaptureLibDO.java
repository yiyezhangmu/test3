package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-12-16 11:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCaptureLibDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("设备id")
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