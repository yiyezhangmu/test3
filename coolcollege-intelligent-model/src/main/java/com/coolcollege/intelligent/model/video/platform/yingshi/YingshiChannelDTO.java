package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/06
 */
@Data
public class YingshiChannelDTO {

    private String deviceSerial;
    private String deviceName;
    private String ipcSerial;
    private String channelNo;
    private String channelName;
    private Integer status;
    private String picUrl;
    private Integer isEncrypt;
    private Integer videoLevel;
    private Boolean relatedIpc;
    private Integer isAdd;

}
