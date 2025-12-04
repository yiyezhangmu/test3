package com.coolcollege.intelligent.service.sync.event;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 邵凌志
 * @date 2020/10/22 15:02
 */
@Slf4j
public class RoleAddOrModifyEvent extends BaseRoleEvent {

    public RoleAddOrModifyEvent(String corpId, String roleInfo, String appType) {
        this.corpId = corpId;
        this.roleInfo = roleInfo;
        this.appType = appType;
    }

    @Override
    public String getEventType() {
        return BaseEvent.ROLE_ADD_OR_MODIFY;
    }

    @Override
    public void doEvent() {
        super.doEvent();
    }
}
