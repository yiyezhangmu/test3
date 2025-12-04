package com.coolcollege.intelligent.model.callback;

import lombok.Data;

/**
 * @author byd
 * @date 2021-02-05 11:25
 */
@Data
public class CallbackRequest {

    /**
     *消息ID
     */
    private String alarmId;

    /**
     * 告警源名称
     */
    private String alarmName;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 告警时间，UTC时间
     */
    private Long alarmTime;

    /**
     *  通道号
     */
    private String channelNo;

    /**
     *  设备序列号
     */
    private String deviceSerial;

    /**
     *  告警图片地址
     */
    private String picUrl;
}
