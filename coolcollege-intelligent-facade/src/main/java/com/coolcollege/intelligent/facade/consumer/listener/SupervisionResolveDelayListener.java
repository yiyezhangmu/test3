package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.supervision.dto.TaskResolveDelayDTO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 20:18
 * @Version 1.0
 */
@Slf4j
@Service
public class SupervisionResolveDelayListener implements MessageListener {

    @Resource
    private SupervisionTaskParentService supervisionTaskParentService;
    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if(message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
            return Action.CommitMessage;
        }
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "SupervisionResolveDelayListener:" + message.getMsgID();
        TaskResolveDelayDTO resultDTO = JSONObject.parseObject(text, TaskResolveDelayDTO.class);
        log.info("SupervisionResolveDelayListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), JSONObject.toJSONString(resultDTO));
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                supervisionTaskParentService.splitSupervisionTaskForPerson(resultDTO.getEnterpriseId(),resultDTO.getCurrentUser(),resultDTO.getParentId(),resultDTO.getTaskStartTime());
            } catch (Exception e) {
                log.error("SupervisionResolveDelayListener error" + e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.CommitMessage;
    }
}
