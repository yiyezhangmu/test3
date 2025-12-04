package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyStoreTaskResolveDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubTaskForStoreData;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

/**
 * 门店子任务监听
 *
 * @author chenyupeng
 * @since 2022/3/1
 */
@Slf4j
@Service
public class StoreSubTaskDataQueueListener implements MessageListener {

    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private TaskResolveService taskResolveService;

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

        String lockKey = "StoreSubTaskDataQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                switch (RocketMqTagEnum.getByTag(message.getTag())){
                    case STORE_SUB_TASK_DATA_QUEUE:
                        storeSubTaskDataQueue(text);
                        break;
                    case STORE_TASK_RESOLVE_DATA_QUEUE:
                        UnifyStoreTaskResolveDTO taskResolve = JSONObject.parseObject(text, UnifyStoreTaskResolveDTO.class);
                        if(Objects.isNull(taskResolve)  || StringUtils.isBlank(taskResolve.getEnterpriseId()) || Objects.isNull(taskResolve.getTaskStore())){
                            log.info("参数错误,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
                            return Action.CommitMessage;
                        }
                        taskResolveService.taskResolve(taskResolve.getEnterpriseId(), taskResolve.getTaskStore(), taskResolve.isRefresh());
                        break;
                    default:
                        log.info("暂无消费者");
                }

            }catch (Exception e){
                log.error("StoreSubTaskDataQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void storeSubTaskDataQueue(String text) {
        log.info("storeSubTaskDataQueue, reqBody={}", text);
        UnifySubTaskForStoreData subTaskForStoreData = JSONObject.parseObject(text, UnifySubTaskForStoreData.class);
        log.info("门店任务分解  {}", subTaskForStoreData.getStoreId());
        String enterpriseId = subTaskForStoreData.getEnterpriseId();
        String storeId = subTaskForStoreData.getStoreId();
        Set<String> userSet = subTaskForStoreData.getUserSet();
        Long parentTaskId = subTaskForStoreData.getTaskId();
        Long newLoopCount = subTaskForStoreData.getNewLoopCount();
        Long createTime = subTaskForStoreData.getCreateTime();
        Set<String> ccUserSet = subTaskForStoreData.getCcUserSet();
        unifyTaskService.buildSubTaskBySingleStore(enterpriseId, storeId, userSet, parentTaskId, newLoopCount , createTime, ccUserSet, subTaskForStoreData);
    }

}
