package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.BailiInformNodeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.TB_DISPLAY_TASK;

/**
 * 陈列任务创建
 *
 * @author chenyupeng
 * @since 2022/3/3
 */
@Slf4j
@Service
public class UnifyTaskDisplayListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;
    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;

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

        String lockKey = "UnifyTaskDisplayListener:" + message.getMsgID();
        boolean lock = redis.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                receiveTopic(text);
            }catch (Exception e){
                log.error("UnifyTaskDisplayListener consume error",e);
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
                log.info("新陈列任务创建消费消息接收:####" + json);

                TaskMessageDTO taskMessageDTO = dealTaskJson(json);
                if (Objects.isNull(taskMessageDTO)) {
                    return;
                }
                log.info("新陈列任务创建消息消费 taskMessageDTO :####" + JSON.toJSONString(taskMessageDTO));



                String operate = taskMessageDTO.getOperate();
                switch (operate) {
                    case UnifyTaskConstant.TaskMessage.OPERATE_ADD:
                        // 新增
                        addTbDisplayTableRecord(taskMessageDTO);
                        sendMsg(taskMessageDTO,operate);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_TURN:
                        // 转交
                        turnTbDisplayTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE:
                        // 重新分配
                        reallocateTbDisplayTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_PASS:
                        // 通过
                        sendMsg(taskMessageDTO,operate);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_REJECT:
                        // 拒绝
                        sendMsg(taskMessageDTO,operate);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_DELETE:
                        // 删除
                        // delPatrolStoreTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE:
                        // 更新陈列记录完成
                        tbDisplayTableRecordService.completeTbDisplayTask(taskMessageDTO);
                        sendMsg(taskMessageDTO,operate);
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

    private void send(String enterpriseId, String bizType, Map<String,Object> map){
        //mq发送签到消息
        JSONObject data = new JSONObject();
        data.put("enterpriseId", enterpriseId);
        //模块类型巡店
        data.put("moduleType", TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        //业务类型
        data.put("bizType",bizType);
        //时间戳
        data.put("timestamp", System.currentTimeMillis());
        //业务数据
        data.put("data",map);
        log.info("mq消息参数:{}",data.toJSONString());
        SendResult send = simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM, System.currentTimeMillis() + 2000);
        log.info("发送mq消息成功返回:{}",send);
    }
    private void sendMsg(TaskMessageDTO taskMessageDTO,String operate) {
        try {
            //检查是否开启mq配置
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(taskMessageDTO.getEnterpriseId(), 1);
            if (Objects.isNull(enterpriseMqInformConfigDTO)) {
                log.info("企业未开启MQ消息推送,enterpriseId:{}", taskMessageDTO.getEnterpriseId());
                return;
            }
            switch (operate) {
                case UnifyTaskConstant.TaskMessage.OPERATE_ADD:
                    // 新增
                    //发送陈列门店任务创建消息
                    Map addMsg = getAddMsg(taskMessageDTO);
                    send(taskMessageDTO.getEnterpriseId(), BailiInformNodeEnum.DISPLAY_TASK_RELEASE.getCode(),addMsg);
                    break;
                case UnifyTaskConstant.TaskMessage.OPERATE_PASS:
                case UnifyTaskConstant.TaskMessage.OPERATE_REJECT:
                    // 通过
                    // 拒绝
                    JSONObject taskHandleData = JSONObject.parseObject( taskMessageDTO.getTaskHandleData());
                    String action = taskHandleData.get("flow_action_key").toString();
//                    if (QuestionActionKeyEnum.PASS.getCode().equals(action) && Integer.valueOf(taskMessageDTO.getNodeNo())<3){
//                        log.info("当前节点不需要发送消息");
//                        break;
//                    }
                    Map updateMsg = getUpdateMsg(taskMessageDTO);
                    send(taskMessageDTO.getEnterpriseId(), BailiInformNodeEnum.SUBTASK_OF_APPROVING_NODE_FLOW_IS_COMPLETED.getCode(),updateMsg);
                    break;
                case UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE:
                    // 更新陈列记录完成
                    Map completeMsg = getCompleteMsg(taskMessageDTO);
                    send(taskMessageDTO.getEnterpriseId(), BailiInformNodeEnum.DISPLAY_TASK_STATUS_COMPLETED.getCode(),completeMsg);
                    break;
                default:
                    log.error("任务中台操作类型有误：operate=" + operate);
            }
        }catch (Exception e){
            log.error("发送消息失败",e);
        }



    }

    private Map getCompleteMsg(TaskMessageDTO taskMessageDTO) {
        Map<String, Object> map = Maps.newHashMap();
        TbDisplayTableRecordDO recordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount());
        map.put("recordId",recordDO.getId());
        return map;
    }

    private Map getAddMsg(TaskMessageDTO taskMessageDTO){
        Map<String, Object> map = Maps.newHashMap();
        TbDisplayTableRecordDO recordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount());
        map.put("recordId",recordDO.getId());
        return map;
    }

    private Map getUpdateMsg(TaskMessageDTO taskMessageDTO){
        Map<String, Object> map = Maps.newHashMap();
        TbDisplayTableRecordDO recordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount());
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        TaskSubDO taskSubDO = taskSubDOList.get(0);
        JSONObject handelData = JSONObject.parseObject(taskMessageDTO.getTaskHandleData());
        map.put("recordId",recordDO.getId());
        map.put("nodeNo", taskMessageDTO.getNodeNo());
        map.put("taskId", taskMessageDTO.getUnifyTaskId());
        map.put("handleUserId", taskSubDO.getHandleUserId());
        map.put("action", handelData.get("flow_action_key"));
        map.put("storeId", taskMessageDTO.getStoreId());
        map.put("remark",handelData.get("remark"));

        return map;
    }

    /**
     * 新增陈列任务
     */
    private void addTbDisplayTableRecord(TaskMessageDTO taskMessageDTO) {
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        if (CollectionUtils.isEmpty(taskSubDOList)) {
            return;
        }
        tbDisplayTableRecordService.addTbDisplayTableRecord(taskMessageDTO, taskSubDOList);

    }

    /**
     * 解析json数据
     */
    private TaskMessageDTO dealTaskJson(String json) {
        JSONObject taskJsonObj = JSON.parseObject(json);
        // 非巡店任务直接返回
        String taskType = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_TYPE_KEY);
        if (!TB_DISPLAY_TASK.getCode().equals(taskType)) {
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
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return TaskMessageDTO.builder().operate(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.OPERATE_KEY))
                .unifyTaskId(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.UNIFY_TASK_ID_KEY))
                .enterpriseId(enterpriseId).taskType(taskType)
                .createUserId(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.CREATE_USER_ID_KEY))
                .createTime(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.CREATE_TIME_KEY))
                .data(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.DATA_KEY))
                .attachUrl(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.ATTACH_URL))
                .storeId(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.STORE_ID))
                .loopCount(taskJsonObj.getLong(UnifyTaskConstant.TaskMessage.LOOP_COUNT))
                .taskHandleData(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_HANDLE_DATA_KEY))
                .nodeNo(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.NODE_NO_KEY))
                .taskInfo(taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_INFO)).build();
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


    private void turnTbDisplayTask(TaskMessageDTO taskMessageDTO) {
        String data = taskMessageDTO.getData();
        JSONObject dataMap = JSON.parseObject(data);
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long oldSubTaskId = dataMap.getLong("oldSubTaskId");
        List<TaskSubDO> newSubDOList = JSON.parseArray(dataMap.getString("newSubDOList"), TaskSubDO.class);
        if (oldSubTaskId == null || CollectionUtils.isEmpty(newSubDOList)) {
            return;
        }
        TaskSubDO oldTaskSubDO = taskSubMapper.getSimpleTaskSubDOListById(enterpriseId, oldSubTaskId);
        TaskSubDO newTaskSubDo =  newSubDOList.get(0);
        tbDisplayTableRecordService.turnTbDisplayTask(enterpriseId, oldTaskSubDO, newTaskSubDo);

    }

    // 重新分配
    private void reallocateTbDisplayTask(TaskMessageDTO taskMessageDTO) {
        String data = taskMessageDTO.getData();
        JSONObject dataMap = JSON.parseObject(data);
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        String operUserId = dataMap.getString("operUserId");
        TaskStoreDO taskStoreDO = JSON.parseObject(dataMap.getString("taskStore"), TaskStoreDO.class);
        if (taskStoreDO == null) {
            return;
        }
        tbDisplayTableRecordService.reallocateTbDisplayTask(enterpriseId, taskStoreDO, operUserId);
    }
}
