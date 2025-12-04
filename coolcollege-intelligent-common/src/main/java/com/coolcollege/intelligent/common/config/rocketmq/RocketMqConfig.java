package com.coolcollege.intelligent.common.config.rocketmq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author zhangchenbiao
 * @FileName: RocketMqConfig
 * @Description: 该服务的rocketmq的配置 以及生产者的topic
 * @date 2021-12-21 11:33
 */
@Configuration
@ConfigurationProperties(prefix = "rocketmq")
public class RocketMqConfig {

    private String accessKey;
    private String secretKey;
    private String nameSrvAdder;
    /**
     * 普通消息的topic
     */
    private String topic;
    /**
     * 分区顺序消息topic
     */
    private String orderTopic;

    public Properties getMqProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, this.accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, this.secretKey);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, this.nameSrvAdder);
        return properties;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getNameSrvAdder() {
        return nameSrvAdder;
    }

    public void setNameSrvAdder(String nameSrvAdder) {
        this.nameSrvAdder = nameSrvAdder;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getOrderTopic() {
        return orderTopic;
    }

    public void setOrderTopic(String orderTopic) {
        this.orderTopic = orderTopic;
    }
}
