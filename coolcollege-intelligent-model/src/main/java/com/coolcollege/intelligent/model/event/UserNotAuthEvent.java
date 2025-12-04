package com.coolcollege.intelligent.model.event;

import java.util.List;

/**
 * 用户不在授权的event
 */
public class UserNotAuthEvent {
    private String corpId;
    private String appType;
    private List<String> userIds;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
