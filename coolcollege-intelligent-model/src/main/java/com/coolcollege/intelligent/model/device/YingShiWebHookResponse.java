package com.coolcollege.intelligent.model.device;

import lombok.Data;

@Data
public class YingShiWebHookResponse {

    private String messageId;;


    public YingShiWebHookResponse(String messageId) {
        this.messageId = messageId;
    }
}
