package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableDataColumnMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskStoreDao;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayTaskVO;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentCcUserDao;
import com.coolcollege.intelligent.model.common.IdListDTO;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.metatable.MetaTableConstant;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TaskStoreMetaTableColDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaTableDTO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaDataVO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO;
import com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolRecordAuthDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayDeleteParam;
import com.coolcollege.intelligent.model.unifytask.*;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.DisplayTaskQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreQuery;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskClearRequest;
import com.coolcollege.intelligent.model.unifytask.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolcollege.intelligent.service.elasticsearch.ElasticSearchService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.utils.MDCUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.TB_DISPLAY_TASK;

/**
 * @author byd
 * @date 2021-02-22 10:34
 */
@Service
@Slf4j
public class UnifyTaskStoreServiceImpl implements UnifyTaskStoreService {

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;

    @Autowired
    private PatrolStoreService patrolStoreService;

    @Resource
    private TbDisplayTableRecordMapper tbDisplayTableRecordMapper;

    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;

    @Resource
    private OrderMessageService orderMessageService;

    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;

    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;

    @Resource
    private TbDisplayTableDataColumnMapper tbDisplayTableDataColumnMapper;

    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private TaskStoreDao taskStoreDao;

    @Resource
    private QuestionParentInfoDao questionParentInfoDao;

    @Lazy
    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private UnifyTaskParentCcUserDao unifyTaskParentCcUserDao;

    @Resource
    TbDisplayTableRecordService tbDisplayTableRecordService;

    @Override
    public void updateTaskStoreDOBySubTask(String enterpriseId, TaskSubDO taskSubDO) {
        log.info("updateTaskStoreDOBySubTask {}", JSONUtil.toJsonStr(taskSubDO));
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        if (taskStoreDO == null) {
            log.info("taskStore 不存在enterpriseId:{}, unifyTaskId:{}, storeId:{}, loopCount:{}", enterpriseId, taskSubDO.getUnifyTaskId(),
                    taskSubDO.getStoreId(), taskSubDO.getLoopCount());
            return;
        }
        if(UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())){
            log.info("taskStore任务结束 不存在enterpriseId:{}, unifyTaskId:{}, storeId:{}, loopCount:{}, nodeNo:{}", enterpriseId, taskSubDO.getUnifyTaskId(),
                    taskSubDO.getStoreId(), taskSubDO.getLoopCount(), taskSubDO.getNodeNo());
            return;
        }
        //只更改本次循环的任务
        taskStoreDO.setActionKey(taskSubDO.getActionKey());
        taskStoreDO.setRemark(taskSubDO.getRemark());
        taskStoreDO.setNodeNo(taskSubDO.getNodeNo());
        if (taskSubDO.getHandleTime() != null) {
            taskStoreDO.setHandleTime(DateUtil.convertTimestampToDate(taskSubDO.getHandleTime()));
        }
        taskStoreDO.setFlowState(taskSubDO.getFlowState());
        taskStoreDO.setSubStatus(taskSubDO.getSubStatus());
        taskStoreDO.setBizCode(taskSubDO.getBizCode());
        taskStoreDO.setTaskData(taskSubDO.getTaskData());
        taskStoreMapper.updateByPrimaryKey(enterpriseId, taskStoreDO);
    }

    @Override
    public TaskStoreDayVO selectStoreClearList(String enterpriseId, TaskStoreQuery query) {
        PageHelper.clearPage();
        //时间不为空
        LocalDateTime localDateTime = DateUtils.convertStringToDate(query.getSelectTime());
        //查询日期
        LocalDate localDate = localDateTime.toLocalDate();

        //查询是否企业配置逾期代办是否显示
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        query.setOverdueTask(storeCheckSettingDO.getOverdueTaskContinue());
        query.setHandlerOvertimeTaskContinue(storeCheckSettingDO.getHandlerOvertimeTaskContinue());
        query.setApproverOvertimeTaskContinue(storeCheckSettingDO.getApproveOvertimeTaskContinue());
        //是否当天
        boolean isCurrDay = localDate.isEqual(LocalDate.now());
        query.setCurrDay(isCurrDay);

        //周第一天
        LocalDate weekFirstDay = DateUtils.getStartDayOfWeek(localDateTime.toLocalDate()).toLocalDate();
        //周最后一天
        LocalDate weekLastDay = DateUtils.getEndDayOfWeek(localDateTime.toLocalDate()).toLocalDate();

        //月第一天
        LocalDate monthFirstDay = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
        //月最后一天
        LocalDate monthLastDay = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();



        //是否允许过期可执行
        //封装返回结果
        TaskStoreDayVO taskStoreDayVO = new TaskStoreDayVO();
        List<TaskStoreDO> resultOverdue = new ArrayList<>();
        //查询逾期任务可执行
        if (query.getOverdueTask() || storeCheckSettingDO.getApproveOvertimeTaskContinue() || storeCheckSettingDO.getHandlerOvertimeTaskContinue()) {

            //查询日
            query.setSelectTime(DateUtils.convertDateToString(localDate, false));
            query.setRunRule(TaskRunRuleEnum.ONCE.getCode());
            resultOverdue.addAll(taskStoreMapper.selectOverdueStoreClearList(enterpriseId, query));
            query.setSelectTime(DateUtils.convertDateToString(localDate, false));
            query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
            query.setTaskCycle(TaskCycleEnum.DAY.getCode());
            resultOverdue.addAll(taskStoreMapper.selectOverdueStoreClearList(enterpriseId, query));
            //查询周
            query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
            query.setTaskCycle(TaskCycleEnum.WEEK.getCode());
            query.setSelectTime(DateUtils.convertDateToString(weekLastDay, false));
            resultOverdue.addAll(taskStoreMapper.selectOverdueStoreClearList(enterpriseId, query));
            //查询月
            query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
            query.setTaskCycle(TaskCycleEnum.MONTH.getCode());
            query.setSelectTime(DateUtils.convertDateToString(monthLastDay, false));
            resultOverdue.addAll(taskStoreMapper.selectOverdueStoreClearList(enterpriseId, query));
        }

        //查询日清当前日 有效期
        query.setSelectTime(DateUtils.convertDateToString(localDate, false));
        query.setRunRule(TaskRunRuleEnum.ONCE.getCode());
        query.setTaskCycle(null);
        query.setSelectBeginTime(DateUtils.convertDateToString(localDate, true));
        List<TaskStoreDO> result = new ArrayList<>(taskStoreMapper.selectStoreClearList(enterpriseId, query));
        //查询日清当前日 有效期
        query.setSelectTime(DateUtils.convertDateToString(localDate, false));
        query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
        query.setTaskCycle(TaskCycleEnum.DAY.getCode());
        query.setSelectBeginTime(DateUtils.convertDateToString(localDate, true));
        result.addAll(taskStoreMapper.selectStoreClearList(enterpriseId, query));
        //查询日清当前周 有效期
        query.setSelectTime(DateUtils.convertDateToString(weekLastDay, false));
        query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
        query.setTaskCycle(TaskCycleEnum.WEEK.getCode());
        query.setSelectBeginTime(DateUtils.convertDateToString(weekFirstDay, true));
        //加入总集合
        result.addAll(taskStoreMapper.selectStoreClearList(enterpriseId, query));
        //查询日清当前月
        query.setSelectTime(DateUtils.convertDateToString(monthLastDay, false));
        query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
        query.setTaskCycle(TaskCycleEnum.MONTH.getCode());
        query.setSelectBeginTime(DateUtils.convertDateToString(monthFirstDay, true));
        //加入总集合
        result.addAll(taskStoreMapper.selectStoreClearList(enterpriseId, query));

        Map<String, Future<TbDisplayTableRecordDO>> tmpMap = new HashMap<>();

        result.forEach(e -> {
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(e.getTaskType())){
                tmpMap.put(e.getUnifyTaskId() + "#" + e.getLoopCount() + "#" + e.getStoreId(), EXECUTOR_SERVICE.submit(() -> getDisplayTableRecord(enterpriseId, e, query.getDbName())));
            }
        });

        Set<Long> unifyTaskIds =
                result.stream().map(TaskStoreDO::getUnifyTaskId).collect(Collectors.toSet());
        Map<String, Boolean> subTaskMap = new HashMap<>(resultOverdue.size());



        Set<Long> unifyOverdueTaskIds = new HashSet<>();
        //加入逾期任务
        resultOverdue.forEach(e -> {
            unifyOverdueTaskIds.add(e.getUnifyTaskId());
            subTaskMap.put(e.getUnifyTaskId() + "#" + e.getLoopCount() + "#" + e.getStoreId(), false);
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(e.getTaskType())){
                tmpMap.put(e.getUnifyTaskId() + "#" + e.getLoopCount() + "#" + e.getStoreId(), EXECUTOR_SERVICE.submit(() -> getDisplayTableRecord(enterpriseId, e, query.getDbName())));
            }
        });
        if (CollectionUtils.isNotEmpty(unifyOverdueTaskIds)) {
            unifyTaskIds.addAll(unifyOverdueTaskIds);
        }

        //查询不到任务直接返回
        if (CollectionUtils.isEmpty(unifyTaskIds)) {
            return taskStoreDayVO;
        }
        List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        //回显任务名称
        Map<Long, String> idTaskNameMap = parentDOList.stream()
                .filter(a -> a.getId() != null && a.getTaskName() != null)
                .collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskName, (a, b) -> a));

        //回显任务信息
        Map<Long, String> idQuestionTaskInfo = parentDOList.stream()
                .filter(a -> a.getId() != null && a.getTaskInfo() != null)
                .collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskInfo, (a, b) -> a));

        List<String> userIdList = parentDOList.stream().map(TaskParentDO::getCreateUserId).collect(Collectors.toList());


        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, String> userIdNameMap = CollectionUtils.emptyIfNull(userDOList).stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName, (a, b) -> a));

        //回显人员
        Map<String, List<PersonDTO>> personMap = getTaskPerson(enterpriseId, result);

        Iterator<TaskStoreDO> iterator = result.iterator();

        while (iterator.hasNext()) {
            TaskStoreDO storeDO = iterator.next();
            if(subTaskMap.containsKey(storeDO.getUnifyTaskId() + "#" + storeDO.getLoopCount() + "#" + storeDO.getStoreId())){
                iterator.remove();
                continue;
            }

            if(UnifyStatus.COMPLETE.getCode().equals(storeDO.getSubStatus())){
                //判断完成时间是否有效 日
                if(TaskRunRuleEnum.ONCE.getCode().equals(storeDO.getRunRule()) ||
                        TaskRunRuleEnum.LOOP.getCode().equals(storeDO.getRunRule()) && TaskCycleEnum.DAY.getCode().equals(storeDO.getTaskCycle())){
                    LocalDate handleTime = storeDO.getHandleTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if(!localDate.isEqual(handleTime)){
                        iterator.remove();
                        continue;
                    }
                }
                //判断完成时间是否有效 周
                if(TaskRunRuleEnum.LOOP.getCode().equals(storeDO.getRunRule()) && TaskCycleEnum.WEEK.getCode().equals(storeDO.getTaskCycle())){
                    LocalDate handleTime = storeDO.getHandleTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if(handleTime.isBefore(weekFirstDay) || handleTime.isAfter(weekLastDay)){
                        iterator.remove();
                        continue;
                    }
                }

                //判断完成时间是否有效 月
                if(TaskRunRuleEnum.LOOP.getCode().equals(storeDO.getRunRule()) && TaskCycleEnum.MONTH.getCode().equals(storeDO.getTaskCycle())){
                    LocalDate handleTime = storeDO.getHandleTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if(handleTime.isBefore(monthFirstDay) || handleTime.isAfter(monthLastDay)){
                        iterator.remove();
                        continue;
                    }
                }
            }

            if(UnifyStatus.COMPLETE.getCode().equals(storeDO.getSubStatus()) && storeDO.getHandleTime().after(storeDO.getSubEndTime())){
                resultOverdue.add(storeDO);
                iterator.remove();
                continue;
            }
            if(UnifyStatus.ONGOING.getCode().equals(storeDO.getSubStatus()) && storeDO.getSubEndTime().before(new Date())){
                resultOverdue.add(storeDO);
                iterator.remove();
                continue;
            }
            storeDO.setTaskName(idTaskNameMap.get(storeDO.getUnifyTaskId()));
            storeDO.setTaskInfo(idQuestionTaskInfo.get(storeDO.getUnifyTaskId()));
            storeDO.setCreateUserName(userIdNameMap.get(storeDO.getCreateUserId()));
            if(Constants.SYSTEM_USER_ID.equals(storeDO.getCreateUserId()) || Constants.AI_USER_ID.equals(storeDO.getCreateUserId())){
                storeDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
            }
            if(UnifyNodeEnum.END_NODE.getCode().equals(storeDO.getNodeNo())){
                storeDO.setProcessUserList(new ArrayList<>());
            }else {
                storeDO.setProcessUserList(personMap.get(storeDO.getUnifyTaskId() + "#" + storeDO.getStoreId() +"#" + storeDO.getNodeNo()+ "#" + storeDO.getLoopCount()));
            }
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(storeDO.getTaskType())){
                Future<TbDisplayTableRecordDO> future = tmpMap.get(storeDO.getUnifyTaskId() + "#" + storeDO.getLoopCount() + "#" + storeDO.getStoreId());
                try{
                    TbDisplayTableRecordDO tbDisplayTableRecordDO = future.get();
                    if(tbDisplayTableRecordDO != null){
                        storeDO.setFirstHandlerTime(tbDisplayTableRecordDO.getFirstHandlerTime());
                    }
                }catch (Exception e){
                    log.error("获取 future TbDisplayTableRecordDO 异常", e);
                }
            }
        }
        //逾期任务人员回显
        resultOverdue.forEach(e -> {
            e.setTaskName(idTaskNameMap.get(e.getUnifyTaskId()));
            e.setTaskInfo(idQuestionTaskInfo.get(e.getUnifyTaskId()));
            e.setCreateUserName(userIdNameMap.get(e.getCreateUserId()));
            if(Constants.SYSTEM_USER_ID.equals(e.getCreateUserId()) || Constants.AI_USER_ID.equals(e.getCreateUserId())){
                e.setCreateUserName(Constants.SYSTEM_USER_NAME);
            }
            e.setProcessUserList(personMap.get(e.getUnifyTaskId() + "#" + e.getStoreId() +"#" + e.getNodeNo()+ "#" + e.getLoopCount()));
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(e.getTaskType())){
                Future<TbDisplayTableRecordDO> future = tmpMap.get(e.getUnifyTaskId() + "#" + e.getLoopCount() + "#" + e.getStoreId());
                try{
                    TbDisplayTableRecordDO tbDisplayTableRecordDO = future.get();
                    if(tbDisplayTableRecordDO != null){
                        e.setFirstHandlerTime(tbDisplayTableRecordDO.getFirstHandlerTime());
                    }
                }catch (Exception exception){
                    log.error("获取 future TbDisplayTableRecordDO 异常", exception);
                }
            }
        });

        //逾期可执行分组
        Map<String, List<TaskStoreDO>> resultOverdueMap = resultOverdue.stream().collect(Collectors.groupingBy(e -> e.getRunRule() + "#" +
                (e.getTaskCycle() == null ? "" : e.getTaskCycle())));
        //正常有效期可执行
        Map<String, List<TaskStoreDO>> resultMap = result.stream().collect(Collectors.groupingBy(e -> e.getRunRule() + "#" +
                (e.getTaskCycle() == null ? "" : e.getTaskCycle())));

        List<TaskStoreDO> dayResult = new ArrayList<>();
        List<TaskStoreDO> dayResultOnce = resultMap.get(TaskRunRuleEnum.ONCE.getCode() + "#");
        if(CollectionUtils.isNotEmpty(dayResultOnce)){
            dayResult.addAll(dayResultOnce);
        }
        List<TaskStoreDO> dayResultLoop = resultMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.DAY.getCode());
        if(CollectionUtils.isNotEmpty(dayResultLoop)){
            dayResult.addAll(dayResultLoop);
        }

        List<TaskStoreDO> dayOverdueResult = new ArrayList<>();
        List<TaskStoreDO> dayOverdueResultOnce = resultOverdueMap.get(TaskRunRuleEnum.ONCE.getCode() + "#");
        if(CollectionUtils.isNotEmpty(dayOverdueResultOnce)){
            dayOverdueResult.addAll(dayOverdueResultOnce);
        }
        List<TaskStoreDO> dayOverdueResultLoop = resultOverdueMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.DAY.getCode());
        if(CollectionUtils.isNotEmpty(dayOverdueResultLoop)){
            dayOverdueResult.addAll(dayOverdueResultLoop);
        }

        taskStoreDayVO.setDayList(dayResult);
        taskStoreDayVO.setDayOverdueList(dayOverdueResult);
        taskStoreDayVO.setWeekList(resultMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.WEEK.getCode()));
        taskStoreDayVO.setWeekOverdueList(resultOverdueMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.WEEK.getCode()));
        taskStoreDayVO.setMonthList(resultMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.MONTH.getCode()));
        taskStoreDayVO.setMonthOverdueList(resultOverdueMap.get(TaskRunRuleEnum.LOOP.getCode() + "#" + TaskCycleEnum.MONTH.getCode()));
        return taskStoreDayVO;
    }

    @Override
    public List<TaskStoreClearVO> getStoreClearListNew(String enterpriseId, String dbName, StoreTaskClearRequest request) {
        Pair<String, String> startAndEndTime = TimeCycleEnum.getStartAndEndTime(request.getTimeCycle(), request.getTimeUnion());
        //查询是否企业配置逾期代办是否显示
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        TaskStoreQuery query = new TaskStoreQuery();
        query.setStoreId(request.getStoreId());
        query.setSelectTime(startAndEndTime.getValue());
        query.setSelectBeginTime(startAndEndTime.getKey());
        query.setOverdueTask(storeCheckSettingDO.getOverdueTaskContinue());
        query.setHandlerOvertimeTaskContinue(storeCheckSettingDO.getHandlerOvertimeTaskContinue());
        query.setApproverOvertimeTaskContinue(storeCheckSettingDO.getApproveOvertimeTaskContinue());
        query.setCurrDay(TimeCycleEnum.isCurrentDay(request.getTimeCycle(), request.getTimeUnion()));
        query.setTaskCycle(request.getTimeCycle().name());
        query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
        query.setDbName(dbName);
        Integer currentDay = TimeCycleEnum.getCurrentDay();
        List<TaskStoreDO> taskList = new ArrayList<>(taskStoreMapper.selectStoreClearList(enterpriseId, query));
        //日清 才查询单次任务
        if(TimeCycleEnum.DAY.equals(request.getTimeCycle())){
            query.setTaskCycle(null);
            query.setRunRule(TaskRunRuleEnum.ONCE.getCode());
            taskList.addAll(taskStoreMapper.selectStoreClearList(enterpriseId, query));
        }
        Map<String, Future<TbDisplayTableRecordDO>> tmpMap = new HashMap<>();
        taskList.forEach(e -> {
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(e.getTaskType())){
                tmpMap.put(e.getUnifyTaskId() + "#" + e.getLoopCount() + "#" + e.getStoreId(), EXECUTOR_SERVICE.submit(() -> getDisplayTableRecord(enterpriseId, e, query.getDbName())));
            }
        });
        Set<Long> unifyTaskIds = taskList.stream().map(TaskStoreDO::getUnifyTaskId).collect(Collectors.toSet());
        List<TaskStoreClearVO> resultList = new ArrayList<>();
        List<TaskParentDO> parentDOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(unifyTaskIds)){
            parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        }
        //回显任务名称
        Map<Long, String> idTaskNameMap = parentDOList.stream().filter(a -> a.getId() != null && a.getTaskName() != null).collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskName, (a, b) -> a));
        //回显任务信息
        Map<Long, String> idQuestionTaskInfo = parentDOList.stream().filter(a -> a.getId() != null && a.getTaskInfo() != null).collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskInfo, (a, b) -> a));
        taskList = taskList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<StoreTaskClearVO> taskStoreClearVOList = StoreTaskClearVO.convertVOList(taskList);
        int totalTask = taskStoreClearVOList.size();
        int finishNum = 0;
        Iterator<StoreTaskClearVO> iterator = taskStoreClearVOList.iterator();
        while (iterator.hasNext()) {
            StoreTaskClearVO storeDO = iterator.next();
            if(UnifyStatus.ONGOING.getCode().equals(storeDO.getSubStatus()) && storeDO.getSubEndTime().before(new Date())){
                //逾期
                storeDO.setIsOverDue(Boolean.TRUE);
            }
            if(UnifyStatus.COMPLETE.getCode().equals(storeDO.getSubStatus()) && UnifyNodeEnum.END_NODE.getCode().equals(storeDO.getNodeNo())){
                //完成
                finishNum ++;
                storeDO.setIsFinish(Boolean.TRUE);
            }
            storeDO.setTaskName(idTaskNameMap.get(storeDO.getUnifyTaskId()));
            storeDO.setTaskInfo(idQuestionTaskInfo.get(storeDO.getUnifyTaskId()));
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(storeDO.getTaskType())){
                Future<TbDisplayTableRecordDO> future = tmpMap.get(storeDO.getUnifyTaskId() + "#" + storeDO.getLoopCount() + "#" + storeDO.getStoreId());
                try{
                    future.get();
                }catch (Exception e){
                    log.error("获取 future TbDisplayTableRecordDO 异常", e);
                }
            }
        }
        if(Objects.nonNull(request.getIsFinish())){
            taskStoreClearVOList = taskStoreClearVOList.stream().filter(o->o.getIsFinish().equals(request.getIsFinish())).collect(Collectors.toList());
        }
        if(Objects.nonNull(request.getIsOverDue())){
            taskStoreClearVOList = taskStoreClearVOList.stream().filter(o->o.getIsOverDue().equals(request.getIsOverDue())).collect(Collectors.toList());
        }
        Boolean isFinish = CollectionUtils.isEmpty(taskList) || currentDay < request.getTimeUnion() ? null : totalTask ==  finishNum && finishNum != Constants.ZERO;
        resultList.add(new TaskStoreClearVO(request.getTimeUnion(), isFinish, request.getTimeCycle(), taskList.size(), finishNum, taskStoreClearVOList));
        List<Integer> timeCycleList = TimeCycleEnum.getTimeCycleList(request.getTimeCycle(), request.getTimeUnion());
        if(CollectionUtils.isNotEmpty(timeCycleList)){
            Map<Integer, TaskStoreClearVO> storeClearVOMap = resultList.stream().collect(Collectors.toMap(k -> k.getTimeUnion(), Function.identity(), (k1, k2) -> k1));
            query.setSelectBeginTime(TimeCycleEnum.getDayMinTime(timeCycleList.get(Constants.INDEX_ZERO)));
            query.setSelectTime(TimeCycleEnum.getDayMaxTime(timeCycleList.get(timeCycleList.size() - Constants.INDEX_ONE), request.getTimeCycle()));
            List<TaskNumDTO> taskNumList = new ArrayList<>();
            if(TimeCycleEnum.DAY.equals(request.getTimeCycle())){
                query.setTaskCycle(null);
                query.setRunRule(TaskRunRuleEnum.ONCE.getCode());
                //先查单次任务逾期的
                List<TaskNumDTO> onceTaskNumList = taskStoreMapper.selectTaskStatusNum(enterpriseId, query);
                if(CollectionUtils.isNotEmpty(onceTaskNumList)){
                    taskNumList.addAll(onceTaskNumList);
                }
            }
            query.setTaskCycle(request.getTimeCycle().name());
            query.setRunRule(TaskRunRuleEnum.LOOP.getCode());
            List<TaskNumDTO> dayTaskNumList = taskStoreMapper.selectTaskStatusNum(enterpriseId, query);
            if(CollectionUtils.isNotEmpty(dayTaskNumList)){
                taskNumList.addAll(dayTaskNumList);
            }
            for (Integer timeUnion : timeCycleList) {
                TaskStoreClearVO taskStoreClearVO = storeClearVOMap.get(timeUnion);
                Integer maxTimeCycle = TimeCycleEnum.getMaxTimeCycle(request.getTimeCycle(), timeUnion);
                if(Objects.isNull(taskStoreClearVO)){
                    Boolean noComplete = null;
                    if(timeUnion <= currentDay){
                        noComplete = TaskNumDTO.isNoComplete(taskNumList, timeUnion, maxTimeCycle);
                    }
                    resultList.add(new TaskStoreClearVO(timeUnion, noComplete, request.getTimeCycle()));
                }
            }
        }
        return resultList;
    }

    @Override
    public TaskSubVO jumpDetail(String enterpriseId, Long taskStoreId, CurrentUser currentUser) {
        String userId = currentUser.getUserId();
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        TaskSubVO taskSub;
        if (taskStoreDO == null) {
            return null;
        }
        // 查询最新node节点
        //查询当前人记录
        taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                userId, UnifyStatus.ONGOING.getCode(), null);
        if(taskSub != null){
            taskSub.setEditFlag(Boolean.TRUE);
        }

        if (taskSub == null) {
            //查不到当前人记录查询进行中最新的一条,
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                    null, UnifyStatus.ONGOING.getCode(), null);
        }

        if (taskSub == null) {
            //查不到进行中的记录，则任务已完成查询完成的记录
            taskSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                    null, UnifyStatus.COMPLETE.getCode(), UnifyNodeEnum.END_NODE.getCode());
        }
        //查询是否企业配置逾期代办是否显示
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        if(taskSub != null){
            taskSub.setEditFlag(Boolean.TRUE);
            //用户操作权限判断
            if(taskSub.getSubBeginTime() > System.currentTimeMillis()) {
                //未开始不可编辑
                taskSub.setEditFlag(Boolean.FALSE);
            }
            if(taskSub.getEditFlag() == null){
                taskSub.setEditFlag(Boolean.FALSE);
            }
            //逾期不可执行
            if(!storeCheckSettingDO.getOverdueTaskContinue() && taskSub.getSubEndTime() < System.currentTimeMillis()){
                taskSub.setEditFlag(Boolean.FALSE);
            }
            if(!taskSub.getHandleUserId().equals(userId)){
                taskSub.setEditFlag(Boolean.FALSE);
            }
            //状态节点不一致重新更新数据
            if(!taskStoreDO.getSubStatus().equals(taskSub.getSubStatus()) || !taskStoreDO.getNodeNo().equals(taskSub.getFlowNodeNo())){
                TaskSubDO taskSubVO = new TaskSubDO();
                BeanUtils.copyProperties(taskSub, taskSubVO);
                taskSubVO.setActionKey(taskSub.getFlowActionKey());
                taskSubVO.setNodeNo(taskSub.getFlowNodeNo());
                updateTaskStoreDOBySubTask(enterpriseId, taskSubVO);
            }
            PatrolRecordAuthDTO patrolRecordAuth = patrolStoreService.getRecordAuth(enterpriseId, currentUser, null, taskSub.getSubTaskId());
            taskSub.setPatrolRecordAuth(patrolRecordAuth);
        }
        return taskSub;
    }

    @Override
    public PageInfo taskStoreList(String enterpriseId, TaskStoreLoopQuery query) {
        if(query.getLoopCount() == null){
            query.setLoopCount(1L);
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<TaskStoreLoopVO> resultList = new ArrayList<>();
        List<TaskStoreDO> taskStoreList = new ArrayList<>();
        TaskParentDO taskParentDO = new TaskParentDO();
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            taskStoreList = taskStoreMapper.taskStoreList(enterpriseId, query);
            taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId,query.getUnifyTaskId());
        }else {
            taskStoreList = taskStoreMapper.taskStoreListExistUnifyTaskIds(enterpriseId, query);
            List<Long> longUnifyTaskIds = query.getUnifyTaskIds().stream().map(Long::valueOf).collect(Collectors.toList());
            List<TaskParentDO> taskParentDOS = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, longUnifyTaskIds);
            taskParentDO = taskParentDOS.get(0);
        }
        if (CollectionUtils.isEmpty(taskStoreList)) {
            return new PageInfo();
        }
        String taskName = null;
        String createUserName = null;
        if(taskParentDO != null){
            taskName = taskParentDO.getTaskName();
            createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, taskParentDO.getCreateUserId());
        }

        List<String> storeIdList = taskStoreList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());

        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);

        Map<String, StoreDO> storeMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));


        List<String> storeSucIdList = taskStoreList.stream().filter(e -> UnifyStatus.COMPLETE.getCode().equals(e.getSubStatus()))
                .map(TaskStoreDO::getStoreId).collect(Collectors.toList());

        Map<String, PersonNodeNoDTO> supervisorMap = new HashMap<>();

        List<PersonNodeNoDTO> supervisorList = tbPatrolStoreRecordMapper.selectUserByTaskLoopCount(enterpriseId, query.getUnifyTaskId(), storeSucIdList, query.getLoopCount());
        List<Long> businessIdList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(supervisorList)){
            supervisorMap = supervisorList.stream().collect(Collectors.toMap(e -> e.getUnifyTaskId() + "#" + e.getStoreId(), data -> data,(a, b)->a));
            businessIdList = supervisorList.stream().map(PersonNodeNoDTO::getBusinessId).collect(Collectors.toList());
        }
        Map<Long, PersonNodeNoDTO> handlerMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(businessIdList)){
            List<PersonNodeNoDTO> handlerMapList = tbDataTableMapper.getUserListByBusinessIdList(enterpriseId, businessIdList, MetaTableConstant.BusinessTypeConstant.PATROL_STORE);
            if(CollectionUtils.isNotEmpty(handlerMapList)){
                handlerMap = handlerMapList.stream().collect(Collectors.toMap(PersonNodeNoDTO::getBusinessId, data -> data,(a, b)->a));
            }
        }
        Map<String, List<PersonDTO>> map = getTaskPerson(enterpriseId, taskStoreList);
        Map<String, List<PersonNodeNoDTO>> personNodeNoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds())){
            personNodeNoMap = getUserListByUnifyTaskList(enterpriseId, query.getUnifyTaskIds(), storeIdList, query.getLoopCount(), null);
        }else {
            personNodeNoMap = getUserList(enterpriseId, query.getUnifyTaskId(), storeIdList, query.getLoopCount(), null);
        }
        //查询处理人、审核人列表

        PageInfo pageInfo = new PageInfo(taskStoreList);
        for (TaskStoreDO taskStoreDO : taskStoreList) {
            String nodeNo = taskStoreDO.getNodeNo();
            TaskStoreLoopVO vo = new TaskStoreLoopVO();
            BeanUtils.copyProperties(taskStoreDO, vo);
            vo.setTaskName(taskName);
            vo.setCreateUserName(createUserName);
            if(UnifyStatus.COMPLETE.getCode().equals(taskStoreDO.getSubStatus())){
                vo.setExpireFlag(taskStoreDO.getHandleTime().after(taskStoreDO.getSubEndTime()));
                nodeNo = UnifyNodeEnum.END_NODE.getCode();
                PersonNodeNoDTO personNodeNoDTO = supervisorMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId());
                vo.setSupervisor(personNodeNoDTO);
                if(personNodeNoDTO != null){
                    vo.setHandler(handlerMap.get(personNodeNoDTO.getBusinessId()));
                }
            }else {
                vo.setExpireFlag(taskStoreDO.getSubEndTime().before(new Date()));
            }
            vo.setProcessUserList(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + nodeNo));

            if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && UnifyStatus.ONGOING.getCode().equals(taskStoreDO.getSubStatus())){
                vo.setHanderUser(changPersonDTO(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + taskStoreDO.getNodeNo())));
            }else {
                vo.setHanderUser(map.get(taskStoreDO.getUnifyTaskId()+"#" + taskStoreDO.getStoreId() + "#"  + UnifyNodeEnum.FIRST_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
            }
            vo.setApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + UnifyNodeEnum.SECOND_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
            vo.setRecheckUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + UnifyNodeEnum.THIRD_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
            vo.setCreateTime(taskStoreDO.getCreateTime());

            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + taskStoreDO.getUnifyTaskId() + "_" + taskStoreDO.getStoreId() + "_" + taskStoreDO.getLoopCount();
            vo.setAiProcessing(false);
            if (StringUtils.isNotBlank(redisUtilPool.getString(taskStatusKey))) {
                vo.setAiProcessing(true);
            }

            StoreDO storeDO = storeMap.get(taskStoreDO.getStoreId());
            if(storeDO !=null){
                vo.setStoreOpenTime(storeDO.getOpenDate());
            }
            resultList.add(vo);
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public TaskStoreStageCount taskStoreListCount(String enterpriseId, TaskStoreLoopQuery query) {
        TaskStoreStageCount taskStoreStageCount = new TaskStoreStageCount();
        UnifyTaskStoreCount unifyTaskStoreCount = new UnifyTaskStoreCount();
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds())){
            unifyTaskStoreCount = taskStoreMapper.selectTaskStoreCountByUnifyTaskIds(enterpriseId, query.getUnifyTaskIds(), query.getLoopCount());
        }else {
            unifyTaskStoreCount = taskStoreMapper.selectTaskStoreCount(enterpriseId, query.getUnifyTaskId(), query.getLoopCount());
        }
        taskStoreStageCount.setTotalCount(unifyTaskStoreCount.getTotalCount());
        taskStoreStageCount.setCompleteCount(unifyTaskStoreCount.getCompleteCount());
        taskStoreStageCount.setOngoingCount(unifyTaskStoreCount.getOngoingCount());
        taskStoreStageCount.setApproveCount(unifyTaskStoreCount.getApproveCount());
        return taskStoreStageCount;
    }

    @Override
    public List<TaskStoreStageVO> taskStageList(String enterpriseId, Long unifyTaskId, String status) {
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, unifyTaskId);
        String taskName = null;
        if(taskParentDO != null){
            taskName = taskParentDO.getTaskName();
        }
        List<TaskStoreStageVO> result = new ArrayList<>();
        List<TaskStoreStageVO> list = taskStoreMapper.taskStageList(enterpriseId, unifyTaskId, status);

        if(CollectionUtils.isNotEmpty(list)){
            for(TaskStoreStageVO vo : list){
                vo.setTaskName(taskName);
                if(vo.getTotalCount().equals(vo.getCompleteCount())){
                    vo.setExpireFlag(vo.getHandleTime().after(vo.getSubEndTime()));
                    vo.setSubStatus(UnifyStatus.COMPLETE.getCode());
                }else {
                    vo.setExpireFlag(vo.getSubEndTime().before(new Date()));
                    vo.setSubStatus(UnifyStatus.ONGOING.getCode());
                }
                //查询成功的，过掉不成功对的
                if(UnifyStatus.COMPLETE.getCode().equals(status) && !UnifyStatus.COMPLETE.getCode().equals(vo.getSubStatus())){
                    continue;
                }
                //查询进行中的，过掉不在进行中的的
                if(UnifyStatus.ONGOING.getCode().equals(status) && !UnifyStatus.ONGOING.getCode().equals(vo.getSubStatus())){
                    continue;
                }
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public TaskStoreStageCount taskStageListCount(String enterpriseId, Long unifyTaskId) {
        TaskStoreStageCount taskStoreStageCount = new TaskStoreStageCount();
        Long total = taskStoreMapper.selectTaskStageCount(enterpriseId, unifyTaskId, null);
        Long ongoing = taskStoreMapper.selectTaskStageCount(enterpriseId, unifyTaskId, UnifyStatus.ONGOING.getCode());
        taskStoreStageCount.setTotalCount(total);
        taskStoreStageCount.setCompleteCount(total - ongoing);
        taskStoreStageCount.setOngoingCount(ongoing);
        return taskStoreStageCount;
    }

    @Override
    public void batchInsertTaskStore(String enterpriseId, List<TaskStoreDO> taskStoreList) {
        taskStoreMapper.batchInsertTaskStore(enterpriseId, taskStoreList);
    }

    @Override
    public int delTaskStoreByParentTaskId(String enterpriseId, Long parentTaskId) {
        return taskStoreMapper.delTaskStoreByParentTaskId(enterpriseId, parentTaskId);
    }

    @Override
    public SubTaskDTO displayStoreTaskList(String enterpriseId, TaskStoreLoopQuery query) {
        if (query.getLoopCount() == null) {
            query.setLoopCount(1L);
        }
        SubTaskDTO subTaskDTO = new SubTaskDTO();
        UnifySubStatisticsDTO statistics = elasticSearchService.getDisplayTaskCount(enterpriseId, query);
        subTaskDTO.setStatistics(statistics);
        List<DisplayTaskStoreLoopVO> resultList = new ArrayList<>();
        PageVO<TaskStoreDO> taskStorePage = new PageVO<>();
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds()) || query.getUnifyTaskId() != null){
            taskStorePage = elasticSearchService.getTaskStoreList(enterpriseId, query);
        }else {
            return subTaskDTO;
        }
        List<TaskStoreDO> taskStoreList = taskStorePage.getList();
        TaskParentDO taskParentDO = null;
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, query.getUnifyTaskId());
        }else {
            List<Long> LongTypeUnifyTaskIds = query.getUnifyTaskIds().stream().map(Long::valueOf).collect(Collectors.toList());
            List<TaskParentDO> taskParentDOS = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, LongTypeUnifyTaskIds);
            if (CollectionUtils.isNotEmpty(taskParentDOS)){
                taskParentDO = taskParentDOS.get(0);
            }
        }
        if (CollectionUtils.isEmpty(taskStoreList) && CollectionUtils.isNotEmpty(query.getUnifyTaskIds())){
            return subTaskDTO;
        }
        if (CollectionUtils.isEmpty(taskStoreList)) {
            subTaskDTO.setPageInfo(new PageInfo());
            subTaskDTO.setSubBeginTime(taskParentDO.getBeginTime());
            subTaskDTO.setSubEndTime(taskParentDO.getEndTime());
            JSONObject taskInfoJsonObj = JSON.parseObject(taskParentDO.getTaskInfo());
            JSONObject tbDisplayDefined = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
            String handleEndTime = null;
            if (tbDisplayDefined != null) {
                //巡店总结
                handleEndTime = tbDisplayDefined.getString("handleEndTime");
            }
            if(TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule()) && StringUtils.isNotBlank(handleEndTime)){
                subTaskDTO.setHanderEndTime(DateUtil.parse(handleEndTime + ":59", DatePattern.NORM_DATETIME_PATTERN).getTime());
            }
            return subTaskDTO;
        }

        List<String> storeIdList = taskStoreList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());
        List<Long> taskStoreIdList = taskStoreList.stream().map(TaskStoreDO::getId).collect(Collectors.toList());

        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.taskStoreListByIdList(enterpriseId, taskStoreIdList);
        if(CollectionUtils.isEmpty(taskStoreDOList)){
            return subTaskDTO;
        }
        Map<Long, String> taskStoreNodeNoMap = taskStoreDOList.stream().collect(Collectors.toMap(TaskStoreDO::getId, TaskStoreDO::getNodeNo, (a, b) -> a));

        List<TbDisplayTableRecordDO> tbDisplayTableRecordList = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            tbDisplayTableRecordList = tbDisplayTableRecordMapper.listByUnifyTaskIdAndloopCountAndStoreIds(
                    enterpriseId, query.getUnifyTaskId(), query.getLoopCount(), new ArrayList<>(storeIdList));
        }else {
            tbDisplayTableRecordList = tbDisplayTableRecordMapper.listByUnifyTaskIdsAndloopCountAndStoreIds(
                    enterpriseId, query.getUnifyTaskIds(), query.getLoopCount(), new ArrayList<>(storeIdList));
        }

        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);

        Map<String, StoreDO> storeMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));


        Map<String, TbDisplayTableRecordDO> tbDisplayTableRecordMap =
                tbDisplayTableRecordList.stream().collect(Collectors.toMap(e -> e.getUnifyTaskId() + Constants.MOSAICS + e.getStoreId() +
                        Constants.MOSAICS + e.getLoopCount(), Function.identity(), (a, b) -> a));

        Map<String, List<PersonDTO>> map = getTaskPerson(enterpriseId, taskStoreList);
        //查询待处理的节点
        Map<String, List<PersonNodeNoDTO>> personNodeNoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            personNodeNoMap = getUserList(enterpriseId, query.getUnifyTaskId(), storeIdList, query.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode());
        }else {
            personNodeNoMap = getUserListByUnifyTaskList(enterpriseId, query.getUnifyTaskIds(), storeIdList, query.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode());
        }
        String createUserId = Optional.ofNullable(taskParentDO).map(TaskParentDO::getCreateUserId).orElse(null);
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, createUserId);
        Date now = new Date();
        //查询处理人、审核人列表
        PageInfo<DisplayTaskStoreLoopVO> pageInfo = new PageInfo(taskStoreList);
        Set<String> handlerUserIds = CollStreamUtil.toSet(taskStoreList, TaskStoreDO::getHandlerUserId);
        List<EnterpriseUserDO> handlerUsers = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(handlerUserIds));
        Map<String, PersonDTO> handlerUserMap = CollStreamUtil.toMap(handlerUsers, EnterpriseUserDO::getUserId, v -> new PersonDTO(v.getUserId(), v.getName(), v.getAvatar()));
        for (TaskStoreDO taskStoreDO : taskStoreList) {
            DisplayTaskStoreLoopVO displayTaskStoreLoopVO = new DisplayTaskStoreLoopVO();
            displayTaskStoreLoopVO.setStoreId(taskStoreDO.getStoreId());
            displayTaskStoreLoopVO.setId(taskStoreDO.getId());
            displayTaskStoreLoopVO.setRegionId(taskStoreDO.getRegionId());
            displayTaskStoreLoopVO.setStoreName(taskStoreDO.getStoreName());
            displayTaskStoreLoopVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            displayTaskStoreLoopVO.setTaskType(taskStoreDO.getTaskType());
            displayTaskStoreLoopVO.setLoopCount(taskStoreDO.getLoopCount());
            displayTaskStoreLoopVO.setNodeNo(taskStoreDO.getNodeNo());
            String nodeNo = taskStoreNodeNoMap.get(taskStoreDO.getId());
            displayTaskStoreLoopVO.setNodeNo(nodeNo);
            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + taskStoreDO.getUnifyTaskId() + "_" + taskStoreDO.getStoreId() + "_" + taskStoreDO.getLoopCount();
            displayTaskStoreLoopVO.setProcessing(false);
            if (StringUtils.isNotBlank(redisUtilPool.getString(taskStatusKey)) ||
                    StringUtils.isNotBlank(query.getNodeNo()) && !nodeNo.equals(query.getNodeNo())) {
                displayTaskStoreLoopVO.setProcessing(true);
            }
            displayTaskStoreLoopVO.setSubBeginTime(taskStoreDO.getSubBeginTime());
            displayTaskStoreLoopVO.setSubEndTime(taskStoreDO.getSubEndTime());
            displayTaskStoreLoopVO.setTaskType(taskStoreDO.getTaskType());
            displayTaskStoreLoopVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            displayTaskStoreLoopVO.setHandleTime(taskStoreDO.getHandleTime());
            displayTaskStoreLoopVO.setHandlerEndTime(taskStoreDO.getHandlerEndTime());
            if (Objects.nonNull(taskParentDO)){
                displayTaskStoreLoopVO.setTaskName(taskParentDO.getTaskName());
            }
            displayTaskStoreLoopVO.setCreateUserName(createUserName);
            displayTaskStoreLoopVO.setCurrDate(now);
            displayTaskStoreLoopVO.setCreateTime(taskStoreDO.getCreateTime());
            String key = taskStoreDO.getUnifyTaskId() + Constants.MOSAICS + taskStoreDO.getStoreId() + Constants.MOSAICS + taskStoreDO.getLoopCount();
            TbDisplayTableRecordDO tbDisplayTableRecord = tbDisplayTableRecordMap.get(key);
            if (tbDisplayTableRecord != null) {
                displayTaskStoreLoopVO.setFirstHandlerTime(tbDisplayTableRecord.getFirstHandlerTime());
                displayTaskStoreLoopVO.setHandleUserName(StringUtils.isNotBlank(tbDisplayTableRecord.getRecheckUserName()) ?
                        tbDisplayTableRecord.getRecheckUserName() : tbDisplayTableRecord.getApproveUserName());
                displayTaskStoreLoopVO.setScore(tbDisplayTableRecord.getScore());
                displayTaskStoreLoopVO.setAiScore(tbDisplayTableRecord.getAiScore());
                displayTaskStoreLoopVO.setIsAiCheck(tbDisplayTableRecord.getIsAiCheck());
            }
            if(taskStoreDO.getHandlerEndTime() != null){
                subTaskDTO.setHanderEndTime(taskStoreDO.getHandlerEndTime().getTime());
            }
            if (taskStoreDO.getSubBeginTime() != null) {
                subTaskDTO.setSubBeginTime(taskStoreDO.getSubBeginTime().getTime());
            }
            if (taskStoreDO.getSubEndTime() != null) {
                subTaskDTO.setSubEndTime(taskStoreDO.getSubEndTime().getTime());
            }
            if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && UnifyStatus.ONGOING.getCode().equals(taskStoreDO.getSubStatus())){
                displayTaskStoreLoopVO.setHanderUser(changPersonDTO(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + taskStoreDO.getNodeNo())));
            }else {
                if (StringUtils.isNotBlank(taskStoreDO.getHandlerUserId())) {
                    // 有实际处理人的情况下驳回会给实际处理人发任务，因此展示实际处理人
                    displayTaskStoreLoopVO.setHanderUser(Collections.singletonList(handlerUserMap.get(taskStoreDO.getHandlerUserId())));
                } else {
                    displayTaskStoreLoopVO.setHanderUser(map.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + "1" + "#" + taskStoreDO.getLoopCount()));
                }
            }
            displayTaskStoreLoopVO.setApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "2" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setRecheckUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "3" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setThirdApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "4" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setFourApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "5" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setFiveApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "6" + "#" + taskStoreDO.getLoopCount()));

            //历史数据已完成
            if(StringUtils.isBlank(displayTaskStoreLoopVO.getHandleUserName()) && UnifyNodeEnum.END_NODE.getCode().equals(displayTaskStoreLoopVO.getNodeNo())){
                List<PersonDTO> userList = CollectionUtils.isEmpty(displayTaskStoreLoopVO.getRecheckUser()) ? displayTaskStoreLoopVO.getApproveUser() : displayTaskStoreLoopVO.getRecheckUser();
                if(CollectionUtils.isNotEmpty(userList)){
                    List<String> list = userList.stream().map(PersonDTO::getUserName).collect(Collectors.toList());
                    displayTaskStoreLoopVO.setHandleUserName(StringUtils.join(list, ","));
                }
            }

            StoreDO storeDO = storeMap.get(taskStoreDO.getStoreId());
            if(storeDO !=null){
                displayTaskStoreLoopVO.setStoreOpenTime(storeDO.getOpenDate());
            }
            resultList.add(displayTaskStoreLoopVO);
        }
        pageInfo.setList(resultList);
        pageInfo.setTotal(taskStorePage.getTotal());
        subTaskDTO.setPageInfo(pageInfo);
        if(subTaskDTO.getHanderEndTime() == null){
            subTaskDTO.setHanderEndTime(subTaskDTO.getSubBeginTime());
        }
        return subTaskDTO;
    }

    @Override
    public SubTaskDTO displayStoreTaskNewList(String enterpriseId, TaskStoreLoopQuery query) {
        if (query.getLoopCount() == null) {
            query.setLoopCount(1L);
        }
        SubTaskDTO subTaskDTO = new SubTaskDTO();
        UnifySubStatisticsDTO statistics = taskStoreMapper.getDisplayTaskDBCount(enterpriseId, query);
        subTaskDTO.setStatistics(statistics);
        List<DisplayTaskStoreLoopVO> resultList = new ArrayList<>();
        PageVO<TaskStoreDO> taskStorePage = new PageVO<>();
        if (CollectionUtils.isNotEmpty(query.getUnifyTaskIds()) || query.getUnifyTaskId() != null){
            PageHelper.startPage(query.getPageNumber(), query.getPageSize());
            List<TaskStoreDO> taskStorePageList = taskStoreMapper.selectDisplayTaskStoreDBList(enterpriseId, query);
            PageInfo pageInfo = new PageInfo(taskStorePageList);
            taskStorePage.setList(taskStorePageList);
            taskStorePage.setTotal(pageInfo.getTotal());
            taskStorePage.setPageNum(pageInfo.getPageNum());
            taskStorePage.setPageSize(pageInfo.getPageSize());
        }else {
            return subTaskDTO;
        }
        List<TaskStoreDO> taskStoreList = taskStorePage.getList();
        TaskParentDO taskParentDO = null;
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, query.getUnifyTaskId());
        }else {
            List<Long> LongTypeUnifyTaskIds = query.getUnifyTaskIds().stream().map(Long::valueOf).collect(Collectors.toList());
            List<TaskParentDO> taskParentDOS = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, LongTypeUnifyTaskIds);
            if (CollectionUtils.isNotEmpty(taskParentDOS)){
                taskParentDO = taskParentDOS.get(0);
            }
        }
        if (CollectionUtils.isEmpty(taskStoreList) && CollectionUtils.isNotEmpty(query.getUnifyTaskIds())){
            return subTaskDTO;
        }
        if (CollectionUtils.isEmpty(taskStoreList)) {
            subTaskDTO.setPageInfo(new PageInfo());
            subTaskDTO.setSubBeginTime(taskParentDO.getBeginTime());
            subTaskDTO.setSubEndTime(taskParentDO.getEndTime());
            JSONObject taskInfoJsonObj = JSON.parseObject(taskParentDO.getTaskInfo());
            JSONObject tbDisplayDefined = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
            String handleEndTime = null;
            if (tbDisplayDefined != null) {
                //巡店总结
                handleEndTime = tbDisplayDefined.getString("handleEndTime");
            }
            if(TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule()) && StringUtils.isNotBlank(handleEndTime)){
                subTaskDTO.setHanderEndTime(DateUtil.parse(handleEndTime + ":59", DatePattern.NORM_DATETIME_PATTERN).getTime());
            }
            return subTaskDTO;
        }

        List<String> storeIdList = taskStoreList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());

        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(enterpriseId, storeIdList);

        Map<String, StoreDO> storeMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));

        List<TbDisplayTableRecordDO> tbDisplayTableRecordList = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            tbDisplayTableRecordList = tbDisplayTableRecordMapper.listByUnifyTaskIdAndloopCountAndStoreIds(
                    enterpriseId, query.getUnifyTaskId(), query.getLoopCount(), new ArrayList<>(storeIdList));
        }else {
            tbDisplayTableRecordList = tbDisplayTableRecordMapper.listByUnifyTaskIdsAndloopCountAndStoreIds(
                    enterpriseId, query.getUnifyTaskIds(), query.getLoopCount(), new ArrayList<>(storeIdList));
        }

        Map<String, TbDisplayTableRecordDO> tbDisplayTableRecordMap =
                tbDisplayTableRecordList.stream().collect(Collectors.toMap(e -> e.getUnifyTaskId() + Constants.MOSAICS + e.getStoreId() +
                        Constants.MOSAICS + e.getLoopCount(), Function.identity(), (a, b) -> a));

        Map<String, List<PersonDTO>> map = getTaskPerson(enterpriseId, taskStoreList);
        //查询待处理的节点
        Map<String, List<PersonNodeNoDTO>> personNodeNoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(query.getUnifyTaskIds())){
            personNodeNoMap = getUserList(enterpriseId, query.getUnifyTaskId(), storeIdList, query.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode());
        }else {
            personNodeNoMap = getUserListByUnifyTaskList(enterpriseId, query.getUnifyTaskIds(), storeIdList, query.getLoopCount(), UnifyNodeEnum.FIRST_NODE.getCode());
        }
        EnterpriseUserDO userDO = null;
        if (Objects.nonNull(taskParentDO)){
            userDO = enterpriseUserDao.selectByUserId(enterpriseId, taskParentDO.getCreateUserId());
        }
        String createUserName = Objects.isNull(userDO) ? null : userDO.getName();
        Date now = new Date();
        //查询处理人、审核人列表
        PageInfo<DisplayTaskStoreLoopVO> pageInfo = new PageInfo(taskStoreList);
        Set<String> handlerUserIds = CollStreamUtil.toSet(taskStoreList, TaskStoreDO::getHandlerUserId);
        List<EnterpriseUserDO> handlerUsers = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(handlerUserIds));
        Map<String, PersonDTO> handlerUserMap = CollStreamUtil.toMap(handlerUsers, EnterpriseUserDO::getUserId, v -> new PersonDTO(v.getUserId(), v.getName(), v.getAvatar()));
        for (TaskStoreDO taskStoreDO : taskStoreList) {
            DisplayTaskStoreLoopVO displayTaskStoreLoopVO = new DisplayTaskStoreLoopVO();
            displayTaskStoreLoopVO.setStoreId(taskStoreDO.getStoreId());
            displayTaskStoreLoopVO.setId(taskStoreDO.getId());
            displayTaskStoreLoopVO.setRegionId(taskStoreDO.getRegionId());
            displayTaskStoreLoopVO.setStoreName(taskStoreDO.getStoreName());
            displayTaskStoreLoopVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            displayTaskStoreLoopVO.setTaskType(taskStoreDO.getTaskType());
            displayTaskStoreLoopVO.setLoopCount(taskStoreDO.getLoopCount());
            displayTaskStoreLoopVO.setNodeNo(taskStoreDO.getNodeNo());

            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + taskStoreDO.getUnifyTaskId() + "_" + taskStoreDO.getStoreId() + "_" + taskStoreDO.getLoopCount();
            displayTaskStoreLoopVO.setProcessing(false);
            if (StringUtils.isNotBlank(redisUtilPool.getString(taskStatusKey))) {
                displayTaskStoreLoopVO.setProcessing(true);
            }
            displayTaskStoreLoopVO.setSubBeginTime(taskStoreDO.getSubBeginTime());
            displayTaskStoreLoopVO.setSubEndTime(taskStoreDO.getSubEndTime());
            displayTaskStoreLoopVO.setTaskType(taskStoreDO.getTaskType());
            displayTaskStoreLoopVO.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            displayTaskStoreLoopVO.setHandleTime(taskStoreDO.getHandleTime());
            displayTaskStoreLoopVO.setHandlerEndTime(taskStoreDO.getHandlerEndTime());
            if (Objects.nonNull(taskParentDO)){
                displayTaskStoreLoopVO.setTaskName(taskParentDO.getTaskName());
            }
            displayTaskStoreLoopVO.setCreateUserName(createUserName);
            displayTaskStoreLoopVO.setCurrDate(now);
            displayTaskStoreLoopVO.setCreateTime(taskStoreDO.getCreateTime());
            String key = taskStoreDO.getUnifyTaskId() + Constants.MOSAICS + taskStoreDO.getStoreId() + Constants.MOSAICS + taskStoreDO.getLoopCount();
            TbDisplayTableRecordDO tbDisplayTableRecord = tbDisplayTableRecordMap.get(key);
            if (tbDisplayTableRecord != null) {
                displayTaskStoreLoopVO.setFirstHandlerTime(tbDisplayTableRecord.getFirstHandlerTime());
                displayTaskStoreLoopVO.setHandleUserName(StringUtils.isNotBlank(tbDisplayTableRecord.getRecheckUserName()) ?
                        tbDisplayTableRecord.getRecheckUserName() : tbDisplayTableRecord.getApproveUserName());
                displayTaskStoreLoopVO.setScore(tbDisplayTableRecord.getScore());
                displayTaskStoreLoopVO.setAiScore(tbDisplayTableRecord.getAiScore());
                displayTaskStoreLoopVO.setIsAiCheck(tbDisplayTableRecord.getIsAiCheck());
            }
            if(taskStoreDO.getHandlerEndTime() != null){
                subTaskDTO.setHanderEndTime(taskStoreDO.getHandlerEndTime().getTime());
            }
            if (taskStoreDO.getSubBeginTime() != null) {
                subTaskDTO.setSubBeginTime(taskStoreDO.getSubBeginTime().getTime());
            }
            if (taskStoreDO.getSubEndTime() != null) {
                subTaskDTO.setSubEndTime(taskStoreDO.getSubEndTime().getTime());
            }
            if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && UnifyStatus.ONGOING.getCode().equals(taskStoreDO.getSubStatus())){
                displayTaskStoreLoopVO.setHanderUser(changPersonDTO(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + taskStoreDO.getNodeNo())));
            }else {
                if (StringUtils.isNotBlank(taskStoreDO.getHandlerUserId())) {
                    // 有实际处理人的情况下驳回会给实际处理人发任务，因此展示实际处理人
                    displayTaskStoreLoopVO.setHanderUser(Collections.singletonList(handlerUserMap.get(taskStoreDO.getHandlerUserId())));
                } else {
                    displayTaskStoreLoopVO.setHanderUser(map.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + "1" + "#" + taskStoreDO.getLoopCount()));
                }
            }
            displayTaskStoreLoopVO.setApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "2" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setRecheckUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "3" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setThirdApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "4" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setFourApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "5" + "#" + taskStoreDO.getLoopCount()));
            displayTaskStoreLoopVO.setFiveApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + "6" + "#" + taskStoreDO.getLoopCount()));

            //历史数据已完成
            if(StringUtils.isBlank(displayTaskStoreLoopVO.getHandleUserName()) && UnifyNodeEnum.END_NODE.getCode().equals(displayTaskStoreLoopVO.getNodeNo())){
                List<PersonDTO> userList = CollectionUtils.isEmpty(displayTaskStoreLoopVO.getRecheckUser()) ? displayTaskStoreLoopVO.getApproveUser() : displayTaskStoreLoopVO.getRecheckUser();
                if(CollectionUtils.isNotEmpty(userList)){
                    List<String> list = userList.stream().map(PersonDTO::getUserName).collect(Collectors.toList());
                    displayTaskStoreLoopVO.setHandleUserName(StringUtils.join(list, ","));
                }
            }

            StoreDO storeDO = storeMap.get(taskStoreDO.getStoreId());
            if(storeDO !=null){
                displayTaskStoreLoopVO.setStoreOpenTime(storeDO.getOpenDate());
            }
            resultList.add(displayTaskStoreLoopVO);
        }
        pageInfo.setList(resultList);
        pageInfo.setTotal(taskStorePage.getTotal());
        subTaskDTO.setPageInfo(pageInfo);
        if(subTaskDTO.getHanderEndTime() == null){
            subTaskDTO.setHanderEndTime(subTaskDTO.getSubBeginTime());
        }
        return subTaskDTO;
    }

    /**
     * 找各个节点新增、删除的人
     * @param enterpriseId
     * @param taskStoreDO
     * @param handerUserList
     * @param approveUserList
     * @param recheckUserList
     * @return
     */
    @Override
    public Map<String, List<String>> getCurrentNodePersonChangeMap(String enterpriseId, TaskStoreDO taskStoreDO, List<String> handerUserList, List<String> approveUserList,
                                                                   List<String> recheckUserList, List<String> thirdApproveUserList,
                                                                   List<String> fourApproveUserList, List<String> fiveApproveUserList) {

        log.info("门店任务人员变更getCurrentNodePersonChangeMap  taskStoreDO：{}，handerUserList：{}，approveUserList：{}, recheckUserList :{} thirdApproveUserList,{}"  ,
                JSON.toJSONString(taskStoreDO), JSON.toJSONString(handerUserList), JSON.toJSONString(approveUserList), JSON.toJSONString(recheckUserList), JSON.toJSONString(thirdApproveUserList));

        //查找当前节点新增、删除的人，
        Map<String, List<String>> nodePersonMap = Maps.newHashMap();
//        Map<String, List<String>> existNodePersonMap = getNodePersonByTaskStore(taskStoreDO);
        // 当前节点存在的人
        List<String> existUserIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(),
                taskStoreDO.getNodeNo(), taskStoreDO.getLoopCount());
        List<String> changeUserIdList = null;
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = handerUserList;
        }else if (UnifyNodeEnum.SECOND_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = approveUserList;
        }else if (UnifyNodeEnum.THIRD_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = recheckUserList;
        }else if (UnifyNodeEnum.FOUR_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = thirdApproveUserList;
        }else if (UnifyNodeEnum.FIVE_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = fourApproveUserList;
        }else if (UnifyNodeEnum.SIX_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            changeUserIdList = fiveApproveUserList;
        }
        // 找新增和删除的处理人
        if(CollectionUtils.isNotEmpty(changeUserIdList) && CollectionUtils.isNotEmpty(existUserIdList)){
            List<String> newAddUserId = ListUtils.emptyIfNull(changeUserIdList)
                    .stream().filter(data -> StringUtils.isNotBlank(data) && !existUserIdList.contains(data))
                    .collect(Collectors.toList());
            List<String> finalChangeUserIdList = changeUserIdList;
            List<String> removeUserId = ListUtils.emptyIfNull(existUserIdList)
                    .stream().filter(data -> !finalChangeUserIdList.contains(data))
                    .collect(Collectors.toList());
            nodePersonMap.put(Constants.PERSON_CHANGE_KEY_NEWADD, newAddUserId);
            nodePersonMap.put(Constants.PERSON_CHANGE_KEY_REMOVE, removeUserId);
            log.info("##currentNodePersonChangeMap ={}", JSON.toJSONString(nodePersonMap));
        }
        return nodePersonMap;
    }

    @Override
    public Map<String, List<String>> selectTaskStorAllNodePerson(String enterpriseId, Long taskId, String storeId, Long loopCount) {

        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskId, storeId, loopCount);
        if(taskStoreDO == null){
            // 自主巡店任务id为0
            return Maps.newHashMap();
        }
        fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);
        Map<String, List<String>> nodePersonMap = getNodePersonByTaskStore(taskStoreDO);
        log.info("##selectTaskStorAllNodePerson ={}", JSON.toJSONString(nodePersonMap));
        return nodePersonMap;
    }

    /**
     * 转交替换相应节点人员
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @param node
     * @param fromUserId
     * @param toUserId
     */
    @Override
    public TaskStoreDO replaceTaskStoreNodePerson(String enterpriseId, Long taskId, String storeId, Long loopCount, String node, String fromUserId, String toUserId) {
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskId, storeId, loopCount);
        if(taskStoreDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店任务不存在replaceTaskStoreNodePerson【"+taskId+"】");
        }
        fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);
        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if(extendInfoJsonObj == null){
            log.info("转交替换相应节点人员失败 门店任务节点人员还未生成 :{},企业id:{}", taskStoreDO.getId(), enterpriseId);
            return taskStoreDO;
        }
        String nodePerson = extendInfoJsonObj.getString(node);
        // 前后加逗号替换  ？？？
        nodePerson = nodePerson.replace(Constants.COMMA + fromUserId + Constants.COMMA, Constants.COMMA + toUserId + Constants.COMMA);
        extendInfoJsonObj.put(node, nodePerson);
        log.info("门店任务节点人员变更后:{}", JSON.toJSONString(extendInfoJsonObj));
        taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), JSON.toJSONString(extendInfoJsonObj), null, null);
        return taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreDO.getId());
    }

    @Override
    public List<TaskStoreDO> listTaskStoreByEid(String enterpriseId, Boolean isRunIncrement, Long maxId) {
        return taskStoreMapper.listTaskStoreByEid(enterpriseId, isRunIncrement, maxId);
    }

//    /**
//     * 订正门店任务节点人员
//     * @param enterpriseId
//     * @param taskStoreDO
//     */
//    @Override
//    public void correctionTaskStoreNodePerson(String enterpriseId, TaskStoreDO taskStoreDO, String dbName) {
//        try {
//            DataSourceHelper.changeToSpecificDataSource(dbName);
//            List<UnifyPersonDTO> unifyPersonDTOS = taskMappingMapper.selectPersonInfo(enterpriseId,
//                    TaskMappingDO.builder().unifyTaskId(taskStoreDO.getUnifyTaskId()).type(taskStoreDO.getStoreId()).build(), null);
//            Map<String, Set<String>> nodePersonSetMap = ListUtils.emptyIfNull(unifyPersonDTOS).stream().filter(s -> Objects.nonNull(s.getNode()))
//                    .collect(Collectors.groupingBy(s -> s.getNode(), Collectors.mapping(s -> s.getUserId(), Collectors.toSet())));
//
//            // 订正数据时   组装门店任务表  处理人、审批人、复审人、抄送人
//            fillTaskStoreExtendAndCcInfo(taskStoreDO, nodePersonSetMap);
//            // 更新
//            taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), taskStoreDO.getExtendInfo(), taskStoreDO.getCcUserIds(), taskStoreDO.getOriginalExtendInfo());
//            List<TaskStoreDO> TaskStoreList = new ArrayList();
//            TaskStoreDO taskStore = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreDO.getId());
//            TaskStoreList.add(taskStore);
//            this.uniteHandlerTaskStoreEsData(enterpriseId,TaskStoreList);
//        } catch (Exception e) {
//            log.error("correctionTaskStoreNodePerson,订正失败, 企业id:{}, 门店任务:{} ", enterpriseId, JSON.toJSONString(taskStoreDO), e);
//        }
//    }

    @Override
    public List<String> selectCcPersonInfoByTaskList(String enterpriseId, Long taskId, String storeId, Long loopCount) {
        Map<String, List<String>> nodePersonMap = this.selectTaskStorAllNodePerson(enterpriseId, taskId, storeId, loopCount);
        List<String> ccUserIdList = nodePersonMap.get(UnifyNodeEnum.CC.getCode());
        if(CollectionUtils.isEmpty(ccUserIdList)){
            return Collections.emptyList();
        }
        return ccUserIdList;
    }

    @Override
    public List<String> selectAuditUserIdList(String enterpriseId, Long taskId, String storeId, Long loopCount) {
        Map<String, List<String>> nodePersonMap = this.selectTaskStorAllNodePerson(enterpriseId, taskId, storeId, loopCount);
        List<String> auditUserIdList = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
        if(CollectionUtils.isEmpty(auditUserIdList)){
            return Collections.emptyList();
        }
        return auditUserIdList;
    }

    /**
     * 查找指定任务 指定门店集合下的人员  包括 创建人、处理人、抄送人、审批人。。。 单个分享、批量分享、巡店分享
     * @param enterpriseId
     * @param storeIdList
     * @param taskId
     * @return
     */
    @Override
    public List<StorePersonDto> selectTaskPersonByTaskAndStore(String enterpriseId, List<String> storeIdList, Long taskId) {

        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.listByTaskIdAndStoreIdList(enterpriseId, Collections.singletonList(taskId), storeIdList, null);
        fillExtendAndCcInfoByList(enterpriseId, taskStoreDOList);
        List<StorePersonDto> storePersonDtoList = taskStoreDOList.stream()
                .map(t -> translateToStorePersonDto(t)).collect(Collectors.toList());
        return storePersonDtoList;

    }

    /**
     * 散开门店任务节点人员信息
     * @param enterpriseId
     * @param taskIdList
     * @param storeIdList
     * @return
     */
    @Override
    public List<UnifyPersonDTO> selectALLNodeUserInfoList(String enterpriseId, List<Long> taskIdList, List<String> storeIdList, Long loopCount) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return Lists.newArrayList();
        }
        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.listByTaskIdAndStoreIdList(enterpriseId, taskIdList, storeIdList, loopCount);
        if (CollectionUtils.isEmpty(taskStoreDOList)) {
            return Lists.newArrayList();
        }
        fillExtendAndCcInfoByList(enterpriseId, taskStoreDOList);
        return  getALLNodeUserInfoByList(enterpriseId, taskStoreDOList);

    }

    /**
     * 更新重新分配后的人员
     * @param enterpriseId
     * @param taskStoreDO
     * @param handerUserList
     * @param approveUserList
     * @param recheckUserList
     */
    @Override
    public String updateReallocateNodePerson(String enterpriseId, TaskStoreDO taskStoreDO, List<String> handerUserList, List<String> approveUserList,
                                           List<String> recheckUserList , List<String>  thirdApproveUserList, List<String> fourApproveUserList, List<String> fiveApproveUserList,
                                           TaskSubVO taskSubVO, List<String> newaddPersonList) {

        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if(extendInfoJsonObj == null){
            log.info("重新分配相应节点人员失败 门店任务节点人员还未生成 :{},企业id:{}", taskStoreDO.getId(), enterpriseId);
            return null;
        }
        // 原来节点不为空
        String handleUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        String auditUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SECOND_NODE.getCode());
        String recheckUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.THIRD_NODE.getCode());
        String thirdApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FOUR_NODE.getCode());
        String fourApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIVE_NODE.getCode());
        String fiveApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SIX_NODE.getCode());
        if (UnifyNodeEnum.SIX_NODE.getCode().equals(taskStoreDO.getNodeNo()) && CollectionUtils.isNotEmpty(fiveApproveUserList)
                && StringUtils.isNotBlank(fiveApproveUserIdStr)) {
            extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
        } else if (UnifyNodeEnum.FIVE_NODE.getCode().equals(taskStoreDO.getNodeNo()) && CollectionUtils.isNotEmpty(fourApproveUserList)
                && StringUtils.isNotBlank(fourApproveUserIdStr)) {
            extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdList(fourApproveUserList));
            if(CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
            }
        } else if (UnifyNodeEnum.FOUR_NODE.getCode().equals(taskStoreDO.getNodeNo()) && CollectionUtils.isNotEmpty(thirdApproveUserList)
                && StringUtils.isNotBlank(thirdApproveUserIdStr)) {
            extendInfoJsonObj.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdList(thirdApproveUserList));
            if(CollectionUtils.isNotEmpty(fourApproveUserList) && StringUtils.isNotBlank(fourApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdList(fourApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
            }
        }else if (UnifyNodeEnum.THIRD_NODE.getCode().equals(taskStoreDO.getNodeNo()) && CollectionUtils.isNotEmpty(recheckUserList)
                && StringUtils.isNotBlank(recheckUserIdStr)) {
            extendInfoJsonObj.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdList(recheckUserList));
            if(CollectionUtils.isNotEmpty(thirdApproveUserList) && StringUtils.isNotBlank(thirdApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdList(thirdApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fourApproveUserList) && StringUtils.isNotBlank(fourApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdList(fourApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
            }
        }else if(UnifyNodeEnum.SECOND_NODE.getCode().equals(taskStoreDO.getNodeNo())){
            if(CollectionUtils.isNotEmpty(approveUserList) && StringUtils.isNotBlank(auditUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdList(approveUserList));
            }
            if(CollectionUtils.isNotEmpty(recheckUserList) && StringUtils.isNotBlank(recheckUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdList(recheckUserList));
            }
            if(CollectionUtils.isNotEmpty(thirdApproveUserList) && StringUtils.isNotBlank(thirdApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdList(thirdApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fourApproveUserList) && StringUtils.isNotBlank(fourApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdList(fourApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
            }
        }else if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo())){
            if(CollectionUtils.isNotEmpty(handerUserList) && StringUtils.isNotBlank(handleUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdList(handerUserList));
            }
            if(CollectionUtils.isNotEmpty(approveUserList) && StringUtils.isNotBlank(auditUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdList(approveUserList));
            }
            if(CollectionUtils.isNotEmpty(recheckUserList) && StringUtils.isNotBlank(recheckUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdList(recheckUserList));
            }
            if(CollectionUtils.isNotEmpty(thirdApproveUserList) && StringUtils.isNotBlank(thirdApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdList(thirdApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fourApproveUserList) && StringUtils.isNotBlank(fourApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdList(fourApproveUserList));
            }
            if(CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)){
                extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdList(fiveApproveUserList));
            }
        }
        String extendInfoJsonStr =  JSON.toJSONString(extendInfoJsonObj);
        log.info("门店任务节点人员重新分配后:{}", extendInfoJsonStr);
        taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), extendInfoJsonStr, null, null);
        return extendInfoJsonStr;
    }

    @Override
    public void updateHandlerUserAfterReject(String enterpriseId, TaskSubDO taskSubDO, String handerUser) {
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        if(null == taskStoreDO){
            return;
        }
        fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);
        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if(extendInfoJsonObj == null){
            log.info("处理拒绝后更新处理人失败 门店任务节点人员还未生成 :{},企业id:{}", taskStoreDO.getId(), enterpriseId);
            return;
        }
        extendInfoJsonObj.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdList(Collections.singletonList(handerUser)));
        log.info("处理拒绝后更新处理人:{}", JSON.toJSONString(extendInfoJsonObj));
        taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), JSON.toJSONString(extendInfoJsonObj), null, null);
    }

    @Override
    public List<PersonDTO> getCurrentNodePerson(String enterpriseId, Long taskStoreId, String nodeNo) {
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        Map<String, List<PersonNodeNoDTO>> personNodeNoMap = getUserList(enterpriseId, taskStoreDO.getUnifyTaskId(), Lists.newArrayList(taskStoreDO.getStoreId()), taskStoreDO.getLoopCount(), null);
        Map<String, List<PersonDTO>> map = getTaskPerson(enterpriseId, Lists.newArrayList(taskStoreDO));
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && UnifyStatus.ONGOING.getCode().equals(taskStoreDO.getSubStatus())){
            return changPersonDTO(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + taskStoreDO.getNodeNo()));
        }
        return map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + nodeNo + "#" + taskStoreDO.getLoopCount());
    }

    @Override
    public TaskPersonVO getNodePersonForReallocate(String enterpriseId, Long taskStoreId) {
        TaskPersonVO taskPersonVO = new TaskPersonVO();
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        Map<String, List<PersonDTO>> map = getTaskPerson(enterpriseId, Lists.newArrayList(taskStoreDO));
        Map<String, List<PersonNodeNoDTO>> personNodeNoMap = getUserList(enterpriseId, taskStoreDO.getUnifyTaskId(), Lists.newArrayList(taskStoreDO.getStoreId()), taskStoreDO.getLoopCount(), null);
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && UnifyStatus.ONGOING.getCode().equals(taskStoreDO.getSubStatus())){
            taskPersonVO.setHandleUser(changPersonDTO(personNodeNoMap.get(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId() + "#" + taskStoreDO.getNodeNo())));
        }else {
            taskPersonVO.setHandleUser(map.get(taskStoreDO.getUnifyTaskId()+"#" + taskStoreDO.getStoreId() + "#"  + UnifyNodeEnum.FIRST_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
        }
        taskPersonVO.setApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + UnifyNodeEnum.SECOND_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
        taskPersonVO.setSecondApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + UnifyNodeEnum.THIRD_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
        taskPersonVO.setThirdApproveUser(map.get(taskStoreDO.getUnifyTaskId()+"#"+taskStoreDO.getStoreId() + "#" + UnifyNodeEnum.FOUR_NODE.getCode() + "#" + taskStoreDO.getLoopCount()));
        taskPersonVO.setNodeNo(taskStoreDO.getNodeNo());
        return taskPersonVO;
    }

    @Override
    public void fillSingleTaskStoreExtendAndCcInfo(String enterpriseId, TaskStoreDO  taskStoreDO) {
        List<TaskStoreDO> taskStoreHasExtendCcInfoList = taskStoreMapper.selectExtendAndCcInfoByTaskStoreIds(enterpriseId, Collections.singletonList(taskStoreDO.getId()));
        taskStoreDO.setCcUserIds(taskStoreHasExtendCcInfoList.get(0).getCcUserIds());
        taskStoreDO.setExtendInfo(taskStoreHasExtendCcInfoList.get(0).getExtendInfo());
    }

    private List<UnifyPersonDTO> getALLNodeUserInfoByList(String enterpriseId, List<TaskStoreDO> taskStoreDOList) {
        List<UnifyPersonDTO> result = Lists.newArrayList();
        if(CollectionUtils.isEmpty(taskStoreDOList)){
            return result;
        }
        // 收集所有用户id
        List<List<UnifyPersonDTO>> aLLNodeUserInfoList = taskStoreDOList.stream()
                .map(t -> getALLNodeUserInfoBySingle(t)).collect(Collectors.toList());
        aLLNodeUserInfoList.forEach(p -> {
            result.addAll(p);
        });
        List<String> userIdList = result.stream()
                .map(UnifyPersonDTO::getUserId)
                .collect(Collectors.toList());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String,EnterpriseUserDO> enterpriseUserDOMap = CollectionUtils.emptyIfNull(enterpriseUserDOList).stream()
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, data -> data,(a, b) -> a));
        result.forEach(p -> {
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDOMap.get(p.getUserId());
            if(enterpriseUserDO != null){
                p.setUserName(enterpriseUserDO.getName());
                p.setAvatar(enterpriseUserDO.getAvatar());
            }else {
                log.info("用户不存在，企业id :{},用户id:{}", enterpriseId, p.getUserId());
                if (Constants.SYSTEM_USER_ID.equals(p.getUserId())) {
                    log.info("system用户设置userName为system");
                    p.setUserName(Constants.SYSTEM_USER_NAME);
                }
                if (Constants.AI.equals(p.getUserId())) {
                    log.info("AI用户设置userName为AI");
                    p.setUserName(Constants.AI);
                }
            }

        });
        return result;
    }



    // 散开门店任务节点人员信息
    private List<UnifyPersonDTO> getALLNodeUserInfoBySingle(TaskStoreDO taskStoreDO) {
        List<UnifyPersonDTO> aLLNodeUserInfoList = Lists.newArrayList();
        Map<String, List<String>> nodePersonMap = getNodePersonByTaskStore(taskStoreDO);
        List<String> createUserIdList = nodePersonMap.get(UnifyNodeEnum.ZERO_NODE.getCode());
        List<String> handleUserIdList = nodePersonMap.get(UnifyNodeEnum.FIRST_NODE.getCode());
        List<String> auditUserIdList = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
        List<String> recheckUserIdList =  nodePersonMap.get(UnifyNodeEnum.THIRD_NODE.getCode());
        List<String> thirdApproveList =  nodePersonMap.get(UnifyNodeEnum.FOUR_NODE.getCode());
        List<String> fourApproveList =  nodePersonMap.get(UnifyNodeEnum.FIVE_NODE.getCode());
        List<String> fiveApproveList =  nodePersonMap.get(UnifyNodeEnum.SIX_NODE.getCode());

        List<String> ccUserIdList = nodePersonMap.get(UnifyNodeEnum.CC.getCode());
        if(CollectionUtils.isNotEmpty(createUserIdList)){
            List<UnifyPersonDTO> createUserInfoList = createUserIdList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.ZERO_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(createUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(handleUserIdList)){
            List<UnifyPersonDTO> handleUserInfoList = handleUserIdList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.FIRST_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(handleUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(auditUserIdList)){
            List<UnifyPersonDTO> auditUserInfoList = auditUserIdList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.SECOND_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(auditUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(recheckUserIdList)){
            List<UnifyPersonDTO> recheckUserInfoList = recheckUserIdList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.THIRD_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(recheckUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(thirdApproveList)){
            List<UnifyPersonDTO> thirdApproveUserInfoList = thirdApproveList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.FOUR_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(thirdApproveUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(fourApproveList)){
            List<UnifyPersonDTO> fourApproveUserInfoList = fourApproveList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.FIVE_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(fourApproveUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(fiveApproveList)){
            List<UnifyPersonDTO> fiveApproveUserInfoList = fiveApproveList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.SIX_NODE.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(fiveApproveUserInfoList);
        }
        if(CollectionUtils.isNotEmpty(ccUserIdList)){
            List<UnifyPersonDTO> ccUserInfoList = ccUserIdList.stream()
                    .map(e -> transUnifyPersonDTO(taskStoreDO, e, UnifyNodeEnum.CC.getCode())).collect(Collectors.toList());
            aLLNodeUserInfoList.addAll(ccUserInfoList);
        }
        return aLLNodeUserInfoList;
    }

    public UnifyPersonDTO transUnifyPersonDTO(TaskStoreDO taskStoreDO, String userId, String node){
        return UnifyPersonDTO.builder()
                .unifyTaskId(taskStoreDO.getUnifyTaskId())
                .userId(userId)
                .storeId(taskStoreDO.getStoreId())
                .node(node)
                .loopCount(taskStoreDO.getLoopCount())
                .subTaskCode(taskStoreDO.getUnifyTaskId() + "#" + taskStoreDO.getStoreId())
                .build();
    }

    private StorePersonDto translateToStorePersonDto(TaskStoreDO taskStoreDO) {
        StorePersonDto storePersonDto = new StorePersonDto();
        List<String> allUserIdList = Lists.newArrayList();
        storePersonDto.setStoreId(taskStoreDO.getStoreId());
        storePersonDto.setLoopCount(taskStoreDO.getLoopCount());
        Map<String, List<String>> nodePersonMap = getNodePersonByTaskStore(taskStoreDO);
        List<String> createUserIdList = nodePersonMap.get(UnifyNodeEnum.ZERO_NODE.getCode());
        List<String> handleUserIdList = nodePersonMap.get(UnifyNodeEnum.FIRST_NODE.getCode());
        List<String> auditUserIdList = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
        List<String> recheckUserIdList =  nodePersonMap.get(UnifyNodeEnum.THIRD_NODE.getCode());
        List<String> thirdApproveUserIdList =  nodePersonMap.get(UnifyNodeEnum.FOUR_NODE.getCode());
        List<String> fourApproveUserIdList =  nodePersonMap.get(UnifyNodeEnum.FIVE_NODE.getCode());
        List<String> fiveApproveUserIdList =  nodePersonMap.get(UnifyNodeEnum.SIX_NODE.getCode());
        List<String> ccUserIdList = nodePersonMap.get(UnifyNodeEnum.CC.getCode());
        if(CollectionUtils.isNotEmpty(createUserIdList)){
            allUserIdList.addAll(createUserIdList);
        }
        if(CollectionUtils.isNotEmpty(handleUserIdList)){
            allUserIdList.addAll(handleUserIdList);
        }
        if(CollectionUtils.isNotEmpty(auditUserIdList)){
            allUserIdList.addAll(auditUserIdList);
        }
        if(CollectionUtils.isNotEmpty(recheckUserIdList)){
            allUserIdList.addAll(recheckUserIdList);
        }
        if(CollectionUtils.isNotEmpty(thirdApproveUserIdList)){
            allUserIdList.addAll(thirdApproveUserIdList);
        }
        if(CollectionUtils.isNotEmpty(fourApproveUserIdList)){
            allUserIdList.addAll(fourApproveUserIdList);
        }
        if(CollectionUtils.isNotEmpty(fiveApproveUserIdList)){
            allUserIdList.addAll(fiveApproveUserIdList);
        }
        if(CollectionUtils.isNotEmpty(ccUserIdList)){
            allUserIdList.addAll(ccUserIdList);
        }
        allUserIdList = allUserIdList.stream().distinct().collect(Collectors.toList());
        storePersonDto.setUserIdList(allUserIdList);
        return storePersonDto;
    }

    // 根据门店任务获取各个节点人员 处理人、审批人、复审人、抄送人
    @Override
    public Map<String, List<String>> getNodePersonByTaskStore(TaskStoreDO taskStoreDO) {
        Map<String, List<String>> nodePersonMap = Maps.newHashMap();

        nodePersonMap.put(UnifyNodeEnum.ZERO_NODE.getCode(), Collections.singletonList(taskStoreDO.getCreateUserId()));
        // 抄送人
        if(StringUtils.isNotBlank(taskStoreDO.getCcUserIds())){
            nodePersonMap.put(UnifyNodeEnum.CC.getCode(), toUserIdListByString(taskStoreDO.getCcUserIds()));
        }
        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if(extendInfoJsonObj == null){
            return  nodePersonMap;
        }
        String handleUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        if(StringUtils.isNotBlank(handleUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.FIRST_NODE.getCode(), toUserIdListByString(handleUserIdStr));
        }

        String auditUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SECOND_NODE.getCode());
        if(StringUtils.isNotBlank(auditUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.SECOND_NODE.getCode(),  toUserIdListByString(auditUserIdStr));
        }

        String recheckUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.THIRD_NODE.getCode());
        if(StringUtils.isNotBlank(recheckUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.THIRD_NODE.getCode(), toUserIdListByString(recheckUserIdStr));
        }
        String thirdApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FOUR_NODE.getCode());
        if(StringUtils.isNotBlank(thirdApproveUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.FOUR_NODE.getCode(), toUserIdListByString(thirdApproveUserIdStr));
        }
        String fourApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIVE_NODE.getCode());
        if(StringUtils.isNotBlank(fourApproveUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.FIVE_NODE.getCode(), toUserIdListByString(fourApproveUserIdStr));
        }
        String sixApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SIX_NODE.getCode());
        if(StringUtils.isNotBlank(sixApproveUserIdStr)){
            nodePersonMap.put(UnifyNodeEnum.SIX_NODE.getCode(), toUserIdListByString(sixApproveUserIdStr));
        }
        return  nodePersonMap;

    }

    @Override
    public TaskStoreDO getTaskStoreDetail(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        return taskStoreDao.getTaskStoreDetail(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    @Override
    public Map<String, List<PersonDTO>> getTaskPerson(String enterpriseId, List<TaskStoreDO> taskStoreDOList) {

        fillExtendAndCcInfoByList(enterpriseId, taskStoreDOList);

        List<UnifyPersonDTO> unifyPersonList = getALLNodeUserInfoByList(enterpriseId, taskStoreDOList);
        return unifyPersonList.stream()
                .collect(Collectors.groupingBy(e -> e.getUnifyTaskId() + "#" + e.getStoreId() +"#" + e.getNode() + "#" + e.getLoopCount() ,
                        Collectors.mapping(s -> new PersonDTO(s.getUserId(), s.getUserName(), s.getAvatar()), Collectors.toList())));
    }

    @Override
    public List<DisplayTaskVO> getDisplayStoreTaskList(String enterpriseId, DisplayTaskDTO reqDTO) {
        DisplayTaskQuery query = convertToDisplayTaskQuery(enterpriseId, reqDTO);
        if (StringUtils.isNotBlank(reqDTO.getParentTaskName())) {
            List<Long> unifyTaskIds = taskParentMapper.selectIdByName(enterpriseId, reqDTO.getParentTaskName());
            query.setUnifyTaskIds(unifyTaskIds);
        }
        List<TaskStoreDO> esList = elasticSearchService.getDisplayStoreTaskList(enterpriseId, query);
        if (CollectionUtils.isEmpty(esList)) {
            return Collections.emptyList();
        }
        // 查询父任务
        List<Long> parentTaskIds = ListUtil.toList(CollStreamUtil.toSet(esList, TaskStoreDO::getUnifyTaskId));
        List<TaskParentDO> parentTaskList = taskParentMapper.selectTaskByIds(enterpriseId, parentTaskIds);
        Map<Long, TaskParentDO> parentTaskMaps = CollStreamUtil.toMap(parentTaskList, TaskParentDO::getId, v -> v);

        return CollStreamUtil.toList(esList, v -> convertToDisplayTaskVO(v, parentTaskMaps.get(v.getUnifyTaskId())));
    }

    @Override
    public void batchStopTask(String enterpriseId, IdListDTO idListDTO, EnterpriseConfigDO enterpriseConfig, CurrentUser user) {
        //只删除待处理的任务
        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.taskStoreListByIdList(enterpriseId,idListDTO.getIdList());
        for (TaskStoreDO taskStoreDO : taskStoreDOList) {
            TbDisplayDeleteParam tbDisplayDeleteParam = new TbDisplayDeleteParam();
            tbDisplayDeleteParam.setTaskStoreId(taskStoreDO.getId());
            //待审批中的门店任务只删除待办表
            taskSubMapper.updateSubStatusComplete(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
            if (taskStoreDO.getTaskType().equals(TB_DISPLAY_TASK.getCode())){
                tbDisplayTableRecordService.deleteRecord(enterpriseId,tbDisplayDeleteParam,user,"notDone", enterpriseConfig);
            }else {//其他类型删除子任务
                patrolStoreService.deleteRecord(enterpriseId,tbDisplayDeleteParam,user,"notDone", enterpriseConfig);
            }
        }
    }

    @Override
    public void batchPostponeTask(String enterpriseId, PostponeTaskDTO postponeTaskDTO, EnterpriseConfigDO enterpriseConfig, CurrentUser user) {
        List<TaskStoreDO> taskStoreDOList = taskStoreMapper.taskStoreListByIdList(enterpriseId,postponeTaskDTO.getIdList());
        taskStoreDOList.forEach(taskStoreDO -> {
            taskStoreDO.setSubEndTime(postponeTaskDTO.getPostponeTime());
            if(!TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskStoreDO.getTaskType())){
                taskStoreDO.setHandlerEndTime(postponeTaskDTO.getPostponeTime());
            }
            taskStoreMapper.updateByPrimaryKey(enterpriseId, taskStoreDO);
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskStoreDO.getTaskType())){
                TbDisplayTableRecordDO displayTableRecordDO = tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
                if(displayTableRecordDO != null){
                    displayTableRecordDO.setSubEndTime(postponeTaskDTO.getPostponeTime());
                    tbDisplayTableRecordMapper.updateByPrimaryKeySelective(enterpriseId, displayTableRecordDO);
                    TbDisplayHistoryDO historyDO = new TbDisplayHistoryDO();
                    historyDO.setIsValid(true);
                    historyDO.setOperateType(TbDisplayConstant.TbDisplayRecordStatusConstant.DELAY);
                    historyDO.setOperateUserId(user.getUserId());
                    historyDO.setOperateUserName(user.getName());
                    historyDO.setSubTaskId(0L);
                    historyDO.setActionKey("");
                    historyDO.setCreateTime(new Date());
                    historyDO.setRecordId(displayTableRecordDO.getId());
                    historyDO.setRemark("任务延期至：" + DateUtil.format(postponeTaskDTO.getPostponeTime(), DateUtils.DATE_FORMAT_SEC_4) +
                            "\n" + postponeTaskDTO.getRemark());
                }
            }else {//其他类型删除子任务
                TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCountAndOne(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
                if(tbPatrolStoreRecordDO != null){
                    tbPatrolStoreRecordDO.setSubEndTime(postponeTaskDTO.getPostponeTime());
                    tbPatrolStoreRecordMapper.updateByPrimaryKeySelective(tbPatrolStoreRecordDO, enterpriseId);
                    TbPatrolStoreHistoryDo historyDO = new TbPatrolStoreHistoryDo();
                    historyDO.setOperateType(PatrolStoreConstant.ActionKeyConstant.DELAY);
                    historyDO.setOperateUserId(user.getUserId());
                    historyDO.setOperateUserName(user.getName());
                    historyDO.setSubTaskId(0L);
                    historyDO.setCreateTime(new Date());
                    historyDO.setBusinessId(tbPatrolStoreRecordDO.getId());
                    historyDO.setNodeNo(taskStoreDO.getNodeNo());
                    historyDO.setRemark(postponeTaskDTO.getRemark());
                    historyDO.setDeleted(false);
                    historyDO.setActionKey(PatrolStoreConstant.ActionKeyConstant.DELAY);
                    historyDO.setRemark("任务延期至：" + DateUtil.format(postponeTaskDTO.getPostponeTime(), DateUtils.DATE_FORMAT_SEC_4) +
                            "\n" + postponeTaskDTO.getRemark());
                    tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, historyDO);
                }
            }
        });
    }

    /**
     * 陈列任务DTO对象转换为陈列任务ES查询对象
     * @param enterpriseId 企业id
     * @param reqDTO 陈列任务DTO
     * @return 陈列任务ES查询对象
     */
    private DisplayTaskQuery convertToDisplayTaskQuery(String enterpriseId, DisplayTaskDTO reqDTO) {
        DisplayTaskQuery query = new DisplayTaskQuery();
        EnterpriseUserDO user = enterpriseUserDao.selectByJobnumber(enterpriseId, reqDTO.getJobnumber());
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        query.setUserId(user.getUserId());
        if (StringUtils.isNotBlank(reqDTO.getStoreNum())) {
            StoreDO storeDO = storeMapper.selectStoreNameByNum(enterpriseId, reqDTO.getStoreNum());
            if (ObjectUtil.isNull(storeDO)) {
                throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
            }
            query.setStoreId(storeDO.getStoreId());
        }
        query.setStartTime(reqDTO.getStartTime());
        query.setEndTime(reqDTO.getEndTime());
        query.setStatus(reqDTO.getStatus());
        query.setReturnLimit(reqDTO.getReturnLimit());
        return query;
    }

    /**
     * TaskStoreDO转换为DisplayTaskVO
     * @param taskStoreDO 门店任务DO对象
     * @param parentTask 父任务DO对象
     * @return 陈列任务VO
     */
    private DisplayTaskVO convertToDisplayTaskVO(TaskStoreDO taskStoreDO, TaskParentDO parentTask) {
        if (ObjectUtil.isNull(parentTask)) {
            return null;
        }
        DisplayTaskVO vo = new DisplayTaskVO();
        vo.setStoreName(taskStoreDO.getStoreName());
        vo.setId(taskStoreDO.getId());
        vo.setStartTime(String.valueOf(taskStoreDO.getCreateTime().toInstant().toEpochMilli()));
        vo.setEndTime(String.valueOf(taskStoreDO.getHandlerEndTime().toInstant().toEpochMilli()));
        vo.setParentTaskName(parentTask.getTaskName());
        vo.setParentTaskDesc(parentTask.getTaskDesc());
        if ("1".equals(taskStoreDO.getNodeNo())) {
            vo.setStatus("handle");
        } else if ("endNode".equals(taskStoreDO.getNodeNo())) {
            vo.setStatus("complete");
        } else if (UnifyNodeEnum.getApproveNoList().contains(taskStoreDO.getNodeNo())) {
            vo.setStatus("approval");
        }
        return vo;
    }


    private Map<String, List<PersonNodeNoDTO>> getUserListByUnifyTaskList(String enterpriseId, List<String> unifyTaskIds, List<String> storeIdList, Long loopCount, String nodeNo){
        List<PersonNodeNoDTO> personNodeNoDTOList = taskSubMapper.selectUserIdByLoopCountAndStoreIdListAndUnifyTaskList(enterpriseId, unifyTaskIds, storeIdList, loopCount, null);

        if(CollectionUtils.isEmpty(personNodeNoDTOList)){
            return new HashMap<>();
        }

        List<String> userIdList = personNodeNoDTOList.stream().map(PersonNodeNoDTO::getUserId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            Map<String, String> peopleMap = new HashMap<>();
            for(EnterpriseUserDO enterpriseUserDO : userList){
                peopleMap.put(enterpriseUserDO.getUserId(), enterpriseUserDO.getName());
            }

            for(PersonNodeNoDTO person : personNodeNoDTOList){
                if(Constants.AI.equals(person.getUserId())){
                    person.setUserName(Constants.AI);
                }else {
                    person.setUserName(peopleMap.get(person.getUserId()));
                }
            }
        }
        return personNodeNoDTOList.stream().collect(Collectors.groupingBy(e -> e.getUnifyTaskId() + "#" + e.getStoreId() + "#" + e.getNodeNo()));
    }

    private Map<String, List<PersonNodeNoDTO>> getUserList(String enterpriseId, Long unifyTaskId, List<String> storeIdList, Long loopCount, String nodeNo){
        List<PersonNodeNoDTO> personNodeNoDTOList = taskSubMapper.selectUserIdByLoopCountAndStoreIdList(enterpriseId, unifyTaskId, storeIdList, loopCount, null);

        if(CollectionUtils.isEmpty(personNodeNoDTOList)){
            return new HashMap<>();
        }

        List<String> userIdList = personNodeNoDTOList.stream().map(PersonNodeNoDTO::getUserId).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            Map<String, String> peopleMap = new HashMap<>();
            for(EnterpriseUserDO enterpriseUserDO : userList){
                peopleMap.put(enterpriseUserDO.getUserId(), enterpriseUserDO.getName());
            }

            for(PersonNodeNoDTO person : personNodeNoDTOList){
                if(Constants.AI.equals(person.getUserId())){
                    person.setUserName(Constants.AI);
                }else {
                    person.setUserName(peopleMap.get(person.getUserId()));
                }
            }
        }
        return personNodeNoDTOList.stream().collect(Collectors.groupingBy(e -> e.getUnifyTaskId() + "#" + e.getStoreId() + "#" + e.getNodeNo()));
    }
    // 更新流程引擎当前节点的  审批人 或 复审人


    @Override
    public void taskReissue(String enterpriseId, Long taskId, String storeId, Long loopCount, UnifySubTaskForStoreData unifySubTaskForStoreData) {
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskId, storeId, loopCount);
        if(CollectionUtils.isEmpty(unifySubTaskForStoreData.getAddProcessList())){
            log.info("taskReissue#没有人员增加不补发eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, taskId, storeId, loopCount);
            return;
        }
        if(taskStoreDO == null){
            log.info("taskReissue#任务不存在eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, taskId, storeId, loopCount);
            return;
        }
        if(UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())){
            log.info("taskReissue#任务已完成，不在进行补发，eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, taskId, storeId, loopCount);
            return;
        }

        this.fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskStoreDO.getUnifyTaskId());
        //最新一条，防止空指针
        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null, taskStoreDO.getNodeNo());

        List<TaskMappingDO> personList = Lists.newArrayList();
        Set<String> storeIdSet = new HashSet<>();
        storeIdSet.add(storeId);
        unifyTaskService.getPerson(unifySubTaskForStoreData.getAddProcessList(), taskId, personList, storeIdSet, enterpriseId, taskParentDO.getCreateUserId(), taskParentDO.getTaskType(), false,null);

        //处理人门店映射关系
        Map<String, List<TaskMappingDO>> collectMap = personList.stream()
                .filter(f -> UnifyNodeEnum.FIRST_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //抄送人门店映射关系
        Map<String, List<TaskMappingDO>> ccPersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.CC.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //审核人,门店映射
        Map<String, List<TaskMappingDO>> auditPersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.SECOND_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //复审人,门店映射
        Map<String, List<TaskMappingDO>> recheckPersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.THIRD_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //三级,门店映射
        Map<String, List<TaskMappingDO>> thirdApprovePersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.FOUR_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //四级,门店映射
        Map<String, List<TaskMappingDO>> fourApprovePersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.FIVE_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        //五级审批,门店映射
        Map<String, List<TaskMappingDO>> fiveApprovePersonMap = personList.stream()
                .filter(f -> UnifyNodeEnum.SIX_NODE.getCode().equals(f.getNode()))
                .collect(Collectors.groupingBy(TaskMappingDO::getType));
        Long createTime = System.currentTimeMillis();

        Set<String> userSet = new HashSet<>();
        if(collectMap.get(storeId) != null){
            userSet = collectMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        Set<String> auditUserSet = new HashSet<>();
        Set<String> recheckUserSet = new HashSet<>();
        //三级审批人
        Set<String> thirdApproveSet = new HashSet<>();
        //四级审批人
        Set<String> fourApproveSet = new HashSet<>();
        //五级审批人
        Set<String> fiveApproveSet = new HashSet<>();
        //抄送人
        Set<String> ccUserSet = new HashSet<>();
        if (auditPersonMap.get(storeId) != null) {
            auditUserSet = auditPersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        if (recheckPersonMap.get(storeId) != null) {
            recheckUserSet = recheckPersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        if (recheckPersonMap.get(storeId) != null) {
            recheckUserSet = recheckPersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        if (thirdApprovePersonMap.get(storeId) != null) {
            thirdApproveSet = thirdApprovePersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        if (fourApprovePersonMap.get(storeId) != null) {
            fourApproveSet = fourApprovePersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        if (fiveApprovePersonMap.get(storeId) != null) {
            fiveApproveSet = fiveApprovePersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }

        if (ccPersonMap.get(storeId) != null) {
            ccUserSet = ccPersonMap.get(storeId).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
        }
        unifySubTaskForStoreData.setCcUserSet(ccUserSet);
        // 当前节点 需要 新增  移除  的人员
        Map<String, List<String>>  currentNodePersonChangeMap = this.getCurrentNodePersonChangeMap(enterpriseId, taskStoreDO,
                toListBySet(userSet), toListBySet(auditUserSet),
                toListBySet(recheckUserSet), toListBySet(thirdApproveSet),
                toListBySet(fourApproveSet), toListBySet(fiveApproveSet));
        List<String> newAddPersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_NEWADD);
        // 重新分配节点人员
        unifyTaskService.reallocateStoreTaskPersonByNodeNew(enterpriseId, taskStoreDO,
                taskParentDO, newAddPersonList, null, createTime, null, taskSubVO, true);
        // 更新task_store 表
        this.updateTaskReissuePerson(enterpriseId, taskStoreDO, userSet, auditUserSet,
                recheckUserSet, thirdApproveSet,
                fourApproveSet, fiveApproveSet,
                taskParentDO, newAddPersonList, unifySubTaskForStoreData.getCcUserSet());
        // 保存父任务处理人关系映射
        unifyTaskService.saveTaskParentUser(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getTaskType()
                , Stream.of(toListBySet(unifySubTaskForStoreData.getUserSet()),  toListBySet(unifySubTaskForStoreData.getAuditUserSet())
                                , toListBySet(unifySubTaskForStoreData.getRecheckUserSet()), toListBySet(unifySubTaskForStoreData.getThirdApproveSet())
                                , toListBySet(unifySubTaskForStoreData.getFourApproveSet()), toListBySet(unifySubTaskForStoreData.getFiveApproveSet()))
                        .flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public void taskReissue(String enterpriseId, Long taskId, String storeId, Long loopCount, List<TaskMappingDO> personList) {
        TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskId, storeId, loopCount);
        if(taskStoreDO == null){
            log.info("taskReissue #任务不存在eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, taskId, storeId, loopCount);
            return;
        }
        if(UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())){
            log.info("taskReissue #任务已完成，不在进行补发，eid:{},taskId:{},storeId:{}, loopCount:{}", enterpriseId, taskId, storeId, loopCount);
            return;
        }
        Set<String> handlerUserSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.FIRST_NODE);
        Set<String> auditUserSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.SECOND_NODE);
        Set<String> recheckUserSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.THIRD_NODE);
        Set<String> thirdApproveSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.FOUR_NODE);
        Set<String> fourApproveSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.FIVE_NODE);
        Set<String> fiveApproveSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.SIX_NODE);
        Set<String> ccUserSet = TaskMappingDO.getNodeUserList(personList, storeId, UnifyNodeEnum.CC);
        this.fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskStoreDO.getUnifyTaskId());
        //最新一条，防止空指针
        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null, taskStoreDO.getNodeNo());
        Long createTime = System.currentTimeMillis();
        // 当前节点 需要 新增  移除  的人员
        Map<String, List<String>>  currentNodePersonChangeMap = this.getCurrentNodePersonChangeMap(enterpriseId, taskStoreDO,
                toListBySet(handlerUserSet), toListBySet(auditUserSet),
                toListBySet(recheckUserSet), toListBySet(thirdApproveSet),
                toListBySet(fourApproveSet), toListBySet(fiveApproveSet));
        List<String> newAddPersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_NEWADD);
        // 重新分配节点人员
        unifyTaskService.reallocateStoreTaskPersonByNodeNew(enterpriseId, taskStoreDO,
                taskParentDO, newAddPersonList, null, createTime, null, taskSubVO, true);
        // 更新task_store 表
        this.updateTaskReissuePerson(enterpriseId, taskStoreDO, handlerUserSet, auditUserSet,
                recheckUserSet, thirdApproveSet,
                fourApproveSet, fiveApproveSet,
                taskParentDO, newAddPersonList, ccUserSet);
        // 保存父任务处理人关系映射
        unifyTaskService.saveTaskParentUser(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getTaskType()
                , Stream.of(toListBySet(handlerUserSet),  toListBySet(auditUserSet), toListBySet(recheckUserSet), toListBySet(thirdApproveSet)
                        , toListBySet(fourApproveSet), toListBySet(fiveApproveSet)).flatMap(List::stream).collect(Collectors.toList()));
    }

    private List<PersonDTO> changPersonDTO(List<PersonNodeNoDTO> personNodeNoDTOS){
        List<PersonDTO> list = new ArrayList<>();
        if(CollectionUtils.isEmpty(personNodeNoDTOS)){
            return list;
        }

        for(PersonNodeNoDTO personNodeNoDTO : personNodeNoDTOS){
            PersonDTO personDTO = new PersonDTO();
            personDTO.setUserId(personNodeNoDTO.getUserId());
            personDTO.setUserName(personNodeNoDTO.getUserName());
            list.add(personDTO);
        }
        return list;
    }

    private String toStringByUserIdList(List<String> userIdList){
        return toStringByUserIdSet(new HashSet<>(userIdList));
    }

    private String toStringByUserIdSet(Set<String> userIdSet){
        return Constants.COMMA + String.join(Constants.COMMA, userIdSet) + Constants.COMMA;
    }


    private List<String> toListBySet(Set<String> userIdSet){
        if(CollectionUtils.isEmpty(userIdSet)){
            new ArrayList<>();
        }
        return new ArrayList<>(userIdSet);
    }


    private List<String> toUserIdListByString(String userIdStr){
        List<String> userIdList = Arrays.asList(StringUtils.split(userIdStr, Constants.COMMA));
        return new ArrayList<String>(userIdList);
    }

    private void fillExtendAndCcInfoByList(String enterpriseId, List<TaskStoreDO> taskStoreDOList) {
        if(CollectionUtils.isNotEmpty(taskStoreDOList)){
            List<Long> taskStoreIdList = taskStoreDOList.stream().map(TaskStoreDO::getId).collect(Collectors.toList());
            List<TaskStoreDO> taskStoreHasExtendCcInfoList = taskStoreMapper.selectExtendAndCcInfoByTaskStoreIds(enterpriseId, taskStoreIdList);
            Map<Long, TaskStoreDO> taskStoreHasExtendCcInfoMap = taskStoreHasExtendCcInfoList.stream().collect(Collectors.toMap(TaskStoreDO::getId, o->o));
            for(TaskStoreDO storeDO : taskStoreDOList){
                TaskStoreDO tempStoreDO = taskStoreHasExtendCcInfoMap.get(storeDO.getId());
                if(tempStoreDO != null){
                    storeDO.setCcUserIds(tempStoreDO.getCcUserIds());
                    storeDO.setExtendInfo(tempStoreDO.getExtendInfo());
                }
            }
        }
    }

    private TbDisplayTableRecordDO getDisplayTableRecord(String enterpriseId, TaskStoreDO taskStoreDO, String dbName){
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return tbDisplayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
    }

    @Override
    public TaskStoreQuestionDataVO getLastTaskStoreQuestion(String enterpriseId, String storeId) {
        TaskStoreQuestionDataVO result = new TaskStoreQuestionDataVO();
        TaskStoreDO question = taskStoreMapper.selectLastStoreQuestion(enterpriseId, storeId);
        if (question == null) {
            return result;
        }
        TaskParentDO taskParent = taskParentMapper.selectParentTaskById(enterpriseId, question.getUnifyTaskId());
        //添加一条只查子任务id的sql
        List<Long> subTaskIds = taskSubMapper.getSubTaskIdListByUnifyTaskId(enterpriseId, question.getUnifyTaskId());
        if (CollectionUtils.isNotEmpty(subTaskIds)) {
            result.setSubTaskId(subTaskIds.get(0));
        }
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, question.getCreateUserId());
        result.setUnifyTaskId(taskParent.getId());
        result.setTaskStoreId(question.getId());
        result.setStoreId(storeId);
        result.setTaskInfo(taskParent.getTaskInfo());
        result.setUnifyTaskName(taskParent.getTaskName());
        result.setHandlerEndTime(question.getHandlerEndTime());
        result.setCreateUserName(createUserName);
        result.setSubStatus(question.getSubStatus());
        result.setCreateUserId(question.getCreateUserId());
        result.setNodeNo(question.getNodeNo());
        result.setLoopCount(question.getLoopCount());
        //筛选处理人列表
        if (TaskStatusEnum.COMPLETE.getCode().equals(question.getSubStatus())) {
            //先拿nodeNo 为1的子任务
            TaskSubVO handleSub = taskSubMapper.getCompleteSubTaskByReallocate(enterpriseId, taskParent.getId(), storeId, 1L, null, null,
                    DisplayConstant.ActionKeyConstant.PASS, UnifyNodeEnum.FIRST_NODE.getCode());
            if (handleSub != null) {
                EnterpriseUserDO handleUser = enterpriseUserDao.selectByUserId(enterpriseId, handleSub.getHandleUserId());
                result.setHandlerUserName(handleUser.getName());
            } else {
                //如果不存在nodeNo 为1的子任务，查询nodeOn为 endNode的子任务
                TaskSubVO endSub = taskSubMapper.getCompleteSubTaskByReallocate(enterpriseId, taskParent.getId(), storeId, 1L, null, null,
                        DisplayConstant.ActionKeyConstant.PASS, UnifyNodeEnum.END_NODE.getCode());
                if (endSub != null) {
                    EnterpriseUserDO handleUser = enterpriseUserDao.selectByUserId(enterpriseId, endSub.getHandleUserId());
                    result.setHandlerUserName(handleUser.getName());
                }
            }
        }
        if (StringUtils.isBlank(result.getHandlerUserName())) {
            List<UnifyPersonDTO> taskPer = selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(taskParent.getId()),
                    Collections.singletonList(storeId), 1L);
            List<UnifyPersonDTO> handleUsers = taskPer.stream().filter(per -> UnifyNodeEnum.FIRST_NODE.getCode().equals(per.getNode()))
                    .collect(Collectors.toList());
            String handleUserStr = String.join(",", handleUsers.stream().map(UnifyPersonDTO::getUserName).collect(Collectors.toList()));
            result.setHandlerUserName(handleUserStr);
        }

        return result;
    }

    @Override
    public List<TaskStoreMetaDataVO> getStoreMetaTableData(String enterpriseId, String storeId, List<TbMetaTableDTO> query, CurrentUser user) {
        List<TaskStoreMetaDataVO> result = new ArrayList<>();
        List<Future<TaskStoreMetaDataVO>> futures = new ArrayList<>();
        for (TbMetaTableDTO metaTable : query) {
            futures.add(EXECUTOR_SERVICE.submit(() -> selectMetaTableData(enterpriseId, storeId, metaTable.getMetaTableId(),
                    metaTable.getColumns(), user.getDbName())));
        }
        for (Future<TaskStoreMetaDataVO> future: futures) {
            try {
                result.add(future.get());
            } catch (Exception e) {
                log.error("获取最新检查表数据失败, ", e);
            }
        }
        return result;
    }

    public TaskStoreMetaDataVO selectMetaTableData(String eid, String storeId, Long metaTableId, List<TaskStoreMetaTableColDTO> cols, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        TaskStoreMetaDataVO tableData = tbDataTableMapper.selectLastMetaTableData(eid, storeId, metaTableId);
        if (tableData == null) {
            return initMetaTableInfo(eid, metaTableId, cols);
        }
        List<TaskStoreMetaTableColVO> colList = new ArrayList<>();
        if (!TableTypeUtil.isUserDefinedTable(tableData.getTableProperty(),tableData.getTableType())) {
            if (CollectionUtils.isNotEmpty(cols)) {
                List<Long> colIds = cols.stream().map(TaskStoreMetaTableColDTO::getMetaTableColumnId).collect(Collectors.toList());
                colList = tbDataStaTableColumnMapper.selectStaColumnData(eid, tableData.getDataTableId(), null, colIds);
            } else {
                colList = tbDataStaTableColumnMapper.selectStaColumnData(eid, tableData.getDataTableId(), metaTableId, null);
            }
        }
        if (UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(tableData.getTableType())) {
            if (CollectionUtils.isNotEmpty(cols)) {
                List<Long> colIds = cols.stream().map(TaskStoreMetaTableColDTO::getMetaTableColumnId).collect(Collectors.toList());
                colList = tbDisplayTableDataColumnMapper.selectDisColumnData(eid, tableData.getDataTableId(), null, colIds);
            } else {
                colList = tbDisplayTableDataColumnMapper.selectDisColumnData(eid, tableData.getDataTableId(), metaTableId, null);
            }
        }
        if (TableTypeUtil.isUserDefinedTable(tableData.getTableProperty(),tableData.getTableType())) {
            List<TbMetaDefTableColumnDO> defColList = tbMetaDefTableColumnMapper.selectByTableId(eid, metaTableId);
            Map<Long, String> colTypeMap = defColList.stream()
                    .collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, TbMetaDefTableColumnDO::getFormat));
            //组装
            if (CollectionUtils.isNotEmpty(cols)) {
                List<Long> colIds = cols.stream().map(TaskStoreMetaTableColDTO::getMetaTableColumnId).collect(Collectors.toList());
                colList = tbDataDefTableColumnMapper.selectDefColumnData(eid, tableData.getDataTableId(), null, colIds);
            } else {
                colList = tbDataDefTableColumnMapper.selectDefColumnData(eid, tableData.getDataTableId(), metaTableId, null);
            }
            colList.forEach(col -> {
                col.setFormat(colTypeMap.get(col.getMetaTableColumnId()));
            });
        }
        tableData.setColumns(colList);
        return tableData;
    }

    private TaskStoreMetaDataVO initMetaTableInfo(String eid, Long metaTableId, List<TaskStoreMetaTableColDTO> cols) {
        TaskStoreMetaDataVO tableData = new TaskStoreMetaDataVO();
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(eid, metaTableId);
        if (tableDO == null) {
            return tableData;
        }
        tableData.setMateTableId(metaTableId);
        tableData.setTableName(tableDO.getTableName());
        tableData.setTableType(tableDO.getTableType());
        List<TaskStoreMetaTableColVO> columns = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cols)) {
            if (TableTypeUtil.isUserDefinedTable(tableDO.getTableProperty(),tableData.getTableType())) {
                List<TbMetaDefTableColumnDO> defColList = tbMetaDefTableColumnMapper.selectByTableId(eid, metaTableId);
                //组装 id -> data
                Map<Long, TbMetaDefTableColumnDO> colTypeMap = defColList.stream()
                        .collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, data -> data));
                cols.forEach(col -> {
                    TaskStoreMetaTableColVO vo = new TaskStoreMetaTableColVO();
                    vo.setMetaTableColumnId(col.getMetaTableColumnId());
                    vo.setMetaTableColumnName(colTypeMap.get(col.getMetaTableColumnId()).getColumnName());
                    vo.setFormat(colTypeMap.get(col.getMetaTableColumnId()).getFormat());
                    columns.add(vo);
                });
            }

        } else {
            if (TableTypeUtil.isUserDefinedTable(tableDO.getTableProperty(),tableData.getTableType())) {
                List<TbMetaDefTableColumnDO> defColList = tbMetaDefTableColumnMapper.selectByTableId(eid, metaTableId);
                defColList.forEach(col -> {
                    TaskStoreMetaTableColVO vo = new TaskStoreMetaTableColVO();
                    vo.setMetaTableColumnId(col.getId());
                    vo.setMetaTableColumnName(col.getColumnName());
                    vo.setFormat(col.getFormat());
                    columns.add(vo);
                });
            }
        }
        tableData.setColumns(columns);
        return tableData;
    }


    private void updateTaskReissuePerson(String enterpriseId, TaskStoreDO taskStoreDO, Set<String> handleUserList, Set<String> approveUserList,
                                         Set<String> recheckUserList, Set<String> thirdApproveUserList,
                                         Set<String> fourApproveUserList, Set<String> fiveApproveUserList,
                                         TaskParentDO taskParentDO, List<String> newAddPersonList, Set<String> ccUserList) {

        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if (extendInfoJsonObj == null) {
            log.info("任务补发相应节点人员失败 门店任务节点人员还未生成 :{},企业id:{}", taskStoreDO.getId(), enterpriseId);
            return;
        }
        // 原来节点不为空
        String handleUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        String auditUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SECOND_NODE.getCode());
        String recheckUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.THIRD_NODE.getCode());
        String thirdApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FOUR_NODE.getCode());
        String fourApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIVE_NODE.getCode());
        String fiveApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SIX_NODE.getCode());
        String ccUserIdStr = taskStoreDO.getCcUserIds();
        //各个节点人员
        if (CollectionUtils.isNotEmpty(handleUserList) && StringUtils.isNotBlank(handleUserIdStr)) {
            List<String> haveHandleUserList = toUserIdListByString(handleUserIdStr);
            handleUserList.addAll(haveHandleUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdSet(handleUserList));
        }
        if (CollectionUtils.isNotEmpty(approveUserList) && StringUtils.isNotBlank(auditUserIdStr)) {
            List<String> haveAuditUserList = toUserIdListByString(auditUserIdStr);
            approveUserList.addAll(haveAuditUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdSet(approveUserList));
        }
        if (CollectionUtils.isNotEmpty(recheckUserList) && StringUtils.isNotBlank(recheckUserIdStr)) {
            List<String> haveRecheckUserList = toUserIdListByString(recheckUserIdStr);
            recheckUserList.addAll(haveRecheckUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdSet(recheckUserList));
        }
        if (CollectionUtils.isNotEmpty(thirdApproveUserList) && StringUtils.isNotBlank(thirdApproveUserIdStr)) {
            List<String> haveThirdApproveUserList = toUserIdListByString(thirdApproveUserIdStr);
            thirdApproveUserList.addAll(haveThirdApproveUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdSet(thirdApproveUserList));
        }
        if (CollectionUtils.isNotEmpty(fourApproveUserList) && StringUtils.isNotBlank(fourApproveUserIdStr)) {
            List<String> haveFourApproveUserList = toUserIdListByString(fourApproveUserIdStr);
            fourApproveUserList.addAll(haveFourApproveUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdSet(fourApproveUserList));
        }
        if (CollectionUtils.isNotEmpty(fiveApproveUserList) && StringUtils.isNotBlank(fiveApproveUserIdStr)) {
            List<String> haveFiveApproveUserList = toUserIdListByString(fiveApproveUserIdStr);
            fiveApproveUserList.addAll(haveFiveApproveUserList);
            extendInfoJsonObj.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdSet(fiveApproveUserList));
        }
        if(CollectionUtils.isNotEmpty(ccUserList)){
            if(StringUtils.isNotBlank(ccUserIdStr)){
                List<String> haveCcUserList = toUserIdListByString(ccUserIdStr);
                ccUserList.addAll(haveCcUserList);
            }
            ccUserIdStr = toStringByUserIdSet(ccUserList);
            List<UnifyTaskParentCcUserDO> addCCUserList = new ArrayList<>();
            for (String s : ccUserList) {
                UnifyTaskParentCcUserDO unifyTaskParentCcUser = new UnifyTaskParentCcUserDO(taskParentDO.getId(), taskParentDO.getTaskName(),
                        taskParentDO.getTaskType(), s, UnifyStatus.ONGOING.getCode(), taskParentDO.getBeginTime(), taskParentDO.getEndTime());
                addCCUserList.add(unifyTaskParentCcUser);
            }
            unifyTaskParentCcUserDao.batchInsertOrUpdate(enterpriseId, addCCUserList);
        }

        String extendInfoJsonStr = JSON.toJSONString(extendInfoJsonObj);
        log.info("任务补发后节点taskStoreId:{},extendInfoJsonStr:{}", taskStoreDO.getId(), extendInfoJsonStr);
        taskStoreMapper.updateExtendAndCcInfoByTaskStoreId(enterpriseId, taskStoreDO.getId(), extendInfoJsonStr, ccUserIdStr, null);
    }

    @Override
    public Map<Long, List<String>> getTaskStoreUserIdMap(List<TaskStoreDO> taskStoreDOList) {
        // 收集所有用户id;
        Map<Long, List<String>> userIdMap = new HashMap<>();
        taskStoreDOList.forEach(taskStoreDO -> {
            userIdMap.put(taskStoreDO.getId(), getALLNodeUserIdListBySingle(taskStoreDO));
        });
        return userIdMap;
    }

    @Override
    public List<TaskStoreDO> selectByUnifyTaskId(String enterpriseId, Long parentTaskId) {
        return taskStoreMapper.selectByUnifyTaskId(enterpriseId,parentTaskId);
    }

    @Override
    public CombineTaskStoreDTO combineTaskList(String enterpriseId, TaskStoreLoopQuery query) {
        String currentUserId = UserHolder.getUser().getUserId();
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, query.getUnifyTaskId());
        if(taskParentDO == null){
            return null;
        }
        if (query.getLoopCount() == null) {
            query.setLoopCount(1L);
        }
        CombineTaskStoreDTO combineTaskStoreDTO = new CombineTaskStoreDTO();
        PageVO<TaskStoreDO> taskStorePage = elasticSearchService.getTaskStoreList(enterpriseId, query);
        List<TaskStoreDO> taskStoreList = taskStorePage.getList();
        CombineTaskStoreDTO.TaskInfo taskInfo = new CombineTaskStoreDTO.TaskInfo();
        taskInfo.setTaskName(taskParentDO.getTaskName());
        taskInfo.setTaskType(taskParentDO.getTaskType());
        taskInfo.setCreateUserId(taskParentDO.getCreateUserId());
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, taskParentDO.getCreateUserId());
        taskInfo.setCreateUserName(createUserName);
        combineTaskStoreDTO.setTaskInfo(taskInfo);
        if (CollectionUtils.isEmpty(taskStoreList)) {
            combineTaskStoreDTO.setPageInfo(new PageInfo());
            return combineTaskStoreDTO;
        }
        taskInfo.setHandlerEndTime(taskStoreList.get(0).getHandlerEndTime());
        taskInfo.setSubEndTime(taskStoreList.get(0).getSubEndTime());
        List<String> storeIdList = taskStoreList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getByStoreIds(enterpriseId, storeIdList);
        Map<String, StoreDO> storeIdDOMap =
                storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity(), (a, b) -> a));

        List<TaskSubDO> taskSubDOList = taskSubMapper.listCombineStore(enterpriseId, query.getUnifyTaskId(), storeIdList, query.getLoopCount(), currentUserId, query.getNodeStr());
        Map<String, Long> taskSubDOMap = ListUtils.emptyIfNull(taskSubDOList).stream().collect(Collectors.toMap(TaskSubDO::getStoreId, TaskSubDO::getId, (o1, o2) -> o1 > o2 ? o1 : o2));

        List<TaskSubDO> endTaskSubDOList = taskSubMapper.listCombineStore(enterpriseId, query.getUnifyTaskId(), storeIdList, query.getLoopCount(), currentUserId, UnifyNodeEnum.END_NODE.getCode());
        Map<String, Long> endTaskSubMap = ListUtils.emptyIfNull(endTaskSubDOList).stream().collect(Collectors.toMap(TaskSubDO::getStoreId, TaskSubDO::getId));

        Map<String, TbDisplayTableRecordDO> tbDisplayTableRecordDOMap = Maps.newHashMap();
        if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
            List<TbDisplayTableRecordDO> tbDisplayTableRecordDOList = tbDisplayTableRecordMapper.listByUnifyTaskId(enterpriseId, query.getUnifyTaskId(), query.getLoopCount(), storeIdList);
            tbDisplayTableRecordDOMap = tbDisplayTableRecordDOList.stream().collect(Collectors.toMap(TbDisplayTableRecordDO::getStoreId, Function.identity(), (a, b) -> a));
        }

        List<CombineTaskStoreDTO.TaskStoreInfo> resultList = new ArrayList<>();
        PageInfo<CombineTaskStoreDTO.TaskStoreInfo> pageInfo = new PageInfo(taskStoreList);
        for (TaskStoreDO taskStoreDO : taskStoreList) {
            CombineTaskStoreDTO.TaskStoreInfo taskStoreInfo = new CombineTaskStoreDTO.TaskStoreInfo();
            taskStoreInfo.setTaskStoreId(taskStoreDO.getId());
            taskStoreInfo.setUnifyTaskId(taskStoreDO.getUnifyTaskId());
            taskStoreInfo.setLoopCount(taskStoreDO.getLoopCount());
            taskStoreInfo.setSubTaskId(0L);
            taskStoreInfo.setStoreId(taskStoreDO.getStoreId());
            taskStoreInfo.setStoreName(taskStoreDO.getStoreName());
            taskStoreInfo.setNodeNo(taskStoreDO.getNodeNo());
            taskStoreInfo.setHandleTime(taskStoreDO.getHandleTime());
            taskStoreInfo.setSubEndTime(taskStoreDO.getSubEndTime());
            taskStoreInfo.setHandlerEndTime(taskStoreDO.getHandlerEndTime());
            StoreDO storeDO = storeIdDOMap.get(taskStoreDO.getStoreId());
            if(storeDO != null){
                taskStoreInfo.setAvatar(storeDO.getAvatar());
                taskStoreInfo.setStoreAddress(storeDO.getStoreAddress());
                taskStoreInfo.setStoreStatus(storeDO.getStoreStatus());
            }
            if(taskSubDOMap.get(taskStoreDO.getStoreId()) != null){
                taskStoreInfo.setSubTaskId(taskSubDOMap.get(taskStoreDO.getStoreId()));
            }else if(endTaskSubMap.get(taskStoreDO.getStoreId()) != null){
                taskStoreInfo.setSubTaskId(endTaskSubMap.get(taskStoreDO.getStoreId()));
            }
            if(TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
                TbDisplayTableRecordDO tbDisplayTableRecordDO = tbDisplayTableRecordDOMap.get(taskStoreDO.getStoreId());
                if(tbDisplayTableRecordDO.getFirstHandlerTime() != null && tbDisplayTableRecordDO.getHandlerEndTime() != null){
                    taskStoreInfo.setHandlerOverdue(tbDisplayTableRecordDO.getFirstHandlerTime().after(tbDisplayTableRecordDO.getHandlerEndTime()));
                }else if(tbDisplayTableRecordDO.getHandlerEndTime() != null){
                    taskStoreInfo.setHandlerOverdue(new Date().after(tbDisplayTableRecordDO.getHandlerEndTime()));
                }else {
                    taskStoreInfo.setHandlerOverdue(new Date().after(tbDisplayTableRecordDO.getSubEndTime()));
                }
            }
            resultList.add(taskStoreInfo);
        }
        pageInfo.setList(resultList);
        pageInfo.setTotal(taskStorePage.getTotal());
        combineTaskStoreDTO.setPageInfo(pageInfo);
        return combineTaskStoreDTO;
    }

    @Override
    public String getTaskHandlerUserId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        TaskStoreDO taskStoreDetail = taskStoreDao.getTaskStoreDetail(enterpriseId, unifyTaskId, storeId, loopCount);
        if(Objects.isNull(taskStoreDetail)){
            return null;
        }
        return taskStoreDetail.getHandlerUserId();
    }

    // 散开门店任务节点人员信息
    private List<String> getALLNodeUserIdListBySingle(TaskStoreDO taskStoreDO) {
        List<String> aLLNodeUserInfoList = Lists.newArrayList();
        Map<String, List<String>> nodePersonMap = getNodePersonByTaskStore(taskStoreDO);
        List<String> handleUserIdList = nodePersonMap.get(UnifyNodeEnum.FIRST_NODE.getCode());
        List<String> auditUserIdList = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
        List<String> recheckUserIdList = nodePersonMap.get(UnifyNodeEnum.THIRD_NODE.getCode());
        List<String> thirdApproveList = nodePersonMap.get(UnifyNodeEnum.FOUR_NODE.getCode());
        List<String> fourApproveList = nodePersonMap.get(UnifyNodeEnum.FIVE_NODE.getCode());
        List<String> fiveApproveList = nodePersonMap.get(UnifyNodeEnum.SIX_NODE.getCode());
        if (CollectionUtils.isNotEmpty(handleUserIdList)) {
            aLLNodeUserInfoList.addAll(handleUserIdList);
        }
        if (CollectionUtils.isNotEmpty(auditUserIdList)) {
            aLLNodeUserInfoList.addAll(auditUserIdList);
        }
        if (CollectionUtils.isNotEmpty(recheckUserIdList)) {
            aLLNodeUserInfoList.addAll(recheckUserIdList);
        }
        if (CollectionUtils.isNotEmpty(thirdApproveList)) {
            aLLNodeUserInfoList.addAll(thirdApproveList);
        }
        if (CollectionUtils.isNotEmpty(fourApproveList)) {
            aLLNodeUserInfoList.addAll(fourApproveList);
        }
        if (CollectionUtils.isNotEmpty(fiveApproveList)) {
            aLLNodeUserInfoList.addAll(fiveApproveList);
        }
        return aLLNodeUserInfoList;
    }

}
