package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkHandleCommentUpdateDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkResolveDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSendMessageDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkSingleStoreResolveDTO;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/27 14:33
 * @Version 1.0
 */
@Slf4j
@Service
public class StoreWorkTaskResolveListener implements MessageListener {
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private StoreWorkService storeWorkService;
    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            return Action.CommitMessage;
        }
        log.info("StoreWorkTaskResolveListener_text:{}",text);
        String lockKey = "StoreWorkTaskResolveListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case STOREWORK_TASK_RESOLVE:
                        StoreWorkResolveDTO storeWorkResolveDTO = JSONObject.parseObject(text, StoreWorkResolveDTO.class);
                        storeWorkService.resolve(storeWorkResolveDTO);
                        break;
                    case STOREWORK_TASK_SINGLE_STORE_RESOLVE:
                        StoreWorkSingleStoreResolveDTO storeWorkSingleStoreResolveDTO = JSONObject.parseObject(text, StoreWorkSingleStoreResolveDTO.class);
                        storeWorkService.storeWorkSingleStoreResolve(storeWorkSingleStoreResolveDTO);
                        break;
                    case STOREWORK_HANDLE_COMMENT_PERSON_UPDATE:
                        StoreWorkHandleCommentUpdateDTO handleCommentUpdateDTO = JSONObject.parseObject(text, StoreWorkHandleCommentUpdateDTO.class);
                        log.info("handleCommentUpdateDTO:{}",JSONObject.toJSONString(handleCommentUpdateDTO));
                        storeWorkService.storeWorkHandleCommentUpdate(handleCommentUpdateDTO);
                        break;
                }
            }catch (Exception e){
                log.error("StoreWorkMessageListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }
}
