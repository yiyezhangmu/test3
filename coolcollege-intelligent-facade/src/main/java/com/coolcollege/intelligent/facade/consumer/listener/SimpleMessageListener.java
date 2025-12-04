package com.coolcollege.intelligent.facade.consumer.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author zhangchenbiao
 * @FileName: SampleMessageListener
 * @Description: 消息监听
 * @date 2021-12-22 17:24
 */
@Service
public class SimpleMessageListener implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext context) {
        if(message.getReconsumeTimes() + 1 >= Integer.valueOf(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
        }
        return null;
    }
}
