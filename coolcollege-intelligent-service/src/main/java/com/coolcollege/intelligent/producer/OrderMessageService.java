package com.coolcollege.intelligent.producer;

import com.aliyun.openservices.ons.api.SendResult;
import com.coolstore.base.enums.RocketMqTagEnum;

/**
 * @author zhangchenbiao
 * @FileName: OrderMessageService
 * @Description: 顺序消息生产者
 * @date 2021-12-22 17:37
 */
public interface OrderMessageService {

    /**
     * 发送顺序消息
     * @param message
     * @param tag
     * @param shardingKey
     * @return
     */
    SendResult send(String message, RocketMqTagEnum tag, final String shardingKey);

}
