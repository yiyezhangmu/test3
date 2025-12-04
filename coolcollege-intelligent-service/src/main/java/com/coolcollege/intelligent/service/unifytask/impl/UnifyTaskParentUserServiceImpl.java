package com.coolcollege.intelligent.service.unifytask.impl;
import java.util.Date;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentUserDao;
import com.coolcollege.intelligent.model.enums.TaskStatusEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentUserDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskParentUserSaveDTO;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 父任务处理人
 * @author zhangnan
 * @date 2022-02-23 10:49
 */
@Service
public class UnifyTaskParentUserServiceImpl implements UnifyTaskParentUserService {

    @Resource
    private UnifyTaskParentUserDao unifyTaskParentUserDao;
    @Resource
    private TaskParentDao taskParentDao;

    @Override
    public void batchInsertOrUpdate(TaskParentUserSaveDTO saveDTO) {
        if(CollectionUtils.isEmpty(saveDTO.getUserIds()) || Objects.isNull(saveDTO.getUnifyTaskId())) {
            return;
        }
        TaskParentDO parentDO = taskParentDao.selectById(saveDTO.getEnterpriseId(), saveDTO.getUnifyTaskId());
        List<UnifyTaskParentUserDO> userDOList = Lists.newArrayList();
        for (String userId : saveDTO.getUserIds()) {
            UnifyTaskParentUserDO userDO = new UnifyTaskParentUserDO();
            userDO.setUserId(userId);
            userDO.setUnifyTaskId(saveDTO.getUnifyTaskId());
            userDO.setTaskName(parentDO.getTaskName());
            userDO.setTaskType(parentDO.getTaskType());
            userDO.setBeginTime(parentDO.getBeginTime());
            userDO.setEndTime(parentDO.getEndTime());
            userDO.setParentStatus(TaskStatusEnum.ONGOING.getCode());
            userDO.setParentCreateTime(parentDO.getCreateTime());
            userDO.setUpdateTime(new Date());
            userDOList.add(userDO);
        }
        ListUtils.partition(userDOList, Constants.BATCH_INSERT_COUNT).forEach(list ->
                unifyTaskParentUserDao.batchInsertOrUpdate(saveDTO.getEnterpriseId() ,list));
    }
}
