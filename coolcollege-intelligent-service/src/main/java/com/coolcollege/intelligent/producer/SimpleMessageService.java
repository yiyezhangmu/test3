package com.coolcollege.intelligent.producer;

import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolstore.base.enums.RocketMqTagEnum;

/**
 * @author zhangchenbiao
 * @FileName: MessageProducerService
 * @Description: rocketmq 消息生产者
 * @date 2021-12-22 16:12
 */
public interface SimpleMessageService {

    /**
     * 同步发送
     * @param message  消息
     * @param tag tag
     * @return
     */
    SendResult send(String message, RocketMqTagEnum tag);

    /**
     * 单向发送
     * @param message 消息
     * @param tag tag
     */
    void sendOneway(String message, RocketMqTagEnum tag);

    /**
     * 异步发送
     * @param message 消息
     * @param tag tag
     * @param sendCallback  回调
     */
    void sendAsync(String message, RocketMqTagEnum tag, SendCallback sendCallback);

    /**
     * 同步发送（延时）
     * @param message
     * @param tag
     * @param startDeliverTime
     * @return
     */
    SendResult send(String message, RocketMqTagEnum tag, Long startDeliverTime);
}
