package com.coolcollege.intelligent.service.sync.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAddEvent extends BaseUserEvent {

    public UserAddEvent(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.USER_ADD_ORG;
    }

    @Override
    public void doEvent() {
//        String eid = getEid();
        try {
            log.info("原版本的钉钉同步");
            super.syncUser();
        } catch (Exception e) {
            log.error("人员更新失败,人员为{}：", userId, e);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserAddEvent{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", corpId='").append(corpId).append('\'');
        sb.append(", eid=").append(eid);
        sb.append('}');
        return sb.toString();
    }

}
