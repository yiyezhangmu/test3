package com.coolcollege.intelligent.service.jms;


import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.jms.constans.MqQueueNameEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ydw on 2019/5/8.
 */
@Service
@Slf4j
public class JmsSendMessageSyncService {

    @Resource(name = "noticeThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Resource
    private SimpleMessageService simpleMessageService;

    private static final int MAX_MESSAGE_USER_COUNT = 4500;

    private static final int SLEEP_TIME_SECOND = 60 * 1000;

    /**
     * 异步发送消息
     *
     * @param corpId
     * @param userIds
     * @param args
     * @param queueName
     */
    @Async("noticeThreadPool")
    public void sendMessageAsync(String corpId, List<String> userIds, JSONObject args, String queueName) {
        try {
            if (userIds.size() >= MAX_MESSAGE_USER_COUNT) {
                this.sendMessageForMaxUser(userIds, args, queueName);
            } else {
                this.sendMessage(userIds, args, queueName);
            }
        } catch (Exception e) {
            log.info("sendMessageAsync error:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 同步发送
     *
     * @param corpId
     * @param userIds
     * @param args
     * @param queueName
     */
    public void sendMessageSync(String corpId, List<String> userIds, JSONObject args, String queueName) {
        if (userIds.size() >= MAX_MESSAGE_USER_COUNT) {
            this.sendMessageForMaxUser(userIds, args, queueName);
        } else {
            this.sendMessage(userIds, args, queueName);
        }
    }

    /**
     * 对大于5000人次的发送
     *
     * @param userIds
     * @param args
     * @param queueName
     */
    private void sendMessageForMaxUser(List<String> userIds, JSONObject args, String queueName) {
        executor.submit(() -> {
            List<List<String>> users = Lists.partition(userIds, MAX_MESSAGE_USER_COUNT);
            for (int i = 0; i < users.size(); i++) {
                this.sendMessage(users.get(i), args, queueName);
                if (i != (users.size() - 1)) {
                    try {
                        // 由于钉钉对同一家企业1分钟发送消息的人数不能超过5000，那么如果超过5000则睡一分钟
                        Thread.sleep(SLEEP_TIME_SECOND);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("sendMessageSync Exception:" + e);
                    }
                }
            }
        });
    }


    /**
     * 发送消息(E应用ISV使用SDK发送)
     *
     * @param userIds
     * @param args
     * @param queueName
     */
    public void sendMessage(List<String> userIds, JSONObject args, String queueName) {
        List<List<String>> users = Lists.partition(userIds, 200);
        users.forEach(u -> {
            args.put("userIds", String.join(",", u));
            if(MqQueueNameEnum.MQ_QUEUE_NAME_DING.getValue().equals(queueName)){
                simpleMessageService.send(args.toString(), RocketMqTagEnum.STORE_DING_QUEUE);
            }else if(MqQueueNameEnum.MQ_QUEUE_NAME_APP_PUSH.getValue().equals(queueName)){
                simpleMessageService.send(args.toString(), RocketMqTagEnum.APP_PUSH_QUEUE);
            }
        });
    }
}
