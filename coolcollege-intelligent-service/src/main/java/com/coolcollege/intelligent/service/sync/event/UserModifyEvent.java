package com.coolcollege.intelligent.service.sync.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserModifyEvent extends BaseUserEvent {

    public UserModifyEvent(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.USER_MODIFY_ORG;
    }

    @Override
    public void doEvent() {

        try {
            // 同步人员信息
            log.info("原版本的钉钉同步");
            super.syncUser();
        } catch (Exception e) {
            log.error("报错信息：", e);
            log.error("人员更新失败，用户id为{}", userId);
        }

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserModifyEvent{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }

}
