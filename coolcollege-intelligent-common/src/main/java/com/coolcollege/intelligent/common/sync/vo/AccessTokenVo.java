package com.coolcollege.intelligent.common.sync.vo;

import com.alibaba.fastjson.annotation.JSONField;

public class AccessTokenVo {

    @JSONField(name = "access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
