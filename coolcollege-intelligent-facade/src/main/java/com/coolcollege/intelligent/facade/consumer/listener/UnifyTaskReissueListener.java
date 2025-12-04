package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 任务补发
 *
 * @author byd
 * @since 2023/2/21
 */
@Slf4j
@Service
public class UnifyTaskReissueListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 消息唯一标识key
     */
    private static final String MESSAGE_PRIMARY_KEY = "primary_key";

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "UnifyTaskReissueListener:" + message.getMsgID();
        boolean lock = redis.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                taskReissue(text);
            }catch (Exception e){
                log.error("UnifyTaskReissueListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redis.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void taskReissue(String json) {
        if (StringUtils.isNotBlank(json)) {
            log.info("taskReissue监听的text消息:####" + json);
            UnifyTaskBuildDTO unifyTaskBuildDTO = JSONObject.parseObject(json, UnifyTaskBuildDTO.class);
            if (Objects.isNull(unifyTaskBuildDTO)) {
                return;
            }
            String enterpriseId = unifyTaskBuildDTO.getEnterpriseId();
            Long taskId = unifyTaskBuildDTO.getTaskId();
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(unifyTaskBuildDTO.getEnterpriseId());
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskBuildDTO.getTaskId());
            if(taskParentDO == null){
                log.info("taskReissue#任务不存eid:{},taskId:{}", enterpriseId, unifyTaskBuildDTO.getTaskId());
                return;
            }
            unifyTaskService.taskParentResolve(enterpriseId, taskId, enterpriseConfigDO.getDbName(), true, false);
        }
    }
}
