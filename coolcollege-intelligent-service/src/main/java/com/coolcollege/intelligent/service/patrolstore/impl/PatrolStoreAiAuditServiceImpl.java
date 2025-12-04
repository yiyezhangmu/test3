package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.ImageUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDetailDao;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolStoreAIResolveDTO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbDataStaColumnExtendInfoDO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreSubmitParam;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author wxp
 * @date 2025-07-15 19:21
 */
@Service
@Slf4j
public class PatrolStoreAiAuditServiceImpl{

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private MqMessageDAO mqMessageDAO;
    @Resource
    private TbDataStaColumnExtendInfoMapper tbDataStaColumnExtendInfoMapper;
    @Lazy
    @Resource
    private PatrolStoreServiceImpl patrolStoreService;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbPatrolStoreRecordInfoMapper tbPatrolStoreRecordInfoMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Lazy
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private AIService aiService;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Autowired
    private UnifyTaskPersonService unifyTaskPersonService;
    @Resource
    private TbPatrolPlanDetailDao tbPatrolPlanDetailDao;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private AiModelLibraryService aiModelLibraryService;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;

    /**
     * 异步回调处理某一项AI结果
     */
    // @Transactional(rollbackFor = Exception.class)
    public void handleAsyncAiResult(String enterpriseId, Long dataColumnId, AIResolveDTO aiResolve, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO) {
        log.info("handleAsyncAiResult异步处理检查项AI结果dataColumnId:{},aiResolve:{}", dataColumnId, JSONObject.toJSONString(aiResolve));
        TbDataStaTableColumnDO dataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, dataColumnId);
        TbDataStaColumnExtendInfoDO dataStaColumnExtendInfoDO = tbDataStaColumnExtendInfoMapper.selectById(enterpriseId, dataColumnId);
        if(Objects.isNull(dataStaTableColumnDO) || Objects.isNull(dataStaColumnExtendInfoDO)){
            try {
                log.info("handleAsyncAiResult数据检查项不存在sleep60秒dataColumnId:{},aiResolve:{}", dataColumnId, JSONObject.toJSONString(aiResolve));
                Thread.sleep(50000);
            } catch (Exception e) {
                log.error("handleAsyncAiResult,error",e);
            }
            dataStaTableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, dataColumnId);
            dataStaColumnExtendInfoDO = tbDataStaColumnExtendInfoMapper.selectById(enterpriseId, dataColumnId);
            if(Objects.isNull(dataStaTableColumnDO) || Objects.isNull(dataStaColumnExtendInfoDO)){
                log.info("handleAsyncAiResult数据检查项不存在dataColumnId:{},aiResolve:{}", dataColumnId, JSONObject.toJSONString(aiResolve));
                return;
            }
        }
        if(!Constants.STORE_WORK_AI.COLUMN_AI_STATUS_PROCESSING.equals(dataStaColumnExtendInfoDO.getAiStatus())){
            log.info("handleAsyncAiResult数据检查项已ai处理结束dataColumnId:{}", dataColumnId);
            return;
        }
        Long subTaskId = dataStaColumnExtendInfoDO.getSubTaskId();
        Long businessId = dataStaTableColumnDO.getBusinessId();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);

        if (Objects.nonNull(aiResolve)) {
            setAiField(dataStaTableColumnDO, dataStaColumnExtendInfoDO, aiResolve);
        }
        tbDataStaTableColumnMapper.batchUpdateResult(enterpriseId, Collections.singletonList(dataStaTableColumnDO));
        log.info("handleAsyncAiResult######batchInsertOrUpdateDataColumnExtendInfo######dataStaColumnExtendInfoDO:{}", JSONObject.toJSONString(dataStaColumnExtendInfoDO));
        tbDataStaColumnExtendInfoMapper.batchInsertOrUpdateDataColumnExtendInfo(enterpriseId, Collections.singletonList(dataStaColumnExtendInfoDO));

        TbMetaTableDO tbMetaTable = tbMetaTableMapper.selectById(enterpriseId, dataStaTableColumnDO.getMetaTableId());
        patrolStoreService.countScore(enterpriseId, tbPatrolStoreRecordDO, tbMetaTable, dataStaTableColumnDO.getDataTableId());
        // 计算巡店记录的分数
        PatrolStoreSubmitParam submitParam = new PatrolStoreSubmitParam();
        submitParam.setBusinessId(businessId);
        patrolStoreService.countPatrolStoreRecordScore(enterpriseId, submitParam, tbPatrolStoreRecordDO.getBusinessCheckType());
        // ai状态变更后处理后续逻辑
        handleAfterAiStatusChange(enterpriseId, subTaskId, businessId, tbPatrolStoreRecordDO, enterpriseStoreCheckSettingDO);
    }

    private void handleAfterAiStatusChange(String enterpriseId, Long subTaskId, Long businessId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO,
                                           EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO) {
        boolean checkHasProcessingColumn = tbDataStaColumnExtendInfoMapper.checkHasProcessingColumn(enterpriseId, businessId);
        if (checkHasProcessingColumn) {
            // 插入AI审批操作记录
            insertAiAuditHistory(enterpriseId, businessId, subTaskId);

            boolean approve = false;
            TaskParentDO taskParentDO = null;
            if (tbPatrolStoreRecordDO.getTaskId() != 0) {
                taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
                //流程信息处理
                List<TaskProcessDTO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
                // 节点配置信息组装
                Map<String, String> nodeMap = ListUtils.emptyIfNull(process).stream()
                        .filter(a -> a.getNodeNo() != null && a.getApproveType() != null)
                        .collect(Collectors.toMap(TaskProcessDTO::getNodeNo, TaskProcessDTO::getApproveType, (a, b) -> a));

                String approveUser = nodeMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
                // 判断是否有审核节点，没有审核通过流程直接结束
                if (StringUtils.isNotBlank(approveUser)) {
                    approve = Boolean.TRUE;
                }
            }

            List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
            List<Long> metaTableIdList = ListUtils.emptyIfNull(formDataList).stream()
                    .map(dto -> Long.parseLong(dto.getOriginMappingId()))
                    .collect(Collectors.toList());

            //查询检查表是否包非Ai项
            Integer count = tbMetaStaTableColumnMapper.unAiCheckColumnCountByMetaTableIdList(enterpriseId, metaTableIdList);

            boolean patrolSkipApproval = enterpriseStoreCheckSettingDO.getPatrolSkipApproval() != null && enterpriseStoreCheckSettingDO.getPatrolSkipApproval();

            List<TbDataStaTableColumnDO> allColumnList =  tbDataStaTableColumnMapper.selectByBusinessIdAndMetaTableIds(enterpriseId, businessId, metaTableIdList);
            boolean isAllPass = allColumnList.stream().allMatch(column -> CheckResultEnum.PASS.getCode().equals(column.getCheckResult()));
            // 需要人工审核
            if (!approve || patrolSkipApproval && count == 0 && isAllPass) {
                log.info("handleAfterAiStatusChange######不需要审批");
                patrolStoreService.completePotral(enterpriseId, businessId, Constants.AI, Constants.AI, tbPatrolStoreRecordDO.getSubTaskId());
                unifyTaskService.completeSubTask(enterpriseId, subTaskId, null, null);
                // 修改审批人信息
                tbDataTableMapper.updateAuditInfo(enterpriseId, businessId, PATROL_STORE, Constants.AI
                        , Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");

                tbPatrolStoreRecordInfoMapper.updateAuditInfo(enterpriseId, businessId, Constants.AI
                        , Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");
            }else {
                //发送消息处理
                TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
                if (taskSubDO == null) {
                    return;
                }
                WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, taskSubDO.getId(), taskSubDO,
                        TbDisplayConstant.BizCodeConstant.DISPLAY_HANDLE, TbDisplayConstant.ActionKeyConstant.PASS, null,
                        Constants.AI, null, null, null);
                mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), taskSubDO.getId(), JSONObject.toJSONString(workflowDataDTO));
                simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
            }
            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + tbPatrolStoreRecordDO.getTaskId() + "_" + tbPatrolStoreRecordDO.getStoreId() + "_" + tbPatrolStoreRecordDO.getLoopCount();
            redisUtilPool.delKey(taskStatusKey);
        } else {
            log.info("handleAfterAiStatusChange存在AI分析中的项，暂不插入AI审批操作记录,businessId:{}", businessId);
        }
    }


    private void setAiField(TbDataStaTableColumnDO columnDO,
                            TbDataStaColumnExtendInfoDO updateColumnExtendInfo,
                            AIResolveDTO aiResolveDTO) {
        // 成功返回
        TbMetaColumnResultDO matchResult = aiResolveDTO.getColumnResult();
        String comment = aiResolveDTO.getAiComment();
        String checkPics = StringUtils.isBlank(aiResolveDTO.getAiImageUrl()) ? null : aiResolveDTO.getAiImageUrl();
        // 设置原表数据
        columnDO.setCheckResult(matchResult.getMappingResult());
        columnDO.setCheckResultId(matchResult.getId());
        columnDO.setCheckResultName(matchResult.getResultName());
        columnDO.setCheckText(comment);
        columnDO.setCheckScore(aiResolveDTO.getAiScore());
        columnDO.setCheckPics(checkPics);

        // 设置扩展表字段
        updateColumnExtendInfo.setAiCheckResult(matchResult.getMappingResult());
        updateColumnExtendInfo.setAiCheckResultId(matchResult.getId());
        updateColumnExtendInfo.setAiCheckResultName(matchResult.getResultName());
        updateColumnExtendInfo.setAiCheckPics(checkPics); // 如果有返回图，设置
        updateColumnExtendInfo.setAiCheckText(comment);
        updateColumnExtendInfo.setAiCheckScore(aiResolveDTO.getAiScore());
        updateColumnExtendInfo.setAiStatus(Constants.STORE_WORK_AI.COLUMN_AI_STATUS_COMPLETE);
        updateColumnExtendInfo.setUpdateUserId(Constants.AI);
        updateColumnExtendInfo.setUpdateTime(new Date());
    }


    /**
     * 处理超时未回调AI的检查项记录
     */
    public void handleTimeoutAiPatrolCheck(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);

        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        int timeoutMinutes = 30;
        List<TbDataStaColumnExtendInfoDO> notCallBackAiColumnList = tbDataStaColumnExtendInfoMapper.selectTimeoutProcessingRecords(enterpriseId, timeoutMinutes);
        if (CollectionUtils.isEmpty(notCallBackAiColumnList)){
            log.info("handleTimeoutAiChecks 无超时未回调的记录");
            return;
        }
        log.info("handleTimeoutAiChecks 开始处理超时AI记录，数量: {}", notCallBackAiColumnList.size());
        // 1. 提取 ID 列表
        List<Long> idList = notCallBackAiColumnList.stream()
                .map(TbDataStaColumnExtendInfoDO::getId)
                .collect(Collectors.toList());
        // 2. 批量更新为失败状态
        tbDataStaColumnExtendInfoMapper.updateAiStatusByJob( enterpriseId, idList, "模型未回调，定时任务处理");
        // 3. 获取 businessId 列表 & 查询对应的记录
        Set<Long> businessIdSet = notCallBackAiColumnList.stream()
                .map(TbDataStaColumnExtendInfoDO::getBusinessId)
                .collect(Collectors.toSet());
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, new ArrayList<>(businessIdSet));
        // 4. 构建 businessId -> record 的映射
        Map<Long, TbPatrolStoreRecordDO> recordMap = ListUtils.emptyIfNull(recordList).stream()
                .collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, record -> record));
        // 5. 遍历 businessId，判断是否处理完毕，执行后续逻辑
        for (Long businessId : businessIdSet) {
            TbPatrolStoreRecordDO record = recordMap.get(businessId);
            if (record == null) {
                log.warn("handleTimeoutAiChecks 未找到巡店记录 businessId: {}", businessId);
                continue;
            }
            Long subTaskId = record.getSubTaskId();
            handleAfterAiStatusChange(enterpriseId, subTaskId, businessId, record, enterpriseStoreCheckSettingDO);
        }
        log.info("handleTimeoutAiChecks 定时处理完成");
    }


    @Async("generalThreadPool")
    public void processOfflineAiAudit(String enterpriseId, Long businessId, TbPatrolStoreRecordDO recordDO,
                                      String businessType, Long subTaskId, List<Long> needAiAuditStaTableIds,
                                      EnterpriseSettingDO enterpriseSettingDO, String userId, boolean approve,
                                      String userName, String dingCorpId, String appType, TaskParentDO taskParentDO) {

        log.info("processOfflineAiAudit异步处理AI审批,businessId：{}", businessId);
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        //记录任务状态，处理中
        String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + recordDO.getTaskId() + "_" + recordDO.getStoreId() + "_" + recordDO.getLoopCount();
        log.info("processOfflineAiAudit###AI审批中放缓存,businessId：{}, taskStatusKey：{}", businessId, taskStatusKey);
        redisUtilPool.setString(taskStatusKey, "1", 30*60);
        // 重新分析项
        tbDataStaColumnExtendInfoMapper.resetAiStatus(enterpriseId, businessId);
        // 需要AI审核  返回ai审批是否结束
        boolean aiAuditFinish = dealAiAudit(enterpriseId, businessId, recordDO, businessType, subTaskId, needAiAuditStaTableIds, enterpriseSettingDO, userId);

        //查询检查表是否包非Ai项
        Integer count = tbMetaStaTableColumnMapper.unAiCheckColumnCountByMetaTableIdList(enterpriseId, needAiAuditStaTableIds);

        boolean patrolSkipApproval = storeCheckSettingDO.getPatrolSkipApproval() != null && storeCheckSettingDO.getPatrolSkipApproval();

        List<TbDataStaTableColumnDO> allColumnList =  tbDataStaTableColumnMapper.selectByBusinessIdAndMetaTableIds(enterpriseId, businessId, needAiAuditStaTableIds);
        boolean isAllPass = allColumnList.stream().allMatch(column -> CheckResultEnum.PASS.getCode().equals(column.getCheckResult()));
        // 考虑ai审批完成，并且没有人工审批情况，需要 处理status = 1 的情况
        log.info("processOfflineAiAudit###处理status = 1 的情况,businessId：{}, approve：{}, patrolSkipApproval：{}, count：{}, isAllPass：{},aiAuditFinish:{}",
                businessId, approve, patrolSkipApproval, count, isAllPass, aiAuditFinish);
        if (aiAuditFinish) {
            if (!approve || patrolSkipApproval && count == 0 && isAllPass)  {
                //全部为Ai项,且调过审批，则处理
                handleFinishPatrolTask(enterpriseId, businessId, userId, userName, subTaskId, taskParentDO, recordDO, dingCorpId, appType);
                // 修改审批人信息
                updateAuditInfoForAi(enterpriseId, businessId);
            }else {
                // 有人工审批，走正常的人工审批
                TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
                if (taskSubDO == null) {
                    return;
                }
                updateSubTaskStatus(enterpriseId, taskSubDO, userId);
                //发送消息处理
                WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, taskSubDO.getId(), taskSubDO,
                        TbDisplayConstant.BizCodeConstant.DISPLAY_HANDLE, TbDisplayConstant.ActionKeyConstant.PASS, null,
                        userId, null, null, null);
                mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), taskSubDO.getId(), JSONObject.toJSONString(workflowDataDTO));
                simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
            }
            redisUtilPool.delKey(taskStatusKey);
        } else {
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
            if (taskSubDO == null) {
                return;
            }
            updateSubTaskStatus(enterpriseId, taskSubDO, userId);
        }
    }

    private void updateSubTaskStatus(String enterpriseId, TaskSubDO taskSubDO, String userId) {
        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getNodeNo(),
                taskSubDO.getGroupItem(), taskSubDO.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder().subStatus(UnifyStatus.COMPLETE.getCode()).build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, taskSubDO.getId());
        //记录实际处理人
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(taskSubDO.getNodeNo())) {
            TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
            taskStoreMapper.updatedHandlerUserByTaskStoreId(enterpriseId, taskStoreDO.getId(), userId);
        }
    }

    private void updateAuditInfoForAi(String enterpriseId, Long businessId) {
        // 修改审批人信息
        tbDataTableMapper.updateAuditInfo(enterpriseId, businessId, PATROL_STORE, Constants.AI, Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");
        tbPatrolStoreRecordInfoMapper.updateAuditInfo(enterpriseId, businessId, Constants.AI, Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");
    }

    private String handleFinishPatrolTask(String enterpriseId, Long businessId, String userId, String userName,
                                          Long subTaskId, TaskParentDO taskParentDO, TbPatrolStoreRecordDO tbPatrolStoreRecordDO,
                                          String dingCorpId, String appType) {
        String oldSubTaskNodeNo = null;
        // 完成巡店任务
        patrolStoreService.completePotral(enterpriseId, businessId, userId, userName, subTaskId);
        // 修改任务完成状态
        if (Objects.nonNull(subTaskId) && subTaskId != 0 && taskParentDO != null
                && !TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            oldSubTaskNodeNo = unifyTaskService.completeSubTask(enterpriseId, subTaskId, null, null);
        }
        // 计划巡店
        if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            unifyTaskPersonService.updateTaskPersonWhenCompletePotral(enterpriseId, tbPatrolStoreRecordDO, dingCorpId, appType);
        }
        // 行事历
        tbPatrolPlanDetailDao.updateFinishTimeAndStatus(enterpriseId, businessId);
        return  oldSubTaskNodeNo;
    }


    private Boolean dealAiAudit(String enterpriseId, Long businessId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, String businessType, Long subTaskId, List<Long> needAiAuditStaTableIds, EnterpriseSettingDO enterpriseSettingDO, String userId) {
        log.info("开始处理AI审核dealAiAudit，businessId:{}，businessType:{}，needAiAuditStaTableIds:{}", businessId, businessType, needAiAuditStaTableIds);
        boolean aiAuditFinish = true;
        AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
        log.info("开启线下巡店AI,aiConfigDTO：{}",JSONObject.toJSONString(aiConfigDTO));
        if (aiConfigDTO != null && aiConfigDTO.aiEnable(AIBusinessModuleEnum.PATROL_STORE_OFFLINE)) {
            String aiStyle = aiConfigDTO.aiStyle(AIBusinessModuleEnum.PATROL_STORE_OFFLINE);
            List<TbDataStaTableColumnDO> allColumnList =  tbDataStaTableColumnMapper.selectByBusinessIdAndMetaTableIds(enterpriseId, businessId, needAiAuditStaTableIds);
            // ========== 数据准备 ==========
            List<Long> metaColumnIds = CollStreamUtil.toList(allColumnList, TbDataStaTableColumnDO::getMetaColumnId);
            List<TbMetaStaTableColumnDO> metaStaTableColumnDOs = tbMetaStaTableColumnMapper.getDetailByIdList(enterpriseId, metaColumnIds);

            List<Long> aiColumnIdList = metaStaTableColumnDOs.stream().filter(metaStaTableColumnDO -> metaStaTableColumnDO.getIsAiCheck() != null &&
                    metaStaTableColumnDO.getIsAiCheck() == 1).map(TbMetaStaTableColumnDO::getId).collect(Collectors.toList());

            List<TbDataStaTableColumnDO> allAiColumnList = allColumnList.stream().filter(columnDO -> aiColumnIdList.contains(columnDO.getMetaColumnId())).collect(Collectors.toList());

            List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaStaColumnMap = CollStreamUtil.toMap(metaStaTableColumnDOs, TbMetaStaTableColumnDO::getId, v -> v);
            Map<Long, List<TbMetaColumnResultDO>> metaColumnResultMap = CollStreamUtil.groupByKey(columnResultDOList, TbMetaColumnResultDO::getMetaColumnId);
            Set<String> aiModelCodes = CollStreamUtil.toSet(metaStaTableColumnDOs, TbMetaStaTableColumnDO::getAiModel);
            Map<String, AiModelLibraryDO> aiModelMap = aiModelLibraryService.getModelMapByCodes(new ArrayList<>(aiModelCodes));
            // 拆分同步与异步模型
            List<TbDataStaTableColumnDO> syncColumnList = new ArrayList<>();
            List<TbDataStaTableColumnDO> asyncColumnList = new ArrayList<>();

            for (TbDataStaTableColumnDO columnDO : allAiColumnList) {
                TbMetaStaTableColumnDO metaColumn = metaStaColumnMap.get(columnDO.getMetaColumnId());
                if (metaColumn == null) {
                    continue;
                }
                AiModelLibraryDO aiModel = aiModelMap.get(metaColumn.getAiModel());
                if (aiModel != null && aiModel.getSyncGetResult()) {
                    syncColumnList.add(columnDO);
                } else {
                    asyncColumnList.add(columnDO);
                }
            }

            // ========== 业务处理 ==========
            List<Future<PatrolStoreAIResolveDTO>> futureList = new ArrayList<>();
            for (TbDataStaTableColumnDO columnDO : allAiColumnList) {
                // AI分析，每个检查项进行一遍AI分析
                futureList.add(EXECUTOR_SERVICE.submit(() -> {
                    Integer aiStatus = Constants.STORE_WORK_AI.COLUMN_AI_STATUS_PROCESSING;
                    String aiFailReason = "";
                    // 检查项
                    TbMetaStaTableColumnDO metaColumn = metaStaColumnMap.get(columnDO.getMetaColumnId());
                    if (Objects.isNull(metaColumn)) {
                        throw new ServiceException(ErrorCodeEnum.META_COLUMN_NOT_EXIST);
                    }
                    AiModelLibraryDO aiModel = aiModelMap.get(metaColumn.getAiModel());
                    List<TbMetaColumnResultDO> metaColumnResultList = metaColumnResultMap.get(columnDO.getMetaColumnId());
                    List<String> imageList =  ImageUtil.getImageList(columnDO.getCheckPics());
                    AIResolveDTO aiResolveDTO = null;
                    if (aiModel != null && aiModel.getSyncGetResult()) {
                        // 同步模型：允许最多重试 3 次
                        try {
                            aiResolveDTO = aiService.aiPatrolResolve(enterpriseId, aiModel, imageList, metaColumn, metaColumnResultList, aiStyle);
                            log.info("同步模型正常返回,aiModel：{},aiResolveDTO：{}",JSONObject.toJSONString(aiModel) ,JSONObject.toJSONString(aiResolveDTO));
                            if (aiResolveDTO != null) {
                                aiStatus = Constants.STORE_WORK_AI.COLUMN_AI_STATUS_COMPLETE;
                            }
                        } catch (Exception e) {
                            aiFailReason = e.getMessage();
                            aiStatus = Constants.STORE_WORK_AI.COLUMN_AI_STATUS_FAIL;
                            log.info("同步模型AI调用异常，数据检查项ID：{}，模型：{}，异常信息：{}", columnDO.getId(), aiModel.getCode(), e.getMessage());
                        }
                    }
                    return new PatrolStoreAIResolveDTO(columnDO.getId(), aiStatus, aiFailReason, true, "", "", aiResolveDTO);
                }));
            }
            List<PatrolStoreAIResolveDTO> aiResultList = new ArrayList<>();
            futureList.forEach(v -> {
                try {
                    aiResultList.add(v.get());
                } catch (ServiceException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("AI分析异常", e.getCause());
                    throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
                }
            });
            List<TbDataStaTableColumnDO> updateColumnList = new ArrayList<>();
            List<TbDataStaColumnExtendInfoDO> updateColumnExtendList = new ArrayList<>();
            for (PatrolStoreAIResolveDTO aiResult : aiResultList) {
                TbDataStaTableColumnDO updateColumn = TbDataStaTableColumnDO.builder()
                        .id(aiResult.getColumnId())
                        .build();

                TbDataStaColumnExtendInfoDO updateColumnExtendInfo = TbDataStaColumnExtendInfoDO.builder()
                        .id(aiResult.getColumnId())
                        .businessId(businessId)
                        .subTaskId(subTaskId)
                        .aiStatus(aiResult.getAiStatus())
                        .aiFailReason(aiResult.getAiFailReason())
                        .aiCheckResultId(0L)
                        .createUserId(userId)
                        .createTime(new Date())
                        .build();

                if (Objects.nonNull(aiResult.getAiResolveDTO())) {
                    setAiField(updateColumn, updateColumnExtendInfo, aiResult.getAiResolveDTO());
                }
                updateColumnList.add(updateColumn);
                updateColumnExtendList.add(updateColumnExtendInfo);
            }
            if (CollectionUtils.isNotEmpty(updateColumnList)) {
                tbDataStaTableColumnMapper.batchUpdateResult(enterpriseId, updateColumnList);
            }
            if (CollectionUtils.isNotEmpty(updateColumnExtendList)) {
                tbDataStaColumnExtendInfoMapper.batchInsertOrUpdateDataColumnExtendInfo(enterpriseId, updateColumnExtendList);
            }

            //计算得分
            Map<Long, Long> metaToDataTableMap = ListUtils.emptyIfNull(allAiColumnList).stream()
                    .collect(Collectors.toMap(
                            TbDataStaTableColumnDO::getMetaTableId,
                            TbDataStaTableColumnDO::getDataTableId,
                            (v1, v2) -> v1 // 如有重复保留第一个（理论上不会冲突）
                    ));
            List<Long> metaTableIdList = new ArrayList<>(metaToDataTableMap.keySet());
            List<TbMetaTableDO> metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
            Map<Long, TbMetaTableDO> metaTableMap = ListUtils.emptyIfNull(metaTableDOList).stream()
                    .collect(Collectors.toMap(TbMetaTableDO::getId, v -> v));

            metaToDataTableMap.forEach((metaTableId, dataTableId) -> {
                TbMetaTableDO tbMetaTable = metaTableMap.get(metaTableId);
                if (tbMetaTable != null && dataTableId != null) {
                    patrolStoreService.countScore(enterpriseId, tbPatrolStoreRecordDO, tbMetaTable, dataTableId);
                }
            });
            // 计算巡店记录的分数
            PatrolStoreSubmitParam submitParam = new PatrolStoreSubmitParam();
            submitParam.setBusinessId(businessId);
            patrolStoreService.countPatrolStoreRecordScore(enterpriseId, submitParam, businessType);

            boolean checkHasProcessingColumn = tbDataStaColumnExtendInfoMapper.checkHasProcessingColumn(enterpriseId, businessId);
            if (checkHasProcessingColumn) {
                insertAiAuditHistory(enterpriseId, businessId, subTaskId);
            } else {
                log.info("存在AI分析中的项，暂不插入AI审批操作记录,businessId:{}", businessId);
                aiAuditFinish = false;
            }

            // 异步模型后处理（异步提交任务）
            for (TbDataStaTableColumnDO columnDO : asyncColumnList) {
                EXECUTOR_SERVICE.submit(() -> {
                    TbMetaStaTableColumnDO metaColumn = metaStaColumnMap.get(columnDO.getMetaColumnId());
                    AiModelLibraryDO aiModel = aiModelMap.get(metaColumn.getAiModel());
                    try {
                        List<TbMetaColumnResultDO> metaColumnResultList = metaColumnResultMap.get(columnDO.getMetaColumnId());
                        AIResolveRequestDTO request = new AIResolveRequestDTO();
                        request.setDataColumn(columnDO);
                        request.setMetaStaTableColumnDO(metaColumn);
                        request.setResultDOList(metaColumnResultList);
                        request.setStyle(aiStyle);
                        AIResolveDTO aiResolveDTO = aiService.aiPatrolResolve(enterpriseId, AiResolveBusinessTypeEnum.PATROL, aiModel, request);
                        log.info("异步模型正常返回, aiModel：{}, request：{},aiResolveDTO：{}",JSONObject.toJSONString(aiModel), JSONObject.toJSONString(request), JSONObject.toJSONString(aiResolveDTO));
                    } catch (Exception e) {
                        log.error("异步模型AI调用异常，数据检查项ID：{}，模型：{}，异常：{}", columnDO.getId(), aiModel != null ? aiModel.getCode() : "未知模型",  e.getMessage(), e);
                    }
                });
            }
        }
        return aiAuditFinish;

    }

    private void insertAiAuditHistory(String enterpriseId, Long businessId, Long subTaskId) {
        // 插入AI审批操作记录
        tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, TbPatrolStoreHistoryDo.builder()
                .createTime(new Date())
                .updateTime(new Date())
                .actionKey(PatrolStoreConstant.ActionKeyConstant.PASS)
                .businessId(businessId)
                .deleted(false)
                .nodeNo(UnifyNodeEnum.SECOND_NODE.getCode())
                .operateType(PatrolStoreConstant.PatrolStoreOperateTypeConstant.APPROVE)
                .operateUserName(Constants.AI)
                .operateUserId(Constants.AI)
                .subTaskId(subTaskId)
                .photo("")
                .remark(null)
                .build());
    }


}

