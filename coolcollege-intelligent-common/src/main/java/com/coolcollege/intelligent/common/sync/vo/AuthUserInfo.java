package com.coolcollege.intelligent.common.sync.vo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 授权用户信息
 */
public class AuthUserInfo {

    @JSONField(name = "userId")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
