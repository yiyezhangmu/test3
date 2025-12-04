package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.service.form.FormInitializeService;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 初始化设备
 *
 * @author chenyupeng
 * @since 2022/2/28
 */
@Slf4j
@Service
public class InitDeviceQueueListener implements MessageListener {

    @Resource
    private FormInitializeService formInitializeService;

    @Resource
    private WeComService weComService;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {

        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "InitDeviceQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                initDevice(text);
            }catch (Exception e){
                log.error("InitDeviceQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void initDevice(String text) {
        JSONObject jsonObject = JSONObject.parseObject(text);
        String eid = jsonObject.getString("eid");
//        formInitializeService.initDevice(eid);  企微上架用，上架后不再执行绑定摄像头的操作
        weComService.sendOpenSucceededMsg(eid);
    }
}
