package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySuToDoDTO;

import java.util.List;

/**
 * 子任务
 * @author zhangnan
 * @date 2022-04-15 11:18
 */
public interface UnifyTaskSubService {

    TaskSubDO insertTaskSub(String enterpriseId, String userId, TaskParentDO taskParentDO);

    void updateSubStatusBySubTaskId(String enterpriseId, String subStatus, Long subTaskId);
}
