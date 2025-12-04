package com.coolcollege.intelligent.model.device;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/07
 */
@Data
public class DeviceChannelDO {
    private Long id;
    //该字段仅记录部分平台子通道号有单独通道唯一建，不使用该字段。
    private String deviceId;
    private String parentDeviceId;
    private String channelNo;
    private String channelName;
    private String status;
    private Boolean hasPtz;
    private Date createTime;
    private Date updateTime;
    /**
     * 门店场景id
     */
    private Long storeSceneId;
    /**
     * 是否支持抓图 0-不支持 1-支持
     */
    private Integer supportCapture;
    /**
     * 是否支持客流分析
     */
    private Boolean supportPassenger;

    /**
     * 是否开启客流分析
     */
    private Boolean enablePassenger;


    private String remark;

    private String extendInfo;


}
