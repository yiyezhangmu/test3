package com.coolcollege.intelligent.service.unifytask.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.model.enums.TaskStatusEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.enums.UnifyTaskActionEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySuToDoDTO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskSubService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhangnan
 * @date 2022-04-15 11:20
 */
@Service
public class UnifyTaskSubServiceImpl implements UnifyTaskSubService {

    @Resource
    private TaskSubDao taskSubDao;
    @Resource
    private AuthVisualService visualService;

    @Override
    public TaskSubDO insertTaskSub(String enterpriseId, String userId, TaskParentDO taskParentDO) {
        TaskSubDO taskSubDO = new TaskSubDO();
        taskSubDO.setUnifyTaskId(taskParentDO.getId());
        taskSubDO.setHandleUserId(userId);
        taskSubDO.setCreateUserId(taskParentDO.getCreateUserId());
        taskSubDO.setCreateTime(System.currentTimeMillis());
        taskSubDO.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
        taskSubDO.setSubStatus(UnifyStatus.ONGOING.getCode());
        taskSubDO.setCycleCount(taskParentDO.getLoopCount());
        taskSubDO.setLoopCount(taskParentDO.getLoopCount());
        taskSubDO.setSubTaskCode(taskParentDO.getId().toString());
        taskSubDO.setSubBeginTime(taskParentDO.getBeginTime());
        taskSubDO.setSubEndTime(taskParentDO.getEndTime());
        taskSubDO.setTaskType(taskParentDO.getTaskType());
        taskSubDO.setHandlerEndTime(new Date(taskParentDO.getEndTime()));
        taskSubDO.setIsOperateOverdue(taskParentDO.getIsOperateOverdue());
        taskSubDao.insertTaskSub(enterpriseId, taskSubDO);
        return taskSubDO;
    }

    @Override
    public void updateSubStatusBySubTaskId(String enterpriseId, String subStatus, Long subTaskId) {
        Long time = System.currentTimeMillis();
        TaskSubDO subDO = TaskSubDO.builder()
                .id(subTaskId)
                .actionKey(UnifyTaskActionEnum.PASS.getCode())
                .handleTime(time)
                .subStatus(subStatus)
                .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                .taskData(null)
                .remark(null)
                .nodeNo(UnifyNodeEnum.END_NODE.getCode())
                .build();
        taskSubDao.updateSubDetailById(enterpriseId, subDO);
    }
}
