package com.coolcollege.intelligent.common.sync.vo;

import java.util.List;

/**
 * 门店分组
 * @author xugk
 */
public class StoreGroupReqBody {

    private String corpId;

    private String eventType;

    private Long groupId;

    private List<Long> storeDeptIdList;

    private String appType;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getStoreDeptIdList() {
        return storeDeptIdList;
    }

    public void setStoreDeptIdList(List<Long> storeDeptIdList) {
        this.storeDeptIdList = storeDeptIdList;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @Override
    public String toString() {
        return "StoreGroupReqBody{" +
                "corpId='" + corpId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", groupId=" + groupId +
                ", storeDeptIdList=" + storeDeptIdList +
                ", appType='" + appType + '\'' +
                '}';
    }
}
