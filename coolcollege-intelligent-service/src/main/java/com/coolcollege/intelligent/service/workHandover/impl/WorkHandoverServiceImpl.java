package com.coolcollege.intelligent.service.workHandover.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.workHandover.WorkHandoverEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableDao;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskPersonDao;
import com.coolcollege.intelligent.dao.workHandover.dao.WorkHandoverDao;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubInfoVO;
import com.coolcollege.intelligent.model.workHandover.WorkHandoverDO;
import com.coolcollege.intelligent.model.workHandover.dto.WorkHandoverDTO;
import com.coolcollege.intelligent.model.workHandover.request.WorkHandoverRequest;
import com.coolcollege.intelligent.model.workHandover.vo.WorkHandoverVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.service.workHandover.WorkHandoverService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2022-11-17 11:34
 */
@Slf4j
@Service
public class WorkHandoverServiceImpl implements WorkHandoverService {

    @Resource
    private WorkHandoverDao workHandoverDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;

    @Resource
    private UnifyTaskService unifyTaskService;

    @Resource
    private QuestionParentUserMappingService questionParentUserMappingService;

    @Resource
    private UnifyTaskPersonDao unifyTaskPersonDao;

    @Resource
    private TbPatrolStoreRecordMapper patrolStoreRecordMapper;

    @Resource
    private SwStoreWorkDataTableDao swStoreWorkDataTableDao;

    @Override
    public PageInfo<WorkHandoverVO> list(String enterpriseId, String name, Integer pageNum, Integer pageSize) {
        DataSourceHelper.reset();
        PageHelper.startPage(pageNum, pageSize);
        List<WorkHandoverDO> workHandoverDOList = workHandoverDao.selectList(enterpriseId, name);
        PageInfo pageInfo = new PageInfo<>(workHandoverDOList);
        if (CollectionUtils.isEmpty(workHandoverDOList)) {
            return pageInfo;
        }
        List<WorkHandoverVO> workHandoverVOList = new ArrayList<>();
        workHandoverDOList.forEach(workHandoverDO -> {
            WorkHandoverVO workHandoverVO = new WorkHandoverVO();
            BeanUtils.copyProperties(workHandoverDO, workHandoverVO);
            if (StringUtils.isNotBlank(workHandoverDO.getHandoverContent())) {
                List<String> contentList = JSONObject.parseArray(workHandoverDO.getHandoverContent(), String.class);
                if (CollectionUtils.isNotEmpty(contentList)) {
                    List<String> contentNameList = new ArrayList<>();
                    contentList.forEach(content -> {
                        String contentName = WorkHandoverEnum.getNameByCode(content);
                        if (StringUtils.isNotBlank(contentName)) {
                            contentNameList.add(contentName);
                        }
                    });
                    workHandoverVO.setHandoverContentList(contentNameList);
                }
            }
            workHandoverVOList.add(workHandoverVO);
        });
        pageInfo.setList(workHandoverVOList);
        return pageInfo;
    }

    @Override
    public Long addWorkHandover(String enterpriseId, WorkHandoverRequest handoverRequest, String userId) {
        if (handoverRequest.getHandoverUserId().equals(handoverRequest.getTransferUserId())) {
            throw new ServiceException(ErrorCodeEnum.USER_ID_SAME);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        WorkHandoverDO workHandoverDO = new WorkHandoverDO();
        workHandoverDO.setTransferUserId(handoverRequest.getTransferUserId());
        workHandoverDO.setTransferUserName(enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, handoverRequest.getTransferUserId()));
        workHandoverDO.setHandoverUserId(handoverRequest.getHandoverUserId());
        workHandoverDO.setHandoverUserName(enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, handoverRequest.getHandoverUserId()));
        workHandoverDO.setHandoverContent(JSONObject.toJSONString(handoverRequest.getHandoverContentList()));
        workHandoverDO.setStatus(0);
        workHandoverDO.setCreateUserId(userId);
        workHandoverDO.setCreateUserName(enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, userId));
        workHandoverDO.setCreateTime(new Date());
        workHandoverDO.setEnterpriseId(enterpriseId);
        DataSourceHelper.reset();
        workHandoverDao.save(workHandoverDO);
        //发送消息，转交任务
        WorkHandoverDTO resultDTO = new WorkHandoverDTO(enterpriseId, workHandoverDO.getId());
        simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.WORK_HANDOVER_TASK);
        return workHandoverDO.getId();
    }

    @Override
    public void againWorkHandover(String eid, Long workHandoverId) {
        DataSourceHelper.reset();
        WorkHandoverDO workHandoverDO = workHandoverDao.selectById(workHandoverId);
        if (workHandoverDO == null) {
            throw new ServiceException(ErrorCodeEnum.WORK_HANDOVER_NOT_FIND);
        }
        if (!Constants.INDEX_TWO.equals(workHandoverDO.getStatus())){
            throw new ServiceException(ErrorCodeEnum.WORK_HANDOVER_NOT_AGAIN);
        }
        //发送消息，转交任务
        WorkHandoverDTO resultDTO = new WorkHandoverDTO(workHandoverDO.getEnterpriseId(), workHandoverDO.getId());
        simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.WORK_HANDOVER_TASK);
    }

    @Override
    public void beginWorkHandover(String enterpriseId, Long workHandoverId) {
        log.info("beginWorkHandover#开始交接#workHandoverId:{},eid:{}", workHandoverId, enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        WorkHandoverDO workHandoverDO = workHandoverDao.selectById(workHandoverId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (workHandoverDO.getStatus() == 1) {
            log.info("交接已完成,不在交接eid:{},workHandoverId:{}", enterpriseId, workHandoverId);
            return;
        }
        if (StringUtils.isBlank(workHandoverDO.getHandoverContent())) {
            log.info("交接内容为空,不再交接eid:{},workHandoverId:{}", enterpriseId, workHandoverId);
            return;
        }
        List<String> contentList = JSONObject.parseArray(workHandoverDO.getHandoverContent(), String.class);
        if (CollectionUtils.isEmpty(contentList)) {
            log.info("交接内容为空,不再交接eid:{},workHandoverId:{}", enterpriseId, workHandoverId);
            return;
        }
        int status = 1;
        try {
            //依次交接任务
            for (String taskType : contentList) {
                WorkHandoverEnum workHandoverEnum = WorkHandoverEnum.getByCode(taskType);
                if (workHandoverEnum == null) {
                    continue;
                }
                switch (workHandoverEnum) {
                    case PATROL_STORE_ONLINE:
                    case PATROL_STORE_OFFLINE:
                    case DISPLAY_TASK:
                    case QUESTION_ORDER:
                        handoverTask(enterpriseId, workHandoverDO, workHandoverEnum);
                        break;
                    case PATROL_STORE_PLAN:
                        handoverPlan(enterpriseId, workHandoverDO);
                        break;
                    case STORE_WORK:
                        handoverStoreWork(enterpriseId, workHandoverDO);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            status = 2;
            log.error("beginWorkHandover#交接失败#workHandoverId:{},eid:{}", workHandoverId, enterpriseId, e);
        } finally {
            log.info("beginWorkHandover#交接结束#workHandoverId:{},eid:{},status:{}", workHandoverId, enterpriseId, status);
            workHandoverDO.setStatus(status);
            workHandoverDO.setCompleteTime(new Date());
            DataSourceHelper.reset();
            workHandoverDao.updateById(workHandoverDO);
        }
    }


    private String toStringByUserIdList(List<String> userIdList){
        return Constants.COMMA + String.join(Constants.COMMA, userIdList) + Constants.COMMA;
    }
    /**
     * 处理门店任务和子任务
     */
    private void handoverTask(String enterpriseId, WorkHandoverDO workHandoverDO, WorkHandoverEnum workHandoverEnum) {
        int pageSize = 1000;
        int maxSize = 10000;
        long pages = (maxSize + pageSize - 1) / pageSize;
        for (int curPage = 1; curPage <= pages; curPage++) {
            List<TaskStoreDO> taskStoreDOList = elasticSearchService.getTaskStoreWorkList(enterpriseId, workHandoverDO.getTransferUserId(),
                    workHandoverEnum.getCode(), curPage, pageSize);
            log.info("handoverTask#eid:{},transferUserId:{},workHandoverEnum:{},size:{}", enterpriseId, workHandoverDO.getTransferUserId(), workHandoverEnum.getCode(), taskStoreDOList.size());
            if (CollectionUtils.isEmpty(taskStoreDOList)) {
                return;
            }
            taskStoreDOList.forEach(taskStoreDO -> {

                //替换相应节点人员
                String extendInfo = taskStoreDO.getExtendInfo().replaceAll("," + workHandoverDO.getTransferUserId() + ",", "," + workHandoverDO.getHandoverUserId() + ",");
                taskStoreDO.setExtendInfo(extendInfo);
                Map<String, List<String>> nodeMap = unifyTaskStoreService.getNodePersonByTaskStore(taskStoreDO);
                JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
                nodeMap.remove(UnifyNodeEnum.ZERO_NODE.getCode());
                nodeMap.remove(UnifyNodeEnum.CC.getCode());
                for (Map.Entry<String, List<String>> entry : nodeMap.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    List<String> newUserIdList = value.stream().distinct().collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(value)) {
                        extendInfoJsonObj.put(key, toStringByUserIdList(newUserIdList));
                    }
                }
                //替换人员
                extendInfo = extendInfoJsonObj.toJSONString();
                taskStoreDO.setExtendInfo(extendInfo);
                taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), extendInfo, null, null);
                //转交人没有正在处理的任务,不在处理
                TaskSubDO handleSub = taskSubMapper.selectSubTaskByTaskIdAndStoreId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                        workHandoverDO.getTransferUserId(), taskStoreDO.getNodeNo());
                if (handleSub == null) {
                    //工单需要单独处理待办
                    if (WorkHandoverEnum.QUESTION_ORDER.getCode().equals(workHandoverEnum.getCode())) {
                        questionParentUserMappingService.updateUserMapping(enterpriseId, taskStoreDO.getUnifyTaskId(), Collections.singletonList(workHandoverDO.getTransferUserId()), null);
                    }
                    log.info("handoverTask#eid:{},transferUserId:{},workHandoverEnum:{}，转交人没有正在处理的任务,不在处理", enterpriseId, workHandoverDO.getTransferUserId(), workHandoverEnum.getCode());
                    return;
                }
                TaskSubInfoVO oldSub = taskSubMapper.selectTaskBySubId(enterpriseId, handleSub.getId());
                // 保存转交人到父任务处理人表中
                unifyTaskService.saveTaskParentUser(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getTaskType(), Lists.newArrayList(workHandoverDO.getHandoverUserId()));
                //取消移交人的任务,修改原任务已完成
                TaskSubDO subDO = TaskSubDO.builder()
                        .id(oldSub.getId())
                        .handleTime(System.currentTimeMillis())
                        .subStatus(UnifyStatus.COMPLETE.getCode())
                        .actionKey(DisplayConstant.ActionKeyConstant.TURN)
                        .turnUserId(workHandoverDO.getHandoverUserId())
                        .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                        .build();
                taskSubMapper.updateSubDetailById(enterpriseId, subDO);
                //交接人任务
                TaskSubDO handoverUserTaskSubDO = taskSubMapper.selectSubTaskByTaskIdAndStoreId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                        workHandoverDO.getHandoverUserId(), taskStoreDO.getNodeNo());
                if (handoverUserTaskSubDO == null) {
                    //添加交接人的任务
                    TaskSubDO newSubDO = TaskSubDO.builder()
                            .unifyTaskId(oldSub.getUnifyTaskId())
                            .createUserId(oldSub.getCreateUserId())
                            .createTime(System.currentTimeMillis())
                            .handleUserId(workHandoverDO.getHandoverUserId())
                            .storeId(oldSub.getStoreId())
                            .bizCode(oldSub.getBizCode())
                            .cid(oldSub.getCid())
                            .instanceId(oldSub.getInstanceId())
                            .cycleCount(oldSub.getCycleCount())
                            .nodeNo(oldSub.getNodeNo())
                            .templateId(oldSub.getTemplateId())
                            .subStatus(UnifyStatus.ONGOING.getCode())
                            .parentTurnSubId(oldSub.getId())
                            .flowState(UnifyTaskConstant.FLOW_INIT)
                            .groupItem(oldSub.getGroupItem())
                            .loopCount(oldSub.getLoopCount())
                            .subTaskCode(StringUtils.join(oldSub.getUnifyTaskId(), Constants.MOSAICS, oldSub.getStoreId()))
                            .taskData(oldSub.getTaskData())
                            .subBeginTime(oldSub.getSubBeginTime())
                            .subEndTime(oldSub.getSubEndTime())
                            .taskType(oldSub.getTaskType())
                            .storeArea(oldSub.getStoreArea())
                            .storeName(oldSub.getStoreName())
                            .handlerEndTime(oldSub.getHandlerEndTime())
                            .regionId(oldSub.getRegionId())
                            .build();
                    taskSubMapper.insertTaskSub(enterpriseId, newSubDO);
                }
                //工单需要单独处理待办
                if (WorkHandoverEnum.QUESTION_ORDER.getCode().equals(workHandoverEnum.getCode())) {
                    questionParentUserMappingService.updateUserMapping(enterpriseId, taskStoreDO.getUnifyTaskId(),
                            Collections.singletonList(workHandoverDO.getTransferUserId()), Collections.singletonList(workHandoverDO.getHandoverUserId()));

                }
            });
        }
    }

    /**
     * 处理巡店计划
     */
    private void handoverPlan(String enterpriseId, WorkHandoverDO workHandoverDO) {
        GetMiddlePageDataByPersonRequest request = new GetMiddlePageDataByPersonRequest();
        request.setUserIds(Collections.singletonList(workHandoverDO.getTransferUserId()));
        request.setSubStatus(UnifyStatus.ONGOING.getCode());
        int pageSize = 100;
        int maxSize = 10000;
        long pages = (maxSize + pageSize - 1) / pageSize;
        for (int curPage = 1; curPage <= pages; curPage++) {
            request.setPageNum(curPage);
            request.setPageSize(pageSize);
            List<UnifyTaskPersonDO> taskPersonDOList = unifyTaskPersonDao.selectList(enterpriseId, UnifyStatus.ONGOING.getCode(), workHandoverDO.getTransferUserId());
            if (CollectionUtils.isEmpty(taskPersonDOList)) {
                return;
            }
            taskPersonDOList.forEach(unifyTaskPersonDO -> {
                //取消移交人的任务,修改原任务已完成
                TaskSubDO subDO = TaskSubDO.builder()
                        .id(unifyTaskPersonDO.getSubTaskId())
                        .handleTime(System.currentTimeMillis())
                        .subStatus(UnifyStatus.COMPLETE.getCode())
                        .actionKey(DisplayConstant.ActionKeyConstant.TURN)
                        .turnUserId(workHandoverDO.getHandoverUserId())
                        .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                        .build();
                taskSubMapper.updateSubDetailById(enterpriseId, subDO);
                //
                UnifyTaskPersonDO currUnifyTaskPerson = unifyTaskPersonDao.selectByUserIdAndLoopCount(enterpriseId, unifyTaskPersonDO.getTaskId(), workHandoverDO.getHandoverUserId(),
                        unifyTaskPersonDO.getLoopCount());
                if (currUnifyTaskPerson != null) {
                    //删除移交人任务
                    unifyTaskPersonDao.deleteByPrimaryKey(enterpriseId, unifyTaskPersonDO.getId());
                    log.info("unifyTaskPerson#已有任务不在交接,eid:{},subTaskId:{}", enterpriseId, unifyTaskPersonDO.getSubTaskId());
                    return;
                }
                TaskSubInfoVO oldSub = taskSubMapper.selectTaskBySubId(enterpriseId, unifyTaskPersonDO.getSubTaskId());
                if(oldSub == null){
                    log.info("unifyTaskPerson#子任务不存在,eid:{},subTaskId:{}", enterpriseId, unifyTaskPersonDO.getSubTaskId());
                    return;
                }

                //添加交接人的任务
                TaskSubDO newSubDO = TaskSubDO.builder()
                        .unifyTaskId(oldSub.getUnifyTaskId())
                        .createUserId(oldSub.getCreateUserId())
                        .createTime(System.currentTimeMillis())
                        .handleUserId(workHandoverDO.getHandoverUserId())
                        .storeId(oldSub.getStoreId())
                        .bizCode(oldSub.getBizCode())
                        .cid(oldSub.getCid())
                        .instanceId(oldSub.getInstanceId())
                        .cycleCount(oldSub.getCycleCount())
                        .nodeNo(oldSub.getNodeNo())
                        .templateId(oldSub.getTemplateId())
                        .subStatus(UnifyStatus.ONGOING.getCode())
                        .parentTurnSubId(oldSub.getId())
                        .flowState(UnifyTaskConstant.FLOW_INIT)
                        .groupItem(oldSub.getGroupItem())
                        .loopCount(oldSub.getLoopCount())
                        .subTaskCode(StringUtils.join(oldSub.getUnifyTaskId(), Constants.MOSAICS, oldSub.getStoreId()))
                        .taskData(oldSub.getTaskData())
                        .subBeginTime(oldSub.getSubBeginTime())
                        .subEndTime(oldSub.getSubEndTime())
                        .taskType(oldSub.getTaskType())
                        .storeArea(oldSub.getStoreArea())
                        .storeName(oldSub.getStoreName())
                        .handlerEndTime(oldSub.getHandlerEndTime())
                        .regionId(oldSub.getRegionId())
                        .build();
                taskSubMapper.insertTaskSub(enterpriseId, newSubDO);
                unifyTaskPersonDao.updateTaskPersonById(enterpriseId, newSubDO.getId(), workHandoverDO.getHandoverUserId(), unifyTaskPersonDO.getId());
                //订正巡店记录
                List<TbPatrolStoreRecordDO> patrolStoreRecordDOList = patrolStoreRecordMapper.selectIdByTaskLoopCountAndSupervisorId(enterpriseId, unifyTaskPersonDO.getTaskId(),
                        workHandoverDO.getTransferUserId(), unifyTaskPersonDO.getLoopCount());
                if (CollectionUtils.isNotEmpty(patrolStoreRecordDOList)) {
                    List<Long> recordIdList = patrolStoreRecordDOList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
                    ListUtils.partition(recordIdList, Constants.BATCH_INSERT_COUNT).forEach(idList -> {
                        patrolStoreRecordMapper.updateSupervisorIdByIds(enterpriseId, idList, workHandoverDO.getHandoverUserId());
                    });
                }
            });
        }
    }

    /**
     * 处理店务
     */
    private void handoverStoreWork(String enterpriseId, WorkHandoverDO workHandoverDO) {
        //需要转交的检查表(执行人)
        List<SwStoreWorkDataTableDO> storeWorkDataTableDOList = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByUserId(enterpriseId, workHandoverDO.getTransferUserId());
        if (CollectionUtils.isEmpty(storeWorkDataTableDOList)) {
            return;
        }
        List<SwStoreWorkDataTableDO> result = new ArrayList<>();
        storeWorkDataTableDOList.forEach(x -> {
            String handleUserIds = x.getHandleUserIds();
            String newHandleUserId = null;
            String handleOverUserIdComma = String.format("%s%s%s", Constants.COMMA, workHandoverDO.getHandoverUserId(), Constants.COMMA);
            String transferUserIdComma = String.format("%s%s%s", Constants.COMMA, workHandoverDO.getTransferUserId(), Constants.COMMA);
            if(x.getHandleUserIds().contains(handleOverUserIdComma)){
                newHandleUserId = handleUserIds.replace(transferUserIdComma, Constants.COMMA);
            }else {
                newHandleUserId = handleUserIds.replace(transferUserIdComma, handleOverUserIdComma);
            }
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = new SwStoreWorkDataTableDO();
            swStoreWorkDataTableDO.setUpdateTime(new Date());
            swStoreWorkDataTableDO.setUpdateUserId(workHandoverDO.getTransferUserId());
            swStoreWorkDataTableDO.setHandleUserIds(newHandleUserId);
            swStoreWorkDataTableDO.setId(x.getId());
            result.add(swStoreWorkDataTableDO);
        });
        swStoreWorkDataTableDao.batchUpdate(enterpriseId, result);
        //需要转交的检查表(点评人)
        List<SwStoreWorkDataTableDO> storeCommentWorkDataTableDOList = swStoreWorkDataTableDao.selectCommentSwStoreWorkDataTableByUserId(enterpriseId, workHandoverDO.getTransferUserId());
        if (CollectionUtils.isEmpty(storeCommentWorkDataTableDOList)) {
            return;
        }
        storeCommentWorkDataTableDOList.forEach(x -> {
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = new SwStoreWorkDataTableDO();
            String commentUserIds = x.getCommentUserIds();
            String newCommentUserIds = null;
            String handleOverUserIdComma = String.format("%s%s%s", Constants.COMMA, workHandoverDO.getHandoverUserId(), Constants.COMMA);
            String transferUserIdComma = String.format("%s%s%s", Constants.COMMA, workHandoverDO.getTransferUserId(), Constants.COMMA);
            if(commentUserIds.contains(handleOverUserIdComma)){
                newCommentUserIds = commentUserIds.replace(transferUserIdComma, Constants.COMMA);
            }else {
                newCommentUserIds = commentUserIds.replace(transferUserIdComma, handleOverUserIdComma);
            }
            swStoreWorkDataTableDO.setUpdateTime(new Date());
            swStoreWorkDataTableDO.setUpdateUserId(workHandoverDO.getTransferUserId());
            swStoreWorkDataTableDO.setCommentUserIds(newCommentUserIds);
            swStoreWorkDataTableDO.setId(x.getId());
            swStoreWorkDataTableDao.updateCommentUserIds(enterpriseId, swStoreWorkDataTableDO);
        });
    }
}
