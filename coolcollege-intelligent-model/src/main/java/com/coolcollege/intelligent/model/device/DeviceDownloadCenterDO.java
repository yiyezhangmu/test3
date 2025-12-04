package com.coolcollege.intelligent.model.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-12-16 11:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDownloadCenterDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("文件名称")
    private String name;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("截止时间")
    private Date endTime;

    @ApiModelProperty("视频时长 单位秒")
    private Long duration;

    @ApiModelProperty("下载状态  0初始化 1成功 2失败")
    private Integer status;

    @ApiModelProperty("是否同步抓拍库")
    private Integer isSyncCaptureLib;

    @ApiModelProperty("文件地址")
    private String fileUrl;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("文件ID")
    private String fileId;

    @ApiModelProperty("下载失败错误描述")
    private String errorMsg;
}