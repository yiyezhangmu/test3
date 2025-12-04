package com.coolcollege.intelligent.service.sync.qywxEvent;

import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
import com.coolcollege.intelligent.service.sync.event.BaseEvent;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 企业微信用户同步事件处理入库
 */
@Slf4j
public abstract class BaseChatUserEvent extends BaseEvent {

    protected String userId;

    @Override
    public void doEvent() {
        List<String> originUserIds = Arrays.stream(userId.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(","))
                        .collect(Collectors.toList());
        log.info("企业微信用户同步开始userId:{}", originUserIds);
        ChatService chatService = SpringContextUtil.getBean("chatService", ChatService.class);
        String accessToken = chatService.getDkfOrQwAccessToken(corpId, appType);
        DataSourceHelper.reset();
        QywxUserSyncService qywxUserSyncService = SpringContextUtil.getBean("qywxUserSyncService", QywxUserSyncService.class);
        EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
        CoolCollegeIntegrationApiService coolCollegeIntegrationApiService = SpringContextUtil.getBean("coolCollegeIntegrationApiServiceImpl", CoolCollegeIntegrationApiService.class);
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(corpId, appType);
        if(Objects.isNull(config)){
            return;
        }
        String eid = config.getEnterpriseId();
        String dbName = config.getDbName();
        if (getEventType().equals(CREATE_USER) || getEventType().equals(UPDATE_USER)) {
            log.info("企业微信用户添加、更新事件: {}", originUserIds);
            List<String> userIds = new ArrayList<>();
            originUserIds.forEach(userId -> {
                try {
                    qywxUserSyncService.syncWeComUser(corpId, userId, accessToken, eid, dbName, config.getAppType());
                    userIds.add(corpId + "_" + userId);
                } catch (Exception e) {
                    log.error("企业微信用户添加、更新失败，userId:{}, eid:{}", userId, eid, e);
                }
            });
            //用户信息变更，推送变更消息,异步推送，如果是业培一体2.0 并且开通业培一体服务，做推送酷学院
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        }
        if (getEventType().equals(DELETE_USER)) {
            log.info("企业微信用户离职事件: {}", originUserIds);
            originUserIds.forEach(userId -> {
                try {
                    userId = corpId + "_" + userId;
                    qywxUserSyncService.syncDeleteWeComUser(eid, userId, dbName);
                } catch (Exception e) {
                    log.error("企业微信用户删除失败，userId:{}, eid:{}", userId, eid, e);
                }
            });
        }

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
