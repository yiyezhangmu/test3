package com.coolcollege.intelligent.model.video.platform.yingshi.webhook;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebhookMessage implements Serializable {

    private WebhookMessageHeader header;

    private Object body;

}
