package com.coolcollege.intelligent.service.jms.constans;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MQ队列名称枚举
 */
public enum MqQueueNameEnum {
    /**
     *
     */
    MQ_QUEUE_NAME_DING("StoreDingQueue"),// 任务消息
    MQ_QUEUE_NAME_BACKLOG("StoreBacklog"), // 代办消息
    MQ_QUEUE_NAME_BACKLOG_UPDATE("StoreBacklogUpdate"), // 代办消息更新
    MQ_QUEUE_NAME_OPEN_ENTERPRISE("open_enterprise"),// 新开通企业的队列名称
    MQ_QUEUE_NAME_ENTERPRISE_MARKET_BUY("enterprise_market_buy"),// 企业开通套餐的队列名称
    MQ_QUEUE_NAME_TASK_REMINDER_DING("TaskReminderDingQueue"),// 任务提醒的钉钉消息发送的队列名称
    MQ_QUEUE_NAME_TASK_DISTRIBUTION_SALES("TaskDistributionSales"), //分配企业大客户销售发送的队列名称
    MQ_QUEUE_NAME_APP_PUSH("AppPushQueue"); //app推送消息队列


    private static final Map<String, MqQueueNameEnum> map = Arrays.stream(values()).collect(Collectors.toMap(MqQueueNameEnum::getValue, Function.identity()));

    private String value;

    MqQueueNameEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MqQueueNameEnum parseValue(String value) {
        return map.get(value);
    }
}
