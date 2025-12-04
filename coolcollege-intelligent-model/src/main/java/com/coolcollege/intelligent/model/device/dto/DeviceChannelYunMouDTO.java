package com.coolcollege.intelligent.model.device.dto;

import lombok.Data;

@Data
public class DeviceChannelYunMouDTO {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     *设备名称
     */
    private String deviceName;

    /**
     *设备型号
     */
    private String deviceModel;

    /**
     *设备序列号
     */
    private String deviceSerial;

    /**
     *设备状态，0：离线，1：在线
     */
    private String deviceStatus;

    /**
     *通道ID
     */
    private String channelId;

    /**
     *通道名
     */
    private String channelName;

    /**
     *通道号
     */
    private String channelNo;

    /**
     *通道状态，0：离线，1：在线
     */
    private String channelStatus;

    /**
     *通道封面图片URL
     */
    private String channelPicUrl;

    /**
     *门店ID
     */
    private String storeId;

    /**
     *门店名称
     */
    private String storeName;

    /**
     *门店编号
     */
    private String storeNo;

    /**
     *通道启用禁用标记，1：启用，0：禁用
     */
    private String isUse;

    /**
     *通道序列号
     */
    private String ipcSerial;
}
