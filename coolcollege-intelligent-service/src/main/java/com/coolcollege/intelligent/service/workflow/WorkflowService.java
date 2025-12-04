package com.coolcollege.intelligent.service.workflow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.workflow.FlowNodeTypeEnum;
import com.coolcollege.intelligent.common.util.FilterEmojiUtils;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskTurnDTO;
import com.coolcollege.intelligent.model.workFlow.WorkFlowCaseDTO;
import com.coolcollege.intelligent.model.workFlow.WorkFlowNodeDTO;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;


import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/27 11:04
 */
@Service
@Slf4j
public class WorkflowService {

    public Boolean subSubmitCheck(TaskSubDO subDO) {
        if (UnifyStatus.COMPLETE.getCode().equals(subDO.getSubStatus())
            || UnifyTaskConstant.FLOW_PROCESSED.equals(subDO.getFlowState())) {
            return Boolean.FALSE;
        }
        return true;
    }

    /**
     * 根据子任务信息拼凑任务引擎json对象
     *
     * @param enterpriseId
     * @param cid
     * @param taskSubDO
     * @param bizCode
     * @param actionKey
     * @param userId
     * @param remark
     * @param taskData
     * @return
     */
    public WorkflowDataDTO getFlowJsonObject(String enterpriseId, Long cid, TaskSubDO taskSubDO, String bizCode,
                                        String actionKey, UnifyTaskTurnDTO turnTask, String userId, String remark, String taskData, String handleAction) {
        // data
        WorkflowDataDTO data = new WorkflowDataDTO();
        Long cycleCount = taskSubDO.getCycleCount();
        if(Objects.isNull(cycleCount)){
            cycleCount = 1L;
        }
        String primaryKey = MessageFormat.format("{0}_{1}_{2}_{3}_{4}", JSON.toJSONString(taskSubDO.getUnifyTaskId()), taskSubDO.getStoreId(), taskSubDO.getLoopCount(), taskSubDO.getNodeNo(), cycleCount);
        //转交不更新节点，不需要去重
        if(DisplayConstant.ActionKeyConstant.TURN.equals(actionKey)){
            primaryKey = UUIDUtils.get32UUID();
        }
        data.setPrimaryKey(primaryKey);
        data.setBizCode(bizCode);
        data.setEnterpriseId(enterpriseId);
        data.setCycleCount(cycleCount);
        data.setNodeNo(taskSubDO.getNodeNo());
        data.setActionKey(actionKey);
        data.setCid(String.valueOf(cid));
        data.setTurnFromUserId(userId);
        if(DisplayConstant.ActionKeyConstant.TURN.equals(actionKey)){
            data.setTurnToUserId(turnTask.getTurnUserId());
        }
        data.setTaskData(taskData);
        data.setRemark(remark);
        data.setSubTaskId(taskSubDO.getId());
        data.setUnifyTaskId(taskSubDO.getUnifyTaskId());
        data.setStoreId(taskSubDO.getStoreId());
        data.setLoopCount(taskSubDO.getLoopCount());
        data.setCreateUserId(userId);
        JSONObject dataObj = new JSONObject();
        dataObj.put("flow_trigger_flag", true);
        dataObj.put("flow_template_id", taskSubDO.getTemplateId());
        dataObj.put("flow_instance_id", taskSubDO.getInstanceId());
        dataObj.put("flow_cycle_count", taskSubDO.getCycleCount());
        dataObj.put("flow_node_no", taskSubDO.getNodeNo());
        dataObj.put("flow_action_key", actionKey);
        if (DisplayConstant.ActionKeyConstant.TURN.equals(actionKey)) {
            dataObj.put("flow_turn_from_user_id", userId);
            dataObj.put("flow_turn_to_user_id", turnTask.getTurnUserId());
        }

        // 业务自己加的
        dataObj.put("create_user_id",userId);
        dataObj.put("sub_task_id",taskSubDO.getId());
        dataObj.put("remark", FilterEmojiUtils.filterEmoji(remark));
        dataObj.put("task_data",taskData);
        if(StringUtils.isNotBlank(handleAction)){
            dataObj.put("handle_action", handleAction);
        }
        data.setData(JSONObject.toJSONString(dataObj));
        return data;
    }

    /**
     * 获取处理任务节点
     * @return
     */
    public List<WorkFlowNodeDTO> dealFlowInfoNodeList(List<TaskProcessDTO> nodeList, String taskType) {
        // 节点配置信息组装
        Map<String, String> nodeMap = ListUtils.emptyIfNull(nodeList).stream()
                .filter(a -> a.getNodeNo() != null && a.getApproveType() != null)
                .collect(Collectors.toMap(TaskProcessDTO::getNodeNo, TaskProcessDTO::getApproveType, (a, b) -> a));
        //一级审批节点
        boolean secondUser = StringUtils.isNotBlank(nodeMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
        //二级审批节点
        boolean thirdUser = StringUtils.isNotBlank(nodeMap.get(UnifyNodeEnum.THIRD_NODE.getCode()));
        //三级审批节点
        boolean fourUser = StringUtils.isNotBlank(nodeMap.get(UnifyNodeEnum.FOUR_NODE.getCode()));
        //四级审批节点
        boolean fiveUser = StringUtils.isNotBlank(nodeMap.get(UnifyNodeEnum.FIVE_NODE.getCode()));
        //五级审批节点
        boolean sixUser = StringUtils.isNotBlank(nodeMap.get(UnifyNodeEnum.SIX_NODE.getCode()));
        // 判断是否有复审节点，没有复审节点审核通过流程直接结束
        String secondGoNode = UnifyNodeEnum.END_NODE.getCode();
        if (secondUser) {
            secondGoNode = UnifyNodeEnum.SECOND_NODE.getCode();
        }
        // 组装node
        List<WorkFlowNodeDTO> nodes = Lists.newArrayList();
        // 1任务发起
        WorkFlowNodeDTO first = new WorkFlowNodeDTO();
        List<WorkFlowCaseDTO> flowCases1 = Lists.newArrayList();
        WorkFlowCaseDTO case1 = new WorkFlowCaseDTO();
        case1.setDefaultGoNode(secondGoNode);
        case1.setGoNode(secondGoNode);
        flowCases1.add(case1);
        first.setFlowCases(flowCases1);
        first.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
        first.setNodeName("发送审批任务");
        first.setNodeType(FlowNodeTypeEnum.NODE_NORMAL.getValue());
        first.setStartNode(Boolean.TRUE);
        first.setEndNode(Boolean.FALSE);
        nodes.add(first);
        //一级审批节点
        if(secondUser){
            UnifyNodeEnum nextNode = UnifyNodeEnum.END_NODE;
            if (thirdUser) {
                nextNode = UnifyNodeEnum.THIRD_NODE;
            }
            WorkFlowNodeDTO secondNode = dealApproveNode(UnifyNodeEnum.SECOND_NODE, UnifyNodeEnum.FIRST_NODE, nextNode, nodeMap);
            nodes.add(secondNode);
        }
        //二级审批节点
        if(thirdUser){
            UnifyNodeEnum nextNode = UnifyNodeEnum.END_NODE;
            if (fourUser) {
                nextNode = UnifyNodeEnum.FOUR_NODE;
            }
            UnifyNodeEnum preNode = UnifyNodeEnum.FIRST_NODE;
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
                //新陈列复审拒绝，返回到审核阶段
                preNode = UnifyNodeEnum.SECOND_NODE;
            }
            WorkFlowNodeDTO thirdNode = dealApproveNode(UnifyNodeEnum.THIRD_NODE, preNode, nextNode, nodeMap);
            nodes.add(thirdNode);
        }
        //三级审批节点
        if(fourUser){
            UnifyNodeEnum nextNode = UnifyNodeEnum.END_NODE;
            if (fiveUser) {
                nextNode = UnifyNodeEnum.FIVE_NODE;
            }
            UnifyNodeEnum preNode = UnifyNodeEnum.FIRST_NODE;
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
                //新陈列复审拒绝，返回到审核阶段
                preNode = UnifyNodeEnum.THIRD_NODE;
            }
            WorkFlowNodeDTO fourNode = dealApproveNode(UnifyNodeEnum.FOUR_NODE, preNode, nextNode, nodeMap);
            nodes.add(fourNode);
        }
        //四级审批节点
        if(fiveUser){
            UnifyNodeEnum nextNode = UnifyNodeEnum.END_NODE;
            if (sixUser) {
                nextNode = UnifyNodeEnum.SIX_NODE;
            }
            UnifyNodeEnum preNode = UnifyNodeEnum.FIRST_NODE;
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
                //新陈列复审拒绝，返回到审核阶段
                preNode = UnifyNodeEnum.FOUR_NODE;
            }
            WorkFlowNodeDTO fiveNode = dealApproveNode(UnifyNodeEnum.FIVE_NODE, preNode, nextNode, nodeMap);
            nodes.add(fiveNode);
        }
        //五级审批节点
        if(sixUser){
            UnifyNodeEnum preNode = UnifyNodeEnum.FIRST_NODE;
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)){
                //新陈列复审拒绝，返回到审核阶段
                preNode = UnifyNodeEnum.FIVE_NODE;
            }
            WorkFlowNodeDTO sixNode = dealApproveNode(UnifyNodeEnum.SIX_NODE, preNode, UnifyNodeEnum.END_NODE, nodeMap);
            nodes.add(sixNode);
        }
        // 3结束
        WorkFlowNodeDTO end = new WorkFlowNodeDTO();
        end.setExecuteCount(1);
        end.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        end.setEndNode(Boolean.TRUE);
        end.setStartNode(Boolean.FALSE);
        end.setNodeType(FlowNodeTypeEnum.NODE_NORMAL.getValue());
        end.setNodeName("结束节点");
        nodes.add(end);
        return nodes;
    }

    /**
     * 构建审批节点
     * @param currNode 当前节点
     * @param preNode 上一个节点
     * @param nextNode 下一个节点
     * @param nodeMap 节点信息
     */
    private WorkFlowNodeDTO dealApproveNode(UnifyNodeEnum currNode, UnifyNodeEnum preNode, UnifyNodeEnum nextNode, Map<String, String> nodeMap ) {
        WorkFlowNodeDTO flowNode = new WorkFlowNodeDTO();
        List<WorkFlowCaseDTO> flowCases2 = Lists.newArrayList();
        WorkFlowCaseDTO casePass2 = new WorkFlowCaseDTO();
        casePass2.setCaseWhen("pass");
        casePass2.setCheckType(nodeMap.get(currNode.getCode()));
        casePass2.setGoNode(nextNode.getCode());
        WorkFlowCaseDTO caseReject2 = new WorkFlowCaseDTO();
        caseReject2.setCaseWhen("reject");
        caseReject2.setGoNode(preNode.getCode());
        flowCases2.add(casePass2);
        flowCases2.add(caseReject2);
        flowNode.setFlowCases(flowCases2);
        flowNode.setNodeNo(currNode.getCode());
        flowNode.setNodeName("审批结果判定");
        flowNode.setNodeType(FlowNodeTypeEnum.NODE_SWITCH.getValue());
        flowNode.setStartNode(Boolean.FALSE);
        flowNode.setEndNode(Boolean.FALSE);
        return flowNode;
    }
}
