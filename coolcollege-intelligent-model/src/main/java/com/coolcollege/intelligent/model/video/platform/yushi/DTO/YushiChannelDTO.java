package com.coolcollege.intelligent.model.video.platform.yushi.DTO;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/06
 */
@Data
public class YushiChannelDTO {

    private String parentDeviceId;

    private String deviceSerial;
    private Integer channelNo;
    private String channelName;
    private Integer status;
    private Integer isShared;
    private String permission;

}
