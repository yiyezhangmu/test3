package com.coolcollege.intelligent.model.video.platform;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/30
 */
@Data
public class AddDeviceDTO {
    private String eid;
    private String deviceId;
    private String deviceName;
    private String scene;
    private String remark;
    private String videoType;
    private String dataSourceId;

}
