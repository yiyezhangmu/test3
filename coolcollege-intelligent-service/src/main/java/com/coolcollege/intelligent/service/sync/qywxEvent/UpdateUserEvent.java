package com.coolcollege.intelligent.service.sync.qywxEvent;

import com.coolcollege.intelligent.service.sync.event.BaseEvent;

public class UpdateUserEvent extends BaseChatUserEvent {

    public UpdateUserEvent(String corpId, String userId, String appType) {
        this.corpId = corpId;
        this.userId = userId;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.UPDATE_USER;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UpdateUserEvent{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }
}
