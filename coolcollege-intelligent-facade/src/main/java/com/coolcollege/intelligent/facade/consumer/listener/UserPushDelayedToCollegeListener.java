package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.coolcollege.GetCoolCollegeOpenResultDTO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @Author suzhuhong
 * @Date 2022/6/21 15:52
 * @Version 1.0
 */
@Slf4j
@Service
public class UserPushDelayedToCollegeListener  implements MessageListener {
    @Resource
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "UserPushDelayedTOCollegeListener:" + message.getMsgID();
        GetCoolCollegeOpenResultDTO resultDTO = JSONObject.parseObject(text, GetCoolCollegeOpenResultDTO.class);
        log.info("UserPushDelayedTOCollegeListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), JSONObject.toJSONString(resultDTO));
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                coolCollegeIntegrationApiService.sendUsersToCoolCollege(resultDTO.getStoreEnterpriseId(), Collections.emptyList(), resultDTO.getRegionId());
            } catch (Exception e) {
                log.error("UserPushDelayedTOCollegeListener  error" + e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.CommitMessage;
    }
}
