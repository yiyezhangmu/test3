package com.coolcollege.intelligent.facade.consumer.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.util.RedisUtilPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * describe: 乐橙回调消息监听
 *
 * @author zhouyiping
 * @date 2022/04/26
 */
@Slf4j
@Service
public class ImouListener implements MessageListener {

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());
        log.info("ImouListener.msg={}",text);
        String lockKey = "ImouListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                //imouVideoServiceImpl.dealMsg(text);
            }catch (ServiceException e){
                log.error("ImouListener consume error ={}",e.getErrorMessage(),e);
                return Action.ReconsumeLater;
            } catch (Exception e){
                log.error("ImouListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
