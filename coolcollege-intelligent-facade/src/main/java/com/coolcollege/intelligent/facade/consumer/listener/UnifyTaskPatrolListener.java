package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreHistoryMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.*;

/**
 * 巡店任务创建
 *
 * @author chenyupeng
 * @since 2022/3/3
 */
@Slf4j
@Service
public class UnifyTaskPatrolListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private PatrolStoreService patrolStoreService;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskSubMapper taskSubMapper;

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

        String lockKey = "UnifyTaskPatrolListener:" + message.getMsgID();
        boolean lock = redis.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                receiveTopic(text);
            }catch (Exception e){
                log.error("UnifyTaskPatrolListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redis.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{},reqBody={}",message.getTag(),message.getMsgID(),text);
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void receiveTopic(String json) {
        try {
            if (StringUtils.isNotBlank(json)) {
                log.info("receiveTopic监听的text消息:####" + json);

                TaskMessageDTO taskMessageDTO = dealTaskJson(json);
                if (Objects.isNull(taskMessageDTO)) {
                    return;
                }
                log.info("taskMessageDTO :####" + JSON.toJSONString(taskMessageDTO));

                String operate = taskMessageDTO.getOperate();
                switch (operate) {
                    case UnifyTaskConstant.TaskMessage.OPERATE_ADD:
                        // 新增
                        addPatrolStoreTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_TURN:
                        // 转交
                        turnPatrolStoreTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE:
                        // 重新分配
                        reallocatePatrolTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_PASS:
                        // 通过
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_REJECT:
                        // 拒绝
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_DELETE:
                        // 删除
                        delPatrolStoreTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE:
                        // 完成
                        // 删除或签的其他巡店任务
                        patrolStoreService.completePatrolStoreTask(taskMessageDTO);
                        break;
                    default:
                        throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                                "任务中台操作类型有误：operate=" + operate);
                }
            }
        } catch (Exception e) {
            log.error("任务中台信息处理异常", e);
        } finally {
            DataSourceHelper.reset();
        }
    }

    /**
     * 新增巡店任务
     */
    private void addPatrolStoreTask(TaskMessageDTO taskMessageDTO) {
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        if (CollectionUtils.isEmpty(taskSubDOList)) {
            return;
        }

        //过滤相同taskId + storeId + loopCount 同一批次的任务
        taskSubDOList = taskSubDOList.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUnifyTaskId() + "#" + o.getStoreId() + "#" + o.getLoopCount()))
                ), ArrayList::new));

        taskSubDOList.forEach(taskSub -> patrolStoreService.addPatrolStoreTask(taskMessageDTO, taskSub));
    }

    /**
     * 转交巡店任务
     */
    private void turnPatrolStoreTask(TaskMessageDTO taskMessageDTO) {
        log.info("转交巡店任务123：{}",JSONObject.toJSONString(taskMessageDTO));
        String data = taskMessageDTO.getData();
        JSONObject dataMap = JSON.parseObject(data);
        Long oldSubTaskId = dataMap.getLong("oldSubTaskId");
        List<TaskSubDO> newSubDOList = JSON.parseArray(dataMap.getString("newSubDOList"), TaskSubDO.class);
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        if (oldSubTaskId == null || CollectionUtils.isEmpty(newSubDOList)) {
            return;
        }
        TaskSubDO oldSubDO = taskSubMapper.getTaskSubDOById(enterpriseId,oldSubTaskId);
        if(oldSubDO != null){
            TaskSubDO subDO = newSubDOList.get(0);
            String oldHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, oldSubDO.getHandleUserId());
            String newHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, subDO.getHandleUserId());
            TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId,subDO.getUnifyTaskId(),subDO.getStoreId(),subDO.getLoopCount(),null,null);
            if (recordDO != null){
                TbPatrolStoreHistoryDo tbPatrolStoreHistoryDo = new TbPatrolStoreHistoryDo();
                tbPatrolStoreHistoryDo.setBusinessId(recordDO.getId());
                tbPatrolStoreHistoryDo.setCreateTime(new Date(System.currentTimeMillis()));
                tbPatrolStoreHistoryDo.setUpdateTime(new Date(System.currentTimeMillis()));
                tbPatrolStoreHistoryDo.setNodeNo(subDO.getNodeNo());
                tbPatrolStoreHistoryDo.setActionKey("");
                tbPatrolStoreHistoryDo.setDeleted(false);
                tbPatrolStoreHistoryDo.setOperateType("turn");
                tbPatrolStoreHistoryDo.setOperateUserId(subDO.getCreateUserId());
                tbPatrolStoreHistoryDo.setSubTaskId(subDO.getId());
                tbPatrolStoreHistoryDo.setOperateUserName(oldHandleUserName);
                if (StringUtils.isNotBlank(subDO.getContent())){
                    tbPatrolStoreHistoryDo.setRemark(oldHandleUserName+"转交给"+newHandleUserName+"/n"+subDO.getContent());
                }else {
                    tbPatrolStoreHistoryDo.setRemark(oldHandleUserName+"转交给"+newHandleUserName);
                }
                tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId,tbPatrolStoreHistoryDo);
            }
        }
    }

    /**
     * 删除巡店任务
     */
    private void delPatrolStoreTask(TaskMessageDTO taskMessageDTO) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        String data = taskMessageDTO.getData();
        Long taskId = taskMessageDTO.getUnifyTaskId();
        List<Long> subTaskIds = JSON.parseArray(data, Long.class);
        if (CollectionUtils.isEmpty(subTaskIds)) {
            return;
        }
        if(taskId == null){
            return;
        }
        // 根据子任务ids获取巡店记录
        List<Long> businessIds = tbPatrolStoreRecordMapper.selectIdsByTaskId(enterpriseId, taskId);
        if (CollectionUtils.isEmpty(businessIds)) {
            return;
        }
        Lists.partition(businessIds, Constants.BATCH_INSERT_COUNT).forEach(businessIdList -> {
            patrolStoreService.delPatrolStoreByBusinessIds(enterpriseId, businessIdList);
        });
    }

    /**
     * 解析json数据
     */
    private TaskMessageDTO dealTaskJson(String json) {
        JSONObject taskJsonObj = JSON.parseObject(json);
        // 非巡店任务直接返回
        String taskType = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_TYPE_KEY);
        if (!PATROL_STORE_ONLINE.getCode().equals(taskType)
                && !PATROL_STORE_OFFLINE.getCode().equals(taskType)
                && !PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskType)
                && !PATROL_STORE_PLAN.getCode().equals(taskType)
                && !PATROL_STORE_AI.getCode().equals(taskType)
                && !PRODUCT_FEEDBACK.getCode().equals(taskType)
                && !PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(taskType)) {
            return null;
        }
        // 分布式锁
        String primaryKey = taskJsonObj.getString(MESSAGE_PRIMARY_KEY);
        Assert.notNull(primaryKey, "primary_key is notExist");
        if (!checkMessageReceive(taskType, primaryKey)) {
            log.info("不在本实例处理，丢弃任务中台监听信息primary_key：" + taskType + primaryKey);
            return null;
        }
        String enterpriseId = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.ENTERPRISE_ID_KEY);
        // 切数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return TaskMessageDTO.builder().operate(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.OPERATE_KEY))
                .unifyTaskId(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.UNIFY_TASK_ID_KEY))
                .enterpriseId(enterpriseId).taskType(taskType)
                .createUserId(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.CREATE_USER_ID_KEY))
                .createTime(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.CREATE_TIME_KEY))
                .data(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.DATA_KEY))
                .storeCheckSetting(enterpriseStoreCheckSettingDO)
                .build();
    }

    /**
     * 查看消息是否消费过，让锁自然失效（100S），不手动解锁
     *
     * @param code
     *            code
     * @param primaryKey
     *            primaryKey
     * @return boolean
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

    public void reallocatePatrolTask(TaskMessageDTO taskMessageDTO) {
        String data = taskMessageDTO.getData();
        JSONObject dataMap = JSON.parseObject(data);
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        String operUserId = dataMap.getString("operUserId");
        TaskStoreDO taskStoreDO = JSON.parseObject(dataMap.getString("taskStore"), TaskStoreDO.class);
        if (taskStoreDO == null) {
            return;
        }
        patrolStoreService.reallocatePatrolTask(enterpriseId, taskStoreDO, operUserId);
    }
}
