package com.coolcollege.intelligent.service.sync.event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpUserModifyScopeEvent extends BaseUserEvent {

    public OpUserModifyScopeEvent(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.OP_USER_MODIFY_AUTH_SCOPE;
    }

    @Override
    public void doEvent() {
        try {
            // 同步人员信息
            log.info("同步用户权限范围");
            super.syncUser();
        } catch (Exception e) {
            log.error("报错信息：", e);
            log.error("同步用户权限范围失败，用户id为{}", userId);
        }

    }
}
