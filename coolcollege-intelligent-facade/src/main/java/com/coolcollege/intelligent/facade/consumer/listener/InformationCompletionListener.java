package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.requestBody.store.StoreRequestBody;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 信息补全
 *
 * @author chenyupeng
 * @since 2022/3/3
 */
@Slf4j
@Service
public class InformationCompletionListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private RedisUtilPool redis;
    @Autowired
    private StoreService storeService;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskSubDao taskSubDao;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private SimpleMessageService simpleMessageService;


    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "InformationCompletionListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                receiveTopic(text);
            }catch (Exception e){
                log.error("InformationCompletionListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            simpleMessageService.send(text, RocketMqTagEnum.INFORMATION_COMPLETION_LICENSE);
            return Action.CommitMessage;
        }

        return Action.ReconsumeLater;
    }

    public void receiveTopic(String json) {
        try {
            if (StringUtils.isNotBlank(json)) {
                log.info("unify_task_send_topic StoreInfo监听的text消息:####" + json);
                JSONObject taskJsonObj = JSON.parseObject(json);
                TaskMessageDTO taskMessageDTO = dealTaskJson(taskJsonObj);
                if (Objects.isNull(taskMessageDTO)) {
                    return;
                }
                log.info("unify_task_send_topic StoreInfo监听的text消息taskMessageDTO :####" + JSON.toJSONString(taskMessageDTO));
                String operate = taskMessageDTO.getOperate();
                if(UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE.equals(operate)){
                    //任务完成更新门店数据
                    List<TaskSubDO> subDOList = JSON.parseArray(taskMessageDTO.getData(),TaskSubDO.class);
                    TaskSubDO subDO =subDOList.get(0);
                    StoreRequestBody requestBody = JSONObject.parseObject(subDO.getTaskData(), StoreRequestBody.class);
                    log.info("unify_task_send_topic StoreInfo监听的requestBody消息:####" + JSON.toJSONString(requestBody));
                    if (StringUtils.isEmpty(subDO.getStoreId())) {
                        throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
                    }
                    requestBody.setStore_id(subDO.getStoreId());
                    String enterpriseId = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.ENTERPRISE_ID_KEY);
                    // 查询处理人完成的任务节点
                    TaskSubDO handlerSubTask = taskSubDao.selectHandlerCompletedSubTask(enterpriseId, subDO.getStoreId(), TaskTypeEnum.PATROL_STORE_INFORMATION.getCode(), subDO.getUnifyTaskId());
                    log.info("unify_task_send_topic StoreInfo handler task:", JSONObject.toJSONString(handlerSubTask));
                    // 如果没有查到处理人完成的任务节点，说明当前节点（subDO）就是处理人的任务节点
                    String handlerUserId = Optional.ofNullable(handlerSubTask).map(TaskSubDO::getHandleUserId).orElse(subDO.getHandleUserId());
                    String updaterName = enterpriseUserDao.selectNameByUserId(enterpriseId, handlerUserId);
                    Boolean result = storeService.updateStore(enterpriseId, requestBody, Boolean.TRUE,updaterName);
                    if (!result) {
                        throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                                "更新门店数据失败");
                    }
                }
            }
        } catch (Exception e) {
            log.error("任务中台信息处理异常", e);
        } finally {
            DataSourceHelper.reset();
        }
    }

    /**
     * 解析json数据
     */
    private TaskMessageDTO dealTaskJson(JSONObject taskJsonObj) {
        // 非信息补全任务直接返回
        String taskType = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_TYPE_KEY);
        if (!TaskTypeEnum.PATROL_STORE_INFORMATION.getCode().equals(taskType)) {
            return null;
        }
        // 分布式锁
        String primaryKey = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.PRIMARY_KEY);
        ValidateUtil.validateString(primaryKey);
        if (!checkMessageReceive(taskType, primaryKey)) {
            log.info("不在本实例处理，丢弃任务中台监听信息primary_key：" + taskType + primaryKey);
            return null;
        }
        String enterpriseId = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.ENTERPRISE_ID_KEY);
        // 切数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return TaskMessageDTO.builder().operate(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.OPERATE_KEY))
                .unifyTaskId(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.UNIFY_TASK_ID_KEY))
                .enterpriseId(enterpriseId).taskType(taskType)
                .createUserId(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.CREATE_USER_ID_KEY))
                .createTime(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.CREATE_TIME_KEY))
                .data(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.DATA_KEY)).build();
    }

    /**
     * 查看消息是否消费过，让锁自然失效（100S），不手动解锁
     */
    public boolean checkMessageReceive(String code, String primaryKey) {
        String key = code + primaryKey;
        Long exists = redis.setStringIfNotExists(key, primaryKey);
        if (Objects.equals(exists, 1L)) {
            redis.expire(key, 100);
            return true;
        } else {
            return false;
        }
    }
}
