package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.PersonSubTaskDataQueueDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubTaskForStoreData;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

/**
 * 按人子任务监听
 * @author zhangnan
 * @since 2022/4/15
 */
@Slf4j
@Service
public class PersonSubTaskDataQueueListener implements MessageListener {

    @Resource
    private UnifyTaskService unifyTaskService;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("PersonSubTaskDataQueueListener messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "PersonSubTaskDataQueueListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                this.personSubTaskDataQueue(text);
            }catch (Exception e){
                log.error("PersonSubTaskDataQueueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("PersonSubTaskDataQueueListener success,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    private void personSubTaskDataQueue(String text) {
        log.info("personSubTaskDataQueue, reqBody={}", text);
        PersonSubTaskDataQueueDTO data = JSONObject.parseObject(text, PersonSubTaskDataQueueDTO.class);
        if(StringUtils.isAnyBlank(data.getEnterpriseId(), data.getDbName())) {
            log.error("personSubTaskDataQueue enterprise info null");
            return;
        }
        if(Objects.isNull(data.getTaskParent()) || Objects.isNull(data.getTaskParent().getId())) {
            log.error("personSubTaskDataQueue data null");
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(data.getDbName());
        unifyTaskService.buildSubTaskByPerson(data.getEnterpriseId(), data.getUserId(), data.getTaskParent());
    }

}
