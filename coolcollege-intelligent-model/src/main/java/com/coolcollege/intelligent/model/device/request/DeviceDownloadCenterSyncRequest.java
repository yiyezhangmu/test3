package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/12/27 18:10
 * @Version 1.0
 */
@Data
public class DeviceDownloadCenterSyncRequest {

    private Long id;

    private String storeId;

    private String name;

    private String fileUrl;

    private String deviceId;
}
