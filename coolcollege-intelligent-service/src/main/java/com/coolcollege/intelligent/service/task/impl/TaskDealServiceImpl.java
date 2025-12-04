package com.coolcollege.intelligent.service.task.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.enums.UnifyTaskActionEnum;
import com.coolcollege.intelligent.model.task.param.DealParam;
import com.coolcollege.intelligent.model.task.param.DealTaskParam;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.task.TaskDealService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/14 15:31
 */
@Service
@Slf4j
public class TaskDealServiceImpl implements TaskDealService {

    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private MqMessageDAO mqMessageDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeal(String enterpriseId, List<DealParam> paramList, CurrentUser user) {
        paramList.forEach(a -> deal(enterpriseId, a, user, null, null));
    }

    @Override
    public void dealTask(String enterpriseId, DealTaskParam dealTaskParam, CurrentUser user) {
        Long loopCount = dealTaskParam.getLoopCount() == null ? Constants.INDEX_ONE.longValue() : dealTaskParam.getLoopCount();
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, dealTaskParam.getUnifyTaskId(), dealTaskParam.getStoreId(),
                loopCount);
        if (taskStoreDO == null) {
            log.info("任务不存在，eid:{},taskId:{},storeId:{}", enterpriseId, dealTaskParam.getUnifyTaskId(), dealTaskParam.getStoreId());
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        TaskSubDO oldSub = taskSubMapper.selectSubTaskByTaskIdAndStoreId(enterpriseId, dealTaskParam.getUnifyTaskId(), dealTaskParam.getStoreId(),
                loopCount, user.getUserId(), dealTaskParam.getNodeNo());

        if (oldSub == null) {
            log.info("任务不存在，taskId:{},storeId:{}", dealTaskParam.getUnifyTaskId(), dealTaskParam.getStoreId());
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_HANDLE);
        }
        DealParam dealParam = new DealParam();
        dealParam.setSubTaskId(oldSub.getId());
        dealParam.setRemark(dealTaskParam.getRemark());
        dealParam.setActiveKey(dealTaskParam.getActiveKey());
        dealParam.setData(dealTaskParam.getData());
        //处理操作
        deal(enterpriseId, dealParam, user, dealTaskParam.getHandleAction(), taskStoreDO.getId());
    }

    @Override
    public void batchDealQuestion(String enterpriseId, List<DealTaskParam> paramList, CurrentUser user) {
        paramList.forEach(a -> dealTask(enterpriseId, a, user));
    }


    /**
     * ##taskStep2
     * 有审批流无关联检查表的任务处理
     *
     * @param enterpriseId
     * @param param
     * @param user
     */
    private void deal(String enterpriseId, DealParam param, CurrentUser user, String handleAction, Long taskStoreId) {
        ValidateUtil.validateObj(UnifyTaskActionEnum.getByCode(param.getActiveKey()));
        // 提交节点校验
        Long subTaskId = param.getSubTaskId();
        TaskSubDO oldSub = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        Boolean flag = workflowService.subSubmitCheck(oldSub);
        if (flag == null || !flag) {
            log.info("该任务已被其他人操作，subTaskId:" + subTaskId);
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该任务已被其他人操作");
        }

        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getNodeNo(),
                oldSub.getGroupItem(), oldSub.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder()
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, subTaskId);
        //记录实际处理人
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(oldSub.getNodeNo())) {
            if (taskStoreId == null) {
                TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount());
                taskStoreId = taskStoreDO.getId();
            }
            taskStoreMapper.updatedHandlerUserByTaskStoreId(enterpriseId, taskStoreId, user.getUserId());
        }

        // 发送流程引擎
        WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, subTaskId, oldSub,
                UnifyTaskConstant.SELF_BIZCODE, param.getActiveKey(), null,
                user.getUserId(), param.getRemark(), param.getData(), handleAction);
        mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), subTaskId, JSONObject.toJSONString(workflowDataDTO));
        simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
    }
}
