package com.coolcollege.intelligent.model.video.platform.yushi.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/26
 */
@Data
public class VideoEventRecordStoreVO {

    //主键
    private Long id;
    //捕抓设备Id
    private String deviceId;
    //捕抓的图片地址
    private String sourcePicUrl;
    //告警源名称
    private String alarmName;
    //事件类型
    private String eventType;

    private String eventName;
    //命中时间
    private Long shotTime;
    //创建时间
    private Long createTime;
    //门店ID
    private String storeId;
    private String storeName;

}
