package com.coolcollege.intelligent.service.question.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.question.request.ExportRegionQuestionReportRequest;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoDetailVO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionParentInfoVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskParentDetailVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskParentQuestionVO;
import com.coolcollege.intelligent.model.unifytask.vo.UnifyTaskParentItemVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDisplayService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.QUESTION_ORDER;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;

/**
 * @author byd
 * @date 2022-08-04 14:32
 */
@Slf4j
@Service(value = "questionParentInfoService")
public class QuestionParentInfoServiceImpl implements QuestionParentInfoService {

    @Resource
    private QuestionParentInfoDao questionParentInfoDao;

    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;

    @Resource
    private UnifyTaskService unifyTaskService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private QuestionRecordDao questionRecordDao;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private WorkflowService workflowService;

    @Resource
    private TbDataStaTableColumnMapper dataStaTableColumnMapper;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private RedisConstantUtil redisConstantUtil;

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private StoreDao storeDao;

    @Resource
    private UnifyTaskDisplayService unifyTaskDisplayService;

    @Resource
    private UnifyTaskParentService unifyTaskParentService;

    @Resource
    private SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;

    @Override
    public PageInfo<TbQuestionParentInfoVO> questionList(String eid, QuestionParentRequest questionParentRequest) {
        PageHelper.startPage(questionParentRequest.getPageNumber(), questionParentRequest.getPageSize());
        if(questionParentRequest.getBeginCreateTime() != null){
            questionParentRequest.setBeginCreateDate(DateUtils.convertTimeToString(questionParentRequest.getBeginCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if(questionParentRequest.getEndCreateTime() != null){
            questionParentRequest.setEndCreateDate(DateUtils.convertTimeToString(questionParentRequest.getEndCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        List<TbQuestionParentInfoDO> list = questionParentInfoDao.list(eid, questionParentRequest);
        List<TbQuestionParentInfoVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>(resultList);
        }
        PageInfo pageInfo = new PageInfo<>(list);
        Set<String> userIdSet = list.stream().map(TbQuestionParentInfoDO::getCreateId).collect(Collectors.toSet());
        // 查询用户
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(eid, new ArrayList<>(userIdSet));
        Map<String, EnterpriseUserDO> userMap = userDOList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity()));
        for (TbQuestionParentInfoDO tbQuestionParentInfoDO : list) {
            TbQuestionParentInfoVO parentInfoVO = new TbQuestionParentInfoVO();
            parentInfoVO.setId(tbQuestionParentInfoDO.getId());
            parentInfoVO.setUnifyTaskId(tbQuestionParentInfoDO.getUnifyTaskId());
            parentInfoVO.setStatus(tbQuestionParentInfoDO.getStatus());
            parentInfoVO.setQuestionType(tbQuestionParentInfoDO.getQuestionType());
            parentInfoVO.setQuestionName(tbQuestionParentInfoDO.getQuestionName());
            parentInfoVO.setStatus(tbQuestionParentInfoDO.getStatus());
            parentInfoVO.setCreateId(tbQuestionParentInfoDO.getCreateId());
            parentInfoVO.setCreateTime(tbQuestionParentInfoDO.getCreateTime());
            parentInfoVO.setFinishNum(tbQuestionParentInfoDO.getFinishNum());
            parentInfoVO.setTotalNum(tbQuestionParentInfoDO.getTotalNum());
            parentInfoVO.setPlannedSpeed(tbQuestionParentInfoDO.getFinishNum()+Constants.STORE_PATH_SPILT+tbQuestionParentInfoDO.getTotalNum());
            EnterpriseUserDO userDO = userMap.get(tbQuestionParentInfoDO.getCreateId());
            if (userDO != null) {
                parentInfoVO.setCreateUserName(userDO.getName());
            }else if(Constants.AI.equals(parentInfoVO.getCreateId())){
                parentInfoVO.setCreateUserName(Constants.AI);
            }
            resultList.add(parentInfoVO);
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public TaskParentQuestionVO questionDetail(String eid, Long questionParentInfoId, String currentUserId) {
        TbQuestionParentInfoDO questionParentInfoDO = questionParentInfoDao.selectById(eid, questionParentInfoId);
        if (questionParentInfoDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }TaskParentQuestionVO taskParentQuestionVO = new TaskParentQuestionVO();
        taskParentQuestionVO.setQuestionName(questionParentInfoDO.getQuestionName());
        taskParentQuestionVO.setId(questionParentInfoDO.getId());
        List<UnifyTaskParentItemDO> itemDOList = unifyTaskParentItemDao.list(eid, questionParentInfoDO.getUnifyTaskId());
        if(CollectionUtils.isEmpty(itemDOList)){
            //老数据不在，从父任务查询
            TaskParentDO taskParentDO = taskParentMapper.selectTaskById(eid, questionParentInfoDO.getUnifyTaskId());
            if (taskParentDO == null) {
                throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
            }
            taskParentQuestionVO.setQuestionName(taskParentDO.getTaskName());
            UnifyTaskParentItemDO unifyTaskParentItemDO = new UnifyTaskParentItemDO();
            unifyTaskParentItemDO.setItemName(taskParentDO.getTaskName());
            unifyTaskParentItemDO.setBeginTime(new Date(taskParentDO.getBeginTime()));
            unifyTaskParentItemDO.setEndTime(new Date(taskParentDO.getEndTime()));
            unifyTaskParentItemDO.setNodeInfo(taskParentDO.getNodeInfo());
            unifyTaskParentItemDO.setTaskDesc(taskParentDO.getTaskDesc());
            unifyTaskParentItemDO.setUnifyTaskId(taskParentDO.getId());
            TaskParentDetailVO taskParentDetailVO = unifyTaskDisplayService.getDisplayParentDetail(eid, questionParentInfoDO.getUnifyTaskId());
            unifyTaskParentItemDO.setNodeInfo(JSONObject.toJSONString(taskParentDetailVO.getProcess()));
            QuestionTaskInfoDTO taskInfoDTO = JSONObject.parseObject(taskParentDO.getTaskInfo(), QuestionTaskInfoDTO.class);
            if(CollectionUtils.isNotEmpty(taskParentDetailVO.getFormData())){
                taskInfoDTO.setMetaColumnId(Long.valueOf(taskParentDetailVO.getFormData().get(0).getOriginMappingId()));
                taskInfoDTO.setMetaColumnName(taskParentDetailVO.getFormData().get(0).getMappingName());
            }
            taskInfoDTO.setAttachUrl(taskParentDO.getAttachUrl());
            unifyTaskParentItemDO.setTaskInfo(JSONObject.toJSONString(taskInfoDTO));
            String storeId = taskParentDetailVO.getStoreList().get(0).getStoreId();
            unifyTaskParentItemDO.setStoreId(storeId);
            itemDOList = new ArrayList<>();
            itemDOList.add(unifyTaskParentItemDO);
        }
        List<UnifyTaskParentItemVO> itemVOList = new ArrayList<>();
        List<String> storeIdList = itemDOList.stream().map(UnifyTaskParentItemDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(eid, storeIdList);
        Map<String, String> storeNameMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName));
        itemDOList.forEach(item -> {
            UnifyTaskParentItemVO itemVO = new UnifyTaskParentItemVO();
            itemVO.setId(item.getId());
            itemVO.setItemName(item.getItemName());
            itemVO.setBeginTime(item.getBeginTime());
            itemVO.setEndTime(item.getEndTime());
            itemVO.setNodeInfo(item.getNodeInfo());
            itemVO.setTaskDesc(item.getTaskDesc());
            itemVO.setUnifyTaskId(item.getUnifyTaskId());
            itemVO.setTaskInfo(item.getTaskInfo());
            itemVO.setCreateUserId(item.getCreateUserId());
            itemVO.setCreateTime(item.getCreateTime());
            itemVO.setUpdateTime(item.getUpdateTime());
            itemVO.setUpdateUserId(item.getUpdateUserId());
            itemVO.setStoreId(item.getStoreId());
            itemVO.setStoreName(storeNameMap.get(item.getStoreId()));
            itemVOList.add(itemVO);
        });
        taskParentQuestionVO.setItemList(itemVOList);
        return taskParentQuestionVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteQuestion(String eid, Long questionParentInfoId, String appType, String dingCorpId) {
        TbQuestionParentInfoDO questionParentInfoDO = questionParentInfoDao.selectById(eid, questionParentInfoId);
        if (questionParentInfoDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        unifyTaskService.batchDelUnifyTask(eid, Lists.newArrayList(questionParentInfoDO.getUnifyTaskId()), dingCorpId, appType);
        questionParentInfoDao.deleteById(eid, questionParentInfoId);
        unifyTaskParentItemDao.deleteByUnifyTaskId(eid, questionParentInfoDO.getUnifyTaskId());
        //删除工单待办
        cancelUpcoming(eid, dingCorpId, appType, questionParentInfoId);
    }

    @Override
    public TbQuestionParentInfoDetailVO questionParentInfoDetail(String eid, Long questionParentInfoId) {
        TbQuestionParentInfoDO questionParentInfoDO = questionParentInfoDao.selectById(eid, questionParentInfoId);
        if (questionParentInfoDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        TbQuestionParentInfoDetailVO detailVO = new TbQuestionParentInfoDetailVO();
        detailVO.setId(questionParentInfoDO.getId());
        detailVO.setUnifyTaskId(questionParentInfoDO.getUnifyTaskId());
        detailVO.setStatus(questionParentInfoDO.getStatus());
        detailVO.setQuestionType(questionParentInfoDO.getQuestionType());
        detailVO.setQuestionName(questionParentInfoDO.getQuestionName());
        detailVO.setCreateId(questionParentInfoDO.getCreateId());
        detailVO.setFinishNum(questionParentInfoDO.getFinishNum());
        detailVO.setTotalNum(questionParentInfoDO.getTotalNum());
        detailVO.setCreateTime(questionParentInfoDO.getCreateTime());
        String userName = enterpriseUserDao.selectNameIgnoreActiveByUserId(eid, questionParentInfoDO.getCreateId());
        detailVO.setCreateUserName(userName);
        UnifySubStatisticsDTO unifySubStatisticsDTO = questionRecordDao.selectQuestionTaskCount(eid, questionParentInfoDO.getUnifyTaskId());
        detailVO.setWaitApproveNum(unifySubStatisticsDTO.getApprover());
        detailVO.setWaitRectifiedNum(unifySubStatisticsDTO.getHandle());
        Long dataColumnId = questionRecordDao.selectDataColumnId(eid, questionParentInfoDO.getUnifyTaskId());
        if (dataColumnId != null && dataColumnId != 0) {
            if (QuestionTypeEnum.STORE_WORK.getCode().equals(questionParentInfoDO.getQuestionType())){
                SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(dataColumnId, eid);
                if (swStoreWorkDataTableColumnDO!=null){
                    detailVO.setTcBusinessId(swStoreWorkDataTableColumnDO.getTcBusinessId());
                    detailVO.setStoreId(swStoreWorkDataTableColumnDO.getStoreId());
                    detailVO.setStoreName(swStoreWorkDataTableColumnDO.getStoreName());
                }
            }else {
                TbDataStaTableColumnDO dataStaTableColumnDO = dataStaTableColumnMapper.selectById(eid, dataColumnId);
                if (dataStaTableColumnDO != null) {
                    detailVO.setBusinessId(dataStaTableColumnDO.getBusinessId());
                    TbPatrolStoreRecordDO patrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(eid, dataStaTableColumnDO.getBusinessId());
                    if(patrolStoreRecordDO != null){
                        detailVO.setStoreId(patrolStoreRecordDO.getStoreId());
                        detailVO.setStoreName(patrolStoreRecordDO.getStoreName());
                        detailVO.setSignEndTime(patrolStoreRecordDO.getSignEndTime());
                    }
                }
            }
        }
        return detailVO;
    }

    @Override
    public Long buildQuestion(String enterpriseId, BuildQuestionRequest buildQuestionRequest, String userId, boolean isAuto,Boolean isFilterUserAuth) {
        //封装校验组装数
        List<UnifyTaskParentItemDO> unifyTaskParentItemDOList = getTaskItemList(enterpriseId, buildQuestionRequest, userId, isAuto);
        TaskParentDO parentDO = unifyTaskParentService.insertQuestionOrder(enterpriseId, buildQuestionRequest, userId, unifyTaskParentItemDOList);
        unifyTaskService.buildTaskStoreQuestionOrder(enterpriseId, parentDO.getId(), isFilterUserAuth, false);
        // 发送消息，生成任务
        return parentDO.getId();
    }

    @Override
    public ImportTaskDO questionListExport(String enterpriseId, QuestionParentRequest questionParentRequest, CurrentUser user) {
        // 查询导出数量，限流
        if(questionParentRequest.getBeginCreateTime() != null){
            questionParentRequest.setBeginCreateDate(DateUtils.convertTimeToString(questionParentRequest.getBeginCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if(questionParentRequest.getEndCreateTime() != null){
            questionParentRequest.setEndCreateDate(DateUtils.convertTimeToString(questionParentRequest.getEndCreateTime(), DateUtils.DATE_FORMAT_SEC));
        }
        Long count = questionParentInfoDao.questionListCount(enterpriseId,questionParentRequest);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.QUESTION_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TASK_QUESTION_REPORT);
        // 构造异步导出参数
        ExportRegionQuestionReportRequest msg = new ExportRegionQuestionReportRequest();
        questionParentRequest.setCurrentUserId(user.getUserId());
        msg.setEnterpriseId(enterpriseId);
        msg.setQuestionParentRequest(questionParentRequest);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.QUESTION_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }



    private List<UnifyTaskParentItemDO> getTaskItemList(String enterpriseId, BuildQuestionRequest buildQuestionRequest, String userId, boolean isAuto){
        Map<String, Long> loopCountStoreIdMap = new HashMap<>();
        List<UnifyTaskParentItemDO> unifyTaskParentItemDOList = new ArrayList<>();
        buildQuestionRequest.getQuestionList().forEach(questionDTO -> {
            //问题工单巡店同一检查项不能再次发起
            Long dataColumnId = questionDTO.getTaskInfo().getDataColumnId();
            boolean isCreating = StringUtils.isNotBlank(redisUtilPool.getString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataColumnId))));
            //店务工单校验逻辑
            if (QuestionTypeEnum.STORE_WORK.getCode().equals(buildQuestionRequest.getQuestionType())){
                SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(dataColumnId, enterpriseId);
                boolean isHaveOrder = swStoreWorkDataTableColumnDO != null && swStoreWorkDataTableColumnDO.getTaskQuestionId() != null && swStoreWorkDataTableColumnDO.getTaskQuestionId() > 0;
                if(!isAuto && isHaveOrder){
                    throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "该检查项已发起工单，不能再次发起工单");
                }
                if (!isAuto && isCreating) {
                    throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE);
                }
                if(isAuto && (isHaveOrder || isCreating)){
                    return;
                }
            }if (QuestionTypeEnum.PATROL_RECHECK_REPORT.getCode().equals(buildQuestionRequest.getQuestionType())){
                int count = tbQuestionRecordMapper.getCountQuestionTypeAndDataColumnId(enterpriseId, QuestionTypeEnum.PATROL_RECHECK_REPORT.getCode(), dataColumnId);
                if(!isAuto && count > 0){
                    throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "该检查项已发起工单，不能再次发起工单");
                }
                if (!isAuto && isCreating) {
                    throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE);
                }
            }else {
                if(dataColumnId != null && dataColumnId > 0){
                    TbDataStaTableColumnDO tableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, dataColumnId);
                    boolean isHaveOrder = tableColumnDO != null && tableColumnDO.getTaskQuestionId() != null && tableColumnDO.getTaskQuestionId() > 0;
                    if(!isAuto && isHaveOrder){
                        throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "该检查项已发起工单，不能再次发起工单");
                    }
                    if (!isAuto && isCreating) {
                        throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE);
                    }
                    if(isAuto && (isHaveOrder || isCreating)){
                        return;
                    }
                }
            }
            if (CollectionUtils.isEmpty(questionDTO.getProcess())) {
                throw new ServiceException(ErrorCodeEnum.QUESTION_USER_NOT_NULL);
            }
            TaskProcessDTO handleUsers = questionDTO.getProcess().stream()
                    .filter(process -> UnifyNodeEnum.FIRST_NODE.getCode().equals(process.getNodeNo()))
                    .findFirst().orElse(null);
            if (handleUsers == null || CollectionUtils.isEmpty(handleUsers.getUser())) {
                throw new ServiceException(ErrorCodeEnum.QUESTION_USER_NOT_NULL);
            }

            Long loopCount = loopCountStoreIdMap.getOrDefault(questionDTO.getStoreId(), 1L);
            loopCountStoreIdMap.put(questionDTO.getStoreId(), loopCount + 1L);
            //RPC创建模板至流程引擎 null默认工作流任务
            //工单查询工单任务明细表
            UnifyTaskParentItemDO unifyTaskParentItemDO = new UnifyTaskParentItemDO();
            unifyTaskParentItemDO.setItemName(questionDTO.getTaskName());
            unifyTaskParentItemDO.setStoreId(questionDTO.getStoreId());
            unifyTaskParentItemDO.setBeginTime(new Date());
            unifyTaskParentItemDO.setEndTime(questionDTO.getEndTime());
            unifyTaskParentItemDO.setCreateTime(new Date());
            unifyTaskParentItemDO.setTaskDesc(questionDTO.getTaskDesc());
            unifyTaskParentItemDO.setTaskInfo(JSONUtil.toJsonStr(questionDTO.getTaskInfo()));
            unifyTaskParentItemDO.setNodeInfo(JSONObject.toJSONString(questionDTO.getProcess()));
            unifyTaskParentItemDO.setLoopCount(loopCount);
            unifyTaskParentItemDO.setCreateUserId(userId);
            UnifyTaskBuildDTO task = new UnifyTaskBuildDTO();
            task.setProcess(questionDTO.getProcess());
            //从事物抽出来
            unifyTaskParentItemDO.setTemplateId(null);
            unifyTaskParentItemDOList.add(unifyTaskParentItemDO);
        });
        return unifyTaskParentItemDOList;
    }


    public void cancelUpcoming(String enterpriseId, String dingCorpId,String appType, Long parentQuestionId) {
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", QUESTION_ORDER.getCode() + "_" + parentQuestionId);
        jsonObject.put("appType",appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public Map<String, String> workOrderCompletionStatus(String enterpriseId, Long businessId) {
        log.info("workOrderCompletionStatus request enterpriseId:{},businessId:{}",enterpriseId,businessId);
        Map<String, String> result = new HashMap<>();
        //查询是否存在有工单的记录
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);
        List<Long> taskQuestionId = tbDataStaTableColumnDOS.stream().filter(item -> item.getTaskQuestionId() != 0).map(TbDataStaTableColumnDO::getTaskQuestionId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(taskQuestionId)){
            result.put("result","noResult");
            return result;
        }
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOS = questionParentInfoDao.selectByUnifyTaskIds(enterpriseId, taskQuestionId);
        int countFinishNum = tbQuestionParentInfoDOS.stream().mapToInt(TbQuestionParentInfoDO::getFinishNum).sum();
        int countTotalNum = tbQuestionParentInfoDOS.stream().mapToInt(TbQuestionParentInfoDO::getTotalNum).sum();
        result.put("countFinishNum",String.valueOf(countFinishNum));
        result.put("countTotalNum",String.valueOf(countTotalNum));
        result.put("countPercent",(countFinishNum) + "/" + (countTotalNum));
        return result;
    }
}
