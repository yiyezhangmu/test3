package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.patrolstore.dto.SendWXGroupMessageDTO;
import com.coolcollege.intelligent.service.patrolstore.GroupConfigService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangchenbiao
 * @FileName: WXGroupMessageListener
 * @Description:
 * @date 2024-09-12 16:21
 */
@Slf4j
@Component
public class WXGroupMessageListener implements MessageListener {

    @Resource
    private GroupConfigService groupConfigService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，message:{}，try times：{}", message.getMsgID(), text, message.getReconsumeTimes());

        String lockKey = "WXGroupMessageListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if (lock) {
            try {
                SendWXGroupMessageDTO param = JSONObject.parseObject(text, SendWXGroupMessageDTO.class);
                groupConfigService.sendWXGroupMessage(param);
            } catch (Exception e) {
                log.error("WXGroupMessageListener consume error", e);
                redisUtilPool.delKey(lockKey);
                return Action.ReconsumeLater;
            }
            log.info("消费成功，tag：{}，messageId：{}", message.getTag(), message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
