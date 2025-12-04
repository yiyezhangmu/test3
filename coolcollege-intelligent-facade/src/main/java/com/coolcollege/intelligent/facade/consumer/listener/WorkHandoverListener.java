package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.workHandover.dto.WorkHandoverDTO;
import com.coolcollege.intelligent.service.workHandover.WorkHandoverService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工作交接
 * @author byd
 * @date 2022-11-18 10:16
 */
@Slf4j
@Service
public class WorkHandoverListener  implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Autowired
    private WorkHandoverService workHandoverService;
    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        String lockKey = "WorkHandoverListener:" + message.getMsgID();
        WorkHandoverDTO resultDTO = JSONObject.parseObject(text, WorkHandoverDTO.class);
        log.info("WorkHandoverListener messageId：{}，try times：{}， receive data :{}", message.getMsgID(), message.getReconsumeTimes(), JSONObject.toJSONString(resultDTO));
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if (lock) {
            try {
                workHandoverService.beginWorkHandover(resultDTO.getEid(), resultDTO.getWorkHandoverId());
            } catch (Exception e) {
                log.error("WorkHandoverListener#error", e);
                return Action.ReconsumeLater;
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        }
        return Action.CommitMessage;
    }
}
