package com.coolcollege.intelligent.model.device.vo;

import lombok.Data;

@Data
public class OpenVideoUrlVO {

    private String url;

    private String expireTime;

    public OpenVideoUrlVO(String url, String expireTime) {
        this.url = url;
        this.expireTime = expireTime;
    }

}
