package com.coolcollege.intelligent.facade.unifytask.impl;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskSubDao;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.unifytask.TaskSubDTO;
import com.coolcollege.intelligent.facade.unifytask.TaskSubFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 统一子任务rpc接口实现
 * @author zhangnan
 * @date 2021-12-14 16:35
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.TASK_SUB_FACADE_FACADE_UNIQUE_ID ,interfaceType = TaskSubFacade.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class TaskSubFacadeImpl implements TaskSubFacade {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private TaskSubDao taskSubDao;

    @Override
    public ResultDTO<TaskSubDTO> getHandlerCompletedSubTask(String enterpriseId, String storeId, String taskType, Long unifyTaskId) {
        // 根据企业id切库
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        String dbName = enterpriseConfigDO.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return ResultDTO.successResult(this.parasTaskSubDoToDto(taskSubDao.selectHandlerCompletedSubTask(enterpriseId,storeId,taskType,unifyTaskId)));
    }

    private TaskSubDTO parasTaskSubDoToDto(TaskSubDO subDO) {
        if(subDO == null) {
            return null;
        }
        TaskSubDTO subDTO = new TaskSubDTO();
        subDTO.setId(subDO.getId());
        subDTO.setUnifyTaskId(subDO.getUnifyTaskId());
        subDTO.setCreateUserId(subDO.getCreateUserId());
        subDTO.setHandleUserId(subDO.getHandleUserId());
        subDTO.setHandleUserName(subDO.getHandleUserName());
        subDTO.setCreateTime(subDO.getCreateTime());
        subDTO.setHandleTime(subDO.getHandleTime());
        subDTO.setActionKey(subDO.getActionKey());
        subDTO.setRemark(subDO.getRemark());
        subDTO.setStoreId(subDO.getStoreId());
        subDTO.setNodeNo(subDO.getNodeNo());
        subDTO.setInstanceId(subDO.getInstanceId());
        subDTO.setTemplateId(subDO.getTemplateId());
        subDTO.setSubStatus(subDO.getSubStatus());
        subDTO.setCycleCount(subDO.getCycleCount());
        subDTO.setBizCode(subDO.getBizCode());
        subDTO.setCid(subDO.getCid());
        subDTO.setParentTurnSubId(subDO.getParentTurnSubId());
        subDTO.setFlowState(subDO.getFlowState());
        subDTO.setGroupItem(subDO.getGroupItem());
        subDTO.setLoopCount(subDO.getLoopCount());
        subDTO.setTurnUserId(subDO.getTurnUserId());
        subDTO.setSubTaskCode(subDO.getSubTaskCode());
        subDTO.setTaskData(subDO.getTaskData());
        subDTO.setSubBeginTime(subDO.getSubBeginTime());
        subDTO.setSubEndTime(subDO.getSubEndTime());
        subDTO.setStoreArea(subDO.getStoreArea());
        subDTO.setTaskType(subDO.getTaskType());
        subDTO.setRegionId(subDO.getRegionId());
        subDTO.setStoreName(subDO.getStoreName());
        subDTO.setHandlerEndTime(subDO.getHandlerEndTime());
        return subDTO;
    }
}
