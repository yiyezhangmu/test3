package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.workHandover.WorkHandoverEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDao;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentUserMappingDao;
import com.coolcollege.intelligent.dao.safetycheck.dao.ScSafetyCheckUpcomingDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.AgencyMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskPersonDao;
import com.coolcollege.intelligent.dto.EnterpriseQuestionSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enums.TaskQueryEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.enums.UnifyStatus;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentUserMappingDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.dto.CommissionTotalDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskPersonTaskInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskReminderDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyPersonDTO;
import com.coolcollege.intelligent.model.unifytask.query.QuestionQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskAgencyQuery;
import com.coolcollege.intelligent.model.unifytask.vo.PatrolPlanVO;
import com.coolcollege.intelligent.model.unifytask.vo.QuestionToDoVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskAgencyVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskAgencyService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 21:12
 */
@Service
@Slf4j
public class UnifyTaskAgencyServiceImpl implements UnifyTaskAgencyService {

    @Resource
    private AgencyMapper agencyMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;
    @Autowired
    private UnifyTaskPersonService unifyTaskPersonService;
    @Resource
    private QuestionParentUserMappingDao questionParentUserMappingDao;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    private ScSafetyCheckUpcomingDao scSafetyCheckUpcomingDao;
    @Resource
    UnifyTaskPersonDao unifyTaskPersonDao;

    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;
    @Resource
    private TbPatrolPlanDao tbPatrolPlanDao;
    @Override
    public PageInfo getTaskAgencyList(String enterpriseId, TaskAgencyQuery query, CurrentUser user, Boolean isNewTodoTask) {
        String queryType = query.getQueryType();
        //校验参数--查询类型
        if (ObjectUtil.isEmpty(TaskQueryEnum.getByCode(queryType))) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        String currentUserId = user.getUserId();
        String queryUserId = user.getUserId();
        if(StrUtil.isNotEmpty(query.getUserId())){
            queryUserId = query.getUserId();
        }
        PageInfo pageInfo = new PageInfo(new ArrayList());
        List<TaskAgencyVO> result = new ArrayList<>();
        List<Long> unifyTaskIds = new ArrayList();
        switch (queryType) {
            case "create":
                // 分页查门店任务表
                PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                List<TaskStoreDO> taskStoreDOList = new ArrayList<>();
                if (isNewTodoTask) {
                    taskStoreDOList = taskStoreMapper.listMyCreateTaskByTaskTypes(enterpriseId, queryUserId, query.getStoreIdList(), query.getTaskTypes());
                } else {
                    taskStoreDOList = taskStoreMapper.listMyCreateTask(enterpriseId, queryUserId, query.getStoreIdList());
                }
                PageInfo tempPageInfo = new PageInfo(taskStoreDOList);
                List<String> subTaskCodeLoopCounts = Lists.newArrayList();
                List<String> subTaskCodes = Lists.newArrayList();
                taskStoreDOList.forEach(taskStore -> {
                    subTaskCodeLoopCounts.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId(), Constants.MOSAICS, taskStore.getLoopCount()));
                    subTaskCodes.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId()));
                });
                if (CollectionUtils.isNotEmpty(subTaskCodeLoopCounts)) {
                    //  增加父任务id条件   todo
                    result = agencyMapper.selectAgencyCreateOrCCListByTaskIds(enterpriseId, subTaskCodeLoopCounts, subTaskCodes);
                    result.stream().forEach(a->{
                        unifyTaskIds.add(a.getUnifyTaskId());
                    });
                    //时间加工
                    this.getHandlerEndTime(enterpriseId,result,unifyTaskIds);
                    //同任务同门店取一个任务显示
                    result = allSubGroup(result, queryUserId);
                    getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
                    pageInfo = new PageInfo(result);
                    pageInfo.setTotal(tempPageInfo.getTotal());
                    pageInfo.setPageNum(query.getPageNumber());
                    pageInfo.setPageSize(query.getPageSize());
                }
                break;
            case "pending":
                //待处理
                //查询是否企业配置逾期代办是否显示
                DataSourceHelper.reset();
                EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
                DataSourceHelper.changeToMy();
                PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                if (isNewTodoTask) {
                    //新版的待办  按照多个taskTypes查询
                    if (CollectionUtils.isNotEmpty(query.getTaskTypes()) && query.getTaskTypes().contains("PATROL_STORE_INFORMATION")){
                        result = agencyMapper.selectTodoTaskList(
                                enterpriseId,
                                queryUserId,
                                storeCheckSettingDO.getOverdueTaskContinue(),
                                storeCheckSettingDO.getHandlerOvertimeTaskContinue(),
                                storeCheckSettingDO.getApproveOvertimeTaskContinue(),
                                query.getStoreId(),
                                query.getTaskTypes(),
                                query.getStoreIdList());
                    }else {
                        result = agencyMapper.selectNewTodoTaskList(
                                    enterpriseId,
                                    queryUserId,
                                    storeCheckSettingDO.getOverdueTaskContinue(),
                                    storeCheckSettingDO.getHandlerOvertimeTaskContinue(),
                                    storeCheckSettingDO.getApproveOvertimeTaskContinue(),
                                    query.getStoreId(),
                                    query.getTaskTypes(),
                                    query.getStoreIdList());
                    }
                } else {
                    result = agencyMapper.selectAgencyPendingListNew(
                            enterpriseId,
                            queryUserId,
                            storeCheckSettingDO.getOverdueTaskContinue(),
                            storeCheckSettingDO.getHandlerOvertimeTaskContinue(),
                            storeCheckSettingDO.getApproveOvertimeTaskContinue(),
                            query.getStoreId(),
                            query.getTaskType(),
                            query.getStoreIdList());
                }
                getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
                result.stream().forEach(a->{
                    unifyTaskIds.add(a.getUnifyTaskId());
                });
                //时间加工
                this.getHandlerEndTime(enterpriseId,result,unifyTaskIds);
                pageInfo = new PageInfo(result);
                break;
            case "cc":
                // 分页查门店任务表
                Date createTime = DateUtils.addDays(new Date(), -30);
                PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                List<TaskStoreDO> taskStoreCCList = new ArrayList<>();
                if (isNewTodoTask) {
                    taskStoreCCList = agencyMapper.selectCreateOrCCTodoTaskList(enterpriseId, queryUserId, query.getStoreId(), query.getTaskTypes(), createTime,
                            query.getStoreIdList());
                } else {
                    taskStoreCCList = agencyMapper.selectAgencyCreateOrCCTaskIdList(enterpriseId, queryUserId, query.getStoreId(), query.getTaskType(), createTime,
                            query.getStoreIdList());
                }
                PageInfo tempPageInfoCC = new PageInfo(taskStoreCCList);
                List<String> subTaskCodeLoopCountCCs = Lists.newArrayList();
                List<String> subTaskCodeCCs = Lists.newArrayList();
                taskStoreCCList.forEach(taskStore -> {
                    subTaskCodeLoopCountCCs.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId(), Constants.MOSAICS, taskStore.getLoopCount()));
                    subTaskCodeCCs.add(StringUtils.join(taskStore.getUnifyTaskId(), Constants.MOSAICS, taskStore.getStoreId()));
                });
                if (CollectionUtils.isNotEmpty(subTaskCodeLoopCountCCs)) {
                    result = agencyMapper.selectAgencyCreateOrCCListByTaskIds(enterpriseId, subTaskCodeLoopCountCCs, subTaskCodeCCs);
                    result.stream().forEach(a->{
                        unifyTaskIds.add(a.getUnifyTaskId());
                    });
                    //时间加工
                    this.getHandlerEndTime(enterpriseId,result,unifyTaskIds);
                    //同任务同门店取一个任务显示
                    result = allSubGroup(result, queryUserId);
                    getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
                    pageInfo = new PageInfo(result);
                    pageInfo.setTotal(tempPageInfoCC.getTotal());
                    pageInfo.setPageNum(query.getPageNumber());
                    pageInfo.setPageSize(query.getPageSize());
                }
                break;
            default:
                //全部
                boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId,queryUserId);
                if(!isAdmin || "993bc9ea70cb4d798b740b41ac0c8a3d_bak".equals(enterpriseId)){//FIXME jeffrey
                    // 如果需要过滤父任务，只查询最多500个父任务ID 【同产品说明下】
                    List<Long> taskIds =  Lists.newArrayList(); // taskMappingMapper.selectTaskIdByPersonRole(enterpriseId, null, queryUserId);
                    if(CollectionUtils.isNotEmpty(taskIds)){
//                        if(UnifyTaskQueryEnum.COMPLETE.getCode().equals(query.getStatus())){
//                            //已完成只会有一条无需分组统计
//                            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
//                            result = agencyMapper.selectAgencyAllListNew(enterpriseId, query, taskIds);
//                            getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
//                            pageInfo = new PageInfo(result);
//                        }else{
                            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                            result = agencyMapper.selectAgencyAllListNew(enterpriseId, query, taskIds, query.getStoreIdList());
                            //同任务同门店取一个任务显示
                            // result = allSubGroup(result, userId);
                            //Integer total = result.size();
                            //ListPageInfo<TaskAgencyVO> resultPage = PageInfoUtil.getListPageInfo(result, query.getPageNumber(), query.getPageSize());
                            //result = resultPage.getList();
                            getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
                            pageInfo = new PageInfo(result);
                            //pageInfo.setTotal(total);
//                        }
                    }
                }else {
//                    if(UnifyTaskQueryEnum.COMPLETE.getCode().equals(query.getStatus())){
                        //已完成只会有一条无需分组统计
                        query.setPageSize(query.getPageSize()*20);
                        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
                        result = agencyMapper.selectAgencyAllListNew(enterpriseId, query, null, query.getStoreIdList());
                        getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
                        pageInfo = new PageInfo(result);
//                    }else{
//                        query.setPageSize(query.getPageSize()*20);
//                        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
//                        result = agencyMapper.selectAgencyAllListNew(enterpriseId, query, null);
//                        //同任务同门店取一个任务显示
//                        // result = allSubGroup(result, userId);
//                        //Integer total = result.size();
//                        //ListPageInfo<TaskAgencyVO> resultPage = PageInfoUtil.getListPageInfo(result, query.getPageNumber(), query.getPageSize());
//                        //result = resultPage.getList();
//                        getSublistAbout(result, enterpriseId, currentUserId, queryUserId);
//                        pageInfo = new PageInfo(result);
//                        //pageInfo.setTotal(total);
//                    }
                }
                break;
        }

        return pageInfo;
    }

    /**
     * 第一次处理时间+第一次审批时间加工
     * @param enterpriseId
     * @param result
     * @param UnifyTaskIds
     */
    private void getHandlerEndTime(String enterpriseId , List<TaskAgencyVO> result, List<Long> UnifyTaskIds){
        if (CollectionUtils.isNotEmpty(UnifyTaskIds)){
            List<TbDisplayTableRecordDO> TbDisplayTableRecordList = tbDisplayTableRecordMapper.listByUnifyTaskIdList(enterpriseId, UnifyTaskIds);
            if (TbDisplayTableRecordList!=null){
                result.stream().forEach(a->{
                    TbDisplayTableRecordList.stream().forEach(tbDisplayTableRecordDO -> {
                        if (a.getUnifyTaskId().equals(tbDisplayTableRecordDO.getUnifyTaskId())&&a.getLoopCount().equals(tbDisplayTableRecordDO.getLoopCount())
                                &&a.getStoreId().equals(tbDisplayTableRecordDO.getStoreId())){
                            a.setFirstHandlerTime(tbDisplayTableRecordDO.getFirstHandlerTime());
                            a.setFirstApproveTime(tbDisplayTableRecordDO.getFirstApproveTime());
                        }
                    });
                });
            }
        }

    }


    /**
     * 查询全部子任务专属分组方法
     * 遇到当前用户为某个节点处理人时，优先选择属于当前处理人的任务
     * @param oldList
     * @return
     */
    private List<TaskAgencyVO> allSubGroup(List<TaskAgencyVO> oldList, String userId) {
        if (CollectionUtils.isEmpty(oldList)) {
            return new ArrayList<>();
        }
        List<TaskAgencyVO> newList = Lists.newArrayList();
        Map<String, List<TaskAgencyVO>> collect = oldList.stream().collect(Collectors.groupingBy(e ->
                StringUtils.join(e.getSubTaskCode(), Constants.MOSAICS,  e.getLoopCount())));
        for (Map.Entry<String, List<TaskAgencyVO>> entry : collect.entrySet()) {
            List<TaskAgencyVO> mapValue = entry.getValue();
            Long maxTime = mapValue.get(0).getCreateTime();
            Long firstHandleTime = mapValue.get(0).getHandleTime();
            if (ObjectUtil.isNotEmpty(firstHandleTime)) {
                maxTime = firstHandleTime;
            }
            //一次遍历获取list中最新时间的审批记录
            int choice = 0;
            for (int i = 0; i < mapValue.size(); i++) {
                TaskAgencyVO item = mapValue.get(i);
                Long time = item.getCreateTime();
                if (ObjectUtil.isNotEmpty(item.getHandleTime())) {
                    time = item.getHandleTime();
                }
                if (time > maxTime) {
                    maxTime = time;
                    choice = i;
                }
            }
            TaskAgencyVO subVO = mapValue.get(choice);
            //TODO jjx 目前只有或签
            if(UnifyStatus.ONGOING.getCode().equals(subVO.getSubStatus())){
                List<TaskAgencyVO> sameSubList = mapValue.stream().filter(s -> s.getGroupSign().equals(
                        StringUtils.join(s.getUnifyTaskId(), '#', s.getStoreId(), '#', s.getGroupItem(), '#', s.getFlowNodeNo()))
                        && UnifyStatus.ONGOING.getCode().equals(s.getSubStatus())).collect(Collectors.toList());
                List<TaskAgencyVO> selfSub = ListUtils.emptyIfNull(sameSubList).stream().filter(s->userId.equals(s.getHandleUserId())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(selfSub)){
                    newList.add(selfSub.get(0));
                }else{
                    newList.add(subVO);
                }
            }else{
                newList.add(subVO);
            }
        }
        if(CollectionUtils.isNotEmpty(newList)){
            newList = newList.stream().sorted(Comparator.comparing(TaskAgencyVO::getCreateTime).reversed())
                    .collect(Collectors.toList());
        }
        return newList;
    }


    /**
     * 子任务列表页面获取子任务详情的一些关联数据
     *
     * @param result
     */
    private void getSublistAbout(List<TaskAgencyVO> result, String enterpriseId, String currentUserId, String queryUserId) {
        if (CollectionUtils.isNotEmpty(result)) {

            //  填充父任务信息
            fillParentTaskInfo(enterpriseId, result);

            //查询是否企业配置逾期代办是否显示
            DataSourceHelper.reset();
            EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
            Boolean overTask = storeCheckSettingDO.getOverdueTaskContinue();
            Boolean handlerOvertimeTaskContinue = storeCheckSettingDO.getHandlerOvertimeTaskContinue();
            Boolean approveOvertimeTaskContinue = storeCheckSettingDO.getApproveOvertimeTaskContinue();
            DataSourceHelper.changeToMy();
            //创建人处理人员 其他他相关人员汇总
            Set<String> allUseIdList = Sets.newHashSet();
            List<Long> taskIds = Lists.newArrayList();
            //问题工单id
            List<Long> questionIds = Lists.newArrayList();
            List<String> storeIdList = Lists.newArrayList();
            result.forEach(item->{
                allUseIdList.add(item.getTaskCreateUserId());
                allUseIdList.add(item.getCreateUserId());
                allUseIdList.add(item.getHandleUserId());
                taskIds.add(item.getUnifyTaskId());
                if(TaskTypeEnum.QUESTION_ORDER.getCode().equals(item.getTaskType())){
                    questionIds.add(item.getUnifyTaskId());
                }
                storeIdList.add(item.getStoreId());
            });
            //获取任务相关人员
            Map<String, List<UnifyPersonDTO>> personMap = getTaskPerson(enterpriseId, taskIds, storeIdList);
            //涉及人员名称补充
            Map<String, String> useMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(allUseIdList));
            Map<Long,Long> mappingMap = new HashMap<>();
            Map<Long, TbMetaStaTableColumnDO> columnMap = new HashMap<>();
            log.info("getSublistAbout enterpriseUserDOList:{}",JSONObject.toJSONString(useMap));
            //问题工单带上检查项信息
            log.info("getSublistAbout questionIds：{}",JSONObject.toJSONString(questionIds));
            if(CollectionUtils.isNotEmpty(questionIds)){
                List<TaskMappingDO> taskMappingDOS = taskMappingMapper.selectMappingByTaskIds(enterpriseId,questionIds);
                List<Long> columnIds = new ArrayList<>();
                for(TaskMappingDO taskMappingDO : taskMappingDOS){
                    mappingMap.put(taskMappingDO.getUnifyTaskId(),taskMappingDO.getOriginMappingId());
                    columnIds.add(taskMappingDO.getOriginMappingId());
                }
                if(CollectionUtils.isNotEmpty(columnIds)){
                    List<TbMetaStaTableColumnDO> columnDOList = tbMetaStaTableColumnMapper.selectByIds(enterpriseId,columnIds);
                    for(TbMetaStaTableColumnDO columnDO : columnDOList){
                        columnMap.put(columnDO.getId(),columnDO);
                    }
                }
            }
            log.info("getSublistAbout result",JSONObject.toJSONString(result));
            for (TaskAgencyVO m : result) {
                String mapTaskCreateName = useMap.get(m.getTaskCreateUserId());
                String taskCreateUserName ;
                if(Constants.SYSTEM_USER_ID.equals(m.getTaskCreateUserId())){
                    taskCreateUserName = Constants.SYSTEM_USER_NAME;
                } else if(Constants.AI.equals(m.getTaskCreateUserId())){
                    taskCreateUserName = Constants.AI;
                } else {
                    taskCreateUserName = mapTaskCreateName;
                }
                String mapCreateName = useMap.get(m.getCreateUserId());
                String createUserName;
                if(Constants.SYSTEM_USER_ID.equals(m.getCreateUserId())){
                    createUserName = Constants.SYSTEM_USER_NAME;
                } else if(Constants.AI.equals(m.getCreateUserId())){
                    createUserName = Constants.AI;
                } else {
                    createUserName = mapCreateName;
                }

                m.setCreateUserName(createUserName);
                m.setHandleUserName(useMap.get(m.getHandleUserId()));
                m.setTaskCreateUserName(taskCreateUserName);
                if (m.getSubEndTime() < System.currentTimeMillis()) {
                    m.setExpireFlag(Boolean.TRUE);
                } else {
                    m.setExpireFlag(Boolean.FALSE);
                }
                //相关人员处理
                String subTaskCode = StringUtils.join(m.getUnifyTaskId(),"#", m.getStoreId(),"#", m.getLoopCount());
                List<UnifyPersonDTO> person = personMap.get(subTaskCode);
                Map<String, List<PersonDTO>> processUser = ListUtils.emptyIfNull(person).stream()
                        .collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                                Collectors.mapping(s -> new PersonDTO(s.getUserId(), s.getUserName(), s.getAvatar()), Collectors.toList())));
                m.setProcessUser(processUser);
                //用户操作权限判断  查询和当前不一样员工待办  不可点击
                if(m.getSubBeginTime() > System.currentTimeMillis() || !currentUserId.equals(queryUserId)){
                    //未开始不可编辑
                    m.setEditFlag(Boolean.FALSE);
                    m.setTurnFlag(Boolean.FALSE);
                }else{
                    dealButtonShow(m, processUser, currentUserId, overTask, handlerOvertimeTaskContinue, approveOvertimeTaskContinue);
                }
                m.setColumn(columnMap.get(mappingMap.get(m.getUnifyTaskId())));
            }
        }
    }

    // 子任务列表填充父任务相关信息
    private void fillParentTaskInfo(String enterpriseId, List<TaskAgencyVO> result) {
        if (CollectionUtils.isNotEmpty(result)) {
            List<Long> taskIds = result.stream().map(data -> data.getUnifyTaskId()).collect(Collectors.toList());
            List<Long> subTaskIdList = result.stream().map(data -> data.getSubTaskId()).collect(Collectors.toList());
            List<TaskParentDO> taskParentList = taskParentMapper.selectTaskByIds(enterpriseId, taskIds);
            Map<Long, TaskParentDO> taskParentMap = taskParentList.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));
            List<UnifyTaskPersonDO> taskPersonDOList = unifyTaskPersonService.listBySubTaskIdList(enterpriseId, subTaskIdList);
            Map<Long, UnifyTaskPersonDO> taskPersonDOMap = Maps.newHashMap();
            if(CollectionUtils.isNotEmpty(taskPersonDOList)){
                taskPersonDOMap = taskPersonDOList.stream().collect(Collectors.toMap(UnifyTaskPersonDO::getSubTaskId, Function.identity(), (a, b) -> a));
            }

            for (TaskAgencyVO m : result) {
                TaskParentDO taskParentDO = taskParentMap.get(m.getUnifyTaskId());
                if(Objects.isNull(taskParentDO)){
                    continue;
                }
                m.setTaskName(taskParentDO.getTaskName());
                m.setBeginTime(taskParentDO.getBeginTime());
                m.setEndTime(taskParentDO.getEndTime());
                m.setNodeInfo(taskParentDO.getNodeInfo());
                m.setTaskCreateUserId(taskParentDO.getCreateUserId());
                m.setTaskType(taskParentDO.getTaskType());
                m.setTaskInfo(taskParentDO.getTaskInfo());
                m.setTaskDesc(taskParentDO.getTaskDesc());
                if(TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(m.getTaskType()) && taskPersonDOMap.get(m.getSubTaskId()) != null){
                    // 查询按人任务的巡店要求
                    UnifyTaskPersonDO unifyTaskPersonDO = taskPersonDOMap.get(m.getSubTaskId());
                    String parentTaskInfoStr = taskParentDO.getTaskInfo();
                    TaskPersonTaskInfoDTO parentTaskInfo = JSONObject.parseObject(parentTaskInfoStr, TaskPersonTaskInfoDTO.class);
                    if(Objects.nonNull(unifyTaskPersonDO)){
                        parentTaskInfo.setExecuteDemand(JSONObject.parseObject(unifyTaskPersonDO.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam());
                        m.setTaskInfo(JSONObject.toJSONString(parentTaskInfo));
                    }

                }
            }
        }
    }

    /**
     * 用户操作权限判断
     * @param m
     * @param user
     * @param userId
     */
    private void dealButtonShow(TaskAgencyVO m, Map<String, List<PersonDTO>> user,
                                String userId, Boolean overTask, Boolean handlerOvertimeTaskContinue, Boolean approveOvertimeTaskContinue) {

        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(m.getTaskType())) {
            if (m.getSubEndTime() < System.currentTimeMillis()) {
                //待处理已逾期
                if (UnifyNodeEnum.FIRST_NODE.getCode().equals(m.getFlowNodeNo()) && !handlerOvertimeTaskContinue) {
                    m.setEditFlag(Boolean.FALSE);
                    m.setTurnFlag(Boolean.FALSE);
                    return;
                }
                //待审批已逾期
                if (UnifyNodeEnum.isApproveNode(m.getFlowNodeNo())
                        && !approveOvertimeTaskContinue) {
                    m.setEditFlag(Boolean.FALSE);
                    m.setTurnFlag(Boolean.FALSE);
                    return;
                }
            }
        } else {
            //已逾期并且，企业配置逾期任务不可执行，不允许任务操作
            if (m.getSubEndTime() < System.currentTimeMillis() && !overTask) {
                m.setEditFlag(Boolean.FALSE);
                m.setTurnFlag(Boolean.FALSE);
                return;
            }
        }

        Boolean editFlag = Boolean.FALSE;
        Boolean turnFlag = Boolean.FALSE;
        if (UnifyStatus.ONGOING.getCode().equals(m.getSubStatus())) {
            List<String> processUser  = ListUtils.emptyIfNull(user.get(m.getFlowNodeNo())).stream().map(PersonDTO::getUserId).collect(Collectors.toList());
            switch (m.getFlowNodeNo()) {
                case "1":
                    if (userId.equals(m.getHandleUserId())) {
                        editFlag = Boolean.TRUE;
                        turnFlag = Boolean.TRUE;
                    }
                    break;
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                    if (processUser.contains(userId)) {
                        editFlag = Boolean.TRUE;
                        turnFlag = Boolean.TRUE;
                    }
                    break;
                default:
                    break;
            }
        }
        m.setEditFlag(editFlag);
        m.setTurnFlag(turnFlag);
    }

    /**
     * 分组获取人员
     *
     * @param enterpriseId
     * @param taskIdList
     * @return
     */
    private Map<String, List<UnifyPersonDTO>> getTaskPerson(String enterpriseId, List<Long> taskIdList, List<String> storeIdList) {
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, taskIdList, storeIdList, null);
        Map<String, List<UnifyPersonDTO>> personMap = unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(e -> e.getSubTaskCode() + Constants.MOSAICS + e.getLoopCount()));
        return personMap;
    }

    @Override
    public PageInfo getReminderList(String enterpriseId, TaskReminderDTO query) {
        PageInfo pageInfo;
        List<TaskAgencyVO> result;
        List<Long> unifyTaskIds = new ArrayList();
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        result = agencyMapper.selectUnifyTaskPendingSub(enterpriseId, query.getUserId(), storeCheckSettingDO.getOverdueTaskContinue(),
                storeCheckSettingDO.getHandlerOvertimeTaskContinue(), storeCheckSettingDO.getApproveOvertimeTaskContinue(),
                query.getUnifyTaskId(), query.getLoopCount());
        getSublistAbout(result, enterpriseId, query.getUserId(), query.getUserId());
        result.stream().forEach(a -> {
            unifyTaskIds.add(a.getUnifyTaskId());
        });
        //时间加工
        this.getHandlerEndTime(enterpriseId, result, unifyTaskIds);
        pageInfo = new PageInfo(result);
        return pageInfo;
    }

    @Override
    public PageInfo<QuestionToDoVO> questionToDoList(String enterpriseId, QuestionQuery query, CurrentUser user) {
        String queryUserId = user.getUserId();
        if(StrUtil.isNotEmpty(query.getUserId())){
            queryUserId = query.getUserId();
        }
        Boolean isHandleUser = null;
        if(TaskQueryEnum.PENDING.getCode().equals(query.getQueryType())){
            isHandleUser = true;
        }
        Boolean isCcUser = null;
        if(TaskQueryEnum.CC.getCode().equals(query.getQueryType())){
            isCcUser = true;
        }
        List<TbQuestionParentInfoDO> parentInfoDOList = new ArrayList<>();
        PageInfo pageInfo;
        if(TaskQueryEnum.PENDING.getCode().equals(query.getQueryType()) || TaskQueryEnum.CC.getCode().equals(query.getQueryType())){
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            Integer status = 0;
            //抄送不需要状态
            if(TaskQueryEnum.CC.getCode().equals(query.getQueryType())){
                status = null;
            }
            EnterpriseQuestionSettingsDTO questionSettingsDTO = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
            log.info("questionToDoList#questionSettingsDTO:{}", JSONObject.toJSONString(questionSettingsDTO));
            List<TbQuestionParentUserMappingDO> list = questionParentUserMappingDao.list(enterpriseId, queryUserId, query.getTaskName(),isHandleUser,
                    isCcUser, status, questionSettingsDTO.getQuestionExpireHandle(), questionSettingsDTO.getQuestionExpireApprove());
            pageInfo = new PageInfo(list);
            if(CollectionUtils.isEmpty(list)){
                return pageInfo;
            }
            List<Long> questionPatentIdList = list.stream().map(TbQuestionParentUserMappingDO::getQuestionParentId).collect(Collectors.toList());
            parentInfoDOList = questionParentInfoDao.selectByIdList(enterpriseId, questionPatentIdList);
            Map<Long, TbQuestionParentInfoDO> parentInfoDOMap = parentInfoDOList.stream().collect(Collectors.toMap(TbQuestionParentInfoDO::getId, Function.identity()));
            List<TbQuestionParentInfoDO> tbQuestionParentInfoDOList = new ArrayList<>();
            list.forEach(e -> {
                TbQuestionParentInfoDO parentInfoDO = parentInfoDOMap.get(e.getQuestionParentId());
                if(parentInfoDO != null){
                    tbQuestionParentInfoDOList.add(parentInfoDO);
                }
            });
            parentInfoDOList = tbQuestionParentInfoDOList;
        }else {
            QuestionParentRequest request = new QuestionParentRequest();
            request.setCreateUserIdList(Collections.singletonList(queryUserId));
            request.setQuestionName(query.getTaskName());
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            parentInfoDOList = questionParentInfoDao.list(enterpriseId, request);
            pageInfo = new PageInfo(parentInfoDOList);
        }
        if(CollectionUtils.isEmpty(parentInfoDOList)){
            return pageInfo;
        }
        Set<String> userIdSet = parentInfoDOList.stream().map(TbQuestionParentInfoDO::getCreateId).collect(Collectors.toSet());
        // 查询用户
        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(userIdSet));

        List<QuestionToDoVO> resultList = new ArrayList<>();
        List<Long> unifyTaskIds = parentInfoDOList.stream().map(TbQuestionParentInfoDO::getUnifyTaskId).collect(Collectors.toList());
        List<TbQuestionRecordDO> tbQuestionRecordDOS = tbQuestionRecordMapper.selectByUnifyTaskIds(enterpriseId, unifyTaskIds);
        Map<Long, List<TbQuestionRecordDO>> recordMap = tbQuestionRecordDOS.stream().collect(Collectors.groupingBy(TbQuestionRecordDO::getUnifyTaskId));
        parentInfoDOList.forEach(infoDO -> {
            QuestionToDoVO toDoVO = new QuestionToDoVO();
            toDoVO.setId(infoDO.getId());
            toDoVO.setQuestionName(infoDO.getQuestionName());
            toDoVO.setQuestionType(infoDO.getQuestionType());
            toDoVO.setCreateName(userMap.get(infoDO.getCreateId()));
            toDoVO.setCreateId(infoDO.getCreateId());
            toDoVO.setCreateTime(infoDO.getCreateTime());
            toDoVO.setFinishNum(infoDO.getFinishNum());
            toDoVO.setTotalNum(infoDO.getTotalNum());
            List<TbQuestionRecordDO> tbQuestionRecordDOS1 = recordMap.get(infoDO.getUnifyTaskId());
            if(CollectionUtils.isNotEmpty(tbQuestionRecordDOS1)){
                Date subRecordEndTime = tbQuestionRecordDOS1.stream().min(Comparator.comparing(TbQuestionRecordDO::getSubEndTime)).map(TbQuestionRecordDO::getSubEndTime).orElse(null);
                toDoVO.setSubRecordEndTime(subRecordEndTime);
            }
            resultList.add(toDoVO);
        });
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public CommissionTotalDTO getTotal(String enterpriseId, TaskAgencyQuery query) {
        String queryUserId = query.getUserId();
        Integer status = 0;
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        //新版的待办  按照多个taskTypes查询
        CommissionTotalDTO commissionTotalDTO = agencyMapper.selectTodoTaskListCount(enterpriseId, queryUserId, storeCheckSettingDO.getOverdueTaskContinue(),
                storeCheckSettingDO.getHandlerOvertimeTaskContinue(), storeCheckSettingDO.getApproveOvertimeTaskContinue(), query.getStoreId(), query.getTaskTypes()
                , query.getStoreIdList());

        Long safetyCheckCount = scSafetyCheckUpcomingDao.totoListCount(enterpriseId, queryUserId);
        commissionTotalDTO.setSafetyCheckTotal(safetyCheckCount);
        EnterpriseQuestionSettingsDTO questionSettingsDTO = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        Long workOrderCount = questionParentInfoDao.selectCount(enterpriseId, queryUserId, questionSettingsDTO.getQuestionExpireHandle(), questionSettingsDTO.getQuestionExpireApprove(), status, true);
        commissionTotalDTO.setWorkOrderTotal(workOrderCount);
        Long patrolPlanCount = tbPatrolPlanDao.getPatrolPlanCount(enterpriseId, queryUserId);
        commissionTotalDTO.setPatrolPlanTotal(patrolPlanCount);
        return commissionTotalDTO;
    }

    @Override
    public PageInfo<PatrolPlanVO> getPatrolPlanList(String enterpriseId, CurrentUser user,Integer completeFlag,Integer pageSize,Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        List<PatrolPlanVO> patrolPlanList = agencyMapper.getPatrolPlanList(enterpriseId, user.getUserId(),
                Arrays.asList(WorkHandoverEnum.PATROL_STORE_PLAN.getCode()),completeFlag);
        //数据处理
        this.handlerPlanCount(enterpriseId,patrolPlanList);
        return new PageInfo<>(patrolPlanList);
    }

    private void handlerPlanCount(String enterpriseId , List<PatrolPlanVO> result){
        if (CollectionUtils.isEmpty(result)){
            return;
        }
        List<Long> subTaskIds = result.stream().filter(x ->
                x.getSubTaskId() != null).map(PatrolPlanVO::getSubTaskId).collect(Collectors.toList());
        Map<Long, PatrolPlanVO> patrolPlanVOMap = result.stream().filter(x ->
                x.getSubTaskId() != null&& WorkHandoverEnum.PATROL_STORE_PLAN.getCode().equals(x.getTaskType())).collect(Collectors.toMap(PatrolPlanVO::getSubTaskId, data -> data));
        List<UnifyTaskPersonDO> unifyTaskPersonDOS = unifyTaskPersonDao.listBySubTaskIdList(enterpriseId, subTaskIds);
        unifyTaskPersonDOS.stream().forEach(x->{
            TaskPersonTaskInfoDTO.ExecuteDemand executeDemand = JSONObject.parseObject(x.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam();
            Boolean isDistinct = executeDemand.getIsDistinct();
            Integer totalPatrolStoreNum = executeDemand.getPatrolStoreNum();
            Integer patrolStoreNum = 0;
            if(StringUtils.isNotBlank(x.getStoreIds())){
                List<String> patroledStoreList = Lists.newArrayList(StringUtils.split(x.getStoreIds(), Constants.COMMA));
                if(isDistinct){
                    patroledStoreList = patroledStoreList.stream().distinct().collect(Collectors.toList());
                }
                patrolStoreNum = patroledStoreList.size();
            }
            PatrolPlanVO patrolPlanVO = patrolPlanVOMap.get(x.getSubTaskId());
            if (!Objects.isNull(patrolPlanVO)){
                patrolPlanVO.setTotalPatrolStoreNum(totalPatrolStoreNum);
                patrolPlanVO.setPatrolStoreNum(patrolStoreNum);
                patrolPlanVO.setTaskName(x.getTaskName());
            }
        });
    }

}
