package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.SysLogConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.patrolstore.dto.StopTaskDTO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayDeleteParam;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.ParentTaskReminderDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
* describe: 任务操作内容处理
*
* @author wangff
* @date 2025-01-24
*/
@Service
@Slf4j
public class UnifyTaskResolve extends AbstractOpContentResolve {

    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private QuestionRecordDao questionRecordDao;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.UNIFY_TASK;
    }

    @Override
    protected void init() {
        super.init();
        funcMap.put(PATROL_STORE_TASK_BATCH_DELETE, this::patrolStoreTaskBatchDelete);
        funcMap.put(PATROL_STORE_TASK_REMIND, this::patrolStoreTaskRemind);
        funcMap.put(PATROL_STORE_TASK_STORE_REMIND, this::patrolStoreTaskStoreRemind);
        funcMap.put(INSERT_BY_PERSON, this::insertByPerson);
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        UnifyTaskBuildDTO task = jsonObject.getObject("task", UnifyTaskBuildDTO.class);
        String taskType = TaskTypeEnum.getTaskTypeByCode(task.getTaskType());
        return SysLogHelper.buildContent(INSERT_TEMPLATE, taskType, task.getTaskName());
    }

    public String insertByPerson(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        BuildByPersonRequest request = jsonObject.getObject("request", BuildByPersonRequest.class);
        String taskType = TaskTypeEnum.getTaskTypeByCode(request.getTaskType());
        return SysLogHelper.buildContent(INSERT_TEMPLATE, taskType, request.getTaskName());
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        UnifyTaskBuildDTO task = jsonObject.getObject("task", UnifyTaskBuildDTO.class);
        String taskType = TaskTypeEnum.getTaskTypeByCode(task.getTaskType());
        return SysLogHelper.buildContent(UPDATE_TEMPLATE, taskType, task.getTaskName());
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    @Override
    protected String remind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        ParentTaskReminderDTO request = jsonObject.getObject("param", ParentTaskReminderDTO.class);
        List<TaskParentDO> parentList = taskParentDao.selectByIds(enterpriseId, request.getUnifyTaskIds());
        if (CollectionUtil.isEmpty(parentList)) {
            log.info("remind#父任务为空");
            return null;
        }
        if (Objects.nonNull(request.getStoreId())) {
            TaskParentDO parentDO = parentList.get(0);
            StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, request.getStoreId());
            String storeItems = SysLogHelper.buildBatchContentItem(ListUtil.toList(storeDO), StoreDO::getStoreName, StoreDO::getId);
            return getTaskStoreContent(SysLogConstant.REMIND, parentDO, storeItems);
        } else {
            String taskType = TaskTypeEnum.getTaskTypeByCode(parentList.get(0).getTaskType());
            String name = SysLogHelper.buildBatchContentItem(parentList, TaskParentDO::getTaskName);
            return SysLogHelper.buildContent(REMIND_TEMPLATE2, taskType, name);
        }
    }

    /**
     * 巡店任务父任务催办
     */
    private String patrolStoreTaskRemind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        Long unifyTaskId = jsonObject.getLong("taskId");
        TaskParentDO parentDO = taskParentDao.selectById(enterpriseId, unifyTaskId);
        if (Objects.isNull(parentDO)) {
            log.info("patrolStoreTaskRemind#父任务不存在");
            return null;
        }
        String taskType = TaskTypeEnum.getTaskTypeByCode(parentDO.getTaskType());
        return SysLogHelper.buildContent(REMIND_TEMPLATE, taskType, parentDO.getTaskName());
    }

    /**
     * 巡店任务门店任务催办
     */
    private String patrolStoreTaskStoreRemind(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        Long taskStoreId = jsonObject.getLong("taskStoreId");
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        if (Objects.isNull(taskStoreDO)) {
            log.info("patrolStoreTaskStoreRemind#门店任务为空");
            if (jsonObject.containsKey("businessId")) {
                // 表单巡店时前端会调用催办接口
                sysLogDO.setDelete(true);
            }
            return null;
        }
        TaskParentDO parentDO = taskParentDao.selectById(enterpriseId, taskStoreDO.getUnifyTaskId());
        if (Objects.isNull(parentDO)) {
            log.info("patrolStoreTaskStoreRemind#父任务不存在");
            return null;
        }
        String storeItems = SysLogHelper.buildBatchContentItem(ListUtil.toList(taskStoreDO), TaskStoreDO::getStoreName, TaskStoreDO::getStoreId);
        return getTaskStoreContent(SysLogConstant.REMIND, parentDO, storeItems);
    }

    @Override
    protected String stop(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        StopTaskDTO request = jsonObject.getObject("stopTaskDTO", StopTaskDTO.class);
        TaskParentDO parentDO = taskParentDao.selectById(enterpriseId, request.getParentTaskId());
        String taskType = TaskTypeEnum.getTaskTypeByCode(parentDO.getTaskType());
        return SysLogHelper.buildContent(STOP_TEMPLATE, taskType, parentDO.getTaskName());
    }

    @Override
    protected String reallocate(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        JSONObject request = jsonObject.getJSONObject("task");
        List<Long> taskStoreIds = request.containsKey("storeTaskIdList") ? request.getJSONArray("storeTaskIdList").toJavaList(Long.class) : ListUtil.toList(request.getLong("taskStoreId"));
        if (CollectionUtil.isEmpty(taskStoreIds)) {
            log.info("reallocate#门店任务id为空");
            return null;
        }
        List<TaskStoreDO> taskStoreList = taskStoreMapper.listByUnifyIds(enterpriseId, taskStoreIds, null);
        if (CollectionUtil.isEmpty(taskStoreList)) {
            log.info("reallocate#门店任务为空");
            return null;
        }
        TaskStoreDO taskStoreDO = taskStoreList.get(0);
        if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskStoreDO.getTaskType())) {
            // 问题工单单独处理
            return questionReallocate(enterpriseId, sysLogDO, taskStoreList);
        }
        Long unifyTaskId = taskStoreDO.getUnifyTaskId();
        TaskParentDO parentDO = taskParentDao.selectById(enterpriseId, unifyTaskId);
        String storeItems = SysLogHelper.buildBatchContentItem(taskStoreList, TaskStoreDO::getStoreName, TaskStoreDO::getStoreId);
        return getTaskStoreContent(SysLogConstant.REALLOCATE, parentDO, storeItems);
    }

    /**
     * 问题工单重新分配
     */
    private String questionReallocate(String enterpriseId, SysLogDO sysLogDO, List<TaskStoreDO> taskStoreList) {
        List<TbQuestionRecordDO> records = questionRecordDao.getRecordByTaskStore(enterpriseId, taskStoreList);
        if (CollectionUtil.isEmpty(records)) {
            log.info("questionReallocate#工单记录为空");
            return null;
        }
        // 模块设置为工单管理
        sysLogDO.setMenus(OpModuleEnum.QUESTION.getMenus());
        sysLogDO.setModuleByMenus();
        String contentItems = records.stream()
                .map(v -> MessageFormat.format(QUESTION_SUB_ITEM_TEMPLATE, v.getParentQuestionName(), v.getParentQuestionId().toString(), v.getTaskName(), v.getId().toString()))
                .collect(Collectors.joining("、"));
        return SysLogHelper.buildContent(COMMON_TEMPLATE, SysLogConstant.REALLOCATE, contentItems);
    }

    /**
     * 巡店任务删除
     */
    private String patrolStoreTaskBatchDelete(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo()));
        String storeItems = jsonObject.getString("storeItems");
        Long unifyTaskId = jsonObject.getLong("unifyTaskId");
        TaskParentDO parentDO = taskParentDao.selectById(enterpriseId, unifyTaskId);
        if (Objects.isNull(parentDO)) {
            log.info("patrolStoreTaskBatchDelete#父任务不存在");
            return null;
        }
        return getTaskStoreContent(SysLogConstant.DELETE, parentDO, storeItems);
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
            case DELETE:
                return deletePreprocess(enterpriseId, reqParams);
            case PATROL_STORE_TASK_BATCH_DELETE:
                return patrolStoreTaskBatchDeletePreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    /**
     * DELETE前置操作逻辑
     */
    private String deletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        List<Long> unifyTaskIdList = jsonObject.getJSONArray("unifyTaskIdList").toJavaList(Long.class);
        List<TaskParentDO> taskParentList = taskParentDao.selectByIds(enterpriseId, unifyTaskIdList);
        if (CollectionUtil.isEmpty(taskParentList)) {
            log.info("deletePreprocess#父任务为空");
            return null;
        }
        String taskType = TaskTypeEnum.getTaskTypeByCode(taskParentList.get(0).getTaskType());
        String result = SysLogHelper.buildBatchContentItem(taskParentList, TaskParentDO::getTaskName);
        return SysLogHelper.buildContent(DELETE_TEMPLATE2, taskType, result);
    }

    /**
     * PATROL_STORE_TASK_BATCH_DELETE前置操作逻辑
     */
    private String patrolStoreTaskBatchDeletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        TbDisplayDeleteParam request = jsonObject.getObject("displayDeleteParam", TbDisplayDeleteParam.class);
        List<Long> taskStoreIds = CollectionUtil.isNotEmpty(request.getIds()) ? request.getIds() : ListUtil.toList(request.getTaskStoreId());
        if (CollectionUtil.isEmpty(taskStoreIds)) {
            log.info("patrolStoreTaskBatchDeletePreprocess#taskStoreIds为空");
            return null;
        }
        List<TaskStoreDO> taskStoreList = taskStoreMapper.listByUnifyIds(enterpriseId, taskStoreIds, null);
        if (CollectionUtil.isEmpty(taskStoreList)) {
            log.info("patrolStoreTaskBatchDeletePreprocess#门店任务为空");
            return null;
        }
        String storeItems = SysLogHelper.buildBatchContentItem(taskStoreList, TaskStoreDO::getStoreName, TaskStoreDO::getStoreId);
        Long unifyTaskId = taskStoreList.get(0).getUnifyTaskId();
        JSONObject result = new JSONObject();
        result.put("storeItems", storeItems);
        result.put("unifyTaskId", unifyTaskId);
        return result.toJSONString();
    }

    /**
     * 门店任务操作结果处理
     */
    private String getTaskStoreContent(String prefix, TaskParentDO parentDO, String storeItems) {
        String taskType = TaskTypeEnum.getTaskTypeByCode(parentDO.getTaskType());
        String taskBeginTime = DateUtil.format(parentDO.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        String taskEndTime = DateUtil.format(parentDO.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        String timeStr = taskBeginTime + " - " + taskEndTime;
        return SysLogHelper.buildContent(TASK_STORE_TEMPLATE, prefix, taskType, parentDO.getTaskName(), timeStr, storeItems);
    }
}
