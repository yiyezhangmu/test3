package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.coolcollege.GetCoolCollegeOpenResultDTO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * 门店端的部门，人员，职位，发生改变后，监听消息，用于推送到酷学院
 * @author xuanfeng
 * @since 2022/4/26
 */
@Slf4j
@Service
public class CoolStoreDataChangeListener implements MessageListener {
    @Resource
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        if(message.getReconsumeTimes() + 1 >= Integer.parseInt(RocketMqConstant.MaxReconsumeTimes)){
            //超过最大消费次数
            return Action.CommitMessage;
        }
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "CoolStoreDataChangeListener:" + message.getMsgID();
        CoolStoreDataChangeDTO resultDTO = JSONObject.parseObject(text, CoolStoreDataChangeDTO.class);
        log.info("CoolStoreDataChangeListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), JSONObject.toJSONString(resultDTO));
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                coolCollegeIntegrationApiService.coolStoreDataChange(resultDTO);
            } catch (Exception e) {
                log.error("get open coolcollege result error" + e);
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.CommitMessage;
    }


}









