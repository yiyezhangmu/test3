package com.coolcollege.intelligent.facade.unifytask;

import com.coolcollege.intelligent.facade.dto.unifytask.TaskSubDTO;
import com.coolstore.base.dto.ResultDTO;

/**
 * 统一子任务RPC接口
 * @author zhangnan
 * @date 2021-11-25 15:18
 */
public interface TaskSubFacade {

    /**
     * 查询处理人已完成的任务
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @param taskType 任务类型
     * @param unifyTaskId 父任务id
     * @return TaskSubDTO
     */
    ResultDTO<TaskSubDTO> getHandlerCompletedSubTask(String enterpriseId, String storeId, String taskType, Long unifyTaskId);
}
