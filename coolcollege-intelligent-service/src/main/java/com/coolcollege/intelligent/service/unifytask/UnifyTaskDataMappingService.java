package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;

import java.util.List;
import java.util.Map;


public interface UnifyTaskDataMappingService {

    void insertDataTaskMappingNew(String enterpriseId, UnifyTaskBuildDTO task, Long taskId);

    /**
     * 新增任务关联数据
     * @param enterpriseId
     * @param taskParentDO
     * @param form
     */
    void insertDataTaskMappingNew(String enterpriseId, TaskParentDO taskParentDO, List<GeneralDTO> form);

    /**
     * 获取按人任务的关联数据
     * @param enterpriseId
     * @param taskIds
     */
    Map<Long, List<GeneralDTO>> getTaskDataMappingMap(String enterpriseId, List<Long> taskIds);
}
