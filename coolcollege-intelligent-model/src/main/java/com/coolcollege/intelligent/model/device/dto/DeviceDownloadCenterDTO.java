package com.coolcollege.intelligent.model.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/12/27 16:47
 * @Version 1.0
 */
@Data
public class DeviceDownloadCenterDTO {

    private Long id;

    private String storeId;

    private String name;

    private Date startTime;

    private Long duration;

    private Integer status;

    private Integer isSyncCaptureLib;

    private String fileUrl;

    private String fileId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("下载失败错误描述")
    private String errorMsg;
}
