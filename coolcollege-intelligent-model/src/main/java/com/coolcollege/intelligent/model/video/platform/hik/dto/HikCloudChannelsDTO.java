package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/12 11:04
 * @Version 1.0
 */
@Data
public class HikCloudChannelsDTO {

    private String deviceId;

    private String deviceName;

    private String deviceModel;

    private String deviceSerial;

    private Integer deviceStatus;

    private String channelId;

    private String channelName;

    private String channelNo;

    private String channelStatus;

    private String channelPicUrl;

    private String storeId;

    private String storeName;

    private String storeNo;

    private Integer isUse;

    private String ipcSerial;

}
