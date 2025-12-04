package com.coolcollege.intelligent.model.device.request;

import lombok.Data;

@Data
public class TPLinkNoticePushRequest {

    private String qrCode;

    private String mac;

    private String deviceName;

    private String parentQrCode;

    private String parentMac;

    private String channelId;

    private String msgId;

    private String imageId;

    private String time;

    private String msgType;

    private String msgContentType;
}
