package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统一门店任务表
 * @author zhangnan
 * @date 2021-12-27 15:08
 */
@Repository
public class TaskStoreDao {

    @Resource
    private TaskStoreMapper taskStoreMapper;

    /**
     * 根据父任务id和时间查询门店任务数量
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param startTime 创建时间开始
     * @param endTime 创建时间结束
     * @return 门店任务数量
     */
    public Integer countByUnifyTaskIdAndTime(String enterpriseId, Long unifyTaskId, Long startTime, Long endTime) {
        // 门店id，任务类型，父任务id其中一个参数为空都直接返回null
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId) || Objects.isNull(startTime)
                || Objects.isNull(endTime)) {
            return null;
        }
        return taskStoreMapper.countByUnifyTaskIdAndTime(enterpriseId, unifyTaskId, new Date(startTime), new Date(endTime));
    }

    /**
     * 根据id查询
     * @param enterpriseId 企业id
     * @param id 门店任务id
     * @return TaskStoreDO
     */
    public TaskStoreDO selectById(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return taskStoreMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     * 根据任务id查询工单门店任务
     * @param enterpriseId 企业id
     * @param unifyTaskIds 父任务id列表
     * @return List<TaskStoreDO>
     */
    public List<TaskStoreDO> selectQuestionTaskByTaskIds(String enterpriseId, List<Long> unifyTaskIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(unifyTaskIds)) {
            return null;
        }
        return taskStoreMapper.listByUnifyTaskIds(enterpriseId, unifyTaskIds, null);
    }


    public int delTaskStoreById(String enterpriseId, Long id){
       return taskStoreMapper.delTaskStoreById(enterpriseId, id);
    }

    /**
     * 根据任务id查询工单门店任务
     * @param enterpriseId 企业id
     * @param ids 门店任务id列表
     * @return List<TaskStoreDO>
     */
    public List<TaskStoreDO> selectQuestionTaskByIds(String enterpriseId, List<Long> ids, String nodeNo) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return taskStoreMapper.listByUnifyIds(enterpriseId, ids, nodeNo);
    }

    /**
     * 获取门店任务抄送人
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @return
     */
    public List<String> getTaskStoreCCUserIds(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount){
        if(StringUtils.isAnyBlank(enterpriseId, storeId) || Objects.isNull(unifyTaskId) || Objects.isNull(loopCount)){
            return Lists.newArrayList();
        }
        TaskStoreDO taskStore = taskStoreMapper.getTaskStore(enterpriseId, unifyTaskId, storeId, loopCount);
        if(Objects.isNull(taskStore) || StringUtils.isBlank(taskStore.getCcUserIds())){
            return Lists.newArrayList();
        }
        return Arrays.stream(StringUtils.split(taskStore.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public TaskStoreDO getTaskStoreDetail(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount){
        if(StringUtils.isBlank(enterpriseId)){
            return null;
        }
        return taskStoreMapper.getTaskStore(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    /**
     * 批量新增门店任务
     * @param enterpriseId
     * @param storeTaskList
     * @return
     */
    public Integer batchAddTaskStore(String enterpriseId, List<TaskStoreDO> storeTaskList){
        if(CollectionUtils.isEmpty(storeTaskList)){
            return null;
        }
        return taskStoreMapper.batchAddTaskStore(enterpriseId, storeTaskList);
    }

    public Integer addTaskStore(String enterpriseId, TaskStoreDO taskStore){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskStore)){
            return null;
        }
        return taskStoreMapper.batchAddTaskStore(enterpriseId, Arrays.asList(taskStore));
    }

    /**
     * 根据门店任务id更新 门店任务节点信息 清空处理人
     * @param enterpriseId 企业id
     * @param id 门店任务id
     * @param extendInfo 门店任务扩展信息
     * @param ccUserIds 抄送人id集合
     * @param clearHandlerUser 是否清空处理人
     */
    public void updateExtendAndCcInfoAndClearHandlerUser(String enterpriseId, Long id, String extendInfo, String ccUserIds, boolean clearHandlerUser) {
        taskStoreMapper.updateExtendAndCcInfoAndClearHandlerUser(enterpriseId, id, extendInfo, ccUserIds, clearHandlerUser);
    }
}
