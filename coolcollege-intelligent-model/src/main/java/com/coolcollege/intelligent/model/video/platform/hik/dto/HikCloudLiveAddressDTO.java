package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/8/25 17:14
 * @Version 1.0
 */
@Data
public class HikCloudLiveAddressDTO {

    /**
     * 设备序列号
     */
    private String deviceSerial;
    /**
     * 设备名称
     */
    private String deviceName;
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
    private String channelNo;

    /**
     * HLS流畅标准流预览地址
     */
    private String hls;

    /**
     * HLS高清标准流预览地址
     */
    private String hlsHd;
    /**
     * RTMP流畅标准流预览地址
     */
    private String rtmp;
    /**
     * 	RTMP高清标准流预览地址
     */
    private String rtmpHd;
    /**
     * flv流畅标准流预览地址
     */
    private String flvAddress;
    /**
     * flv高清标准流预览地址
     */
    private String hdFlvAddress;

    /**
     * 地址使用状态：0-未使用或标准流预览已关闭，1-使用中，2-已过期，3-标准流预览已暂停，0状态不返回地址，其他返回。-1表示ret不返回200时的异常情况
     */
    private String status;

    /**
     * 地址异常状态：0-正常，1-设备不在线，2-设备开启视频加密，3-设备删除，4-失效，5-未绑定，6-账户下流量已超出，0/1/2/6状态返回地址，其他不返回。-1表示ret不返回200时的异常情况
     */
    private Integer exception;

}
