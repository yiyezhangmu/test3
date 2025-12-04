package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSendMessageDTO;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 店务消息提醒
 */
@Slf4j
@Service
public class StoreWorkMessageListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private JmsTaskService jmsTaskService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "StoreWorkMessageListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                List<StoreWorkSendMessageDTO> storeWorkSendMessageDTOList = JSONObject.parseArray(text, StoreWorkSendMessageDTO.class);
                for (StoreWorkSendMessageDTO storeWorkSendMessageDTO : storeWorkSendMessageDTOList) {
                    jmsTaskService.sendStoreWorkMessage(storeWorkSendMessageDTO.getEnterpriseId(), storeWorkSendMessageDTO.getDataTableId(),  storeWorkSendMessageDTO.getOperate(), storeWorkSendMessageDTO.getParamMap());
                }
            }catch (Exception e){
                log.error("StoreWorkMessageListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
