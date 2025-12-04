package com.coolcollege.intelligent.producer;

import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.coolcollege.intelligent.common.config.rocketmq.RocketMqConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhangchenbiao
 * @FileName: ProducerClient
 * @Description: 普通消息client
 * @date 2021-12-21 11:33
 */
@Configuration
public class ProducerClient {

    @Autowired
    private RocketMqConfig rocketMqConfig;

    /**
     * 普通消息
     * @return
     */
    @Primary
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean producerBean() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(rocketMqConfig.getMqProperties());
        return producer;
    }

    /**
     * 分区顺序消息
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public OrderProducerBean orderProducerBean() {
        OrderProducerBean producer = new OrderProducerBean();
        producer.setProperties(rocketMqConfig.getMqProperties());
        return producer;
    }

}
