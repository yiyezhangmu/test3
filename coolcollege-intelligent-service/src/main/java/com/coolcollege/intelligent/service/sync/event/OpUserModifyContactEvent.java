package com.coolcollege.intelligent.service.sync.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpUserModifyContactEvent extends BaseUserEvent {

    public OpUserModifyContactEvent(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.OP_USER_MODIFY_CONTACT;
    }

    @Override
    public void doEvent() {
        try {
            // 同步人员信息
            log.info("同步用户所在区域");
            super.syncUser();
        } catch (Exception e) {
            log.error("报错信息：", e);
            log.error("同步用户所在区域失败，用户id为{}", userId);
        }

    }
}
