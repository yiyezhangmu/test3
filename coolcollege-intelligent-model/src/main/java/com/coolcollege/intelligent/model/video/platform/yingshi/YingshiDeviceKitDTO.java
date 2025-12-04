package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

/**
 * 多维客流设备
 */
@Data
public class YingshiDeviceKitDTO {

    /**
     * 索引id
     */
    private int id;
    /**
     * 设备序列号
     */
    private String deviceSerial;
    /**
     * 设备通道号
     */
    private String channelNo;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 绑定区域
     */
    private String regionTag;
    /**
     * 设备添加时间
     */
    private String addTime;
    /**
     * 	设备在线状态 0-不在线，1-在线
     */
    private int status;
    /**
     * 客流开关状态，true-开启 false-关闭
     */
    private boolean enable;
}
