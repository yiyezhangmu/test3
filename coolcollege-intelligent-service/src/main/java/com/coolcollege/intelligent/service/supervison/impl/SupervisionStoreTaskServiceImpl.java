package com.coolcollege.intelligent.service.supervison.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.rpc.common.utils.BeanUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskCompleteStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.supervision.dao.*;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.ActionTypeEnum;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MessageDealDTO;
import com.coolcollege.intelligent.model.msg.SupervisionTaskMessageDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.supervision.*;
import com.coolcollege.intelligent.model.supervision.dto.ApproveInfoDTO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionStoreDataDTO;
import com.coolcollege.intelligent.model.supervision.request.*;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDetailVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.supervison.SupervisionStoreTaskService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 19:47
 * @Version 1.0
 */
@Slf4j
@Service
public class SupervisionStoreTaskServiceImpl implements SupervisionStoreTaskService {

    @Resource
    SupervisionStoreTaskDao supervisionStoreTaskDao;
    @Resource
    SupervisionTaskDao supervisionTaskDao;
    @Resource
    SupervisionTaskParentDao supervisionTaskParentDao;
    @Resource
    StoreMapper storeMapper;
    @Resource
    TaskSopService taskSopService;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Resource
    TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private SupervisionApproveDao supervisionApproveDao;
    @Resource
    EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    SupervisionHistoryDao supervisionHistoryDao;
    @Resource
    private JmsTaskService jmsTaskService;

    @Lazy
    @Resource
    private SupervisionTaskService supervisionTaskService;
    @Resource
    SupervisionTaskParentService supervisionTaskParentService;

    @Override
    @Transactional
    public Boolean storeTaskCancel(String enterpriseId, Long parentId, Long supervisionTaskId, Long id, EnterpriseConfigDO enterpriseConfigDO) {
        //门店任务取消
        supervisionStoreTaskDao.storeTaskCancel(enterpriseId,null,null,id);
        SupervisionStoreTaskDO supervision = supervisionStoreTaskDao.selectByPrimaryKey(id, enterpriseId);
        //删除审批人的待办
        if (supervision.getTaskState()==4){
            supervisionApproveDao.batchDeleteByTaskParentId(enterpriseId,Arrays.asList(id),"store",null);
            cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervision.getId());
        }
        //未取消的数据 为0的时候表示已经全部取消了
        int notCancelCount = supervisionStoreTaskDao.notCancelCountByParentId(enterpriseId, supervisionTaskId);
        //还有门店未取消，那就只需要取消门店任务 不需要操作后面逻辑
        //判断任务是否是完成状态
        Long aLong = supervisionStoreTaskDao.noCompleteListByTaskId(enterpriseId, supervisionTaskId);
        if (notCancelCount==0){
            supervisionTaskDao.taskCancel(enterpriseId,null,supervisionTaskId);
            SupervisionTaskDO taskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
            cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),taskDO.getId());
        }
        //查看父任务下子任务是否全部取消 去不取消需要 取消父任务
        Integer supervisionTaskNotCancelCount = supervisionTaskDao.notCancelCountByParentId(enterpriseId, parentId);
        if (supervisionTaskNotCancelCount==0){
            supervisionTaskParentDao.taskParentCancel(enterpriseId,parentId);
        }

        supervisionTaskService.syncTaskStatus(enterpriseId,Arrays.asList(supervisionTaskId));
        //执行的任务完成，取消待办
        if (aLong==0){
            SupervisionTaskDO taskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
            cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),taskDO.getId());
        }

        return Boolean.TRUE;
    }

    @Override
    public PageInfo<SupervisionStoreTaskDetailVO> getSupervisionStoreList(String enterpriseId, Long taskId, String userId, SupervisionSubTaskStatusEnum taskStatusEnum, Integer pageSize,
                                                                          Integer pageNum,Integer handleOverTimeStatus,String storeName) {
        Integer taskStatus = null;
        if (taskStatusEnum!=null){
            taskStatus = taskStatusEnum.getStatus();
        }
        //查询门店数
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionStoreTaskDO> supervisionStoreList = supervisionStoreTaskDao.getSupervisionStoreList(enterpriseId, taskId, userId, taskStatus,handleOverTimeStatus,storeName);

        PageInfo<SupervisionStoreTaskDetailVO> result = new PageInfo<>();
        PageInfo<SupervisionStoreTaskDO> supervisionStoreTaskDOPageInfo = new PageInfo<>(supervisionStoreList);
        result.setList(convertSupervisionStoreTaskDetail(enterpriseId,supervisionStoreList));
        result.setTotal(supervisionStoreTaskDOPageInfo.getTotal());

        return result;
    }

    @Override
    public PageInfo<SupervisionStoreTaskVO> listMySupervisionStoreTask(String enterpriseId, SupervisionStoreTaskQueryRequest request) {

        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        Integer taskStatus = null;
        if (request.getStatusEnum()!=null) {
            taskStatus =  request.getStatusEnum().getStatus();
        }
        PageInfo<SupervisionStoreTaskVO> result = new PageInfo<SupervisionStoreTaskVO>();
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listMySupervisionStoreTask(enterpriseId, request, null, null, null,
                taskStatus,request.getHandleOverTimeStatus());
        PageInfo<SupervisionStoreTaskDO> supervisionStoreTaskDOPageInfo = new PageInfo<>(supervisionStoreTaskDOS);
        List<SupervisionStoreTaskVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)) {
            return new PageInfo<>(resultList);
        }

        List<Long> taskParentIdList = supervisionStoreTaskDOS.stream().map(SupervisionStoreTaskDO::getTaskParentId).collect(Collectors.toList());
        List<SupervisionTaskParentDO> taskParentDOList = supervisionTaskParentDao.listByTaskIdList(enterpriseId, taskParentIdList);
        Map<Long, SupervisionTaskParentDO> taskParentDOMap = taskParentDOList.stream().collect(Collectors.toMap(SupervisionTaskParentDO::getId, Function.identity()));

        for (SupervisionStoreTaskDO supervisionStoreTaskDO : supervisionStoreTaskDOS) {
            SupervisionStoreTaskVO vo = new SupervisionStoreTaskVO();
            BeanUtils.copyProperties(supervisionStoreTaskDO, vo);
            SupervisionTaskParentDO supervisionTaskParentDO = taskParentDOMap.get(supervisionStoreTaskDO.getTaskParentId());
            vo.setRemark(supervisionTaskParentDO.getDescription());
            vo.setPriority(supervisionTaskParentDO.getPriority());
            //即将到期标识
            vo.setExpireFlag(Boolean.FALSE);

            Date taskEndTime = supervisionStoreTaskDO.getTaskEndTime();
            Date date =  DateUtil.getNextDay(new Date());
            if (supervisionStoreTaskDO.getTaskState()==0&&taskEndTime.getTime()<date.getTime()&&taskEndTime.getTime()>System.currentTimeMillis()){
                vo.setExpireFlag(Boolean.TRUE);
            }
            Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(supervisionStoreTaskDO.getTaskEndTime(), supervisionStoreTaskDO.getCancelStatus(), supervisionStoreTaskDO.getTaskState());
            vo.setTaskStatus(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
            vo.setTransferReassignFlag(supervisionStoreTaskDO.getTransferReassignFlag());
            vo.setTaskGrouping(supervisionStoreTaskDO.getTaskGrouping());
            vo.setCurrentNode(supervisionStoreTaskDO.getCurrentNode());
            if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
                vo.setApproveInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class));
            }
            vo.setHandleWay(JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class));
            resultList.add(vo);
        }
        result.setList(resultList);
        result.setTotal(supervisionStoreTaskDOPageInfo.getTotal());
        return result;
    }

    @Override
    public SupervisionStoreTaskVO getSupervisionStoreTaskDetail(String enterpriseId, Long taskId) {
        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(taskId, enterpriseId);

        SupervisionStoreTaskVO supervisionStoreTaskVO = new SupervisionStoreTaskVO();
        BeanUtils.copyProperties(supervisionStoreTaskDO, supervisionStoreTaskVO);
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionStoreTaskDO.getTaskParentId(), enterpriseId);
        supervisionStoreTaskVO.setDesc(supervisionTaskParentDO.getDescription());
        supervisionStoreTaskVO.setPriority(supervisionTaskParentDO.getPriority());
        if (StringUtils.isNotEmpty(supervisionStoreTaskDO.getSopIds())){
            List<Long> sopIds = JSONObject.parseArray(supervisionStoreTaskDO.getSopIds(), Long.class);
            List<TaskSopVO> taskSopVOS = taskSopService.listByIdList(enterpriseId, sopIds);
            supervisionStoreTaskVO.setTaskSopVOList(taskSopVOS);
        }
        Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(supervisionStoreTaskDO.getTaskEndTime(), supervisionStoreTaskDO.getCancelStatus(), supervisionStoreTaskDO.getTaskState());

        //是否有驳回的历史记录
        Boolean taskRejectFlag = Boolean.FALSE;
        List<SupervisionHistoryDO> supervisionHistoryDOS = supervisionHistoryDao.selectByTaskIdAndType(enterpriseId, taskId, "store", Boolean.TRUE);
        if (CollectionUtils.isNotEmpty(supervisionHistoryDOS)){
            taskRejectFlag = Boolean.TRUE;
        }
        supervisionStoreTaskVO.setTaskRejectFlag(taskRejectFlag);

        Integer taskHandleOverTimeStatus = supervisionTaskParentService.getTaskHandleOverTimeStatus(supervisionStoreTaskDO.getHandleOverTimeStatus(), supervisionStoreTaskDO.getTaskEndTime().getTime(), supervisionStoreTaskDO.getTaskState());
        supervisionStoreTaskVO.setHandleOverTimeStatus(taskHandleOverTimeStatus);
        supervisionStoreTaskVO.setCurrentNode(supervisionStoreTaskDO.getCurrentNode());
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
            supervisionStoreTaskVO.setApproveInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class));
        }
        EnterpriseUserSingleDTO enterpriseUserSingleDTO = new EnterpriseUserSingleDTO();
        enterpriseUserSingleDTO.setUserId(supervisionStoreTaskDO.getSupervisionHandleUserId());
        enterpriseUserSingleDTO.setUserName(supervisionStoreTaskDO.getSupervisionHandleUserName());
        supervisionStoreTaskVO.setHandleUserIds(Arrays.asList(enterpriseUserSingleDTO));
        supervisionStoreTaskVO.setTaskStartTime(supervisionStoreTaskDO.getTaskStartTime());
        if (StringUtils.isNotEmpty(supervisionStoreTaskDO.getFirstApprove())){
            String[] split = supervisionStoreTaskDO.getFirstApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionStoreTaskVO.setFirstApproveList(enterpriseUserSingleDTOS);
            }
        }
        if (StringUtils.isNotEmpty(supervisionStoreTaskDO.getSecondaryApprove())){
            String[] split = supervisionStoreTaskDO.getSecondaryApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionStoreTaskVO.setSecondaryApproveList(enterpriseUserSingleDTOS);
            }
        }
        if (StringUtils.isNotEmpty(supervisionStoreTaskDO.getThirdApprove())){
            String[] split = supervisionStoreTaskDO.getThirdApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionStoreTaskVO.setThirdApproveList(enterpriseUserSingleDTOS);
            }
        }
        supervisionStoreTaskVO.setTaskState(subStatus);
        supervisionStoreTaskVO.setTaskStatus(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
        supervisionStoreTaskVO.setHandleWay(JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class));
        if (supervisionStoreTaskDO.getFormId()!=null){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(supervisionStoreTaskDO.getFormId()));
            supervisionStoreTaskVO.setFormName(tbMetaTableDO.getTableName());
        }
        return supervisionStoreTaskVO;
    }

    @Override
    public PageInfo<SupervisionStoreTaskDetailVO> supervisionStoreList(String enterpriseId, String userId, Integer pageSize, Integer pageNum) {
        //查询门店数
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionStoreDataDTO> supervisionStoreDataDTOS = supervisionStoreTaskDao.getStoreIdList(enterpriseId, userId);
        PageInfo<SupervisionStoreTaskDetailVO> result = new PageInfo<>();
        if (CollectionUtils.isEmpty(supervisionStoreDataDTOS)){
            return result;
        }
        List<String> storeIdList = supervisionStoreDataDTOS.stream().map(SupervisionStoreDataDTO::getStoreId).collect(Collectors.toList());
        Map<String, Integer> countMap = supervisionStoreDataDTOS.stream().collect(Collectors.toMap(SupervisionStoreDataDTO::getStoreId, SupervisionStoreDataDTO::getCount));
        PageInfo supervisionStoreTaskDOPageInfo = new PageInfo<>(supervisionStoreDataDTOS);
        List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIdList);
        Map<String, StoreDTO> storeMap = storeListByStoreIds.stream().collect(Collectors.toMap(StoreDTO::getStoreId, da-> da));
        List<SupervisionStoreTaskDetailVO> supervisionStoreTaskDetailVOS = new ArrayList<>();
        for (String storeId:storeIdList) {
            SupervisionStoreTaskDetailVO supervisionStoreTaskDetailVO = new SupervisionStoreTaskDetailVO();
            supervisionStoreTaskDetailVO.setStoreId(storeId);
            supervisionStoreTaskDetailVO.setStoreNum(storeMap.getOrDefault(storeId,new StoreDTO()).getStoreNum());
            supervisionStoreTaskDetailVO.setStoreName(storeMap.getOrDefault(storeId,new StoreDTO()).getStoreName());
            supervisionStoreTaskDetailVO.setCount(countMap.get(storeId));
            supervisionStoreTaskDetailVOS.add(supervisionStoreTaskDetailVO);
        }
        result.setTotal(supervisionStoreTaskDOPageInfo.getTotal());
        result.setList(supervisionStoreTaskDetailVOS);
        return result;
    }

    @Override
    public Boolean batchUpdateSupervisionStoreTaskStatus(String enterpriseId, OpenApiUpdateSupervisionTaskDTO dto) {
        log.info("沪上推送门店任务状态变更:{}", JSONObject.toJSONString(dto));
        List<Long> supervisionTaskIdList = StrUtil.splitTrim(dto.getSupervisionTaskIds(), Constants.COMMA)
                .stream().map(Long::valueOf)
                .collect(Collectors.toList());
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTask(enterpriseId, supervisionTaskIdList);
        if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)){
            return Boolean.TRUE;
        }
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId("AI");
        currentUser.setName("系统自动执行");
        List<SupervisionHistoryDO> supervisionHistoryDOS = new ArrayList<>();
        for (SupervisionStoreTaskDO supervisionStoreTaskDO:supervisionStoreTaskDOS) {
            supervisionStoreTaskDO.setTaskState(SupervisionTaskCompleteStatusEnum.YES.getCode());
            supervisionStoreTaskDO.setCurrentNode(9);
            supervisionStoreTaskDO.setSubmitTime(new Date());
            supervisionStoreTaskDO.setCompleteTime(new Date());
            if (supervisionStoreTaskDO.getTaskEndTime().getTime()<System.currentTimeMillis()){
                supervisionStoreTaskDO.setHandleOverTimeStatus(1);
            }
            supervisionHistoryDOS.add(supervisionTaskService.handleSupervisionHistory(supervisionStoreTaskDO.getId(),"store",ActionTypeEnum.HANDLE.name(),currentUser,0));
        }
        supervisionStoreTaskDao.batchUpdateTaskStatus(enterpriseId,supervisionStoreTaskDOS);

        //添加历史执行记录
        supervisionHistoryDao.batchInsert(enterpriseId,supervisionHistoryDOS);

        List<Long> supervisionTaskIds = supervisionStoreTaskDOS.stream().map(SupervisionStoreTaskDO::getSupervisionTaskId).collect(Collectors.toList());
        //查询按人任务未完成的
        List<SupervisionStoreTaskDO> supervisionStoreList = supervisionStoreTaskDao.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId, supervisionTaskIds,Boolean.TRUE,Boolean.TRUE);
        Map<Long, List<SupervisionStoreTaskDO>> longSupervisionStoreTaskDOMap = supervisionStoreList.stream().collect(Collectors.groupingBy(SupervisionStoreTaskDO::getSupervisionTaskId));

        List<Long> ids = new ArrayList<>();
        for (Long id:supervisionTaskIds) {
            List<SupervisionStoreTaskDO> supervisionStoreTaskDO = longSupervisionStoreTaskDOMap.get(id);
            //如果按人任务未完成的是null 表示按人任务下门店全部完成 需要取消待办和更新按人任务状态
            if (CollectionUtils.isEmpty(supervisionStoreTaskDO)){
                ids.add(id);
            }
        }
        Map<Long, List<SupervisionStoreTaskDO>> longListMap = supervisionStoreTaskDOS.stream().collect(Collectors.groupingBy(SupervisionStoreTaskDO::getSupervisionTaskId));
        //如果该任务下门店都完成，按人任务完成
        if (CollectionUtils.isNotEmpty(ids)){
            List<SupervisionTaskDO> result = new ArrayList<>();
            for (Long id:ids) {
                SupervisionTaskDO supervisionTaskDO = new SupervisionTaskDO();
                List<SupervisionStoreTaskDO> sstd = longListMap.get(id);
                supervisionTaskDO.setId(id);
                supervisionTaskDO.setTaskState(SupervisionTaskCompleteStatusEnum.YES.getCode());
                supervisionTaskDO.setCompleteTime(new Date());
                supervisionTaskDO.setSubmitTime(new Date());
                if (CollectionUtils.isNotEmpty(sstd)){
                    supervisionTaskDO.setTaskState(sstd.get(0).getTaskState());
                }
                result.add(supervisionTaskDO);
            }
            supervisionTaskDao.batchUpdateTaskStatus(enterpriseId,result);
        }

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        for(Long supervisionTaskId : ids){
            // 取消待办
            cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), supervisionTaskId);
        }
        return Boolean.TRUE;
    }

    public void cancelUpcoming(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId) {
        log.info("开始删除用户待办");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.SUPERVISION.getCode().toLowerCase() + "_" + supervisionTaskId);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }


    /**
     * 取消门店审批人的钉钉待办
     * @param enterpriseId
     * @param dingCorpId
     * @param appType
     * @param supervisionTaskId
     */
    @Override
    public void cancelSupervisionStoreTaskUpcoming(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId) {
        log.info("开始删除用户按店待办 cancelSupervisionStoreTaskUpcoming supervisionTaskId:{}",supervisionTaskId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.SUPERVISION_STORE.getCode().toLowerCase() + "_" + supervisionTaskId);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }
    /**
     * DO——>VO
     * @param supervisionStoreList
     * @return
     */
    private List<SupervisionStoreTaskDetailVO> convertSupervisionStoreTaskDetail(String enterpriseId,List<SupervisionStoreTaskDO> supervisionStoreList ){

        List<SupervisionStoreTaskDetailVO> supervisionStoreTaskDetailVOS = new ArrayList<>();
        SupervisionStoreTaskDetailVO supervisionStoreTaskDetailVO = null;
        List<String> storeIds = supervisionStoreList.stream().map(SupervisionStoreTaskDO::getStoreId).collect(Collectors.toList());
        List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeMap = storeListByStoreIds.stream().filter(x->StringUtils.isNotEmpty(x.getStoreNum())).collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getStoreNum));
        for (SupervisionStoreTaskDO supervisionStoreTaskDO:supervisionStoreList) {
            supervisionStoreTaskDetailVO = new SupervisionStoreTaskDetailVO();
            supervisionStoreTaskDetailVO.setStoreId(supervisionStoreTaskDO.getStoreId());
            supervisionStoreTaskDetailVO.setStoreName(supervisionStoreTaskDO.getStoreName());
            supervisionStoreTaskDetailVO.setStoreNum(storeMap.get(supervisionStoreTaskDO.getStoreId()));
            supervisionStoreTaskDetailVO.setSupervisionUserId(supervisionStoreTaskDO.getSupervisionUserId());
            supervisionStoreTaskDetailVO.setTaskEndTime(supervisionStoreTaskDO.getTaskEndTime());
            supervisionStoreTaskDetailVO.setSubmitTime(supervisionStoreTaskDO.getSubmitTime());
            supervisionStoreTaskDetailVO.setId(supervisionStoreTaskDO.getId());

            Integer taskHandleOverTimeStatus = supervisionTaskParentService.getTaskHandleOverTimeStatus(supervisionStoreTaskDO.getHandleOverTimeStatus(), supervisionStoreTaskDO.getTaskEndTime().getTime(), supervisionStoreTaskDO.getTaskState());
            supervisionStoreTaskDetailVO.setHandleOverTimeStatus(taskHandleOverTimeStatus);

            supervisionStoreTaskDetailVO.setTaskState(SupervisionTaskParentServiceImpl.getSubStatus(supervisionStoreTaskDO.getTaskEndTime(),supervisionStoreTaskDO.getCancelStatus(),supervisionStoreTaskDO.getTaskState()));
            supervisionStoreTaskDetailVO.setTaskStateStr(SupervisionSubTaskStatusEnum.getByCode(supervisionStoreTaskDetailVO.getTaskState()).getDesc());

            supervisionStoreTaskDetailVOS.add(supervisionStoreTaskDetailVO);
        }
        return supervisionStoreTaskDetailVOS;
    }


    @Override
    public Boolean confirmSupervisionTask(String enterpriseId, Long id, CurrentUser user) {
        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(id, enterpriseId);
        if (supervisionStoreTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        if (!user.getUserId().equals(supervisionStoreTaskDO.getSupervisionHandleUserId())) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
        }
        if (supervisionStoreTaskDO.getCancelStatus()==1||supervisionStoreTaskDO.getDeleted()){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
        }
//        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionStoreTaskDO.getTaskParentId(), enterpriseId);
        updateCompleteStatusCancelUpcoming(enterpriseId, id,user);
//        if(StringUtils.isNotBlank(supervisionTaskParentDO.getCheckCode())) {
//            executor.execute(() -> {
//                // 异步调沪上接口
//                log.info("异步调沪上接口 目前无相关接口");
//            });
//
//        }else {
//            updateCompleteStatusCancelUpcoming(enterpriseId, id);
//        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean storeTaskBatchConfirmFinish(String enterpriseId, List<Long> idList, CurrentUser user) {
        idList.forEach(id -> {
            SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(id, enterpriseId);
            if (supervisionStoreTaskDO == null) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
            }
            if (!user.getUserId().equals(supervisionStoreTaskDO.getSupervisionHandleUserId())) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
            }
            if (supervisionStoreTaskDO.getCancelStatus()==1||supervisionStoreTaskDO.getDeleted()){
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
            }
            updateCompleteStatusCancelUpcoming(enterpriseId, id,user);
        });
        return Boolean.TRUE;
    }


    @Override
    public List<PersonDTO> getSupervisionApproveUserList(String enterpriseId, SupervisionApproveUserRequest request, CurrentUser user) {
        List<PersonDTO> personDTOList = new ArrayList<>();
        //按门店任务催办
        if (request.getStoreTaskId() != null) {
            List<SupervisionApproveDO> supervisionApproveDOList = supervisionApproveDao.selectByTaskIdList(enterpriseId, Collections.singletonList(request.getStoreTaskId()), request.getType());
            if (CollectionUtils.isNotEmpty(supervisionApproveDOList)) {
                personDTOList = supervisionApproveDOList.stream().map(supervisionApproveDO -> PersonDTO.builder()
                        .userId(supervisionApproveDO.getApproveUserId()).userName(supervisionApproveDO.getApproveUserName()).build()).collect(Collectors.toList());
            }
            return personDTOList;
        }
        //催办按人任务
        if (request.getTaskId() != null && Constants.SUPERVISION_TYPE_PERSON.equals(request.getType())) {
            List<SupervisionApproveDO> supervisionApproveDOList = supervisionApproveDao.selectByTaskIdList(enterpriseId, Collections.singletonList(request.getTaskId()), request.getType());
            if (CollectionUtils.isNotEmpty(supervisionApproveDOList)) {
                personDTOList = supervisionApproveDOList.stream().map(supervisionApproveDO -> PersonDTO.builder()
                        .userId(supervisionApproveDO.getApproveUserId()).userName(supervisionApproveDO.getApproveUserName()).build()).collect(Collectors.toList());
            }
            return personDTOList;
        }
        //一键催办
        if (request.getTaskId() != null && Constants.SUPERVISION_TYPE_STORE.equals(request.getType())) {
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOList = supervisionStoreTaskDao.getSupervisionStoreList(enterpriseId, request.getTaskId(), user.getUserId(), 4, null,null);
            if (CollectionUtils.isNotEmpty(supervisionStoreTaskDOList)) {
                List<Long> supervisionStoreTaskIdList = supervisionStoreTaskDOList.stream().map(SupervisionStoreTaskDO::getId).collect(Collectors.toList());
                List<SupervisionApproveDO> supervisionApproveDOList = supervisionApproveDao.selectByTaskIdList(enterpriseId, supervisionStoreTaskIdList, request.getType());
                if (CollectionUtils.isNotEmpty(supervisionApproveDOList)) {
                    personDTOList = supervisionApproveDOList.stream().map(supervisionApproveDO -> PersonDTO.builder()
                            .userId(supervisionApproveDO.getApproveUserId()).userName(supervisionApproveDO.getApproveUserName()).build()).collect(Collectors.toList());
                }
            }
        }
        return personDTOList;
    }

    @Override
    @Transactional
    public Boolean supervisionApprove(String enterpriseId, SupervisionApproveRequest supervisionApproveRequest, CurrentUser user) {
        //按人任务审批
        if ("person".equals(supervisionApproveRequest.getType())){
            return  personTaskApprove(enterpriseId,supervisionApproveRequest,user);
        }
        if ("store".equals(supervisionApproveRequest.getType())){
            return  storeTaskApprove(enterpriseId,supervisionApproveRequest,user);
        }
        return Boolean.TRUE;
    }

    Boolean personTaskApprove(String enterpriseId, SupervisionApproveRequest supervisionApproveRequest, CurrentUser user){
        //按人任务
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionApproveRequest.getTaskId(), enterpriseId);

        Integer currentNode = supervisionTaskDO.getCurrentNode();
        //判断是否有下一节点
        HashMap<Integer, List<String>> map = hasNextNode(supervisionTaskDO.getFirstApprove(), supervisionTaskDO.getSecondaryApprove(), supervisionTaskDO.getThirdApprove());
        List<String> currentNodeApproveUser = map.get(currentNode);
        //判断是不是当前节点人审批
        if (!currentNodeApproveUser.contains(user.getUserId())){
            throw new ServiceException(ErrorCodeEnum.NOT_APPROVE_USER);
        }
        if (supervisionTaskDO.getCancelStatus()==1||supervisionTaskDO.getDeleted()){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
        }

        //完成状态是9

        List<String> list = map.get(currentNode+1);
        Integer nextNode = CollectionUtils.isNotEmpty(list)?currentNode+1:9;
        //添加执行记录
        SupervisionHistoryDO supervisionHistoryDO = supervisionTaskService.handleSupervisionHistory(supervisionApproveRequest.getTaskId(), supervisionApproveRequest.getType(), ActionTypeEnum.APPROVE.name(), user,supervisionTaskDO.getCurrentNode());
        supervisionHistoryDO.setActionKey(supervisionApproveRequest.getActionKey());
        supervisionHistoryDO.setRemark(supervisionApproveRequest.getApproveRemark());
        supervisionHistoryDao.insertSelective(supervisionHistoryDO,enterpriseId);
        //删除当前审批表数据
        supervisionApproveDao.batchDelete(enterpriseId,Arrays.asList(supervisionApproveRequest.getTaskId()),supervisionApproveRequest.getType());
        try {
            EnterpriseConfigDTO enterpriseConfigDO = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        //如果是拒绝 直接到执行人
            SupervisionTaskDO supervision = new SupervisionTaskDO();
            if ("reject".equals(supervisionApproveRequest.getActionKey())){
                //将状态设置为待执行
                supervision.setId(supervisionTaskDO.getId());
                supervision.setTaskState(SupervisionSubTaskStatusEnum.TODO.getStatus());
                supervision.setCurrentNode(0);
                supervision.setApproveStatus(3);
                supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId,supervision);
                //取消审批人的钉钉待办
                cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION.getCode());
                //发送钉钉待办  不需要判断任务是否完成 直接发送待办 按人任务执行完成之后待办就已经取消，所以需要重新发送待办
                jmsTaskService.sendSupervisionTaskBacklogByTaskId(enterpriseId,supervisionTaskDO.getId());
                return Boolean.TRUE;
            }

            //审批人通过 有下一级
            if (CollectionUtils.isNotEmpty(list)){
                //修改状态 status currentNode
                supervision.setId(supervisionTaskDO.getId());
                supervision.setCurrentNode(nextNode);
                supervision.setApproveStatus(2);
                //添加下一节点审批数据
                List<SupervisionApproveDO> supervisionApproveDOS = handleSupervisionApprove(enterpriseId, supervisionTaskDO.getTaskName(), supervisionTaskDO.getTaskParentId(), supervisionTaskDO.getId(), supervisionApproveRequest.getType(), list);
                supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);
                //取消审批人钉钉待办
                cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                //取消当前节点的钉钉待办
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION.getCode());
                //发送工作通知给下一级
                SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);
                SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(list, supervisionTaskParentDO, supervisionTaskDO.getId(),SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                        "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                        "您有一个待审核的任务，点击前往审核。";
                supervisionTaskMessageDTO.setContent(content);
                jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                //发送钉钉待办
                jmsTaskService.sendSupervisionTaskBacklogByTaskId(enterpriseId,supervisionTaskDO.getId());
            }else {
                //修改状态  status currentNode //完成
                supervision.setId(supervisionTaskDO.getId());
                supervision.setCurrentNode(9);
                supervision.setTaskState(SupervisionSubTaskStatusEnum.COMPLETE.getStatus());
                supervision.setCompleteTime(new Date());
                supervision.setApproveStatus(2);
                //取消钉钉待办
                cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                //取消当前节点的钉钉待办
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION.getCode());
            }
            //修改任务状态
            supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId,supervision);
        } catch (ApiException e) {
            log.info("企业配置查询失败{}",e);
        }
        return Boolean.TRUE;
    }


    /**
     * 工作通知
     * @param handleUserIdList
     * @param supervisionTaskParentDO
     * @param supervisionTaskId
     * @return
     */
    public SupervisionTaskMessageDTO supervisionTaskMessageDTO(List<String> handleUserIdList,SupervisionTaskParentDO supervisionTaskParentDO,Long supervisionTaskId,Integer taskState){
        SupervisionTaskMessageDTO supervisionTaskMessageDTO = new SupervisionTaskMessageDTO();
        supervisionTaskMessageDTO.setSupervisionTaskId(supervisionTaskId);
        SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
        supervisionTaskMessageDTO.setHandleWay(handleWay);
        supervisionTaskMessageDTO.setTaskName(supervisionTaskParentDO.getTaskName());
        Integer businessType = 0;
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getCheckStoreIds())){
            businessType = 1;
        }
        supervisionTaskMessageDTO.setBusinessType(businessType);
        supervisionTaskMessageDTO.setHandleUserIdList(handleUserIdList);
        supervisionTaskMessageDTO.setTaskState(taskState);
        supervisionTaskMessageDTO.setTitle(MessageDealDTO.APPROVE_TITLE);
        return supervisionTaskMessageDTO;
    }

    /**
     * 按门店任务状态审批
     * @param enterpriseId
     * @param supervisionApproveRequest
     * @param user
     * @return
     */
    Boolean storeTaskApprove(String enterpriseId, SupervisionApproveRequest supervisionApproveRequest, CurrentUser user){
        //按门店任务审批
        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(supervisionApproveRequest.getTaskId(), enterpriseId);
        Integer currentNode = supervisionStoreTaskDO.getCurrentNode();
        //判断是否有下一节点
        HashMap<Integer, List<String>> map = hasNextNode(supervisionStoreTaskDO.getFirstApprove(), supervisionStoreTaskDO.getSecondaryApprove(), supervisionStoreTaskDO.getThirdApprove());
        List<String> currentNodeApproveUser = map.get(currentNode);
        //判断是不是当前节点人审批
        if (!currentNodeApproveUser.contains(user.getUserId())){
            throw new ServiceException(ErrorCodeEnum.NOT_APPROVE_USER);
        }
        if (supervisionStoreTaskDO.getCancelStatus()==1||supervisionStoreTaskDO.getDeleted()){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
        }
        //完成状态是9
        List<String> list = map.get(currentNode+1);
        Integer nextNode = CollectionUtils.isNotEmpty(list)?currentNode+1:9;
        //添加执行记录
        SupervisionHistoryDO supervisionHistoryDO = supervisionTaskService.handleSupervisionHistory(supervisionApproveRequest.getTaskId(), supervisionApproveRequest.getType(), ActionTypeEnum.APPROVE.name(), user, supervisionStoreTaskDO.getCurrentNode());
        supervisionHistoryDO.setActionKey(supervisionApproveRequest.getActionKey());
        supervisionHistoryDO.setRemark(supervisionApproveRequest.getApproveRemark());
        supervisionHistoryDao.insertSelective(supervisionHistoryDO,enterpriseId);
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionStoreTaskDO.getSupervisionTaskId(), enterpriseId);

        //删除当前审批表数据
        supervisionApproveDao.batchDelete(enterpriseId,Arrays.asList(supervisionApproveRequest.getTaskId()),supervisionApproveRequest.getType());
        Boolean sendFlag = Boolean.FALSE;
        try {
            EnterpriseConfigDTO enterpriseConfigDO = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            //如果是拒绝 直接到执行人
            SupervisionStoreTaskDO supervision = new SupervisionStoreTaskDO();
            if ("reject".equals(supervisionApproveRequest.getActionKey())){
                //将状态设置为待执行
                supervision.setId(supervisionStoreTaskDO.getId());
                supervision.setTaskState(SupervisionSubTaskStatusEnum.TODO.getStatus());
                supervision.setCurrentNode(0);
                supervision.setApproveStatus(3);
                //修改任务状态
                supervisionStoreTaskDao.updateByPrimaryKeySelective(supervision,enterpriseId);
                //计算按人任务状态
                supervisionTaskService.syncTaskStatus(enterpriseId,Arrays.asList(supervisionTaskDO.getId()));
                cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId());
                //取消当前节点的钉钉待办
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION_STORE.getCode());
                //取消钉钉待办
                //发送钉钉待办 需要判断当前执行人是否任务完成了，完成了需要重新发钉钉待办
                if (supervisionTaskDO.getTaskState()==1||supervisionTaskDO.getTaskState()==4){
                    jmsTaskService.sendSupervisionTaskBacklogByTaskId(enterpriseId,supervisionTaskDO.getId());
                }
                return Boolean.TRUE;
            }

            //审批人通过
            if (CollectionUtils.isNotEmpty(list)){
                //修改状态 status currentNode
                supervision.setId(supervisionStoreTaskDO.getId());
                supervision.setCurrentNode(nextNode);
                supervision.setApproveStatus(2);
                //添加下一节点审批数据
                List<SupervisionApproveDO> supervisionApproveDOS = handleSupervisionApprove(enterpriseId, supervisionStoreTaskDO.getTaskName(), supervisionStoreTaskDO.getTaskParentId(), supervisionStoreTaskDO.getId(), supervisionApproveRequest.getType(), list);
                supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);

                //取消钉钉待办
                log.info("取消钉钉待办:{},{}",supervisionStoreTaskDO.getId(),JSONObject.toJSONString(currentNodeApproveUser));
                cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId());
                //取消当前节点的钉钉待办
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION_STORE.getCode());
                //发送工作通知
                SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);
                SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(list, supervisionTaskParentDO, supervisionTaskDO.getId(),SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                        "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                        "您有一个待审核的任务，点击前往审核。";
                supervisionTaskMessageDTO.setContent(content);
                jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                //发送钉钉待办
                sendFlag = Boolean.TRUE;
            }else {
                //修改状态  status currentNode //完成
                supervision.setId(supervisionStoreTaskDO.getId());
                supervision.setCurrentNode(9);
                supervision.setApproveStatus(2);
                supervision.setCompleteTime(new Date());
                supervision.setTaskState(SupervisionSubTaskStatusEnum.COMPLETE.getStatus());
                //取消钉钉待办
                cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId());
                //取消当前节点的钉钉待办
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getId(),currentNodeApproveUser,DingMsgEnum.SUPERVISION_STORE.getCode());

            }
            //修改任务状态
            supervisionStoreTaskDao.updateByPrimaryKeySelective(supervision,enterpriseId);
            //计算按人任务状态
            supervisionTaskService.syncTaskStatus(enterpriseId,Arrays.asList(supervisionTaskDO.getId()));
            if (sendFlag){
                log.info("发送钉钉待办 supervisionStoreId：{}",supervisionStoreTaskDO.getId());
                jmsTaskService.sendSupervisionStoreTaskBacklogByTaskId(enterpriseId,supervisionStoreTaskDO.getId());
            }


        } catch (ApiException e) {
            log.info("企业配置查询失败{}",e);
        }
        return Boolean.TRUE;
    }

    /**
     * 各级审批人
     * @param firstApprove
     * @param secondaryApprove
     * @param thirdApprove
     * @return
     */
    @Override
    public HashMap<Integer,List<String>> hasNextNode(String firstApprove,String secondaryApprove,String  thirdApprove){
        HashMap<Integer, List<String>> re = new HashMap<>();
        if (StringUtils.isNotEmpty(firstApprove)){
            re.put(1,Arrays.asList(firstApprove.substring(1,firstApprove.length()-1).split(Constants.COMMA)));
        }
        if (StringUtils.isNotEmpty(secondaryApprove)){
            re.put(2,Arrays.asList(secondaryApprove.substring(1,secondaryApprove.length()-1).split(Constants.COMMA)));
        }
        if (StringUtils.isNotEmpty(thirdApprove)){
            re.put(3,Arrays.asList(thirdApprove.substring(1,thirdApprove.length()-1).split(Constants.COMMA)));
        }
        return  re;
    }




    /**
     * 处理审批数据
     * @param enterpriseId
     * @param taskName
     * @param parentId
     * @param taskId
     * @param type
     * @param userList
     * @return
     */
    public List<SupervisionApproveDO> handleSupervisionApprove(String enterpriseId,String taskName,Long parentId ,Long taskId,String type,List<String> userList){
        List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, userList);
        Map<String, String> userNameMap = enterpriseUserSingleDTOS.stream().collect(Collectors.toMap(EnterpriseUserSingleDTO::getUserId, EnterpriseUserSingleDTO::getUserName));
        List<SupervisionApproveDO> result = new ArrayList<>();
        userList.forEach(x->{
            SupervisionApproveDO supervisionApproveDO = new SupervisionApproveDO();
            supervisionApproveDO.setCreateTime(new Date());
            supervisionApproveDO.setTaskId(taskId);
            supervisionApproveDO.setType(type);
            supervisionApproveDO.setTaskParentId(parentId);
            supervisionApproveDO.setTaskName(taskName);
            supervisionApproveDO.setApproveUserId(x);
            supervisionApproveDO.setApproveUserName(userNameMap.get(x));
            result.add(supervisionApproveDO);
        });
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void supervisionTransfer(String enterpriseId, SupervisionTransferRequest request, CurrentUser currentUser) {
        SupervisionTaskDO beforeSupervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(request.getSupervisionTaskId(), enterpriseId);
        if (beforeSupervisionTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if (beforeSupervisionTaskDO.getTaskState() != 0) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_TRANSFER_WAIT_SELF);
        }
        if (!currentUser.getUserId().equals(beforeSupervisionTaskDO.getSupervisionHandleUserId())) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_TRANSFER_SELF);
        }
        if (currentUser.getUserId().equals(request.getTransferUserId())) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_TRANSFER_NOT_SELF);
        }
        String supervisionHandleUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, request.getTransferUserId());
        Long oldSupervisionTaskId = beforeSupervisionTaskDO.getId();
        String tansUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, request.getTransferUserId());
        boolean cancelWait = false;
        int businessType = 0;
        Long msgSupervisionTaskId = oldSupervisionTaskId;
        if (CollectionUtils.isEmpty(request.getSupervisionStoreTaskIds()) &&
                Constants.SUPERVISION_TYPE_PERSON.equals(request.getType())) {
            //按人任务
            beforeSupervisionTaskDO.setSupervisionHandleUserId(request.getTransferUserId());
            beforeSupervisionTaskDO.setSupervisionHandleUserName(supervisionHandleUserName);
            beforeSupervisionTaskDO.setTransferReassignFlag(Constants.INDEX_ONE);
            beforeSupervisionTaskDO.setUpdateTime(new Date());
            supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId, beforeSupervisionTaskDO);

            SupervisionHistoryDO supervisionHistoryDO = handleSupervisionTranHistory(request.getSupervisionTaskId(), Constants.SUPERVISION_TYPE_PERSON,
                    ActionTypeEnum.TURN.name(), currentUser, request.getTransferUserId(), tansUserName, Constants.ZERO);
            supervisionHistoryDao.batchInsert(enterpriseId, Collections.singletonList(supervisionHistoryDO));
            cancelWait = true;
        } else {
            businessType = 1;
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOList;
            if(CollectionUtils.isNotEmpty(request.getSupervisionStoreTaskIds())){
                supervisionStoreTaskDOList = supervisionStoreTaskDao.listSupervisionStoreTaskIdList(enterpriseId, request.getSupervisionStoreTaskIds());
            }else {
                supervisionStoreTaskDOList = supervisionStoreTaskDao.listStoreTaskBySupervisionTaskId(enterpriseId,
                        Collections.singletonList(request.getSupervisionTaskId()), null, null, true,null);
            }
            List<String> storeIdList = supervisionStoreTaskDOList.stream().map(SupervisionStoreTaskDO::getStoreId).collect(Collectors.toList());
            SupervisionTaskDO transferSupervisionTaskDO = supervisionTaskDao.selectSupervisionTask(enterpriseId, beforeSupervisionTaskDO.getTaskParentId(), request.getTransferUserId());
            Long transferSupervisionTaskId = null;
            //移除门店id
            String checkObjectIds = beforeSupervisionTaskDO.getCheckObjectIds();
            checkObjectIds = Constants.COMMA + checkObjectIds + Constants.COMMA;
            //按门店任务
            if (transferSupervisionTaskDO == null) {
                beforeSupervisionTaskDO.setId(null);
                beforeSupervisionTaskDO.setSupervisionHandleUserId(request.getTransferUserId());
                beforeSupervisionTaskDO.setSupervisionHandleUserName(supervisionHandleUserName);
                beforeSupervisionTaskDO.setTransferReassignFlag(Constants.INDEX_ONE);
                beforeSupervisionTaskDO.setCreateTime(new Date());
                beforeSupervisionTaskDO.setCheckObjectIds(String.join(Constants.COMMA, storeIdList));
                beforeSupervisionTaskDO.setUpdateTime(new Date());
                supervisionTaskDao.insertSelective(enterpriseId, beforeSupervisionTaskDO);
                transferSupervisionTaskId = beforeSupervisionTaskDO.getId();
            } else {
                transferSupervisionTaskId = transferSupervisionTaskDO.getId();
                transferSupervisionTaskDO.setCheckObjectIds(String.format("%s%s%s", transferSupervisionTaskDO.getCheckObjectIds(), Constants.COMMA, String.join(Constants.COMMA, storeIdList)));
                transferSupervisionTaskDO.setUpdateTime(new Date());
                supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId, transferSupervisionTaskDO);
            }

            msgSupervisionTaskId = transferSupervisionTaskId;
            List<SupervisionHistoryDO> historyDOList = new ArrayList<>();
            for (SupervisionStoreTaskDO storeTaskDO : supervisionStoreTaskDOList) {
                if (storeTaskDO.getTaskState() != 0) {
                    log.info("只能转交执行中的任务,supervisionStoreTask:{}", storeTaskDO.getId());
                    continue;
                }
                storeTaskDO.setSupervisionHandleUserId(request.getTransferUserId());
                storeTaskDO.setSupervisionHandleUserName(supervisionHandleUserName);
                storeTaskDO.setTransferReassignFlag(Constants.INDEX_ONE);
                storeTaskDO.setSupervisionTaskId(transferSupervisionTaskId);
                storeTaskDO.setUpdateTime(new Date());
                supervisionStoreTaskDao.updateByPrimaryKeySelective(storeTaskDO, enterpriseId);

                SupervisionHistoryDO supervisionHistoryDO = handleSupervisionTranHistory(storeTaskDO.getId(), Constants.SUPERVISION_TYPE_STORE,
                        ActionTypeEnum.TURN.name(), currentUser, request.getTransferUserId(), tansUserName, Constants.ZERO);
                historyDOList.add(supervisionHistoryDO);
                //移除门店id
                checkObjectIds = checkObjectIds.replace(Constants.COMMA + storeTaskDO.getStoreId() + Constants.COMMA, Constants.COMMA);
            }
            if(StringUtils.isNotBlank(checkObjectIds)){
                checkObjectIds = checkObjectIds.replaceFirst(Constants.COMMA, "");
                if (StringUtils.isNotBlank(checkObjectIds)) {
                    checkObjectIds = checkObjectIds.substring(0, checkObjectIds.length() - 1);
                }
                SupervisionTaskDO supervisionTaskDOUpdate = new SupervisionTaskDO();
                supervisionTaskDOUpdate.setId(oldSupervisionTaskId);
                supervisionTaskDOUpdate.setCheckObjectIds(checkObjectIds);
                supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId, supervisionTaskDOUpdate);
            }

            List<Long> supervisionTaskIdList = new ArrayList<>();
            //任务全部转交，删除记录
            List<SupervisionStoreTaskDO> beforeSupervisionStoreTaskDOList = supervisionStoreTaskDao.getSupervisionStoreList(enterpriseId, request.getSupervisionTaskId(), currentUser.getUserId(), null, null, null);
            if (CollectionUtils.isEmpty(beforeSupervisionStoreTaskDOList)) {
                supervisionTaskDao.deleteByPrimaryKey(enterpriseId, request.getSupervisionTaskId());
            } else {
                supervisionTaskIdList.add(request.getSupervisionTaskId());
            }
            supervisionHistoryDao.batchInsert(enterpriseId, historyDOList);
            supervisionTaskIdList.add(transferSupervisionTaskId);
            supervisionTaskService.syncTaskStatus(enterpriseId, supervisionTaskIdList);
            //更新待办
            List<SupervisionStoreTaskDO> hasSupervisionStoreTaskDOList = supervisionStoreTaskDao.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId, Collections.singletonList(oldSupervisionTaskId), Boolean.TRUE, Boolean.TRUE);
            //判断门店是否全部完成，全部完成 修改按人任务状态
            if (CollectionUtils.isEmpty(hasSupervisionStoreTaskDOList)) {
                cancelWait = true;
            }
        }
        //待办取消
        if (cancelWait) {
            try {
                EnterpriseConfigDTO enterpriseConfigDO = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
                // 取消待办
                cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), oldSupervisionTaskId);
                supervisionTaskService.cancelUpcomingByPerson(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), oldSupervisionTaskId, Collections.singletonList(currentUser.getUserId()),DingMsgEnum.SUPERVISION.getCode());
            } catch (Exception e) {
                log.info("supervisionTransfer取消待办失败,eid:{},oldSupervisionTaskId:{}", enterpriseId, oldSupervisionTaskId);
            }
        }

        //发送工作
        String content = "任务名称：" + beforeSupervisionTaskDO.getTaskName() + "\n" +
                "截止时间：" + DateUtils.convertTimeToString(beforeSupervisionTaskDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                currentUser.getName() + "给您转交了任务，点击前往执行。";
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(beforeSupervisionTaskDO.getTaskParentId(), enterpriseId);
        SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
        //发送工作通知
        SupervisionTaskMessageDTO taskMessageDTO = new SupervisionTaskMessageDTO();
        taskMessageDTO.setSupervisionTaskId(msgSupervisionTaskId);
        taskMessageDTO.setHandleUserIdList(Collections.singletonList(request.getTransferUserId()));
        taskMessageDTO.setBusinessType(businessType);
        taskMessageDTO.setTaskName(beforeSupervisionTaskDO.getTaskName());
        taskMessageDTO.setHandleWay(handleWay);
        taskMessageDTO.setTitle(MessageDealDTO.SUPERVISION_HANDLE_TITLE);
        taskMessageDTO.setContent(content);
        taskMessageDTO.setTaskState(0);
        jmsTaskService.sendSupervisionTaskMessage(enterpriseId, taskMessageDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void supervisionApproveTaskTransfer(String enterpriseId, SupervisionApproveTaskTransferRequest request, CurrentUser currentUser) {
        SupervisionApproveDO supervisionApproveDO = supervisionApproveDao.getSupervisionApproveDataByTaskId(enterpriseId, currentUser.getUserId(), request.getType(), request.getTaskId());
        if (supervisionApproveDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }

        //转交后的人任务
        SupervisionApproveDO afterSupervisionApproveDO = supervisionApproveDao.getSupervisionApproveDataByTaskId(enterpriseId, request.getTransferUserId(), request.getType(), request.getTaskId());
        if (afterSupervisionApproveDO != null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_TRANSFER_HAS);
        }

        Integer nodeNo = null;
        int businessType = 0;
        if (Constants.SUPERVISION_TYPE_PERSON.equals(request.getType())) {
            SupervisionTaskDO beforeSupervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(request.getTaskId(), enterpriseId);

            if (beforeSupervisionTaskDO == null) {
                throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
            }
            List<SupervisionTaskDO> supervisionTaskDOList = new ArrayList<>();
            SupervisionTaskDO updateSupervisionTaskDO = new SupervisionTaskDO();
            updateSupervisionTaskDO.setId(beforeSupervisionTaskDO.getId());
            nodeNo = beforeSupervisionTaskDO.getCurrentNode();
            if (Constants.INDEX_THREE.equals(nodeNo)){
                updateSupervisionTaskDO.setThirdApprove(beforeSupervisionTaskDO.getThirdApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            if (Constants.INDEX_TWO.equals(nodeNo)){
                updateSupervisionTaskDO.setSecondaryApprove(beforeSupervisionTaskDO.getSecondaryApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            if (Constants.INDEX_ONE.equals(nodeNo)){
                updateSupervisionTaskDO.setFirstApprove(beforeSupervisionTaskDO.getFirstApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            supervisionTaskDOList.add(updateSupervisionTaskDO);
            //批量更新数据
            supervisionTaskDao.batchUpdateTask(enterpriseId, supervisionTaskDOList);
        } else {
            businessType = 1;
            SupervisionStoreTaskDO beforeSupervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(request.getTaskId(), enterpriseId);
            if (beforeSupervisionStoreTaskDO == null) {
                throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
            }
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOList = new ArrayList<>();
            nodeNo = beforeSupervisionStoreTaskDO.getCurrentNode();
            SupervisionStoreTaskDO updateSupervisionStoreTaskDO = new SupervisionStoreTaskDO();
            updateSupervisionStoreTaskDO.setId(beforeSupervisionStoreTaskDO.getId());
            if (Constants.INDEX_THREE.equals(nodeNo)){
                updateSupervisionStoreTaskDO.setThirdApprove(beforeSupervisionStoreTaskDO.getThirdApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            if (Constants.INDEX_TWO.equals(nodeNo)){
                updateSupervisionStoreTaskDO.setSecondaryApprove(beforeSupervisionStoreTaskDO.getSecondaryApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            if (Constants.INDEX_ONE.equals(nodeNo)){
                updateSupervisionStoreTaskDO.setFirstApprove(beforeSupervisionStoreTaskDO.getFirstApprove().replace(Constants.COMMA + currentUser.getUserId() + Constants.COMMA,
                        Constants.COMMA + request.getTransferUserId() + Constants.COMMA));
            }
            supervisionStoreTaskDOList.add(updateSupervisionStoreTaskDO);
            //批量更新数据
            supervisionStoreTaskDao.batchUpdateStoreTask(enterpriseId, supervisionStoreTaskDOList);
        }
        supervisionApproveDao.deleteByPrimaryKey(supervisionApproveDO.getId(), enterpriseId);
        supervisionApproveDO.setCreateTime(new Date());
        supervisionApproveDO.setApproveUserId(request.getTransferUserId());
        supervisionApproveDO.setApproveUserName(request.getTransferUserName());
        supervisionApproveDao.insertSelective(supervisionApproveDO, enterpriseId);
        String tansUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, request.getTransferUserId());
        SupervisionHistoryDO supervisionHistoryDO = handleSupervisionTranHistory(request.getTaskId(), request.getType(),
                ActionTypeEnum.TURN.name(), currentUser, request.getTransferUserId(), tansUserName, nodeNo);
        supervisionHistoryDao.batchInsert(enterpriseId, Collections.singletonList(supervisionHistoryDO));

        //待办取消
        try {
            EnterpriseConfigDTO enterpriseConfigDO = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            // 取消待办
            cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), request.getTaskId());
            supervisionTaskService.cancelUpcomingByPerson(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), request.getTaskId(), Collections.singletonList(currentUser.getUserId()),DingMsgEnum.SUPERVISION.getCode());
        } catch (Exception e) {
            log.info("supervisionTransfer取消待办失败,eid:{},oldSupervisionTaskId:{}", enterpriseId, request.getTaskId());
        }
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionApproveDO.getTaskParentId(), enterpriseId);

        //发送工作
        String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                currentUser.getName() + "给您转交了审批任务，点击前往审批。";
        SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
        //发送工作通知
        SupervisionTaskMessageDTO taskMessageDTO = new SupervisionTaskMessageDTO();
        taskMessageDTO.setSupervisionTaskId(request.getTaskId());
        taskMessageDTO.setHandleUserIdList(Collections.singletonList(request.getTransferUserId()));
        taskMessageDTO.setBusinessType(businessType);
        taskMessageDTO.setTaskName(supervisionTaskParentDO.getTaskName());
        taskMessageDTO.setHandleWay(handleWay);
        taskMessageDTO.setTitle(MessageDealDTO.SUPERVISION_HANDLE_TITLE);
        taskMessageDTO.setContent(content);
        taskMessageDTO.setTaskState(1);
        jmsTaskService.sendSupervisionTaskMessage(enterpriseId, taskMessageDTO);

    }

    public SupervisionHistoryDO handleSupervisionTranHistory(Long taskId, String type, String operateType, CurrentUser user,
                                                             String toUserId, String toUserName, Integer nodeNo) {
        SupervisionHistoryDO supervisionHistoryDO = new SupervisionHistoryDO();
        supervisionHistoryDO.setTaskId(taskId);
        supervisionHistoryDO.setOperateUserId(user.getUserId());
        supervisionHistoryDO.setOperateUserName(user.getName());
        supervisionHistoryDO.setOperateType(operateType);
        supervisionHistoryDO.setType(type);
        supervisionHistoryDO.setCreateTime(new Date());
        supervisionHistoryDO.setToUserId(toUserId);
        supervisionHistoryDO.setToUserName(toUserName);
        supervisionHistoryDO.setNodeNo(String.valueOf(nodeNo));
        return supervisionHistoryDO;
    }

    private void updateCompleteStatusCancelUpcoming(String enterpriseId, Long supervisionTaskId,CurrentUser user) {

        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
        Integer state = SupervisionTaskCompleteStatusEnum.YES.getCode();
        Integer handleOverTimeStatus = 0;
        if (supervisionStoreTaskDO.getTaskEndTime().getTime()<System.currentTimeMillis()){
            handleOverTimeStatus = 1;
        }
        Integer currentNode = 9;
        supervisionStoreTaskDao.updateTaskStatus(enterpriseId,supervisionTaskId,state,currentNode);

        //添加执行记录
        SupervisionHistoryDO supervisionHistoryDO = supervisionTaskService.handleSupervisionHistory(supervisionTaskId, "store", ActionTypeEnum.HANDLE.name(), user,supervisionStoreTaskDO.getCurrentNode());
        supervisionHistoryDao.batchInsert(enterpriseId,Arrays.asList(supervisionHistoryDO));

        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId, Arrays.asList(supervisionStoreTaskDO.getSupervisionTaskId()),Boolean.TRUE,Boolean.TRUE);
        //判断门店是否全部完成，全部完成 修改按人任务状态
        if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)){
            SupervisionTaskHandleRequest request = new SupervisionTaskHandleRequest();
            request.setSupervisionTaskId(supervisionStoreTaskDO.getSupervisionTaskId());
            request.setTaskState(state);
            supervisionTaskDao.updateTaskCompleteInfo(enterpriseId, request,null,handleOverTimeStatus);
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            // 取消待办
            cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), supervisionStoreTaskDO.getSupervisionTaskId());
        }
    }
}
