package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.*;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentUserMappingDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.patrolstore.impl.PatrolStoreServiceImpl;
import com.coolcollege.intelligent.service.question.QuestionHistoryService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.storework.StoreWorkDataTableColumnService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.UnifyTaskConstant.TaskMessage.*;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.QUESTION_ORDER;
import static com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant.TaskQuestionStatusConstant.*;

/**
 * 工单任务创建
 *12
 * @author chenyupeng
 * @since 2022/3/3
 */
@Slf4j
@Service
public class UnifyTaskQuestionListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private PatrolStoreServiceImpl patrolStoreService;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private QuestionHistoryService questionHistoryService;
    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private QuestionParentUserMappingDao questionParentUserMappingDao;
    @Resource
    private QuestionParentUserMappingService questionParentUserMappingService;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private TaskStoreDao taskStoreDao;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;
    @Resource
    StoreWorkDataTableColumnService storeWorkDataTableColumnService;
    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;
    @Resource
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    RegionMapper regionMapper;

    @Resource
    UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    SysRoleMapper sysRoleMapper;

    @Resource
    EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    EnterpriseUserDao enterpriseUserDao;
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

        String lockKey = "UnifyTaskQuestionListener:" + message.getMsgID();
        boolean lock = redis.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                receiveTopic(text);
            }catch (Exception e){
                log.error("UnifyTaskQuestionListener consume error",e);
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
                HashMap<String, String> paramMap = new HashMap<>();
                String operate = taskMessageDTO.getOperate();
                switch (operate) {
                    case UnifyTaskConstant.TaskMessage.OPERATE_ADD:
                        // 添加工单信息
                        addTaskQuestion(taskMessageDTO);
                        this.sendMqMessage(taskMessageDTO, operate);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_TURN:
                        // 转交
                        turnQuestionTask(taskMessageDTO, paramMap);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE:
                        // 重新分配
                        reallocateQuestionTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_PASS:
                    case UnifyTaskConstant.TaskMessage.OPERATE_REJECT:
                        // 拒绝
                        // 通过
                        updateTaskQuestion(taskMessageDTO, false);
                        this.sendMqMessage(taskMessageDTO, operate);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_DELETE:
                        // 删除
                        delPatrolStoreTask(taskMessageDTO);
                        break;
                    case UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE:
                        // 完成
                        updateTaskQuestion(taskMessageDTO, true);
                        this.sendMqMessage(taskMessageDTO, operate);
                        break;
                    default:
                        throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                                "任务中台操作类型有误：operate=" + operate);
                }
                jmsTaskService.sendQuestionMessage(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount(), operate, paramMap);
            }
        } catch (Exception e) {
            log.error("UnifyTaskQuestionListener#任务中台信息处理异常", e);
        } finally {
            DataSourceHelper.reset();
        }
    }
    private void sendMqMessage(TaskMessageDTO taskMessageDTO,String operate){
        try {
            String enterpriseId = taskMessageDTO.getEnterpriseId();
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, EnterpriseStatusEnum.NORMAL.getCode());
            if (Objects.isNull(enterpriseMqInformConfigDTO)){
                log.info("企业未配置消息通知");
                return;
            }
            TbQuestionRecordDO recordDO = tbQuestionRecordMapper.selectByTaskIdAndStoreId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(),taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount());
            String questionType = recordDO.getQuestionType();
            if(!Constants.SUB_WORK_ORDER_DETAIL_EIDS.contains(enterpriseId)){
                if (!QuestionTypeEnum.PATROL_STORE.getCode().equals(questionType)){
                    log.info("巡店工单才发送消息");
                    return;
                }
                if (UnifyTaskConstant.TaskMessage.OPERATE_PASS.equals(operate) && Integer.valueOf(taskMessageDTO.getNodeNo())<3){
                    log.info("审批节点才发消息");
                    return;
                }
            }
            switch (operate) {
                case UnifyTaskConstant.TaskMessage.OPERATE_ADD:
                    log.info("sendMqMessage switch ADD");
                    // 添加工单信息
                    if(Constants.SUB_WORK_ORDER_DETAIL_EIDS.contains(enterpriseId)){
                        Map map=getMsgData(taskMessageDTO);
                        log.info("OPERATE_ADD map :{}",JSONObject.toJSONString(map));
                        send(enterpriseId, BailiInformNodeEnum.SUB_WORK_ORDER_APPROVAL_NODE_FLOWS_SUBTASKS.getCode(),map);
                    }
                    break;
                case UnifyTaskConstant.TaskMessage.OPERATE_PASS:
                    // 拒绝
                case UnifyTaskConstant.TaskMessage.OPERATE_REJECT:
                    // 通过
                    Map data=getMsgData(taskMessageDTO);
                    send(enterpriseId, BailiInformNodeEnum.SUB_WORK_ORDER_APPROVAL_NODE_FLOWS_SUBTASKS.getCode(),data);
                    break;
                case UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE:
                    log.info("sendMqMessage switch COMPLETE");
                    if(Constants.SUB_WORK_ORDER_DETAIL_EIDS.contains(enterpriseId)){
                        Map map=getMsgData(taskMessageDTO);
                        log.info("OPERATE_COMPLETE map :{}",JSONObject.toJSONString(map));
                        send(enterpriseId, BailiInformNodeEnum.SUB_WORK_ORDER_APPROVAL_NODE_FLOWS_SUBTASKS.getCode(),map);
                    }else {
                        HashMap<String, Object> map = Maps.newHashMap();
                        map.put("recordId", recordDO.getId());
                        send(enterpriseId, BailiInformNodeEnum.PATROL_SUB_WORK_ORDER_COMPLETED.getCode(),map);
                    }
                    // 完成
                    break;
                default:
                    log.info("消息类型不需要发送:{}",operate);
            }
        }catch (Exception e){
            log.error("发送mq消息失败:{}",operate,e);
            log.error("Exception details: ", e);
        }

    }
    private void send(String enterpriseId, String bizType, Map<String,Object> map){
        //mq发送消息
        JSONObject data = new JSONObject();
        data.put("enterpriseId", enterpriseId);
        //模块类型巡店
        data.put("moduleType", QUESTION_ORDER.getCode());
        //业务类型
        data.put("bizType",bizType);
        //时间戳
        data.put("timestamp", System.currentTimeMillis());
        //业务数据
        data.put("data",map);
        SendResult send = simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM, System.currentTimeMillis() + 2000);
        log.info("发送mq消息成功:{},{},{}",bizType,send,data.toJSONString());
    }
    private Map getMsgData(TaskMessageDTO taskMessageDTO) {
        log.info("getMsgData taskMessageDTO:{}",JSONObject.toJSONString(taskMessageDTO));
        Map<String, Object> map = Maps.newHashMap();
        log.info("second selectByTaskIdAndStoreId");
        TbQuestionRecordDO recordDO = tbQuestionRecordMapper.selectByTaskIdAndStoreId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(),taskMessageDTO.getStoreId(), taskMessageDTO.getLoopCount());
        log.info("getMsgData recordDO:{}",JSONObject.toJSONString(recordDO));
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        log.info("getMsgData taskSubDOList:{}",JSONObject.toJSONString(taskSubDOList));
        TaskSubDO taskSubDO = taskSubDOList.get(0);
        JSONObject handelData = new JSONObject();
        if (Objects.nonNull(taskMessageDTO.getTaskHandleData())){
            handelData = JSONObject.parseObject(taskMessageDTO.getTaskHandleData());
        }

        map.put("nodeNo", taskMessageDTO.getNodeNo());
        map.put("taskId", taskMessageDTO.getUnifyTaskId());
        map.putIfAbsent("action",Objects.isNull(handelData.get("handle_action")) ? null : handelData.get("handle_action"));
        map.put("handleUserId",taskSubDO.getHandleUserId());
        map.put("createUserId", taskMessageDTO.getCreateUserId());
        map.put("storeId",taskMessageDTO.getStoreId());
        map.put("recordId",recordDO.getId());
        map.put("storeName",recordDO.getStoreName());
        map.put("columnId",recordDO.getMetaColumnId());
        map.put("remark",recordDO.getHandleRemark());
        map.put("createTime", DateUtil.format(recordDO.getCreateTime(),"yyyy-MM-dd HH:mm"));
        TbMetaStaTableColumnDO tbMetaStaTableColumnDO = tbMetaStaTableColumnMapper.selectByPrimaryKey(taskMessageDTO.getEnterpriseId(), recordDO.getMetaColumnId());
        String taskInfo = taskMessageDTO.getTaskInfo();
        JSONObject taskInfoObj = JSONObject.parseObject(taskInfo);
        if (taskInfoObj.containsKey("questionTypeName")){
            map.putIfAbsent("questionTypeName",taskInfoObj.getString("questionTypeName"));
        }
        if (taskInfoObj.containsKey("dedPoints")){
            map.putIfAbsent("dedPoints",taskInfoObj.getString("dedPoints"));
        }else {
            if (StringUtils.isNotBlank(taskInfo)){
                QuestionTaskInfoDTO questionTaskInfoDTO = JSONObject.parseObject(taskInfo, QuestionTaskInfoDTO.class);
                if (Objects.nonNull(questionTaskInfoDTO) && Objects.nonNull(questionTaskInfoDTO.getDataColumnId())){
                    TbDataStaTableColumnDO tbDataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(taskMessageDTO.getEnterpriseId(),recordDO.getDataColumnId());
                    if (Objects.nonNull(tbDataStaTableColumnDO)){
                        map.putIfAbsent("dedPoints",tbDataStaTableColumnDO.getCheckScore());
                    }
                }
            }
        }

        if (tbMetaStaTableColumnDO != null) {
            map.put("columnName",tbMetaStaTableColumnDO.getColumnName());
            map.put("categoryName",tbMetaStaTableColumnDO.getCategoryName());
            map.put("metaTableId",tbMetaStaTableColumnDO.getMetaTableId());
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(taskMessageDTO.getEnterpriseId(), tbMetaStaTableColumnDO.getMetaTableId());
            if (tbMetaTableDO != null) {
                map.put("tableName",tbMetaTableDO.getTableName());
            }
        }
        if (OPERATE_REJECT.equals(taskMessageDTO.getOperate())) {
            map.putIfAbsent("remark",Objects.isNull(handelData.get("remark")) ? null : handelData.get("remark"));
            if (Objects.nonNull(handelData) && Objects.nonNull(handelData.getJSONObject("task_data"))){
                JSONArray jsonArray = handelData.getJSONObject("task_data").getJSONArray("photos");
                map.put("pic",jsonArray.toJSONString());
            }
        }

        RegionDO byStoreId = regionMapper.getByStoreId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getStoreId());
        if (Objects.nonNull(byStoreId)){
            List<UserAuthMappingDO> userAuthByMappingIds = userAuthMappingMapper.getUserAuthByMappingIds(taskMessageDTO.getEnterpriseId(), Collections.singletonList(byStoreId.getRegionId()));
            if (CollectionUtils.isNotEmpty(userAuthByMappingIds)){
                List<String> userIds = userAuthByMappingIds.stream().map(UserAuthMappingDO::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userIds)){
                    List<SysRoleDO> storeManager = sysRoleMapper.selectByRoleNameAndSource(taskMessageDTO.getEnterpriseId(), "店长", PositionSourceEnum.SYNC_POSITION.getValue());
                    if (CollectionUtils.isNotEmpty(storeManager)){
                        List<String> storeManagerRoleIds = storeManager.stream().map(SysRoleDO::getId).map(String::valueOf).collect(Collectors.toList());
                        List<String> userIdsByRoleIds = enterpriseUserRoleMapper.getUserIdsByRoleIds(taskMessageDTO.getEnterpriseId(), storeManagerRoleIds, userIds);
                        if (CollectionUtils.isNotEmpty(userIdsByRoleIds)){
                            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(taskMessageDTO.getEnterpriseId(), userIdsByRoleIds.get(0));
                            if (Objects.nonNull(enterpriseUserDO)){
                                map.putIfAbsent("storeManagerName",enterpriseUserDO.getName());
                                map.putIfAbsent("storeManagerUserId",enterpriseUserDO.getUserId());
                            }else {
                                map.put("storeManagerName"," ");
                                map.put("storeManagerUserId"," ");
                            }
                        }
                    }
                }
            }
        }

        return map;
    }

    /**
     * 新建问题工单
     */
    private void addTaskQuestion(TaskMessageDTO taskMessageDTO) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        // dataColumnId
        String taskInfo = taskMessageDTO.getTaskInfo();
        QuestionTaskInfoDTO questionTaskInfoDTO = JSON.parseObject(taskInfo, QuestionTaskInfoDTO.class);
        Long dataColumnId = questionTaskInfoDTO.getDataColumnId();
        Long metaColumnId = questionTaskInfoDTO.getMetaColumnId();
        Boolean contentLearnFirst = questionTaskInfoDTO.getContentLearnFirst();
        log.info("store_work_question_task taskMessageDTO:{}", JSONObject.toJSONString(taskMessageDTO));
        // 添加问题工单信息 店务工单单独处理
        if (QuestionTypeEnum.STORE_WORK.getCode().equals(taskMessageDTO.getQuestionType())){
            log.info("store_work_question_start unifyTaskId:{}",unifyTaskId);
            //店务更新店务处理作业项表
            swStoreWorkDataTableColumnDao.updateByPrimaryKeySelective(SwStoreWorkDataTableColumnDO.builder().taskQuestionId(unifyTaskId)
                    .taskQuestionStatus(HANDLE).id(dataColumnId).build(),enterpriseId);
            storeWorkDataTableColumnService.updateQuestionData(enterpriseId,dataColumnId);
        }else {
            tbDataStaTableColumnMapper.updateTaskQuestionId(enterpriseId,
                    TbDataStaTableColumnDO.builder().taskQuestionId(unifyTaskId).taskQuestionStatus(HANDLE).id(dataColumnId)
                            .build());
        }

        if (!QuestionTypeEnum.STORE_WORK.getCode().equals(taskMessageDTO.getQuestionType())) {
            TbDataStaTableColumnDO tbDataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, dataColumnId);
            dataColumnId = dataColumnId == null ? 0 : dataColumnId;
            if(metaColumnId == null && dataColumnId > 0 && tbDataStaTableColumnDO != null){
                metaColumnId = tbDataStaTableColumnDO.getMetaColumnId();
            }
        }
        metaColumnId = metaColumnId == null ? 0 : metaColumnId;
        contentLearnFirst = contentLearnFirst != null && contentLearnFirst;
        //添加工单记录
        questionRecordService.addQuestionRecord(taskMessageDTO, dataColumnId, metaColumnId, contentLearnFirst);
    }



    /**
     * 更新问题工单
     */
    private void updateTaskQuestion(TaskMessageDTO taskMessageDTO, boolean isComplete) {
        // taskQuestionStatus
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        TaskSubDO taskSubDO = taskSubDOList.get(0);
        String taskQuestionStatus = "";
        String nodeNo = taskSubDO.getNodeNo();
        String storeId = taskSubDO.getStoreId();
        Long unifyTaskId = taskSubDO.getUnifyTaskId();
        Long loopCount = taskSubDO.getLoopCount();
        TbQuestionRecordDO questionRecordDO = questionRecordDao.selectByTaskIdAndStoreId(taskMessageDTO.getEnterpriseId(), unifyTaskId, storeId, loopCount);
        Long dataColumnId = null;
        if(questionRecordDO.getDataColumnId() != null && questionRecordDO.getDataColumnId() > 0){
            dataColumnId = questionRecordDO.getDataColumnId();
        }
        switch (nodeNo) {
            case "1":
                taskQuestionStatus = HANDLE;
                break;
            case "2":
            case "3":
            case "4":
                taskQuestionStatus = RECHECK;
                break;
            case "endNode":
                taskQuestionStatus = FINISH;
                break;
            default:
        }
        if (QuestionTypeEnum.STORE_WORK.getCode().equals(questionRecordDO.getQuestionType())){
            //店务更新店务处理作业项表
            swStoreWorkDataTableColumnDao.updateByPrimaryKeySelective(SwStoreWorkDataTableColumnDO.builder().taskQuestionId(unifyTaskId)
                    .taskQuestionStatus(taskQuestionStatus).id(dataColumnId).build(),taskMessageDTO.getEnterpriseId());
            storeWorkDataTableColumnService.updateQuestionData(taskMessageDTO.getEnterpriseId(),dataColumnId);
        }else {
            if(dataColumnId != null && dataColumnId > 0){
                tbDataStaTableColumnMapper.updateTaskQuestionStatus(taskMessageDTO.getEnterpriseId(), TbDataStaTableColumnDO
                        .builder().taskQuestionId(taskMessageDTO.getUnifyTaskId()).taskQuestionStatus(taskQuestionStatus).id(dataColumnId).build());
            }
        }
        //完成工单记录状态修改
        questionRecordService.updateQuestionTaskRecord(taskMessageDTO, isComplete);
        TaskStoreDO taskStoreDO = taskStoreDao.selectById(taskMessageDTO.getEnterpriseId(), questionRecordDO.getTaskStoreId());
        //工单人员待办更新
        questionParentUserMappingService.updateByTaskStore(taskMessageDTO.getEnterpriseId(), taskStoreDO, false);
    }

    /**
     * 删除工单
     */
    private void delPatrolStoreTask(TaskMessageDTO taskMessageDTO) {
        Long dataColumnId = null;
        TbQuestionRecordDO questionRecordDO = null;
        if(taskMessageDTO.getQuestionRecordId() != null){
             questionRecordDO = questionRecordDao.selectById(taskMessageDTO.getQuestionRecordId(), taskMessageDTO.getEnterpriseId());
            if(questionRecordDO.getDataColumnId() != null && questionRecordDO.getDataColumnId() > 0){
                dataColumnId = questionRecordDO.getDataColumnId();
            }
        }
        String questionType = questionRecordDao.getQuestionTypeByUnifyTaskId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId());
        if (QuestionTypeEnum.STORE_WORK.getCode().equals(questionType)){
            //店务工单删除
            swStoreWorkDataTableColumnDao.delStoreWorkQuestion(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), dataColumnId);
            //数据统计
            storeWorkDataTableColumnService.updateQuestionData(taskMessageDTO.getEnterpriseId(),dataColumnId);
        }else {
            //其他工单删除
            tbDataStaTableColumnMapper.delTaskQuestion(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId(), dataColumnId);
        }

        if(taskMessageDTO.getQuestionRecordId() != null){
            //删除单条工单记录
            questionRecordDao.deleteById(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getQuestionRecordId());
            TbQuestionParentInfoDO questionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId());
            //计算父工单子工单数量
            UnifySubStatisticsDTO unifySubStatisticsDTO = questionRecordDao.selectQuestionTaskCount(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId());
            questionParentInfoDO.setFinishNum(unifySubStatisticsDTO.getComplete());
            questionParentInfoDO.setTotalNum(unifySubStatisticsDTO.getAll());
            if(unifySubStatisticsDTO.getAll().equals(unifySubStatisticsDTO.getComplete())){
                questionParentInfoDO.setStatus(1);
            }
            questionParentInfoDao.updateByPrimaryKeySelective(taskMessageDTO.getEnterpriseId(), questionParentInfoDO);
            if(questionRecordDO != null){
                unifyTaskParentItemDao.deleteByUnifyTaskIdAndStoreIdAndLoopCount(taskMessageDTO.getEnterpriseId(), questionRecordDO.getUnifyTaskId(),
                        questionRecordDO.getStoreId(), questionRecordDO.getLoopCount());
            }
            if(StringUtils.isNotBlank(taskMessageDTO.getData())){
                TaskStoreDO taskStoreDO = JSONObject.parseObject(taskMessageDTO.getData(), TaskStoreDO.class);
                questionParentUserMappingService.updateByTaskStore(taskMessageDTO.getEnterpriseId(), taskStoreDO, false);
            }
        }else {
            questionRecordDao.deleteByUnifyTaskId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId());
            questionParentUserMappingDao.deleteByUnifyTaskId(taskMessageDTO.getEnterpriseId(), taskMessageDTO.getUnifyTaskId());
        }

    }

    /**
     * 解析json数据
     */
    private TaskMessageDTO dealTaskJson(String json) {
        JSONObject taskJsonObj = JSON.parseObject(json);
        // 非问题工单任务直接返回
        String taskType = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.TASK_TYPE_KEY);
        if (!QUESTION_ORDER.getCode().equals(taskType)) {
            return null;
        }
        // 分布式锁
        String primaryKey = taskJsonObj.getString(MESSAGE_PRIMARY_KEY);
        Assert.notNull(primaryKey, "primary_key is notExist");
        log.info("分布式锁key，taskType={}，primaryKey={}",taskType,primaryKey);
        if (!checkMessageReceive(taskType, primaryKey)) {
            log.info("不在本实例处理，丢弃任务中台监听信息primary_key：" + taskType + primaryKey);
            return null;
        }
        String enterpriseId = taskJsonObj.getString(UnifyTaskConstant.TaskMessage.ENTERPRISE_ID_KEY);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        // 切数据源
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return TaskMessageDTO.builder()
                .operate(taskJsonObj.getString(OPERATE_KEY))
                .unifyTaskId(taskJsonObj.getLong(UNIFY_TASK_ID_KEY))
                .questionType(taskJsonObj.getString(QUESTION_TYPE))
                .enterpriseId(enterpriseId)
                .taskType(taskType)
                .loopCount(taskJsonObj.getLong(LOOP_COUNT))
                .taskParentItemId(taskJsonObj.getLong(TASK_PARENT_ITEM_ID))
                .questionRecordId(taskJsonObj.getLong(QUESTION_RECORD_ID))
                .createUserId(taskJsonObj.getString(CREATE_USER_ID_KEY))
                .createTime(taskJsonObj.getLong(CREATE_TIME_KEY))
                .taskHandleData(taskJsonObj.getString(TASK_HANDLE_DATA_KEY))
                .nodeNo(taskJsonObj.getString(NODE_NO_KEY))
                .storeId(taskJsonObj.getString(STORE_ID))
                .data(taskJsonObj.getString(DATA_KEY))
                .taskInfo(taskJsonObj.getString(TASK_INFO))
                .build();
    }

    private void turnQuestionTask(TaskMessageDTO taskMessageDTO, HashMap<String, String> paramMap) {
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
        questionHistoryService.turnQuestionTask(enterpriseId, oldTaskSubDO, newTaskSubDo);
        paramMap.put("fromUserId", oldTaskSubDO.getHandleUserId());
        paramMap.put("toUserId", newTaskSubDo.getHandleUserId());
        paramMap.put("content",newTaskSubDo.getContent());

    }

    // 重新分配
    private void reallocateQuestionTask(TaskMessageDTO taskMessageDTO) {



        String data = taskMessageDTO.getData();
        JSONObject dataMap = JSON.parseObject(data);
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        String operUserId = dataMap.getString("operUserId");
        TaskStoreDO taskStoreDO = JSON.parseObject(dataMap.getString("taskStore"), TaskStoreDO.class);
        if (taskStoreDO == null) {
            return;
        }

        String handlerUserListStr = dataMap.getString("handlerUserList");
        String approveUserListStr = dataMap.getString("approveUserList");
        String secondUserListStr = dataMap.getString("recheckUserList");
        String thirdApproveUserStr = dataMap.getString("thirdApproveUserList");
        if(StringUtils.isNotBlank(handlerUserListStr)){
            questionHistoryService.reallocateQuestionTask(enterpriseId, taskStoreDO, operUserId, JSONObject.parseArray(handlerUserListStr, String.class), "整改人");
        }
        if(StringUtils.isNotBlank(approveUserListStr)){
            questionHistoryService.reallocateQuestionTask(enterpriseId, taskStoreDO, operUserId, JSONObject.parseArray(approveUserListStr, String.class), "一级审批人");
        }
        if(StringUtils.isNotBlank(secondUserListStr)){
            questionHistoryService.reallocateQuestionTask(enterpriseId, taskStoreDO, operUserId, JSONObject.parseArray(secondUserListStr, String.class), "二级审批人");
        }
        if(StringUtils.isNotBlank(thirdApproveUserStr)){
            questionHistoryService.reallocateQuestionTask(enterpriseId, taskStoreDO, operUserId, JSONObject.parseArray(thirdApproveUserStr, String.class), "三级审批人");
        }
        //更新人员待办
        String changeUserIdListStr = dataMap.getString("changeUserIdList");
        List<String> changeUserIdList = new ArrayList<>();
        if(StringUtils.isNotBlank(changeUserIdListStr)){
            changeUserIdList =  JSONObject.parseArray(changeUserIdListStr, String.class);
        }
        questionParentUserMappingService.updateByTaskStore(enterpriseId, taskStoreDO, false);
        if(CollectionUtils.isNotEmpty(changeUserIdList)){
            questionParentUserMappingService.updateUserMapping(enterpriseId, taskStoreDO.getUnifyTaskId(), changeUserIdList, null);
        }
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
