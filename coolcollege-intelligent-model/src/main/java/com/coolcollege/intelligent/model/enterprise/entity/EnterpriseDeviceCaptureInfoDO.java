package com.coolcollege.intelligent.model.enterprise.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备异步抓拍信息
 * @author   zhangchenbiao
 * @date   2025-11-06 04:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseDeviceCaptureInfoDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("企业Id")
    private String enterpriseId;

    @ApiModelProperty("设备id device_type=nvr_ipc时是parent_device_id_channel_no")
    private String deviceId;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("业务类型 aiInspection：ai巡检")
    private String businessType;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("抓拍任务id")
    private String captureTaskId;

    @ApiModelProperty("图片地址")
    private String picUrl;

    @ApiModelProperty("文件id")
    private String fileId;

    @ApiModelProperty("结果 0 未返回成功  1 返回成功 2 上传失败  3 超时失败")
    private Integer taskResult;

    @ApiModelProperty("设备来源")
    private String resource;

    @ApiModelProperty("错误编码")
    private String errorCode;

    @ApiModelProperty("错误原因")
    private String errorMsg;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}