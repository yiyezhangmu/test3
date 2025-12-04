package com.coolcollege.intelligent.facade.consumer.listener;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import org.springframework.stereotype.Service;

/**
 * @author chenyupeng
 * @since 2021/12/24
 */
@Service
public class OrderMessageListener implements MessageOrderListener {
    @Override
    public OrderAction consume(Message message, ConsumeOrderContext consumeOrderContext) {
        return null;
    }
}
