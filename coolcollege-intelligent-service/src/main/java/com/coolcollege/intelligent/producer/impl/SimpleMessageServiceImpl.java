package com.coolcollege.intelligent.producer.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.coolcollege.intelligent.common.config.rocketmq.RocketMqConfig;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * @author zhangchenbiao
 * @FileName: MessageProducerServiceImpl
 * @Description: 消息生产者实现
 * @date 2021-12-22 16:12
 */
@Service
@Slf4j
public class SimpleMessageServiceImpl implements SimpleMessageService {

    @Resource
    private RocketMqConfig rocketMqConfig;

    @Resource
    private ProducerBean producer;

    @Override
    public SendResult send(String message, RocketMqTagEnum tag) {
        if(StringUtils.isBlank(message) || Objects.isNull(tag)){
            return new SendResult();
        }
        try {
            Message msg = new Message(rocketMqConfig.getTopic(), tag.getTag(), message.getBytes("UTF-8"));
            Properties properties = new Properties();
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            properties.setProperty(Constants.REQUEST_ID, requestId);
            msg.setUserProperties(properties);
            SendResult send = producer.send(msg);
            log.info("消息发送send response:{}, tag:{}", JSONObject.toJSONString(send), tag.getTag());
            return send;
        } catch (Exception e) {
            log.error("send#######", e);
        }
        return new SendResult();

    }

    @Override
    public void sendOneway(String message, RocketMqTagEnum tag) {
        if(StringUtils.isBlank(message) || Objects.isNull(tag)){
            return;
        }
        try {
            Message msg = new Message(rocketMqConfig.getTopic(), tag.getTag(), message.getBytes("UTF-8"));
            Properties properties = new Properties();
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            properties.setProperty(Constants.REQUEST_ID, requestId);
            msg.setUserProperties(properties);
            producer.sendOneway(msg);
        } catch (Exception e) {
            log.error("send@@@@@@", e);
        }
    }

    @Override
    public void sendAsync(String message, RocketMqTagEnum tag, SendCallback sendCallback) {
        if(StringUtils.isBlank(message) || Objects.isNull(tag)){
            return;
        }
        try {
            Message msg = new Message(rocketMqConfig.getTopic(), tag.getTag(), message.getBytes("UTF-8"));
            Properties properties = new Properties();
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            properties.setProperty(Constants.REQUEST_ID, requestId);
            msg.setUserProperties(properties);
            producer.sendAsync(msg, sendCallback);
        } catch (Exception e) {
            log.error("sendAsync@@@@@@", e);
        }
    }

    @Override
    public SendResult send(String message, RocketMqTagEnum tag, Long startDeliverTime) {
        if(StringUtils.isBlank(message) || Objects.isNull(tag)){
            return new SendResult();
        }
        try {
            Message msg = new Message(rocketMqConfig.getTopic(), tag.getTag(), message.getBytes("UTF-8"));
            if(Objects.nonNull(startDeliverTime)) {
                log.info("{} startDeliverTime:{}", tag.getTag(),new Date(startDeliverTime));
                msg.setStartDeliverTime(startDeliverTime);
            }
            Properties properties = new Properties();
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            properties.setProperty(Constants.REQUEST_ID, requestId);
            msg.setUserProperties(properties);
            SendResult send = producer.send(msg);
            log.info("发送消息：data:{}, tag:{}", JSONObject.toJSONString(send), tag.getTag());
            return send;
        } catch (Exception e) {
            log.error("send@@@@@@@@@", e);
        }
        return new SendResult();
    }
}
