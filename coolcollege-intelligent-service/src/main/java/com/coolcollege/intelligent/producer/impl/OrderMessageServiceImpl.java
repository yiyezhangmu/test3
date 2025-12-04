package com.coolcollege.intelligent.producer.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.coolcollege.intelligent.common.config.rocketmq.RocketMqConfig;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Properties;

/**
 * @author zhangchenbiao
 * @FileName: OrderMessageServiceImpl
 * @Description: 顺序消息生产实现
 * @date 2021-12-22 17:38
 */
@Slf4j
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Resource
    private RocketMqConfig rocketMqConfig;

    @Resource
    private OrderProducerBean orderProducerBean;

    @Override
    public SendResult send(String message, RocketMqTagEnum tag, String shardingKey) {
        if(StringUtils.isAnyBlank(message, shardingKey) || Objects.isNull(tag)){
            return new SendResult();
        }
        try {
            Message msg = new Message(rocketMqConfig.getOrderTopic(), tag.getTag(), message.getBytes("UTF-8"));
            Properties properties = new Properties();
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            properties.setProperty(Constants.REQUEST_ID, requestId);
            msg.setUserProperties(properties);
            SendResult send = orderProducerBean.send(msg, shardingKey);
            log.info("发送消息：data:{}, tag:{}, shardingKey:{}", JSONObject.toJSONString(send), tag.getTag(), shardingKey);
            return send;
        } catch (Exception e) {
            log.error("send@@@@@@@@@ddd", e);
        }
        return null;
    }
}
