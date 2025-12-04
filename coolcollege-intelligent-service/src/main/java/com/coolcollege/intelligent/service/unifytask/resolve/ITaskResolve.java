package com.coolcollege.intelligent.service.unifytask.resolve;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReissueDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: ITaskResolve
 * @Description:
 * @date 2025-01-07 10:47
 */
public interface ITaskResolve<T> {

    /**
     * 任务分解
     * @param enterpriseId
     * @param taskStore
     */
    boolean isFilterStoreTask(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore);

    /**
     * 任务分解
     * @param enterpriseId
     * @param taskStore
     */
    List<TaskSubDO> createTaskStoreAndSubTask(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting);

    /**
     * 获取业务数据
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @return
     */
    T getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount);

    /**
     * 补发
     * @param enterpriseId 企业id
     * @param oldTaskStore 原门店任务
     * @param taskParentDO 父任务
     * @param taskReissueDTO 任务补发DTO
     * @param isRefresh 是否刷新
     */
    Map<String, Object> taskReissue(String enterpriseId, TaskStoreDO oldTaskStore, TaskParentDO taskParentDO, TaskReissueDTO taskReissueDTO, boolean isRefresh);

}
