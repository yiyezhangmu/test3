package com.coolcollege.intelligent.model.unifytask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.TaskRunRuleEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.store.StoreDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author byd
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
public class TaskStoreDO {
    /**
     * ID
     */
    @Id
    private Long id;

    /**
     * 任务发起时间
     */
    private Date createTime;

    /**
     *
     */
    private Date editTime;

    /**
     * 当前流程进度节点
     */
    private String nodeNo;

    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 地区id
     */
    private Long regionId;


    /**
     * 地区path新
     */
    private String regionWay;

    /**
     * 任务发起者
     */
    private String createUserId;

    /**
     * 任务发起者名称
     */
    private String createUserName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 任务处理时间
     */
    private Date handleTime;
    /**
     * 任务周期 DAY MONTH YEAR
     */
    private String taskCycle;
    /**
     * 运行规则ONCE单次/LOOP循环
     */
    private String runRule;


    /**
     * 任务状态
     */
    private String actionKey;
    /**
     * 备注
     */
    private String remark;

    /**
     * 任务类型:陈列，巡店，工单 等,来源父任务
     */
    private String taskType;
    /**
     * 子任务状态
     */
    private String subStatus;
    /**
     * 流程属性初始化init/进行中processed
     */
    private String flowState;

    /**
     * 对应cform
     */
    private String bizCode;
    /**
     * 通用数据
     */
    private String taskData;

    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 审批链任务开始时间
     */
    private Date subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Date subEndTime;

    /**
     * 任务名称 非DO
     */
    private String taskName;

    private String storeName;

    /**
     * 处理截止时间
     */
    private Date handlerEndTime;

    /**
     * 处理人列表  非do
     */
    List<PersonDTO> processUserList;
    /**
     * 抄送人id集合
     */
    private String ccUserIds;
    /**
     * 门店任务扩展信息
     */
    private String extendInfo;

    /**
     * 首次处理时间  非do
     */
    private Date firstHandlerTime;

    /**
     * 任务数据
     */
    private String taskInfo;

    private Integer deleted ;

    /**
     * es使用  详细类型
     */
    private String storeTaskDetailType;

    /**
     * es使用 所有处理人
     */
    private String handlerUserIds;

    private String handlerUserId;
    
    public TaskStoreDO() {
        super();
    }
    


    public TaskStoreDO(TaskParentDO parent, TaskSubDO sub) {
        this.createTime = new Date();
        this.editTime = new Date();
        this.storeId = sub.getStoreId();
        this.unifyTaskId = sub.getUnifyTaskId();
        this.nodeNo = sub.getNodeNo();
        this.loopCount = sub.getLoopCount();
        this.createUserId = sub.getCreateUserId();
        this.taskType = parent.getTaskType();
        this.runRule = parent.getRunRule();
        this.taskCycle = parent.getTaskCycle();
        this.actionKey = sub.getActionKey();
        this.flowState = sub.getFlowState();
        this.subStatus = sub.getSubStatus();
        this.bizCode = sub.getBizCode();
        this.taskData = sub.getTaskData();
        this.subBeginTime = DateUtil.convertTimestampToDate(sub.getSubBeginTime());
        this.subEndTime = DateUtil.convertTimestampToDate(sub.getSubEndTime());
    }

    public static TaskStoreDO buildStoreTask(TaskParentDO parent, StoreDO storeDO, List<TaskMappingDO> personList, long loopCount) {
        TaskStoreDO taskStore = new TaskStoreDO();
        Long beginTime = null;
        Long endTime = null;
        if (TaskRunRuleEnum.ONCE.getCode().equals(parent.getRunRule())) {
            beginTime = parent.getBeginTime();
            endTime = parent.getEndTime();
        } else if (TaskRunRuleEnum.LOOP.getCode().equals(parent.getRunRule())) {
            Integer limitInt = new Double(parent.getLimitHour() * 60).intValue();
            beginTime = System.currentTimeMillis();
            endTime = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(), limitInt).getTime();
        }
        taskStore.setCreateTime(new Date());
        taskStore.setEditTime(new Date());
        taskStore.setStoreId(storeDO.getStoreId());
        taskStore.setUnifyTaskId(parent.getId());
        taskStore.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
        taskStore.setLoopCount(loopCount);
        taskStore.setCreateUserId(parent.getCreateUserId());
        taskStore.setTaskType(parent.getTaskType());
        taskStore.setRunRule(parent.getRunRule());
        taskStore.setTaskCycle(parent.getTaskCycle());
        taskStore.setFlowState(UnifyTaskConstant.FLOW_INIT);
        taskStore.setSubStatus(UnifyStatus.ONGOING.getCode());
        taskStore.setSubBeginTime(DateUtil.convertTimestampToDate(beginTime));
        taskStore.setSubEndTime(DateUtil.convertTimestampToDate(endTime));
        taskStore.setRegionId(storeDO.getRegionId());
        taskStore.setRegionWay(storeDO.getRegionPath());
        taskStore.setStoreName(storeDO.getStoreName());
        if(StringUtils.isNotBlank(parent.getTaskInfo())){
            try {
                JSONObject jsonObject = JSONObject.parseObject(parent.getTaskInfo());
                JSONObject patrolStoreDefined = jsonObject.getJSONObject("patrolStoreDefined");
                if(Objects.nonNull(patrolStoreDefined)){
                    taskStore.setTaskData(JSONObject.toJSONString(patrolStoreDefined));
                }
            } catch (Exception e) {
                log.info("任务信息解析失败");
            }
        }
        JSONObject jsonObject = new JSONObject();
        Map<String, List<TaskMappingDO>> nodeUserMap = personList.stream().collect(Collectors.groupingBy(TaskMappingDO::getNode));
        Set<String> handlerUserSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.FIRST_NODE);
        Set<String> auditUserSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.SECOND_NODE);
        Set<String> recheckUserSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.THIRD_NODE);
        Set<String> thirdApproveSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.FOUR_NODE);
        Set<String> fourApproveSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.FIVE_NODE);
        Set<String> fiveApproveSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.SIX_NODE);
        Set<String> ccUserSet = TaskMappingDO.getNodeUserList(nodeUserMap, UnifyNodeEnum.CC);
        if (CollectionUtils.isNotEmpty(ccUserSet)) {
            taskStore.setCcUserIds(toStringByUserIdSet(ccUserSet));
        }
        if (CollectionUtils.isNotEmpty(handlerUserSet)) {
            jsonObject.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdSet(handlerUserSet));
        }
        if (CollectionUtils.isNotEmpty(auditUserSet)) {
            jsonObject.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdSet(auditUserSet));
        }
        if (CollectionUtils.isNotEmpty(recheckUserSet)) {
            jsonObject.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdSet(recheckUserSet));
        }
        if (CollectionUtils.isNotEmpty(thirdApproveSet)) {
            jsonObject.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdSet(thirdApproveSet));
        }
        if (CollectionUtils.isNotEmpty(fourApproveSet)) {
            jsonObject.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdSet(fourApproveSet));
        }
        if (CollectionUtils.isNotEmpty(fiveApproveSet)) {
            jsonObject.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdSet(fiveApproveSet));
        }
        taskStore.setExtendInfo(JSON.toJSONString(jsonObject));
        return taskStore;
    }


    public static TaskStoreDO questionOrderToStoreTask(TaskParentDO parent, UnifyTaskParentItemDO unifyTaskParentItem, StoreDO storeDO, List<TaskMappingDO> personList, long taskParentItemId) {
        TaskStoreDO taskStore = new TaskStoreDO();
        Long beginTime = unifyTaskParentItem.getBeginTime().getTime();
        Long endTime = unifyTaskParentItem.getEndTime().getTime();
        taskStore.setCreateTime(new Date());
        taskStore.setEditTime(new Date());
        taskStore.setStoreId(storeDO.getStoreId());
        taskStore.setUnifyTaskId(parent.getId());
        taskStore.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
        taskStore.setLoopCount(unifyTaskParentItem.getLoopCount());
        taskStore.setCreateUserId(parent.getCreateUserId());
        taskStore.setTaskType(parent.getTaskType());
        taskStore.setRunRule(parent.getRunRule());
        taskStore.setTaskCycle(parent.getTaskCycle());
        taskStore.setFlowState(UnifyTaskConstant.FLOW_INIT);
        taskStore.setSubStatus(UnifyStatus.ONGOING.getCode());
        taskStore.setSubBeginTime(DateUtil.convertTimestampToDate(beginTime));
        taskStore.setSubEndTime(DateUtil.convertTimestampToDate(endTime));
        taskStore.setRegionId(storeDO.getRegionId());
        taskStore.setRegionWay(storeDO.getRegionPath());
        taskStore.setStoreName(storeDO.getStoreName());
        taskStore.setTaskInfo(unifyTaskParentItem.getTaskInfo());
        JSONObject jsonObject = new JSONObject();
        Set<String> handlerUserSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.FIRST_NODE);
        Set<String> auditUserSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.SECOND_NODE);
        Set<String> recheckUserSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.THIRD_NODE);
        Set<String> thirdApproveSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.FOUR_NODE);
        Set<String> fourApproveSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.FIVE_NODE);
        Set<String> fiveApproveSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.SIX_NODE);
        Set<String> ccUserSet = TaskMappingDO.getNodeUserList(personList, storeDO.getStoreId(), UnifyNodeEnum.CC);
        if (CollectionUtils.isNotEmpty(ccUserSet)) {
            taskStore.setCcUserIds(toStringByUserIdSet(ccUserSet));
        }
        if (CollectionUtils.isNotEmpty(handlerUserSet)) {
            jsonObject.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdSet(handlerUserSet));
        }
        if (CollectionUtils.isNotEmpty(auditUserSet)) {
            jsonObject.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdSet(auditUserSet));
        }
        if (CollectionUtils.isNotEmpty(recheckUserSet)) {
            jsonObject.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdSet(recheckUserSet));
        }
        if (CollectionUtils.isNotEmpty(thirdApproveSet)) {
            jsonObject.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdSet(thirdApproveSet));
        }
        if (CollectionUtils.isNotEmpty(fourApproveSet)) {
            jsonObject.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdSet(fourApproveSet));
        }
        if (CollectionUtils.isNotEmpty(fiveApproveSet)) {
            jsonObject.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdSet(fiveApproveSet));
        }
        jsonObject.put("taskParentItemId", taskParentItemId);
        taskStore.setExtendInfo(JSON.toJSONString(jsonObject));
        return taskStore;
    }

    private static String toStringByUserIdSet(Set<String> userIdSet) {
        if(CollectionUtils.isEmpty(userIdSet)){
            return null;
        }
        return Constants.COMMA + String.join(Constants.COMMA, userIdSet) + Constants.COMMA;
    }
    
}
