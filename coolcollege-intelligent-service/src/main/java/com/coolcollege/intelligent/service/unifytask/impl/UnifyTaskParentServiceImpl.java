package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.rpc.common.utils.BeanUtils;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMqInformConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskParentDao;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentItemDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.PersonSubTaskDataQueueDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolstore.base.enums.BailiInformNodeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 父任务
 * @author zhangnan
 * @date 2022-04-14 16:39
 */
@Slf4j
@Service(value = "unifyTaskParentService")
public class UnifyTaskParentServiceImpl implements UnifyTaskParentService {

    private static final Pattern PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SysRoleDao sysRoleDao;

    @Resource
    private QuestionParentInfoDao questionParentInfoDao;

    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;

    @Resource
    private TaskParentMapper taskParentMapper;
    @Lazy
    @Resource
    private UnifyTaskService unifyTaskService;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private RedisConstantUtil redisConstantUtil;

    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;

    @Resource
    EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseMqInformConfigMapper enterpriseMqInformConfigMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;
    @Override
    public TaskParentDO insertByPerson(String enterpriseId, CurrentUser user, BuildByPersonRequest request) {
        // 没有选人/职位
        if(CollectionUtils.isEmpty(request.getProcess())){
            throw new ServiceException(ErrorCodeEnum.EMPTY_REPORT_PARAM);
        }
        // 构建父任务数据
        TaskParentDO taskParentDO = new TaskParentDO();
        taskParentDO.setTaskName(request.getTaskName());
        taskParentDO.setTaskType(request.getTaskType());
        taskParentDO.setBeginTime(request.getBeginTime());
        taskParentDO.setEndTime(request.getEndTime());
        taskParentDO.setCreateUserId(user.getUserId());
        taskParentDO.setCreateTime(System.currentTimeMillis());
        taskParentDO.setTaskDesc(request.getTaskDesc());
        taskParentDO.setParentStatus(UnifyStatus.ONGOING.getCode());
        taskParentDO.setCreateUserName(user.getName());
        taskParentDO.setCreateUserId(user.getUserId());
        taskParentDO.setRunRule(Constants.ONCE);
        taskParentDO.setTaskInfo(request.getTaskInfo());
        taskParentDO.setLoopCount(Constants.LONG_ONE);
        taskParentDO.setNodeInfo(JSONObject.toJSONString(request.getProcess()));
        // 新增父任务
        taskParentDao.insertTaskParent(enterpriseId, taskParentDO);
        if(request.getBeginTime() < System.currentTimeMillis()) {
            // 拆分父任务
            this.splitTaskForPerson(enterpriseId, user.getDbName(), request.getProcess(), taskParentDO);
        }else {
            // 根据有效期开始时间，定时拆分
            String scheduleId = this.setSchedulerForOnce(enterpriseId, taskParentDO.getId(), new Date(taskParentDO.getBeginTime()), Constants.ZERO,Constants.ZERO);
            taskParentDO.setScheduleId(scheduleId);
            taskParentDao.updateScheduleId(enterpriseId, taskParentDO);
        }
        return taskParentDO;
    }

    /**
     * 拆分父任务-按人任务
     * @param enterpriseId
     * @param dbName
     * @param processList
     * @param taskParentDO
     */
    @Override
    public void splitTaskForPerson(String enterpriseId, String dbName, List<TaskProcessDTO> processList, TaskParentDO taskParentDO) {
        // 按人任务只有第一个节点有人员/职位,Set去重
        Set<String> personList = Sets.newHashSet();
        List<Long> positionList = Lists.newArrayList();
        List<String> groupIdList = Lists.newArrayList();
        List<String> regionIdList = Lists.newArrayList();
        for (TaskProcessDTO process : processList) {
            if(!process.getNodeNo().equals(UnifyNodeEnum.FIRST_NODE.getCode())) {
                continue;
            }
            // 判断选人/职位数量
            if(CollectionUtils.isNotEmpty(process.getUser()) && process.getUser().size() > Constants.PERSON_LIMIT){
                throw new ServiceException(ErrorCodeEnum.PERSON_LIMIT, Constants.PERSON_LIMIT);
            }
            personList.addAll(process.getUser().stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                    .map(GeneralDTO::getValue).collect(Collectors.toList()));
            positionList.addAll(process.getUser().stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                    .map(GeneralDTO::getValue).map(Long::parseLong).collect(Collectors.toList()));
            groupIdList = process.getUser().stream().filter(x -> UnifyTaskConstant.PersonType.USER_GROUP.equals(x.getType()))
                    .map(GeneralDTO::getValue).collect(Collectors.toList());
            regionIdList = process.getUser().stream().filter(x -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(x.getType()))
                    .map(GeneralDTO::getValue).collect(Collectors.toList());
        }
        // 根据职位获取人员
        personList.addAll(enterpriseUserRoleDao.selectUserIdsByRoleIdList(enterpriseId, positionList));

        if (CollectionUtils.isNotEmpty(groupIdList)){
            List<String> userIds = enterpriseUserGroupMappingDao.getUserIdsByGroupIdList(enterpriseId, groupIdList);
            //添加分组的人员
            if(CollectionUtils.isNotEmpty(userIds)){
                personList.addAll(userIds);
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<String> userIdList = new ArrayList<>();
            try {
                boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
                if (historyEnterprise) {
                    userIdList = enterpriseUserDao.listUserIdByDepartmentIdList(enterpriseId, regionIdList);
                }else {
                    userIdList = enterpriseUserDao.getUserIdsByRegionIdList(enterpriseId, regionIdList);
                }
            }catch (Exception e){
                log.error("老企业数据异常",e);
            }
            if (CollectionUtils.isNotEmpty(userIdList)){
                personList.addAll(userIdList);
            }
        }

        // 按人发送mq消息创建子任务，按人任务
        for (String handleUserId : personList) {
            PersonSubTaskDataQueueDTO message = new PersonSubTaskDataQueueDTO();
            message.setEnterpriseId(enterpriseId);
            message.setDbName(dbName);
            message.setUserId(handleUserId);
            message.setTaskParent(taskParentDO);
            simpleMessageService.send(JSONObject.toJSONString(message), RocketMqTagEnum.PERSON_SUB_TASK_DATA_QUEUE);
        }
    }

    @Override
    public PageInfo<GetTaskByPersonVO> getTaskParentByPerson(String enterpriseId, GetTaskByPersonRequest request) {
        PageInfo<TaskParentDO> taskParentDOPageInfo = taskParentDao.selectTaskParentForPerson(enterpriseId, request);
        PageInfo<GetTaskByPersonVO> voPageInfo = new PageInfo<>();
        voPageInfo.setPages(taskParentDOPageInfo.getPages());
        voPageInfo.setTotal(taskParentDOPageInfo.getTotal());
        voPageInfo.setPageNum(taskParentDOPageInfo.getPageNum());
        voPageInfo.setPageSize(taskParentDOPageInfo.getPageSize());
        if(CollectionUtils.isEmpty(taskParentDOPageInfo.getList())) {
            voPageInfo.setList(Lists.newArrayList());
            return voPageInfo;
        }
        List<GetTaskByPersonVO> taskForPersonVOList = Lists.newArrayList();
        for (TaskParentDO taskParentDO : taskParentDOPageInfo.getList()) {
            taskForPersonVOList.add(this.buildGetTaskByPersonVO(enterpriseId, taskParentDO));
        }
        voPageInfo.setList(taskForPersonVOList);
        return voPageInfo;
    }

    private GetTaskByPersonVO buildGetTaskByPersonVO(String enterpriseId, TaskParentDO taskParentDO) {
        GetTaskByPersonVO taskForPersonVO = new GetTaskByPersonVO();
        taskForPersonVO.setId(taskParentDO.getId());
        taskForPersonVO.setTaskName(taskParentDO.getTaskName());
        taskForPersonVO.setTaskDesc(taskParentDO.getTaskDesc());
        taskForPersonVO.setBeginTime(taskParentDO.getBeginTime());
        taskForPersonVO.setEndTime(taskParentDO.getEndTime());
        taskForPersonVO.setCreateUserId(taskParentDO.getCreateUserId());
        taskForPersonVO.setCreateUserName(taskParentDO.getCreateUserName());
        taskForPersonVO.setTaskInfo(taskParentDO.getTaskInfo());
        taskForPersonVO.setCreateTime(taskParentDO.getCreateTime());
        taskForPersonVO.setParentStatus(taskParentDO.getParentStatus());
        taskForPersonVO.setIsOverdue(Boolean.FALSE);
        if(UnifyStatus.ONGOING.getCode().equals(taskParentDO.getParentStatus()) && taskParentDO.getEndTime() < System.currentTimeMillis()) {
            taskForPersonVO.setIsOverdue(Boolean.TRUE);
        }
        List<TaskProcessVO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessVO.class);
        taskForPersonVO.setHandlerProcess(this.dealTaskProcess(enterpriseId, process));
        return taskForPersonVO;
    }

    @Override
    public GetTaskByPersonVO getTaskParentById(String enterpriseId, Long unifyTaskId) {
        TaskParentDO taskParentDo = taskParentDao.selectById(enterpriseId, unifyTaskId);
        if(Objects.isNull(taskParentDo)) {
            throw new ServiceException(ErrorCodeEnum.PATROL_STORE_PLAN_DELETED);
        }
        return this.buildGetTaskByPersonVO(enterpriseId, taskParentDo);
    }

    private List<TaskProcessVO> dealTaskProcess(String enterpriseId, List<TaskProcessVO> taskProcessList) {
        List<String> positionIdList = Lists.newArrayList();
        List<String> userIdList = Lists.newArrayList();
        List<String> groupIdList = Lists.newArrayList();
        List<String> regionIdList = Lists.newArrayList();
        taskProcessList.forEach(item -> {
            List<GeneralDTO> user = item.getUser();
            user.forEach(u -> {
                switch (u.getType()) {
                    case UnifyTaskConstant.PersonType.POSITION:
                        positionIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.PERSON:
                        userIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.USER_GROUP:
                        groupIdList.add(u.getValue());
                        break;
                    case UnifyTaskConstant.PersonType.ORGANIZATION:
                        regionIdList.add(u.getValue());
                        break;
                    default:
                        break;
                }
            });
        });
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, String> useMap = ListUtils.emptyIfNull(enterpriseUserDOList)
                .stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName, (a, b) -> a));
        //将positionId的类型转换成long
        List<Long> longPositionList = ListUtils.emptyIfNull(positionIdList)
                .stream()
                .map(data -> {
                    Matcher isNum = PATTERN.matcher(data);
                    if (isNum.matches() && data.length() != 32) {
                        return Long.valueOf(data);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SysRoleDO> roleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(longPositionList)) {
            roleList = sysRoleDao.selectRoleByRoleIds(enterpriseId, longPositionList);
        }
        Map<String, String> positionMap = roleList.stream()
                .filter(a -> a.getId() != null && a.getRoleName() != null)
                .collect(Collectors.toMap(data -> String.valueOf(data.getId()), SysRoleDO::getRoleName, (a, b) -> a));

        List<EnterpriseUserGroupDO> userGroupDOList = enterpriseUserGroupDao.listByGroupIdList(enterpriseId, groupIdList);
        Map<String, String> groupNameMap = userGroupDOList.stream()
                .filter(a -> a.getId() != null && a.getGroupName() != null)
                .collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupId, EnterpriseUserGroupDO::getGroupName, (a, b) -> a));

        Map<String, String> regionNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            //查看是否是老企业
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
            if (historyEnterprise) {
                List<Long> collect = regionIdList.stream().map(s -> Long.parseLong(s.trim()))
                        .collect(Collectors.toList());
                List<DeptNode> depListByDepName = sysDepartmentMapper.getDepListByDepName(enterpriseId, null, collect);
                regionNameMap = depListByDepName.stream().collect(Collectors.toMap(DeptNode::getId, DeptNode::getDepartmentName));
            } else {
                List<RegionDO> regionDOList = regionMapper.getRegionByRegionIds(enterpriseId, regionIdList);
                regionNameMap = regionDOList.stream()
                        .filter(a -> a.getId() != null && a.getName() != null)
                        .collect(Collectors.toMap(data -> String.valueOf(data.getId()), RegionDO::getName, (a, b) -> a));
            }
        }
        //用户、岗位有效性校验
        Map<String, String> finalRegionNameMap = regionNameMap;
        taskProcessList.forEach(item -> {
            List<GeneralDTO> userResult = new ArrayList<>();
            List<GeneralDTO> user = item.getUser();
            user.forEach(u -> {
                switch (u.getType()) {
                    case UnifyTaskConstant.PersonType.POSITION:
                        String name = positionMap.get(u.getValue());
                        u.setName(name);
                        u.setValid(!StringUtils.isEmpty(name));
                        break;
                    case UnifyTaskConstant.PersonType.PERSON:
                        String userName = useMap.get(u.getValue());
                        u.setName(userName);
                        u.setValid(!StringUtils.isEmpty(userName));
                        break;
                    case UnifyTaskConstant.PersonType.USER_GROUP:
                        u.setName(groupNameMap.get(u.getValue()));
                        u.setValid(!StringUtils.isEmpty(u.getName()));
                        break;
                    case UnifyTaskConstant.PersonType.ORGANIZATION:
                        u.setName(finalRegionNameMap.get(u.getValue()));
                        u.setValid(!StringUtils.isEmpty(u.getName()));
                        break;
                    default:
                        break;
                }
                if (u.getValid() != null && u.getValid()) {
                    userResult.add(u);
                }
            });
            item.setUser(userResult);
        });
        return taskProcessList;
    }

    @Override
    public void updateParentStatusByTaskId(String enterpriseId, String parentStatus, Long taskId) {
        taskParentDao.updateParentStatusByTaskId(enterpriseId, parentStatus, taskId);
    }

    @Override
    public String setSchedulerForOnce(String enterpriseId, Long taskId, Date beginTime, Integer offset,int isOperateOverdue) {
        Date nowDate = new Date();
        if(beginTime.before(nowDate)){
            beginTime = nowDate;
        }

        List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
        jobs.add(ScheduleCallBackUtil.getCallBack(schedulerCallbackTaskUrl + "/v2/" + enterpriseId + "/communication/unity_task_scheduler/" + taskId + "/" + isOperateOverdue, ScheduleCallBackEnum.api.getValue()));

        Date afterTenSecond = DateUtil.offset(beginTime, DateField.SECOND, offset);
        String startTime = DateUtils.convertTimeToString(afterTenSecond.getTime(), "yyyy-MM-dd HH:mm:ss");

        ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime, jobs);
        fixedRequest.setTimes(1);
        String requestString = JSON.toJSONString(fixedRequest);

        if (log.isInfoEnabled()) {
            log.info("单次任务延迟回调，开始调用定时器enterpriseId={},taskId={},开始调用参数={}", enterpriseId, taskId, requestString);
        }
        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", requestString, ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);
        if (log.isInfoEnabled()) {
            log.info("单次任务延迟回调，结束调用定时器enterpriseId={},taskId={},返回结果={}", enterpriseId, taskId, jsonObjectSchedule);
        }
        String scheduleId = null;
        if (ObjectUtil.isNotEmpty(jsonObjectSchedule)) {
            scheduleId = jsonObjectSchedule.getString("scheduler_id");
        }
        return scheduleId;
    }

    @Override
    public TaskParentDO getTaskParentDOById(String enterpriseId, Long taskId) {
        return taskParentDao.selectById(enterpriseId, taskId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TaskParentDO insertQuestionOrder(String enterpriseId, BuildQuestionRequest buildQuestionRequest, String userId,
                                            List<UnifyTaskParentItemDO> unifyTaskParentItemDOList) {

        QuestionTaskInfoDTO questionTaskInfoDTO = new QuestionTaskInfoDTO();
        questionTaskInfoDTO.setContentLearnFirst(false);
        TaskParentDO parentDO = TaskParentDO.builder()
                .taskName(buildQuestionRequest.getTaskName())
                .taskType(TaskTypeEnum.QUESTION_ORDER.getCode())
                .beginTime(null)
                .endTime(null)
                .createUserId(userId)
                .createTime(System.currentTimeMillis())
                .taskDesc(null)
                .parentStatus(UnifyStatus.ONGOING.getCode())
                .nodeInfo(null)
                .runRule(TaskRunRuleEnum.ONCE.getCode())
                .taskCycle(TaskRunRuleEnum.ONCE.getCode())
                .runDate(null)
                .calendarTime(null)
                .taskInfo(JSONUtil.toJsonStr(questionTaskInfoDTO))
                .limitHour(null)
                .regionModel(false)
                .loopCount(1L)
                .build();
        //数据插入父任务表
        parentDO.setExtraParam(buildQuestionRequest.getExtraParam());
        taskParentMapper.insertTaskParent(enterpriseId, parentDO);

        TbQuestionParentInfoDO questionParentInfoDO = new TbQuestionParentInfoDO();
        questionParentInfoDO.setUnifyTaskId(parentDO.getId());
        questionParentInfoDO.setStatus(0);
        questionParentInfoDO.setQuestionName(buildQuestionRequest.getTaskName());
        questionParentInfoDO.setQuestionType(buildQuestionRequest.getQuestionType());
        questionParentInfoDO.setFinishNum(0);
        questionParentInfoDO.setTotalNum(buildQuestionRequest.getQuestionList().size());
        questionParentInfoDO.setCreateTime(new Date());
        questionParentInfoDO.setCreateId(userId);
        questionParentInfoDao.insertSelective(enterpriseId, questionParentInfoDO);


        unifyTaskParentItemDOList.forEach(unifyTaskParentItemDO -> {
            QuestionTaskInfoDTO questionTaskInfo = JSONObject.parseObject(unifyTaskParentItemDO.getTaskInfo(), QuestionTaskInfoDTO.class);
            //问题工单巡店同一检查项不能再次发起
            Long dataColumnId = questionTaskInfo.getDataColumnId();
            if (dataColumnId != null && dataColumnId > 0) {
                if (buildQuestionRequest.getQuestionType().equals(QuestionTypeEnum.STORE_WORK.getCode())){
                    redisUtilPool.setString(redisConstantUtil.getStoreWorkQuestionTaskLockKey(enterpriseId, String.valueOf(dataColumnId)),
                            String.valueOf(parentDO.getId()), 60);
                }else{
                    redisUtilPool.setString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataColumnId)),
                            String.valueOf(parentDO.getId()), 60);
                }
            }
            unifyTaskParentItemDO.setUnifyTaskId(parentDO.getId());
            unifyTaskParentItemDao.insertSelective(enterpriseId, unifyTaskParentItemDO);
            //转码视频
            checkQuestionVideoHandel(enterpriseId, unifyTaskParentItemDO, questionTaskInfo,
                    unifyTaskParentItemDO.getId());
        });

        //父工单创建mq消息发送
        try {
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, EnterpriseStatusEnum.NORMAL.getCode());
            if (!Objects.isNull(enterpriseMqInformConfigDTO)) {
                log.info("企业开启了mq配置，发送父工单创建消息");
                //mq发送签到消息
                JSONObject data = new JSONObject();
                data.put("enterpriseId", enterpriseId);
                //模块类型巡店
                data.put("moduleType", TaskTypeEnum.QUESTION_ORDER.getCode());
                //业务类型是签到
                data.put("bizType", BailiInformNodeEnum.PARENT_TASK_CREATION.getCode());
                //时间戳
                data.put("timestamp", System.currentTimeMillis());
                //业务id
                data.put("taskId",parentDO.getId());
                SendResult send = simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM, System.currentTimeMillis() + 3000);
                log.info("父工单创建消息发送结果：{}", send);
            }else {
                log.info("企业未开启mq配置，不发送父工单创建消息");
            }
        }catch (Exception e){
            log.error("父工单创建消息发送失败",e);
        }

        return parentDO;
    }

    @Override
    public TaskParentDO addSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request) {
        // 构建父任务数据
        TaskParentDO taskParentDO = new TaskParentDO();
        taskParentDO.setTaskName(request.getTaskName());
        taskParentDO.setTaskType(TaskTypeEnum.SUPERVISION.getCode());
        taskParentDO.setBeginTime(request.getTaskStartTime().getTime());
        taskParentDO.setEndTime(request.getTaskEndTime().getTime());
        taskParentDO.setCreateUserId(user.getUserId());
        taskParentDO.setCreateTime(System.currentTimeMillis());
        taskParentDO.setTaskDesc(request.getDesc());
        taskParentDO.setParentStatus(UnifyStatus.ONGOING.getCode());
        taskParentDO.setCreateUserName(user.getName());
        taskParentDO.setCreateUserId(user.getUserId());
        taskParentDO.setRunRule(Constants.ONCE);
        taskParentDO.setTaskInfo(null);
        taskParentDO.setLoopCount(Constants.LONG_ONE);
        taskParentDO.setNodeInfo(null);
        // 新增父任务
        taskParentDao.insertTaskParent(enterpriseId, taskParentDO);
        return taskParentDO;
    }

    @Override
    public Boolean updateSuperVisionParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request) {
        TaskParentDO taskParentDO = new TaskParentDO();
        taskParentDO.setTaskName(request.getTaskName());
        taskParentDO.setBeginTime(request.getTaskStartTime().getTime());
        taskParentDO.setEndTime(request.getTaskEndTime().getTime());
        taskParentDO.setTaskDesc(request.getDesc());
        taskParentDao.updateSuperVisionParent(enterpriseId,taskParentDO);
        return Boolean.TRUE;
    }


    private void checkQuestionVideoHandel(String enterpriseId, UnifyTaskParentItemDO unifyTaskParentItemDO, QuestionTaskInfoDTO taskInfo, Long itemId) {
        log.info("checkQuestionVideoHandel#enterpriseId={},itemId={}", enterpriseId, itemId);
        try {
            if (StringUtils.isBlank(taskInfo.getVideos())) {
                return;
            }
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(taskInfo.getVideos(), SmallVideoInfoDTO.class);
            if (smallVideoInfo == null || CollectionUtils.isEmpty(smallVideoInfo.getVideoList())) {
                return;
            }

            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成
                if (smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()) {
                    continue;
                }
                callbackCache = redisUtilPool.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if (StringUtils.isNotBlank(callbackCache)) {
                    smallVideoCache = JSONObject.parseObject(callbackCache, SmallVideoDTO.class);
                    if (smallVideoCache != null && smallVideoCache.getStatus() != null && smallVideoCache.getStatus() >= 3) {
                        BeanUtils.copyProperties(smallVideoCache, smallVideo);
                        if (StringUtils.isNotBlank(smallVideo.getVideoUrl())) {
                            smallVideo.setVideoUrl(smallVideo.getVideoUrl().replace("http://", "https://"));
                        }
                        if (StringUtils.isNotBlank(smallVideo.getVideoUrlBefore())) {
                            smallVideo.setVideoUrlBefore(smallVideo.getVideoUrlBefore().replace("http://", "https://"));
                        }
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        unifyTaskService.setNotCompleteCache(smallVideoParam, smallVideo, itemId, enterpriseId);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    unifyTaskService.setNotCompleteCache(smallVideoParam, smallVideo, itemId, enterpriseId);
                }
            }
            taskInfo.setVideos(JSONObject.toJSONString(smallVideoInfo));
            unifyTaskParentItemDO.setTaskInfo(JSONObject.toJSONString(taskInfo));
            unifyTaskParentItemDao.updateByPrimaryKeySelective(enterpriseId, unifyTaskParentItemDO);
            log.info("checkQuestionVideoHandel处理完成#enterpriseId={},taskId={}", enterpriseId, itemId);
        } catch (Exception e) {
            log.error("工单创建视频异常 taskId:{}", itemId, e);
        }
    }

}
