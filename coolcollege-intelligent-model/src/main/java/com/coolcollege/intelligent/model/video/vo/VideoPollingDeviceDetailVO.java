package com.coolcollege.intelligent.model.video.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/20
 */
@Data
public class VideoPollingDeviceDetailVO {
    private String deviceId;
    private String deviceName;
    private String storeName;
    private Boolean isDelete;
}
