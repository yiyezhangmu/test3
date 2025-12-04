package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 父任务
 * @author zhangnan
 * @date 2021-12-23 19:13
 */
@Repository
public class TaskParentDao {

    @Resource
    private TaskParentMapper taskParentMapper;

    /**
     * 根据父任务id列表查询
     * @param enterpriseId
     * @param ids
     * @return
     */
    public List<TaskParentDO> selectByIds(String enterpriseId, List<Long> ids){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return taskParentMapper.selectTaskByIds(enterpriseId, ids);
    }

    /**
     * 根据父任务id查询
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    public TaskParentDO selectById(String enterpriseId, Long unifyTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId)) {
            return null;
        }
        return taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
    }

    /**
     * 新增父任务
     * @param enterpriseId
     * @param taskParentDO
     */
    public void insertTaskParent(String enterpriseId, TaskParentDO taskParentDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskParentDO)) {
            return;
        }
        taskParentMapper.insertTaskParent(enterpriseId, taskParentDO);
    }

    /**
     * 编辑督导父任务
     * @param enterpriseId
     * @param taskParentDO
     */
    public void updateSuperVisionParent(String enterpriseId, TaskParentDO taskParentDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskParentDO)) {
            return;
        }
        taskParentMapper.updateSuperVisionParent(enterpriseId, taskParentDO);
    }

    /**
     * 查询父任务（按人任务）
     * @param enterpriseId
     * @param request
     * @return
     */
    public PageInfo<TaskParentDO> selectTaskParentForPerson(String enterpriseId, GetTaskByPersonRequest request) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(request)) {
            return new PageInfo<>();
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return new PageInfo<TaskParentDO>(taskParentMapper.selectTaskParentForPerson(enterpriseId, request));
    }

    public void updateParentStatusByTaskId(String enterpriseId, String parentStatus, Long taskId) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(parentStatus)|| Objects.isNull(taskId)) {
            return;
        }
        taskParentMapper.updateParentStatusByTaskId(enterpriseId, parentStatus, taskId);
    }


    public void updateScheduleId(String enterpriseId, TaskParentDO taskParentDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskParentDO)) {
            return;
        }
        taskParentMapper.updateParentTaskById(enterpriseId, taskParentDO, taskParentDO.getId());
    }

    public TaskParentDO getByExtraParam(String enterpriseId, String extraParam) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(extraParam)) {
            return null;
        }
        return taskParentMapper.getByExtraParam(enterpriseId, extraParam);
    }

    public void updateLoopCountById(String enterpriseId, Long taskId, Long loopCount) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskId)) {
            return;
        }
        taskParentMapper.updateLoopCountById(enterpriseId, loopCount, taskId);
    }

}
