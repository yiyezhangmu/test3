package com.coolcollege.intelligent.service.unifytask.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.PersonTypeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.message.MessageStatusEnums;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.achievement.PanasonicMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataDefTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentUserMappingDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreGroupMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbDisplayTableRecordMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.*;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.achievement.entity.ManageStoreCategoryCodeDO;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.ApproveDTO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableUserAuthDO;
import com.coolcollege.intelligent.model.mq.MqMessageDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.newbelle.dto.InventoryStoreDataDTO;
import com.coolcollege.intelligent.model.newbelle.request.InventoryStoreDataRequest;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.request.ExportTaskQuestionRequest;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentUserMappingDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerAddRequest;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerCalendarInfoRequest;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.BasicsAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.store.dto.StoreUserDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.unifytask.*;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.TaskParentQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskDetailByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.*;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.model.workFlow.WorkflowDealDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.newbelle.ProductFeedbackService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.*;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolcollege.intelligent.util.TaskCacheManager;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.BailiInformNodeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_5;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 15:33
 */
@Service(value = "unifyTaskService")
@Slf4j
public class UnifyTaskServiceImpl implements UnifyTaskService {
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private StoreGroupMapper storeGroupMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    @Lazy
    private JmsTaskService jmsTaskService;
    @Resource
    private StoreMapper storeMapper;
    @Lazy
    @Autowired
    private StoreService storeService;
    @Resource
    private StoreGroupMappingMapper storeGroupMappingMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Autowired
    private AuthVisualService authVisualService;
    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Autowired
    private UnifyTaskDataMappingService unifyTaskDataMappingService;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Autowired
    private ImportTaskService importTaskService;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private UnifyTaskParentCcUserService unifyTaskParentCcUserService;
    @Resource
    private TaskStoreDao taskStoreDao;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private UnifyTaskParentUserDao unifyTaskParentUserDao;
    @Resource
    private UnifyTaskParentService unifyTaskParentService;
    @Resource
    private UnifyTaskSubService unifyTaskSubService;
    @Resource
    private UnifyTaskPersonService unifyTaskPersonService;
    @Resource
    private AuthVisualService visualService;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private UnifyTaskParentItemDao unifyTaskParentItemDao;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private QuestionParentUserMappingService questionParentUserMappingService;
    @Resource
    private QuestionParentUserMappingDao questionParentUserMappingDao;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private UnifyTaskParentCollaboratorDao unifyTaskParentCollaboratorDao;
    @Resource
    private TbDisplayTableRecordMapper displayTableRecordMapper;
    @Resource
    private TbPatrolStoreRecordMapper patrolStoreRecordMapper;
    @Resource
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;
    @Resource
    private TaskParentDao taskParentDao;
    @Resource
    private QuestionHistoryDao questionHistoryDao;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private MqMessageDAO mqMessageDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskMappingDao taskMappingDao;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;

    private static final Pattern PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    private static final int STORELIMIT = 20000;

    private static final int PERSON_LIMIT = 200;

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Resource
    ProductFeedbackService productFeedbackService;

    @Resource
    TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private PanasonicMapper panasonicMapper;

    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;
    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;


    /**
     * mapping人员表增加关联关系
     *
     * @param taskId
     * @param fromUserId
     * @param toUserId
     * @param storeId
     * @param enterpriseId
     * @param node
     */
    @Override
    public void addAboutTurnPeople(Long taskId, String fromUserId, String toUserId, String storeId, String enterpriseId, String node, Long loopCount) {
        // 转交替换相应节点人员
        TaskStoreDO taskStoreDO = unifyTaskStoreService.replaceTaskStoreNodePerson(enterpriseId, taskId, storeId, loopCount, node, fromUserId, toUserId);
        // 保存转交人到父任务处理人表中
        this.saveTaskParentUser(enterpriseId, taskId, taskStoreDO.getTaskType(), Lists.newArrayList(toUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskMessageDTO insertUnifyTask(String enterpriseId, UnifyTaskBuildDTO task, String userId, long createTime) {
        List<GeneralDTO> storeGeneralList = task.getStoreIds();
        //遍历入参门店范围，分组、区域得所有门店
        Set<String> storeIdSet = getStoreIdList(enterpriseId, storeGeneralList, userId, false);
        if (CollectionUtils.isEmpty(storeIdSet)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该门店范围内无门店");
        }
        if (storeIdSet.size() > STORELIMIT) {
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "门店数量不允许超过" + STORELIMIT);
        }
        if (task.getBeginTime() > task.getEndTime()) {
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "开始时间不能大于结束时间");
        }
        if (CollectionUtils.isNotEmpty(task.getProcess())) {
            for (TaskProcessDTO process : task.getProcess()) {
                if (CollectionUtils.isNotEmpty(process.getUser()) && process.getUser().size() > PERSON_LIMIT) {
                    throw new ServiceException(ErrorCodeEnum.PERSON_LIMIT, PERSON_LIMIT);
                }
            }
        }

        Long dataColumnId = null;
        String questionType = QuestionTypeEnum.COMMON.getCode();
        //问题工单巡店同一检查项不能再次发起
        if ((TaskTypeEnum.QUESTION_ORDER.getCode().equals(task.getTaskType())) && StringUtils.isNotBlank(task.getTaskInfo())) {
            JSONObject jsonObject = JSON.parseObject(task.getTaskInfo());
            dataColumnId = jsonObject.getLong("dataColumnId");
            String businessId = jsonObject.getString("businessId");
            if (dataColumnId != null && StringUtils.isNotBlank(businessId)) {
                questionType = QuestionTypeEnum.PATROL_STORE.getCode();
                TbDataStaTableColumnDO tableColumnDO = tbDataStaTableColumnMapper.selectById(enterpriseId, dataColumnId);
                if (tableColumnDO != null && tableColumnDO.getTaskQuestionId() != null && tableColumnDO.getTaskQuestionId() > 0) {
                    throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "该检查项已发起工单，不能再次发起工单");
                }
                if (StringUtils.isNotBlank(redisUtilPool.getString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataColumnId))))) {
                    throw new ServiceException(ErrorCodeEnum.TASK_QUESTION_CREATE);
                }
            }
        } else if ((TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(task.getTaskType())
                || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(task.getTaskType())
                || TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(task.getTaskType())
                || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(task.getTaskType())
                || TaskTypeEnum.PRODUCT_FEEDBACK.getCode().equals(task.getTaskType())
                || TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(task.getTaskType()))
                && StringUtils.isNotBlank(task.getTaskInfo())) {
            // 移除taskInfo无用key
            try {
                removeTaskInfoUnuseKey(task);
            } catch (Exception e) {
                log.error("移除taskInfo无用key异常", e);
            }
        }

        String runRule = StringUtils.isEmpty(task.getRunRule()) ? "ONCE" : task.getRunRule();
        //extraParam中保存的输入门店范围
        JSONObject inputStoreScopeJson = new JSONObject();
        if (CollectionUtils.isNotEmpty(task.getTaskDisplayStoreScopeList())) {
            inputStoreScopeJson.put("inputStoreScopeList", task.getTaskDisplayStoreScopeList());
        } else {
            inputStoreScopeJson.put("inputStoreScopeList", task.getStoreIds());
        }
        //设计时未考虑到未生成任务与循环任务，在这里做兼容
        if (Objects.nonNull(task.getIsOperateOverdue())){
            String taskInfo = task.getTaskInfo();
            JSONObject jsonObject = JSON.parseObject(taskInfo);
            if(Objects.nonNull(task.getIsOperateOverdue())){
                jsonObject.put("isOperateOverdue",task.getIsOperateOverdue());
                String newTaskInfo = jsonObject.toJSONString();
                task.setTaskInfo(newTaskInfo);
            }
        }
        TaskParentDO parentDO = TaskParentDO.builder()
                .taskName(task.getTaskName())
                .taskType(task.getTaskType())
                .beginTime(task.getBeginTime())
                .endTime(task.getEndTime())
                .createUserId(userId)
                .createTime(createTime)
                .taskDesc(task.getTaskDesc())
                .parentStatus(UnifyStatus.ONGOING.getCode())
                .nodeInfo(JSON.toJSONString(task.getProcess()))
                .runRule(runRule)
                .taskCycle(task.getTaskCycle())
                .runDate(task.getRunDate())
                .calendarTime(task.getCalendarTime())
                .taskInfo(task.getTaskInfo())
                .limitHour(task.getLimitHour())
                .loopCount(0L).attachUrl(task.getAttachUrl())
                .regionModel(task.getRegionModel())
                .extraParam(inputStoreScopeJson.toJSONString())
                .aiAudit(task.getAiAudit() != null && task.getAiAudit())
                .storeOpenRuleId(task.getRuleId() == null ? 0 : task.getRuleId())
                .productNo(StringUtils.isBlank(task.getProductNo()) ? null : task.getProductNo())
                .build();
        if(TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(task.getTaskType()) ||
                TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(task.getTaskType())){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.PRODUCT, JSONObject.toJSONString(task.getProductInfoDTOList()));
            parentDO.setTaskInfo(jsonObject.toJSONString());
        }
        //RPC创建模板至流程引擎 null默认工作流任务
        if (StringUtils.isEmpty(task.getTaskPattern()) ||
                UnifyTaskPatternEnum.WORKFLOW.getCode().equals(task.getTaskPattern())) {
            parentDO.setTemplateId(null);
        }
        //数据插入父任务表
        taskParentMapper.insertTaskParent(enterpriseId, parentDO);
//        taskSubMapper.updateOperateOverdue(enterpriseId,parentDO.getId(),task.getIsOperateOverdue());
        Long taskId = parentDO.getId();
        //门店映射--按区域、分组、门店三种格式存
        List<TaskMappingDO> storeList = getMappingData(storeGeneralList, taskId);
        Lists.partition(storeList, Constants.BATCH_INSERT_COUNT).forEach(partStoreList -> {
            taskMappingMapper.insertTaskMapping(enterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), partStoreList);
        });
        //数据映射
        if (CollectionUtils.isNotEmpty(task.getForm())) {
            unifyTaskDataMappingService.insertDataTaskMappingNew(enterpriseId, task, taskId);
        }
        //插入协作人
        if (CollectionUtils.isNotEmpty(task.getCollaboratorIdList())) {
            List<UnifyTaskParentCollaboratorDO> list = new ArrayList<>();
            task.getCollaboratorIdList().forEach(collaboratorId -> {
                UnifyTaskParentCollaboratorDO unifyTaskParentCollaboratorDO = new UnifyTaskParentCollaboratorDO(null, parentDO.getId(), parentDO.getTaskName(),
                        task.getTaskType(), collaboratorId, UnifyStatus.ONGOING.getCode(), parentDO.getBeginTime(), parentDO.getEndTime());
                list.add(unifyTaskParentCollaboratorDO);
            });
            unifyTaskParentCollaboratorDao.batchInsertOrUpdate(enterpriseId, list);
        }

        //问题工单巡店同一检查项不能再次发起
        if ((TaskTypeEnum.QUESTION_ORDER.getCode().equals(task.getTaskType())) && dataColumnId != null) {
            redisUtilPool.setString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataColumnId)),
                    String.valueOf(parentDO.getId()), 2 * 60);
        }
        //判断是否是循环任务
        TaskMessageDTO taskMessageDTO = new TaskMessageDTO();
        taskMessageDTO.setUnifyTaskId(taskId);
        //问题工单 处理视频缓存
        if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(task.getTaskType())) {
            checkQuestionVideoHandel(parentDO, enterpriseId);
            //保存工单任务信息
            saveQuestionInfo(enterpriseId, parentDO, questionType, task.getStoreIds().get(0).getValue());
            return taskMessageDTO;
        }

        if (TaskRunRuleEnum.ONCE.getCode().equals(runRule)) {
            setSchedulerForOnce(enterpriseId, taskId, new Date(parentDO.getBeginTime()),task.getIsOperateOverdue());
        } else {
            setScheduler(enterpriseId, taskId, parentDO);
        }

        return taskMessageDTO;
    }

    // 补发子任务  某个门店 某一轮次 的子任务
    @Override
    public void reissueSubTask(String enterpriseId, Long taskId, String storeIds, Long loopCount) {
        log.info("##任务补发 taskId={} ,storeIds={},loopCount={}", taskId, storeIds, loopCount);
        TaskParentDO parentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        ValidateUtil.validateObj(parentDO);
        if (TaskRunRuleEnum.ONCE.getCode().equals(parentDO.getRunRule()) && loopCount > 1) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "单次任务轮次不能大于1");
        }
        Set<String> storeIdSet = Sets.newHashSet();
        if (StringUtils.isEmpty(storeIds)) {
            List<TaskMappingDO> mappingDOList = taskMappingMapper.selectMappingByTaskId(enterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), taskId);
            List<GeneralDTO> storeGeneralList = mappingDOList.stream().map(m -> {
                GeneralDTO generalDTO = new GeneralDTO();
                generalDTO.setValue(m.getMappingId());
                generalDTO.setType(m.getType());
                generalDTO.setFilterRegionId(m.getFilterRegionId());
                return generalDTO;
            }).collect(Collectors.toList());
            //遍历入参门店范围，分组、区域得所有门店
            Boolean filterStoresWthoutPersonnel = filterStoresWthoutPersonnelByTaskInfo(parentDO.getTaskInfo());
            storeIdSet = getStoreIdList(enterpriseId, storeGeneralList, parentDO.getCreateUserId(), filterStoresWthoutPersonnel);
        } else {
            char separator = ',';
            List<String> storeIdList = StrUtil.splitTrim(storeIds, separator);//分割字符并去除空格
            storeIdSet = new HashSet<>(storeIdList);
        }

        List<TaskProcessDTO> process = JSON.parseArray(parentDO.getNodeInfo(), TaskProcessDTO.class);
        //第一次循环都取最新的门店集合储存
        List<TaskMappingDO> personList = Lists.newArrayList();
        getPerson(process, taskId, personList, storeIdSet, enterpriseId, parentDO.getCreateUserId(), parentDO.getTaskType(), true,null);
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
        for (String storeIdTmp : storeIdSet) {
            Set<String> userSet = collectMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            Set<String> ccUserSet = new HashSet<>();
            Set<String> auditUserSet = new HashSet<>();
            Set<String> recheckUserSet = new HashSet<>();
            //三级审批人
            Set<String> thirdApproveSet = new HashSet<>();
            //四级审批人
            Set<String> fourApproveSet = new HashSet<>();
            //五级审批人
            Set<String> fiveApproveSet = new HashSet<>();
            if (ccPersonMap.get(storeIdTmp) != null) {
                ccUserSet = ccPersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (auditPersonMap.get(storeIdTmp) != null) {
                auditUserSet = auditPersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (recheckPersonMap.get(storeIdTmp) != null) {
                recheckUserSet = recheckPersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (recheckPersonMap.get(storeIdTmp) != null) {
                recheckUserSet = recheckPersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (thirdApprovePersonMap.get(storeIdTmp) != null) {
                thirdApproveSet = thirdApprovePersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (fourApprovePersonMap.get(storeIdTmp) != null) {
                fourApproveSet = fourApprovePersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            if (fiveApprovePersonMap.get(storeIdTmp) != null) {
                fiveApproveSet = fiveApprovePersonMap.get(storeIdTmp).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
            }
            UnifySubTaskForStoreData unifySubTaskForStoreData = new UnifySubTaskForStoreData();
            unifySubTaskForStoreData.setTaskId(taskId);
            unifySubTaskForStoreData.setStoreId(storeIdTmp);
            unifySubTaskForStoreData.setNewLoopCount(loopCount);
            unifySubTaskForStoreData.setCreateTime(createTime);
            unifySubTaskForStoreData.setParentDO(parentDO);
            unifySubTaskForStoreData.setUserSet(userSet);
            unifySubTaskForStoreData.setEnterpriseId(enterpriseId);
            unifySubTaskForStoreData.setCcUserSet(ccUserSet);
            unifySubTaskForStoreData.setAuditUserSet(auditUserSet);
            unifySubTaskForStoreData.setRecheckUserSet(recheckUserSet);
            unifySubTaskForStoreData.setThirdApproveSet(thirdApproveSet);
            unifySubTaskForStoreData.setFourApproveSet(fourApproveSet);
            unifySubTaskForStoreData.setFiveApproveSet(fiveApproveSet);
            simpleMessageService.send(JSONObject.toJSONString(unifySubTaskForStoreData), RocketMqTagEnum.STORE_SUB_TASK_DATA_QUEUE);
        }

    }

    /**
     * 遍历入参门店范围，分组、区域得所有门店
     *
     * @param enterpriseId
     * @param storeGeneralList
     * @param userId
     * @return
     */
    @Override
    public Set<String> getStoreIdList(String enterpriseId, List<GeneralDTO> storeGeneralList, String userId, boolean filterStoresWithoutPersonnel) {
        Set<String> storeSet = Sets.newHashSet();
        //有效set
        Set<String> storeEffitiveSet = Sets.newHashSet();
        List<String> regionList = Lists.newArrayList();
        List<String> groupList = Lists.newArrayList();
        List<String> filterGroupIdList = Lists.newArrayList();
        List<String> filterDeptIdList = Lists.newArrayList();
        for (GeneralDTO item : storeGeneralList) {
            if(StringUtils.isBlank(item.getValue())){
                continue;
            }
            switch (item.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    storeEffitiveSet.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    regionList.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    groupList.add(item.getValue());
                    break;
                case UnifyTaskConstant.StoreType.GROUP_REGION:
                    //分组大区  百丽定制
                    filterGroupIdList.add(item.getValue());
                    if(Objects.nonNull(item.getFilterRegionId())){
                        filterDeptIdList.add(item.getFilterRegionId());
                    }
                    break;
                default:
                    throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(),
                            "操作类型有误：operate=" + item.getType());
            }
        }
        //FIXME 存在空
        if (!storeEffitiveSet.isEmpty()) {
            List<String> effticeStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(storeEffitiveSet));
            if (CollectionUtils.isNotEmpty(effticeStoreIdList)) {
                storeSet.addAll(effticeStoreIdList);
            }
        }
        Set<String> allStoreIds = Sets.newHashSet();
        //区域
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<String> regionPathList = regionMapper.getFullPathByIds(enterpriseId, regionList);
            List<StoreAreaDTO> areaStoreList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if(CollectionUtils.isNotEmpty(areaStoreList)){
                allStoreIds.addAll(areaStoreList.stream().map(StoreAreaDTO::getStoreId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
            }
        }
        //分组
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupMappingDO> groupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, groupList);
            if(CollectionUtils.isNotEmpty(groupStoreList)){
                allStoreIds.addAll(groupStoreList.stream().map(StoreGroupMappingDO::getStoreId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
            }
        }
        Set<String> filterDeptStoreList = new HashSet<>();
        //分组
        Set<String> filterGroupStoreIdList = new HashSet<>();
        //分组部门交叉
        if (CollectionUtils.isNotEmpty(filterDeptIdList) && CollectionUtils.isNotEmpty(filterGroupIdList)) {
            //区域
            List<String> regionPathList = regionMapper.getFullPathByIds(enterpriseId, filterDeptIdList);
            List<StoreAreaDTO> areaDTOList = storeMapper.listStoreByRegionPathList(enterpriseId, regionPathList);
            if (CollectionUtils.isNotEmpty(areaDTOList)) {
                List<String> areaStoreIds = areaDTOList.stream().map(StoreAreaDTO::getStoreId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                filterDeptStoreList.addAll(areaStoreIds);
            }
            List<StoreGroupMappingDO> filterGroupStoreList = storeGroupMappingMapper.getStoreGroupMappingByGroupIDs(enterpriseId, filterGroupIdList);
            if (CollectionUtils.isNotEmpty(filterGroupStoreList)) {
                List<String> filterGroupStoreIds = filterGroupStoreList.stream().map(StoreGroupMappingDO::getStoreId).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                filterGroupStoreIdList.addAll(filterGroupStoreIds);
            }
        }
        if (CollectionUtils.isNotEmpty(filterGroupStoreIdList) && CollectionUtils.isNotEmpty(filterDeptStoreList)) {
            filterDeptStoreList.retainAll(filterGroupStoreIdList);
            if (CollectionUtils.isNotEmpty(filterDeptStoreList)) {
                allStoreIds.addAll(filterDeptStoreList);
            }
        }
        if (CollectionUtils.isNotEmpty(allStoreIds)) {
            AuthVisualDTO authStore = authVisualService.authRegionStoreByStore(enterpriseId, userId, new ArrayList<>(allStoreIds));
            log.info("##unify task regionList authStore={}", JSON.toJSONString(authStore));
            if (Objects.nonNull(authStore) && CollectionUtils.isNotEmpty(authStore.getStoreIdList())) {
                List<String> effticeGroupStoreIdList = storeMapper.getEffectiveStoreByIdList(enterpriseId, new ArrayList<>(authStore.getStoreIdList()));
                if (CollectionUtils.isNotEmpty(effticeGroupStoreIdList)) {
                    storeSet.addAll(effticeGroupStoreIdList);
                }
            }
        }
        //  需要过滤没有店内人员的门店
        if (CollectionUtils.isNotEmpty(storeSet) && filterStoresWithoutPersonnel) {
            storeSet = getHasStoreInsidePersonStore(enterpriseId, storeSet);
        }
        return storeSet;
    }

    /**
     * 创建定时任务
     *
     * @param enterpriseId
     * @param taskId
     * @param parentDO
     */
    private Boolean setScheduler(String enterpriseId, Long taskId, TaskParentDO parentDO) {
        //循环任务入参校验
        ValidateUtil.validateObj(UnifyTaskLoopDateEnum.getByCode(parentDO.getTaskCycle()));
        if (!UnifyTaskLoopDateEnum.DAY.getCode().equals(parentDO.getTaskCycle())) {
            ValidateUtil.validateString(parentDO.getRunDate());
        }
        String requestString = null;
        List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
        jobs.add(ScheduleCallBackUtil.getCallBack(schedulerCallbackTaskUrl + "/v2/" + enterpriseId + "/communication/unity_task_scheduler/" + taskId, ScheduleCallBackEnum.api.getValue()));
        //日循环
        if (UnifyTaskLoopDateEnum.DAY.getCode().equals(parentDO.getTaskCycle())) {
            String startTime = DateUtils.convertTimeToString(parentDO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
            Date beginTime = new Date(parentDO.getBeginTime());
            Date endTime = new Date(parentDO.getEndTime());
            long day = DateUtil.betweenDay(beginTime, endTime, true);
            ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime + " " + parentDO.getCalendarTime(), jobs);
            LocalTime now = LocalTime.now();
            LocalTime calendarTime = LocalTime.parse(parentDO.getCalendarTime() + ":00");

            LocalDate beginDate = beginTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            //如果执行时间在任务当前时间之后，则今天会执行
            if (calendarTime.isAfter(now) || beginDate.isAfter(LocalDate.now())) {
                day = day + 1;
            }
            fixedRequest.setTimes((int) day);
            requestString = JSON.toJSONString(fixedRequest);
        } else if (UnifyTaskLoopDateEnum.HOUR.getCode().equals(parentDO.getTaskCycle())) {
            String startTime = DateUtils.convertTimeToString(parentDO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
            ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime + " " + parentDO.getCalendarTime(), jobs);
            fixedRequest.setInterval(Constants.ONE_HOUR);
            requestString = JSON.toJSONString(fixedRequest);
        } else {
            SchedulerAddRequest request = new SchedulerAddRequest();
            String startTime = DateUtils.convertTimeToString(parentDO.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
            request.setStartTime(startTime);
            request.setEndTime(new Date(parentDO.getEndTime()));
            request.setStatus(SchedulerStatusEnum.ON.getCode());
            request.setType(SchedulerTypeEnum.CALENDAR.getCode());
            request.setCalendarInfo(buildSchedulerCalendarInfoRequest(parentDO));
            request.setJobs(jobs);
            requestString = JSON.toJSONString(request);
        }
        if (log.isInfoEnabled()) {
            log.info("新增循环任务，开始调用定时器enterpriseId={},taskId={},开始调用参数={}", enterpriseId, taskId, requestString);
        }
        String schedule = HttpRequest.sendPost(schedulerApiUrl + "/v2/" + enterpriseId + "/schedulers", requestString, ScheduleCallBackUtil.buildHeaderMap());
        JSONObject jsonObjectSchedule = JSONObject.parseObject(schedule);
        if (log.isInfoEnabled()) {
            log.info("新增循环任务，结束调用定时器enterpriseId={},taskId={},返回结果={}", enterpriseId, taskId, jsonObjectSchedule);
        }
        String scheduleId = null;
        if (ObjectUtil.isNotEmpty(jsonObjectSchedule)) {
            scheduleId = jsonObjectSchedule.getString("scheduler_id");
        }
        //把scheduleId 更新回父任务表
        if (StringUtils.isNotEmpty(scheduleId)) {
            taskParentMapper.updateParentTaskById(enterpriseId, TaskParentDO.builder()
                    .scheduleId(scheduleId).build(), taskId);
        }
        return scheduleId == null ? Boolean.FALSE : Boolean.TRUE;
    }

    // 单次任务  设置10秒后执行  创建子任务   统一入口
    @Override
    public Boolean setSchedulerForOnce(String enterpriseId, Long taskId, Date beginTime,int isOperateOverdue) {
        return unifyTaskParentService.setSchedulerForOnce(enterpriseId, taskId, beginTime, 5,isOperateOverdue) == null ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * 构建定时器执行日历
     *
     * @param parentDO
     * @return SchedulerCalendarInfoRequest
     */
    private SchedulerCalendarInfoRequest buildSchedulerCalendarInfoRequest(TaskParentDO parentDO) {
        SchedulerCalendarInfoRequest calendarInfoRequest = new SchedulerCalendarInfoRequest();
        calendarInfoRequest.setCalendarTime(parentDO.getCalendarTime());
        calendarInfoRequest.setCalendarType(parentDO.getTaskCycle().toLowerCase());
        calendarInfoRequest.setCalendarValue(parentDO.getRunDate());
        // 季度循环按 月循环创建
        if (UnifyTaskLoopDateEnum.QUARTER.getCode().equals(parentDO.getTaskCycle())) {
            // yyyy-MM-dd
            List<String> runDateList = Arrays.asList(parentDO.getRunDate().split("-"));
            calendarInfoRequest.setCalendarType(UnifyTaskLoopDateEnum.MONTH.getCode().toLowerCase());
            calendarInfoRequest.setCalendarValue(runDateList.get(2));
        }
        return calendarInfoRequest;
    }

    @Override
    public void taskParentResolve(String enterpriseId, Long taskId, String dbName, boolean isReissue, boolean isRefresh) {
        String lockKey = MessageFormat.format("taskParentResolve:{0}:{1}", enterpriseId, taskId);
        boolean isLock = redisUtilPool.setNxExpire(lockKey, LocalDateTime.now().toString(), Constants.TASK_RESOLVE_LOCK);
        if(!isLock){
            log.info("当前任务正在分解，请稍后再试");
            throw new ServiceException(ErrorCodeEnum.TASK_RESOLVE_ERROR);
        }
        try {
            TaskParentDO parentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
            if(Objects.isNull(parentDO) || Constants.INDEX_ZERO.equals(parentDO.getStatusType())){
                log.info("父任务分解门店任务 任务不存在或已停止");
                return ;
            }
            // 按人任务拆分
            if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(parentDO.getTaskType())) {
                unifyTaskParentService.splitTaskForPerson(enterpriseId, dbName, JSONArray.parseArray(parentDO.getNodeInfo(), TaskProcessDTO.class), parentDO);
                return;
            }
            //查询任务门店
            List<GeneralDTO> storeGeneralList = taskMappingDao.selectMappingByTaskId(enterpriseId, UnifyTableEnum.TABLE_STORE, taskId);
            //遍历入参门店范围，分组、区域得所有门店
            Boolean filterStoresWithoutPersonnel = filterStoresWthoutPersonnelByTaskInfo(parentDO.getTaskInfo());
            Set<String> storeIds = getStoreIdList(enterpriseId, storeGeneralList, parentDO.getCreateUserId(), filterStoresWithoutPersonnel);
            if (CollectionUtils.isEmpty(storeIds)) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该门店范围内无门店");
            }
            List<TaskProcessDTO> process = JSON.parseArray(parentDO.getNodeInfo(), TaskProcessDTO.class);
            //第一次循环都取最新的门店集合储存
            List<TaskMappingDO> personList = Lists.newArrayList();
            getPerson(process, taskId, personList, storeIds, enterpriseId, parentDO.getCreateUserId(), parentDO.getTaskType(), true,null);
            Long oldLoopCount = parentDO.getLoopCount();
            long newLoopCount = oldLoopCount + 1;
            //补发当前阶段任务
            if (isReissue || isRefresh) {
                newLoopCount = oldLoopCount == 0 ? 1 : oldLoopCount;
            }
            List<StoreDO> storeList = storeMapper.getByStoreIds(enterpriseId, new ArrayList<>(storeIds));
            if(CollectionUtils.isEmpty(storeList)){
                log.info("父任务分解门店任务 storeList 为空");
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该门店范围内无门店");
            }
            Map<String, StoreDO> storeMap = storeList.stream().collect(Collectors.toMap(StoreDO::getStoreId, v -> v));
            Map<String, List<TaskMappingDO>> storeUserNodeMap = personList.stream().collect(Collectors.groupingBy(TaskMappingDO::getType));
            for (String storeId : storeIds) {
                long startTime = System.currentTimeMillis();
                StoreDO storeDO = storeMap.get(storeId);
                if(Objects.isNull(storeDO) || StoreIsDeleteEnum.INVALID.getValue().equals(storeDO.getIsDelete())){
                    log.info("父任务分解门店任务 门店为空 或者门店为删除状态 暂时没办法分解任务 :{}", JSONObject.toJSONString(storeDO));
                    continue;
                }
                List<TaskMappingDO> storeUserNodeList = storeUserNodeMap.get(storeId);
                log.info("耗时 ：1---> {}, {}", storeId, (System.currentTimeMillis() - startTime) /1000);
                TaskStoreDO taskStore =  TaskStoreDO.buildStoreTask(parentDO, storeDO, storeUserNodeList, newLoopCount);
                log.info("耗时 ：2---> {}, {}", storeId, (System.currentTimeMillis() - startTime) /1000);
                UnifyStoreTaskResolveDTO taskStoreResolveDTO = new UnifyStoreTaskResolveDTO(enterpriseId, taskStore, isRefresh);
                simpleMessageService.send(JSONObject.toJSONString(taskStoreResolveDTO), RocketMqTagEnum.STORE_TASK_RESOLVE_DATA_QUEUE);
            }
            List<String> saveUsers = personList.stream().filter(f -> UnifyNodeEnum.isHandleNodeList().contains(f.getNode())).map(TaskMappingDO::getMappingId).distinct().collect(Collectors.toList());
            this.saveTaskParentUser(enterpriseId, taskId, parentDO.getTaskType(), saveUsers);
        } catch (ServiceException e) {
            log.info("任务分解异常{}", e);
        } finally {
            redisUtilPool.delKey(lockKey);
        }
    }

    @Override
    public void taskRefresh(String enterpriseId, Long taskId, String dbName) {
        String lockKey = MessageFormat.format("taskRefresh:{0}:{1}", enterpriseId, taskId);
        boolean isLock = redisUtilPool.setNxExpire(lockKey, LocalDateTime.now().toString(), Constants.TASK_RESOLVE_LOCK);
        if(!isLock){
            throw new ServiceException(ErrorCodeEnum.TASK_RESOLVE_REFRESH_ERROR);
        }
        executor.execute(() -> {
            try {
                DataSourceHelper.changeToSpecificDataSource(dbName);

                TaskParentDO parentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
                if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(parentDO.getTaskType())) {
                    buildTaskStoreQuestionOrder(enterpriseId, taskId, null, true);
                } else {
                    taskParentResolve(enterpriseId, taskId, dbName, false, true);
                }
                // 订正相关记录表的regionId和regionPath
                //correctTaskRecordRegionIdAndPath(enterpriseId, parentDO);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtilPool.delKey(lockKey);
            }
        });
    }

    /**
     * 订正任务相关记录表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param parentDO 父任务实体对象
     */
    private void correctTaskRecordRegionIdAndPath(String enterpriseId, TaskParentDO parentDO) {
        log.info("订正记录表，unifyTaskId:{}，taskType:{}", parentDO.getId(), parentDO.getTaskType());
        switch (TaskTypeEnum.getByCode(parentDO.getTaskType())) {
            case QUESTION_ORDER:
                questionRecordDao.correctRegionIdAndPath(enterpriseId, parentDO.getId());
                break;
            case TB_DISPLAY_TASK:
                displayTableRecordMapper.correctRegionIdAndPath(enterpriseId, parentDO.getId());
                break;
            case PATROL_STORE_ONLINE:
            case PATROL_STORE_OFFLINE:
                patrolStoreRecordMapper.correctRegionIdAndPath(enterpriseId, parentDO.getId());
                //tbDataStaTableColumnMapper.correctRegionIdAndPath(enterpriseId, parentDO.getId());
                //tbDataDefTableColumnMapper.correctRegionIdAndPath(enterpriseId, parentDO.getId());
                break;
            default:
                log.info("不支持的任务类型:{}", parentDO.getTaskType());
                break;
        }
    }

    /**
     * 保存父任务处理人映射-用作查询我处理的
     *
     * @param enterpriseId 企业id
     * @param unifyTaskId  父任务id
     * @param taskType     任务类型
     * @param personList   拆分后的人员信息
     */
    @Override
    public void saveTaskParentUser(String enterpriseId, Long unifyTaskId, String taskType, List<String> personList) {
        // 只有陈列任务查询我处理的，百丽专项需求
        if (!TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)) {
            return;
        }
        TaskParentUserSaveDTO saveDTO = new TaskParentUserSaveDTO();
        saveDTO.setEnterpriseId(enterpriseId);
        saveDTO.setUnifyTaskId(unifyTaskId);
        saveDTO.setUserIds(personList.stream().distinct().collect(Collectors.toList()));
        simpleMessageService.send(JSONObject.toJSONString(saveDTO), RocketMqTagEnum.TASK_PARENT_USER_SAVE);
    }

    // 一个门店一个门店构建子任务
    @Override
    public TaskMessageDTO buildSubTaskBySingleStore(String enterpriseId, String storeId, Set<String> userSet
            , Long parentTaskId, Long newLoopCount, Long createTime, Set<String> ccUserSet, UnifySubTaskForStoreData subTaskForStoreData) {
        String key = enterpriseId + "_" + parentTaskId;
        String taskDelFlagKey = redisConstantUtil.getTaskDelFlagKey(key);
        redisUtilPool.setString(taskDelFlagKey, newLoopCount.toString(), 60);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TaskParentDO parentDO = taskParentMapper.selectParentTaskById(enterpriseId, parentTaskId);
        parentDO.setIsOperateOverdue(subTaskForStoreData.getIsOperateOverdue());
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);

        //线上线下巡店、陈列任务、定时巡检、巡店计划、ai巡检 如果门店不是open则，不在发任务
        boolean isContainTaskType = (TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PRODUCT_FEEDBACK.getCode().equals(parentDO.getTaskType())
                || TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(parentDO.getTaskType()));

        if (!Constants.STORE_STATUS_OPEN.equals(storeDO.getStoreStatus()) && isContainTaskType) {
            log.info("buildSubTaskBySingleStore#该门店的未开始,不在生成门店任务taskId:{},storeId:{},loopCount:{},storeStatus:{}",
                    parentDO.getId(), storeId, newLoopCount, storeDO.getStoreStatus());
            return null;
        }


        if (TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(parentDO.getTaskType()) || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType())) {
            int num = deviceMapper.countDeviceByStoreId(enterpriseId, DeviceTypeEnum.DEVICE_VIDEO.getCode(), storeId);
            if (num == Constants.INDEX_ZERO) {
                log.info("该门店的定时巡检任务无摄像头， 无法生成门店任务");
                return null;
            }
        }


        Integer count = taskSubMapper.countByTaskIdAndStoreId(enterpriseId, parentTaskId, storeId, newLoopCount);
        log.info("count:{}", count);
        if (!TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType()) && count > 0) {
            log.info("该门店的子任务已生成，enterpriseId = {},taskId = {}, storeId = {}, loopCount = {}", enterpriseId, parentTaskId, storeId, newLoopCount);
            if (subTaskForStoreData.getTaskReissue() != null && subTaskForStoreData.getTaskReissue()) {
                log.info("该门店进行任务补发，enterpriseId = {},taskId = {}, storeId = {}, loopCount = {}", enterpriseId, parentTaskId, storeId, newLoopCount);
                //对已下发的任务，进行人员增加补发任务
                unifyTaskStoreService.taskReissue(enterpriseId, parentTaskId, storeId, newLoopCount, subTaskForStoreData);
            }
            return null;
        }
        //陈列任务已经删除不在进行补发
        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(parentDO.getTaskType())) {
            TbDisplayTableRecordDO tbDisplayTableRecordDO = displayTableRecordMapper.getByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, parentTaskId, storeId, newLoopCount);
            if (tbDisplayTableRecordDO != null) {
                log.info("该门店的陈列任务已存在，不再进行补发，enterpriseId = {},taskId = {}, storeId = {}, loopCount = {}", enterpriseId, parentTaskId, storeId, newLoopCount);
                return null;
            }
        }
        //巡店任务删除后，不再进行补发
        if (subTaskForStoreData.getTaskReissue() != null && subTaskForStoreData.getTaskReissue()) {
            TbPatrolStoreRecordDO patrolStoreRecordDO = patrolStoreRecordMapper.getRecordByTaskLoopCountAndOne(enterpriseId, parentTaskId, storeId, newLoopCount);
            if (patrolStoreRecordDO != null) {
                log.info("该门店的巡店任务已存在，不再进行补发，enterpriseId = {},taskId = {}, storeId = {}, loopCount = {}", enterpriseId, parentTaskId, storeId, newLoopCount);
                return null;
            }
        }


        // 获取父任务的循环轮次
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, parentTaskId);
        if (taskParentDO == null) {
            log.error("###父任务不存在,parentTaskId={},storeId={},newLoopCount={}", parentTaskId, storeId, newLoopCount);
            return null;
        }


        //松下  出样撤样 任务选择型号跟门店品类不对应不生成任务
        List<ProductInfoDTO> storeProducts=null;
        if (TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(parentDO.getTaskType())|| TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(parentDO.getTaskType())) {
            JSONObject jsonObject = JSON.parseObject(parentDO.getTaskInfo());
            String taskInfoStr = jsonObject.getString(Constants.PRODUCT);

            //任务选择型号列表
            List<ProductInfoDTO> productInfoDTOS = jsonObject.parseArray(taskInfoStr, ProductInfoDTO.class);

            //查当前门店关联品类
            List<ManageStoreCategoryCodeDO> storeCategoryCodeDOS = panasonicMapper.selectManageStoreCategoryCode(storeId, null);
            if (CollectionUtils.isEmpty(storeCategoryCodeDOS)){
                log.info("松下出样和撤样任务，门店未关联品类，不生成任务，enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, parentTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                return null;
            }
            List<String> storeCategoryCodes = storeCategoryCodeDOS.stream().map(c -> c.getCategoryCode()).collect(Collectors.toList());
            //取交集
            storeProducts = productInfoDTOS.stream().filter(o -> storeCategoryCodes.contains(o.getCategoryCode())).collect(Collectors.toList());
            log.info("松下出样和撤样任务，门店任务型号数量过滤后，storeProducts = {}", JSONObject.toJSONString(storeProducts));
            if (CollectionUtils.isEmpty(storeProducts)){
                log.info("松下出样和撤样任务，门店任务型号数量过滤后为空，不生成任务,enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, parentTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                return null;
            }
            //该门店未出样该型号或者库存为0  不生成撤样任务
            if (TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(parentDO.getTaskType())){
                //查询已出样且库存大于0 的
                List<String> models = panasonicMapper.selectStoreSampleExtraction(storeId, storeProducts.stream().map(c -> c.getType()).collect(Collectors.toList()));
                //留下可撤样型号
                storeProducts = storeProducts.stream().filter(c->models.contains(c.getType())).collect(Collectors.toList());
                log.info("松下出样和撤样任务，过滤库存和未出样的，storeProducts = {}", JSONObject.toJSONString(storeProducts));
                if (CollectionUtils.isEmpty(storeProducts)){
                    log.info("松下撤样任务，该门店未出样该型号或者库存为0，不生成任务,enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, parentTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                    return null;
                }
            }
        }
        List<TaskSubDO> subDOList = Lists.newArrayList();
        //去重后的list
        List<TaskSubDO> subTaskDistinctList = Lists.newArrayList();
        List<TaskStoreDO> taskStoreList = Lists.newArrayList();
        Long beginTime = null;
        Long endTime = null;
        String taskInfo = parentDO.getTaskInfo();
        String attachUrl = parentDO.getAttachUrl();


        if (TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(parentDO.getTaskType())|| TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(parentDO.getTaskType())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.PRODUCT, storeProducts);
            taskInfo= jsonObject.toJSONString();
            log.info("松下任务商品数据：{}", taskInfo);
        }

        if (TaskRunRuleEnum.ONCE.getCode().equals(parentDO.getRunRule())) {
            beginTime = parentDO.getBeginTime();
            endTime = parentDO.getEndTime();
        } else if (TaskRunRuleEnum.LOOP.getCode().equals(parentDO.getRunRule())) {
            Integer limitInt = new Double(parentDO.getLimitHour() * 60).intValue();
            beginTime = createTime;
            endTime = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(createTime), limitInt).getTime();
        }
        String templateId = parentDO.getTemplateId();
        //工单定义项id
        //工单单独设置时间
        String storeTaskDetailType = parentDO.getTaskType();
        String questionType = "";
        if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(parentDO.getTaskType()) && subTaskForStoreData.getTaskParentItemId() != null) {
            Long itemId = subTaskForStoreData.getTaskParentItemId();
            UnifyTaskParentItemDO unifyTaskParentItemDO = unifyTaskParentItemDao.selectByPrimaryKey(enterpriseId, itemId);
            TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, parentTaskId);
            beginTime = unifyTaskParentItemDO.getBeginTime().getTime();
            endTime = unifyTaskParentItemDO.getEndTime().getTime();
            taskInfo = unifyTaskParentItemDO.getTaskInfo();
            if (StringUtils.isBlank(templateId)) {
                templateId = unifyTaskParentItemDO.getTemplateId();
            }
            questionType = tbQuestionParentInfoDO.getQuestionType();
        }

        Date handleEndTimeDate = setHandleEndTime(taskParentDO, beginTime);
        if (handleEndTimeDate == null) {
            handleEndTimeDate = new Date(endTime);
        }
        boolean firstuser = true;
        Map<String, Long> handleUserStoreSizeMap = subTaskForStoreData.getHandleUserStoreSizeMap();
        for (String handleUser : userSet) {
            TaskSubDO subDO = TaskSubDO.builder()
                    .unifyTaskId(parentTaskId)
                    .createUserId(parentDO.getCreateUserId())
                    .createTime(createTime)
                    .handleUserId(handleUser)
                    .storeId(storeId)
                    .nodeNo(UnifyNodeEnum.FIRST_NODE.getCode())
                    .subStatus(UnifyStatus.ONGOING.getCode())
                    .flowState(UnifyTaskConstant.FLOW_INIT)
                    .groupItem(1L)
                    .cycleCount(1L)
                    .loopCount(newLoopCount)
                    .subTaskCode(StringUtils.join(parentTaskId, Constants.MOSAICS, storeId))
                    .subBeginTime(beginTime)
                    .subEndTime(endTime)
                    .taskType(parentDO.getTaskType())
                    .storeArea(storeDO.getRegionPath())
                    .regionId(storeDO.getRegionId())
                    .storeName(storeDO.getStoreName())
                    .handlerEndTime(handleEndTimeDate)
                    .isOperateOverdue(subTaskForStoreData.getIsOperateOverdue())
                    .build();
            subDOList.add(subDO);
            if (firstuser) {
                TaskStoreDO taskStore = new TaskStoreDO(taskParentDO, subDO);
                if (storeDO != null) {
                    taskStore.setRegionId(storeDO.getRegionId());
                    taskStore.setRegionWay(storeDO.getRegionPath());
                    taskStore.setStoreName(storeDO.getStoreName());
                    taskStore.setHandlerEndTime(handleEndTimeDate);
                    taskStore.setStoreTaskDetailType(storeTaskDetailType);
                }
                // 组装门店任务表  处理人、审批人、复审人、抄送人
                taskStore = fillTaskStoreNodePersonInfo(taskStore, subTaskForStoreData);
                taskStoreList.add(taskStore);
                //
                subTaskDistinctList.add(subDO);
                firstuser = false;
            }

        }

        unifyTaskStoreService.batchInsertTaskStore(enterpriseId, taskStoreList);


        //插入子任务表
        Lists.partition(subDOList, Constants.BATCH_INSERT_COUNT).forEach(partSubDOList -> {
            taskSubMapper.batchInsertTaskSub(enterpriseId, partSubDOList);
        });
        //工作通知
        String name = enterpriseUserDao.selectNameByUserId(enterpriseId, parentDO.getCreateUserId());
        if (Constants.SYSTEM_USER_ID.equals(parentDO.getCreateUserId())) {
            name = Constants.SYSTEM_USER_SEND_NAME;
        } else if (Constants.AI.equals(parentDO.getCreateUserId())) {
            name = Constants.AI;
        }
        String finalName = name;
        if (!TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType())) {
            subDOList.forEach(item -> {
                Long storeSize = (handleUserStoreSizeMap == null) ?  null : handleUserStoreSizeMap.get(item.getHandleUserId());
                if(Objects.nonNull(storeSize) && storeSize > 1 &&
                        (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(parentDO.getTaskType())
                                || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(parentDO.getTaskType())
                                || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(parentDO.getTaskType()))){
                    // 多个门店只发一条通知 和 待办
                    String outBusinessId = Constants.TASKNOTICECOMBINE + "_" + enterpriseId + "_" + parentDO.getId() + "_" + newLoopCount + "_" + UnifyNodeEnum.FIRST_NODE.getCode() + "_" +MD5Util.md5(JSONUtil.toJsonStr(Arrays.asList(item.getHandleUserId())));
                    jmsTaskService.sendUnifyTaskJms(parentDO.getTaskType(), Arrays.asList(item.getHandleUserId()), UnifyNodeEnum.FIRST_NODE.getCode(),
                            enterpriseId, storeDO.getStoreName(), item.getId(), finalName, item.getSubEndTime(),
                            parentDO.getTaskName(), false, item.getSubBeginTime(), item.getStoreId(), outBusinessId, false, parentDO.getId(), item.getCycleCount());
                }else {
                    jmsTaskService.sendUnifyTaskJms(parentDO.getTaskType(), Arrays.asList(item.getHandleUserId()), UnifyNodeEnum.FIRST_NODE.getCode(),
                            enterpriseId, storeDO.getStoreName(), item.getId(), finalName, item.getSubEndTime(),
                            parentDO.getTaskName(), false, item.getSubBeginTime(), item.getStoreId(), null, false, parentDO.getId(), item.getCycleCount());
                }

                    });
            //抄送人消息通知
            if (enterpriseStoreCheckSettingDO.getTaskCcRemind() && CollectionUtils.isNotEmpty(ccUserSet)) {
                jmsTaskService.sendUnifyTaskJms(parentDO.getTaskType(), new ArrayList<>(ccUserSet), UnifyNodeEnum.CC.getCode(),
                        enterpriseId, storeDO.getStoreName(), subDOList.get(0).getId(), finalName, endTime,
                        parentDO.getTaskName(), false, beginTime, storeId, null, true, parentDO.getId(), subDOList.get(0).getCycleCount());
            }
        }
        //子任务发布广播消息
        TaskMessageDTO taskMessage = new TaskMessageDTO(
                UnifyTaskConstant.TaskMessage.OPERATE_ADD,
                parentTaskId,
                parentDO.getTaskType(),
                parentDO.getCreateUserId(),
                createTime,
                JSON.toJSONString(subTaskDistinctList),
                enterpriseId,
                taskInfo,
                attachUrl);
        if (StringUtils.isNotEmpty(questionType)) {
            taskMessage.setQuestionType(questionType);
        }
        log.info("question_type:{}", JSONObject.toJSONString(taskMessage));
        taskMessage.setTaskParentItemId(subTaskForStoreData.getTaskParentItemId());
        taskMessage.setLoopCount(newLoopCount);
        taskMessage.setStoreId(storeId);
        log.info("newLoopCount:{}, storeId:{}", newLoopCount, storeId);
        sendTaskMessage(taskMessage);

        //陈列计入抄送人
        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
            List<UnifyTaskParentCcUserDO> list = new ArrayList<>();
            ccUserSet.forEach(userId -> {
                UnifyTaskParentCcUserDO unifyTaskParentCcUser = new UnifyTaskParentCcUserDO(taskParentDO.getId(), taskParentDO.getTaskName(),
                        taskParentDO.getTaskType(), userId, UnifyStatus.ONGOING.getCode(), taskParentDO.getBeginTime(), taskParentDO.getEndTime());
                list.add(unifyTaskParentCcUser);
            });
            if (!CollectionUtils.isEmpty(list)) {
                Lists.partition(list, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                    try {
                        unifyTaskParentCcUserService.batchInsertOrUpdate(enterpriseId, p);
                    }catch (Exception e){
                        log.error("批量插入抄送人失败,再重新插入一次", e);
                        unifyTaskParentCcUserService.batchInsertOrUpdate(enterpriseId, p);
                    }
                });
            }
        }
        return taskMessage;
    }
    @Override
    public void sendNotice(String enterpriseId, TaskStoreDO taskStore, TaskParentDO parentDO, List<TaskSubDO> subTaskList, Boolean isCC){
        if(CollectionUtils.isEmpty(subTaskList)){
            log.info("任务分解  子任务为空 消息发送失败");
            return;
        }
        TaskSubDO firstSubTask = subTaskList.get(0);
        if(Objects.isNull(firstSubTask)){
            log.info("任务分解  子任务的第一个对象为空 消息发送失败");
            return;
        }
        //工作通知
        if (!TaskTypeEnum.PATROL_STORE_AI.getCode().equals(parentDO.getTaskType())) {
            List<String> ccUserIds = null;
            //抄送人消息通知
            if (Boolean.TRUE.equals(isCC) && StringUtils.isNotBlank(taskStore.getCcUserIds())) {
                ccUserIds = Arrays.stream(StringUtils.split(taskStore.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            }
            sendTaskJms(enterpriseId, parentDO, taskStore, subTaskList, isCC, ccUserIds, firstSubTask.getId());
        }
        if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(parentDO.getTaskType())) {
            jmsTaskService.sendQuestionMessage(enterpriseId, taskStore.getUnifyTaskId(), taskStore.getStoreId(), taskStore.getLoopCount(), UnifyTaskConstant.TaskMessage.OPERATE_ADD, new HashMap<>());
        }
    }

    @Override
    public void sendTaskJms(String enterpriseId, TaskParentDO parentDO, TaskStoreDO taskStoreDO, List<TaskSubDO> taskSubDOList, Boolean isCc, List<String> ccUserIds, Long ccTaskSubId) {
        String name;
        if (Constants.SYSTEM_USER_ID.equals(parentDO.getCreateUserId())) {
            name = Constants.SYSTEM_USER_SEND_NAME;
        } else if (Constants.AI.equals(parentDO.getCreateUserId())) {
            name = Constants.AI;
        } else {
            name = TaskCacheManager.getCreateUserName(enterpriseId, parentDO.getId(), parentDO.getCreateUserId(), () -> enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, parentDO.getCreateUserId()));
        }
        // 处理人通知
        if (CollectionUtils.isNotEmpty(taskSubDOList)) {
            List<String> userIds = CollStreamUtil.toList(taskSubDOList, TaskSubDO::getHandleUserId);
            sendTaskJms(enterpriseId, parentDO.getId(), taskStoreDO.getStoreId(), taskStoreDO.getStoreName(), taskStoreDO.getLoopCount(), taskStoreDO.getNodeNo(),
                    parentDO.getTaskType(), userIds, taskSubDOList, name, parentDO.getTaskName(), taskStoreDO.getSubBeginTime().getTime(), taskStoreDO.getSubEndTime().getTime());
        }
        //抄送人消息通知
        if (Boolean.TRUE.equals(isCc) && CollectionUtils.isNotEmpty(ccUserIds)) {
            jmsTaskService.sendUnifyTaskJms(parentDO.getTaskType(), ccUserIds, UnifyNodeEnum.CC.getCode(),
                    enterpriseId, taskStoreDO.getStoreName(), ccTaskSubId, name, taskStoreDO.getSubEndTime().getTime(),
                    parentDO.getTaskName(), false, taskStoreDO.getSubBeginTime().getTime(), taskStoreDO.getStoreId(), null, true, parentDO.getId(), null, taskStoreDO.getLoopCount(), null);
        }
    }

    @Override
    public void sendTaskJms(String enterpriseId, Long unifyTaskId, String storeId, String storeName, Long loopCount, String nodeNo,
                            String taskType, List<String> userIds, List<TaskSubDO> taskSubList, String createUserName, String taskName, Long subBeginTime, Long subEndTime) {
        if (TaskTypeEnum.isCombineNoticeTypes(taskType)) {
            String outBusinessId = getCombineOutBusinessId(enterpriseId, unifyTaskId, loopCount, nodeNo);
            // isv消息的锁需要同步修改
            jmsTaskService.sendUnifyTaskJms(taskType, userIds, nodeNo,
                    enterpriseId, storeName, null, createUserName, subEndTime,
                    taskName, false, subBeginTime, storeId, outBusinessId, false, unifyTaskId, CollectionUtils.isNotEmpty(taskSubList) ? taskSubList.get(0).getCycleCount():null);
        } else {
            taskSubList.forEach(item -> {
                jmsTaskService.sendUnifyTaskJms(taskType, Arrays.asList(item.getHandleUserId()), UnifyNodeEnum.FIRST_NODE.getCode(),
                        enterpriseId, storeName, item.getId(), createUserName, item.getSubEndTime(),
                        taskName, false, item.getSubBeginTime(), item.getStoreId(), null, false, unifyTaskId, item.getCycleCount());
            });
        }
    }

    @Override
    public String getCombineOutBusinessId(String enterpriseId, Long unifyTaskId, Long loopCount, String nodeNo) {
        // 结尾加个newTask用于isv判断其为新任务流程的合并消息从而兼容旧任务的businessId格式
        return Constants.TASKNOTICECOMBINE + "_" + enterpriseId + "_" + unifyTaskId + "_" + loopCount + "_" + nodeNo + "_newTask";
    }

    @Override
    public PageInfo taskQuestionReportList(String enterpriseId, List<String> userIdList, Long timeBegin, Long timeEnd, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TaskParentDO> list = taskParentMapper.selectTaskQuestionList(enterpriseId, TaskTypeEnum.QUESTION_ORDER.getCode(), userIdList, timeBegin, timeEnd);

        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo(list);
        }
        PageInfo pageInfo = new PageInfo(list);
        //任务id
        Set<Long> taskIds = list.stream().map(TaskParentDO::getId).collect(Collectors.toSet());

        List<TaskStoreDO> taskStoreList = taskStoreMapper.listByUnifyTaskIds(enterpriseId, new ArrayList<>(taskIds), null);
        Map<Long, TaskStoreDO> taskStoreMap = new HashMap<>();

        List<Long> taskSubIdList = taskSubMapper.selectSubQuestionTaskIdByTaskIdList(enterpriseId, new ArrayList<>(taskIds));
        List<TaskSubVO> taskSubList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskSubIdList)) {
            taskSubList = taskSubMapper.selectSubQuestionTaskByTaskIdList(enterpriseId, taskSubIdList);
        }

        //门店id
        Set<String> storeIds = new HashSet<>();
        List<StorePathDTO> storePathDTOList = new ArrayList<>();
        //区域id
        Set<String> regionIds = new HashSet<>();
        //用户id
        Set<String> userIds = new HashSet<>();
        Map<String, TaskSubVO> taskSubMap = new HashMap<>();

        Map<String, TaskSubVO> taskSubHanderPicMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(taskSubList)) {
            for (TaskSubVO taskSubVO : taskSubList) {
                taskSubMap.put(taskSubVO.getUnifyTaskId() + "#" + taskSubVO.getFlowNodeNo(), taskSubVO);
                if (UnifyStatus.COMPLETE.getCode().equals(taskSubVO.getSubStatus())) {
                    taskSubHanderPicMap.put(taskSubVO.getUnifyTaskId() + "#" + taskSubVO.getFlowNodeNo(), taskSubVO);
                }

                userIds.add(taskSubVO.getHandleUserId());
                userIds.add(taskSubVO.getCreateUserId());
                storeIds.add(taskSubVO.getStoreId());
                regionIds.add(String.valueOf(taskSubVO.getRegionId()));

            }
        }

        for (TaskStoreDO taskStoreDO : taskStoreList) {
            StorePathDTO storePathDTO = new StorePathDTO();
            storePathDTO.setStoreId(taskStoreDO.getStoreId());
            storePathDTO.setRegionPath(taskStoreDO.getRegionWay());
            storePathDTOList.add(storePathDTO);
            taskStoreMap.put(taskStoreDO.getUnifyTaskId(), taskStoreDO);
            storeIds.add(taskStoreDO.getStoreId());
            userIds.add(taskStoreDO.getCreateUserId());
            regionIds.add(String.valueOf(taskStoreDO.getRegionId()));
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(userIds));

        Map<String, String> userMap = userList.stream().filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));

        List<StoreDO> storeList = storeMapper.getStoreByStoreIdList(enterpriseId, new ArrayList<>(storeIds));

        Map<String, StoreDO> storeMap = storeList.stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);

        List<RegionDO> regionList = regionService.getRegionDOsByRegionIds(enterpriseId, new ArrayList<>(regionIds));

        Map<Long, String> regionMap = regionList.stream().filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));

        List<TaskMappingDO> mappingDOList = taskMappingMapper.selectMappingByTaskIds(enterpriseId, new ArrayList<>(taskIds));
        //map:questionId -> metaColumnId
        Map<Long, Long> taskIdMetaColumnIdMap = mappingDOList.stream()
                .filter(a -> a.getUnifyTaskId() != null && a.getOriginMappingId() != null)
                .collect(Collectors.toMap(TaskMappingDO::getUnifyTaskId, TaskMappingDO::getOriginMappingId, (a, b) -> a));
        List<Long> metaColumnIds = mappingDOList.stream().map(TaskMappingDO::getOriginMappingId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> metaColumnList = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, metaColumnIds);
        Map<Long, TbMetaStaTableColumnDO> metaColumnMap = metaColumnList.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data -> data, (a, b) -> a));
        //2.查询检查表详情
        Set<Long> metaTableIds = metaColumnList.stream().map(TbMetaStaTableColumnDO::getMetaTableId).collect(Collectors.toSet());
        List<TbMetaTableDO> metaTableList = tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
        //map:tableId -> table
        Map<Long, String> tableMap = metaTableList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));

        List<TbDataStaTableColumnDO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.getListByQuestionIds(enterpriseId, new ArrayList<>(taskIds));
        Map<Long, String> taskNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tbDataStaTableColumnList)) {
            //2.查询检查表详情
            Set<Long> questionTaskIds = tbDataStaTableColumnList.stream().map(TbDataStaTableColumnDO::getTaskId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(questionTaskIds)) {
                List<TaskParentDO> taskNameList = taskParentMapper.selectTaskByIdsForMap(enterpriseId, new ArrayList<>(questionTaskIds));
                for (TaskParentDO taskParentDO : taskNameList) {
                    taskNameMap.put(taskParentDO.getId(), taskParentDO.getTaskName());
                }
            }
        }
        Map<Long, TbDataStaTableColumnDO> tbDataStaTableColumnMap = tbDataStaTableColumnList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getTaskQuestionId, Function.identity()));
        List<TaskQuestionVO> taskQuestionList = new ArrayList<>();
        for (TaskParentDO parentDO : list) {
            TaskQuestionVO taskQuestionVO = new TaskQuestionVO();
            TaskStoreDO taskStoreHander = taskStoreMap.get(parentDO.getId());
            taskQuestionVO.setCreateTime(parentDO.getCreateTime());
            taskQuestionVO.setCreateTimeDate(new Date(parentDO.getCreateTime()));
            taskQuestionVO.setId(parentDO.getId());
            taskQuestionVO.setTaskName(parentDO.getTaskName());
            taskQuestionVO.setCreateUserId(parentDO.getCreateUserId());
            taskQuestionVO.setCreateUserName(userMap.get(parentDO.getCreateUserId()));
            Long columnId = taskIdMetaColumnIdMap.get(parentDO.getId());
            if (columnId != null) {
                TbMetaStaTableColumnDO columnDO = metaColumnMap.get(columnId);
                if (columnDO != null) {
                    taskQuestionVO.setMetaColumName(columnDO.getColumnName());
                    taskQuestionVO.setCategoryName(columnDO.getCategoryName());
                    taskQuestionVO.setMetaTableName(tableMap.get(columnDO.getMetaTableId()));
                    taskQuestionVO.setSupportScore(columnDO.getSupportScore());
                    taskQuestionVO.setPunishMoney(columnDO.getPunishMoney());
                    taskQuestionVO.setAwardMoney(columnDO.getAwardMoney());
                    taskQuestionVO.setStandardPic(columnDO.getStandardPic());
                    taskQuestionVO.setDescription(columnDO.getDescription());
                }
            }
            TbDataStaTableColumnDO tableColumnDO = tbDataStaTableColumnMap.get(parentDO.getId());
            if (tableColumnDO != null) {
                taskQuestionVO.setCheckResult(tableColumnDO.getCheckResult());
                taskQuestionVO.setCheckResultName(tableColumnDO.getCheckResultName());
                String taskName = taskNameMap.get(tableColumnDO.getTaskId());
                if (StringUtils.isNotBlank(taskName)) {
                    taskQuestionVO.setTaskName(taskName);
                }
            }
            taskQuestionVO.setTaskDesc(parentDO.getTaskDesc());
            if (StringUtils.isNotBlank(parentDO.getTaskInfo())) {
                List<String> questionPictureList = JSONUtil.toList(JSONUtil.parseArray(JSONObject.parseObject(parentDO.getTaskInfo()).getString("photos")), String.class);
                if (CollectionUtils.isNotEmpty(questionPictureList)) {
                    taskQuestionVO.setQuestionPicture(String.join(",", questionPictureList));
                }
                String videos = JSONObject.parseObject(parentDO.getTaskInfo()).getString("videos");
                if (StringUtils.isNotBlank(videos)) {
                    taskQuestionVO.setQuestionVideo(videos);
                }
            }


            // validTime
            String validTime = DateUtils.convertTimeToString(parentDO.getBeginTime(), DATE_FORMAT_SEC_5) + "-"
                    + DateUtils.convertTimeToString(parentDO.getEndTime(), DATE_FORMAT_SEC_5);
            taskQuestionVO.setValidTime(validTime);
            String nodeNo = UnifyNodeEnum.FIRST_NODE.getCode();
            if (taskStoreHander != null) {
                String storeId = taskStoreHander.getStoreId();
                Long regionId = taskStoreHander.getRegionId();
                taskQuestionVO.setRegionName(regionMap.get(regionId));
                taskQuestionVO.setStoreName(storeMap.get(storeId).getStoreName());
                taskQuestionVO.setStoreNum(storeMap.get(storeId).getStoreNum());
                taskQuestionVO.setFullRegionName(fullRegionNameMap.get(storeId));


                nodeNo = taskStoreHander.getNodeNo();
                //节点为1判断下是否逾期
                if (UnifyNodeEnum.FIRST_NODE.getCode().equals(nodeNo)) {
                    taskQuestionVO.setExpireFlag(new Date().after(taskStoreHander.getSubEndTime()));
                }

            }
            taskQuestionVO.setStatus(nodeNo);
            TaskSubVO taskSubHander = taskSubMap.get(parentDO.getId() + "#" + UnifyNodeEnum.FIRST_NODE.getCode());
            if (taskSubHander != null) {
                taskQuestionVO.setHanderUserId(taskSubHander.getHandleUserId());
                taskQuestionVO.setHanderUserName(userMap.get(taskSubHander.getHandleUserId()));
                taskQuestionVO.setHandleOpinion(taskSubHander.getRemark());
                taskQuestionVO.setHandleActionyKey(taskSubHander.getFlowActionKey());
                if (taskSubHander.getHandleTime() != null) {
                    taskQuestionVO.setHandleTime(new Date(taskSubHander.getHandleTime()));
                }
                if (TaskStatusEnum.COMPLETE.getCode().equals(taskSubHander.getSubStatus())) {
                    taskQuestionVO.setExpireFlag(taskSubHander.getHandleTime() < taskSubHander.getSubEndTime());
                }
                if (taskQuestionVO.getRegionName() == null) {
                    String storeId = taskSubHander.getStoreId();
                    Long regionId = taskSubHander.getRegionId();
                    taskQuestionVO.setRegionName(regionMap.get(regionId));
                    taskQuestionVO.setStoreName(storeMap.get(storeId).getStoreName());
                    taskQuestionVO.setStoreNum(storeMap.get(storeId).getStoreNum());
                    taskQuestionVO.setFullRegionName(fullRegionNameMap.get(storeId));

                }
            }

            if (taskSubHander != null) {
                if (StringUtils.isNotBlank(taskSubHander.getTaskData())) {
                    List<String> dataPhotoList = JSONUtil.toList(JSONUtil.parseArray(JSONObject.parseObject(taskSubHander.getTaskData()).getString("photos")), String.class);
                    if (CollectionUtils.isNotEmpty(dataPhotoList)) {
                        taskQuestionVO.setHandlePicture(String.join(",", dataPhotoList));
                    }
                }
            }


            TaskSubVO taskSubApprove = taskSubMap.get(parentDO.getId() + "#" + UnifyNodeEnum.END_NODE.getCode());
            if (taskSubApprove == null) {
                taskSubApprove = taskSubMap.get(parentDO.getId() + "#" + UnifyNodeEnum.SECOND_NODE.getCode());
            }
            if (taskSubApprove != null) {
                taskQuestionVO.setApproveUserId(taskSubApprove.getHandleUserId());
                taskQuestionVO.setApproveUserName(userMap.get(taskSubApprove.getHandleUserId()));
                taskQuestionVO.setApproveOpinion(taskSubApprove.getRemark());
                taskQuestionVO.setApproveActionyKey(taskSubApprove.getFlowActionKey());
                if (taskSubApprove.getHandleTime() != null) {
                    taskQuestionVO.setApproveTime(new Date(taskSubApprove.getHandleTime()));
                }

                if (UnifyNodeEnum.END_NODE.getCode().equals(taskQuestionVO.getStatus())) {
                    taskQuestionVO.setCompleteTime(new Date(taskSubApprove.getHandleTime()));
                    taskQuestionVO.setTotalDurationTime(DateUtil.between(taskQuestionVO.getCreateTimeDate(), taskQuestionVO.getCompleteTime(), DateUnit.MS));
                }
            }
            taskQuestionList.add(taskQuestionVO);
        }
        pageInfo.setList(taskQuestionList);
        return pageInfo;
    }

    @Override
    public ImportTaskDO taskQuestionReportListExport(String enterpriseId, TaskQuestionQuery query) {
        Long count = taskParentMapper.selectTaskQuestionListCount(enterpriseId, TaskTypeEnum.QUESTION_ORDER.getCode(), query.getUserIdList(), query.getBeginTime(), query.getEndTime());
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }

        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.TASK_QUESTION_REPORT);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.TASK_QUESTION_REPORT);

        MsgUniteData msgUniteData = new MsgUniteData();

        ExportTaskQuestionRequest msg = new ExportTaskQuestionRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(query);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.TASK_QUESTION_REPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public UnifyParentStatisticsDTO getParentCount(String enterpriseId, String taskType, Boolean overdueTaskContinue) {
        UnifyParentStatisticsDTO dto = taskParentMapper.selectParentStatisticsCount(enterpriseId, taskType, overdueTaskContinue);
        return dto;
    }

    @Override
    public PageInfo selectParentTaskList(String enterpriseId, String userId, TaskParentQuery query) {
        Boolean adminIs = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> unifyTaskIds = new ArrayList<>();
        if (Constants.CREATE.equals(query.getType())){
            query.setUserIdList(Arrays.asList(userId));
        }else if (Constants.MANAGE.equals(query.getType())){
            if (!adminIs){
                unifyTaskIds = unifyTaskParentCollaboratorDao.selectByUserId(enterpriseId,userId,query.getTaskType());
            }
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<TaskParentDO> taskParentDOList = taskParentMapper.selectParentTaskList(enterpriseId, userId, query, adminIs, unifyTaskIds);
        //找协作人
        List<Long> unifyTaskIdList = taskParentDOList.stream().map(TaskParentDO::getId).collect(Collectors.toList());
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskParentCollaboratorDao.selectCollaboratorIdByTaskIdList(enterpriseId, unifyTaskIdList);
        if (CollectionUtils.isEmpty(taskParentDOList) || CollectionUtils.isEmpty(unifyPersonDTOS)){
            PageInfo pageInfo = new PageInfo<>(taskParentDOList);
            pageInfo.setList(getParentInfo(enterpriseId, taskParentDOList, userId));
            return pageInfo;
        }
        Map<Long, List<UnifyPersonDTO>> unifyTaskMap = unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getUnifyTaskId));

        for (TaskParentDO taskParentDO : taskParentDOList) {
            List<UnifyPersonDTO> unifyPersonDTO = unifyTaskMap.get(taskParentDO.getId());
            if (CollectionUtils.isEmpty(unifyPersonDTO)){
                log.info("taskParentDOList foreach 当前 object为空");
                continue;
            }
            List<String> collect = unifyPersonDTO.stream().map(UnifyPersonDTO::getUserId).collect(Collectors.toList());
            taskParentDO.setCollaboratorId(collect);
        }

        PageInfo pageInfo = new PageInfo<>(taskParentDOList);
        pageInfo.setList(getParentInfo(enterpriseId, taskParentDOList, userId));
        return pageInfo;
    }

    @Override
    public UnifyParentBuildDTO sendTaskToDayJudge(String enterpriseId, Long taskId) {
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        UnifyParentBuildDTO dto = new UnifyParentBuildDTO();
        dto.setUnifyTaskId(taskId);
        dto.setSendTask(false);
        if (taskParentDO == null) {
            return dto;
        }
        //非循环任务，不发起任务
        if (TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule())) {
            dto.setSendTask(false);
            return dto;
        }
        //任务不在有效期
        if (!(System.currentTimeMillis() >= taskParentDO.getBeginTime() && System.currentTimeMillis() <= taskParentDO.getEndTime())) {
            dto.setSendTask(false);
            return dto;
        }

        //判断今天是否生成任务
        Long startTimeToday = com.coolcollege.intelligent.common.util.DateUtil.getTodayTime(0);
        Long endTimeToday = com.coolcollege.intelligent.common.util.DateUtil.getTodayTime(24);
        //2.判断今天是否已经生成过门店任务
        Integer count = taskStoreDao.countByUnifyTaskIdAndTime(enterpriseId, taskId, startTimeToday, endTimeToday);
        //今日已生成任务，返回false，不在提示
        if (count != null && count > 0) {
            dto.setSendTask(false);
            return dto;
        }

        String runDate = taskParentDO.getRunDate() == null ? "" : taskParentDO.getRunDate();

        String[] runDates = runDate.split(",");
        //3.判断是否日期内
        boolean isContinue = true;
        if (TaskCycleEnum.WEEK.getCode().equals(taskParentDO.getTaskCycle())) {
            String s = com.coolcollege.intelligent.common.util.DateUtil.getWeek(System.currentTimeMillis());
            isContinue = Arrays.asList(runDates).contains(s);
        } else if (TaskCycleEnum.MONTH.getCode().equals(taskParentDO.getTaskCycle())) {
            String s = com.coolcollege.intelligent.common.util.DateUtil.getDate(System.currentTimeMillis());
            isContinue = Arrays.asList(runDates).contains(s);
        } else if (TaskCycleEnum.QUARTER.getCode().equals(taskParentDO.getTaskCycle())) {
            String s = DateUtils.convertTimeToString(System.currentTimeMillis(), DateUtils.DATE_FORMAT_DAY);
            isContinue = Arrays.asList(runDates).contains(s);
            log.info("季循环当天是否生成，runDates = {},currDates = {},isContinue = {}", runDates, s, isContinue);
        } else if (TaskCycleEnum.HOUR.getCode().equals(taskParentDO.getTaskCycle())) {
            isContinue = false;
        }
        //不应该再次执行
        if (!isContinue) {
            dto.setSendTask(false);
            return dto;
        }

        LocalTime now = LocalTime.now();

        LocalTime calendarTime = LocalTime.parse(taskParentDO.getCalendarTime() + ":00");
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), calendarTime);
        int limitInt = new Double(taskParentDO.getLimitHour() * 60).intValue();
        LocalDateTime localDateTimeEnd = localDateTime.plusMinutes(limitInt);
        LocalDateTime localDateTimeEndStart = LocalDateTime.now().plusMinutes(30);
        boolean sendTask = false;
        //如果执行时间在任务当天当前时间之之前，且没有超过30分钟则提示发起任务
        if (calendarTime.isBefore(now) && localDateTimeEndStart.isBefore(localDateTimeEnd)) {
            sendTask = true;
        }
        dto.setUnifyTaskId(taskId);
        dto.setSendTask(sendTask);
        return dto;
    }

    public List<TaskParentListVO> getParentInfo(String enterpriseId, List<TaskParentDO> taskParentDOList, String userId) {

        List<TaskParentListVO> taskList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(taskParentDOList)) {
            //获取数据列表
            List<Long> taskIds = new ArrayList<>();
            //单次循环任务id列表
            List<Long> loopTaskIds = new ArrayList<>();
            //userid列表
            List<String> userIds = new ArrayList<>();
            List<TaskProcessVO> processList = new ArrayList<>();
            Map<Long, TaskProcessVO> taskProcessMap = new HashMap<>();
            for (TaskParentDO taskParentDO : taskParentDOList) {
                taskIds.add(taskParentDO.getId());
                if (TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule())) {
                    loopTaskIds.add(taskParentDO.getId());
                }
                //流程信息处理
                List<TaskProcessVO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessVO.class);
                TaskProcessVO processVO = new TaskProcessVO();
                processVO.setUser(new ArrayList<>());
                processVO.setTaskId(taskParentDO.getId());
                // 节点配置信息组装
                if (CollectionUtils.isNotEmpty(process)) {
                    for (TaskProcessVO taskProcessVO : process) {
                        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(taskProcessVO.getNodeNo()) && CollectionUtils.isNotEmpty(taskProcessVO.getUser())) {
                            processVO.getUser().addAll(taskProcessVO.getUser());
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(processVO.getUser())) {
                    processList.add(processVO);
                }

                userIds.add(taskParentDO.getCreateUserId());
            }

            Map<Long, UnifyTaskStoreCount> taskCountMap = new HashMap<>();

            if (CollectionUtils.isNotEmpty(loopTaskIds)) {
                List<UnifyTaskStoreCount> taskCountList = taskStoreMapper.selectTaskCount(enterpriseId, loopTaskIds);
                if (CollectionUtils.isNotEmpty(taskCountList)) {
                    taskCountMap = taskCountList.stream().collect(Collectors.toMap(UnifyTaskStoreCount::getUnifyTaskId, Function.identity()));
                }
            }

            Map<Long, List<BasicsAreaDTO>> areaMap = dealNodeInfo(enterpriseId, taskIds);

            List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingData(enterpriseId, taskIds);
            Map<Long, List<UnifyFormDataDTO>> checkMap = formDataList.stream()
                    .collect(Collectors.groupingBy(UnifyFormDataDTO::getUnifyTaskId));


            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
            Map<String, String> userIdNameMap = CollectionUtils.emptyIfNull(userList).stream()
                    .filter(a -> a.getUserId() != null && a.getName() != null)
                    .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName, (a, b) -> a));

            taskProcessMap = dealTaskProcess(enterpriseId, processList);
            //DO转VO
            for (TaskParentDO item : taskParentDOList) {
                TaskParentListVO taskVO = new TaskParentListVO();
                BeanUtil.copyProperties(item, taskVO);
                taskVO.setCreateUserName(userIdNameMap.get(taskVO.getCreateUserId()));
                Boolean expireFlag = Boolean.FALSE;
                Boolean editFlag = Boolean.FALSE;
                if (item.getBeginTime() > System.currentTimeMillis()) {
                    taskVO.setParentStatus(UnifyStatus.NOSTART.getCode());
                    if (userId.equals(item.getCreateUserId())) {
                        editFlag = Boolean.TRUE;
                    }
                }
                if (item.getEndTime() < System.currentTimeMillis()) {
                    if (!UnifyStatus.COMPLETE.getCode().equals(item.getParentStatus())) {
                        expireFlag = Boolean.TRUE;
                    }
                    editFlag = Boolean.FALSE;
                }

                UnifyTaskStoreCount unifyTaskStoreCount = taskCountMap.get(item.getId());
                if (unifyTaskStoreCount != null) {
                    taskVO.setCompleteCount(unifyTaskStoreCount.getCompleteCount());
                    taskVO.setTotalCount(unifyTaskStoreCount.getTotalCount());
                    taskVO.setOngoingCount(unifyTaskStoreCount.getOngoingCount());
                    taskVO.setOngoingCountOve(unifyTaskStoreCount.getOngoingCountOve());
                } else {
                    taskVO.setCompleteCount(0L);
                    taskVO.setTotalCount(0L);
                    taskVO.setOngoingCount(0L);
                    taskVO.setOngoingCountOve(0L);
                }
                taskVO.setRunDate(item.getRunDate());
                taskVO.setExpireFlag(expireFlag);
                taskVO.setEditFlag(editFlag);
                taskVO.setFormData(checkMap.get(item.getId()));
                taskVO.setHanderProcess(taskProcessMap.get(item.getId()));
                List<BasicsAreaDTO> basicsStore = areaMap.get(item.getId());
                taskVO.setStoreList(basicsStore);
                taskVO.setTaskCycle(item.getTaskCycle());
                taskVO.setRunRule(item.getRunRule());
                taskVO.setStatusType(item.getStatusType());
                taskVO.setCollaboratorId(item.getCollaboratorId());
                if (Constants.SYSTEM_USER_ID.equals(taskVO.getCreateUserId())) {
                    taskVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
                }
                if(TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(item.getTaskType()) ||
                        TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(item.getTaskType())){
                    JSONObject jsonObject = JSON.parseObject(item.getTaskInfo());
                    String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
                    taskVO.setProductInfoDTOList(JSONObject.parseArray(taskInfoStr, ProductInfoDTO.class));
                }
                taskList.add(taskVO);
            }
        }
        return taskList;
    }

    private List<TbMetaTableDO> getTbMetaTableDOS(String enterpriseId, Long taskId) {
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "该任务不存在，任务id" + taskId);
        }
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList =
                taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);

        Set<Long> metaTableIds = unifyFormDataDTOList.stream()
                .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(metaTableIds)) {
            return Collections.emptyList();
        }
        if (UnifyTaskConstant.TaskType.QUESTION_ORDER.equals(taskParentDO.getTaskType())) {
            List<TbMetaStaTableColumnDO> columnList = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
            if (CollectionUtils.isEmpty(columnList)) {
                return Collections.emptyList();
            }
            metaTableIds = columnList.stream().map(data -> data.getMetaTableId()).collect(Collectors.toSet());
        }
        return tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
    }


    @Override
    public TaskMessageDTO getMessageBySubTaskId(String enterpriseId, Long subTaskId) {
        TaskSubDO subTask = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        if (ObjectUtil.isEmpty(subTask)) {
            return null;
        }
        TaskParentDO parentDO = taskParentMapper.selectParentTaskById(enterpriseId, subTask.getUnifyTaskId());
        if (ObjectUtil.isEmpty(parentDO)) {
            return null;
        }
        TaskMessageDTO taskMessage = new TaskMessageDTO(null, subTask.getUnifyTaskId(), parentDO.getTaskType(),
                subTask.getCreateUserId(), subTask.getCreateTime(), JSON.toJSONString(Lists.newArrayList(subTask)),
                enterpriseId, parentDO.getTaskInfo(), parentDO.getAttachUrl());
        return taskMessage;
    }

    /**
     * 发送消息
     *
     * @param taskMessage
     */
    @Async("taskExecutor")
    @Override
    public void sendTaskMessage(TaskMessageDTO taskMessage) {
        JSONObject taskObj = new JSONObject();
        taskObj.put(UnifyTaskConstant.TaskMessage.PRIMARY_KEY, UUIDUtils.get32UUID());
        taskObj.put(UnifyTaskConstant.TaskMessage.OPERATE_KEY, taskMessage.getOperate());
        taskObj.put(UnifyTaskConstant.TaskMessage.UNIFY_TASK_ID_KEY, taskMessage.getUnifyTaskId());
        taskObj.put(UnifyTaskConstant.TaskMessage.TASK_TYPE_KEY, taskMessage.getTaskType());
        taskObj.put(UnifyTaskConstant.TaskMessage.CREATE_USER_ID_KEY, taskMessage.getCreateUserId());
        taskObj.put(UnifyTaskConstant.TaskMessage.CREATE_TIME_KEY, taskMessage.getCreateTime());
        taskObj.put(UnifyTaskConstant.TaskMessage.DATA_KEY, taskMessage.getData());
        taskObj.put(UnifyTaskConstant.TaskMessage.ENTERPRISE_ID_KEY, taskMessage.getEnterpriseId());
        taskObj.put(UnifyTaskConstant.TaskMessage.TASK_INFO, taskMessage.getTaskInfo());
        taskObj.put(UnifyTaskConstant.TaskMessage.ATTACH_URL, taskMessage.getAttachUrl());
        taskObj.put(UnifyTaskConstant.TaskMessage.TASK_HANDLE_DATA_KEY, taskMessage.getTaskHandleData());
        taskObj.put(UnifyTaskConstant.TaskMessage.NODE_NO_KEY, taskMessage.getNodeNo());
        taskObj.put(UnifyTaskConstant.TaskMessage.TASK_PARENT_ITEM_ID, taskMessage.getTaskParentItemId());
        taskObj.put(UnifyTaskConstant.TaskMessage.QUESTION_RECORD_ID, taskMessage.getQuestionRecordId());
        taskObj.put(UnifyTaskConstant.TaskMessage.LOOP_COUNT, taskMessage.getLoopCount());
        taskObj.put(UnifyTaskConstant.TaskMessage.STORE_ID, taskMessage.getStoreId());
        taskObj.put(UnifyTaskConstant.TaskMessage.QUESTION_TYPE, taskMessage.getQuestionType());
        String taskType = taskMessage.getTaskType();
        RocketMqTagEnum rocketMqTagEnum = RocketMqTagEnum.UNIFY_TASK_PATROL;
        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskType)) {
            rocketMqTagEnum = RocketMqTagEnum.UNIFY_TASK_DISPLAY;
        } else if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskType)) {
            rocketMqTagEnum = RocketMqTagEnum.UNIFY_TASK_QUESTION;
        } else if (TaskTypeEnum.PATROL_STORE_INFORMATION.getCode().equals(taskType)) {
            rocketMqTagEnum = RocketMqTagEnum.INFORMATION_COMPLETION;
        } else if (TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(taskType) ||
                TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(taskType) ) {
            rocketMqTagEnum = RocketMqTagEnum.ACHIEVEMENT_PRODUCT_TASK;
        }
        //延时1秒
        simpleMessageService.send(taskObj.toString(), rocketMqTagEnum, System.currentTimeMillis() + 1000);
    }

    /**
     * ##taskStep31  子任务场景任务通过 PASS  3
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String completeSubTask(String enterpriseId, Long subTaskId, String remark, String taskData) {
        ValidateUtil.validateObj(subTaskId);
        TaskSubInfoVO oldSub = taskSubMapper.selectTaskBySubId(enterpriseId, subTaskId);
        Long time = System.currentTimeMillis();
        TaskSubDO subDO = TaskSubDO.builder()
                .id(subTaskId)
                .actionKey(UnifyTaskActionEnum.PASS.getCode())
                .handleTime(time)
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                .taskData(taskData)
                .remark(remark)
                .nodeNo(UnifyNodeEnum.END_NODE.getCode())
                .build();
        taskSubMapper.updateSubDetailById(enterpriseId, subDO);
        //同一批次同一节点的同一的更新为完成
        TaskSubDO queryDO = new TaskSubDO(oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getNodeNo(),
                oldSub.getGroupItem(), oldSub.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder()
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, subTaskId);
        changeParentStatus(enterpriseId, oldSub.getUnifyTaskId());
        //子任务发布广播消息-完成
        TaskSubDO subMessageDO = taskSubMapper.selectSubTaskById(enterpriseId, oldSub.getId());
        //跟新门店任务完成
        unifyTaskStoreService.updateTaskStoreDOBySubTask(enterpriseId, subMessageDO);
        List<TaskSubDO> list = Lists.newArrayList();
        list.add(subMessageDO);
        TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE, oldSub.getUnifyTaskId(), oldSub.getTaskType(),
                subMessageDO.getHandleUserId(), time, JSON.toJSONString(list), enterpriseId, oldSub.getTaskInfo(), null);
        taskMessage.setStoreId(subMessageDO.getStoreId());
        taskMessage.setLoopCount(subMessageDO.getLoopCount());
        log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
        sendTaskMessage(taskMessage);
        return oldSub.getNodeNo();
    }

    @Override
    public ResponseResult getResponseResult(String enterpriseId, String taskId) {
        TaskParentDO parentDO = taskParentMapper.selectTaskById(enterpriseId, Long.parseLong(taskId));
        if (parentDO == null) {
            return ResponseResult.success(Boolean.TRUE);
        }
        // 单次任务直接返回null
        String runRule = parentDO.getRunRule();
        Long now = System.currentTimeMillis();
        //1.判断结束时间
        if (now > parentDO.getEndTime()) {
            if (log.isInfoEnabled()) {
                log.info("该父任务已结束，enterpriseId = {},taskId = {}", enterpriseId, taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        Long startTime = com.coolcollege.intelligent.common.util.DateUtil.getTodayTime(0);
        Long endTime = com.coolcollege.intelligent.common.util.DateUtil.getTodayTime(24);
        //2.判断今天是否已经生成过门店任务
        Integer count = taskStoreDao.countByUnifyTaskIdAndTime(enterpriseId, parentDO.getId(), startTime, endTime);
        if (count > 0) {
            if (log.isInfoEnabled()) {
                log.info("该父任务的当天门店任务已生成，enterpriseId = {},taskId = {}", enterpriseId, taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        if (TaskRunRuleEnum.ONCE.getCode().equals(runRule)) {
            return null;
        }
        String taskCycle = parentDO.getTaskCycle();
        if (TaskCycleEnum.DAY.getCode().equals(taskCycle)) {
            return null;
        }
        String runDate = parentDO.getRunDate();
        if (StringUtils.isBlank(runDate)) {
            if (log.isInfoEnabled()) {
                log.info("该父任务循环方式为空，enterpriseId = {},taskId = {}", enterpriseId, taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        String[] runDates = runDate.split(",");
        //3.判断是否日期内
        Boolean isContinue = true;
        if (TaskCycleEnum.WEEK.getCode().equals(taskCycle)) {
            String s = com.coolcollege.intelligent.common.util.DateUtil.getWeek(now);
            // 数据库存的是7，后端是0
            if ("0".equals(s)) {
                s = "7";
            }
            String finalS = s;
            isContinue = Arrays.asList(runDates).stream().anyMatch(data -> data.equals(finalS));
        } else if (TaskCycleEnum.MONTH.getCode().equals(taskCycle)) {
            String s = com.coolcollege.intelligent.common.util.DateUtil.getDate(now);
            isContinue = Arrays.asList(runDates).stream().anyMatch(data -> data.equals(s));
        }
        if (!isContinue) {
            if (log.isInfoEnabled()) {
                log.info("该任务不在执行时间内，enterpriseId = {},taskId = {}", enterpriseId, taskId);
            }
            return ResponseResult.success(Boolean.TRUE);
        }
        return null;
    }

    private void changeParentStatus(String enterpriseId, Long unifyTaskId) {

        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的任务");
        }
        if ("LOOP".equals(taskParentDO.getRunRule())) {
            return;
        }

        //节点完成需要判断是否父任务下所有任务已完成
        // 若都完成，修改父任务为完成状态,采取匹配的方式，因为任务门店映射可被编辑
        List<String> endStoreList = taskSubMapper.selectEndStoreByTaskId(enterpriseId, unifyTaskId);
        if (CollectionUtils.isNotEmpty(endStoreList)) {
            List<String> allStoreList = taskSubMapper.selectAllStoreByTaskId(enterpriseId, unifyTaskId);
            Boolean updateFlag = Boolean.TRUE;
            for (String item : allStoreList) {
                if (!endStoreList.contains(item)) {
                    updateFlag = Boolean.FALSE;
                    break;
                }
            }
            if (updateFlag) {
                taskParentMapper.updateParentTaskById(enterpriseId, TaskParentDO.builder().parentStatus(UnifyStatus.COMPLETE.getCode()).build(),
                        unifyTaskId);
                //同时更新陈列父任务抄送表中的任务状态
                unifyTaskParentCcUserService.updateTaskParentStatus(enterpriseId, unifyTaskId, UnifyStatus.COMPLETE.getCode());
                unifyTaskParentCollaboratorDao.updateTaskParentStatus(enterpriseId, unifyTaskId, UnifyStatus.COMPLETE.getCode());
                //同时更父任务处理人关系映射表任务状态
                unifyTaskParentUserDao.updateParentStatusByUnifyTaskId(enterpriseId, unifyTaskId, UnifyStatus.COMPLETE.getCode());

                //陈列任务所有门店完成，发送消息
                try {
                    EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, 1);
                    if (enterpriseMqInformConfigDTO == null) {
                        log.info("企业未配置消息通知，enterpriseId={}", enterpriseId);
                        return;
                    }
                    if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
                        HashMap<Object, Object> map = Maps.newHashMap();
                        map.put("taskId", unifyTaskId);
                        sendMqMsg(enterpriseId, map, taskParentDO.getTaskType(), BailiInformNodeEnum.DISPLAY_TASK_ALL_STORES_COMPLETED.getCode());
                    }
                } catch (Exception e) {
                    log.info("陈列任务所有门店完成，发送消息失败，enterpriseId={},unifyTaskId={}", enterpriseId, unifyTaskId, e);
                }

            }
        }
    }


    /**
     * ##taskStep3 流程引擎，监听推动任务处理, 然后回推动putNextAboutData, ,##taskStep2 见
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTask(String corpId, WorkflowDealDTO flow, String enterpriseId, EnterpriseStoreCheckSettingDO checkSettingDO, String appType) {
        String beforeNode = flow.getBeforeNodeNo();
        TaskStoreDO taskStoreDetail = taskStoreDao.getTaskStoreDetail(enterpriseId, flow.getUnifyTaskId(), flow.getStoreId(), flow.getLoopCount());
        if(Objects.isNull(taskStoreDetail)){
            return;
        }
        if(!taskStoreDetail.getNodeNo().equals(beforeNode)){
            log.error("###sentTask error node is not equal,oldStoreTask={},flow={}", JSON.toJSONString(taskStoreDetail), JSON.toJSONString(flow));
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "error node is not equal");
        }
        String primaryKey = flow.getPrimaryKey();
        MqMessageDO init = mqMessageDAO.getMsgById(enterpriseId, primaryKey, MessageStatusEnums.TODO.getValue());
        if(Objects.isNull(init)){
            throw new ServiceException(ErrorCodeEnum.MESSAGE_NOT_FOUND);
        }
        Boolean endFlag = flow.getEndFlag();
        String action = flow.getActionKey();
        Boolean turnFlag = Boolean.FALSE;
        //修改原任务相关
        Long subTaskId = flow.getSubTaskId();
        ValidateUtil.validateObj(subTaskId);
        if (DisplayConstant.ActionKeyConstant.TURN.equals(action)) {
            turnFlag = Boolean.TRUE;
        }
        TaskSubInfoVO oldSub = taskSubMapper.selectTaskBySubId(enterpriseId, subTaskId);
        updateOldTask(enterpriseId, subTaskId, flow);
        //转交逻辑，不会涉及节点转移
        if (turnFlag) {
            turnTask(corpId, enterpriseId, subTaskId, flow, oldSub, appType);
            int result = mqMessageDAO.updateMsgStatus(enterpriseId, primaryKey, MessageStatusEnums.FINISH.getValue(), MessageStatusEnums.TODO.getValue());
            if(result == 0){
                throw new ServiceException(ErrorCodeEnum.MESSAGE_CONSUMED);
            }
            return;
        }
        if (!oldSub.getNodeNo().equals(beforeNode)) {
            log.error("###sentTask error node is not equal,oldSub={},flow={}", JSON.toJSONString(oldSub), JSON.toJSONString(flow));
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "error node is not equal");
        }
        long cycleCount = Objects.nonNull(flow.getCycleCount()) ? flow.getCycleCount() : Constants.INDEX_ONE;
        flow.setCycleCount(cycleCount);
        if(DisplayConstant.ActionKeyConstant.REJECT.equals(flow.getActionKey())){
            flow.setCycleCount(cycleCount + 1);
        }
        //结束是更新原任务并且判断父任务状态是否需要修改,其他节点都是对下一个节点的预操作
        putNextAboutData(corpId, enterpriseId, oldSub, flow, endFlag, action, checkSettingDO, appType);
        int result = mqMessageDAO.updateMsgStatus(enterpriseId, primaryKey, MessageStatusEnums.FINISH.getValue(), MessageStatusEnums.TODO.getValue());
        if(result == 0){
            throw new ServiceException(ErrorCodeEnum.MESSAGE_CONSUMED);
        }
    }

    /**
     * ##taskStep22  子任务场景任务通过 PASS  2
     * 转交
     *
     * @param enterpriseId
     * @param subTaskId
     * @param flow
     * @param oldSub
     */
    private void turnTask(String corpId, String enterpriseId, Long subTaskId, WorkflowDealDTO flow, TaskSubInfoVO oldSub, String appType) {
        String formUserId = flow.getTurnFromUserId();
        String toUserId = flow.getTurnToUserId();
        ValidateUtil.validateString(formUserId, formUserId);

        long createTime = System.currentTimeMillis();
        TaskSubDO turnSubDO = TaskSubDO.builder()
                .unifyTaskId(oldSub.getUnifyTaskId())
                .createUserId(oldSub.getCreateUserId())
                .createTime(createTime)
                .handleUserId(toUserId)
                .storeId(oldSub.getStoreId())
                .bizCode(flow.getBizCode())
                .cid(flow.getCid())
                .instanceId(oldSub.getInstanceId())
                .cycleCount(oldSub.getCycleCount())
                .nodeNo(oldSub.getNodeNo())
                .templateId(oldSub.getTemplateId())
                .subStatus(UnifyStatus.ONGOING.getCode())
                .flowState(UnifyTaskConstant.FLOW_INIT)
                .parentTurnSubId(subTaskId)
                .groupItem(oldSub.getGroupItem())
                .loopCount(oldSub.getLoopCount())
                .subTaskCode(StringUtils.join(oldSub.getUnifyTaskId(), Constants.MOSAICS, oldSub.getStoreId()))
                .taskData(oldSub.getTaskData())
                .subBeginTime(oldSub.getSubBeginTime())
                .subEndTime(oldSub.getSubEndTime())
                .storeArea(oldSub.getStoreArea())
                .taskType(oldSub.getTaskType())
                .storeName(oldSub.getStoreName())
                .regionId(oldSub.getRegionId())
                .handlerEndTime(oldSub.getHandlerEndTime())
                .build();
        taskSubMapper.insertTaskSub(enterpriseId, turnSubDO);
        addAboutTurnPeople(oldSub.getUnifyTaskId(), formUserId, toUserId, oldSub.getStoreId(), enterpriseId, oldSub.getNodeNo(), oldSub.getLoopCount());
        //工单转交添加待办关系人员列表
        if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(oldSub.getTaskType())) {
            questionParentUserMappingService.updateUserMapping(enterpriseId, oldSub.getUnifyTaskId()
                    , Collections.singletonList(turnSubDO.getHandleUserId()), Collections.singletonList(oldSub.getHandleUserId()));
        }
        String name = enterpriseUserDao.selectNameByUserId(enterpriseId, formUserId);
        String outBusinessId = null;
        if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())){
            outBusinessId = getCombineOutBusinessId(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getLoopCount(), oldSub.getNodeNo());
        }
        jmsTaskService.sendUnifyTaskJms(oldSub.getTaskType(), Arrays.asList(toUserId), turnSubDO.getNodeNo(), enterpriseId, oldSub.getStoreName(),
                turnSubDO.getId(), name, turnSubDO.getSubEndTime(), oldSub.getTaskName(), true, turnSubDO.getSubBeginTime(), oldSub.getStoreId(), outBusinessId, false, oldSub.getUnifyTaskId(),turnSubDO.getCycleCount());
        //子任务发布广播消息
        List<TaskSubDO> mqList = Lists.newArrayList();
        mqList.add(turnSubDO);
        Map<String, Object> mqMap = new HashMap<>();
        mqMap.put("oldSubTaskId", oldSub.getId());
        mqMap.put("newSubDOList", mqList);
        TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_TURN, oldSub.getUnifyTaskId(), oldSub.getTaskType(),
                formUserId, createTime, JSON.toJSONString(mqMap), enterpriseId, oldSub.getTaskInfo(), null);
        taskMessage.setStoreId(oldSub.getStoreId());
        taskMessage.setLoopCount(oldSub.getLoopCount());
        log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
        sendTaskMessage(taskMessage);

        //转交的时候处理待办 （审批的时候）
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", corpId);
        Long unifyTaskId = oldSub.getUnifyTaskId();
        List<Long> taskSubVOStream = new ArrayList<>();
        Set<String> cancelUserIds = new HashSet<>();
        List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, unifyTaskId, oldSub.getStoreId(), oldSub.getLoopCount());
        if (taskSubVOS == null) {
            log.info("转交===没有对应的子任务");
            return;
        }
        taskSubVOS.stream().filter(e -> e.getFlowNodeNo().equals(oldSub.getNodeNo()) && e.getStoreId().equals(oldSub.getStoreId()) && e.getParentTurnSubId() == null).forEach(u -> {
            taskSubVOStream.add(u.getSubTaskId());
            cancelUserIds.add(u.getHandleUserId());
        });
        //筛选与当前节点相同的子任务
        jsonObject.put("unifyTaskSubId", subTaskId);
        jsonObject.put("unifyTaskSubIdList", taskSubVOStream);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
        // 发新任务取消待办
        if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())) {
            cancelCombineUpcoming(enterpriseId, unifyTaskId, oldSub.getLoopCount(), oldSub.getStoreId(), oldSub.getNodeNo(), new ArrayList<>(cancelUserIds), corpId, appType);
        }

        log.info("转交 node UnifyTaskServiceImpl.putNextAboutData data send finish,param-->enterpriseId={}，corpId={}，unifyTaskSubId={}", enterpriseId, corpId, subTaskId);
        if(oldSub != null && UnifyNodeEnum.FIRST_NODE.getCode().equals(oldSub.getNodeNo()) && (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(oldSub.getTaskType())
                || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(oldSub.getTaskType())
                || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(oldSub.getTaskType())) ){
        CombineUpcomingCancelData combineUpcomingCancelData = new CombineUpcomingCancelData();
        combineUpcomingCancelData.setEnterpriseId(enterpriseId);
        combineUpcomingCancelData.setDingCorpId(corpId);
        combineUpcomingCancelData.setAppType(appType);
        combineUpcomingCancelData.setUnifyTaskId(oldSub.getUnifyTaskId());
        combineUpcomingCancelData.setLoopCount(oldSub.getLoopCount());
        combineUpcomingCancelData.setHandleUserId(oldSub.getHandleUserId());
        simpleMessageService.send(JSONObject.toJSONString(combineUpcomingCancelData), RocketMqTagEnum.COMBINE_UPCOMING_CANCEL_QUEUE, System.currentTimeMillis() + 10000);
        }
    }

    /**
     * 更新原任务
     *
     * @param enterpriseId
     * @param subTaskId
     * @param flow
     */
    private void updateOldTask(String enterpriseId, Long subTaskId, WorkflowDealDTO flow) {
        String action = flow.getActionKey();
        String taskData = flow.getTaskData();
        TaskSubDO subDO = TaskSubDO.builder()
                .id(subTaskId)
                .bizCode(flow.getBizCode())
                .cid(flow.getCid())
                .actionKey(action)
                .handleTime(System.currentTimeMillis())
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                .taskData(taskData)
                .build();
        //转交不生成处理单
        if (DisplayConstant.ActionKeyConstant.TURN.equals(action)) {
            subDO.setTurnUserId(flow.getTurnToUserId());
        }
        String remark = flow.getRemark();
        if (StringUtils.isEmpty(remark)) {
            remark = StringUtils.EMPTY;
        }
        subDO.setRemark(remark);
        if (flow.getEndFlag()) {
            //最后一个是节点转移的任务
            //不管会签，并签都使其任务变为结束节点
            subDO.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
        }
        log.info("###sentTask subDO={}", JSON.toJSONString(subDO));
        taskSubMapper.updateSubDetailById(enterpriseId, subDO);
    }

    /**
     * ##taskStep21  下一步
     * 对下一个节点人员设置和发起任务
     *
     * @param enterpriseId
     * @param oldSub
     * @param flow
     * @param endFlag
     * @param action
     */
    private void putNextAboutData(String corpId, String enterpriseId, TaskSubInfoVO oldSub, WorkflowDealDTO flow,
                                  Boolean endFlag, String action, EnterpriseStoreCheckSettingDO checkSettingDO, String appType) {
        String formUserId = flow.getCreateUserId();
        log.info("###sentTask endFlag={}", endFlag);
        long createTime = System.currentTimeMillis();
        if (endFlag) {
            //更新父任务状态
            changeParentStatus(enterpriseId, oldSub.getUnifyTaskId());
            //子任务发布广播消息-完成
            TaskSubDO subDO = taskSubMapper.selectSubTaskById(enterpriseId, oldSub.getId());
            log.info("flow----->" + JSONObject.toJSONString(flow));
            log.info("subDO----->" + JSONObject.toJSONString(subDO));

            //日清完成
            unifyTaskStoreService.updateTaskStoreDOBySubTask(enterpriseId, subDO);

            List<TaskSubDO> mqList = Lists.newArrayList();
            mqList.add(subDO);
            TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_COMPLETE, oldSub.getUnifyTaskId(), oldSub.getTaskType(),
                    formUserId, createTime, JSON.toJSONString(mqList), enterpriseId, oldSub.getTaskInfo(), null);
            taskMessage.setTaskHandleData(flow.getData());
            taskMessage.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
            taskMessage.setStoreId(subDO.getStoreId());
            taskMessage.setLoopCount(subDO.getLoopCount());
            log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
            sendTaskMessage(taskMessage);

            //结束节点待办取消
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("enterpriseId", enterpriseId);
            jsonObject.put("corpId", corpId);
            Long unifyTaskId = oldSub.getUnifyTaskId();
            List<Long> taskSubVOStream = new ArrayList<>();
            Set<String> cancelUserIds = new HashSet<>();
            List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, unifyTaskId, oldSub.getStoreId(), oldSub.getLoopCount());
            if (taskSubVOS == null) {
                log.info("===没有对应的子任务");
                return;
            }
            taskSubVOS.stream().filter(e -> e.getFlowNodeNo().equals(UnifyNodeEnum.END_NODE.getCode()) && e.getStoreId().equals(oldSub.getStoreId())).forEach(u -> {
                taskSubVOStream.add(u.getSubTaskId());
                cancelUserIds.add(u.getHandleUserId());
            });
            //筛选与当前节点相同的子任务
            if (taskSubVOStream != null) {
                taskSubVOStream.add(oldSub.getId());
            }
            jsonObject.put("unifyTaskSubId", flow.getSubTaskId());
            jsonObject.put("unifyTaskSubIdList", taskSubVOStream);
            jsonObject.put("appType", appType);
            simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
            // 发新任务取消待办
            if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())) {
                cancelCombineUpcoming(enterpriseId, unifyTaskId, oldSub.getLoopCount(), oldSub.getStoreId(), oldSub.getNodeNo(), new ArrayList<>(cancelUserIds), corpId, appType);
            }
            log.info("end001 node UnifyTaskServiceImpl.putNextAboutData data send finish,param-->enterpriseId={}，corpId={}，unifyTaskSubId={}", enterpriseId, corpId, oldSub.getUnifyTaskId());
            if(oldSub != null && UnifyNodeEnum.FIRST_NODE.getCode().equals(oldSub.getNodeNo()) && (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(oldSub.getTaskType())
                    || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(oldSub.getTaskType())
                    || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(oldSub.getTaskType())) ){
            CombineUpcomingCancelData combineUpcomingCancelData = new CombineUpcomingCancelData();
            combineUpcomingCancelData.setEnterpriseId(enterpriseId);
            combineUpcomingCancelData.setDingCorpId(corpId);
            combineUpcomingCancelData.setAppType(appType);
            combineUpcomingCancelData.setUnifyTaskId(oldSub.getUnifyTaskId());
            combineUpcomingCancelData.setLoopCount(oldSub.getLoopCount());
            combineUpcomingCancelData.setHandleUserId(oldSub.getHandleUserId());
            simpleMessageService.send(JSONObject.toJSONString(combineUpcomingCancelData), RocketMqTagEnum.COMBINE_UPCOMING_CANCEL_QUEUE, System.currentTimeMillis() + 10000);
            }
        } else {
            log.info("###sentTask action={}", action);
            Long cycle = flow.getCycleCount();
            //拒绝只给发起者人发任务
            if (UnifyTaskActionEnum.REJECT.getCode().equals(action)) {
                String flowNodeNo = flow.getNextNodeNo();
                if ((UnifyNodeEnum.FIRST_NODE.getCode().equals(flowNodeNo))) {//FIXME jeffrey 原有逻辑，拒绝发给处理人， 但是下载的逻辑，拒绝并且节点回退到第一个才发给处理人
                    Long groupItem = cycle + 1L;
                    String handUserId = unifyTaskStoreService.getTaskHandlerUserId(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), flow.getLoopCount());
                    List<String> handlerUserIds = Lists.newArrayList();
                    if (StringUtils.isEmpty(handUserId)) {
                        Map<String, List<String>> nodePersonMap = unifyTaskStoreService.selectTaskStorAllNodePerson(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount());
                        log.info("###sentTask nodePersonMap={}", JSON.toJSONString(nodePersonMap));
                        handlerUserIds.addAll(nodePersonMap.getOrDefault(flowNodeNo, Collections.emptyList()));
                    } else {
                        handlerUserIds.add(handUserId);
                    }
                    List<TaskSubDO> list = Lists.newArrayList();
                    boolean subFirst = true;
                    for (String handlerUserId : handlerUserIds) {
                        TaskSubDO newSubDO = TaskSubDO.builder()
                                .unifyTaskId(oldSub.getUnifyTaskId())
                                .createUserId(formUserId)
                                .createTime(createTime)
                                .handleUserId(handlerUserId)
                                .storeId(oldSub.getStoreId())
                                .templateId(oldSub.getTemplateId())
                                .bizCode(flow.getBizCode())
                                .cid(flow.getCid())
                                .nodeNo(flow.getNextNodeNo())
                                .subStatus(UnifyStatus.ONGOING.getCode())
                                .cycleCount(cycle)
                                .flowState(UnifyTaskConstant.FLOW_INIT)
                                .groupItem(groupItem)
                                .loopCount(oldSub.getLoopCount())
                                .subTaskCode(StringUtils.join(oldSub.getUnifyTaskId(), Constants.MOSAICS, oldSub.getStoreId()))
                                .taskData(flow.getTaskData())
                                .subBeginTime(oldSub.getSubBeginTime())
                                .subEndTime(oldSub.getSubEndTime())
                                .taskType(oldSub.getTaskType())
                                .storeArea(oldSub.getStoreArea())
                                .storeName(oldSub.getStoreName())
                                .regionId(oldSub.getRegionId())
                                .handlerEndTime(oldSub.getHandlerEndTime())
                                .build();
                        list.add(newSubDO);
                        if (subFirst) {
                            unifyTaskStoreService.updateTaskStoreDOBySubTask(enterpriseId, newSubDO);
                            subFirst = false;
                        }
                    }
                    taskSubMapper.batchInsertTaskSub(enterpriseId, list);
                    String name = enterpriseUserDao.selectNameByUserId(enterpriseId, formUserId);

                    List<String> userIds = CollStreamUtil.toList(list, TaskSubDO::getHandleUserId);
                    sendTaskJms(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getStoreName(), oldSub.getLoopCount(), flow.getNextNodeNo(),
                            oldSub.getTaskType(), userIds, list, name, oldSub.getTaskName(), oldSub.getSubBeginTime(), oldSub.getSubEndTime());

                    //子任务发布广播消息
                    TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_REJECT, oldSub.getUnifyTaskId(), oldSub.getTaskType(),
                            formUserId, createTime, JSON.toJSONString(list), enterpriseId, oldSub.getTaskInfo(), null);
                    taskMessage.setTaskHandleData(flow.getData());
                    taskMessage.setNodeNo(flow.getNextNodeNo());
                    taskMessage.setStoreId(oldSub.getStoreId());
                    taskMessage.setLoopCount(oldSub.getLoopCount());
                    log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
                    sendTaskMessage(taskMessage);
                } else {
                    //寻找当前节点审批人
                    Map<String, List<String>> nodePersonMap = unifyTaskStoreService.selectTaskStorAllNodePerson(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount());
                    log.info("###sentTask nodePersonMap={}", JSON.toJSONString(nodePersonMap));
                    Set<String> userIdSet = new HashSet<>(nodePersonMap.get(flow.getNextNodeNo()));
                    ValidateUtil.validateList(userIdSet);
                    //处理审批完成发布新节点个任务
                    sendNewNodeTask(enterpriseId, userIdSet, oldSub, flow, formUserId, checkSettingDO);
                    log.info("###sentTask sendNewNodeTask success");
                }
            } else if (UnifyTaskActionEnum.PASS.getCode().equals(action)) {
                //寻找当前节点审批人
                Map<String, List<String>> nodePersonMap = unifyTaskStoreService.selectTaskStorAllNodePerson(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount());
                log.info("###sentTask nodePersonMap={}", JSON.toJSONString(nodePersonMap));
                Set<String> userIdSet = new HashSet<>(nodePersonMap.get(flow.getNextNodeNo()));
                ValidateUtil.validateList(userIdSet);
                //处理审批完成发布新节点个任务
                sendNewNodeTask(enterpriseId, userIdSet, oldSub, flow, formUserId, checkSettingDO);
                log.info("###sentTask sendNewNodeTask success");
            } else {
                log.info("###sentTask action error");
            }
            //非结束节点待办取消
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("enterpriseId", enterpriseId);
            jsonObject.put("corpId", corpId);
            Long unifyTaskId = oldSub.getUnifyTaskId();
            List<Long> taskSubVOStream = new ArrayList<>();
            Set<String> cancelUserIds = new HashSet<>();
            List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, unifyTaskId, oldSub.getStoreId(), oldSub.getLoopCount());
            if (taskSubVOS == null) {
                log.info("===没有对应的子任务");
                return;
            }
            taskSubVOS.stream().filter(e -> e.getFlowNodeNo().equals(flow.getBeforeNodeNo()) && e.getStoreId().equals(oldSub.getStoreId())).forEach(u -> {
                taskSubVOStream.add(u.getSubTaskId());
                cancelUserIds.add(u.getHandleUserId());
            });
            //筛选与当前节点相同的子任务
            log.info("===>taskSubVOS={},====>taskSubVOStream={}", taskSubVOS.size(), taskSubVOStream.size());
            jsonObject.put("unifyTaskSubId", flow.getSubTaskId());
            jsonObject.put("unifyTaskSubIdList", taskSubVOStream);
            jsonObject.put("appType", appType);
            simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
            // 发新任务取消待办
            if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())) {
                cancelCombineUpcoming(enterpriseId, unifyTaskId, oldSub.getLoopCount(), oldSub.getStoreId(), oldSub.getNodeNo(), new ArrayList<>(cancelUserIds), corpId, appType);
            }
            log.info("Leaf002 node UnifyTaskServiceImpl.putNextAboutData data send finish,param-->enterpriseId={}，corpId={}，unifyTaskSubId={}", enterpriseId, corpId, flow.getSubTaskId());
            if(oldSub != null && UnifyNodeEnum.FIRST_NODE.getCode().equals(oldSub.getNodeNo()) && (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(oldSub.getTaskType())
                    || TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(oldSub.getTaskType())
                    || TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(oldSub.getTaskType())) ){
            CombineUpcomingCancelData combineUpcomingCancelData = new CombineUpcomingCancelData();
            combineUpcomingCancelData.setEnterpriseId(enterpriseId);
            combineUpcomingCancelData.setDingCorpId(corpId);
            combineUpcomingCancelData.setAppType(appType);
            combineUpcomingCancelData.setUnifyTaskId(oldSub.getUnifyTaskId());
            combineUpcomingCancelData.setLoopCount(oldSub.getLoopCount());
            combineUpcomingCancelData.setHandleUserId(oldSub.getHandleUserId());
            simpleMessageService.send(JSONObject.toJSONString(combineUpcomingCancelData), RocketMqTagEnum.COMBINE_UPCOMING_CANCEL_QUEUE, System.currentTimeMillis() + 10000);
            }
        }
    }


    private void sendMqMsg(String enterpriseId, Map map, String moduleType, String bizType) {
        try {
            //检查企业是否开启mq配置
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, EnterpriseStatusEnum.NORMAL.getCode());
            if (enterpriseMqInformConfigDTO != null) {
                log.info("企业开启了mq配置，发送消息");
                //mq发送签到消息
                JSONObject data = new JSONObject();
                data.put("enterpriseId", enterpriseId);
                //模块类型巡店
                data.put("moduleType", moduleType);
                //业务类型
                data.put("bizType", bizType);
                //时间戳
                data.put("timestamp", System.currentTimeMillis());
                //业务数据
                data.put("data", map);
                SendResult send = simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM, System.currentTimeMillis() + 3000);
                log.info("发送mq消息成功:{},{}", bizType, send);
            } else {
                log.info("企业没有开启mq配置，不发送消息");
            }
        } catch (Exception e) {
            log.error("发送mq消息失败:{}", bizType, e);
        }
    }

    /**
     * ##taskStep3 下一步
     * 发布新节点个任务
     *
     * @param enterpriseId
     * @param userIdSet
     * @param oldSub
     * @param flow
     * @param formUserId
     */
    private void sendNewNodeTask(String enterpriseId, Set<String> userIdSet, TaskSubInfoVO oldSub, WorkflowDealDTO flow,
                                 String formUserId, EnterpriseStoreCheckSettingDO checkSettingDO) {
        List<TaskSubDO> list = Lists.newArrayList();
        long createTime = System.currentTimeMillis();
        boolean subFirst = true;
        for (String item : userIdSet) {
            TaskSubDO newSubDO = TaskSubDO.builder()
                    .unifyTaskId(oldSub.getUnifyTaskId())
                    .createUserId(formUserId)
                    .createTime(createTime)
                    .handleUserId(item)
                    .storeId(oldSub.getStoreId())
                    .templateId(oldSub.getTemplateId())
                    .bizCode(flow.getBizCode())
                    .cid(flow.getCid())
                    .nodeNo(flow.getNextNodeNo())
                    .subStatus(UnifyStatus.ONGOING.getCode())
                    .cycleCount(flow.getCycleCount())
                    .flowState(UnifyTaskConstant.FLOW_INIT)
                    .groupItem(flow.getCycleCount())
                    .loopCount(oldSub.getLoopCount())
                    .subTaskCode(StringUtils.join(oldSub.getUnifyTaskId(), Constants.MOSAICS, oldSub.getStoreId()))
                    .taskData(flow.getTaskData())
                    .subBeginTime(oldSub.getSubBeginTime())
                    .subEndTime(oldSub.getSubEndTime())
                    .storeArea(oldSub.getStoreArea())
                    .taskType(oldSub.getTaskType())
                    .storeName(oldSub.getStoreName())
                    .regionId(oldSub.getRegionId())
                    .handlerEndTime(oldSub.getHandlerEndTime())
                    .build();
            list.add(newSubDO);
            if (subFirst) {
                unifyTaskStoreService.updateTaskStoreDOBySubTask(enterpriseId, newSubDO);
                subFirst = false;
            }
        }
        ;
        ValidateUtil.validateList(list);
        taskSubMapper.batchInsertTaskSub(enterpriseId, list);
        //企业配置确定审核或复检是否需要发消息
        Boolean messageSendFlag;
        if (UnifyTaskConstant.TaskType.QUESTION_ORDER.equals(oldSub.getTaskType())) {
            messageSendFlag = checkSettingDO.getProblemTickRemind();
        } else {
            messageSendFlag = checkSettingDO.getTaskRemind();
        }
        if (messageSendFlag && UnifyTaskConstant.SEND_MESSAGE_NODE.contains(flow.getNextNodeNo())) {
            //工作通知
            String name = enterpriseUserDao.selectNameByUserId(enterpriseId, formUserId);
            if (Constants.AI.equals(formUserId)) {
                name = Constants.AI;
            }
            String finalName = name;
            List<String> userIds = CollStreamUtil.toList(list, TaskSubDO::getHandleUserId);
            sendTaskJms(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getStoreName(), oldSub.getLoopCount(), flow.getNextNodeNo(),
                    oldSub.getTaskType(), userIds, list, finalName, oldSub.getTaskName(), oldSub.getSubBeginTime(), oldSub.getSubEndTime());
        }
        //子任务发布广播消息
        TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_PASS, oldSub.getUnifyTaskId(),
                oldSub.getTaskType(), formUserId, createTime, JSON.toJSONString(list), enterpriseId, oldSub.getTaskInfo(), null);
        taskMessage.setTaskHandleData(flow.getData());
        taskMessage.setNodeNo(flow.getNextNodeNo());
        taskMessage.setStoreId(oldSub.getStoreId());
        taskMessage.setLoopCount(oldSub.getLoopCount());
        log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
        sendTaskMessage(taskMessage);
    }

    /**
     * @param process
     * @param taskId
     * @param personList
     * @param storeList
     * @param enterpriseId
     * @param createUserId
     */
    @Override
    public void getPerson(List<TaskProcessDTO> process, Long taskId, List<TaskMappingDO> personList,
                          Set<String> storeList, String enterpriseId, String createUserId, String taskType, Boolean addCreateUser, Boolean userAuth) {
        storeList.forEach(storeId -> {
            //人员映射 初始节点添加创建人
            TaskMappingDO createUser = new TaskMappingDO(taskId, createUserId, storeId, UnifyNodeEnum.ZERO_NODE.getCode(), UnifyTaskConstant.ROLE_CREATE);
            personList.add(createUser);
        });
        //遍历各个节点找到节点对应的岗位的人，
        Map<String, Map<String, List<String>>> positionMap = Maps.newHashMap();
        List<TaskProcessDTO> newProcess = getNewProcess(process);
        newProcess.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            boolean isFilterUserAuth = Objects.isNull(userAuth) ? UnifyNodeEnum.FIRST_NODE.getCode().equals(proNode) : userAuth;
            List<GeneralDTO> proUserList = proItem.getUser();
            if (CollectionUtils.isNotEmpty(proUserList)) {
                List<String> positionList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.POSITION.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> nodePersonList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.PERSON.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> groupIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.USER_GROUP.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<String> regionIdList = proUserList.stream().filter(f -> UnifyTaskConstant.PersonType.ORGANIZATION.equals(f.getType()))
                        .map(GeneralDTO::getValue).collect(Collectors.toList());
                List<AuthStoreUserDTO> authStoreUserList = storeService.getStorePositionUserList(enterpriseId,new ArrayList<>(storeList), positionList, nodePersonList, groupIdList, regionIdList, createUserId, isFilterUserAuth);
                if (CollectionUtils.isNotEmpty(authStoreUserList)) {
                    Map<String, List<String>> storeUserMap = authStoreUserList.stream().collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList, (a, b) -> a));
                    positionMap.put(proNode, storeUserMap);
                }

            }
        });
        //log.info("###build Task person positionMap={}", JSON.toJSONString(positionMap));
        //遍历各个节点设置任务关联人
        for (TaskProcessDTO item : newProcess) {
            String node = item.getNodeNo();
            String taskRole;
            if (UnifyNodeEnum.CC.getCode().equals(node)) {
                taskRole = UnifyTaskConstant.ROLE_CC;
            } else {
                taskRole = UnifyTaskConstant.ROLE_APPROVAL;
            }
            List<GeneralDTO> userList = item.getUser();
            ValidateUtil.validateString(node);
            ValidateUtil.validateList(userList);
            List<TaskMappingDO> mappingDOList = Lists.newArrayList();
            Map<String, List<String>> nodeUserMap = positionMap.get(node);
            for (String storeId : storeList) {
                List<String> user = Lists.newArrayList();
                if (ObjectUtil.isNotEmpty(nodeUserMap) && CollectionUtils.isNotEmpty(nodeUserMap.get(storeId))) {
                    user.addAll(nodeUserMap.get(storeId));
                }
                /*if (CollectionUtils.isNotEmpty(userIdList)) {
                    user.addAll(userIdList);
                }*/
                //岗位加人都找不到人的情况下，设置当前节点处理人为创建者
                //抄送人无人情况按无人处理
                if (CollectionUtils.isEmpty(user) && addCreateUser != null && addCreateUser) {
                    if (TaskTypeEnum.PATROL_STORE_AI.getCode().equals(taskType)) {
                        TaskMappingDO templateVO = new TaskMappingDO(taskId, Constants.AI, storeId, node, taskRole);
                        mappingDOList.add(templateVO);
                    } else {
                        if (!UnifyNodeEnum.CC.getCode().equals(node)) {
                            TaskMappingDO templateVO = new TaskMappingDO(taskId, createUserId, storeId, node, taskRole);
                            mappingDOList.add(templateVO);
                        }
                    }
                } else {
                    Set<String> userSet = new HashSet(user);
                    userSet.forEach(userId -> {
                        TaskMappingDO templateVO = new TaskMappingDO(taskId, userId, storeId, node, taskRole);
                        mappingDOList.add(templateVO);
                    });
                }
            }
            if (CollectionUtils.isNotEmpty(mappingDOList)) {
                personList.addAll(mappingDOList);
            }
        }
        //log.info("###build Task person personList={}", JSON.toJSONString(personList));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void stopTaskRun(String enterpriseId, Long taskId) {
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        String scheduleId = taskParentDO.getScheduleId();
        if (StringUtils.isBlank(taskParentDO.getScheduleId())) {
            throw new ServiceException(ErrorCodeEnum.TASK_SCHEDULE_NOT_EXIST);
        }
        taskParentDO.setScheduleId(null);
        taskParentMapper.clearScheduleIdByTaskId(enterpriseId, taskId, System.currentTimeMillis());
        Boolean success = scheduleService.deleteSchedule(enterpriseId, scheduleId);
        if (!success) {
            log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, scheduleId);
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "定时调度器删除失败");
        }
    }

    /**
     * 关系映射
     *
     * @param list
     * @param taskId
     * @return
     */
    private List<TaskMappingDO> getMappingData(List<GeneralDTO> list, Long taskId) {
        List<TaskMappingDO> mappingDOList = Lists.newArrayList();
        list.forEach(e -> {
            TaskMappingDO templateVO = new TaskMappingDO(taskId, e.getValue(), e.getType(), e.getFilterRegionId());
            mappingDOList.add(templateVO);
        });
        return mappingDOList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delUnifyTask(String enterpriseId, Long unifyTaskId, String operateType) {
        String key = enterpriseId + "_" + unifyTaskId;
        String taskDelFlagKey = redisConstantUtil.getTaskDelFlagKey(key);
        if (StringUtils.isNotBlank(redisUtilPool.getString(taskDelFlagKey))) {
            if (OperateTypeEnum.UPDATE.getCode().equals(operateType)) {
                throw new ServiceException(ErrorCodeEnum.TASK_CAN_NOT_UPDATE);
            } else {
                throw new ServiceException(ErrorCodeEnum.TASK_CAN_NOT_DELETE);
            }
        }

        // 根据id获取父任务信息
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, unifyTaskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应陈列父任务");
        }
        // 删除父任务权限校验（只有创建人管理员可以删除）
        String currentUserId = UserHolder.getUser().getUserId();
        // 是否创建人
        boolean isCreateUser = currentUserId.equals(taskParentDO.getCreateUserId());
        // 是否管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, currentUserId);
        //是否协作人
        List<String> collaboratorIds = unifyTaskParentCollaboratorDao.selectCollaboratorIdByTaskId(enterpriseId, taskParentDO.getId());
        boolean isCollaborator = collaboratorIds.contains(currentUserId);
        //陈列任务除外
        if (!isCreateUser && !isAdmin && !isCollaborator && !TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
            throw new ServiceException(ErrorCodeEnum.NO_PERMISSION.getCode(), "非创建人或管理员，无删除权限");
        }
        // 删除数据映射表
        taskMappingMapper.delMappingByTaskId(enterpriseId, UnifyTableEnum.TABLE_DATA.getCode(), unifyTaskId);
        // 删除人员映射表
        taskMappingMapper.delMappingByTaskId(enterpriseId, UnifyTableEnum.TABLE_PERSON.getCode(), unifyTaskId);
        // 删除门店映射表
        taskMappingMapper.delMappingByTaskId(enterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), unifyTaskId);
        // 删除子任务表
        taskSubMapper.delSubTaskByTaskId(enterpriseId, unifyTaskId);
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            // 删除按人任务
            unifyTaskPersonService.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
        } else {
            //删除门店任务
            unifyTaskStoreService.delTaskStoreByParentTaskId(enterpriseId, unifyTaskId);
            //删除父任务与抄送人的映射关系
            unifyTaskParentCcUserService.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
            // 删除父任务与处理人的映射关系
            unifyTaskParentUserDao.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
        }
        // 删除父任务表
        taskParentMapper.delParentTaskByTaskId(enterpriseId, unifyTaskId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelUnifyTask(String enterpriseId, List<Long> unifyTaskIdList, String dingCorpId, String appType) {

        List<TaskSubVO> subIdList = taskSubMapper.selectSubTaskByTaskIdList(enterpriseId, unifyTaskIdList);
        unifyTaskIdList.forEach(a -> delUnifyTask(enterpriseId, a, OperateTypeEnum.DELETE.getCode()));
        if (CollectionUtils.isNotEmpty(subIdList)) {
            Map<Long, List<TaskSubVO>> collectMap = subIdList.stream()
                    .collect(Collectors.groupingBy(TaskSubVO::getUnifyTaskId));
            //子任务发布广播消息
            CurrentUser user = UserHolder.getUser();
            String userId = user.getUserId();
            for (Map.Entry<Long, List<TaskSubVO>> entry : collectMap.entrySet()) {
                List<TaskSubVO> itemList = entry.getValue();
                String taskType = itemList.get(0).getTaskType();
                List<String> handlerUserIds = itemList.stream().filter(o -> UnifyStatus.ONGOING.getCode().equals(o.getSubStatus()) && UnifyNodeEnum.FIRST_NODE.getCode().equals(o.getFlowNodeNo())).map(TaskSubVO::getHandleUserId).collect(Collectors.toList());
                List<String> approveUserIds = itemList.stream().filter(o -> UnifyStatus.ONGOING.getCode().equals(o.getSubStatus()) && UnifyNodeEnum.isApproveNode(o.getFlowNodeNo())).map(TaskSubVO::getHandleUserId).collect(Collectors.toList());
                Long unifyTaskId = entry.getKey();

                List<Long> subTaskIds = itemList.stream().map(TaskSubVO::getSubTaskId).collect(Collectors.toList());
                TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_DELETE, unifyTaskId,
                        taskType, userId, System.currentTimeMillis(), JSON.toJSONString(subTaskIds), enterpriseId, null, null);
                log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
                sendTaskMessage(taskMessage);
                if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskType)) {
                    TbQuestionParentInfoDO questionParentInfo = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
                    List<TbQuestionParentUserMappingDO> tbQuestionParentUserMapping = questionParentUserMappingDao.selectByQuestionParentByUnifyTaskId(enterpriseId, unifyTaskId);
                    List<String> ccUserIds = tbQuestionParentUserMapping.stream().filter(TbQuestionParentUserMappingDO::getIsCcUser).map(TbQuestionParentUserMappingDO::getHandleUserId).distinct().collect(Collectors.toList());
                    Map<SendUserTypeEnum, List<String>> sendUserIds = new HashMap<>();
                    sendUserIds.put(SendUserTypeEnum.CREATE_USER, new ArrayList<>(Arrays.asList(questionParentInfo.getCreateId())));
                    sendUserIds.put(SendUserTypeEnum.CC_USER, ccUserIds);
                    sendUserIds.put(SendUserTypeEnum.HANDLER_USER, handlerUserIds);
                    sendUserIds.put(SendUserTypeEnum.APPROVE_USER, approveUserIds);
                    String title = questionParentInfo.getQuestionName();
                    String content = AppTypeEnum.isQwType(appType) ? "工单【{0}】已被【$userName={1}$】删除，请知悉~" : "工单【{0}】已被【{1}】删除，请知悉~";
                    content = MessageFormat.format(content, questionParentInfo.getQuestionName(), UserHolder.getUser().getName());
                    jmsTaskService.sendDeleteQuestionReminder(enterpriseId, sendUserIds, title, content, questionParentInfo.getId(), null);
                }
                //移除进行中的任务待办
                List<Long> cancleSubTaskIds = subIdList.stream().filter(e -> UnifyStatus.ONGOING.getCode().equals(e.getSubStatus())).map(TaskSubVO::getSubTaskId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(cancleSubTaskIds)) {
                    cancelUpcoming(enterpriseId, subTaskIds, dingCorpId, appType);
                    // 发新任务取消待办
                    List<TaskSubVO> cancelCombineTaskSubList = subIdList.stream()
                            .filter(v -> UnifyStatus.ONGOING.getCode().equals(v.getSubStatus()) && TaskTypeEnum.isCombineNoticeTypes(v.getTaskType()))
                            .collect(Collectors.toList());
                    Map<Long, Map<Long, Map<String, List<TaskSubVO>>>> group = cancelCombineTaskSubList.stream()
                            .collect(Collectors.groupingBy(TaskSubVO::getUnifyTaskId,
                                    Collectors.groupingBy(TaskSubVO::getLoopCount,
                                            Collectors.groupingBy(TaskSubVO::getFlowNodeNo))));
                    group.forEach((taskId, group1) -> {
                        group1.forEach((loopCount, group2) -> {
                            group2.forEach((nodeNo, taskSubList) -> {
                                List<String> userIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getHandleUserId);
                                cancelCombineUpcoming(enterpriseId, unifyTaskId, loopCount, null, nodeNo, userIds, dingCorpId, appType);
                            });
                        });
                    });
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskParentDO changeUnifyTask(String enterpriseId, Long taskId, UnifyTaskBuildDTO task, CurrentUser user, String dingCorpId, String appType) {
        // 获取任务信息
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未找到对应的任务");
        }
        Set<String> chekTaskTypeSet = new HashSet<>();
        chekTaskTypeSet.add(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_ONLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_AI.getCode());
        //陈列任务单次开始后
        if (Objects.nonNull(task.getIsOperateOverdue())){
            String taskInfo = task.getTaskInfo();
            JSONObject jsonObject = JSON.parseObject(taskInfo);
            if(Objects.nonNull(task.getIsOperateOverdue())){
                jsonObject.put("isOperateOverdue",task.getIsOperateOverdue());
                String newTaskInfo = jsonObject.toJSONString();
                task.setTaskInfo(newTaskInfo);
            }
        }
        if (chekTaskTypeSet.contains(taskParentDO.getTaskType())
                && "ONCE".equals(taskParentDO.getRunRule()) && taskParentDO.getBeginTime() < System.currentTimeMillis()) {
            this.updateParentTask(enterpriseId, taskId, task, user);
            return taskParentDO;
        }
        if ("ONCE".equals(taskParentDO.getRunRule()) && taskParentDO.getBeginTime() < System.currentTimeMillis()) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "单次任务开始后不允许编辑");
        }
        if ("LOOP".equals(taskParentDO.getRunRule())) {
            this.updateParentTask(enterpriseId, taskId, task, user);
            return taskParentDO;
        }
        List<TaskSubVO> subIdList = taskSubMapper.selectSubTaskByTaskId(enterpriseId, taskId);
        // 根据父任务id删除相关数据
        delUnifyTask(enterpriseId, taskId, OperateTypeEnum.UPDATE.getCode());
        // 新建陈列任务（创建人，创建时间保持不变）
        // insertUnifyTask(enterpriseId, task, taskParentDO.getCreateUserId(), taskParentDO.getCreateTime());
        // unifyTaskFcade.insertUnifyTask(enterpriseId, task, user, taskParentDO.getCreateTime());
        if (CollectionUtils.isNotEmpty(subIdList)) {
            //子任务发布广播消息
            String userId = user.getUserId();
            List<Long> subTaskIds = subIdList.stream().map(TaskSubVO::getSubTaskId).collect(Collectors.toList());
            TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_DELETE, taskId,
                    taskParentDO.getTaskType(), userId, System.currentTimeMillis(), JSON.toJSONString(subTaskIds), enterpriseId, taskParentDO.getTaskInfo(), null);
            log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
            sendTaskMessage(taskMessage);

            //移除进行中的任务待办
            List<Long> cancleSubTaskIds = subIdList.stream().filter(e -> UnifyStatus.ONGOING.getCode().equals(e.getSubStatus())).map(TaskSubVO::getSubTaskId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cancleSubTaskIds)) {
                cancelUpcoming(enterpriseId, subTaskIds, dingCorpId, appType);
            }
        }
        return taskParentDO;
    }

    private void updateParentTask(String enterpriseId, Long taskId, UnifyTaskBuildDTO task, CurrentUser user) {
        if (task.getBeginTime() > task.getEndTime()) {
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "开始时间不能大于结束时间");
        }
        if (task.getEndTime() < System.currentTimeMillis()) {
            throw new ServiceException(ErrorCodeEnum.WORK_FLOW_ERROR.getCode(), "结束时间不能小于当前时间");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        TaskParentDO parentDO = TaskParentDO.builder()
                .id(taskId)
                .taskName(task.getTaskName())
                .endTime(task.getEndTime())
                .taskDesc(task.getTaskDesc())
                .runDate(task.getRunDate())
                .calendarTime(task.getCalendarTime())
                .limitHour(task.getLimitHour())
                .taskInfo(task.getTaskInfo())
                .attachUrl(task.getAttachUrl())
                .updateUserId(user.getUserId())
                .build();
        Set<String> chekTaskTypeSet = new HashSet<>();
        chekTaskTypeSet.add(TaskTypeEnum.TB_DISPLAY_TASK.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_ONLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode());
        chekTaskTypeSet.add(TaskTypeEnum.PATROL_STORE_AI.getCode());
        //陈列、巡店任务
        if (chekTaskTypeSet.contains(taskParentDO.getTaskType())) {
            //新增指派人员
            List<TaskProcessDTO> newReportList = task.getProcess().stream().filter(taskProcessDTO -> UnifyNodeEnum.NOTICE.getCode().equals(taskProcessDTO.getNodeNo())).collect(Collectors.toList());
            List<TaskProcessDTO> haveProcessList = JSONObject.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
            haveProcessList = haveProcessList.stream().filter(taskProcessDTO -> !UnifyNodeEnum.NOTICE.getCode().equals(taskProcessDTO.getNodeNo())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(newReportList)){
                haveProcessList.addAll(newReportList);
            }
            //新增指派人员
            if (CollectionUtils.isNotEmpty(task.getAddProcessList())) {
                haveProcessList.addAll((task.getAddProcessList()));
            }
            //对ProcessList进行分组处理
            haveProcessList = this.getNewProcess(haveProcessList);
            parentDO.setNodeInfo(JSON.toJSONString(haveProcessList));
            //新增指派门店范围
            if (CollectionUtils.isNotEmpty(task.getAddStoreList())) {
                //门店映射--按区域、分组、门店三种格式存
                List<TaskMappingDO> storeList = getMappingData(task.getAddStoreList(), taskId);
                Lists.partition(storeList, Constants.BATCH_INSERT_COUNT).forEach(partStoreList -> {
                    taskMappingMapper.insertTaskMapping(enterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), partStoreList);
                });
            }
            //插入协作人
            if (CollectionUtils.isNotEmpty(task.getCollaboratorIdList())) {
                unifyTaskParentCollaboratorDao.deleteByTaskId(enterpriseId, taskId);
                List<UnifyTaskParentCollaboratorDO> list = new ArrayList<>();
                task.getCollaboratorIdList().forEach(collaboratorId -> {
                    UnifyTaskParentCollaboratorDO unifyTaskParentCollaboratorDO = new UnifyTaskParentCollaboratorDO(null, parentDO.getId(), parentDO.getTaskName(),
                            task.getTaskType(), collaboratorId, UnifyStatus.ONGOING.getCode(), parentDO.getBeginTime(), parentDO.getEndTime());
                    list.add(unifyTaskParentCollaboratorDO);
                });
                unifyTaskParentCollaboratorDao.batchInsertOrUpdate(enterpriseId, list);
            }
            //单次已经开始的任务进行新增门店范围、或人员范围进行补发
            if (TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule())) {
                task.setEnterpriseId(enterpriseId);
                simpleMessageService.send(JSONObject.toJSONString(task), RocketMqTagEnum.TASK_REISSUE);
            }
        }
        parentDO.setUpdateTime(System.currentTimeMillis());
        taskParentMapper.updateParentByDO(enterpriseId, parentDO);
        parentDO.setTaskCycle(taskParentDO.getTaskCycle());
        parentDO.setBeginTime(taskParentDO.getBeginTime());
        //单次任务不需要修改定时器
        if (Constants.ONCE.equals(taskParentDO.getRunRule())) {
            return;
        }
        if (!setScheduler(enterpriseId, taskId, parentDO)) {
            log.error("定时调度器新增失败，enterpriseId={},task={}", enterpriseId, JSONObject.toJSONString(parentDO));
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "定时调度器新增失败");
        }
        Boolean success = scheduleService.deleteSchedule(enterpriseId, taskParentDO.getScheduleId());
        if (!success) {
            log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, parentDO.getScheduleId());
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "定时调度器删除失败");
        }
    }

    @Override
    public void turnTask(String enterpriseId, UnifyTaskTurnDTO task, CurrentUser user) {
        String userId = user.getUserId();
        String toUserId = task.getTurnUserId();
        Long subTaskId = task.getSubTaskId();
        String remark = Strings.isEmpty(task.getRemark()) ? Strings.EMPTY : task.getRemark();
        TaskSubInfoVO oldSub = taskSubMapper.selectTaskBySubId(enterpriseId, subTaskId);
        ValidateUtil.validateObj(oldSub);
        String formUserId = oldSub.getHandleUserId();
        if (!userId.equals(formUserId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "非当前任务处理人操作");
        }
        if (UnifyStatus.COMPLETE.getCode().equals(oldSub.getSubStatus())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务已被其他人操作");
        }
        //被转交人在审批节点中转交失败
        Map<String, List<UnifyPersonDTO>> personMapFromSubTask = getTaskPersonFromSubTask(enterpriseId, oldSub.getUnifyTaskId(), Collections.singletonList(oldSub.getStoreId()), oldSub.getLoopCount(), UnifyStatus.ONGOING.getCode());
        List<UnifyPersonDTO> unifyPersonDTOS = personMapFromSubTask.get(oldSub.getStoreId());
        Map<String, List<String>> unifyPersonDTOMap = ListUtils.emptyIfNull(unifyPersonDTOS)
                .stream().collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                        Collectors.mapping(UnifyPersonDTO::getUserId, Collectors.toList())));
        List<String> userList = unifyPersonDTOMap.get(oldSub.getNodeNo());
        if (CollectionUtils.isNotEmpty(userList) && userList.contains(toUserId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "转交人存在该任务");
        }
        String node = oldSub.getNodeNo();
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(node)) {
            //修改原任务已完成
            TaskSubDO subDO = TaskSubDO.builder()
                    .id(subTaskId)
                    .handleTime(System.currentTimeMillis())
                    .subStatus(UnifyStatus.COMPLETE.getCode())
                    .actionKey(DisplayConstant.ActionKeyConstant.TURN)
                    .turnUserId(toUserId)
                    .remark(remark)
                    .flowState(UnifyTaskConstant.FLOW_PROCESSED)
                    .build();
            taskSubMapper.updateSubDetailById(enterpriseId, subDO);
            //映射关系
            addAboutTurnPeople(oldSub.getUnifyTaskId(), formUserId, toUserId, oldSub.getStoreId(), enterpriseId, oldSub.getNodeNo(), oldSub.getLoopCount());
            long createTime = System.currentTimeMillis();
            TaskSubDO newSubDO = TaskSubDO.builder()
                    .unifyTaskId(oldSub.getUnifyTaskId())
                    .createUserId(oldSub.getCreateUserId())
                    .createTime(createTime)
                    .handleUserId(toUserId)
                    .storeId(oldSub.getStoreId())
                    .bizCode(oldSub.getBizCode())
                    .cid(oldSub.getCid())
                    .instanceId(oldSub.getInstanceId())
                    .cycleCount(oldSub.getCycleCount())
                    .nodeNo(oldSub.getNodeNo())
                    .templateId(oldSub.getTemplateId())
                    .subStatus(UnifyStatus.ONGOING.getCode())
                    .parentTurnSubId(subTaskId)
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
                    .isOperateOverdue(oldSub.getIsOperateOverdue())
                   .build();
            log.info("##cform_send_topic,newSubDO={}", JSON.toJSONString(newSubDO));
            taskSubMapper.insertTaskSub(enterpriseId, newSubDO);
            //工单转交添加待办关系人员列表
            if (TaskTypeEnum.QUESTION_ORDER.getCode().equals(oldSub.getTaskType())) {
                questionParentUserMappingService.updateUserMapping(enterpriseId, oldSub.getUnifyTaskId()
                        , Collections.singletonList(newSubDO.getHandleUserId()), Collections.singletonList(oldSub.getHandleUserId()));
            }
            //工作通知
            String name = enterpriseUserDao.selectNameByUserId(enterpriseId, formUserId);
            if (Constants.AI.equals(formUserId)) {
                name = Constants.AI;
            }
            if (StringUtils.isNotBlank(task.getRemark())){
                //不改动原有发送消息方法
                String redisKey = "transmitTask:" + enterpriseId + "_" + newSubDO.getId() + "_" + oldSub.getStoreId()+"_" + oldSub.getUnifyTaskId() + "_" + toUserId;
                String param = task.getRemark();
                redisUtilPool.setString(redisKey, JSONObject.toJSONString(param),RedisConstant.THREE_MINUTES);
            }
            String outBusinessId = null;
            if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())){
                outBusinessId = getCombineOutBusinessId(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getLoopCount(), newSubDO.getNodeNo());
            }
            jmsTaskService.sendUnifyTaskJms(oldSub.getTaskType(), Arrays.asList(toUserId),
                    newSubDO.getNodeNo(), enterpriseId,
                    oldSub.getStoreName(), newSubDO.getId(),
                    name, oldSub.getSubEndTime(),
                    oldSub.getTaskName(), true,
                    oldSub.getSubBeginTime(), oldSub.getStoreId(), outBusinessId,
                    false, oldSub.getUnifyTaskId(), newSubDO.getCycleCount());
            //子任务发布广播消息
            List<TaskSubDO> mqList = Lists.newArrayList();

            //填入转交原因
            if (StringUtils.isNotBlank(task.getRemark())){
                newSubDO.setContent("转交原因:"+task.getRemark());
            }
            mqList.add(newSubDO);
            Map<String, Object> mqMap = new HashMap<>();
            mqMap.put("oldSubTaskId", oldSub.getId());
            mqMap.put("newSubDOList", mqList);
            TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_TURN, oldSub.getUnifyTaskId(),
                    oldSub.getTaskType(), formUserId, createTime, JSON.toJSONString(mqMap), enterpriseId, oldSub.getTaskInfo(), null);
            taskMessage.setStoreId(oldSub.getStoreId());
            taskMessage.setLoopCount(oldSub.getLoopCount());
            log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
            sendTaskMessage(taskMessage);
            //转交的时候处理待办
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("enterpriseId", enterpriseId);
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            jsonObject.put("corpId", enterpriseConfigDO.getDingCorpId());
            Long unifyTaskId = oldSub.getUnifyTaskId();
            List<Long> taskSubVOStream = new ArrayList<>();
            List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, unifyTaskId, oldSub.getStoreId(), oldSub.getLoopCount());
            if (taskSubVOS == null) {
                log.info("转交===没有对应的子任务");
                return;
            }
            Set<String> cancelUserIds = new HashSet<>();
            taskSubVOS.stream().filter(e -> e.getFlowNodeNo().equals(node) && e.getStoreId().equals(oldSub.getStoreId()) && e.getParentTurnSubId() == null).forEach(u -> {
                taskSubVOStream.add(u.getSubTaskId());
                cancelUserIds.add(u.getHandleUserId());
            });
            //筛选与当前节点相同的子任务
            jsonObject.put("unifyTaskSubId", subTaskId);
            jsonObject.put("unifyTaskSubIdList", taskSubVOStream);
            jsonObject.put("appType", enterpriseConfigDO.getAppType());
            simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
            // 发新任务取消待办
            if (TaskTypeEnum.isCombineNoticeTypes(oldSub.getTaskType())) {
                cancelCombineUpcoming(enterpriseId, unifyTaskId, oldSub.getLoopCount(), oldSub.getStoreId(), oldSub.getNodeNo(), new ArrayList<>(cancelUserIds), enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
            }
            log.info("转交接口 node UnifyTaskServiceImpl.putNextAboutData data send finish,param-->enterpriseId={}，corpId={}，unifyTaskSubId={}", enterpriseId, enterpriseConfigDO.getDingCorpId(), oldSub.getParentTurnSubId());


        } else {
            TaskSubDO subDO = new TaskSubDO();
            BeanUtil.copyProperties(oldSub, subDO);
            TaskSubVO toUserTaskSubOld = taskSubMapper.getCompleteSubTaskByReallocate(enterpriseId, oldSub.getUnifyTaskId(), oldSub.getStoreId(), oldSub.getLoopCount(), toUserId, UnifyStatus.COMPLETE.getCode(), DisplayConstant.ActionKeyConstant.REALLOCATE, oldSub.getNodeNo());
            if (toUserTaskSubOld != null) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "您所选择的用户已被管理员重新分配，无法转交给他，请转交给其他人员或联系管理员处理。");
            }
            Boolean flag = workflowService.subSubmitCheck(subDO);
            if (flag == null || !flag) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务已被其他人操作");
            }
            // 发送流程引擎
            log.info("发送流程引擎 subDO={}", JSON.toJSONString(subDO));
            WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, Long.parseLong(oldSub.getCid()), subDO,
                    oldSub.getBizCode(), DisplayConstant.ActionKeyConstant.TURN, task, userId, task.getRemark(), null, null);
            mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), subDO.getId(), JSONObject.toJSONString(workflowDataDTO));
            simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
        }
    }

    @Override
    public List<Long> getOrSubTaskIds(String enterpriseId, Long subTaskId) {
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        if (taskSubDO == null) {
            return new ArrayList<>();
        }
        return taskSubMapper.selectOrSubTaskIds(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(),
                taskSubDO.getNodeNo(), taskSubDO.getGroupItem(), taskSubDO.getLoopCount());
    }

    @Override
    public String sendUnifyTaskDing(String enterpriseId, Long taskId, Boolean isSentMsg) {
        StringBuilder stringBuilder = new StringBuilder();
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务不存在");
        }
        if (!TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(taskParentDO.getTaskType())
                && !TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(taskParentDO.getTaskType())
                && !TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskParentDO.getTaskType())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务不是巡店任务");
        }

        String createUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, taskParentDO.getCreateUserId());
        String taskType = taskParentDO.getTaskType();

        List<TaskSubDO> subDOList = taskSubMapper.getTaskSubDOListForSend(enterpriseId, taskId, null, null, UnifyNodeEnum.FIRST_NODE.getCode());


        stringBuilder.append("taskId : ").append(taskId).append("\n");

        Set<String> storeIdSet = subDOList.stream().map(TaskSubDO::getStoreId).collect(Collectors.toSet());
        List<StoreDO> storeList = storeMapper.getByStoreIdList(enterpriseId, new ArrayList<>(storeIdSet));
        Map<String, String> storeMap = storeList.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));

        Set<String> hasStoreIdSet = new HashSet<>();

        for (TaskSubDO taskSubDO : subDOList) {
            String storeName = storeMap.get(taskSubDO.getStoreId());

            if (hasStoreIdSet.contains(taskSubDO.getStoreId())) {
                continue;
            }
            hasStoreIdSet.add(taskSubDO.getStoreId());
            log.warn("*##@@taskSubDO storeName:{} subTaskId:{} 进入发送", storeName, taskSubDO.getId());

            List<String> storeManage = new ArrayList<>();
            List<StoreUserDTO> storeUserDTOList = storeService.getStoreUserPositionList(enterpriseId, taskSubDO.getStoreId(), null, null, null, null);
            for (StoreUserDTO storeUserDTO : storeUserDTOList) {
                if ("50000000".equals(String.valueOf(storeUserDTO.getPositionId()))) {
                    storeManage.add(storeUserDTO.getUserId());
                }
            }

            //
            Set<String> handleUserId = new HashSet<>();
            if (CollectionUtils.isNotEmpty(storeManage)) {
                handleUserId.addAll(storeManage);
            } else {
                log.warn("&&##@@handleUserId  handleUserId:{} storeName:{} subTaskId:{} 放弃发送", JSON.toJSONString(handleUserId), storeName, taskSubDO.getId());
                continue;
            }


            if (CollectionUtils.isEmpty(handleUserId)) {
                log.warn("**##@@createUserId  handleUserId:{} storeName:{} subTaskId:{} 放弃发送", JSON.toJSONString(handleUserId), storeName, taskSubDO.getId());
                continue;
            }
            log.warn("///##@@taskSubDO taskId:{} storeName:{} subTaskId:{} , handleUserId{} 进入发送", taskId, storeName, taskSubDO.getId(), JSON.toJSONString(handleUserId));
            stringBuilder.append("storeId : ").append(taskSubDO.getStoreId()).append("\n");
            stringBuilder.append("storeName : ").append(storeName).append("\n");
            stringBuilder.append("handleUserId : ").append(JSON.toJSONString(handleUserId)).append("\n");
            stringBuilder.append("subTaskId : ").append(taskSubDO.getId()).append("\n");
            stringBuilder.append("taskName : ").append(taskParentDO.getTaskName()).append("\n");
            if (handleUserId.size() > 1) {
                log.warn("@@@##@@@@taskId:{} size > 1, subTaskId:{} , handleUserId{} ", taskId, taskSubDO.getId(), JSON.toJSONString(handleUserId));
            }
            if (isSentMsg != null && isSentMsg) {
                jmsTaskService.sendUnifyTaskJms(taskType, new ArrayList<>(handleUserId), taskSubDO.getNodeNo(), enterpriseId, storeName, taskSubDO.getId(), createUserName, taskSubDO.getSubEndTime(),
                        taskParentDO.getTaskName() + "(系统消息补发)", false, taskSubDO.getSubBeginTime(), taskSubDO.getStoreId(), null, false, taskSubDO.getUnifyTaskId(), taskSubDO.getCycleCount());
                log.warn("##@@taskId:{}, subTaskId{} 发送完成", taskId, taskSubDO.getId());
            }

            stringBuilder.append("subTaskId ").append(taskSubDO.getId()).append(": 发送完\n\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String sendUnifyTaskTestDing(String enterpriseId, Long taskId) {
        return sendUnifyTaskDing(enterpriseId, taskId, false);
    }

    @Override
    public void reissueDingNotice(String enterpriseId, Long taskId, String storeId, Long loopCount, Boolean isSentMsg) {

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务不存在");
        }
        if (!TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(taskParentDO.getTaskType())
                && !TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(taskParentDO.getTaskType())
                && !TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(taskParentDO.getTaskType())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务不是巡店任务");
        }

        String createUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, taskParentDO.getCreateUserId());
        String taskType = taskParentDO.getTaskType();
        //未完成子任务通知
        List<TaskSubDO> subDOList = taskSubMapper.getTaskSubDOListForSend(enterpriseId, taskId, storeId, loopCount, UnifyNodeEnum.FIRST_NODE.getCode());
        log.warn("@reissueDingNotice taskId : {} ", taskId);

        Set<String> storeIdSet = subDOList.stream().map(TaskSubDO::getStoreId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(storeIdSet)) {
            log.warn("@reissueDingNotice storeIdSet 不存在 : {} ", taskId);
            return;
        }
        List<StoreDO> storeList = storeMapper.getByStoreIdList(enterpriseId, new ArrayList<>(storeIdSet));
        Map<String, String> storeMap = storeList.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));

        for (TaskSubDO taskSubDO : subDOList) {
            String storeName = storeMap.get(taskSubDO.getStoreId());
            Set<String> handleUserId = new HashSet<>();
            handleUserId.add(taskSubDO.getHandleUserId());
            String outBusinessId = enterpriseId + "_" + taskSubDO.getId() + "_" + MD5Util.md5(JSONUtil.toJsonStr(handleUserId));
            log.warn("##@@reissueDingNotice  outBusinessId :{}", outBusinessId);
            log.warn("##@@reissueDingNotice taskId:{} storeName:{} subTaskId:{} , handleUserId{} outBusinessId:{} 进入发送",
                    taskId, storeName, taskSubDO.getId(), JSON.toJSONString(handleUserId), outBusinessId);
            if (isSentMsg != null && isSentMsg) {
                jmsTaskService.sendUnifyTaskJms(taskType, new ArrayList<>(handleUserId), taskSubDO.getNodeNo(), enterpriseId, storeName, taskSubDO.getId(), createUserName, taskSubDO.getSubEndTime(),
                        taskParentDO.getTaskName() + "(系统消息补发)", false, taskSubDO.getSubBeginTime(), taskSubDO.getStoreId(), null, false, taskSubDO.getUnifyTaskId(), taskSubDO.getCycleCount());
                log.warn("##@@reissueDingNotice over taskId:{}, subTaskId{} 发送完成", taskId, taskSubDO.getId());
            }
        }
        log.warn("@reissueDingNotice 发送完成 taskId : {} ", taskId);
    }


    @Override
    public void sendDingNotice(String enterpriseId, Long taskId, Long loopCount) {
        String key = enterpriseId + "_" + taskId + "_" + loopCount;
        String eidLockKey = redisConstantUtil.getTaskStageNoticeKey(key);
        if (StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
            throw new ServiceException("一天只能发送该阶段任务通知一次");
        }


        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该任务不存在");
        }

        String createUserName = enterpriseUserDao.selectNameByUserId(enterpriseId, taskParentDO.getCreateUserId());
        String taskType = taskParentDO.getTaskType();
        //未完成子任务通知
        List<TaskSubDO> subDOList = taskSubMapper.getTaskSubDOListForSend(enterpriseId, taskId, null, loopCount, null);

        Set<String> storeIdSet = subDOList.stream().map(TaskSubDO::getStoreId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(storeIdSet)) {
            throw new ServiceException("门店不存在");
        }
        List<StoreDO> storeList = storeMapper.getByStoreIdList(enterpriseId, new ArrayList<>(storeIdSet));
        Map<String, String> storeMap = storeList.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));

        for (TaskSubDO taskSubDO : subDOList) {
            String storeName = storeMap.get(taskSubDO.getStoreId());
            Set<String> handleUserId = new HashSet<>();
            handleUserId.add(taskSubDO.getHandleUserId());
            String outBusinessId = enterpriseId + "_" + taskSubDO.getId() + "_" + MD5Util.md5(JSONUtil.toJsonStr(handleUserId)) +
                    "_" + loopCount + "_" + DateUtils.convertTimeToString(System.currentTimeMillis(), DateUtils.DATE_FORMAT_DAY);
            log.warn("##@@sendDingNotice  outBusinessId :{}", outBusinessId);
            log.warn("##@@sendDingNotice taskId:{} storeName:{} subTaskId:{} , handleUserId{} outBusinessId:{} 进入发送",
                    taskId, storeName, taskSubDO.getId(), JSON.toJSONString(handleUserId), outBusinessId);

            jmsTaskService.sendUnifyTaskJms(taskType, new ArrayList<>(handleUserId), taskSubDO.getNodeNo(), enterpriseId, storeName, taskSubDO.getId(), createUserName, taskSubDO.getSubEndTime(),
                    taskParentDO.getTaskName(), false, taskSubDO.getSubBeginTime(), taskSubDO.getStoreId(), outBusinessId, false, taskSubDO.getUnifyTaskId(), taskSubDO.getCycleCount());
            log.warn("##@@sendDingNotice over taskId:{}, subTaskId{} 发送完成", taskId, taskSubDO.getId());

        }
        //加入锁  设置当前日有效
        redisUtilPool.setString(eidLockKey, enterpriseId, com.coolcollege.intelligent.common.util.DateUtil.getRemainSecondsOneDay(new Date()));
        log.warn("@sendDingNotice 发送完成 taskId : {} loopCount: {} ", taskId, loopCount);
    }

    @Override
    public List<ApproveDTO> taskSubList(String enterpriseId, String taskId, String storeId) {
        return taskSubMapper.taskSubList(enterpriseId, taskId, storeId);
    }

    /**
     * 查询指定任务id、门店id、轮次 各个节点的处理人、审批人
     *
     * @param enterpriseId
     * @param taskId
     * @param storeIds
     * @param loopCount
     * @return
     */
    @Override
    public Map<String, List<UnifyPersonDTO>> getTaskPersonFromSubTask(String enterpriseId, Long taskId, List<String> storeIds, Long loopCount, String subStatus) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return Maps.newHashMap();
        }
        // List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, Collections.singletonList(taskId), storeIds, loopCount);
        List<UnifyPersonDTO> unifyPersonDTOS = taskMappingMapper.getTaskPersonFromSubTask(enterpriseId, taskId
                , storeIds, loopCount, subStatus);
        List<String> userIdList = unifyPersonDTOS.stream()
                .map(unifyPersonDTO -> unifyPersonDTO.getUserId())
                .collect(Collectors.toList());
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);

        Map<String, EnterpriseUserDO> userMap = CollectionUtils.isNotEmpty(userDOList)
                ? userDOList.stream().collect(Collectors.toMap(s -> s.getUserId(), s -> s)) : Maps.newHashMap();

        unifyPersonDTOS.stream().forEach(unifyPersonDTO -> {
            EnterpriseUserDO enterpriseUserDO = userMap.get(unifyPersonDTO.getUserId());
            if(enterpriseUserDO!=null){
                unifyPersonDTO.setUserName(enterpriseUserDO.getName());
                unifyPersonDTO.setAvatar(enterpriseUserDO.getAvatar());
            }
        });
        Map<String, List<UnifyPersonDTO>> personMap = unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getStoreId));
        return personMap;
    }

    /**
     * 检查用户有没有指定任务、指定门店、指定轮次、指定节点的处理权限
     *
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @param node
     * @param userId
     * @return
     */
    @Override
    public boolean checkHasHandleAuth(String enterpriseId, Long taskId, String storeId, Long loopCount, String node, String userId) {
        List<String> storeIds = new ArrayList<String>();
        storeIds.add(storeId);
        Map<String, List<UnifyPersonDTO>> personMapFromSubTask = getTaskPersonFromSubTask(enterpriseId, taskId, storeIds, loopCount, null);
        List<UnifyPersonDTO> personFromSubTask = personMapFromSubTask.get(storeId);

        Map<String, List<String>> processUserFromSubTask = ListUtils.emptyIfNull(personFromSubTask).stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getNode,
                        Collectors.mapping(UnifyPersonDTO::getUserId, Collectors.toList())));
        // 处理人从子任务获取
        List<String> handUserIdList = processUserFromSubTask.get(node);
        if (CollectionUtils.isNotEmpty(handUserIdList) && handUserIdList.contains(userId)) {
            return true;
        }
        return false;
    }

    @Override
    public void turnStoreTask(String enterpriseId, UnifyStoreTaskTurnDTO task, CurrentUser user) {
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, task.getTaskStoreId());
        if (taskStoreDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店任务不存在，无法转交");
        }
        if (UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店任务已完成，无法转交");
        }
        TaskSubVO oldSub = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),
                user.getUserId(), UnifyStatus.ONGOING.getCode(), taskStoreDO.getNodeNo());
        if (oldSub == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "你不是该任务的处理人，无法转交");
        }
        UnifyTaskTurnDTO unifyTaskTurnDTO = new UnifyTaskTurnDTO();
        unifyTaskTurnDTO.setSubTaskId(oldSub.getSubTaskId());
        unifyTaskTurnDTO.setTurnUserId(task.getTurnUserId());
        unifyTaskTurnDTO.setRemark(task.getRemark());
        this.turnTask(enterpriseId, unifyTaskTurnDTO, user);
    }

    @Override
    public void taskReminder(String enterpriseId, ParentTaskReminderDTO param) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<Long> unifyTaskIds = param.getUnifyTaskIds();
        List<UnifyParentUser> taskUserList = taskSubMapper.selectUnCompleteUser(enterpriseId, unifyTaskIds, null, null, null, param.getStoreId(),
                storeCheckSettingDO.getOverdueTaskContinue(), storeCheckSettingDO.getHandlerOvertimeTaskContinue(), storeCheckSettingDO.getApproveOvertimeTaskContinue());
        if(CollectionUtils.isEmpty(taskUserList)){
            return;
        }
        List<TaskParentDO> taskParentList = taskParentMapper.selectParentTaskByTaskIds(enterpriseId, unifyTaskIds);
        if(CollectionUtils.isEmpty(taskParentList)){
            return;
        }
        List<String> createUsernameIds = new ArrayList<>();
        Map<Long, TaskParentDO> taskNameMap = new HashMap<>();
        taskParentList.forEach(o->{
            createUsernameIds.add(o.getCreateUserId());
            taskNameMap.put(o.getId(), o);
        });
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, createUsernameIds);
        taskUserList.forEach(o->{
            HashMap<String, String> paramMap = new HashMap<>();
            TaskParentDO taskParent = taskNameMap.get(o.getUnifyTaskId());
            if(Objects.isNull(taskParent)){
                return;
            }
            paramMap.put("taskName", taskParent.getTaskName());
            paramMap.put("createUserName", userNameMap.get(taskParent.getCreateUserId()));
            paramMap.put("handEndTime", DateUtils.convertTimeToString(o.getSubEndTime(), DateUtils.DATE_FORMAT_MINUTE));
            jmsTaskService.sendUnifyTaskReminder(enterpriseId, enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), o.getUnifyTaskId(), o.getSubTaskId(), o.getTaskType(), Arrays.asList(o.getUserId()), o.getLoopCount(), o.getNodeNo(), paramMap);
        });
    }


    @Override
    public List<UnifyStoreTaskBatchErrorDTO> batchTurnStoreTask(String enterpriseId, UnifyStoreTaskBatchTurnDTO task, CurrentUser user) {
        List<UnifyStoreTaskBatchErrorDTO> errorList = new ArrayList<>();
        task.getTaskStoreIdList().forEach(taskStoreId -> {
            UnifyStoreTaskTurnDTO unifyStoreTaskTurnDTO = new UnifyStoreTaskTurnDTO();
            unifyStoreTaskTurnDTO.setTaskStoreId(taskStoreId);
            unifyStoreTaskTurnDTO.setTurnUserId(task.getTurnUserId());
            unifyStoreTaskTurnDTO.setRemark(task.getRemark());
            try {
                this.turnStoreTask(enterpriseId, unifyStoreTaskTurnDTO, user);
            } catch (ServiceException e) {
                TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
                String storeName = taskStoreDO == null ? "" : taskStoreDO.getStoreName();
                UnifyStoreTaskBatchErrorDTO unifyStoreTaskBatchErrorDTO = new UnifyStoreTaskBatchErrorDTO();
                unifyStoreTaskBatchErrorDTO.setTaskStoreId(taskStoreId);
                unifyStoreTaskBatchErrorDTO.setStoreName(storeName);
                unifyStoreTaskBatchErrorDTO.setErrMsg(e.getErrorMessage());
                errorList.add(unifyStoreTaskBatchErrorDTO);
                log.info("reallocateStoreTask ServiceException", e);
            } catch (Exception e) {
                TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
                String storeName = taskStoreDO == null ? "" : taskStoreDO.getStoreName();
                UnifyStoreTaskBatchErrorDTO unifyStoreTaskBatchErrorDTO = new UnifyStoreTaskBatchErrorDTO();
                unifyStoreTaskBatchErrorDTO.setTaskStoreId(taskStoreId);
                unifyStoreTaskBatchErrorDTO.setStoreName(storeName);
                unifyStoreTaskBatchErrorDTO.setErrMsg("转交失败");
                errorList.add(unifyStoreTaskBatchErrorDTO);
                log.info("reallocateStoreTask error", e);
            }
        });
        return errorList;
    }

    /**
     * 任务重新分配
     *
     * @param enterpriseId
     * @param task
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reallocateStoreTask(String enterpriseId, ReallocateStoreTaskDTO task, String dingCorpId, CurrentUser user, String appType,
                                    Boolean isFill) {

        String redisKeyPrefix = RedisConstant.TASK_STORE_REALLOCATE;
        String taskStoreKey = redisKeyPrefix + "_" + enterpriseId + "_" + task.getTaskStoreId();
        //加两秒防止重复提交
        if (StringUtils.isNotBlank(redisUtilPool.getString(taskStoreKey))) {
            log.info("该门店任务已被重新分配，taskStoreId:" + task.getTaskStoreId());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请不要重复提交！");
        }
        redisUtilPool.setString(taskStoreKey, task.getTaskStoreId() + "", 2);

        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, task.getTaskStoreId());
        if (taskStoreDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店任务不存在，无法重新分配");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskStoreDO.getUnifyTaskId());
        if (UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店任务已完成，无法重新分配");
        }
        if (CollectionUtils.isNotEmpty(task.getCurrentNodeApproveUserList())) {
            if (UnifyNodeEnum.SECOND_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                task.setApproveUserList(task.getCurrentNodeApproveUserList());
            } else if (UnifyNodeEnum.THIRD_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                task.setRecheckUserList(task.getCurrentNodeApproveUserList());
            } else if (UnifyNodeEnum.FOUR_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                task.setThirdApproveUserList(task.getCurrentNodeApproveUserList());
            } else if (UnifyNodeEnum.FIVE_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                task.setFourApproveUserList(task.getCurrentNodeApproveUserList());
            } else if (UnifyNodeEnum.SIX_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                task.setFiveApproveUserList(task.getCurrentNodeApproveUserList());
            }
        }

        unifyTaskStoreService.fillSingleTaskStoreExtendAndCcInfo(enterpriseId, taskStoreDO);
        if (isFill) {
            // 节点人员校验
            checkBatchReallocateNodePerson(taskStoreDO, task.getHanderUserList(), task.getApproveUserList(), task.getRecheckUserList(),
                    task.getThirdApproveUserList(), task.getFourApproveUserList(), task.getFiveApproveUserList());
        } else {
            // 节点人员校验
            checkReallocateNodePerson(taskStoreDO, task.getHanderUserList(), task.getApproveUserList(), task.getRecheckUserList(),
                    task.getThirdApproveUserList(), task.getFourApproveUserList(), task.getFiveApproveUserList());
        }
        //最新一条，防止空指针
        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null, taskStoreDO.getNodeNo());

        Long createTime = System.currentTimeMillis();
        // 当前节点 需要 新增  移除  的人员
        Map<String, List<String>> currentNodePersonChangeMap = unifyTaskStoreService.getCurrentNodePersonChangeMap(enterpriseId, taskStoreDO,
                task.getHanderUserList(), task.getApproveUserList(), task.getRecheckUserList(), task.getThirdApproveUserList(),
                task.getFourApproveUserList(), task.getFiveApproveUserList());
        List<String> newaddPersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_NEWADD);
        List<String> removePersonList = currentNodePersonChangeMap.get(Constants.PERSON_CHANGE_KEY_REMOVE);
        // 重新分配节点人员
        List<Long> removeSubTaskIdList = reallocateStoreTaskPersonByNodeNew(enterpriseId, taskStoreDO, taskParentDO, newaddPersonList, removePersonList, createTime, user.getUserId(), taskSubVO, true);
        // 更新task_store 表
        unifyTaskStoreService.updateReallocateNodePerson(enterpriseId, taskStoreDO, task.getHanderUserList(), task.getApproveUserList(), task.getRecheckUserList(), task.getThirdApproveUserList(),
                task.getFourApproveUserList(), task.getFiveApproveUserList(), taskSubVO, newaddPersonList);
        // 保存父任务处理人关系映射
        this.saveTaskParentUser(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getTaskType()
                , Stream.of(ListUtils.emptyIfNull(task.getHanderUserList()), ListUtils.emptyIfNull(task.getApproveUserList())
                        , ListUtils.emptyIfNull(task.getRecheckUserList()), ListUtils.emptyIfNull(task.getThirdApproveUserList())
                        , ListUtils.emptyIfNull(task.getFiveApproveUserList()), ListUtils.emptyIfNull(task.getFiveApproveUserList()))
                        .flatMap(List::stream).collect(Collectors.toList()));
        // 取消待办
        if (CollectionUtils.isNotEmpty(removeSubTaskIdList)) {
            cancelUpcoming(enterpriseId, removeSubTaskIdList, dingCorpId, appType);
            // 发新任务取消待办
            cancelCombineUpcoming(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getLoopCount(), taskStoreDO.getStoreId(), taskStoreDO.getNodeNo(), removePersonList, dingCorpId, appType);
        }
        //发送分配日志
        sendReallocateLogMsg(enterpriseId, taskStoreDO, task, user.getUserId(), taskParentDO, newaddPersonList, removePersonList);
    }

    @Override
    public List<UnifyStoreTaskBatchErrorDTO> batchReallocateStoreTask(String enterpriseId, ReallocateStoreTaskListDTO task, String dingCorpId, CurrentUser user, String appType) {
        List<UnifyStoreTaskBatchErrorDTO> errorList = new ArrayList<>();
        if (CollectionUtils.isEmpty(task.getStoreTaskIdList()) && CollectionUtils.isNotEmpty(task.getQuestionRecordIdList())) {
            // 根据工单记录id查询门店任务id
            List<TbQuestionRecordDO> tbQuestionRecordDOS = tbQuestionRecordMapper.selectByIds(enterpriseId, task.getQuestionRecordIdList());
            List<Long> taskStoreIds = CollStreamUtil.toList(tbQuestionRecordDOS, TbQuestionRecordDO::getTaskStoreId);
            task.setStoreTaskIdList(taskStoreIds);
        }
        task.getStoreTaskIdList().forEach(taskStoreId -> {
            try {
                task.setTaskStoreId(taskStoreId);
                reallocateStoreTask(enterpriseId, task, dingCorpId, user, appType, Boolean.TRUE);
            } catch (ServiceException e) {
                TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
                String storeName = taskStoreDO == null ? "" : taskStoreDO.getStoreName();
                UnifyStoreTaskBatchErrorDTO unifyStoreTaskBatchErrorDTO = new UnifyStoreTaskBatchErrorDTO();
                unifyStoreTaskBatchErrorDTO.setTaskStoreId(taskStoreId);
                unifyStoreTaskBatchErrorDTO.setStoreName(storeName);
                unifyStoreTaskBatchErrorDTO.setErrMsg(e.getErrorMessage());
                errorList.add(unifyStoreTaskBatchErrorDTO);
                log.info("batchReallocateStoreTask ServiceException", e);
            } catch (Exception e) {
                TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
                String storeName = taskStoreDO == null ? "" : taskStoreDO.getStoreName();
                UnifyStoreTaskBatchErrorDTO unifyStoreTaskBatchErrorDTO = new UnifyStoreTaskBatchErrorDTO();
                unifyStoreTaskBatchErrorDTO.setTaskStoreId(taskStoreId);
                unifyStoreTaskBatchErrorDTO.setStoreName(storeName);
                unifyStoreTaskBatchErrorDTO.setErrMsg("转交失败");
                errorList.add(unifyStoreTaskBatchErrorDTO);
                log.info("batchReallocateStoreTask error", e);
            }
        });
        return errorList;
    }

    @Override
    public boolean hasCheckTableAuth(String enterpriseId, Long unifyTaskId, String userId) {
        // 检查表ids
        List<TbMetaTableDO> tbMetaTableList = getTbMetaTableDOS(enterpriseId, unifyTaskId);
        if (CollectionUtils.isEmpty(tbMetaTableList)) {
            return true;
        }
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if(isAdmin){
            return true;
        }
        List<Long> metaTableIds = tbMetaTableList.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaTableUserAuthDO> tableAuthList = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, metaTableIds);
        Map<String, TbMetaTableUserAuthDO> authMap = tableAuthList.stream().collect(Collectors.toMap(TbMetaTableUserAuthDO::getBusinessId, item -> item, (v1, v2)->v1));
        for (TbMetaTableDO tbMetaTableDO : tbMetaTableList) {
            if (UserRangeTypeEnum.ALL.getType().equals(tbMetaTableDO.getUseRange())) {
                return true;
            }
            TbMetaTableUserAuthDO tbMetaTableUserAuthDO = authMap.get(tbMetaTableDO.getId());
            boolean canSee = Optional.ofNullable(tbMetaTableUserAuthDO).map(TbMetaTableUserAuthDO::getUseAuth).orElse(false);
            boolean isMetaCreater = userId.equals(tbMetaTableDO.getCreateUserId());
            if (canSee || isMetaCreater) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Long> reallocateStoreTaskPersonByNodeNew(String enterpriseId, TaskStoreDO taskStoreDO, TaskParentDO parentDO, List<String> newaddPersonList, List<String> removePersonList, Long createTime, String operUserId, TaskSubVO taskSubVO, boolean sendNotice) {
        Map<String, Object> result = reallocateStoreTask(enterpriseId, taskStoreDO, parentDO, newaddPersonList, removePersonList, createTime, operUserId, taskSubVO, sendNotice);
        return (List<Long>) result.get(Constants.REMOVE_SUB_TASK_ID_LIST);
    }

    @Override
    public Map<String, Object> reallocateStoreTask(String enterpriseId, TaskStoreDO taskStoreDO, TaskParentDO parentDO, List<String> newaddPersonList, List<String> removePersonList, Long createTime, String operUserId, TaskSubVO taskSubVO, boolean sendNotice) {
        Map<String, Object> result = new HashMap<>();
        // 处理移除的逻辑 修改原任务已完成
        if (CollectionUtils.isNotEmpty(removePersonList)) {
            List<Long> removeSubTaskIdList = taskSubMapper.getRemoveSubTaskIdList(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), removePersonList, UnifyStatus.ONGOING.getCode(), taskStoreDO.getNodeNo());
            if (CollectionUtils.isNotEmpty(removeSubTaskIdList)) {
                taskSubMapper.updateNeedRemoveSubTaskStatus(enterpriseId, removeSubTaskIdList, System.currentTimeMillis(), UnifyStatus.COMPLETE.getCode(), DisplayConstant.ActionKeyConstant.REALLOCATE, UnifyTaskConstant.FLOW_PROCESSED);
            }
            result.put(Constants.REMOVE_SUB_TASK_ID_LIST, removeSubTaskIdList);
            result.put(Constants.REMOVE_USER_ID_LIST, removePersonList);
        }
        // 处理新增的逻辑
        if (CollectionUtils.isNotEmpty(newaddPersonList)) {
            List<TaskSubDO> subDOList = Lists.newArrayList();
            StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, taskStoreDO.getStoreId());
            for (String newaddUserId : newaddPersonList) {
                TaskSubDO subDO = TaskSubDO.builder()
                        .unifyTaskId(taskStoreDO.getUnifyTaskId())
                        .createUserId(parentDO.getCreateUserId())
                        .createTime(createTime)
                        .handleUserId(newaddUserId)
                        .storeId(taskStoreDO.getStoreId())
                        .nodeNo(taskStoreDO.getNodeNo())
                        .subStatus(UnifyStatus.ONGOING.getCode())
                        .flowState(UnifyTaskConstant.FLOW_INIT)
                        .groupItem(1L)
                        .loopCount(taskStoreDO.getLoopCount())
                        .subTaskCode(StringUtils.join(taskStoreDO.getUnifyTaskId(), Constants.MOSAICS, taskStoreDO.getStoreId()))
                        .subBeginTime(taskStoreDO.getSubBeginTime().getTime())
                        .subEndTime(taskStoreDO.getSubEndTime().getTime())
                        .taskType(parentDO.getTaskType())
                        .storeArea(storeDO.getRegionPath())
                        .regionId(storeDO.getRegionId())
                        .storeName(storeDO.getStoreName())
                        .handlerEndTime(taskStoreDO.getHandlerEndTime())
                        .instanceId(taskSubVO.getFlowInstanceId())
                        .bizCode(taskSubVO.getBizCode())
                        .cid(taskSubVO.getCid())
                        .cycleCount(taskSubVO.getFlowCycleCount())
                        .isOperateOverdue(Boolean.TRUE.equals(taskSubVO.isOperateOverdue()) ? "1" : "0")
                        .build();
                subDOList.add(subDO);
            }
            result.put(Constants.ADD_SUB_TASK_LIST, subDOList);

            //插入子任务表
            Lists.partition(subDOList, Constants.BATCH_INSERT_COUNT).forEach(partSubDOList -> {
                taskSubMapper.batchInsertTaskSub(enterpriseId, partSubDOList);
            });
            //工作通知
            String name = enterpriseUserDao.selectNameByUserId(enterpriseId, parentDO.getCreateUserId());
            if (Constants.SYSTEM_USER_ID.equals(parentDO.getCreateUserId())) {
                name = Constants.SYSTEM_USER_SEND_NAME;
            }
            if (Constants.AI.equals(parentDO.getCreateUserId())) {
                name = Constants.AI;
            }
            String finalName = name;
            if (sendNotice) {
                if (TaskTypeEnum.isCombineNoticeTypes(parentDO.getTaskType())) {
                    sendTaskJms(enterpriseId, parentDO, taskStoreDO, subDOList, false, null, null);
                } else {
                    subDOList.forEach(item ->
                            jmsTaskService.sendUnifyTaskJms(parentDO.getTaskType(), Arrays.asList(item.getHandleUserId()), taskStoreDO.getNodeNo(),
                                    enterpriseId, storeDO.getStoreName(), item.getId(), finalName, item.getSubEndTime(),
                                    parentDO.getTaskName(), false, item.getSubBeginTime(), item.getStoreId(), null, false, item.getUnifyTaskId(), item.getCycleCount()));
                }
            }
        }

        return result;
    }


    @Override
    public List<GeneralDTO> productDeal(String enterpriseId,UnifyTaskBuildDTO task) {
        List<GeneralDTO> storeIds = task.getStoreIds();
        List<String> region = storeIds.stream().filter(type -> type.getType().equals("region")).map(GeneralDTO::getValue).collect(Collectors.toList());
        String productNo = task.getProductNo();
        InventoryStoreDataRequest request = new InventoryStoreDataRequest();
        List<String> productList = new ArrayList<>();
        productList.add(productNo);
        request.setProductNo(productList);
        List<InventoryStoreDataDTO> inventoryStoreData = productFeedbackService.getInventoryStoreData(enterpriseId, request);
        List<String> storeNewNo = inventoryStoreData.stream().map(InventoryStoreDataDTO::getStore_new_no).collect(Collectors.toList());

        List<GeneralDTO> temp = new ArrayList<>();
        for (String regionId : region) {
            List<RegionDO> store = regionMapper.getStoreByParentIds(enterpriseId, regionId);
            List<String> storeIdList = store.stream().map(RegionDO::getStoreId).collect(Collectors.toList());
            List<StoreDO> storeEntity = storeMapper.getStoreByStoreIdList(enterpriseId, storeIdList);
            List<StoreDO> newEntity = storeEntity.stream().filter(o -> storeNewNo.contains(o.getStoreNum())).collect(Collectors.toList());
            for (StoreDO storeDO : newEntity) {
                GeneralDTO generalDTO = new GeneralDTO();
                generalDTO.setValue(storeDO.getStoreId());
                generalDTO.setType("store");
                temp.add(generalDTO);
            }
        }
        return temp;
    }

    @Override
    public TaskParentDO getByExtraParam(String enterpriseId, String extraParam) {
        return taskParentDao.getByExtraParam(enterpriseId, extraParam);
    }

    @Override
    public int updateQuestionRecordFinish(String enterpriseId,Date approveTime, String approveUserId, String approveUserName, String approveActionKey,
                                          Date handleTime,String handleUserId, String handleUserName,String handleActionKey,Long unifyTaskId) {
        List<TaskStoreDO> taskStoreDOS = taskStoreMapper.listByUnifyTaskId(enterpriseId, unifyTaskId);
        List<Long> taskStoreIdList = taskStoreDOS.stream().map(TaskStoreDO::getId).collect(Collectors.toList());
        for (TaskStoreDO taskStore : taskStoreDOS) {
            taskStore.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
            taskStore.setHandleTime(new Date());
            if(StringUtils.isNotBlank(approveActionKey)){
                taskStore.setActionKey(approveActionKey);
            }
            taskStoreMapper.updateByPrimaryKey(enterpriseId, taskStore);
        }
        taskSubMapper.updateSubStatusByTaskId(enterpriseId, unifyTaskId);
        TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
        tbQuestionParentInfoDO.setStatus(Constants.INDEX_ONE);
        tbQuestionParentInfoDO.setFinishNum(tbQuestionParentInfoDO.getTotalNum());
        questionParentInfoDao.updateByPrimaryKeySelective(enterpriseId, tbQuestionParentInfoDO);
        // 更新处理人信息
        questionRecordDao.updateHandleInfoByTaskId(enterpriseId, handleTime, handleUserId, handleUserName, handleActionKey, unifyTaskId);
        questionRecordDao.updateQuestionRecordFinish(enterpriseId, approveTime, approveUserId, approveUserName, approveActionKey, unifyTaskId);
        List<TbQuestionRecordDO>  tbQuestionRecordDOList = questionRecordDao.questionListByTaskStoreIds(enterpriseId, taskStoreIdList);
        ListUtils.emptyIfNull(tbQuestionRecordDOList).forEach(recordDO -> {
            buildQuestionHistoryByOaPlugin(enterpriseId, UnifyTaskConstant.OperateType.HANDLE, handleUserId, handleUserName, handleActionKey, UnifyNodeEnum.FIRST_NODE.getCode(), handleTime, recordDO.getId());
            if(StringUtils.isNotBlank(approveUserId)){
                buildQuestionHistoryByOaPlugin(enterpriseId, UnifyTaskConstant.OperateType.APPROVE, approveUserId, approveUserName, approveActionKey, UnifyNodeEnum.SECOND_NODE.getCode(), approveTime, recordDO.getId());
            }
        });
        return  1;
    }

    private void buildQuestionHistoryByOaPlugin(String enterpriseId, String operateType, String operateUserId, String operateUserName, String actionKey, String nodeNo, Date createTime, Long recordId) {
        TbQuestionHistoryDO historyDO = new TbQuestionHistoryDO();
        historyDO.setOperateType(operateType);
        historyDO.setOperateUserId(operateUserId);
        historyDO.setOperateUserName(operateUserName);
        historyDO.setSubTaskId(0L);
        historyDO.setActionKey(actionKey);
        historyDO.setCreateTime(createTime);
        historyDO.setRecordId(recordId);
        historyDO.setNodeNo(nodeNo);
        historyDO.setRemark("");
        questionHistoryDao.insert(enterpriseId, historyDO);
    }

    // 重新分配 取消待办
    @Override
    public void cancelUpcoming(String enterpriseId, List<Long> subTaskIdList, String dingCorpId, String appType) {
        if (CollectionUtils.isEmpty(subTaskIdList)) {
            return;
        }
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("unifyTaskSubIdList", subTaskIdList);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public void cancelCombineUpcoming(String enterpriseId, Long unifyTaskId, Long loopCount, String storeId, String nodeNo, List<String> userIds, String dingCorpId, String appType) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            // 不存在其他门店子任务时才取消待办
            if (StringUtils.isNotBlank(storeId)) {
                Set<String> filterUserIds = new HashSet<>(taskSubMapper.filterExistOtherStoreSubTask(enterpriseId, unifyTaskId, storeId, loopCount, userIds));
                userIds = userIds.stream().filter(v -> !filterUserIds.contains(v)).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(userIds)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enterpriseId", enterpriseId);
                jsonObject.put("corpId", dingCorpId);
                jsonObject.put("taskKey", getCombineOutBusinessId(enterpriseId, unifyTaskId, loopCount, nodeNo));
                jsonObject.put("appType", appType);
                jsonObject.put("userIds", userIds);
                simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
            }
        }
    }

    private List<TaskProcessDTO> getNewProcess(List<TaskProcessDTO> process) {
        List<TaskProcessDTO> newProcess = Lists.newArrayList();
        Map<String, TaskProcessDTO> nodeUserListMap = Maps.newHashMap();
        process.forEach(proItem -> {
            String proNode = proItem.getNodeNo();
            List<GeneralDTO> proUserList = proItem.getUser();
            String approveType = proItem.getApproveType();
            TaskProcessDTO exsitProcess = nodeUserListMap.get(proNode);
            if (exsitProcess != null) {
                exsitProcess.getUser().addAll(proUserList);
                //去重
                exsitProcess.setUser(exsitProcess.getUser().stream().filter(distinctByKey(a -> (a.getValue() + Constants.SPLIT_LINE
                        + a.getType()))).collect(Collectors.toList()));
            } else {
                exsitProcess = new TaskProcessDTO();
                exsitProcess.setUser(proUserList);
                exsitProcess.setNodeNo(proNode);
                exsitProcess.setApproveType(approveType);
                nodeUserListMap.put(proNode, exsitProcess);
            }
        });
        nodeUserListMap.forEach((node, userList) -> {
            newProcess.add(userList);
        });
        return newProcess;
    }
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }




    private Map<Long, List<BasicsAreaDTO>> dealNodeInfo(String enterpriseId, List<Long> taskIds) {

        List<UnifyStoreDTO> storeTaskList = taskMappingMapper.selectStoreInfo(enterpriseId, taskIds);
        List<String> regionList = new ArrayList<>();
        List<String> storeList = new ArrayList<>();
        List<String> groupList = new ArrayList<>();
        for (UnifyStoreDTO unifyStoreDTO : storeTaskList) {
            if (UnifyTaskConstant.StoreType.REGION.equals(unifyStoreDTO.getType())) {
                regionList.add(unifyStoreDTO.getStoreId());
            } else if (UnifyTaskConstant.StoreType.STORE.equals(unifyStoreDTO.getType())) {
                storeList.add(unifyStoreDTO.getStoreId());
            } else if (UnifyTaskConstant.StoreType.GROUP.equals(unifyStoreDTO.getType())) {
                groupList.add(unifyStoreDTO.getStoreId());
            }
        }

        //区域
        Map<String, String> regionDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(regionList)) {
            List<RegionDO> regionDOList = regionMapper.getRegionByRegionIdsForMap(enterpriseId, regionList);
            regionDOMap = regionDOList.stream()
                    .filter(a -> a.getRegionId() != null && a.getName() != null)
                    .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getName, (a, b) -> a));
        }
        //分组
        Map<String, String> groupDOMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(groupList)) {
            List<StoreGroupDO> storeGroupDOS = storeGroupMapper.getListByIds(enterpriseId, groupList);
            groupDOMap = storeGroupDOS.stream()
                    .filter(a -> a.getGroupId() != null && a.getGroupName() != null)
                    .collect(Collectors.toMap(StoreGroupDO::getGroupId, StoreGroupDO::getGroupName, (a, b) -> a));
        }
        //常规
        Map<String, String> storeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(storeList)) {
            List<StoreDO> storeDOList = storeMapper.getStoresByStoreIds(enterpriseId, new ArrayList<>(storeList));
            storeMap = ListUtils.emptyIfNull(storeDOList)
                    .stream()
                    .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                    .collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreName, (a, b) -> a));
        }
        List<BasicsAreaDTO> basicsStore = Lists.newArrayList();
        for (UnifyStoreDTO s : storeTaskList) {
            BasicsAreaDTO basicsStoreDTO;
            switch (s.getType()) {
                case UnifyTaskConstant.StoreType.STORE:
                    basicsStoreDTO = new BasicsAreaDTO(s.getUnifyTaskId(), s.getStoreId(), storeMap.get(s.getStoreId()), s.getType());
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.REGION:
                    basicsStoreDTO = new BasicsAreaDTO(s.getUnifyTaskId(), s.getStoreId(), regionDOMap.get(s.getStoreId()), s.getType());
                    basicsStore.add(basicsStoreDTO);
                    break;
                case UnifyTaskConstant.StoreType.GROUP:
                    if (groupDOMap.containsKey(s.getStoreId())) {
                        basicsStoreDTO = new BasicsAreaDTO(s.getUnifyTaskId(), s.getStoreId(), groupDOMap.get(s.getStoreId()), s.getType());
                        basicsStore.add(basicsStoreDTO);
                    }
                    break;
                default:
                    break;
            }
        }
        if (CollectionUtils.isEmpty(basicsStore)) {
            return new HashMap<>();
        }
        basicsStore = basicsStore.stream().distinct().collect(Collectors.toList());
        return basicsStore.stream().collect(Collectors.groupingBy(BasicsAreaDTO::getTaskId));
    }

    @Override
    public Map<Long, TaskProcessVO> dealTaskProcess(String enterpriseId, List<TaskProcessVO> taskProcessList) {
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
        Map<String, String> useMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIdList);
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
            roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, longPositionList);
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
        return taskProcessList.stream().collect(Collectors.toMap(TaskProcessVO::getTaskId, Function.identity(), (a, b) -> a));
    }

    private void removeTaskInfoUnuseKey(UnifyTaskBuildDTO task) {
        // log.info("##移除无用key前 taskInfo={}", task.getTaskInfo());
        Object obj = JSON.parse(task.getTaskInfo());
        if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            if (null != jsonArray && jsonArray.size() > 0) {
                jsonArray.forEach(s -> {
                    JSONObject jsonObject = (JSONObject) s;
                    jsonObject.remove("shareGroup");
                    jsonObject.remove("shareGroupName");
                    jsonObject.remove("resultShareGroup");
                    jsonObject.remove("resultShareGroupName");
                });
                task.setTaskInfo(JSON.toJSONString(jsonArray));
            }
        } else if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.remove("shareGroup");
            jsonObject.remove("shareGroupName");
            jsonObject.remove("resultShareGroup");
            jsonObject.remove("resultShareGroupName");
            task.setTaskInfo(JSON.toJSONString(jsonObject));
        }
        log.info("##移除无用key后 taskInfo={}", task.getTaskInfo());
    }

    // 组装门店任务表  处理人、审批人、复审人、抄送人
    public TaskStoreDO fillTaskStoreNodePersonInfo(TaskStoreDO taskStore, UnifySubTaskForStoreData subTaskForStoreData) {
        JSONObject jsonObject = new JSONObject();
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getCcUserSet())) {
            taskStore.setCcUserIds(toStringByUserIdSet(subTaskForStoreData.getCcUserSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getUserSet())) {
            jsonObject.put(UnifyNodeEnum.FIRST_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getUserSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getAuditUserSet())) {
            jsonObject.put(UnifyNodeEnum.SECOND_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getAuditUserSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getRecheckUserSet())) {
            jsonObject.put(UnifyNodeEnum.THIRD_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getRecheckUserSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getThirdApproveSet())) {
            jsonObject.put(UnifyNodeEnum.FOUR_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getThirdApproveSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getFourApproveSet())) {
            jsonObject.put(UnifyNodeEnum.FIVE_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getFourApproveSet()));
        }
        if (CollectionUtils.isNotEmpty(subTaskForStoreData.getFiveApproveSet())) {
            jsonObject.put(UnifyNodeEnum.SIX_NODE.getCode(), toStringByUserIdSet(subTaskForStoreData.getFiveApproveSet()));
        }
        taskStore.setExtendInfo(JSON.toJSONString(jsonObject));
        return taskStore;
    }

    private Date setHandleEndTime(TaskParentDO taskParentDO, Long beginTime) {
        String handleEndTime = null;
        Long handleLimitHour = null;
        //巡店任务
        if (UnifyTaskConstant.PATROL_LIST.contains(taskParentDO.getTaskType())) {
            try {
                JSONObject taskInfoJsonObj = JSON.parseObject(taskParentDO.getTaskInfo());
                JSONObject patrolStoreDefined = taskInfoJsonObj.getJSONObject("patrolStoreDefined");
                log.info("taskInfo实体类", patrolStoreDefined);
                if (patrolStoreDefined != null) {
                    //巡店总结
                    handleEndTime = patrolStoreDefined.getString("handleEndTime");
                    //巡店签名
                    handleLimitHour = patrolStoreDefined.getLong("handleLimitHour");
                }
            } catch (Exception e) {
                log.error("taskInfo解析异常", e);
            }
        }

        //陈列任务
        if (TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(taskParentDO.getTaskType())) {
            try {
                JSONObject taskInfoJsonObj = JSON.parseObject(taskParentDO.getTaskInfo());
                JSONObject tbDisplayDefined = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
                log.info("taskInfo实体类", tbDisplayDefined);
                if (tbDisplayDefined != null) {
                    //巡店总结
                    handleEndTime = tbDisplayDefined.getString("handleEndTime");
                    //巡店签名
                    handleLimitHour = tbDisplayDefined.getLong("handleLimitHour");
                }
            } catch (Exception e) {
                log.error("taskInfo解析异常", e);
            }
        }

        Date handleEndTimeDate = null;
        if (TaskRunRuleEnum.ONCE.getCode().equals(taskParentDO.getRunRule())) {
            if (StringUtils.isNotBlank(handleEndTime)) {
                handleEndTimeDate = com.coolcollege.intelligent.common.util.DateUtil.parse(handleEndTime + ":59", DatePattern.NORM_DATETIME_PATTERN);
            }
        } else if (TaskRunRuleEnum.LOOP.getCode().equals(taskParentDO.getRunRule())) {
            if (handleLimitHour != null) {
                long handleLimitHourTime = handleLimitHour * 60;
                handleEndTimeDate = org.apache.commons.lang3.time.DateUtils.addMinutes(new Date(beginTime), (int) handleLimitHourTime);
            }

        }
        return handleEndTimeDate;
    }

    // 过滤没有店内人员的门店
    public boolean filterStoresWthoutPersonnelByTaskInfo(String taskInfo) {
        Boolean filterStoresWthoutPersonnel = false;
        if (!JSONUtil.isJsonObj(taskInfo)) {
            return filterStoresWthoutPersonnel;
        }
        JSONObject taskInfoJsonObj = JSON.parseObject(taskInfo);
        if (taskInfoJsonObj != null) {
            JSONObject tbdisplaydefindObj = taskInfoJsonObj.getJSONObject("tbDisplayDefined");
            if (tbdisplaydefindObj != null) {
                filterStoresWthoutPersonnel = tbdisplaydefindObj.getBoolean("filterStoresWthoutPersonnel");
            }
        }
        return filterStoresWthoutPersonnel == null ? false : filterStoresWthoutPersonnel;
    }

    public Set<String> getHasStoreInsidePersonStore(String enterpriseId, Set<String> storeSet) {
        log.info("##传入门店 enterpriseId={} ,storeSet={}", enterpriseId, JSON.toJSONString(storeSet));
        List<AuthStoreUserDTO> authStoreUserDTOList = authVisualService.authStoreUser(enterpriseId,
                new ArrayList<>(storeSet), CoolPositionTypeEnum.STORE_INSIDE.getCode());
        Map<String, List<String>> authStoreUserDTOMap = ListUtils.emptyIfNull(authStoreUserDTOList).stream().filter(a -> StringUtils.isNotBlank(a.getStoreId()) && CollectionUtils.isNotEmpty(a.getUserIdList()))
                .collect(Collectors.toMap(AuthStoreUserDTO::getStoreId, AuthStoreUserDTO::getUserIdList));

        List<String> userIdList = ListUtils.emptyIfNull(authStoreUserDTOList)
                .stream()
                .map(AuthStoreUserDTO::getUserIdList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<StoreUserDTO> storeInsideUserList = roleMapper.userAndPositionList(enterpriseId, userIdList, null, CoolPositionTypeEnum.STORE_INSIDE.getCode());
            List<String> storeInsideUserIdList = ListUtils.emptyIfNull(storeInsideUserList).stream().map(storeUserDTO -> storeUserDTO.getUserId()).collect(Collectors.toList());
            authStoreUserDTOMap.forEach((storeId, oldUserIdList) -> {
                oldUserIdList.retainAll(storeInsideUserIdList);
            });
        }
        if (CollectionUtils.isNotEmpty(authStoreUserDTOList)) {
            storeSet = ListUtils.emptyIfNull(authStoreUserDTOList).stream().filter(s -> CollectionUtils.isNotEmpty(authStoreUserDTOMap.get(s.getStoreId())))
                    .map(AuthStoreUserDTO::getStoreId).collect(Collectors.toSet());
        }
        log.info("##过滤后的门店 enterpriseId={} ,storeSet={}", enterpriseId, JSON.toJSONString(storeSet));
        return storeSet;
    }

    private String toStringByUserIdSet(Set<String> userIdSet) {
        return Constants.COMMA + String.join(Constants.COMMA, userIdSet) + Constants.COMMA;
    }

    public void checkReallocateNodePerson(TaskStoreDO taskStoreDO, List<String> handerUserList, List<String> approveUserList, List<String> recheckUserList,
                                          List<String> thirdApproveUserList, List<String> fourApproveUserList, List<String> fiveApproveUserList) {
        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if (extendInfoJsonObj == null) {
            log.info("重新分配相应节点人员失败 门店任务节点人员还未生成 :{}", taskStoreDO.getId());
            return;
        }
        // 原来节点不为空
        String handleUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        String auditUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SECOND_NODE.getCode());
        String recheckUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.THIRD_NODE.getCode());
        String thirdApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FOUR_NODE.getCode());
        String fourApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIVE_NODE.getCode());
        String fiveApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SIX_NODE.getCode());
        if (StringUtils.isNotBlank(handleUserIdStr) && CollectionUtils.isEmpty(handerUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "处理人不能为空！");
        } else if (StringUtils.isNotBlank(auditUserIdStr) && CollectionUtils.isEmpty(approveUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "审批人不能为空！");
        } else if (StringUtils.isNotBlank(recheckUserIdStr) && CollectionUtils.isEmpty(recheckUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(),
                    TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskStoreDO.getTaskType()) ? "二级审批人不能为空！" : "复审人不能为空！");
        } else if (StringUtils.isNotBlank(thirdApproveUserIdStr) && CollectionUtils.isEmpty(thirdApproveUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "三级审批人不能为空！");
        } else if (StringUtils.isNotBlank(fourApproveUserIdStr) && CollectionUtils.isEmpty(fourApproveUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "四级审批人不能为空！");
        } else if (StringUtils.isNotBlank(fiveApproveUserIdStr) && CollectionUtils.isEmpty(fiveApproveUserList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "五级审批人不能为空！");
        }
    }

    public void checkBatchReallocateNodePerson(TaskStoreDO taskStoreDO, List<String> handerUserList, List<String> approveUserList, List<String> recheckUserList,
                                               List<String> thirdApproveUserList, List<String> fourApproveUserList, List<String> fiveApproveUserList) {
        JSONObject extendInfoJsonObj = JSON.parseObject(taskStoreDO.getExtendInfo());
        if (extendInfoJsonObj == null) {
            log.info("重新分配相应节点人员失败 门店任务节点人员还未生成 :{}", taskStoreDO.getId());
            return;
        }
        // 原来节点不为空
        String handleUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIRST_NODE.getCode());
        String auditUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SECOND_NODE.getCode());
        String recheckUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.THIRD_NODE.getCode());
        String thirdApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FOUR_NODE.getCode());
        String fourApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.FIVE_NODE.getCode());
        String fiveApproveUserIdStr = extendInfoJsonObj.getString(UnifyNodeEnum.SIX_NODE.getCode());
        if (StringUtils.isNotBlank(handleUserIdStr) && CollectionUtils.isEmpty(handerUserList)) {
            handerUserList = toUserIdListByString(handleUserIdStr);
        } else if (StringUtils.isNotBlank(auditUserIdStr) && CollectionUtils.isEmpty(approveUserList)) {
            approveUserList = toUserIdListByString(auditUserIdStr);
        } else if (StringUtils.isNotBlank(recheckUserIdStr) && CollectionUtils.isEmpty(recheckUserList)) {
            recheckUserList = toUserIdListByString(recheckUserIdStr);
        } else if (StringUtils.isNotBlank(thirdApproveUserIdStr) && CollectionUtils.isEmpty(thirdApproveUserList)) {
            thirdApproveUserList = toUserIdListByString(thirdApproveUserIdStr);
        } else if (StringUtils.isNotBlank(fourApproveUserIdStr) && CollectionUtils.isEmpty(fourApproveUserList)) {
            fourApproveUserList = toUserIdListByString(fourApproveUserIdStr);
        } else if (StringUtils.isNotBlank(fiveApproveUserIdStr) && CollectionUtils.isEmpty(fiveApproveUserList)) {
            fiveApproveUserList = toUserIdListByString(fiveApproveUserIdStr);
        }
    }

    private void checkQuestionVideoHandel(TaskParentDO taskParentDO, String enterpriseId) {
        log.info("checkQuestionVideoHandel#enterpriseId={},taskId={}", enterpriseId, taskParentDO.getId());
        try {
            if (StringUtils.isBlank(taskParentDO.getTaskInfo())) {
                return;
            }
            JSONObject taskInfo = JSONObject.parseObject(taskParentDO.getTaskInfo());

            String videos = taskInfo.getString(UnifyTaskConstant.TaskInfo.VIDEOS);

            if (StringUtils.isBlank(videos)) {
                return;
            }


            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(videos, SmallVideoInfoDTO.class);

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
                        setNotCompleteCache(smallVideoParam, smallVideo, taskParentDO.getId(), enterpriseId);
                    }
                } else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam, smallVideo, taskParentDO.getId(), enterpriseId);
                }
            }
            taskInfo.put(UnifyTaskConstant.TaskInfo.VIDEOS, JSONObject.toJSONString(smallVideoInfo));
            taskParentMapper.updateTaskInfoById(enterpriseId, taskParentDO.getId(), JSONObject.toJSONString(taskInfo), System.currentTimeMillis());
            log.info("checkQuestionVideoHandel处理完成#enterpriseId={},taskId={}", enterpriseId, taskParentDO.getId());
        } catch (Exception e) {
            log.error("工单创建视频异常 taskId:{}", taskParentDO.getId(), e);
        }
    }

    private void saveQuestionInfo(String enterpriseId, TaskParentDO parentDO, String questionType, String storeId) {
        //工单查询工单任务明细表
        UnifyTaskParentItemDO unifyTaskParentItemDO = new UnifyTaskParentItemDO();
        unifyTaskParentItemDO.setUnifyTaskId(parentDO.getId());
        unifyTaskParentItemDO.setItemName(parentDO.getTaskName());
        unifyTaskParentItemDO.setStoreId(storeId);
        unifyTaskParentItemDO.setBeginTime(new Date(parentDO.getBeginTime()));
        unifyTaskParentItemDO.setEndTime(new Date(parentDO.getEndTime()));
        unifyTaskParentItemDO.setCreateTime(new Date());
        unifyTaskParentItemDO.setCreateUserId(parentDO.getCreateUserId());
        unifyTaskParentItemDO.setTaskDesc(parentDO.getTaskDesc());
        unifyTaskParentItemDO.setTemplateId(parentDO.getTemplateId());
        unifyTaskParentItemDO.setTaskInfo(parentDO.getTaskInfo());
        unifyTaskParentItemDO.setNodeInfo(parentDO.getNodeInfo());
        unifyTaskParentItemDO.setLoopCount(1L);
        unifyTaskParentItemDao.insertSelective(enterpriseId, unifyTaskParentItemDO);
        TbQuestionParentInfoDO questionParentInfoDO = new TbQuestionParentInfoDO();
        questionParentInfoDO.setUnifyTaskId(parentDO.getId());
        questionParentInfoDO.setStatus(0);
        questionParentInfoDO.setQuestionName(parentDO.getTaskName());
        questionParentInfoDO.setQuestionType(questionType);
        questionParentInfoDO.setFinishNum(0);
        questionParentInfoDO.setTotalNum(1);
        questionParentInfoDO.setCreateTime(new Date());
        questionParentInfoDO.setCreateId(parentDO.getCreateUserId());
        questionParentInfoDao.insertSelective(enterpriseId, questionParentInfoDO);
        buildTaskStoreQuestionOrder(enterpriseId, parentDO.getId(), null, false);
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     *
     * @param smallVideoParam
     * @param smallVideo
     * @param taskId
     * @param enterpriseId
     * @return void
     * @author chenyupeng
     * @date 2021/10/14
     */
    @Override
    public void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long taskId, String enterpriseId) {
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.QUESTION_PARENT_CREATE.getValue());
        smallVideoParam.setBusinessId(taskId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtilPool.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE, smallVideo.getVideoId(), JSONObject.toJSONString(smallVideoParam));
    }

    private boolean isDifferent(List<String> firstList, List<String> secondList) {
        return CollectionUtils.isNotEmpty(firstList) && CollectionUtils.isNotEmpty(secondList)
                && !(firstList.containsAll(secondList) && secondList.containsAll(firstList));
    }

    private void sendReallocateLogMsg(String enterpriseId, TaskStoreDO taskStoreDO, ReallocateStoreTaskDTO task, String userId, TaskParentDO taskParentDO,
                                      List<String> newAddPersonList, List<String> removePersonList) {
        Map<String, List<String>> nodePersonMap = unifyTaskStoreService.getNodePersonByTaskStore(taskStoreDO);
        List<String> firstNode = nodePersonMap.get(UnifyNodeEnum.FIRST_NODE.getCode());
        List<String> approveNode = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
        List<String> recheckNode = nodePersonMap.get(UnifyNodeEnum.THIRD_NODE.getCode());
        List<String> thirdApproveNode = nodePersonMap.get(UnifyNodeEnum.FOUR_NODE.getCode());
        List<String> fourApproveNode = nodePersonMap.get(UnifyNodeEnum.FIVE_NODE.getCode());
        List<String> fiveApproveNode = nodePersonMap.get(UnifyNodeEnum.SIX_NODE.getCode());
        boolean firstNodeChange = isDifferent(firstNode, task.getHanderUserList());
        boolean approveNodeChange = isDifferent(approveNode, task.getApproveUserList());
        boolean recheckNodeChange = isDifferent(recheckNode, task.getRecheckUserList());
        boolean thirdApproveNodeChange = isDifferent(thirdApproveNode, task.getThirdApproveUserList());
        boolean fourApproveNodeChange = isDifferent(fourApproveNode, task.getFourApproveUserList());
        boolean fiveApproveNodeChange = isDifferent(fiveApproveNode, task.getFiveApproveUserList());
        boolean isChange = firstNodeChange || approveNodeChange || recheckNodeChange || thirdApproveNodeChange || fourApproveNodeChange || fiveApproveNodeChange;
        if (isChange) {
            //发送插入
            //子任务发布广播消息
            Map<String, Object> mqMap = new HashMap<>();
            mqMap.put("operUserId", userId);
            mqMap.put("taskStore", taskStoreDO);
            List<String> changeUserIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(newAddPersonList)) {
                changeUserIdList.addAll(newAddPersonList);
            }
            if (CollectionUtils.isNotEmpty(removePersonList)) {
                changeUserIdList.addAll(removePersonList);
            }
            if (CollectionUtils.isNotEmpty(changeUserIdList)) {
                mqMap.put("changeUserIdList", changeUserIdList);
            }
            if (firstNodeChange) {
                mqMap.put("handlerUserList", task.getHanderUserList());
            }
            if (approveNodeChange) {
                mqMap.put("approveUserList", task.getApproveUserList());
            }
            if (recheckNodeChange) {
                mqMap.put("recheckUserList", task.getRecheckUserList());
            }
            if (thirdApproveNodeChange) {
                mqMap.put("thirdApproveUserList", task.getThirdApproveUserList());
            }
            TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE, taskStoreDO.getUnifyTaskId(), taskParentDO.getTaskType(),
                    taskParentDO.getCreateUserId(), new Date().getTime(), JSON.toJSONString(mqMap), enterpriseId, taskParentDO.getTaskInfo(), taskParentDO.getAttachUrl());
            taskMessage.setStoreId(taskStoreDO.getStoreId());
            taskMessage.setLoopCount(taskStoreDO.getLoopCount());
            log.info("newLoopCount:{}, storeId:{}", taskMessage.getLoopCount(), taskMessage.getStoreId());
            sendTaskMessage(taskMessage);
        }
    }

    @Override
    public void insertUnifyTaskByPerson(String enterpriseId, CurrentUser user, BuildByPersonRequest request) {
        TaskParentDO taskParentDO = unifyTaskParentService.insertByPerson(enterpriseId, user, request);
        // 获取检查表id
        List<Long> metaTableIds = request.getForm().stream().map(GeneralDTO::getValue).map(Long::parseLong).collect(Collectors.toList());
        // 检查表锁表
        tbMetaTableService.updateLockedByIds(enterpriseId, metaTableIds);
        // 检查表数据关联
        unifyTaskDataMappingService.insertDataTaskMappingNew(enterpriseId, taskParentDO, request.getForm());
    }

    @Override
    public void buildSubTaskByPerson(String enterpriseId, String userId, TaskParentDO taskParentDO) {
        TaskPersonTaskInfoDTO taskInfo = JSONObject.parseObject(taskParentDO.getTaskInfo(), TaskPersonTaskInfoDTO.class);
        // 按人巡店计划，查询用户有权限的门店数量，没有门店权限不为用户创建任务
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            TaskPersonTaskInfoDTO.ExecuteDemand executeDemand = taskInfo.getExecuteDemand();
            List<AuthStoreCountDTO> authStoreCountDTOS = visualService.authStoreCount(enterpriseId, Lists.newArrayList(userId), false);
            int storeNum = authStoreCountDTOS.get(Constants.INDEX_ZERO).getStoreCount();
            if (CollectionUtils.isEmpty(authStoreCountDTOS) || storeNum == Constants.ZERO) {
                return;
            }
            Integer totalStoreNum = storeService.countAllStore(enterpriseId);
            // 如果用户的门店数不足任务要求，取用户全部门店数量
            if (storeNum < taskInfo.getExecuteDemand().getPatrolStoreNum()
                    && (StringUtils.isEmpty(executeDemand.getStoreRange()) || Constants.PATROL_STORE_RANGE_AUTH.equals(executeDemand.getStoreRange()))) {
                log.info("用户的管辖门店数不足任务要求 enterpriseId={} ,userId={},storeNum={},执行门店数={}", enterpriseId, userId, storeNum, taskInfo.getExecuteDemand().getPatrolStoreNum());
                executeDemand.setPatrolStoreNum(storeNum);
            } else if (totalStoreNum != null && totalStoreNum < taskInfo.getExecuteDemand().getPatrolStoreNum()
                    && StringUtils.isNotEmpty(executeDemand.getStoreRange()) && Constants.PATROL_STORE_RANGE.equals(executeDemand.getStoreRange())) {
                log.info("企业所有门店数不足任务要求 enterpriseId={} ,userId={},totalStoreNum={},执行门店数={}", enterpriseId, userId, totalStoreNum, taskInfo.getExecuteDemand().getPatrolStoreNum());
                executeDemand.setPatrolStoreNum(totalStoreNum);
            }
            TaskPersonTaskInfoDTO.PatrolParam patrolParam = new TaskPersonTaskInfoDTO.PatrolParam();
            patrolParam.setPatrolParam(executeDemand);
            taskInfo.setPatrolParam(patrolParam);
            taskParentDO.setTaskInfo(JSONObject.toJSONString(taskInfo));
        }
        // 先创建子任务，unify_task_person表冗余了taskSubId
        TaskSubDO taskSubDO = unifyTaskSubService.insertTaskSub(enterpriseId, userId, taskParentDO);
        unifyTaskPersonService.insertTaskPerson(enterpriseId, userId, taskSubDO.getId(), taskParentDO);
        EnterpriseUserDO createUser = enterpriseUserDao.selectByUserId(enterpriseId, taskParentDO.getCreateUserId());
        // 发送工作通知，钉钉待办
        if (taskParentDO.getTaskType().equals(TaskTypeEnum.PRODUCT_FEEDBACK.getCode())){
            jmsTaskService.sendUnifyTaskJms(taskParentDO.getTaskType(), Lists.newArrayList(userId), taskSubDO.getNodeNo(), enterpriseId,
                    taskParentDO.getTaskName(), taskSubDO.getId(), createUser.getName(), taskSubDO.getSubEndTime(), taskParentDO.getTaskName(),
                    Boolean.FALSE, taskSubDO.getSubBeginTime(), taskSubDO.getStoreId(), null, Boolean.FALSE, taskParentDO.getId(), taskSubDO.getCycleCount());
        }else {
            jmsTaskService.sendUnifyTaskJms(taskParentDO.getTaskType(), Lists.newArrayList(userId), taskSubDO.getNodeNo(), enterpriseId,
                    taskParentDO.getTaskName(), taskSubDO.getId(), createUser.getName(), taskSubDO.getSubEndTime(), taskParentDO.getTaskName(),
                    Boolean.FALSE, taskSubDO.getSubBeginTime(), null, null, Boolean.FALSE, taskParentDO.getId(), taskSubDO.getCycleCount());
        }
    }

    @Override
    public PageInfo<GetTaskByPersonVO> getTaskByPerson(String enterpriseId, GetTaskByPersonRequest request) {
        PageInfo<GetTaskByPersonVO> pageInfo = unifyTaskParentService.getTaskParentByPerson(enterpriseId, request);
        // 获取父任务id列表
        List<Long> taskIds = pageInfo.getList().stream().map(GetTaskByPersonVO::getId).collect(Collectors.toList());
        // 根据父任务id列表查询任务数据映射
        Map<Long, List<GeneralDTO>> formMap = unifyTaskDataMappingService.getTaskDataMappingMap(enterpriseId, taskIds);
        // 获取创建人id列表
        List<String> userIds = pageInfo.getList().stream().map(GetTaskByPersonVO::getCreateUserId).collect(Collectors.toList());
        Map<String, EnterpriseUserDO> userDOMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
        for (GetTaskByPersonVO taskForPersonVO : pageInfo.getList()) {
            taskForPersonVO.setForm(formMap.get(taskForPersonVO.getId()));
            EnterpriseUserDO user = userDOMap.get(taskForPersonVO.getCreateUserId());
            if (Objects.nonNull(user)) {
                taskForPersonVO.setCreateUserName(user.getName());
            }
        }
        return pageInfo;
    }

    @Override
    public GetTaskByPersonVO getTaskDetailByPerson(String enterpriseId, GetTaskDetailByPersonRequest request) {
        // 查询父任务
        GetTaskByPersonVO taskByPersonVO = unifyTaskParentService.getTaskParentById(enterpriseId, request.getUnifyTaskId());
        // 查询父任务检查表数据
        Map<Long, List<GeneralDTO>> formMap = unifyTaskDataMappingService.getTaskDataMappingMap(enterpriseId, Lists.newArrayList(taskByPersonVO.getId()));
        taskByPersonVO.setForm(formMap.get(taskByPersonVO.getId()));
        EnterpriseUserDO createUser = enterpriseUserDao.selectByUserId(enterpriseId, taskByPersonVO.getCreateUserId());
        taskByPersonVO.setCreateUserName(createUser.getName());
        if (Objects.isNull(request.getSubTaskId())) {
            return taskByPersonVO;
        }
        // 查询按人任务的巡店要求
        UnifyTaskPersonDO taskPersonDO = unifyTaskPersonService.getTaskPersonBySubTaskId(enterpriseId, request.getSubTaskId());
        TaskPersonTaskInfoDTO taskInfo = JSONObject.parseObject(taskByPersonVO.getTaskInfo(), TaskPersonTaskInfoDTO.class);
        taskInfo.setExecuteDemand(JSONObject.parseObject(taskPersonDO.getExecuteDemand(), TaskPersonTaskInfoDTO.PatrolParam.class).getPatrolParam());
        taskByPersonVO.setTaskInfo(JSONObject.toJSONString(taskInfo));
        return taskByPersonVO;
    }

    @Override
    public void buildTaskStoreQuestionOrder(String enterpriseId, Long taskId, Boolean isFilterUserAuth, boolean isRefresh) {
        TaskParentDO parentDO = taskParentMapper.selectTaskById(enterpriseId, taskId);
        // 按人任务拆分
        List<UnifyTaskParentItemDO> parentItemList = unifyTaskParentItemDao.list(enterpriseId, taskId);
        if (CollectionUtils.isEmpty(parentItemList) || parentDO == null) {
            log.info("buildQuestionOrder#任务已被删除，无法生成任务#taskId:{}", taskId);
            return;
        }
        Set<String> storeIdSet = parentItemList.stream().map(UnifyTaskParentItemDO::getStoreId).collect(Collectors.toSet());
        Long createTime = System.currentTimeMillis();
        //循环发送创建任务
        EnterpriseUserDO createUser = enterpriseUserDao.selectByUserId(enterpriseId, parentDO.getCreateUserId());
        GeneralDTO createPerson = new GeneralDTO();
        createPerson.setValue(parentDO.getCreateUserId());
        if (createUser == null) {
            createPerson.setName(parentDO.getCreateUserId());
        } else {
            createPerson.setName(createUser.getName());
        }
        createPerson.setType(PersonTypeEnum.PERSON.getType());
        List<StoreDO> storeList = storeMapper.getByStoreIds(enterpriseId, new ArrayList<>(storeIdSet));
        if(CollectionUtils.isEmpty(storeList)){
            log.info("父任务分解门店任务 storeList 为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该门店范围内无门店");
        }
        Map<String, StoreDO> storeMap = storeList.stream().collect(Collectors.toMap(k -> k.getStoreId(), v -> v));
        for (UnifyTaskParentItemDO item : parentItemList) {
            String storeId = item.getStoreId();
            StoreDO storeDO = storeMap.get(storeId);
            if(Objects.isNull(storeDO) || StoreIsDeleteEnum.INVALID.getValue().equals(storeDO.getIsDelete())){
                log.info("父任务分解门店任务 门店为空 或者门店为删除状态 暂时没办法分解任务 :{}", JSONObject.toJSONString(storeDO));
                continue;
            }
            List<TaskProcessDTO> process = JSON.parseArray(item.getNodeInfo(), TaskProcessDTO.class);
            ListUtils.emptyIfNull(process).forEach(data -> {
                if (data.getCreateUserApprove() != null && data.getCreateUserApprove()) {
                    data.getUser().add(createPerson);
                }
            });
            //第一次循环都取最新的门店集合储存
            List<TaskMappingDO> personList = Lists.newArrayList();
            getPerson(process, taskId, personList, storeIdSet, enterpriseId, parentDO.getCreateUserId(), parentDO.getTaskType(), true,isFilterUserAuth);
            TaskStoreDO taskStore = TaskStoreDO.questionOrderToStoreTask(parentDO, item, storeDO, personList, item.getId());
            UnifyStoreTaskResolveDTO taskStoreResolveDTO = new UnifyStoreTaskResolveDTO(enterpriseId, taskStore, isRefresh);
            simpleMessageService.send(JSONObject.toJSONString(taskStoreResolveDTO), RocketMqTagEnum.STORE_TASK_RESOLVE_DATA_QUEUE);

        }
        parentDO.setLoopCount(1L);
        taskParentMapper.updateParentTaskById(enterpriseId, parentDO, taskId);
    }

    private List<String> toUserIdListByString(String userIdStr){
        List<String> userIdList = Arrays.asList(StringUtils.split(userIdStr, Constants.COMMA));
        return new ArrayList<>(userIdList);
    }

}
