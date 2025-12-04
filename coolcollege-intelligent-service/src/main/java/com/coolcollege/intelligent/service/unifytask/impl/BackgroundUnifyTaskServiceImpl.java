package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.util.ObjectUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.ParentTaskDTO;
import com.coolcollege.intelligent.model.unifytask.dto.SubTaskDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.unifytask.BackgroundUnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDisplayService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 16:57
 */
@Service
@Slf4j
public class BackgroundUnifyTaskServiceImpl implements BackgroundUnifyTaskService {
    @Autowired
    private UnifyTaskDisplayService displayService;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;

    @Override
    public ParentTaskDTO getBackgroundParentList(String enterpriseId, DisplayQuery query, CurrentUser user) {
        ParentTaskDTO parentTaskDTO = displayService.getDisplayParent(enterpriseId,query, user);
        return parentTaskDTO;
    }

    @Override
    public SubTaskDTO getBackgroundTaskSubStatisticsList(String enterpriseId, DisplayQuery query, CurrentUser user) {
        if(query.getUnifyTaskId() == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "父任务id【unifyTaskId】不能为空");
        }
        SubTaskDTO subDTO = displayService.getDisplaySub(enterpriseId, query, user);
        TaskParentDO parentDO =taskParentMapper.selectParentTaskByTaskId(enterpriseId,query.getUnifyTaskId());
        if(ObjectUtil.isNotEmpty(parentDO)){
            subDTO.setParent(displayService.getParentInfo(enterpriseId, Lists.newArrayList(parentDO)).get(0));
            if(query.getLoopCount() != null){
                TaskSubDO taskSubDO = taskSubMapper.getSubBeginTimeEndTimeByTaskIdAndLoopCount(enterpriseId, query.getUnifyTaskId(),query.getLoopCount());
                subDTO.setSubBeginTime(taskSubDO.getSubBeginTime());
                subDTO.setSubEndTime(taskSubDO.getSubEndTime());
            }
        }
        return subDTO;
    }
}
