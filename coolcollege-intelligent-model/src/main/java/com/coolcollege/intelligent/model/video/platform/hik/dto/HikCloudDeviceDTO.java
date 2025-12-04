package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/8/25 16:23
 * @Version 1.0
 */
@Data
public class HikCloudDeviceDTO {
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备序列号
     */
    private String deviceSerial;

    /**
     * 设备状态，0：离线，1：在线
     */
    private Integer deviceStatus;

    /**
     * 通道ID
     */
    private String channelId;

    /**
     * 通道名
     */
    private String channelName;

    /**
     * 通道号
     */
    private Integer channelNo;

    /**
     * 	通道状态，0：离线，1：在线
     */
    private Integer channelStatus;

    /**
     * 通道封面图片URL
     */
    private String channelPicUrl;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店编号
     */
    private String storeNo;

    /**
     * 通道启用禁用标记，1：启用，0：禁用
     */
    private Integer isUse;

    /**
     * 通道序列号
     */
    private String ipcSerial;


}
