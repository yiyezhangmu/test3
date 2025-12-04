package com.coolcollege.intelligent.service.sync.qywxEvent;

import com.coolcollege.intelligent.service.sync.event.BaseEvent;

/**
 * 企业微信用户删除事件消费
 */
public class DeleteUserEvent extends BaseChatUserEvent {

    public DeleteUserEvent(String corpId, String userId, String appType) {
        this.corpId = corpId;
        this.userId = userId;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.DELETE_USER;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserLeaveEvent{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }
}
