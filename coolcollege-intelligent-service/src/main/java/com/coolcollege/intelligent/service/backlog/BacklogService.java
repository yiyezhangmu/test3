package com.coolcollege.intelligent.service.backlog;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @author 邵凌志
 * @date 2021/1/27 15:21
 */
@Service
public class BacklogService {

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Resource
    private SimpleMessageService simpleMessageService;

    /**
     * 更新钉钉待办消息
     * @param eid
     * @param backlogId
     * @return
     */
    public Boolean updateBacklogStatus(String eid, String backlogId) {
        CurrentUser user = UserHolder.getUser();
        JSONObject map = new JSONObject();
        map.put("corpId", user.getDingCorpId());
        map.put("userId", user.getUserId());
        map.put("backlogId", backlogId);
        map.put("enterpriseId", eid);
        map.put("appType", user.getAppType());
        simpleMessageService.send(map.toString(), RocketMqTagEnum.STORE_BACK_LOG_UPDATE);
        return Boolean.TRUE;
    }
}
