package com.coolcollege.intelligent.service.supervison.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskCompleteStatusEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.sign.HuShangSignUtils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.supervision.dao.*;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enums.ActionTypeEnum;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MessageDealDTO;
import com.coolcollege.intelligent.model.msg.SupervisionTaskMessageDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.coolcollege.intelligent.model.supervision.*;
import com.coolcollege.intelligent.model.supervision.dto.*;
import com.coolcollege.intelligent.model.supervision.request.SupervisionDefDataRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskHandleRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionReassignStoreVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.supervison.SupervisionDefDataColumnService;
import com.coolcollege.intelligent.service.supervison.SupervisionStoreTaskService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author wxp
 * @Date 2023/2/1 16:38
 * @Version 1.0
 */
@Service
@Slf4j
public class SupervisionTaskServiceImpl implements SupervisionTaskService {
    public static final String SUPERVISION_TASK = "SUPERVISION_TASK_";
    @Resource
    private SupervisionTaskDao supervisionTaskDao;
    @Resource
    private SupervisionTaskParentDao supervisionTaskParentDao;
    @Resource
    private SupervisionStoreTaskDao supervisionStoreTaskDao;
    @Autowired
    private StoreMapper storeMapper;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private SupervisionDefDataColumnService supervisionDefDataColumnService;

    private static String hsStrategyCenterUrl = "http://hushang.com/open/rule/check";
    private static String signKey = "T3Usaf8b2eCiX9Ljq";

    @Resource
    private SupervisionTaskService supervisionTaskService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private SupervisionDefDataColumnDao supervisionDefDataColumnDao;

    @Autowired
    private RedisUtilPool redis;

    @Resource
    private TaskSopService taskSopService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SupervisionHistoryDao supervisionHistoryDao;
    @Resource
    private SupervisionApproveDao supervisionApproveDao;
    @Resource
    private JmsTaskService jmsTaskService;

    @Resource
    SupervisionStoreTaskService supervisionStoreTaskService;
    @Resource
    SupervisionTaskParentService supervisionTaskParentService;

    @Override
    public PageInfo<SupervisionTaskVO> listMySupervisionTask(String enterpriseId, SupervisionTaskQueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        String startTime = "";
        String endTime = "";
        if (request.getStartTime()!=null||request.getEndTime()!=null){
            startTime = DateUtils.convertTimeToString(request.getStartTime(),DateUtils.DATE_FORMAT_SEC);
            endTime = DateUtils.convertTimeToString(request.getEndTime(),DateUtils.DATE_FORMAT_SEC);
        }
        List<SupervisionTaskDO> supervisionTaskDOList = supervisionTaskDao.listMySupervisionTask(enterpriseId, request,startTime,endTime,
                request.getTaskPriorityEnumList(),request.getStatusEnumList(),request.getTaskGroupingList(),request.getHandleOverTimeStatus());
        List<SupervisionTaskVO> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(supervisionTaskDOList)) {
            return new PageInfo<>(resultList);
        }
        List<Long> taskParentIdList = supervisionTaskDOList.stream().map(SupervisionTaskDO::getTaskParentId).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(supervisionTaskDOList);
        List<SupervisionTaskParentDO> taskParentDOList = supervisionTaskParentDao.listByTaskIdList(enterpriseId, taskParentIdList);
        Map<Long, SupervisionTaskParentDO> taskParentDOMap = taskParentDOList.stream().collect(Collectors.toMap(SupervisionTaskParentDO::getId, Function.identity()));

        List<Long> taskId = supervisionTaskDOList.stream().map(SupervisionTaskDO::getId).collect(Collectors.toList());
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listStoreTaskBySupervisionTaskId(enterpriseId, taskId,null,Boolean.FALSE,Boolean.FALSE,null);
        Map<Long, List<SupervisionStoreTaskDO>> storeTaskDOMap = supervisionStoreTaskDOS.stream().collect(Collectors.groupingBy(SupervisionStoreTaskDO::getSupervisionTaskId));


        List<String> allStoreIdList = ListUtils.emptyIfNull(supervisionTaskDOList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getCheckObjectIds()))
                .map(data -> StrUtil.splitTrim(data.getCheckObjectIds(), Constants.COMMA))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> storeIdNameMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(allStoreIdList)) {
            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, allStoreIdList);
            storeIdNameMap = ListUtils.emptyIfNull(storeDOList).stream()
                    .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }
        for (SupervisionTaskDO supervisionTaskDO : supervisionTaskDOList) {
            SupervisionTaskVO vo = new SupervisionTaskVO();
            BeanUtils.copyProperties(supervisionTaskDO, vo);
            SupervisionTaskParentDO supervisionTaskParentDO = taskParentDOMap.get(supervisionTaskDO.getTaskParentId());
            //即将到期标识
            vo.setExpireFlag(Boolean.FALSE);
            Date taskEndTime = supervisionTaskDO.getTaskEndTime();
            Date date =  DateUtil.getNextDay(new Date());
            if (taskEndTime.getTime()<date.getTime()&&taskEndTime.getTime()>System.currentTimeMillis()){
                vo.setExpireFlag(Boolean.TRUE);
            }
            vo.setOldDataFlag(Boolean.FALSE);
            //有门店 但是没有分解门店任务 就是老数据
            List<SupervisionStoreTaskDO> sup = storeTaskDOMap.get(supervisionTaskDO.getId());
            if (CollectionUtils.isEmpty(sup)&&StringUtils.isNotEmpty(supervisionTaskDO.getCheckObjectIds())){
                vo.setOldDataFlag(Boolean.TRUE);
            }
            Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(supervisionTaskDO.getTaskEndTime(), supervisionTaskDO.getCancelStatus(), supervisionTaskDO.getTaskState());
            vo.setTaskStatus(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
            vo.setTaskState(subStatus);
            vo.setDesc(supervisionTaskParentDO.getDescription());
            vo.setTaskParentId(supervisionTaskDO.getTaskParentId());
            if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
                vo.setApproveInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class));
            }
            vo.setTaskGrouping(supervisionTaskDO.getTaskGrouping());
            vo.setTransferReassignFlag(supervisionTaskDO.getTransferReassignFlag());
            vo.setPriority(supervisionTaskParentDO.getPriority());
            vo.setCurrentNode(supervisionTaskDO.getCurrentNode());
            vo.setCheckStoreIds(supervisionTaskParentDO.getCheckStoreIds());
            vo.setHandleWay(JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class));
            if(StringUtils.isNotBlank(supervisionTaskDO.getCheckObjectIds())) {
                vo.setCheckStoreNames(getCheckStoreNames(supervisionTaskDO.getCheckObjectIds(), storeIdNameMap));
            }
            resultList.add(vo);
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }


    @Override
    public SupervisionTaskVO getSupervisionTaskDetail(String enterpriseId, Long supervisionTaskId) {
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
        if (supervisionTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        SupervisionTaskVO supervisionTaskVO = new SupervisionTaskVO();
        BeanUtils.copyProperties(supervisionTaskDO, supervisionTaskVO);

        Boolean taskRejectFlag = Boolean.FALSE;
        List<SupervisionHistoryDO> supervisionHistoryDOS = supervisionHistoryDao.selectByTaskIdAndType(enterpriseId, supervisionTaskId, "person", Boolean.TRUE);
        if (CollectionUtils.isNotEmpty(supervisionHistoryDOS)){
            taskRejectFlag = Boolean.TRUE;
        }
        supervisionTaskVO.setTaskRejectFlag(taskRejectFlag);

        Integer taskHandleOverTimeStatus = supervisionTaskParentService.getTaskHandleOverTimeStatus(supervisionTaskDO.getHandleOverTimeStatus(), supervisionTaskDO.getTaskEndTime().getTime(), supervisionTaskDO.getTaskState());
        supervisionTaskVO.setHandleOverTimeStatus(taskHandleOverTimeStatus);
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);
        supervisionTaskVO.setDesc(supervisionTaskParentDO.getDescription());
        supervisionTaskVO.setPriority(supervisionTaskParentDO.getPriority());
        supervisionTaskVO.setOldDataFlag(Boolean.FALSE);
        supervisionTaskVO.setTaskEndTime(supervisionTaskDO.getTaskEndTime());
        supervisionTaskVO.setTaskStartTime(supervisionTaskDO.getTaskStartTime());
        supervisionTaskVO.setCurrentNode(supervisionTaskDO.getCurrentNode());
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getProcessInfo())){
            supervisionTaskVO.setApproveInfoDTO(JSONObject.parseObject(supervisionTaskParentDO.getProcessInfo(), ApproveInfoDTO.class));
        }
        EnterpriseUserSingleDTO enterpriseUserSingleDTO = new EnterpriseUserSingleDTO();
        enterpriseUserSingleDTO.setUserId(supervisionTaskDO.getSupervisionHandleUserId());
        enterpriseUserSingleDTO.setUserName(supervisionTaskDO.getSupervisionHandleUserName());
        supervisionTaskVO.setHandleUserIds(Arrays.asList(enterpriseUserSingleDTO));
        if (StringUtils.isNotEmpty(supervisionTaskDO.getFirstApprove())){
            String[] split = supervisionTaskDO.getFirstApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionTaskVO.setFirstApproveList(enterpriseUserSingleDTOS);
            }
        }
        if (StringUtils.isNotEmpty(supervisionTaskDO.getSecondaryApprove())){
            String[] split = supervisionTaskDO.getSecondaryApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionTaskVO.setSecondaryApproveList(enterpriseUserSingleDTOS);
            }
        }
        if (StringUtils.isNotEmpty(supervisionTaskDO.getThirdApprove())){
            String[] split = supervisionTaskDO.getThirdApprove().split(Constants.COMMA);
            if (split.length!=0){
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, Arrays.asList(split));
                supervisionTaskVO.setThirdApproveList(enterpriseUserSingleDTOS);
            }
        }
        //有门店 但是没有分解门店任务 就是老数据
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId, Arrays.asList(supervisionTaskId),Boolean.FALSE,Boolean.TRUE);
        if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)&&StringUtils.isNotEmpty(supervisionTaskDO.getCheckObjectIds())){
            supervisionTaskVO.setOldDataFlag(Boolean.TRUE);
        }
        Integer subStatus = SupervisionTaskParentServiceImpl.getSubStatus(supervisionTaskDO.getTaskEndTime(), supervisionTaskDO.getCancelStatus(), supervisionTaskDO.getTaskState());
        supervisionTaskVO.setTaskStatus(SupervisionSubTaskStatusEnum.getByCode(subStatus).getDesc());
        supervisionTaskVO.setTaskState(subStatus);
        if (StringUtils.isNotEmpty(supervisionTaskDO.getSopIds())){
            List<Long> sopIds = JSONObject.parseArray(supervisionTaskDO.getSopIds(), Long.class);
            List<TaskSopVO> taskSopVOS = taskSopService.listByIdList(enterpriseId, sopIds);
            supervisionTaskVO.setTaskSopVOList(taskSopVOS);
        }
        supervisionTaskVO.setHandleWay(JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class));
        if(StringUtils.isNotBlank(supervisionTaskDO.getCheckObjectIds())) {
            List<String> storeIdList = StrUtil.splitTrim(supervisionTaskDO.getCheckObjectIds(), Constants.COMMA);
            List<SingleStoreDTO> storeDOList = storeMapper.getBasicStoreStoreIdList(enterpriseId, storeIdList);
            Map<String, String> storeIdNameMap = ListUtils.emptyIfNull(storeDOList).stream()
                    .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(SingleStoreDTO::getStoreId, SingleStoreDTO::getStoreName, (a, b) -> a));
            supervisionTaskVO.setCheckStoreNames(getCheckStoreNames(supervisionTaskDO.getCheckObjectIds(), storeIdNameMap));
            supervisionTaskVO.setStoreDTOList(storeDOList);
        }
        if (supervisionTaskDO.getFormId()!=null){
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(supervisionTaskDO.getFormId()));
            supervisionTaskVO.setFormName(tbMetaTableDO.getTableName());
        }
        return supervisionTaskVO;
    }

    @Override
    public Boolean submitSupervisionTask(String enterpriseId, SupervisionTaskHandleRequest request, CurrentUser user, EnterpriseConfigDO enterpriseConfigDO) {
        return Boolean.TRUE;
    }


    @Override
    public Boolean submitSupervisionTaskByFormId(String enterpriseId, SupervisionDefDataRequest request, CurrentUser user, EnterpriseConfigDO enterpriseConfigDO) {

        SupervisionTaskDO supervisionTaskDO = null;
        SupervisionStoreTaskDO supervisionStoreTaskDO = null;
        Boolean sengUpcoming = Boolean.FALSE;
        if ("person".equals(request.getType())){
             supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(request.getTaskId(), enterpriseId);
            if (supervisionTaskDO == null) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
            }
            if (supervisionTaskDO.getTaskState()!=0){
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CURRENT_NODE_ERROR);
            }
            if (supervisionTaskDO.getCancelStatus()==1||supervisionTaskDO.getDeleted()){
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
            }
            SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey( supervisionTaskDO.getTaskParentId(),enterpriseId);
            if (!user.getUserId().equals(supervisionTaskDO.getSupervisionHandleUserId())) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
            }
            if(request.getSubmit() != null && request.getSubmit()){
                Integer state = 0;
                Integer handleOverTimeStatus = 0;
                if (System.currentTimeMillis()>supervisionTaskDO.getTaskEndTime().getTime()){
                    handleOverTimeStatus = 1;
                }
                supervisionTaskDO.setHandleOverTimeStatus(handleOverTimeStatus);
                //4、取消待办
                cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),request.getTaskId());
                //删除按人待办
                cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),request.getTaskId(),Arrays.asList(supervisionTaskDO.getSupervisionHandleUserId()),DingMsgEnum.SUPERVISION.getCode());
                //2、是否有下一节点
                HashMap<Integer, List<String>> integerListHashMap = supervisionStoreTaskService.hasNextNode(supervisionTaskDO.getFirstApprove(), supervisionTaskDO.getSecondaryApprove(), supervisionTaskDO.getThirdApprove());
                Integer currentNode = supervisionTaskDO.getCurrentNode();
                List<String> list = integerListHashMap.get(currentNode + 1);
                //不为空 有下级
                if (CollectionUtils.isNotEmpty(list)){
                    state = SupervisionSubTaskStatusEnum.APPROVAL.getStatus();
                    currentNode = currentNode + 1;
                    //3、生成审批数据
                    List<SupervisionApproveDO> supervisionApproveDOS = handleSupervisionApprove(enterpriseId, supervisionTaskDO.getTaskName(), supervisionTaskDO.getTaskParentId(), supervisionTaskDO.getId(), request.getType(), list);
                    //批量新增审批数据
                    supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);
                    supervisionTaskDO.setApproveStatus(1);
                    //新增审批人钉钉待办
                    sengUpcoming = true;
                    //新增工作通知
                    SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(list, supervisionTaskParentDO,
                            request.getTaskId(), MessageDealDTO.APPROVE_TITLE,SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                    String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                            "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                            "您有一个待审核的任务，点击前往审核。";
                    supervisionTaskMessageDTO.setContent(content);
                    jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);

                }else {
                    state = SupervisionSubTaskStatusEnum.COMPLETE.getStatus();
                    //完成时间
                    supervisionTaskDO.setCompleteTime(new Date());
                    supervisionTaskDO.setSubmitTime(new Date());
                    supervisionTaskDO.setApproveStatus(0);
                    currentNode = 9;
                }
                supervisionTaskDO.setTaskState(state);
                supervisionTaskDO.setCurrentNode(currentNode);

                //执行时间
                supervisionTaskDO.setSubmitTime(new Date());
                supervisionTaskDao.batchUpdateTaskStatus(enterpriseId, Arrays.asList(supervisionTaskDO));

                //1、添加执行记录
                SupervisionHistoryDO supervisionHistoryDO = handleSupervisionHistory(supervisionTaskDO.getId(), request.getType(), ActionTypeEnum.HANDLE.name(), user,0);
                //添加执行记录
                supervisionHistoryDao.insertSelective(supervisionHistoryDO,enterpriseId);

                if (sengUpcoming){
                    //发送审批人待办
                    log.info("发送审批人待办：id：{}",supervisionTaskDO.getId());
                    jmsTaskService.sendSupervisionTaskBacklogByTaskId(enterpriseId,supervisionTaskDO.getId());
                }
            }
        }else {
             supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(request.getTaskId(), enterpriseId);
            Integer handleOverTimeStatus = 0;
            if (System.currentTimeMillis()>supervisionStoreTaskDO.getTaskEndTime().getTime()){
                handleOverTimeStatus = 1;
            }
            SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionStoreTaskDO.getTaskParentId(), enterpriseId);
            supervisionStoreTaskDO.setHandleOverTimeStatus(handleOverTimeStatus);
            if (supervisionStoreTaskDO == null) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
            }
            if (supervisionStoreTaskDO.getTaskState()!=0){
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CURRENT_NODE_ERROR);
            }
            if (supervisionStoreTaskDO.getCancelStatus()==1||supervisionStoreTaskDO.getDeleted()){
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
            }
            if (!user.getUserId().equals(supervisionStoreTaskDO.getSupervisionHandleUserId())) {
                throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
            }
            HashMap<Integer, List<String>> integerListHashMap = supervisionStoreTaskService.hasNextNode(supervisionStoreTaskDO.getFirstApprove(), supervisionStoreTaskDO.getSecondaryApprove(), supervisionStoreTaskDO.getThirdApprove());
            Integer currentNode = supervisionStoreTaskDO.getCurrentNode();
            List<String> list = integerListHashMap.get(currentNode + 1);
            Integer state = 0;
            if (CollectionUtils.isNotEmpty(list)){
                state = SupervisionSubTaskStatusEnum.APPROVAL.getStatus();
                currentNode = currentNode + 1;
                //3、生成审批数据
                List<SupervisionApproveDO> supervisionApproveDOS = handleSupervisionApprove(enterpriseId, supervisionStoreTaskDO.getTaskName(), supervisionStoreTaskDO.getTaskParentId(), supervisionStoreTaskDO.getId(), request.getType(), list);
                //批量新增审批数据
                supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);
                //新增钉钉待办
                sengUpcoming = true;
                //新增工作通知
                SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(list, supervisionTaskParentDO, request.getTaskId(), MessageDealDTO.APPROVE_TITLE,state);
                String content = "任务名称：" + supervisionTaskParentDO.getTaskName() + "\n" +
                        "截止时间：" + DateUtils.convertTimeToString(supervisionTaskParentDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                        "您有一个待审核的任务，点击前往审核。";
                supervisionTaskMessageDTO.setContent(content);
                jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                supervisionStoreTaskDO.setApproveStatus(1);
            }else {
                state = SupervisionSubTaskStatusEnum.COMPLETE.getStatus();
                //9表示任务流程已结束
                currentNode = 9;
                supervisionStoreTaskDO.setCompleteTime(new Date());
                supervisionStoreTaskDO.setSubmitTime(new Date());
                supervisionStoreTaskDO.setApproveStatus(0);
            }
            supervisionStoreTaskDO.setSubmitTime(new Date());
            supervisionStoreTaskDO.setTaskState(state);
            supervisionStoreTaskDO.setCurrentNode(currentNode);
            supervisionStoreTaskDao.batchUpdateTaskStatus(enterpriseId, Arrays.asList(supervisionStoreTaskDO));
            //同步状态
            syncTaskStatus(enterpriseId,Arrays.asList(supervisionStoreTaskDO.getSupervisionTaskId()));
            Long aLong = supervisionStoreTaskDao.noCompleteListByTaskId(enterpriseId, supervisionStoreTaskDO.getSupervisionTaskId());
            if (aLong==0){
                cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getSupervisionTaskId());
                cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getSupervisionTaskId(),Arrays.asList(supervisionStoreTaskDO.getSupervisionHandleUserId()),DingMsgEnum.SUPERVISION.getCode());
            }
            //1、添加执行记录
            SupervisionHistoryDO supervisionHistoryDO = handleSupervisionHistory(supervisionStoreTaskDO.getId(), request.getType(), ActionTypeEnum.HANDLE.name(), user,0);
            //添加执行记录
            supervisionHistoryDao.insertSelective(supervisionHistoryDO,enterpriseId);

            if (sengUpcoming){
                //发送审批人待办
                log.info("发送审批人待办：id：{}",supervisionStoreTaskDO.getId());
                jmsTaskService.sendSupervisionStoreTaskBacklogByTaskId(enterpriseId,supervisionStoreTaskDO.getId());
            }
        }
        List<SupervisionDefDataColumnDO> result = new ArrayList<>();
        for (SupervisionDefDataDTO supervisionDefDataDTO:request.getSupervisionDefDataDTOList()) {
            SupervisionDefDataColumnDO supervisionDefDataColumnDO = new SupervisionDefDataColumnDO();
            supervisionDefDataColumnDO.setId(supervisionDefDataDTO.getId());
            supervisionDefDataColumnDO.setValue1(supervisionDefDataDTO.getValue1());
            supervisionDefDataColumnDO.setValue2(supervisionDefDataDTO.getValue2());
            supervisionDefDataColumnDO.setSupervisorId(user.getUserId());
            supervisionDefDataColumnDO.setCheckVideo(supervisionDefDataDTO.getCheckVideo());
            checkDefTableVideoHandel(supervisionDefDataColumnDO, enterpriseId);
            result.add(supervisionDefDataColumnDO);
        }
        //批量修改数据
        supervisionDefDataColumnDao.batchUpdate(enterpriseId,result);
        return Boolean.TRUE;
    }

    /**
     * 自定义检查表视频转码
     * @param enterpriseId
     */
    public void checkDefTableVideoHandel(SupervisionDefDataColumnDO supervisionDefDataDO, String enterpriseId) {
        if(StringUtils.isBlank(supervisionDefDataDO.getCheckVideo())){
            return;
        }

        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(supervisionDefDataDO.getCheckVideo(), SmallVideoInfoDTO.class);
        if (smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())) {
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    continue;
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        setDefTableNotCompleteCache(smallVideoParam, smallVideo, supervisionDefDataDO.getId(), enterpriseId);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    setDefTableNotCompleteCache(smallVideoParam, smallVideo, supervisionDefDataDO.getId(), enterpriseId);
                }
            }
            supervisionDefDataDO.setCheckVideo(JSONObject.toJSONString(smallVideoInfo));
        }
    }

    /**
     * 督导自定义检查表检查项转码设置
     *
     * @param smallVideoParam
     * @param smallVideo
     * @param businessId
     * @param enterpriseId
     */
    public void setDefTableNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long businessId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.SUPERVISION_DATA_DEF_TABLE_COLUMN.getValue());
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }


    /**
     * 任务数据提交缓存key
     * @param eid
     * @return
     */
    private String getKey(String eid,String userId) {
        return String.format(SUPERVISION_TASK,eid,userId);
    }


    @Override
    public Boolean confirmSupervisionTask(String enterpriseId, SupervisionTaskHandleRequest request, CurrentUser user) {
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(request.getSupervisionTaskId(), enterpriseId);
        if (supervisionTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        if (!user.getUserId().equals(supervisionTaskDO.getSupervisionHandleUserId())) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
        }
        if (supervisionTaskDO.getCancelStatus()==1||supervisionTaskDO.getDeleted()){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
        }
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);
        updateCompleteStatusCancelUpcoming(enterpriseId, request.getSupervisionTaskId(),user);

//        if(StringUtils.isNotBlank(supervisionTaskParentDO.getCheckCode())) {
//            executor.execute(() -> {
//                // 异步调沪上接口
//                callHsStrategyCenter(enterpriseId, supervisionTaskDO.getId(), supervisionTaskParentDO.getCheckCode());
//            });
//
//        }else {
//            updateCompleteStatusCancelUpcoming(enterpriseId, request.getSupervisionTaskId());
//        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean confirmSupervisionTasks(String enterpriseId, List<SupervisionTaskHandleRequest> request, CurrentUser user) {
        if (CollectionUtils.isNotEmpty(request)) {
            request.stream().forEach(t -> {
                SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(t.getSupervisionTaskId(), enterpriseId);
                if (supervisionTaskDO == null) {
                    throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
                }
                if (!user.getUserId().equals(supervisionTaskDO.getSupervisionHandleUserId())) {
                    throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_HANDLE_SELF);
                }
                if (supervisionTaskDO.getCancelStatus()==1||supervisionTaskDO.getDeleted()){
                    throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_CANCEL_OR_DELETED);
                }
                SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);
                updateCompleteStatusCancelUpcoming(enterpriseId, t.getSupervisionTaskId(),user);
            });
        }
        return Boolean.TRUE;
    }

    /**
     * 更新完成状态，取消钉钉待办
     * @param enterpriseId
     * @param supervisionTaskId
     */
    private void updateCompleteStatusCancelUpcoming(String enterpriseId, Long supervisionTaskId, CurrentUser user) {
        SupervisionTaskHandleRequest request = new SupervisionTaskHandleRequest();
        request.setSupervisionTaskId(supervisionTaskId);
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
        Integer state = SupervisionTaskCompleteStatusEnum.YES.getCode();
        Integer handleOverTimeStatus = 0;
        if (System.currentTimeMillis()>supervisionTaskDO.getTaskEndTime().getTime()){
            handleOverTimeStatus = 1;
        }
        Integer currentNode  = 9;
        request.setTaskState(state);
        supervisionTaskDao.updateTaskCompleteInfo(enterpriseId, request,currentNode,handleOverTimeStatus);
        supervisionStoreTaskDao.batchUpdateStateBySupervisionTaskId(enterpriseId, Arrays.asList(supervisionTaskId), state);
        //添加执行记录
        SupervisionHistoryDO supervisionHistoryDO = handleSupervisionHistory(supervisionTaskId, "person", ActionTypeEnum.HANDLE.name(), user,supervisionTaskDO.getCurrentNode());
        supervisionHistoryDao.batchInsert(enterpriseId,Arrays.asList(supervisionHistoryDO));

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        // 取消待办
        cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), supervisionTaskId);
    }

    @Override
    public Boolean batchUpdateSupervisionTaskStatus(String enterpriseId, OpenApiUpdateSupervisionTaskDTO dto) {
        log.info("沪上推送督导任务状态变更:{}", JSONObject.toJSONString(dto));
        List<Long> supervisionTaskIdList = StrUtil.splitTrim(dto.getSupervisionTaskIds(), Constants.COMMA)
                .stream().map(Long::valueOf)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(supervisionTaskIdList)){
            return Boolean.TRUE;
        }

        List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listByIds(enterpriseId, supervisionTaskIdList);
        for (SupervisionTaskDO supervisionTaskDO:supervisionTaskDOS) {
            supervisionTaskDO.setTaskState(SupervisionTaskCompleteStatusEnum.YES.getCode());
            supervisionTaskDO.setCurrentNode(9);
            supervisionTaskDO.setSubmitTime(new Date());
            supervisionTaskDO.setCompleteTime(new Date());
            if (supervisionTaskDO.getTaskEndTime().getTime()<System.currentTimeMillis()){
                supervisionTaskDO.setHandleOverTimeStatus(1);
            }
        }
        //纯人任务
        List<SupervisionTaskDO> supervisionTaskList = supervisionTaskDOS.stream().filter(x -> StringUtils.isEmpty(x.getCheckObjectIds())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(supervisionTaskList)){
            List<SupervisionHistoryDO> list = new ArrayList<>();
            CurrentUser currentUser = new CurrentUser();
            currentUser.setUserId("AI");
            currentUser.setName("系统自动执行");
            for (SupervisionTaskDO supervisionTaskDO:supervisionTaskList) {
                list.add(handleSupervisionHistory(supervisionTaskDO.getId(), "person", ActionTypeEnum.HANDLE.name(), currentUser,supervisionTaskDO.getCurrentNode()));
            }
            supervisionHistoryDao.batchInsert(enterpriseId,list);
        }
        //批量修改任务状态
        supervisionTaskDao.batchUpdateTaskStatus(enterpriseId, supervisionTaskDOS);

        List<Long> taskId = supervisionTaskDOS.stream().map(SupervisionTaskDO::getId).collect(Collectors.toList());
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listStoreTaskBySupervisionTaskId(enterpriseId, taskId, null, null, Boolean.TRUE,null);

        for (SupervisionStoreTaskDO supervisionStoreTaskDO:supervisionStoreTaskDOS) {
            supervisionStoreTaskDO.setTaskState(SupervisionTaskCompleteStatusEnum.YES.getCode());
            supervisionStoreTaskDO.setCurrentNode(9);
            supervisionStoreTaskDO.setSubmitTime(new Date());
            supervisionStoreTaskDO.setCompleteTime(new Date());
            if (supervisionStoreTaskDO.getTaskEndTime().getTime()<System.currentTimeMillis()){
                supervisionStoreTaskDO.setHandleOverTimeStatus(1);
            }
        }
        supervisionStoreTaskDao.batchUpdateTaskStatus(enterpriseId,supervisionStoreTaskDOS);

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        for(Long supervisionTaskId : supervisionTaskIdList){
            // 取消待办
            cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), supervisionTaskId);
        }
        return Boolean.TRUE;
    }

    private String getCheckStoreNames(String checkObjectIds, Map<String, String> storeIdNameMap) {
        List<String> checkStoreNameList = new ArrayList<>();
        List<String> checkStoreIdList = Arrays.asList(checkObjectIds.split(Constants.COMMA));
        for(String checkStoreId : checkStoreIdList){
            String storeName = storeIdNameMap.get(checkStoreId);
            if(StringUtils.isNotBlank(storeName)){
                checkStoreNameList.add(storeName);
            }
        }
        if(CollectionUtils.isNotEmpty(checkStoreNameList)){
            return StringUtils.join(checkStoreNameList, Constants.COMMA);
        }
        return null;
    }

    /**
     * 沪上策略中心
     * https://lcna71ca4f.feishu.cn/docx/JlW0dhYUEoKnf2xoL28cGmrVnBc
     * @param enterpriseId
     * @param supervisionTaskId
     * @param checkCode
     */
    public void callHsStrategyCenter(String enterpriseId, Long supervisionTaskId, String checkCode) {
        log.info("callHsStrategyCenter enterpriseId:{}, supervisionTaskId:{}, checkCode:{}", enterpriseId, supervisionTaskId, checkCode);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (Objects.isNull(enterpriseConfigDO)) {
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //封装调用参数
        SupervisionTaskDTO supervisionTaskDTO = new SupervisionTaskDTO();
        supervisionTaskDTO.setTaskId(supervisionTaskId);
        supervisionTaskDTO.setRuleCode(checkCode);
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        // 1~6位随机数，12345
        String nonce = String.valueOf(RandomUtils.nextInt(1,999999));
        String createdSign = HuShangSignUtils.generateSign(timeStamp, nonce, "",signKey,false);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("TIMESTAMP", String.valueOf(timeStamp));
        httpHeaders.set("NONCE", nonce);
        httpHeaders.set("SIGN", createdSign);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("callHsStrategyCenter request is {}", JSONObject.toJSONString(supervisionTaskDTO));
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(hsStrategyCenterUrl, new HttpEntity<>(JSONObject.toJSONString(supervisionTaskDTO), httpHeaders), JSONObject.class);
        log.info("callHsStrategyCenter resp {}", JSONObject.toJSONString(responseEntity));
        JSONObject response = responseEntity.getBody();
        if (Objects.nonNull(response) && (SyncConfig.STATUS_200.equals(response.getString("code")))) {
            // 返回成功 变更任务状态
            CheckResultDTO checkResultDTO = JSONObject.parseObject(response.getString("data"), CheckResultDTO.class);
            // 0:待定 1:通过 2:不通过
            if(checkResultDTO != null && checkResultDTO.getResult() == 1){
//                updateCompleteStatusCancelUpcoming(enterpriseId, supervisionTaskId);
            }
        } else {
            log.info("调用沪上策略中心异常 {}", JSONObject.toJSONString(response));
        }
    }

    @Override
    public void cancelUpcoming(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId) {
        log.info("开始删除用户待办 supervisionTaskId:{}",supervisionTaskId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.SUPERVISION.getCode().toLowerCase()+ "_" + supervisionTaskId);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public void cancelUpcomingByPerson(String enterpriseId, String dingCorpId, String appType, Long supervisionTaskId,List<String> userIdList,String taskKey) {
        log.info("开始删除用户待办 supervisionTaskId:{},userIdList:{},taskKey:{}",supervisionTaskId,JSONObject.toJSONString(userIdList),taskKey);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", taskKey.toLowerCase() + "_" + supervisionTaskId);
        jsonObject.put("appType", appType);
        jsonObject.put("userIds", userIdList);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public Boolean taskCancel(String enterpriseId, Long taskId, Long id, EnterpriseConfigDO enterpriseConfigDO) {
        //将数据变为已经取消状态
        supervisionTaskDao.taskCancel(enterpriseId, null, id);

        //有数据 是门店任务
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTaskBySupervisionTaskId(enterpriseId, Arrays.asList(id), Boolean.FALSE,Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(supervisionStoreTaskDOS)){
            List<Long> supervisionStoreTaskIdList = supervisionStoreTaskDOS.stream().filter(x -> x.getTaskState() == 4).map(SupervisionStoreTaskDO::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(supervisionStoreTaskIdList)){
                supervisionApproveDao.batchDeleteByTaskParentId(enterpriseId,supervisionStoreTaskIdList,"store",null);
                //删除审批人钉钉待办
                for (Long supervisionStoreId:supervisionStoreTaskIdList) {
                    supervisionStoreTaskService.cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreId);
                }
            }
        }else {
            supervisionApproveDao.batchDeleteByTaskParentId(enterpriseId,Arrays.asList(id),"person",null);
        }

        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(id, enterpriseId);
        if (supervisionTaskDO==null){
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
        //查询父任务下子任务(按人任务)是否全部取消 如果都取消了 父任务也要取消
        Integer noCompleteCount = supervisionTaskDao.notCancelCountByParentId(enterpriseId, taskId);
        if (noCompleteCount==0){
            supervisionTaskParentDao.taskParentCancel(enterpriseId,taskId);
        }
        //取消门店任务 根据 督导任务表中的id 取消任务
        supervisionStoreTaskDao.storeTaskCancel(enterpriseId,null,id,null);
        return Boolean.TRUE;
    }

    @Override
    public Boolean supervisionReassign(String enterpriseId,CurrentUser user, SupervisionReassignDTO supervisionReassignDTO) {
        log.info("supervisionReassign:{}",JSONObject.toJSONString(supervisionReassignDTO));
        //类型与任务ID不能为空
        if (StringUtils.isEmpty(supervisionReassignDTO.getType())||CollectionUtils.isEmpty(supervisionReassignDTO.getTaskIdList())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        if (supervisionReassignDTO.getType().equals("person")){
            //纯人任务
            List<SupervisionTaskDO> supervisionTaskDOS = supervisionTaskDao.listByIds(enterpriseId, supervisionReassignDTO.getTaskIdList());
            List<SupervisionTaskDO> list = new ArrayList<>();
            Map<Integer, List<String>> userMap = new HashMap<>();
            SupervisionTaskDO supervisionTask = supervisionTaskDOS.get(0);
            if (supervisionTask.getTaskState()==1){
                //按人任务完成 不能重新分配
                return Boolean.TRUE;
            }
            SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTask.getTaskParentId(), enterpriseId);
            supervisionTaskDOS.forEach(x->{
                SupervisionTaskDO supervisionTaskDO = new SupervisionTaskDO();
                supervisionTaskDO.setId(x.getId());
                supervisionTaskDO.setTaskState(x.getTaskState());
                supervisionTaskDO.setTaskEndTime(x.getTaskEndTime());
                supervisionTaskDO.setTaskName(x.getTaskName());
                supervisionTaskDO.setCurrentNode(x.getCurrentNode());
                if (x.getTaskState() != 1){
                    if (x.getCurrentNode()<=3&&StringUtils.isNotEmpty(x.getThirdApprove())){
                        supervisionTaskDO.setThirdApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getThirdApprove(), Constants.COMMA));
                        userMap.put(3,Arrays.asList(supervisionReassignDTO.getThirdApprove()));
                    }
                    if (x.getCurrentNode()<=2&&StringUtils.isNotEmpty(x.getSecondaryApprove())){
                        supervisionTaskDO.setSecondaryApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getSecondaryApprove(), Constants.COMMA));
                        userMap.put(2,Arrays.asList(supervisionReassignDTO.getSecondaryApprove()));
                    }
                    if (x.getCurrentNode()<=1&&StringUtils.isNotEmpty(x.getFirstApprove())){
                        supervisionTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getFirstApproveList().stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA));
                        userMap.put(1,supervisionReassignDTO.getFirstApproveList());
                    }
                    if (x.getCurrentNode()<=0){
                        supervisionTaskDO.setSupervisionHandleUserId(supervisionReassignDTO.getHandleUserId());
                        supervisionTaskDO.setSupervisionHandleUserName(supervisionReassignDTO.getHandleUserName());
                        userMap.put(0,Arrays.asList(supervisionReassignDTO.getHandleUserId()));
                    }
                    //重新分配表示
                    supervisionTaskDO.setTransferReassignFlag(2);
                    list.add(supervisionTaskDO);
                }
                //批量更新数据
                supervisionTaskDao.batchUpdateTask(enterpriseId,list);

                //按人任务 一场重新分配一个人的任务 list 最多一条数据
                list.forEach(data->{
                    //添加一条执行记录
                    SupervisionHistoryDO supervisionHistoryDO = handleSupervisionHistory(data.getId(), supervisionReassignDTO.getType(), ActionTypeEnum.REALLOCATE.name(), user,data.getCurrentNode());
                    supervisionHistoryDao.insertSelective(supervisionHistoryDO,enterpriseId);
                    //如果是审批节点 需要更新审批表数据
                    List<String> cancelUserList = new ArrayList<>();
                    if (SupervisionSubTaskStatusEnum.APPROVAL.getStatus().equals(data.getTaskState())){
                        //如果当前任务是审批节点()
                        //当前节点的审批人
                        List<String> userList = userMap.get(data.getCurrentNode());
                        //删除审批表数据
                        supervisionApproveDao.batchDelete(enterpriseId,Arrays.asList(data.getId()),supervisionReassignDTO.getType());
                        //添加新的审批人数据
                        List<SupervisionApproveDO> supervisionApproveDOS = handleSupervisionApprove(enterpriseId, x.getTaskName(), x.getTaskParentId(), x.getId(), supervisionReassignDTO.getType(), userList);
                        supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);
                        String content = "任务名称：" + data.getTaskName() + "\n" +
                                "截止时间：" + DateUtils.convertTimeToString(data.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                                user.getName()+"给您分配了审批任务，点击前往审批。";
                        SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(userList,supervisionTaskParentDO,data.getId(),MessageDealDTO.REASSIGN_TITLE,SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                        supervisionTaskMessageDTO.setContent(content);
                        cancelUserList = userList;
                        //发送消息通知
                        jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                    }
                    //发送工作通知
                    //判断是发送执行通知还是审批通知
                    if (SupervisionSubTaskStatusEnum.TODO.getStatus().equals(data.getTaskState())){
                        cancelUserList = Arrays.asList(data.getSupervisionHandleUserId(),data.getSupervisionUserId());
                        String content = "任务名称：" + data.getTaskName() + "\n" +
                                "截止时间：" + DateUtils.convertTimeToString(data.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                                user.getName()+"给您分配了任务，点击前往执行。";
                        SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(Arrays.asList(supervisionReassignDTO.getHandleUserId()),supervisionTaskParentDO,data.getId(),MessageDealDTO.REASSIGN_TITLE,SupervisionSubTaskStatusEnum.TODO.getStatus());
                        supervisionTaskMessageDTO.setContent(content);
                        jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                    }
                    log.info("重新分配 取消按人钉钉待办cancelUserList：{},id:{},status:{}",JSONObject.toJSONString(cancelUserList),supervisionTaskDO.getId(),data.getTaskState());
                    //取消钉钉待办
                    supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId());
                    //取消按人待办 不取消 之后的待办会发送失败
                    cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionTaskDO.getId(),cancelUserList,DingMsgEnum.SUPERVISION.getCode());
                });
            });

        }
        if (supervisionReassignDTO.getType().equals("store")){
            //校验最多200家
            if (supervisionReassignDTO.getTaskIdList().size()>200){
                throw new  ServiceException(ErrorCodeEnum.SUPERVISION_STORE_COUNT_LIMIT);
            }
            //门店任务
            List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listSupervisionStoreTask(enterpriseId, supervisionReassignDTO.getTaskIdList());
            //过滤掉已经完成的任务
            supervisionStoreTaskDOS = supervisionStoreTaskDOS.stream().filter(x->x.getTaskState() != 1).collect(Collectors.toList());
            //如果任务都完成 无需重新分配 直接退出
            if (CollectionUtils.isEmpty(supervisionStoreTaskDOS)){
                return Boolean.TRUE;
            }
            SupervisionStoreTaskDO supervisionStoreTask = supervisionStoreTaskDOS.get(0);
            SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionStoreTask.getTaskParentId(), enterpriseId);

            //需要计算状态的按人任务
            List<Long> taskStatus = new ArrayList<>();

            //督导任务 执行阶段任务
            List<SupervisionStoreTaskDO> list = new ArrayList<>();
            List<SupervisionStoreTaskDO> supervisionStoreHandleList = supervisionStoreTaskDOS.stream().filter(x->x.getTaskState() == 0).collect(Collectors.toList());
            Long supervisionTaskId = null;
            if (CollectionUtils.isNotEmpty(supervisionStoreHandleList)){
                //工作通知使用
                supervisionTaskId = supervisionStoreHandleList.get(0).getSupervisionTaskId();
                list.addAll(handleSupervisionStoreTask(supervisionStoreHandleList, supervisionReassignDTO));
                //判断是否有执行人的按人任务,没有执行人的按人任务 需要添加执行人的按人任务
                SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectSupervisionTask(enterpriseId,supervisionStoreTaskDOS.get(0).getTaskParentId(), supervisionReassignDTO.getHandleUserId());
                List<String> storeIds = list.stream().map(SupervisionStoreTaskDO::getStoreId).collect(Collectors.toList());
                if (supervisionTaskDO==null){
                    //添加按人任务
                    supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionStoreTaskDOS.get(0).getSupervisionTaskId(), enterpriseId);
                    supervisionTaskDO.setId(null);
                    supervisionTaskDO.setTaskState(SupervisionSubTaskStatusEnum.TODO.getStatus());
                    supervisionTaskDO.setCancelStatus(0);
                    supervisionTaskDO.setSupervisionHandleUserId(supervisionReassignDTO.getHandleUserId());
                    supervisionTaskDO.setSupervisionHandleUserName(supervisionReassignDTO.getHandleUserName());
                    supervisionTaskDO.setFirstApprove(list.get(0).getFirstApprove());
                    supervisionTaskDO.setSecondaryApprove(list.get(0).getSecondaryApprove());
                    supervisionTaskDO.setThirdApprove(list.get(0).getThirdApprove());
                    supervisionTaskDO.setCreateTime(new Date());
                    //更新门店范围
                    supervisionTaskDO.setCheckObjectIds(storeIds.stream().collect(Collectors.joining(Constants.COMMA)));
                    supervisionTaskDao.insertSelective(enterpriseId,supervisionTaskDO);
                    supervisionTaskId = supervisionTaskDO.getId();
                    //添加到重新计算状态的列表中
                    taskStatus.add(supervisionTaskDO.getId());
                }else {
                    //更新门店范围
                    if (!supervisionStoreTaskDOS.get(0).getSupervisionHandleUserId().equals(supervisionReassignDTO.getHandleUserId())){
                        supervisionTaskDO.setCheckObjectIds(String.format("%s%s%s", supervisionTaskDO.getCheckObjectIds(), Constants.COMMA, storeIds.stream().collect(Collectors.joining(Constants.COMMA))));
                    }
                    supervisionTaskId = supervisionTaskDO.getId();
                    supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId,supervisionTaskDO);
                    taskStatus.add(supervisionTaskDO.getId());
                }
                for (SupervisionStoreTaskDO supervisionStoreTaskDO:list) {
                    supervisionStoreTaskDO.setSupervisionHandleUserId(supervisionReassignDTO.getHandleUserId());
                    supervisionStoreTaskDO.setSupervisionHandleUserName(supervisionReassignDTO.getHandleUserName());
                    supervisionStoreTaskDO.setSupervisionTaskId(supervisionTaskDO.getId());
                    supervisionStoreTaskDO.setTransferReassignFlag(2);
                }
                //判断门店任务是否全部重新分配 重新分配需要删除按人任务
                Integer count = supervisionStoreTaskDao.countStoreTaskBySupervisionTaskId(enterpriseId, Arrays.asList(supervisionStoreTaskDOS.get(0).getSupervisionTaskId()));
                if (count==0||list.size()==count){
                    SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDOS.get(0);
                    supervisionTaskDao.deleteByPrimaryKey(enterpriseId,supervisionStoreTaskDO.getSupervisionTaskId());
                    //取消钉钉待办
                    supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getSupervisionTaskId());
                    //取消按人待办 不取消 之后的待办会发送失败
                    cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervisionStoreTaskDO.getSupervisionTaskId(),Arrays.asList(supervisionStoreTaskDO.getSupervisionHandleUserId()),DingMsgEnum.SUPERVISION.getCode());
                }else {
                    //添加到重新计算状态的列表中 如果删除了不需要统计状态
                    taskStatus.add(supervisionStoreHandleList.get(0).getSupervisionTaskId());
                    //如果任务没有删除 需要更新重新分配人的门店范围
                    SupervisionTaskDO supervision = supervisionTaskDao.selectByPrimaryKey(supervisionStoreTask.getSupervisionTaskId(), enterpriseId);
                    if (StringUtils.isNotEmpty(supervision.getCheckObjectIds())&&!supervision.getSupervisionHandleUserId().equals(supervisionReassignDTO.getHandleUserId())){
                        List<String> storeIdList = new ArrayList<>(Arrays.asList(supervision.getCheckObjectIds().split(Constants.COMMA)));
                        storeIdList.removeAll(storeIds);
                        supervision.setCheckObjectIds(storeIdList.stream().collect(Collectors.joining(Constants.COMMA)));
                        supervisionTaskDao.updateByPrimaryKeySelective(enterpriseId,supervision);
                    }
                }

            }
            //督导任务 待审批阶段
            List<SupervisionStoreTaskDO> supervisionStoreApproveList = supervisionStoreTaskDOS.stream().filter(x->x.getTaskState() == 4).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(supervisionStoreApproveList)){
                list.addAll(handleSupervisionStoreTask(supervisionStoreApproveList, supervisionReassignDTO));
                //审批数据处理
                List<Long> taskList = supervisionStoreApproveList.stream().map(SupervisionStoreTaskDO::getId).collect(Collectors.toList());
                //删除审批表数据
                supervisionApproveDao.batchDelete(enterpriseId,taskList,supervisionReassignDTO.getType());
                //新增审批表数据
                List<SupervisionApproveDO>  supervisionApproveDOS = new ArrayList<>();
                supervisionStoreApproveList.forEach(x->{
                    List<String> userIdList = new ArrayList<>();
                    HashMap<Integer, List<String>> currentUserList = supervisionStoreTaskService.hasNextNode(x.getFirstApprove(), x.getSecondaryApprove(), x.getThirdApprove());
                    List<String> oldUserList  = currentUserList.get(x.getCurrentNode());;
                    if (x.getCurrentNode()==1){
                        userIdList = supervisionReassignDTO.getFirstApproveList();
                    }else if (x.getCurrentNode()==2){
                        userIdList = Arrays.asList(supervisionReassignDTO.getSecondaryApprove());
                    }else if (x.getCurrentNode()==3){
                        userIdList = Arrays.asList(supervisionReassignDTO.getThirdApprove());
                    }
                    supervisionApproveDOS.addAll(handleSupervisionApprove(enterpriseId,x.getTaskName(),x.getTaskParentId(),x.getId(), supervisionReassignDTO.getType(),userIdList ));
                    //取消钉钉待办
                    supervisionStoreTaskService.cancelSupervisionStoreTaskUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),x.getId());
                    //取消按人待办 不取消 之后的待办会发送失败
                    cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),
                            x.getId(),oldUserList,DingMsgEnum.SUPERVISION_STORE.getCode());
                });
                supervisionApproveDao.batchInsert(supervisionApproveDOS,enterpriseId);
            }

            //批量更新数据执行人审批人
            supervisionStoreTaskDao.batchUpdateStoreTask(enterpriseId,list);

            //同步状态 完成状态与取消状态  更改supervisionTaskId
            if (CollectionUtils.isNotEmpty(taskStatus)) {
                syncTaskStatus(enterpriseId, taskStatus);
            }
            //取消钉钉待办 按人
            SupervisionTaskDO supervision = supervisionTaskDao.selectByPrimaryKey(supervisionStoreTask.getSupervisionTaskId(), enterpriseId);
            //不为null且不是执行状态需要删除钉钉待办 或者是取消状态
            if(supervision!=null&&(supervision.getTaskState()!=0||supervision.getCancelStatus()==1)){
                supervisionTaskService.cancelUpcoming(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervision.getId());
                //取消按人待办 不取消 之后的待办会发送失败
                cancelUpcomingByPerson(enterpriseId,enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType(),supervision.getId(),Arrays.asList(supervision.getSupervisionHandleUserId()),DingMsgEnum.SUPERVISION.getCode());
            }

            //按门店任务
            List<SupervisionHistoryDO> historyList =  new ArrayList<>();
            list.forEach(data-> {
                //添加一条执行记录
                historyList.add(handleSupervisionHistory(data.getId(), supervisionReassignDTO.getType(), ActionTypeEnum.REALLOCATE.name(), user,data.getCurrentNode()));
            });
            //添加历史记录
            supervisionHistoryDao.batchInsert(enterpriseId,historyList);

            log.info("supervisionTaskId:{}",supervisionTaskId);
            //如果supervisionStoreHandleList不为空 表示执行节点有任务 需要发送工作通知和钉钉待办
            if (CollectionUtils.isNotEmpty(supervisionStoreHandleList)){
                SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreHandleList.get(0);
                String content = "任务名称：" + supervisionStoreHandleList.get(0).getTaskName() + "\n" +
                        "截止时间：" + DateUtils.convertTimeToString(supervisionStoreHandleList.get(0).getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                        user.getName()+"给您分配了任务，点击前往执行。";
                SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(Arrays.asList(supervisionReassignDTO.getHandleUserId()),supervisionTaskParentDO,supervisionTaskId,MessageDealDTO.REASSIGN_TITLE,SupervisionSubTaskStatusEnum.TODO.getStatus());
                supervisionTaskMessageDTO.setContent(content);
                jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
            }
            //审批节点发送工作通知判断
            if (CollectionUtils.isNotEmpty(supervisionStoreApproveList)){
                Map<Integer, List<SupervisionStoreTaskDO>> supervisionMap = supervisionStoreApproveList.stream().collect(Collectors.groupingBy(SupervisionStoreTaskDO::getCurrentNode));
                HashMap<Integer, List<String>> integerListHashMap = supervisionStoreTaskService.hasNextNode(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getFirstApproveList().stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA),
                        String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getSecondaryApprove(), Constants.COMMA), String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getThirdApprove(), Constants.COMMA));
                for (int i = 1; i <= 3; i++) {
                    List<SupervisionStoreTaskDO> supervisionStoreTaskList = supervisionMap.get(i);
                    List<String> userList = integerListHashMap.get(i);
                    if (CollectionUtils.isNotEmpty(supervisionStoreTaskList)){
                        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskList.get(0);
                        supervisionTaskId = supervisionStoreTaskDO.getSupervisionTaskId();
                        String content = "任务名称：" + supervisionStoreTaskDO.getTaskName() + "\n" +
                                "截止时间：" + DateUtils.convertTimeToString(supervisionStoreTaskDO.getTaskEndTime().getTime(), DateUtils.DATE_FORMAT_SEC) + "\n" +
                                user.getName()+"给您分配了审批任务，点击前往审批。";
                        SupervisionTaskMessageDTO supervisionTaskMessageDTO = supervisionTaskMessageDTO(userList,supervisionTaskParentDO,supervisionTaskId,MessageDealDTO.REASSIGN_TITLE,SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                        supervisionTaskMessageDTO.setContent(content);
                        jmsTaskService.sendSupervisionTaskMessage(enterpriseId,supervisionTaskMessageDTO);
                    }
                }
            }
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
    public SupervisionTaskMessageDTO supervisionTaskMessageDTO(List<String> handleUserIdList,SupervisionTaskParentDO supervisionTaskParentDO,Long supervisionTaskId,String title,Integer taskState){
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
        supervisionTaskMessageDTO.setTitle(title);
        return supervisionTaskMessageDTO;
    }


    /**
     * 同步按人任务状态
     * @param taskIds
     * @return
     */
    @Override
    public Boolean syncTaskStatus(String enterpriseId,List<Long> taskIds){
        //按人任务
        List<SupervisionStoreTaskBasicDataDTO> supervisionStoreTaskBasicDataDTOS = supervisionStoreTaskDao.supervisionStoreTaskBasicData(enterpriseId, taskIds);
        Map<Long, SupervisionStoreTaskBasicDataDTO> basicDataDTOMap = supervisionStoreTaskBasicDataDTOS.stream().collect(Collectors.toMap(SupervisionStoreTaskBasicDataDTO::getSupervisionTaskId, data -> data));
        log.info("supervisionStoreTaskBasicDataDTOS:{}",JSONObject.toJSONString(supervisionStoreTaskBasicDataDTOS));
        List<SupervisionTaskDO> list = new ArrayList<>();
        taskIds.forEach(x->{
            SupervisionTaskDO supervisionTaskDO = new SupervisionTaskDO();
            supervisionTaskDO.setId(x);
            SupervisionStoreTaskBasicDataDTO supervisionStoreTaskBasicDataDTO = basicDataDTOMap.get(x);
            if (supervisionStoreTaskBasicDataDTO!=null){
                //只要有1个门店任务是待执行状态，人的任务状态就是待执行
                if (supervisionStoreTaskBasicDataDTO.getHandleNum()!=0){
                    supervisionTaskDO.setTaskState(SupervisionSubTaskStatusEnum.TODO.getStatus());
                }
                //所有的门店任务都不是待执行的时候且所有门店任务都不是已完成时
                if (supervisionStoreTaskBasicDataDTO.getHandleNum()==0&&(!supervisionStoreTaskBasicDataDTO.getFilterCancelCount().equals(supervisionStoreTaskBasicDataDTO.getCompleteNum()))){
                    supervisionTaskDO.setTaskState(SupervisionSubTaskStatusEnum.APPROVAL.getStatus());
                    supervisionTaskDO.setSubmitTime(supervisionStoreTaskBasicDataDTO.getMaxSubmitTime());
                }
                //所有的门店任务都是已完成时
                if (supervisionStoreTaskBasicDataDTO.getFilterCancelCount().equals(supervisionStoreTaskBasicDataDTO.getCompleteNum())){
                    supervisionTaskDO.setSubmitTime(supervisionStoreTaskBasicDataDTO.getMaxSubmitTime());
                    supervisionTaskDO.setCompleteTime(supervisionStoreTaskBasicDataDTO.getMaxCompleteTime());
                    supervisionTaskDO.setTaskState(SupervisionSubTaskStatusEnum.COMPLETE.getStatus());
                }
                Integer  handleOverTimeStatus = supervisionStoreTaskBasicDataDTO.getHandleOverTimeNum()!=0?1:0;
                supervisionTaskDO.setHandleOverTimeStatus(handleOverTimeStatus);
                Integer cancelStatus = supervisionStoreTaskBasicDataDTO.getCancelNum().equals(supervisionStoreTaskBasicDataDTO.getCount())?1:0;
                supervisionTaskDO.setCancelStatus(cancelStatus);
                log.info("supervisionTaskDO:{}",JSONObject.toJSONString(supervisionTaskDO));
                list.add(supervisionTaskDO);
            }
        });
        //批量修改任务状态
        supervisionTaskDao.batchUpdateTaskStatusAndCancelStatus(enterpriseId,list);
        return  Boolean.TRUE;
    }


    public List<SupervisionStoreTaskDO> handleSupervisionStoreTask(List<SupervisionStoreTaskDO> supervisionStoreTaskDOS,SupervisionReassignDTO supervisionReassignDTO) {
        List<SupervisionStoreTaskDO> list = new ArrayList<>();
        supervisionStoreTaskDOS.forEach(x->{
            SupervisionStoreTaskDO supervisionStoreTaskDO = new SupervisionStoreTaskDO();
            supervisionStoreTaskDO.setId(x.getId());
            supervisionStoreTaskDO.setStoreId(x.getStoreId());
            supervisionStoreTaskDO.setTaskState(x.getTaskState());
            supervisionStoreTaskDO.setCurrentNode(x.getCurrentNode());
            if (x.getCurrentNode() <= 3 && StringUtils.isNotEmpty(x.getThirdApprove())) {
                supervisionStoreTaskDO.setThirdApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getThirdApprove(), Constants.COMMA));
            }
            if (x.getCurrentNode() <= 2 && StringUtils.isNotEmpty(x.getSecondaryApprove())) {
                supervisionStoreTaskDO.setSecondaryApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getSecondaryApprove(), Constants.COMMA));
            }
            if (x.getCurrentNode() <= 1 && StringUtils.isNotEmpty(x.getFirstApprove())) {
                supervisionStoreTaskDO.setFirstApprove(String.format("%s%s%s", Constants.COMMA, supervisionReassignDTO.getFirstApproveList().stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA));
            }
            if (x.getCurrentNode() <= 0) {
                supervisionStoreTaskDO.setSupervisionHandleUserId(supervisionReassignDTO.getHandleUserId());
                supervisionStoreTaskDO.setSupervisionHandleUserName(supervisionReassignDTO.getHandleUserName());
            }
            //重新分配表示
            supervisionStoreTaskDO.setTransferReassignFlag(2);
            list.add(supervisionStoreTaskDO);
        });
        return list;
    }


    /**
     * 历史记录
     * @param taskId
     * @param type
     * @param operateType
     * @param user
     * @return
     */
    @Override
    public SupervisionHistoryDO handleSupervisionHistory(Long taskId,String type,String operateType,CurrentUser user,Integer currentNode){
        SupervisionHistoryDO supervisionHistoryDO = new SupervisionHistoryDO();
        supervisionHistoryDO.setTaskId(taskId);
        supervisionHistoryDO.setOperateUserId(user.getUserId());
        supervisionHistoryDO.setOperateUserName(user.getName());
        supervisionHistoryDO.setOperateType(operateType);
        supervisionHistoryDO.setCreateTime(new Date());
        supervisionHistoryDO.setType(type);
        if (currentNode!=null){
            supervisionHistoryDO.setNodeNo(String.valueOf(currentNode));
        }
        return supervisionHistoryDO;
    }

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






    @Override
    public PageInfo<SupervisionReassignStoreVO> getSupervisionReassignStore(String enterpriseId, Long taskId, String storeName, Integer pageSize, Integer pageNum) {
        //查询未被转交或者未被重新分配过的门店任务
        PageHelper.startPage(pageNum,pageSize);
        List<SupervisionStoreTaskDO> supervisionStoreTaskDOS = supervisionStoreTaskDao.listStoreTaskBySupervisionTaskId(enterpriseId, Arrays.asList(taskId),storeName, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        PageInfo supervisionStoreTaskDOPageInfo = new PageInfo<>(supervisionStoreTaskDOS);
        List<SupervisionReassignStoreVO> result = new ArrayList<>();
        SupervisionReassignStoreVO supervisionReassignStoreVO = null;
        for (SupervisionStoreTaskDO supervisionStoreTaskDO:supervisionStoreTaskDOS) {
            supervisionReassignStoreVO = new SupervisionReassignStoreVO();
            supervisionReassignStoreVO.setSupervisionStoreTaskId(supervisionStoreTaskDO.getId());
            supervisionReassignStoreVO.setStoreId(supervisionStoreTaskDO.getStoreId());
            supervisionReassignStoreVO.setStoreName(supervisionStoreTaskDO.getStoreName());
            result.add(supervisionReassignStoreVO);
        }
        supervisionStoreTaskDOPageInfo.setList(result);
        return supervisionStoreTaskDOPageInfo;
    }


}
