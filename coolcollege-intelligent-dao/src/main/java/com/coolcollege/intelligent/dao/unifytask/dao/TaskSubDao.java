package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySuToDoDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 统一子任务表
 * @author zhangnan
 * @date 2021-12-14 15:43
 */
@Repository
public class TaskSubDao {

    @Resource
    private TaskSubMapper taskSubMapper;

    /**
     * 查询处理人已完成的任务
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @param taskType 任务类型
     * @param unifyTaskId 父任务id
     * @return TaskSubDO
     */
    public TaskSubDO selectHandlerCompletedSubTask(String enterpriseId, String storeId, String taskType, Long unifyTaskId) {
        // 门店id，任务类型，父任务id其中一个参数为空都直接返回null
        if(StringUtils.isBlank(storeId) || StringUtils.isBlank(taskType) || Objects.isNull(unifyTaskId)) {
            return null;
        }
        return taskSubMapper.selectHandlerCompletedSubTask(enterpriseId, storeId, taskType, unifyTaskId);
    }

    /**
     * 创建子任务
     * @param enterpriseId
     * @param taskSubDO
     */
    public void insertTaskSub(String enterpriseId, TaskSubDO taskSubDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskSubDO)) {
            return;
        }
        taskSubMapper.insertTaskSub(enterpriseId, taskSubDO);
    }

    /**
     * 根据子任务id查询子任务
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    public TaskSubDO selectBySubTaskId(String enterpriseId, Long subTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subTaskId)) {
            return null;
        }
        return taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
    }

    public void updateSubDetailById(String enterpriseId, TaskSubDO taskSubDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskSubDO)) {
            return;
        }
        taskSubMapper.updateSubDetailById(enterpriseId, taskSubDO);
    }

    /**
     * 获取父任务的待处理用户
     * @param enterpriseId
     * @param unifyTaskId
     * @param loopCount
     * @return
     */
    public List<String> getPendingUserByUnifyTaskId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount){
        if(StringUtils.isBlank(enterpriseId) || unifyTaskId == null){
            return Lists.newArrayList();
        }
        return taskSubMapper.getPendingUserByUnifyTaskId(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    /**
     * 获取任务数量
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    public UnifySuToDoDTO getCountByUnifyTaskId(String enterpriseId, Long unifyTaskId, String handleUserId){
        return taskSubMapper.getCountByUnifyTaskId(enterpriseId, unifyTaskId, handleUserId);
    }

    public Integer batchInsertTaskSub(String enterpriseId, List<TaskSubDO> taskSubList){
        if(CollectionUtils.isEmpty(taskSubList)){
            return Constants.ZERO;
        }
        return taskSubMapper.batchInsertTaskSub(enterpriseId, taskSubList);
    }

    /**
     * 查询最新的一条
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param storeId 门店id
     * @param loopCount 循环任务的循环批次
     * @param userId 用户id
     * @param subStatus 子任务状态
     * @param nodeNo 节点编号
     * @return 子任务VO
     */
    public TaskSubVO getLatestSubId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount, String userId, String subStatus, String nodeNo) {
        return taskSubMapper.getLatestSubId(enterpriseId, unifyTaskId, storeId, loopCount, userId, subStatus, nodeNo);
    }
}
