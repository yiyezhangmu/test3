package com.coolcollege.intelligent.model.video.platform.yingshi.webhook;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebhookMessageHeader implements Serializable {

    private String messageId;

    private String deviceId;

    /**
     * 消息类型，需向消息管道服务申请
     */
    private String type;

    private Integer channelNo;

    private Long messageTime;

}
