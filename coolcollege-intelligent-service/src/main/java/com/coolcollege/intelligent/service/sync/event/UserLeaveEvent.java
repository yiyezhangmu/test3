package com.coolcollege.intelligent.service.sync.event;

import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserMappingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class UserLeaveEvent extends BaseUserEvent {


    public UserLeaveEvent(String corpId, String userId) {
        this.corpId = corpId;
        this.userId = userId;
    }

    @Override
    public String getEventType() {
        return BaseEvent.USER_LEAVE_ORG;
    }

    @Override
    public void doEvent() {
        log.info("原版本的钉钉同步");
        List<String> userIds = Arrays.stream(userId.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").split(",")).collect(Collectors.toList());
        log.info("UserLeaveEvent dingding userIds : {}", userIds);
        String eid = getEid();
        String dbName = getDbName();
        log.info("UserLeaveEvent enterprise userIds : {}", userIds);
        try {
            RedisUtilPool redisUtil = SpringContextUtil.getBean("redisUtilPool", RedisUtilPool.class);
            DataSourceHelper.changeToSpecificDataSource(dbName);
            try {
                log.info("B1打卡组人员离职事件....eid={},corpId={},users={}",eid,corpId,userIds);
            }catch (Exception e){
                log.info("B1打卡组人员离职事件异常....eid={},corpId={},users={},e={}",eid,corpId,userIds,e);
            }
            log.info("企业id为{}", eid);
            if (CollectionUtils.isNotEmpty(userIds)) {
                delUsersInfo(userIds);
                if (getEnableDingSync()) {
                    if (StringUtils.isNotEmpty(redisUtil.getString("syncDingStore" + eid))) {
                        Long second = redisUtil.getExpire("syncDingStore" + eid);
                        log.info("线程处于同步中，在等待。。。。");
                        Thread.sleep(second * 1000L);
                    }
                    log.info("开始自动同步人员离职事件。。。。。。");
                }
                this.deleteStoreAndUser(userIds);
            }
        } catch (Exception e) {
            log.error("delUsersInfo error, corpId={}, userId={}", corpId, userId, e);
        }
    }

    public void delUsersInfo(List<String> userIdList) {
        DataSourceHelper.reset();

        String eid = getEid();
        SpringContextUtil.getBean("enterpriseUserMappingService", EnterpriseUserMappingService.class).deleteByUserIds(userIdList, eid);

        DataSourceHelper.changeToSpecificDataSource(getDbName());

        //删除企业用户
        SpringContextUtil.getBean("enterpriseUserService", EnterpriseUserService.class).deleteEnterpriseByUserIds(userIdList, eid);
    }

    public void deleteStoreAndUser(List<String> userIds) {
        String eid = getEid();
        DataSourceHelper.changeToSpecificDataSource(getDbName());
        log.info("此次人员离职实践删除的人员关系为{}", userIds);
        UserAuthMappingService authMappingService = SpringContextUtil.getBean("userAuthMappingServiceImpl", UserAuthMappingService.class);
        authMappingService.deleteUserAuthMapping(eid, userIds.get(0));

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
