package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.workFlow.WorkFlowCaseDTO;
import com.coolcollege.intelligent.model.workFlow.WorkFlowNodeDTO;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.model.workFlow.WorkflowDealDTO;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: WorkflowListener
 * @Description:
 * @date 2024-01-29 9:55
 */
@Service
@Slf4j
public class WorkflowListener implements MessageListener {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private TaskSubDao taskSubDao;
    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        try {
            String text = new String(message.getBody());
            log.info("workflow表单中台监听信息:####" + text);
            WorkflowDataDTO flow = JSONObject.parseObject(text, WorkflowDataDTO.class);
            log.info("###sentTask flow={}", JSON.toJSONString(flow));
            ValidateUtil.validateString(flow.getBizCode(), flow.getCid(), flow.getNodeNo(),flow.getEnterpriseId());
            String enterpriseId = flow.getEnterpriseId();
            //切数据源
            DataSourceHelper.reset();
            EnterpriseStoreCheckSettingDO checkSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
            EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            WorkflowDealDTO workflowDeal = getWorkflowDealDTO(flow);
            if(Objects.isNull(workflowDeal)){
                log.info("流程处理异常");
                return Action.CommitMessage;
            }
            unifyTaskService.sendTask(enterpriseConfigDO.getDingCorpId(), workflowDeal, enterpriseId, checkSettingDO,enterpriseConfigDO.getAppType());
            DataSourceHelper.reset();
            //清除任务状态，非处理中
            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + flow.getUnifyTaskId() + "_" + flow.getStoreId() + "_" + flow.getLoopCount();
            redisUtilPool.delKey(taskStatusKey);
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error("###sentTask error={}", e.getMessage());
            return Action.ReconsumeLater;
        }
    }

    public WorkflowDealDTO getWorkflowDealDTO(WorkflowDataDTO flow){
        TaskParentDO taskParent = taskParentDao.selectById(flow.getEnterpriseId(), flow.getUnifyTaskId());
        if(Objects.isNull(taskParent) || Constants.INDEX_ZERO.equals(taskParent.getStatusType())){
            log.info("任务不存在或已停止");
            return null;
        }
        WorkflowDealDTO workflowDeal = new WorkflowDealDTO();
        List<TaskProcessDTO> nodeList = JSONObject.parseArray(taskParent.getNodeInfo(), TaskProcessDTO.class);
        if(TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskParent.getTaskType())){
            UnifyTaskParentItemDO taskItem = unifyTaskParentItemDao.getByUnifyTaskIdAndStoreIdAndLoopCount(flow.getEnterpriseId(), flow.getUnifyTaskId(), flow.getStoreId(), flow.getLoopCount());
            if(Objects.isNull(taskItem)){
                log.info("工单流程相关数据找不到");
                return null;
            }
            nodeList = JSONObject.parseArray(taskItem.getNodeInfo(), TaskProcessDTO.class);
        }
        List<WorkFlowNodeDTO> workflowNodeList = workflowService.dealFlowInfoNodeList(nodeList, taskParent.getTaskType());
        Map<String, WorkFlowNodeDTO> nodeMap = workflowNodeList.stream().collect(Collectors.toMap(o -> o.getNodeNo(), Function.identity()));
        WorkFlowNodeDTO workFlowNode = nodeMap.get(flow.getNodeNo());
        if(Objects.isNull(workFlowNode)){
            log.info("流程节点不存在");
            return null;
        }
        workflowDeal.setEnterpriseId(flow.getEnterpriseId());
        workflowDeal.setCid(flow.getCid());
        workflowDeal.setBizCode(flow.getBizCode());
        workflowDeal.setCycleCount(flow.getCycleCount());
        workflowDeal.setActionKey(flow.getActionKey());
        workflowDeal.setTurnFromUserId(flow.getTurnFromUserId());
        workflowDeal.setTurnToUserId(flow.getTurnToUserId());
        workflowDeal.setTaskData(flow.getTaskData());
        workflowDeal.setSubTaskId(flow.getSubTaskId());
        workflowDeal.setUnifyTaskId(flow.getUnifyTaskId());
        workflowDeal.setStoreId(flow.getStoreId());
        workflowDeal.setLoopCount(flow.getLoopCount());
        workflowDeal.setRemark(flow.getRemark());
        workflowDeal.setCreateUserId(flow.getCreateUserId());
        workflowDeal.setBeforeNodeNo(flow.getNodeNo());
        workflowDeal.setData(flow.getData());
        workflowDeal.setPrimaryKey(flow.getPrimaryKey());
        List<WorkFlowCaseDTO> flowCases = workFlowNode.getFlowCases();
        if(CollectionUtils.isEmpty(flowCases)){
            log.info("流程节点对应处理节点不存在");
            return null;
        }
        if(DisplayConstant.ActionKeyConstant.TURN.equals(flow.getActionKey())){
            workflowDeal.setNextNodeNo(flow.getNodeNo());
        }else if(flowCases.size() == Constants.ONE && !workFlowNode.getEndNode()){
            workflowDeal.setNextNodeNo(flowCases.get(Constants.ZERO).getGoNode());
        }else{
            Map<String, WorkFlowCaseDTO> caseMap = flowCases.stream().collect(Collectors.toMap(k -> k.getCaseWhen(), Function.identity()));
            WorkFlowCaseDTO workFlowCase = caseMap.get(flow.getActionKey());
            workflowDeal.setNextNodeNo(workFlowCase.getGoNode());
        }
        WorkFlowNodeDTO nextWorkFlowNode = nodeMap.get(workflowDeal.getNextNodeNo());
        if(Objects.isNull(nextWorkFlowNode)){
            log.info("流程节点对应处理节点不存在");
            return null;
        }
        workflowDeal.setEndFlag(nextWorkFlowNode.getEndNode());
        return workflowDeal;
    }
}
