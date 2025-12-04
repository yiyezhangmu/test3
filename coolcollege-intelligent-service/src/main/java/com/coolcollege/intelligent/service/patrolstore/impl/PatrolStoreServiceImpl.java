package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MengZiYuan2TableWeightEnum;
import com.coolcollege.intelligent.common.enums.meta.MengZiYuanTableWeightEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.patrol.PatrolStoreRecordStatusEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.enums.workHandover.WorkHandoverEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.*;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaColumnReasonDao;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.dao.patrolstore.*;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDetailDao;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbMetaColumnAppealDao;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.dto.EnterpriseSafetyCheckSettingsDTO;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.display.DisplayConstant;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.ApproveDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SelfGuidedStoreCCRuleDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreCheckSettingVO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.vo.CategoryStatisticsVO;
import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.metatable.vo.TbRecordVO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.*;
import com.coolcollege.intelligent.model.patrolstore.dto.*;
import com.coolcollege.intelligent.model.patrolstore.entity.TbDataStaColumnExtendInfoDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.coolcollege.intelligent.model.patrolstore.param.*;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolRecordAuthDTO;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.PatrolStorePictureMsgDTO;
import com.coolcollege.intelligent.model.region.dto.PatrolStoreScoreMsgDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import com.coolcollege.intelligent.model.safetycheck.vo.StorePartnerSignatureVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentAppealVO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.SchedulerAddRequest;
import com.coolcollege.intelligent.model.setting.vo.StoreCheckSettingLevelVO;
import com.coolcollege.intelligent.model.setting.vo.TableCheckSettingLevelVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import com.coolcollege.intelligent.model.storework.vo.HandlerUserVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.constant.TbDisplayConstant;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayDeleteParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordDeleteVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.model.workFlow.WorkflowDataDTO;
import com.coolcollege.intelligent.producer.OrderMessageService;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.ai.DashScopeService;
import com.coolcollege.intelligent.service.ai.PatrolAIService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableResultDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.region.impl.RegionServiceImpl;
import com.coolcollege.intelligent.service.safetycheck.ScSafetyCheckFlowService;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskPersonService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.service.workflow.WorkflowService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.BailiInformNodeEnum;
import com.coolstore.base.enums.BailiModuleTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.utils.MDCUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC_5;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.PATROL_STORE_OFFLINE;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.TB_DISPLAY_TASK;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.*;
import static com.coolcollege.intelligent.model.patrolstore.PatrolStoreConstant.PatrolTypeConstant.PATROL_STORE_ONLINE;
import static com.coolcollege.intelligent.model.patrolstore.param.StoreTaskMapParam.OTHER;

/**
 * @author yezhe
 * @date 2020-12-08 19:21
 */
@Service
@Slf4j
public class PatrolStoreServiceImpl implements PatrolStoreService {

    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbDataDefTableColumnMapper tbDataDefTableColumnMapper;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaTableDao tbMetaTableDao;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;
    @Autowired
    private PatrolStoreRecordsService patrolStoreRecordsService;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    @Lazy
    private RegionService regionService;
    @Autowired
    @Lazy
    private RegionServiceImpl regionServiceImpl;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private TaskSopMapper taskSopMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    private TbPatrolStorePictureMapper patrolStorePictureMapper;
    @Resource
    private TbPatrolStoreRecordInfoMapper tbPatrolStoreRecordInfoMapper;
    @Autowired
    private RedisConstantUtil redisConstantUtil;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private ImportTaskService importTaskService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource(name="deleteRecord")
    private ThreadPoolTaskExecutor deleteThread;
    @Resource
    private WorkflowService workflowService;
    @Resource
    private AIService aiService;
    @Resource
    private RegionDao regionDao;
    @Resource
    private AiModelLibraryService aiModelLibraryService;

    private final static String NORMAL="正常";

    private final static String ABNORMAL="异常";

    @Resource
    private OrderMessageService orderMessageService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Autowired
    private UnifyTaskPersonService unifyTaskPersonService;
    @Resource
    private OnePartyService onePartyService;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    @Lazy
    private JmsTaskService jmsTaskService;
    @Resource
    private TbMetaTableService metaTableService;
    @Resource
    private TbMetaColumnReasonDao metaColumnReasonDao;
    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    private ScSafetyCheckFlowService scSafetyCheckFlowService;
    @Resource
    private TbMetaColumnAppealDao metaColumnAppealDao;

    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Resource
    private QuestionParentInfoDao questionParentInfoDao;

    @Resource
    TbDisplayTableRecordService tbDisplayTableRecordService;

    @Resource
    EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseMapper enterpriseMapper;

    @Resource
    private MqMessageDAO mqMessageDAO;
    @Resource
    private TbPatrolPlanDetailDao tbPatrolPlanDetailDao;
    @Resource
    private PatrolAIService patrolAIService;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private DashScopeService dashScopeService;
    @Resource
    private TbDataStaColumnExtendInfoMapper tbDataStaColumnExtendInfoMapper;
    @Resource
    private PatrolStoreAiAuditServiceImpl patrolStoreAiAuditService;
    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;



    /**
     * 新建巡店任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPatrolStoreTask(TaskMessageDTO taskMessageDTO, TaskSubDO taskSubDO) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, unifyTaskId);
        if(Objects.isNull(taskParentDO)){
            return false;
        }
        // 检查表ids
        List<UnifyFormDataDTO> unifyFormDataDTOList =
                taskMappingMapper.selectMappingDataByTaskId(enterpriseId, unifyTaskId);
        Set<Long> metaTableIds = unifyFormDataDTOList.stream()
                .filter(a -> UnifyTaskDataTypeEnum.STANDARD.getCode().equals(a.getType())
                        || UnifyTaskDataTypeEnum.DEFINE.getCode().equals(a.getType())
                || UnifyTaskDataTypeEnum.AI.getCode().equals(a.getType()))
                .map(a -> Long.valueOf(a.getOriginMappingId())).collect(Collectors.toSet());
        // 子任务
        PatrolStoreBuildParam.PatrolStoreSubBuildParam subBuildParam = PatrolStoreBuildParam.PatrolStoreSubBuildParam.builder().subTaskId(taskSubDO.getId())
                .subBeginTime(taskSubDO.getSubBeginTime()).subEndTime(taskSubDO.getSubEndTime())
                .storeId(taskSubDO.getStoreId()).loopCount(taskSubDO.getLoopCount()).handleUserId(taskSubDO.getHandleUserId()).build();
        // 参数构建
        PatrolStoreBuildParam patrolStoreBuildParam = PatrolStoreBuildParam.builder().unifyTaskId(unifyTaskId).taskType(taskMessageDTO.getTaskType())
                .patrolType(taskMessageDTO.getTaskType()).createUserId(taskMessageDTO.getCreateUserId())
                .metaTableIds(new ArrayList<>(metaTableIds)).subBuildParams(subBuildParam).taskName(taskParentDO.getTaskName())
                .storeCheckSettingDO(taskMessageDTO.getStoreCheckSetting()).taskInfo(taskParentDO.getTaskInfo()).build();
        if (CollectionUtils.isNotEmpty(metaTableIds)) {
            patrolStoreBuildParam.setMetaTableId(new ArrayList<>(metaTableIds).get(0));
        }

        //幂等处理,防止重复添加
        String lockKey = enterpriseId + "_" + patrolStoreBuildParam.getUnifyTaskId() + "_" + patrolStoreBuildParam.getSubBuildParams().getStoreId()
                + "_" + patrolStoreBuildParam.getSubBuildParams().getLoopCount();
        boolean lock = false;
        boolean result = false;
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskMessageDTO.getTaskType())) {
            lock = true;
            TaskPersonTaskInfoDTO taskPersonTaskInfoDTO = JSONObject.parseObject(taskParentDO.getTaskInfo(), TaskPersonTaskInfoDTO.class);
            patrolStoreBuildParam.setPatrolType(taskPersonTaskInfoDTO.getExecuteWay().getWay());
        }else {
            lock = redisUtil.setNxExpire(lockKey, lockKey, CommonConstant.PATROL_LOCK_TIMES);
        }
        if (lock) {
            try {
                result = buildPatrolStore(enterpriseId, patrolStoreBuildParam);
            } catch (Exception e) {
                log.error("addPatrolStoreTask -> buildPatrolStore has error", e);
            } finally {
                redisUtil.delKey(lockKey);
            }
        } else {
            throw new ServiceException(ErrorCodeEnum.PATROL_STORE_RECORD_CREATING);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completePatrolStoreTask(TaskMessageDTO taskMessageDTO) {
        String enterpriseId = taskMessageDTO.getEnterpriseId();
        Long unifyTaskId = taskMessageDTO.getUnifyTaskId();
        // 子任务
        String data = taskMessageDTO.getData();
        List<TaskSubDO> taskSubDOList = JSON.parseArray(data, TaskSubDO.class);
        if (CollectionUtils.isEmpty(taskSubDOList)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "taskMessageDTO.data=" + data);
        }
        Long subTaskId = taskSubDOList.get(0).getId();
        // 校验一下
        if (unifyTaskId == null || unifyTaskId == 0 || subTaskId == null || subTaskId == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    String.format("unifyTaskId:%s,subTaskId:%s", unifyTaskId, subTaskId));
        }
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        //查找统一批次的任务
        // 根据子任务id获取所有的或签子任务ids
        List<Long> orSubTaskIds = unifyTaskService.getOrSubTaskIds(enterpriseId, subTaskId);
        if (CollectionUtils.isEmpty(orSubTaskIds)) {
            throw new ServiceException("该子任务未找到任何或签任务，包括自己");
        }
        // 移除自己
        orSubTaskIds.remove(subTaskId);
        if (CollectionUtils.isEmpty(orSubTaskIds)) {
            return false;
        }
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, unifyTaskId, taskSubDO.getStoreId(),
                taskSubDO.getLoopCount(), null, null);
        //获取当前businessId 新数据只有一个
        if (recordDO != null && recordDO.getLoopCount() > 0) {
            //多人单条记录巡店，不需要删除
            return false;
        }

        List<Long> orBusinessIds = tbPatrolStoreRecordMapper.selectIdsBySubTaskIds(enterpriseId, orSubTaskIds);

        if (CollectionUtils.isEmpty(orBusinessIds)) {
            return false;
        }
        // TODO 后期移除删除记录
        return delPatrolStoreByBusinessIds(enterpriseId, orBusinessIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean buildPatrolStore(String enterpriseId, PatrolStoreBuildParam param) {
        log.info("新建巡店任务参数，param={}", JSON.toJSONString(param));
        PatrolStoreBuildParam.PatrolStoreSubBuildParam patrolStoreSubBuildParam = param.getSubBuildParams();

        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, patrolStoreSubBuildParam.getSubTaskId());
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO;
        if (!TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
            if (taskSubDO.getLoopCount() > 0) {
                tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(),
                        taskSubDO.getLoopCount(), param.getPatrolType(), null);
                if (tbPatrolStoreRecordDO == null) {
                    tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordBySubTaskId(enterpriseId, patrolStoreSubBuildParam.getSubTaskId(), param.getPatrolType());
                }
            } else {
                tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordBySubTaskId(enterpriseId, patrolStoreSubBuildParam.getSubTaskId(), param.getPatrolType());
            }
            if (tbPatrolStoreRecordDO != null) {
                log.info("新建巡店任务 记录已存在， subTaskId = {}", patrolStoreSubBuildParam.getSubTaskId());
                return false;
            }
        }

        // Map:storeId->storeName
        Set<String> storeIds = Collections.singleton(param.getSubBuildParams().getStoreId());
        List<StoreDO> storeDOList = storeMapper.getByStoreIds(enterpriseId, new ArrayList<>(storeIds));
        if (CollectionUtils.isEmpty(storeDOList)) {
            log.info("新建巡店任务 店铺不存在， storeId = {}", param.getSubBuildParams().getStoreId());
            return false;
        }
        Map<String, StoreDO> storeIdDOMap =
                storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity(), (a, b) -> a));

        // 锁定检查表元数据meta_table
        List<Long> metaTableIds = param.getMetaTableIds();
        tbMetaTableDao.updateLockedByIds(enterpriseId, metaTableIds);
        List<TbMetaTableDO> metaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);

        String tableType = "";
        //选择单个检查表，设置表类型
        if (metaTableDOList != null && metaTableDOList.size() == 1) {
            TbMetaTableDO metaTableDO = metaTableDOList.get(0);
            tableType = metaTableDO.getTableType();
        }
        // 初始化巡店记录patrol_store_record
        List<TbPatrolStoreRecordDO> recordList = new ArrayList<>();
        TbPatrolStoreRecordDO recordDO = TbPatrolStoreRecordDO.builder().taskId(param.getUnifyTaskId()).subTaskId(0L)
                .storeId(patrolStoreSubBuildParam.getStoreId()).storeName(storeIdDOMap.get(patrolStoreSubBuildParam.getStoreId()).getStoreName())
                .storeLongitudeLatitude(storeIdDOMap.get(patrolStoreSubBuildParam.getStoreId()).getLongitudeLatitude())
                .regionId(storeIdDOMap.get(patrolStoreSubBuildParam.getStoreId()).getRegionId()).loopCount(patrolStoreSubBuildParam.getLoopCount())
                .supervisorId("").businessCheckType(BusinessCheckType.PATROL_STORE.getCode())
                .regionWay(storeIdDOMap.get(patrolStoreSubBuildParam.getStoreId()).getRegionPath())
                .supervisorName("").patrolType(param.getPatrolType()).tableType(tableType == null ? "" : tableType)
                .metaTableId(param.getMetaTableId()).submitStatus(0)
                .metaTableIds(Constants.COMMA + (param.getMetaTableIds().stream().map(String::valueOf).collect(Collectors.joining(","))) + Constants.COMMA)
                .createUserId(param.getCreateUserId()).taskName(param.getTaskName()).build();
        if (patrolStoreSubBuildParam.getSubBeginTime() != null) {
            recordDO.setSubBeginTime(new Date(patrolStoreSubBuildParam.getSubBeginTime()));
        }
        if (patrolStoreSubBuildParam.getSubEndTime() != null) {
            recordDO.setSubEndTime(new Date(patrolStoreSubBuildParam.getSubEndTime()));
        }
        // 计划巡店生成记录时即关联父任务id、子任务id
        TaskPersonTaskInfoDTO taskPersonTaskInfoDTO = null;
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
            taskPersonTaskInfoDTO = JSONObject.parseObject(param.getTaskInfo(), TaskPersonTaskInfoDTO.class);
            recordDO.setSubTaskId(patrolStoreSubBuildParam.getSubTaskId());
            recordDO.setSupervisorId(taskSubDO.getHandleUserId());
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, Collections.singletonList(taskSubDO.getHandleUserId()));
            Map<String, String> userMap = userList.stream().filter(a -> a.getUserId() != null && a.getName() != null)
                    .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));
            recordDO.setSupervisorName(userMap.get(taskSubDO.getHandleUserId()));
            recordDO.setPatrolType(taskPersonTaskInfoDTO.getExecuteWay().getWay());
        }
        recordList.add(recordDO);
        Date now = new Date();
        tbPatrolStoreRecordMapper.batchInsert(enterpriseId, recordList);
        List<Long> recordIds = recordList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(recordIds) && JSONObject.isValidObject(param.getTaskInfo())) {
            Boolean isOpenSummary = false;
            Boolean isOpenAutograph = false;
            Boolean isOpenCheckInOut = true;
            boolean isOpenMustDo = false;
            try {
                JSONObject taskInfoJsonObj = JSON.parseObject(param.getTaskInfo());
                JSONObject patrolStoreDefined = taskInfoJsonObj.getJSONObject("patrolStoreDefined");
                log.info("taskInfo实体类", patrolStoreDefined);
                if (patrolStoreDefined != null) {
                    //巡店总结
                    isOpenSummary = patrolStoreDefined.getBoolean("isOpenSummary");
                    //巡店签名
                    isOpenAutograph = patrolStoreDefined.getBoolean("isOpenAutograph");
                    //签到签退
                    Boolean taskOpenCheckInOut = patrolStoreDefined.getBoolean("isOpenCheckInOut");
                    Boolean taskIsOpenMustDo = patrolStoreDefined.getBoolean("isOpenMustDo");
                    isOpenCheckInOut = taskOpenCheckInOut == null || taskOpenCheckInOut;
                    isOpenMustDo = taskIsOpenMustDo != null && taskIsOpenMustDo;

                }
                if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
                    log.info("计划巡店参数taskPersonTaskInfoDTO={}", JSONObject.toJSONString(taskPersonTaskInfoDTO));
                    isOpenSummary = taskPersonTaskInfoDTO.getExecuteWay().getIsOpenSummary();
                    isOpenAutograph = taskPersonTaskInfoDTO.getExecuteWay().getIsOpenAutograph();
                }
            } catch (Exception e) {
                log.error("taskInfo解析异常", e);
            }

            EnterpriseStoreCheckSettingDO settingDO = param.getStoreCheckSettingDO();
            if(TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(param.getPatrolType()) || TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(param.getPatrolType()) ){
                settingDO.setAutonomyOpenSignature(false);
                settingDO.setOpenSubmitFirst(false);
            }
            settingDO.setAutonomyOpenSummary(isOpenSummary != null && isOpenSummary);
            settingDO.setAutonomyOpenSignature(isOpenAutograph != null && isOpenAutograph);
            tbPatrolStoreRecordMapper.updateStoreCheckSettings(enterpriseId, recordIds, settingDO);
            TbPatrolStoreRecordInfoDO patrolStoreRecordInfoDO = new TbPatrolStoreRecordInfoDO();
            patrolStoreRecordInfoDO.setEid(enterpriseId);
            patrolStoreRecordInfoDO.setId(recordDO.getId());
            JSONObject extendParam = new JSONObject();
            extendParam.put("isOpenCheckInOut", isOpenCheckInOut);
            extendParam.put("isOpenMustDo", isOpenMustDo);
            patrolStoreRecordInfoDO.setParams(JSONObject.toJSONString(extendParam));
            tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId, patrolStoreRecordInfoDO);
        }

        // 初始化检查表data_table
        List<TbDataTableDO> tbDataTableDOList = recordList.stream()
                .flatMap(a -> metaTableDOList.stream()
                        .map(metaTableDO -> TbDataTableDO.builder().taskId(a.getTaskId()).subTaskId(0L)
                                .storeId(a.getStoreId()).storeName(a.getStoreName()).regionId(a.getRegionId())
                                .regionPath(a.getRegionWay()).businessId(a.getId()).businessType(PATROL_STORE)
                                .metaTableId(metaTableDO.getId()).tableName(metaTableDO.getTableName()).tableProperty(metaTableDO.getTableProperty())
                                .description(metaTableDO.getDescription()).createUserId(a.getCreateUserId())
                                .supervisorId("").supportScore(metaTableDO.getSupportScore())
                                .patrolType(param.getPatrolType())
                                .tableType(metaTableDO.getTableType()).build()))
                .collect(Collectors.toList());
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
            TaskPersonTaskInfoDTO finalTaskPersonTaskInfoDTO = taskPersonTaskInfoDTO;
            tbDataTableDOList.forEach(tbDataTableDO -> {
                tbDataTableDO.setSubTaskId(patrolStoreSubBuildParam.getSubTaskId());
                tbDataTableDO.setPatrolType(finalTaskPersonTaskInfoDTO.getExecuteWay().getWay());
            });
        }
        tbDataTableMapper.batchInsert(enterpriseId, tbDataTableDOList);
        // 初始化标准检查项data_sta_table_column
        List<Long> metaStaTableIds = metaTableDOList.stream().filter(a -> !TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(metaStaTableIds)) {
            Map<Long, String> sceneNameMap = new HashMap<>();
            //查询场景名称
            List<StoreSceneDo> sceneDoList = storeSceneMapper.getStoreSceneList(enterpriseId);
            if(CollectionUtils.isNotEmpty(sceneDoList)){
                sceneNameMap = sceneDoList.stream().collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getName, (a, b) -> a));
            }

            tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, metaStaTableIds,Boolean.TRUE);
            Map<Long, List<TbMetaStaTableColumnDO>> metaTableIdColumnListMap = tbMetaStaTableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));
            Map<Long, String> finalSceneNameMap = sceneNameMap;
            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList =
                    tbDataTableDOList.stream().filter(a -> !TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                            .flatMap(a -> metaTableIdColumnListMap.get(a.getMetaTableId()).stream()
                                    .map(column -> TbDataStaTableColumnDO.builder().categoryName(column.getCategoryName())
                                            .dataTableId(a.getId()).metaTableId(a.getMetaTableId()).metaColumnId(column.getId())
                                            .metaColumnName(column.getColumnName()).description(column.getDescription())
                                            .createUserId(a.getCreateUserId()).supervisorId("").storeId(a.getStoreId())
                                            .storeName(a.getStoreName()).regionId(a.getRegionId())
                                            .regionWay(a.getRegionPath())
                                            .columnType(column.getColumnType())
                                            .storeSceneId((column.getStoreSceneId() == null || !finalSceneNameMap.containsKey(column.getStoreSceneId())) ? null : column.getStoreSceneId())
                                            .storeSceneName(column.getStoreSceneId() == null ? null : finalSceneNameMap.get(column.getStoreSceneId()))
                                            .taskId(a.getTaskId()).subTaskId(0L).businessId(a.getBusinessId())
                                            .checkResult("").checkResultId(0L).checkResultName("").checkPics("").checkText("")
                                            .checkVideo("").checkScore(BigDecimal.ZERO).scoreTimes(BigDecimal.ONE).awardTimes(BigDecimal.ONE).weightPercent(BigDecimal.ONE)
                                            .businessType(PATROL_STORE).patrolType(param.getPatrolType()).createDate(DateUtils.getTime(now)).createTime(now).build()))
                            .collect(Collectors.toList());
            if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
                TaskPersonTaskInfoDTO finalTaskPersonTaskInfoDTO1 = taskPersonTaskInfoDTO;
                tbDataStaTableColumnDOList.forEach(tbDataStaTableColumnDO -> {
                    tbDataStaTableColumnDO.setSubTaskId(patrolStoreSubBuildParam.getSubTaskId());
                    tbDataStaTableColumnDO.setPatrolType(finalTaskPersonTaskInfoDTO1.getExecuteWay().getWay());
                });
            }
            tbDataStaTableColumnMapper.batchInsert(enterpriseId, tbDataStaTableColumnDOList);
        }
        // 初始化自定义检查项data_def_table_column
        List<Long> metaDefTableIds = metaTableDOList.stream().filter(a -> TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaDefTableIds)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList =
                    tbMetaDefTableColumnMapper.selectByTableIds(enterpriseId, metaDefTableIds);
            Map<Long, List<TbMetaDefTableColumnDO>> metaTableIdColumnListMap = tbMetaDefTableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList =
                    tbDataTableDOList.stream().filter(a -> TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                            .flatMap(a -> metaTableIdColumnListMap.get(a.getMetaTableId()).stream()
                                    .map(column -> TbDataDefTableColumnDO.builder().dataTableId(a.getId())
                                            .metaTableId(a.getMetaTableId()).metaColumnId(column.getId())
                                            .metaColumnName(column.getColumnName()).description(column.getDescription())
                                            .createUserId(a.getCreateUserId()).supervisorId("").storeId(a.getStoreId())
                                            .storeName(a.getStoreName()).regionId(a.getRegionId()).regionPath(a.getRegionPath())
                                            .taskId(a.getTaskId()).subTaskId(0L).businessId(a.getBusinessId())
                                            .businessType(PATROL_STORE).patrolType(param.getPatrolType()).patrolStoreTime(now).build()))
                            .collect(Collectors.toList());
            if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(param.getTaskType())) {
                TaskPersonTaskInfoDTO finalTaskPersonTaskInfoDTO2 = taskPersonTaskInfoDTO;
                tbDataDefTableColumnDOList.forEach(tbDataDefTableColumnDO -> {
                    tbDataDefTableColumnDO.setSubTaskId(patrolStoreSubBuildParam.getSubTaskId());
                    tbDataDefTableColumnDO.setPatrolType(finalTaskPersonTaskInfoDTO2.getExecuteWay().getWay());
                });
            }
            tbDataDefTableColumnMapper.batchInsert(enterpriseId, tbDataDefTableColumnDOList);
        }
        TbMetaTableDO metaTableDO = metaTableDOList.get(0);
        //如果是定时巡检，且选择的表为标准表开始抓拍图片,或者是AI巡店
        if((TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(param.getPatrolType()) || TaskTypeEnum.PATROL_STORE_AI.getCode().equals(param.getPatrolType()))
                && !TableTypeUtil.isUserDefinedTable( metaTableDO.getTableProperty(), metaTableDO.getTableType())){
            //异步处理数据
            try {
                // AI巡检或检查项全为AI检查项的定时巡检，标记dataTable的submitStatus的第4位，表示正在进行AI分析
                if (CollectionUtils.isNotEmpty(tbDataTableDOList)) {
                    Map<Long, Boolean> metaDataTableIsAllAiCheckMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId,
                            Collectors.mapping(TbMetaStaTableColumnDO::getIsAiCheck,
                                    Collectors.reducing(true, isAiCheck -> isAiCheck == 1, Boolean::logicalAnd))));
                    List<TbDataTableDO> aiCheckTableList = tbDataTableDOList.stream()
                            .filter(v -> TaskTypeEnum.PATROL_STORE_AI.getCode().equals(v.getPatrolType()) || metaDataTableIsAllAiCheckMap.getOrDefault(v.getMetaTableId(), false))
                            .collect(Collectors.toList());
                    List<TbDataTableDO> aiDataTableList = CollStreamUtil.toList(aiCheckTableList, v -> TbDataTableDO.builder().id(v.getId()).submitStatus(Constants.SUBMITSTATUS_EIGHT | Optional.ofNullable(v.getSubmitStatus()).orElse(0)).build());
                    if (CollectionUtils.isNotEmpty(aiDataTableList)) {
                        Lists.partition(aiDataTableList, Constants.BATCH_INSERT_COUNT).forEach(list -> {
                            tbDataTableMapper.updateBatchById(enterpriseId, list);
                        });
                    }
                }

                log.info("send patrol_store_capture_picture_queue recordId:{}",recordDO.getId());
                simpleMessageService.send(JSONObject.toJSONString(new PatrolStorePictureMsgDTO(enterpriseId, recordDO.getId(), null,param.getPatrolType())), RocketMqTagEnum.PATROL_STORE_CAPTURE_PICTURE_QUEUE);
            } catch (Exception e) {
                log.error("发送定时巡检抓拍 businessId:{}", recordDO.getId(), e);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delPatrolStoreByBusinessIds(String enterpriseId, List<Long> businessIds) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return false;
        }
        // 删除标准检查项
        tbDataStaTableColumnMapper.updateDelByBusinessIds(enterpriseId, businessIds, PATROL_STORE);
        // 删除自定义检查项
        tbDataDefTableColumnMapper.updateDelByBusinessIds(enterpriseId, businessIds, PATROL_STORE);
        // 删除检查表
        tbDataTableMapper.updateDelByBusinessIds(enterpriseId, businessIds, PATROL_STORE);
        // 删除巡店记录
        tbPatrolStoreRecordMapper.updateDelByIds(enterpriseId, businessIds);
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessIds.get(0));
        if (tbPatrolStoreRecordDO != null && TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            // 删除稽核相关数据
            scSafetyCheckFlowService.delSafetyCheckByBusinessIds(enterpriseId, businessIds);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long signIn(String enterpriseId, PatrolStoreSignInParam param) {
        // 门店通检查当月巡店门店数
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(param.getAppType())) {
            onePartyService.checkStoreQuantity(enterpriseId, param.getDingCorpId(), param.getStoreId());
        }
        Long businessId = param.getBusinessId();
        if (businessId == null) {
            // 无记录的自主巡店
            if (param.getStoreId() == null || param.getPatrolType() == null) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "自主巡店门店id不能为空");
            }
            PageHelper.clearPage();
            // 幂等校验，防止重复当天创建多条记录  稽核巡店可以生成多条待处理的记录
            if (!TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(param.getPatrolType()) && !TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(param.getPatrolType())) {
                TbPatrolStoreRecordDO record = tbPatrolStoreRecordMapper.getSpontaneousPatrolStoreRecord(enterpriseId,
                        UserHolder.getUser().getUserId(), param.getStoreId(), param.getPatrolType());
                if (record != null) {
                    throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "今日已有巡店记录，巡店记录id不能为空");
                }
            }
            businessId = spontaneousPatrolStore(enterpriseId, param.getStoreId(), param.getPatrolType(), param.getStoreCheckSetting(), param.getThirdBusinessId());
        }
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);

        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该businessId无对应记录，businessId:" + businessId);
        }
        Long subTaskId = tbPatrolStoreRecordDO.getSubTaskId();

        //查询子任务id
        if (tbPatrolStoreRecordDO.getTaskId() != 0 && subTaskId == 0) {
            TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                    param.getUserId(), null, null);
            if (taskSub != null) {
                subTaskId = taskSub.getSubTaskId();
            }
        }
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
        if (taskSubDO != null && taskSubDO.getSubBeginTime() > System.currentTimeMillis()) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "任务未开始，不能进行操作");
        }

        boolean  isOperateOverdue = Optional.ofNullable(param.getStoreCheckSetting()).map(o->o.getOverdueTaskContinue()).orElse(Boolean.FALSE);

        if( Objects.nonNull(taskSubDO) && taskSubDO.getCreateTime() > 1712591999000L){
            isOperateOverdue = "1".equals(taskSubDO.getIsOperateOverdue()) || Objects.isNull(taskSubDO.getIsOperateOverdue());
        }
        if(Objects.nonNull(isOperateOverdue) && !isOperateOverdue && Objects.nonNull(tbPatrolStoreRecordDO.getSubEndTime()) && tbPatrolStoreRecordDO.getSubEndTime().getTime() < System.currentTimeMillis()){
            throw new ServiceException(ErrorCodeEnum.TASK_OVERDUE);
        }

        if (tbPatrolStoreRecordDO.getSignInStatus() != 0) {
            // 幂等，防止重复签到
            return businessId;
        }
        // 修改巡店记录
        Date signStartTime = param.getSignStartTime();
        if (signStartTime == null) {
            signStartTime = new Date();
        }
        TbPatrolStoreRecordDO updateStoreRecord = TbPatrolStoreRecordDO.builder().id(businessId).signStartTime(signStartTime)
                .signStartAddress(param.getSignStartAddress()).startLongitudeLatitude(param.getStartLongitudeLatitude())
                .signInStatus(param.getSignInStatus()).build();
        if(tbPatrolStoreRecordDO.getTaskId() == 0 && tbPatrolStoreRecordDO.getSubBeginTime() == null){
            updateStoreRecord.setSubBeginTime(signStartTime);
        }
        tbPatrolStoreRecordMapper.updateById(enterpriseId, updateStoreRecord);

        //记录签到额外信息
        TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO =new TbPatrolStoreRecordInfoDO();
        tbPatrolStoreRecordInfoDO.setEid(enterpriseId);
        tbPatrolStoreRecordInfoDO.setId(businessId);
        tbPatrolStoreRecordInfoDO.setSignInWay(param.getSignInWay());
        tbPatrolStoreRecordInfoDO.setSignInRemark(param.getSignInRemark());
        tbPatrolStoreRecordInfoDO.setPatrolType(tbPatrolStoreRecordDO.getPatrolType());
        tbPatrolStoreRecordInfoDO.setSupervisorId(tbPatrolStoreRecordDO.getSupervisorId());
        tbPatrolStoreRecordInfoDO.setSupervisorName(tbPatrolStoreRecordDO.getSupervisorName());
        if(StringUtils.isNotBlank(param.getSignInImg())){
            TbPatrolStoreRecordInfoDO dbRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, businessId);
            JSONObject jsonObject = new JSONObject();
            if(Objects.nonNull(dbRecordInfoDO) && Objects.nonNull(dbRecordInfoDO.getParams())){
                jsonObject = JSONObject.parseObject(dbRecordInfoDO.getParams());
            }
            jsonObject.put(TbPatrolStoreRecordInfoDO.PARAMS_SIGN_IN_IMG, param.getSignInImg());
            tbPatrolStoreRecordInfoDO.setParams(JSONObject.toJSONString(jsonObject));
        }
        tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId,tbPatrolStoreRecordInfoDO);

        tbDataTableMapper.updateSignStartEndTime(enterpriseId, signStartTime, null, businessId, PATROL_STORE);
        //发送消息
        if(StringUtils.isNotBlank(param.getSignInImg())){
            SendWXGroupMessageDTO message = new SendWXGroupMessageDTO(enterpriseId, WXMessageTypeEnum.IMAGE, param.getSignInImg(), param.getUserId());
            simpleMessageService.send(JSONObject.toJSONString(message), RocketMqTagEnum.SEND_WX_GROUP_MESSAGE);
        }

        //检查企业是否开启mq配置
        try {
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, 1);
            if (!Objects.isNull(enterpriseMqInformConfigDTO)) {
                log.info("企业开启了mq配置，发送签到消息");
                //mq发送签到消息
                JSONObject data = new JSONObject();
                data.put("enterpriseId", enterpriseId);
                //模块类型巡店
                data.put("moduleType", BailiModuleTypeEnum.PATROL_STORE.getCode());
                //业务类型是签到
                data.put("bizType", BailiInformNodeEnum.SIGN_IN.getCode());
                //时间戳
                data.put("timestamp", System.currentTimeMillis());
                //业务id
                data.put("businessId",businessId);
                simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM,System.currentTimeMillis()+3000);
            }else {
                log.info("企业未开启mq配置，不发送签到消息");
            }
        }catch (Exception e){
            log.error("企业开启了mq配置，发送签到消息失败",e);
        }
        //设置定时任务
        // setGiveUpScheduler(enterpriseId, businessId);, 暂时去掉，不需要了
        return businessId;
    }



    /**
     * 自主巡店创建巡店记录
     */
    private Long spontaneousPatrolStore(String enterpriseId, String storeId, String patrolType, EnterpriseStoreCheckSettingDO settingDO, String thirdBusinessId) {
        // store
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
        // user
        CurrentUser user = UserHolder.getUser();

        // 初始化巡店记录patrol_store_record
        TbPatrolStoreRecordDO record =
                TbPatrolStoreRecordDO.builder().taskId(0L).subTaskId(0L).storeId(storeId).storeName(storeDO.getStoreName())
                        .storeLongitudeLatitude(storeDO.getLongitudeLatitude()).regionId(storeDO.getRegionId()).loopCount(0L)
                        .supervisorId(user.getUserId()).supervisorName(user.getName()).tableType("")
                        .regionWay(storeDO.getRegionPath()).submitStatus(0).businessCheckType(BusinessCheckType.PATROL_STORE.getCode())
                        .patrolType(patrolType).metaTableIds("").metaTableId(0L).createUserId(user.getUserId()).taskName(thirdBusinessId).build();
        tbPatrolStoreRecordMapper.batchInsert(enterpriseId, Lists.newArrayList(record));
        //门店自检不需要总结和签名
        if (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(patrolType) ||
                TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(patrolType) ||
                TaskTypeEnum.PATROL_STORE_FORM.getCode().equals(patrolType) ||
                TaskTypeEnum.STORE_SELF_CHECK.getCode().equals(patrolType)) {
            settingDO.setAutonomyOpenSignature(false);
            settingDO.setAutonomyOpenSummary(false);
            settingDO.setOpenSubmitFirst(false);
        }
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(patrolType)) {
            settingDO.setAutonomyOpenSignature(false);
            settingDO.setAutonomyOpenSummary(false);
        }
        tbPatrolStoreRecordMapper.updateStoreCheckSettings(enterpriseId, Collections.singletonList(record.getId()), settingDO);

        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(patrolType)) {
            // 生成稽核流程
            scSafetyCheckFlowService.generateSafetyCheckFlowData(enterpriseId, UnifyNodeEnum.ZERO_NODE.getCode(), null, record, null, null);
        }

        return record.getId();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long signOut(String dingCorpId, String enterpriseId, PatrolStoreSignOutParam param, EnterpriseStoreCheckSettingDO storeCheckSettingDO, String userId, String userName,String appType, EnterpriseSettingDO enterpriseSettingDO) {
        Long businessId = param.getBusinessId();
        // 校验签到、检查表、检查项
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (tbPatrolStoreRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该businessId无对应记录，businessId:" + businessId);
        }
        if (tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该任务已被其他人处理");
        }
        if (tbPatrolStoreRecordDO.getSignInStatus() == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店未签到，无法签退");
        }
        if (tbPatrolStoreRecordDO.getSignOutStatus() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已签退完成，无法再次签退");
        }

        boolean isOperateOverdue = Optional.ofNullable(storeCheckSettingDO).map(o->o.getOverdueTaskContinue()).orElse(Boolean.FALSE);;
        if (tbPatrolStoreRecordDO.getTaskId() != 0){
            TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                    null, null, null);
            if(taskSub != null){
                TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, taskSub.getSubTaskId());
                if(Objects.nonNull(taskSubDO) && taskSubDO.getCreateTime() > 1712591999000L){
                    isOperateOverdue = "1".equals(taskSubDO.getIsOperateOverdue()) || Objects.isNull(taskSubDO.getIsOperateOverdue());
                }
            }
        }
        if(Objects.nonNull(isOperateOverdue) && !isOperateOverdue && Objects.nonNull(tbPatrolStoreRecordDO.getSubEndTime()) && tbPatrolStoreRecordDO.getSubEndTime().getTime() < System.currentTimeMillis()){
            throw new ServiceException(ErrorCodeEnum.TASK_OVERDUE);
        }
        Date signEndTime = param.getSignEndTime();
        if (signEndTime == null) {
            signEndTime = new Date();
        }
        // 签退异常时需要根据企业配置确定检查时长
        long tourTime = 0L;
        if (Constants.INDEX_ONE.equals(tbPatrolStoreRecordDO.getSignInStatus())&&Constants.INDEX_ONE.equals(param.getSignOutStatus())) {
            tourTime = signEndTime.getTime() - tbPatrolStoreRecordDO.getSignStartTime().getTime();
        } else if (Constants.INDEX_TWO.equals(tbPatrolStoreRecordDO.getSignInStatus())||param.getSignOutStatus() == 2) {
            // 默认1分钟
            tourTime = param.getDefaultTourTime() == null ? 1 * 60 * 1000 : param.getDefaultTourTime() * 60 * 1000;
        }
        TbPatrolStoreRecordDO updateStoreRecord =  TbPatrolStoreRecordDO.builder().id(businessId).signEndTime(signEndTime).supervisorId(userId).supervisorName(userName)
                .signEndAddress(param.getSignEndAddress()).endLongitudeLatitude(param.getEndLongitudeLatitude())
                .signOutStatus(param.getSignOutStatus()).tourTime(tourTime).build();
        if(tbPatrolStoreRecordDO.getTaskId() == 0L && tbPatrolStoreRecordDO.getSubEndTime() == null){
            updateStoreRecord.setSubEndTime(signEndTime);
        }
        // 修改巡店记录
        tbPatrolStoreRecordMapper.updateById(enterpriseId, updateStoreRecord);
        //记录签到额外信息
        TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO =new TbPatrolStoreRecordInfoDO();
        tbPatrolStoreRecordInfoDO.setEid(enterpriseId);
        tbPatrolStoreRecordInfoDO.setId(businessId);
        tbPatrolStoreRecordInfoDO.setSignOutWay(param.getSignOutWay());
        tbPatrolStoreRecordInfoDO.setSignOutRemark(param.getSignOutRemark());
        if(StringUtils.isNotBlank(param.getSignOutImg())){
            TbPatrolStoreRecordInfoDO dbRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, businessId);
            JSONObject jsonObject = new JSONObject();
            if(Objects.nonNull(dbRecordInfoDO) && Objects.nonNull(dbRecordInfoDO.getParams())){
                jsonObject = JSONObject.parseObject(dbRecordInfoDO.getParams());
            }
            jsonObject.put(TbPatrolStoreRecordInfoDO.PARAMS_SIGN_OUT_IMG, param.getSignOutImg());
            tbPatrolStoreRecordInfoDO.setParams(JSONObject.toJSONString(jsonObject));
        }
        tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId,tbPatrolStoreRecordInfoDO);

        tbDataTableMapper.updateSignStartEndTime(enterpriseId, null, signEndTime, businessId, PATROL_STORE);
        if(StringUtils.isNotBlank(param.getSignOutImg())){
            SendWXGroupMessageDTO imageMessage = new SendWXGroupMessageDTO(enterpriseId, WXMessageTypeEnum.IMAGE, param.getSignOutImg(), userId);
            simpleMessageService.send(JSONObject.toJSONString(imageMessage), RocketMqTagEnum.SEND_WX_GROUP_MESSAGE);
            SendWXGroupMessageDTO message = new SendWXGroupMessageDTO(enterpriseId, WXMessageTypeEnum.MARKDOWN, tbPatrolStoreRecordDO.getSignStartTime(), signEndTime, tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getSummary(), userId, param.getSignOutImg());
            simpleMessageService.send(JSONObject.toJSONString(message), RocketMqTagEnum.SEND_WX_GROUP_MESSAGE);
        }
        // 不允許先签退，后提交，则结束巡店
        if (!tbPatrolStoreRecordDO.getOpenSubmitFirst()) {
            //判断为自主巡店，如果结束巡店，删除巡检中的检查表
            if (tbPatrolStoreRecordDO.getTaskId() == 0) {
                List<TbDataTableDO> dataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId(), PATROL_STORE);
                int beforeSize = dataTableDOList.size();
                dataTableDOList = dataTableDOList.stream().filter(dataTable -> (dataTable.getSubmitStatus() & 1) == 0).collect(Collectors.toList());
                int afterSize = dataTableDOList.size();
                //表单全部未提交
                if (beforeSize == afterSize) {
                    throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店存在未提交检查表，无法提交巡店");
                }
                if (CollectionUtils.isNotEmpty(dataTableDOList)) {
                    // 移除
                    List<Long> rmMetaTableIds =
                            dataTableDOList.stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(rmMetaTableIds)) {
                        rmMetaTable(enterpriseId, tbPatrolStoreRecordDO.getId(), rmMetaTableIds);
                    }
                    tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, tbPatrolStoreRecordDO.getId(), Constants.INDEX_ONE | tbPatrolStoreRecordDO.getSubmitStatus());
                    tbPatrolStoreRecordDO.setSubmitStatus(Constants.INDEX_ONE | tbPatrolStoreRecordDO.getSubmitStatus());
                }
                //自主巡店工作通知
                sendMessageBySelfPatrol(enterpriseId,tbPatrolStoreRecordDO,storeCheckSettingDO);

            }
            if (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())){
                sendMessageByVideoPatrol(enterpriseId,tbPatrolStoreRecordDO,storeCheckSettingDO);
            }
            // 全为AI检查项的，无需校验提交状态
            boolean noNeedVerify = tbMetaStaTableColumnMapper.isAllAiCheckColumnByMetaTableId(enterpriseId, tbPatrolStoreRecordDO.getMetaTableId());
            //自主巡店在完成巡店中校验表单提交
            if(tbPatrolStoreRecordDO.getTaskId() != 0 && !noNeedVerify){
                //表单提交
                checkSubmitStatus(enterpriseId, businessId, tbPatrolStoreRecordDO, true);
            }
            //是否结束巡店
            overPatrol(enterpriseId, businessId, userId, userName, dingCorpId, true,appType, param.getSignatureUser(),null, enterpriseSettingDO);
        }
        //查询子任务信息

        //mq推送
        try {
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, 1);
            if (!Objects.isNull(enterpriseMqInformConfigDTO)) {
                log.info("企业开启了mq配置，发送签退消息");
                //mq发送签到消息
                JSONObject data = new JSONObject();
                data.put("enterpriseId", enterpriseId);
                //模块类型巡店
                data.put("moduleType", BailiModuleTypeEnum.PATROL_STORE.getCode());
                //业务类型是签退
                data.put("bizType", BailiInformNodeEnum.SIGN_OUT.getCode());
                //时间戳
                data.put("timestamp", System.currentTimeMillis());
                //业务id
                data.put("businessId",businessId);
                simpleMessageService.send(data.toJSONString(), RocketMqTagEnum.BAILI_STATUS_INFORM,System.currentTimeMillis()+3000);
            }else {
                log.info("企业未开启mq配置，不发送签退消息");
            }
        }catch (Exception e){
            log.error("mq推送失败",e);
        }
        return businessId;
    }

    private void sendMessageBySelfPatrol(String enterpriseId,TbPatrolStoreRecordDO tbPatrolStoreRecordDO,EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        log.info("进入自主巡店抄送规则开始->eid:{},tbPatrolStoreRecordDO:{},enterpriseStoreCheckSetting:{}",enterpriseId,JSONObject.toJSONString(tbPatrolStoreRecordDO),JSONObject.toJSONString(enterpriseStoreCheckSetting));
        if (Objects.isNull(enterpriseStoreCheckSetting)){
            log.info("sendMessageBySelfPatrol enterpriseStoreCheckSetting is null");
            return;
        }
        CurrentUser user = UserHolder.getUser();
        //查找是否有抄送规则
        if (StringUtils.isNotBlank(enterpriseStoreCheckSetting.getSelfGuidedStoreCCRules())) {
            List<String> currentRoleIds = enterpriseUserRoleMapper.selectRoleIdsByUserId(enterpriseId, user.getUserId())
                    .stream().map(String::valueOf)
                    .collect(Collectors.toList());
            log.info("sendMessageBySelfPatrol currentRoleIds：{}",JSONObject.toJSONString(currentRoleIds));
            //找出发送角色与接收角色
            String selfGuidedStoreCCRules = enterpriseStoreCheckSetting.getSelfGuidedStoreCCRules();
            List<SelfGuidedStoreCCRuleDTO> selfGuidedStoreCCRuleDTOS = JSONArray.parseArray(selfGuidedStoreCCRules, SelfGuidedStoreCCRuleDTO.class);
            List<String> doRoleIdList = selfGuidedStoreCCRuleDTOS
                    .stream()
                    .map(SelfGuidedStoreCCRuleDTO::getDoRole)
                    .collect(Collectors.toList());
            log.info("sendMessageBySelfPatrol doRoleIdList：{}",JSONObject.toJSONString(doRoleIdList));
            if (doRoleIdList.stream().anyMatch(currentRoleIds::contains)) {
                //当前用户角色为自主角色时的抄送角色
                List<Long> ccRoleIds = selfGuidedStoreCCRuleDTOS
                        .stream()
                        .filter(item -> StringUtils.isNotBlank(item.getDoRole()) && StringUtils.isNotBlank(item.getCcRole()))
                        .filter(item -> currentRoleIds.contains(item.getDoRole()))
                        .map(SelfGuidedStoreCCRuleDTO::getCcRole)
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                log.info("sendMessageBySelfPatrol ccRoleIds：{}",JSONObject.toJSONString(ccRoleIds));
                //需要发送的用户列表
                if(CollectionUtils.isEmpty(ccRoleIds)){
                    log.info("该门店没有配置需要抄送的职位");
                    return;
                }
                List<String> userIdsByRoleIdList = enterpriseUserRoleMapper.getUserIdsByRoleIdList(enterpriseId, ccRoleIds);
                if (CollectionUtils.isEmpty(userIdsByRoleIdList)) {
                    log.info("抄送的职位下不存在用户");
                    return;
                }

                //所巡门店的信息
                String regionWay = tbPatrolStoreRecordDO.getRegionWay();
                String regionId = regionWay.substring(regionWay.lastIndexOf("/", regionWay.lastIndexOf("/") - 1) + 1, regionWay.lastIndexOf("/"));
                RegionNode regionByRegionId = regionMapper.getRegionByRegionId(enterpriseId, regionId);

                //用户列表的管辖权限
                List<String> totalRegions = new ArrayList<>();
                List<String> sendUserIds = new ArrayList<>();
                List<UserAuthMappingDO> allByUserIds = userAuthMappingMapper.getAllByUserIds(enterpriseId, userIdsByRoleIdList);
                List<String> regionIds = ListUtils.emptyIfNull(allByUserIds).stream()
                        .map(UserAuthMappingDO::getMappingId)
                        .collect(Collectors.toList());
                List<RegionDO> regionByRegionIds = regionDao.getRegionByRegionIds(enterpriseId, regionIds);
                List<String> combinedList = ListUtils.emptyIfNull(regionByRegionIds).stream()
                        .filter(item ->
                                ( Constants.ROOT_REGION_ID.equals(item.getRegionId()) ||
                                  Constants.REGION_TYPE_ROOT.equals(item.getRegionType()) ||
                                (Constants.REGION_TYPE_PATH.equals(item.getRegionType()) &&
                                        regionByRegionId.getRegionPath().contains(item.getRegionPath() + item.getRegionId() + "/")) ||
                                (Constants.STORE.equals(item.getRegionType()) && regionId.equals(item.getRegionId()))))
                        .map(RegionDO::getRegionId)
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(combinedList)){
                    totalRegions.addAll(combinedList);
                }
                if (CollectionUtils.isNotEmpty(totalRegions)){
                    log.info("自主巡店抄送规则,巡店region:{},符合条件的region:{}",JSONObject.toJSONString(regionByRegionId),JSONObject.toJSONString(totalRegions));
                    sendUserIds = allByUserIds.stream()
                            .filter(item -> totalRegions.contains(item.getMappingId()))
                            .map(UserAuthMappingDO::getUserId)
                            .collect(Collectors.toList());
                }
//                Map<String, List<UserAuthMappingDO>> userAuthMap = allByUserIds.stream().collect(Collectors.groupingBy(UserAuthMappingDO::getUserId));

                if (CollectionUtils.isEmpty(sendUserIds)){
                    log.info("该门店没有需要抄送的职位人员");
                    return;
                }
                jmsTaskService.sendUnifyTaskJms(
                        TaskTypeEnum.SELF_PATROL_STORE.getCode(),
                        sendUserIds,
                        "cc_ps",
                        enterpriseId,
                        tbPatrolStoreRecordDO.getStoreName(),
                        tbPatrolStoreRecordDO.getId(),
                        user.getName(),
                        null,
                        "自主巡店抄送",
                        Boolean.FALSE,
                        null,
                        tbPatrolStoreRecordDO.getStoreId(),
                        null,
                        Boolean.TRUE,
                        tbPatrolStoreRecordDO.getTaskId(),
                        null,
                        tbPatrolStoreRecordDO.getLoopCount(),
                        tbPatrolStoreRecordDO.getId()
                        );
            }
        }
    }

    private void sendMessageByVideoPatrol(String enterpriseId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        log.info("进入视频巡店抄送规则开始->eid:{},tbPatrolStoreRecordDO:{},enterpriseStoreCheckSetting:{}", enterpriseId, JSONObject.toJSONString(tbPatrolStoreRecordDO), JSONObject.toJSONString(enterpriseStoreCheckSetting));
        if (Objects.isNull(enterpriseStoreCheckSetting)) {
            log.info("sendMessageByVideoPatrol enterpriseStoreCheckSetting is null");
            return;
        }
        CurrentUser user = UserHolder.getUser();
        if (StringUtils.isBlank(enterpriseStoreCheckSetting.getVideoPatrolStoreCCRules())) {
            log.info("视频巡店自定义规则为空");
            return;
        }
        //当前登录用户的角色
        List<String> currentRoleIds = enterpriseUserRoleMapper.selectRoleIdsByUserId(enterpriseId, user.getUserId())
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        log.info("sendMessageByVideoPatrol currentRoleIds：{}", JSONObject.toJSONString(currentRoleIds));

        //找出发送角色与接收角色
        String videoPatrolStoreCCRules = enterpriseStoreCheckSetting.getVideoPatrolStoreCCRules();
        List<SelfGuidedStoreCCRuleDTO> videoGuidedStoreCCRuleDTOS = JSONArray.parseArray(videoPatrolStoreCCRules, SelfGuidedStoreCCRuleDTO.class);
        List<String> doRoleIdList = videoGuidedStoreCCRuleDTOS
                .stream()
                .map(SelfGuidedStoreCCRuleDTO::getDoRole)
                .collect(Collectors.toList());
        log.info("sendMessageByVideoPatrol doRoleIdList：{}", JSONObject.toJSONString(doRoleIdList));
        //如果规则内执行角色与当前用户角色匹配
        if (doRoleIdList.stream().anyMatch(currentRoleIds::contains)) {
            List<Long> ccRoleIds = videoGuidedStoreCCRuleDTOS
                    .stream()
                    .filter(item -> StringUtils.isNotBlank(item.getDoRole()) && StringUtils.isNotBlank(item.getCcRole()))
                    .filter(item -> currentRoleIds.contains(item.getDoRole()))
                    .map(SelfGuidedStoreCCRuleDTO::getCcRole)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            log.info("sendMessageByVideoPatrol ccRoleIds：{}", JSONObject.toJSONString(ccRoleIds));
            //需要发送的用户列表
            List<String> userIdsByRoleIdList = enterpriseUserRoleMapper.getUserIdsByRoleIdList(enterpriseId, ccRoleIds);
            //所巡门店的信息
            String regionWay = tbPatrolStoreRecordDO.getRegionWay();
            String regionId = regionWay.substring(regionWay.lastIndexOf("/", regionWay.lastIndexOf("/") - 1) + 1, regionWay.lastIndexOf("/"));
            RegionNode regionByRegionId = regionMapper.getRegionByRegionId(enterpriseId, regionId);

            //用户列表的管辖权限
            List<String> totalRegions = new ArrayList<>();
            List<String> sendUserIds = new ArrayList<>();
            List<UserAuthMappingDO> allByUserIds = userAuthMappingMapper.getAllByUserIds(enterpriseId, userIdsByRoleIdList);
            List<String> regionIds = allByUserIds.stream()
                    .map(UserAuthMappingDO::getMappingId)
                    .collect(Collectors.toList());
            List<RegionDO> regionByRegionIds = regionMapper.getRegionByRegionIds(enterpriseId, regionIds);
            List<String> combinedList = regionByRegionIds.stream()
                    .filter(item ->
                            (Constants.REGION_TYPE_ROOT.equals(item.getRegionType()) ||
                            (Constants.REGION_TYPE_PATH.equals(item.getRegionType()) && regionByRegionId.getRegionPath().contains(item.getRegionPath() + item.getRegionId() + "/")) ||
                            (Constants.STORE.equals(item.getRegionType()) && regionId.equals(item.getRegionId()))))
                    .map(RegionDO::getRegionId)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(combinedList)) {
                totalRegions.addAll(combinedList);
            }
            if (CollectionUtils.isNotEmpty(totalRegions)) {
                log.info("视频巡店抄送规则,巡店region:{},符合条件的region:{}", JSONObject.toJSONString(regionByRegionId), JSONObject.toJSONString(totalRegions));
                sendUserIds = allByUserIds.stream()
                        .filter(item -> totalRegions.contains(item.getMappingId()))
                        .map(UserAuthMappingDO::getUserId)
                        .collect(Collectors.toList());
            }
//                Map<String, List<UserAuthMappingDO>> userAuthMap = allByUserIds.stream().collect(Collectors.groupingBy(UserAuthMappingDO::getUserId));

            if (CollectionUtils.isEmpty(sendUserIds)) {
                log.info("该门店没有需要抄送的职位人员");
                return;
            }
            jmsTaskService.sendUnifyTaskJms(
                    TaskTypeEnum.PATROL_STORE_ONLINE.getCode(),
                    sendUserIds,
                    "cc_online",
                    enterpriseId,
                    tbPatrolStoreRecordDO.getStoreName(),
                    tbPatrolStoreRecordDO.getId(),
                    user.getName(),
                    null,
                    "线上巡店抄送",
                    Boolean.FALSE,
                    null,
                    tbPatrolStoreRecordDO.getStoreId(),
                    null,
                    Boolean.TRUE,
                    tbPatrolStoreRecordDO.getTaskId(),
                    null,
                    tbPatrolStoreRecordDO.getLoopCount(),
                    tbPatrolStoreRecordDO.getId()
            );
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean giveUp(String enterpriseId, Long businessId) {
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该businessId无对应记录，businessId:" + businessId);
        }
        if (tbPatrolStoreRecordDO.getStatus() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已完成，无法放弃");
        }
        //巡店计划也需要删除数据
        Boolean patrolPlan = Boolean.FALSE;
        if (tbPatrolStoreRecordDO.getSubTaskId() != 0){
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, tbPatrolStoreRecordDO.getSubTaskId());
            if (WorkHandoverEnum.PATROL_STORE_PLAN.getCode().equals(taskSubDO.getTaskType())){
                patrolPlan = Boolean.TRUE;
            }
        }
        // 行事历类型的巡店，则清空巡店信息
        TbPatrolPlanDetailDO patrolPlanDetailDO = tbPatrolPlanDetailDao.getPlanDetailByBusinessId(enterpriseId, businessId);
        if (Objects.isNull(patrolPlanDetailDO)) {
            //自主巡店删除巡店记录
            if(tbPatrolStoreRecordDO.getTaskId() == 0||patrolPlan){
                delPatrolStoreByBusinessIds(enterpriseId, Collections.singletonList(businessId));
                return true;
            }
        }
// 修改巡店记录签到状态(移动到deletePatrolDetail逻辑中)
//        tbPatrolStoreRecordMapper.updateById(enterpriseId,
//                TbPatrolStoreRecordDO.builder().id(businessId).signInStatus(0).build());
        //删除巡店记录拓展额外信息
        tbPatrolStoreRecordInfoMapper.deleteTbPatrolStoreRecordInfo(enterpriseId,businessId);
        deletePatrolDetail(enterpriseId,tbPatrolStoreRecordDO,businessId);
        return true;
    }

    private void deletePatrolDetail(String enterpriseId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO,Long businessId) {
        TbPatrolStoreRecordDO newRecordDo = removeBeforeDetail(tbPatrolStoreRecordDO,businessId);
        List<TbDataTableDO> tbDataTableDOs = removeBeforeTbDataTableDO(enterpriseId,businessId,tbPatrolStoreRecordDO.getTaskId());
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDO = removeBeforeTbDataStaTableColumnDO(enterpriseId,businessId,tbPatrolStoreRecordDO.getTaskId());
        tbPatrolStoreRecordMapper.clearDetail(enterpriseId, newRecordDo);
        tbDataTableMapper.updateForeachByPrimaryKeySelective(tbDataTableDOs, enterpriseId);
        tbDataStaTableColumnMapper.updateByList(tbDataStaTableColumnDO, enterpriseId);
    }

    private List<TbDataStaTableColumnDO> removeBeforeTbDataStaTableColumnDO(String enterpriseId,Long businessId,Long taskId) {
        List<TbDataStaTableColumnDO> tbDataStaTableColumnDOS = tbDataStaTableColumnMapper.selectByBusinessIdAndTaskId(enterpriseId, businessId, taskId);
        for (TbDataStaTableColumnDO tbDataStaTableColumnDO : tbDataStaTableColumnDOS) {
            tbDataStaTableColumnDO.setCheckResult("");
            tbDataStaTableColumnDO.setCheckResultId(0L);
            tbDataStaTableColumnDO.setCheckResultName("");
            tbDataStaTableColumnDO.setCheckPics("");
            tbDataStaTableColumnDO.setCheckText("");
            tbDataStaTableColumnDO.setCheckVideo("");
            tbDataStaTableColumnDO.setCheckScore(BigDecimal.ZERO);
            tbDataStaTableColumnDO.setSubmitStatus(0);
            tbDataStaTableColumnDO.setRewardPenaltMoney(BigDecimal.ZERO);
            tbDataStaTableColumnDO.setWeightPercent(BigDecimal.ONE);
            tbDataStaTableColumnDO.setColumnMaxScore(BigDecimal.ZERO);
            tbDataStaTableColumnDO.setColumnMaxAward(BigDecimal.ZERO);
        }
        return tbDataStaTableColumnDOS;
    }

    private List<TbDataTableDO> removeBeforeTbDataTableDO(String eid,Long businessId,Long taskId) {
        List<TbDataTableDO> tbDataTableDOs = tbDataTableMapper.selectByBusinessIdAndTaskId(eid, businessId, taskId);
        if (CollectionUtils.isEmpty(tbDataTableDOs)){
            return null;
        }
        for (TbDataTableDO tbDataTableDO : tbDataTableDOs) {
            tbDataTableDO = tbDataTableDO;
            tbDataTableDO.setSubmitStatus(0);
            tbDataTableDO.setTotalScore(BigDecimal.ZERO);
            tbDataTableDO.setCheckScore(BigDecimal.ZERO);
            tbDataTableDO.setTaskCalTotalScore(BigDecimal.ZERO);
            tbDataTableDO.setTotalResultAward(BigDecimal.ZERO);
            tbDataTableDO.setFailNum(0);
            tbDataTableDO.setPassNum(0);
            tbDataTableDO.setInapplicableNum(0);
            tbDataTableDO.setTotalCalColumnNum(0);
            tbDataTableDO.setCheckResultLevel("0");
            tbDataTableDO.setSignStartTime(null);
        }
        return tbDataTableDOs;
    }

    private TbPatrolStoreRecordDO removeBeforeDetail(TbPatrolStoreRecordDO tbPatrolStoreRecordDO,Long businessId) {
        TbPatrolStoreRecordDO newRecordDo = new TbPatrolStoreRecordDO();
        BeanUtils.copyProperties(tbPatrolStoreRecordDO, newRecordDo);
        newRecordDo.setSignStartTime(null);
        newRecordDo.setSignStartAddress("");
        newRecordDo.setStartLongitudeLatitude("");
        newRecordDo.setPassNum(0);
        newRecordDo.setSummary("");
        newRecordDo.setSupervisorSignature("");
        newRecordDo.setSummaryVideo("");
        newRecordDo.setScore(BigDecimal.ZERO);
        newRecordDo.setFailNum(0);
        newRecordDo.setPassNum(0);
        newRecordDo.setInapplicableNum(0);
        newRecordDo.setTaskCalTotalScore(BigDecimal.ZERO);
        newRecordDo.setTotalCalColumnNum(0);
        newRecordDo.setTotalResultAward(BigDecimal.ZERO);
        newRecordDo.setCheckResultLevel(Constants.ZERO_STR);
        newRecordDo.setSubmitStatus(0);
        newRecordDo.setNeedRecheck(Boolean.FALSE);
        newRecordDo.setId(businessId);
        newRecordDo.setSignInStatus(0);
        log.info("removeBeforeDetail newRecordDo：{}",JSONObject.toJSONString(newRecordDo));
        return newRecordDo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean configMetaTable(String enterpriseId, MetaTableConfigParam param) {
        // 新的检查表信息
        List<Long> newMetaTableIds = param.getMetaTableIds();
        // 查询巡店记录
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO =
                tbPatrolStoreRecordMapper.selectById(enterpriseId, param.getBusinessId());
        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该businessId无对应记录，businessId:" + param.getBusinessId());
        }
        if (tbPatrolStoreRecordDO.getTaskId() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "非自主巡店，无法重新配置检查表");
        }
        if (tbPatrolStoreRecordDO.getStatus() == 1) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "巡店已完成，无法重新配置检查表");
        }
        Long metaTableId = 0L;
        String tableType = null;
        if (CollectionUtils.isNotEmpty(newMetaTableIds)) {
            metaTableId = newMetaTableIds.get(0);
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
            if (tbMetaTableDO != null) {
                tableType = tbMetaTableDO.getTableType();
            }

        }
        // 更新记录表的检查表信息
        TbPatrolStoreRecordDO tb = TbPatrolStoreRecordDO.builder()
                .metaTableIds(Constants.COMMA + (newMetaTableIds.stream().map(String::valueOf).collect(Collectors.joining(","))) + Constants.COMMA)
                .metaTableId(metaTableId).tableType(tableType)
                .id(tbPatrolStoreRecordDO.getId()).build();
        if (CollectionUtils.isEmpty(newMetaTableIds)){
            tb.setMetaTableIds("");
        }
        tbPatrolStoreRecordMapper.updateById(enterpriseId, tb);
        // 旧的检查表信息
        String oldMetaTableIdsStr = tbPatrolStoreRecordDO.getMetaTableIds();
        List<Long> oldMetaTableIds = new ArrayList<>();
        if (StringUtils.isNotBlank(oldMetaTableIdsStr)) {
            if(oldMetaTableIdsStr.startsWith(Constants.COMMA) && oldMetaTableIdsStr.endsWith(Constants.COMMA)){
                oldMetaTableIdsStr = oldMetaTableIdsStr.substring(1,oldMetaTableIdsStr.length()-1);
            }
            oldMetaTableIds
                    .addAll(Arrays.stream(oldMetaTableIdsStr.split(",")).filter(x->StringUtils.isNotBlank(x)).map(Long::valueOf).collect(Collectors.toList()));
        }
        // 新增
        List<Long> addMetaTableIds =
                newMetaTableIds.stream().filter(a -> !oldMetaTableIds.contains(a)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(addMetaTableIds)) {
            addMetaTable(enterpriseId, tbPatrolStoreRecordDO, addMetaTableIds);
        }
        // 移除
        List<Long> rmMetaTableIds =
                oldMetaTableIds.stream().filter(a -> !newMetaTableIds.contains(a)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(rmMetaTableIds)) {
            rmMetaTable(enterpriseId, param.getBusinessId(), rmMetaTableIds);
        }
        //全部提交,更新检查表状态所有已提交
        List<TbDataTableDO> resultList = tbDataTableMapper.selectByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId(), PATROL_STORE);
        List<TbDataTableDO> notSubmitList = ListUtils.emptyIfNull(resultList).stream().filter(dataTable -> (dataTable.getSubmitStatus() & 1) == 0).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(resultList) && CollectionUtils.isEmpty(notSubmitList)){
            tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, param.getBusinessId(),Constants.INDEX_ONE|tbPatrolStoreRecordDO.getSubmitStatus());
        }
        return true;
    }

    /**
     * 记录新增检查表
     */
    @Override
    public void addMetaTable(String enterpriseId, TbPatrolStoreRecordDO record, List<Long> addMetaTableIds) {
        if (CollectionUtils.isEmpty(addMetaTableIds)) {
            return;
        }
        // 锁定检查表元数据meta_table
        tbMetaTableMapper.updateLockedByIds(enterpriseId, addMetaTableIds);
        List<TbMetaTableDO> metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, addMetaTableIds);
        // 初始化检查表data_table
        List<TbDataTableDO> tbDataTableDOList = metaTableDOList.stream()
                .map(metaTableDO -> TbDataTableDO.builder().taskId(record.getTaskId()).subTaskId(record.getSubTaskId())
                        .storeId(record.getStoreId()).storeName(record.getStoreName()).regionId(record.getRegionId())
                        .regionPath(record.getRegionWay()).businessId(record.getId()).businessType(PATROL_STORE)
                        .metaTableId(metaTableDO.getId()).tableName(metaTableDO.getTableName()).tableProperty(metaTableDO.getTableProperty())
                        .description(metaTableDO.getDescription()).createUserId(record.getCreateUserId()).patrolType(record.getPatrolType())
                        .signEndTime(record.getSignEndTime()).signStartTime(record.getSignStartTime())
                        .supervisorId(record.getSupervisorId()).supportScore(metaTableDO.getSupportScore())
                        .tableType(metaTableDO.getTableType()).build())
                .collect(Collectors.toList());
        List<TbDataTableDO> exsitDataTableList = tbDataTableMapper.selectByBusinessId(enterpriseId, record.getId(), PATROL_STORE);
        if(CollectionUtils.isNotEmpty(exsitDataTableList) && exsitDataTableList.size() == 1){
            TbDataTableDO exsitOneDataTable = exsitDataTableList.get(0);
            if(exsitOneDataTable.getMetaTableId() != null && exsitOneDataTable.getMetaTableId() == 0){
                exsitOneDataTable.setMetaTableId(addMetaTableIds.get(0));
            }
        }
        Map<Long, TbDataTableDO> exsitDataTableMap = ListUtils.emptyIfNull(exsitDataTableList).stream().collect(Collectors.toMap(TbDataTableDO::getMetaTableId, Function.identity()));

        for (TbDataTableDO tbDataTableDO : tbDataTableDOList) {
            TbDataTableDO exsitDataTable = exsitDataTableMap.get(tbDataTableDO.getMetaTableId());
            if (exsitDataTable == null) {
                tbDataTableMapper.batchInsert(enterpriseId, Collections.singletonList(tbDataTableDO));
            } else {
                //dataTableId 不能为null，插入的时候返回，更新的时候手动设置
                tbDataTableDO.setId(exsitDataTable.getId());
                //检查表切换成功，将检查表提交状态修改为0
                //使用与运算
                int submitsStatus = exsitDataTable.getSubmitStatus() & 6;
                tbDataTableMapper.updateSubmitStatus(enterpriseId,tbDataTableDO.getId(),submitsStatus);
                tbDataTableMapper.updateTbDateTableByBusinessId(enterpriseId, tbDataTableDO);
            }
        }


        // 初始化标准检查项data_sta_table_column
        List<Long> metaStaTableIds = metaTableDOList.stream().filter(a -> !TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaStaTableIds)) {
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList =
                    tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, metaStaTableIds,Boolean.TRUE);
            Map<Long, List<TbMetaStaTableColumnDO>> metaTableIdColumnListMap = tbMetaStaTableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));

            List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList =
                    tbDataTableDOList.stream().filter(a -> !TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                            .flatMap(a -> metaTableIdColumnListMap.get(a.getMetaTableId()).stream()
                                    .map(column -> TbDataStaTableColumnDO.builder().categoryName(column.getCategoryName())
                                            .dataTableId(a.getId()).metaTableId(a.getMetaTableId()).metaColumnId(column.getId())
                                            .metaColumnName(column.getColumnName()).description(column.getDescription())
                                            .createUserId(a.getCreateUserId()).supervisorId(a.getSupervisorId()).storeId(a.getStoreId())
                                            .storeName(a.getStoreName()).regionId(a.getRegionId())
                                            .regionWay(a.getRegionPath()).columnType(column.getColumnType()).patrolType(record.getPatrolType())
                                            .taskId(a.getTaskId()).subTaskId(a.getSubTaskId()).businessId(a.getBusinessId())
                                            .checkResult("").checkResultId(0L).checkResultName("").checkPics("").checkText("")
                                            .checkVideo("").checkScore(BigDecimal.ZERO).scoreTimes(BigDecimal.ONE).awardTimes(BigDecimal.ONE).weightPercent(BigDecimal.ONE)
                                            .businessType(PATROL_STORE).createDate(DateUtils.getTime(record.getCreateTime())).createTime(record.getCreateTime()).build()))
                            .collect(Collectors.toList());
            tbDataStaTableColumnMapper.batchInsert(enterpriseId, tbDataStaTableColumnDOList);
        }
        // 初始化自定义检查项data_def_table_column
        List<Long> metaDefTableIds = metaTableDOList.stream().filter(a -> TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaDefTableIds)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList =
                    tbMetaDefTableColumnMapper.selectByTableIds(enterpriseId, metaDefTableIds);
            Map<Long, List<TbMetaDefTableColumnDO>> metaTableIdColumnListMap = tbMetaDefTableColumnDOList.stream()
                    .collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
            List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList =
                    tbDataTableDOList.stream().filter(a -> TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                            .flatMap(a -> metaTableIdColumnListMap.get(a.getMetaTableId()).stream()
                                    .map(column -> TbDataDefTableColumnDO.builder().dataTableId(a.getId())
                                            .metaTableId(a.getMetaTableId()).metaColumnId(column.getId()).patrolType(record.getPatrolType())
                                            .metaColumnName(column.getColumnName()).description(column.getDescription())
                                            .createUserId(a.getCreateUserId()).supervisorId(a.getSupervisorId()).storeId(a.getStoreId())
                                            .storeName(a.getStoreName()).regionId(a.getRegionId()).regionPath(a.getRegionPath())
                                            .taskId(a.getTaskId()).subTaskId(a.getSubTaskId()).businessId(a.getBusinessId())
                                            .businessType(PATROL_STORE).patrolStoreTime(record.getCreateTime()).build()))
                            .collect(Collectors.toList());
            tbDataDefTableColumnMapper.batchInsert(enterpriseId, tbDataDefTableColumnDOList);
        }
    }

    /**
     * 记录移除检查表
     */
    private void rmMetaTable(String enterpriseId, @NotNull(message = "巡店记录id不能为空") Long businessId,
                             List<Long> rmMetaTableIds) {
        if (CollectionUtils.isEmpty(rmMetaTableIds)) {
            return;
        }
        List<TbMetaTableDO> metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, rmMetaTableIds);
        // 硬删除标准检查项
        List<Long> metaStaTableIds = metaTableDOList.stream().filter(a -> !TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaStaTableIds)) {
            tbDataStaTableColumnMapper.delByBusinessIdAndMetaTableIds(enterpriseId, businessId, PATROL_STORE,
                    metaStaTableIds);
        }
        // 硬删除自定义检查项
        List<Long> metaDefTableIds = metaTableDOList.stream().filter(a ->TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType()))
                .map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(metaDefTableIds)) {
            tbDataDefTableColumnMapper.delByBusinessIdAndMetaTableIds(enterpriseId, businessId, PATROL_STORE,
                    metaDefTableIds);
        }
        // 硬删除检查表
        tbDataTableMapper.delByBusinessIdAndMetaTableIds(enterpriseId, businessId, PATROL_STORE, rmMetaTableIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(String enterpriseId, PatrolStoreSubmitParam param, String userId) {
        boolean submit = param.getSubmit() == null || param.getSubmit();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO =
                tbPatrolStoreRecordMapper.selectById(enterpriseId, param.getBusinessId());
        if (tbPatrolStoreRecordDO == null || tbPatrolStoreRecordDO.getDeleted() != 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(),
                    "该businessId无对应记录，businessId:" + param.getBusinessId());
        }
        String businessType = tbPatrolStoreRecordDO.getBusinessCheckType();
        if(StringUtils.isBlank(businessType)){
            businessType = PATROL_STORE;
        }
        // 稽核巡店 结束仍可以修改结果
        if (tbPatrolStoreRecordDO.getStatus() == 1
                && !TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已处理，无法再次提交");
        }
        if (tbPatrolStoreRecordDO.getSignInStatus() == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店未签到，无法提交表单");
        }
        TbDataTableDO tbDataTableDo = tbDataTableMapper.selectById(enterpriseId, param.getDataTableId());
        if(Objects.isNull(tbDataTableDo)){
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该检查表不存在");
        }
        if ((tbDataTableDo.getSubmitStatus() & 1) == 1 && !submit) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店已提交检查表，无法再次保存");
        }

        // 记录检查表停留时间
        recordTableDwellTime(enterpriseId, param.getBusinessId(), param.getDwellTime(), param.getDataTableId());

        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbDataTableDo.getMetaTableId());
        if(!TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(), metaTableDO.getTableType())){
            if (CollectionUtils.isEmpty(param.getDataStaTableColumnParamList())) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "sta提交内容为空");
            }
            boolean isSafetyCheckFinish = submit && TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType()) && tbPatrolStoreRecordDO.getStatus() == 1;
            // 转换项
            List<TbDataStaTableColumnDO> staColumnList = PatrolStoreSubmitParam.convertDataColumnList(param.getDataStaTableColumnParamList(), userId, isSafetyCheckFinish);
            //上传视频处理，从缓存获取转码后的url
            checkVideoHandel(staColumnList,enterpriseId);
            tbDataStaTableColumnMapper.batchUpdate(enterpriseId, param.getBusinessId(), businessType, param.getDataTableId(), staColumnList, submit);
            //如果所有项提交，自动提交表单
            if(!submit){
                Integer notSubmitCount = tbDataStaTableColumnMapper.dataStaColumnNotSubmitCount(enterpriseId, param.getDataTableId());
                if(notSubmitCount != null && notSubmitCount == 0){
                    submit = true;
                }
            }
            if(submit){
                //计算得分
                countScore(enterpriseId, tbPatrolStoreRecordDO, metaTableDO, tbDataTableDo.getId());
                // 计算巡店记录的分数
                countPatrolStoreRecordScore(enterpriseId, param, tbPatrolStoreRecordDO.getBusinessCheckType());
            }
            // 稽核类型  完成状态下  稽稽核完成修改结果对新产生的不合格项发起工单
            if(isSafetyCheckFinish){
                // 修改结果比对插入历史
                scSafetyCheckFlowService.buildColumnCheckHistory(enterpriseId, param.getBusinessId(),param.getDataTableId(), userId);
                try {
                    String operateType = PatrolStoreConstant.PatrolStoreOperateTypeConstant.EDIT_RESULT;
                    String username = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, userId);
                    tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, TbPatrolStoreHistoryDo.builder().createTime(new Date())
                            .updateTime(new Date()).actionKey("").businessId(tbPatrolStoreRecordDO.getId())
                            .deleted(false).nodeNo(UnifyNodeEnum.END_NODE.getCode()).operateType(operateType)
                            .operateUserName(username).operateUserId(userId).subTaskId(0L).remark("").build());
                    simpleMessageService.send(JSONObject.toJSONString(new PatrolStoreScoreMsgDTO(enterpriseId, tbPatrolStoreRecordDO.getId(), 0L, tbPatrolStoreRecordDO.getSupervisorId(), "", true)),
                            RocketMqTagEnum.PATROL_STORE_SCORE_COUNT_QUEUE, System.currentTimeMillis() + 1000);
                } catch (Exception e) {
                    log.error("稽核完成修改结果对新产生的不合格项发起工单 businessId:{}", tbPatrolStoreRecordDO.getId(), e);
                }
            }
        }else {
            if (CollectionUtils.isEmpty(param.getDataDefTableColumnParamList())) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "def提交内容为空");
            }
            // 修改自定义检查项
            List<TbDataDefTableColumnDO> defColumnList =
                    param.getDataDefTableColumnParamList().stream().map(a -> TbDataDefTableColumnDO.builder().checkVideo(a.getCheckVideo())
                                    .id(a.getId()).value1(a.getValue1()).value2(a.getValue2()).supervisorId(userId).submitStatus(a.getSubmitStatus()).build())
                            .collect(Collectors.toList());
            //上传视频处理，从缓存获取转码后的url
            checkDefTableVideoHandel(defColumnList,enterpriseId);
            tbDataDefTableColumnMapper.batchUpdate(enterpriseId, param.getBusinessId(), businessType,
                    param.getDataTableId(), defColumnList, submit);

            //如果所有项提交，自动提交表单
            if(!submit){
                Integer notSubmitCount = tbDataDefTableColumnMapper.dataDefColumnNotSubmitCount(enterpriseId, param.getDataTableId());
                if(notSubmitCount != null && notSubmitCount == 0){
                    submit = true;
                }
            }
        }
        // 修改检查表提交状态
        if (submit) {
            //修改状态之前，先查询总结或者签名是否已经提交
            PatrolRecordRequest query = new PatrolRecordRequest();
            query.setDataTableId(param.getDataTableId());
            //使用或运算
            query.setSubmitStatus(Constants.INDEX_ONE);
            log.info("##dataTableId:{}", param.getDataTableId());
            tbDataTableMapper.changeDataTableSubmitStatus(enterpriseId, query);
            //全部提交,更新检查表状态所有已提交
            List<TbDataTableDO> resultList = tbDataTableMapper.selectByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId(), businessType);
            resultList = resultList.stream().filter(dataTable -> (dataTable.getSubmitStatus() & 1) == 0).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(resultList)){
                tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, param.getBusinessId(),Constants.INDEX_ONE|tbPatrolStoreRecordDO.getSubmitStatus());
            }
            if(TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())){
                //如果是视频巡店 则处理ai数据
                String uuid = UUIDUtils.get32UUID();
                log.info("ai分析 requestId:{}", uuid);
                String finalBusinessType = businessType;
                // 标记为正在进行AI分析
                PatrolRecordRequest submitUpdateRequest = new PatrolRecordRequest();
                submitUpdateRequest.setDataTableId(param.getDataTableId());
                submitUpdateRequest.setSubmitStatus(Constants.SUBMITSTATUS_EIGHT);
                tbDataTableMapper.changeDataTableSubmitStatus(enterpriseId, submitUpdateRequest);
                Executors.newFixedThreadPool(2).execute(() -> dealAi(enterpriseId, param, tbPatrolStoreRecordDO, metaTableDO, tbDataTableDo.getId(), finalBusinessType, uuid));
            }
        }
        return submit;
    }

    /**
     * 记录检查表停留时间
     * @param enterpriseId 企业id
     * @param businessId 业务id
     * @param dwellTime 停留时间
     * @param dataTableId 检查表id
     */
    private void recordTableDwellTime(String enterpriseId, Long businessId, String dwellTime, Long dataTableId) {
        TbPatrolStoreRecordInfoDO dbRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, businessId);
        if (StringUtils.isBlank(dwellTime) || Objects.isNull(dbRecordInfoDO)) {
            return;
        }
        JSONObject paramJson = new JSONObject();
        if(StringUtils.isNotBlank(dbRecordInfoDO.getParams())){
            paramJson = JSONObject.parseObject(dbRecordInfoDO.getParams());
        }
        JSONObject dataTableDwellTime = paramJson.getJSONObject("dataTableDwellTime");
        if (dataTableDwellTime == null) {
            dataTableDwellTime = new JSONObject();
        }
        dataTableDwellTime.put(String.valueOf(dataTableId), dwellTime);
        paramJson.put("dataTableDwellTime", dataTableDwellTime);
        TbPatrolStoreRecordInfoDO record = new TbPatrolStoreRecordInfoDO();
        record.setId(businessId);
        record.setEid(enterpriseId);
        record.setParams(JSONObject.toJSONString(paramJson));
        tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId, record);
    }

    /**
     *
     * @param enterpriseId
     * @param param
     * @param businessType
     * @param requestId
     */
    private void dealAi(String enterpriseId, PatrolStoreSubmitParam param, TbPatrolStoreRecordDO recordDO, TbMetaTableDO tbMetaTable, Long dataTableId, String businessType, String requestId) {
        MDCUtils.put(Constants.REQUEST_ID, requestId);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        try {
            List<TbDataStaTableColumnDO> columnList = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, param.getBusinessId(), businessType);
            if(CollectionUtils.isEmpty(columnList)){
                log.info("检查项为空了不需要");
                return;
            }
            List<Long> metaColumnIds = CollStreamUtil.toList(columnList, TbDataStaTableColumnDO::getMetaColumnId);
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, metaColumnIds);
            Map<Long, TbMetaStaTableColumnDO> metaColumnMap = CollStreamUtil.toMap(tbMetaStaTableColumnDOS, TbMetaStaTableColumnDO::getId, Function.identity());

            Set<String> aiModelCodes = CollStreamUtil.toSet(tbMetaStaTableColumnDOS, TbMetaStaTableColumnDO::getAiModel);
            Map<String, AiModelLibraryDO> aiModelMap = aiModelLibraryService.getModelMapByCodes(new ArrayList<>(aiModelCodes));

            List<TbDataStaTableColumnDO> aiCloumnList = columnList.stream()
                    .filter(o->StringUtils.isNotBlank(o.getCheckPics()) && YesOrNoEnum.YES.getCode().equals(Optional.ofNullable(metaColumnMap.get(o.getMetaColumnId())).map(TbMetaStaTableColumnDO::getIsAiCheck).orElse(0)))
                    .collect(Collectors.toList());
            if(CollectionUtils.isEmpty(aiCloumnList)){
                log.info("ai检查项为空了不需要");
                return;
            }
            List<Long> meteColumnIds = aiCloumnList.stream().map(TbDataStaTableColumnDO::getMetaColumnId).collect(Collectors.toList());
            List<TbMetaColumnResultDO> columnResultList = tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, meteColumnIds);
            Map<Long, List<TbMetaColumnResultDO>> columnResultMap = CollStreamUtil.groupByKey(columnResultList, TbMetaColumnResultDO::getMetaColumnId);
            List<Future<Boolean>> futureList = new ArrayList<>();
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            for (TbDataStaTableColumnDO column : aiCloumnList) {
                if(StringUtils.isBlank(column.getCheckPics())){
                    continue;
                }
                futureList.add(EXECUTOR_SERVICE.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    List<String> imageUrls = ImageUtil.getImageList(column.getCheckPics());
                    TbMetaStaTableColumnDO staTableColumnDO = metaColumnMap.get(column.getMetaColumnId());
                    List<TbMetaColumnResultDO> staColumnResultList = columnResultMap.get(column.getMetaColumnId());
                    AIResolveDTO aiResolveDTO = aiService.aiPatrolResolve(enterpriseId, aiModelMap.get(staTableColumnDO.getAiModel()), imageUrls, staTableColumnDO, staColumnResultList, AICommentStyleEnum.DETAIL.getStyle());
                    column.setCheckText(aiResolveDTO.getAiComment());
                    TbDataStaTableColumnDO updateColumn = new TbDataStaTableColumnDO();
                    updateColumn.setId(column.getId());
                    updateColumn.setCheckText(aiResolveDTO.getAiComment());
                    updateColumn.setCheckScore(aiResolveDTO.getAiScore());
                    if (Objects.nonNull(aiResolveDTO.getColumnResult())) {
                        updateColumn.setCheckResult(aiResolveDTO.getColumnResult().getMappingResult());
                        updateColumn.setCheckResultId(aiResolveDTO.getColumnResult().getId());
                        updateColumn.setCheckResultName(aiResolveDTO.getColumnResult().getResultName());
                    } else {
                        updateColumn.setCheckResult(CheckResultEnum.PASS.getCode());
                    }
                    tbDataStaTableColumnMapper.updateByPrimaryKeySelective(updateColumn, enterpriseId);
                    return true;
                }));
            }
            futureList.forEach(v -> {
                try {
                    v.get();
                } catch (ServiceException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("AI分析异常", e.getCause());
                    throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
                }
            });
            //计算得分
            countScore(enterpriseId, recordDO, tbMetaTable, dataTableId);
            // 计算巡店记录的分数
            countPatrolStoreRecordScore(enterpriseId, param, businessType);
            // 将dataTable的标记为置零
            TbDataTableDO tbDataTableDO = tbDataTableMapper.selectById(enterpriseId, dataTableId);
            if ((Constants.SUBMITSTATUS_EIGHT & tbDataTableDO.getSubmitStatus()) == Constants.SUBMITSTATUS_EIGHT) {
                tbDataTableMapper.updateSubmitStatus(enterpriseId, dataTableId, tbDataTableDO.getSubmitStatus() & ~(Constants.SUBMITSTATUS_EIGHT));
            }
        } catch (Exception e) {
            log.info("ai 分析异常", e);
        }
    }

    /**
     * 从包含多个图片地址的字符串中提取 URL 列表
     *
     * @param imageUrls 包含多个图片地址的字符串
     * @return 提取到的图片地址列表
     */
    public static List<String> extractImageUrls(String imageUrls) {
        List<String> urlList = new ArrayList<>();
        if (imageUrls == null || imageUrls.isEmpty()) {
            return urlList;
        }
        // 正则表达式匹配以 http:// 或 https:// 开头的 URL
        String regex = "(https?://[^\\s,]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(imageUrls);

        // 提取所有匹配的 URL
        while (matcher.find()) {
            urlList.add(matcher.group(1));
        }
        return urlList;
    }

    @Override
    public void countPatrolStoreRecordScore(String enterpriseId, PatrolStoreSubmitParam param, String businessCheckType) {
        if(StringUtils.isBlank(businessCheckType)){
            businessCheckType = PATROL_STORE;
        }
        TbPatrolStoreRecordStatisticsDTO patrolStoreRecordStatisticsDTO = tbDataTableMapper.statisticsWhenCountScore(enterpriseId, param.getBusinessId(), businessCheckType);
        TbPatrolStoreRecordDO recordDONew = new  TbPatrolStoreRecordDO();
        recordDONew.setScore(patrolStoreRecordStatisticsDTO.getScore());
        handleMengZiYuanScore(enterpriseId, param, businessCheckType, recordDONew);
        recordDONew.setTaskCalTotalScore(patrolStoreRecordStatisticsDTO.getTaskCalTotalScore());
        recordDONew.setTotalCalColumnNum(patrolStoreRecordStatisticsDTO.getTotalCalColumnNum());
        recordDONew.setCollectColumnNum(patrolStoreRecordStatisticsDTO.getCollectColumnNum());
        recordDONew.setPassNum(patrolStoreRecordStatisticsDTO.getPassNum());
        recordDONew.setFailNum(patrolStoreRecordStatisticsDTO.getFailNum());
        recordDONew.setInapplicableNum(patrolStoreRecordStatisticsDTO.getInapplicableNum());
        recordDONew.setId(patrolStoreRecordStatisticsDTO.getId());
        recordDONew.setTotalResultAward(patrolStoreRecordStatisticsDTO.getTotalResultAward());
        // 取最小的等级
        String checkResultLevel =  patrolStoreRecordStatisticsDTO.getCheckResultLevel();
        if(StringUtils.isNotBlank(checkResultLevel) && checkResultLevel.contains(LevelRuleEnum.DISQUALIFICATION.getCode())){
            checkResultLevel = LevelRuleEnum.DISQUALIFICATION.getCode();
        }else if( StringUtils.isNotBlank(checkResultLevel) && checkResultLevel.contains(LevelRuleEnum.ELIGIBLE.getCode())){
            checkResultLevel = LevelRuleEnum.ELIGIBLE.getCode();
        }else if(StringUtils.isNotBlank(checkResultLevel) && checkResultLevel.contains(LevelRuleEnum.GOOD.getCode())){
            checkResultLevel = LevelRuleEnum.GOOD.getCode();
        } else if(StringUtils.isNotBlank(checkResultLevel) && checkResultLevel.contains(LevelRuleEnum.EXCELLENT.getCode())){
            checkResultLevel = LevelRuleEnum.EXCELLENT.getCode();
        } else {
            checkResultLevel = "";
        }
        recordDONew.setCheckResultLevel(checkResultLevel);
        // 修改巡店记录
        tbPatrolStoreRecordMapper.updateById(enterpriseId, recordDONew);
    }

    private void handleMengZiYuanScore(String enterpriseId, PatrolStoreSubmitParam param, String businessCheckType, TbPatrolStoreRecordDO recordDONew) {
        if(Constants.MENGZIYUAN_ENTERPRISE_ID.equals(enterpriseId)){
            List<TbDataTableDO> tbDataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, param.getBusinessId(), businessCheckType);
            Map<Long, TbDataTableDO> tbDataTableDOMap = ListUtils.emptyIfNull(tbDataTableDOList).stream().collect(Collectors.toMap(k -> k.getMetaTableId(), Function.identity()));
            Set<Long> selectTableIdSet = ListUtils.emptyIfNull(tbDataTableDOList).stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toSet());
            Set<Long> customTableIdSet = MengZiYuanTableWeightEnum.getCustomTableIdSet();
            log.info("蒙自源POC环境按指定检查表的权重计算分数，自定义的表：{} ,选择的表：{} ", JSONObject.toJSONString(customTableIdSet), JSONObject.toJSONString(selectTableIdSet));
            if(customTableIdSet.equals(selectTableIdSet)){
                BigDecimal totalScore = new BigDecimal(Constants.ZERO_STR);
                for (MengZiYuanTableWeightEnum value : MengZiYuanTableWeightEnum.values()) {
                    TbDataTableDO tbDataTableDO = tbDataTableDOMap.get(value.getId());
                    if(value == MengZiYuanTableWeightEnum.FOOD_SAFETY){
                        totalScore = totalScore.add(value.getWeight().multiply(BigDecimal.valueOf(tbDataTableDO.getFailNum())));
                    }else {
                        totalScore = totalScore.add(value.getWeight().multiply(tbDataTableDO.getCheckScore()));
                    }
                }
                totalScore = totalScore.setScale(2,BigDecimal.ROUND_HALF_UP);
                recordDONew.setScore(totalScore);
            }
        }else if(Constants.MENGZIYUAN2_ENTERPRISE_ID.equals(enterpriseId)){
            List<TbDataTableDO> tbDataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, param.getBusinessId(), businessCheckType);
            Map<Long, TbDataTableDO> tbDataTableDOMap = ListUtils.emptyIfNull(tbDataTableDOList).stream().collect(Collectors.toMap(k -> k.getMetaTableId(), Function.identity()));
            Set<Long> selectTableIdSet = ListUtils.emptyIfNull(tbDataTableDOList).stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toSet());
            Set<Long> customTableIdSet = MengZiYuan2TableWeightEnum.getCustomTableIdSet();
            log.info("蒙自源2POC环境按指定检查表的权重计算分数，自定义的表：{} ,选择的表：{} ", JSONObject.toJSONString(customTableIdSet), JSONObject.toJSONString(selectTableIdSet));
            if(customTableIdSet.equals(selectTableIdSet)){
                BigDecimal totalScore = new BigDecimal(Constants.ZERO_STR);
                for (MengZiYuan2TableWeightEnum value : MengZiYuan2TableWeightEnum.values()) {
                    TbDataTableDO tbDataTableDO = tbDataTableDOMap.get(value.getId());
                    if(value == MengZiYuan2TableWeightEnum.FOOD_SAFETY){
                        totalScore = totalScore.add(value.getWeight().multiply(BigDecimal.valueOf(tbDataTableDO.getFailNum())));
                    }else {
                        totalScore = totalScore.add(value.getWeight().multiply(tbDataTableDO.getCheckScore()));
                    }
                }
                totalScore = totalScore.setScale(2,BigDecimal.ROUND_HALF_UP);
                recordDONew.setScore(totalScore);
            }
        }
    }


    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     * @author chenyupeng
     * @date 2021/10/14
     * @param tbDataStaTableColumnDOList
     * @param enterpriseId
     * @return void
     */
    @Override
    public void checkVideoHandel(List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList,String enterpriseId){
        if(CollectionUtils.isEmpty(tbDataStaTableColumnDOList)){
            return;
        }
        for (TbDataStaTableColumnDO tbDataStaTableColumnDO : tbDataStaTableColumnDOList) {
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(tbDataStaTableColumnDO.getCheckVideo(), SmallVideoInfoDTO.class);
            if(smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())){
                String callbackCache;
                SmallVideoDTO smallVideoCache;
                SmallVideoParam smallVideoParam;
                for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                    //如果转码完成
                    if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                        continue;
                    }
                    callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                    if(StringUtils.isNotBlank(callbackCache)){
                        smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                        if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >= 3){
                            BeanUtils.copyProperties(smallVideoCache,smallVideo);
                        }else {
                            smallVideoParam = new SmallVideoParam();
                            setNotCompleteCache(smallVideoParam,smallVideo,tbDataStaTableColumnDO.getId(),enterpriseId);
                        }
                    }else {
                        smallVideoParam = new SmallVideoParam();
                        setNotCompleteCache(smallVideoParam,smallVideo,tbDataStaTableColumnDO.getId(),enterpriseId);
                    }
                }
                tbDataStaTableColumnDO.setCheckVideo(JSONObject.toJSONString(smallVideoInfo));
            }
        }

    }

    /**
     * 自定义检查表视频转码
     * @param tbDataDefTableColumnDOList
     * @param enterpriseId
     */
    public void checkDefTableVideoHandel(List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList, String enterpriseId) {
        if (CollectionUtils.isEmpty(tbDataDefTableColumnDOList)) {
            return;
        }
        for (TbDataDefTableColumnDO dataDefTableColumnDO : tbDataDefTableColumnDOList) {
            SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(dataDefTableColumnDO.getCheckVideo(), SmallVideoInfoDTO.class);
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
                            setDefTableNotCompleteCache(smallVideoParam, smallVideo, dataDefTableColumnDO.getId(), enterpriseId);
                        }
                    } else {
                        smallVideoParam = new SmallVideoParam();
                        setDefTableNotCompleteCache(smallVideoParam, smallVideo, dataDefTableColumnDO.getId(), enterpriseId);
                    }
                }
                dataDefTableColumnDO.setCheckVideo(JSONObject.toJSONString(smallVideoInfo));
            }
        }
    }

    /**
     * 如果前端提交的时候，视频还没有转码成功，会把videoId存入缓存，回调的时候再进行处理
     * @author chenyupeng
     * @date 2021/10/14
     * @param smallVideoParam
     * @param smallVideo
     * @param businessId
     * @param enterpriseId
     * @return void
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam,SmallVideoDTO smallVideo,Long businessId,String enterpriseId){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.TB_DATA_STA_TABLE_COLUMN.getValue());
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(),JSONObject.toJSONString(smallVideoParam));
    }

    /**
     * 自定义检查表检查项转码设置
     * @param smallVideoParam
     * @param smallVideo
     * @param businessId
     * @param enterpriseId
     */
    public void setDefTableNotCompleteCache(SmallVideoParam smallVideoParam,SmallVideoDTO smallVideo,Long businessId,String enterpriseId){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.TB_DATA_DEF_TABLE_COLUMN.getValue());
        smallVideoParam.setBusinessId(businessId);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redisUtil.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(),JSONObject.toJSONString(smallVideoParam));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOnline(String dingCorpId, String enterpriseId, PatrolStoreSubmitOnlineParam param, EnterpriseStoreCheckSettingDO storeCheckSettingDO, String userId, String userName,String appType, EnterpriseSettingDO enterpriseSettingDO) {
        boolean submit = param.getSubmit() == null || param.getSubmit();
        // 自主巡店签到
        Long businessId = signIn(enterpriseId,
                PatrolStoreSignInParam.builder().businessId(param.getBusinessId()).signInStatus(1).userId(UserHolder.getUser().getUserId()).appType(appType)
                        .storeId(param.getStoreId()).patrolType(PATROL_STORE_ONLINE).signStartTime(param.getSignStartTime()).storeCheckSetting(storeCheckSettingDO)
                        .dingCorpId(dingCorpId)
                        .build());
        if (param.getBusinessId() == null) {
            // 自主巡店配置检查表
            configMetaTable(enterpriseId, MetaTableConfigParam.builder().businessId(businessId)
                    .metaTableIds(Lists.newArrayList(param.getMetaTableId())).build());
        }
        // 查询检查表信息
        List<DataTableInfoDTO> dataTableInfoDTOS = dataTableInfoList(enterpriseId, businessId, userId);

        // 转化成提交数据
        Assert.notEmpty(dataTableInfoDTOS, "检查表配置失败，param={},businessId={}", JSON.toJSONString(param), businessId);
        DataTableInfoDTO dataTableInfoDTO = dataTableInfoDTOS.get(0);
        Assert.isTrue(dataTableInfoDTO.getMetaTable().getId().equals(param.getMetaTableId()),
                "配置的检查表匹配失败，param={}，businessId={}，metaTable={}", JSON.toJSONString(param), businessId,
                dataTableInfoDTO.getMetaTable().getId());
        TbDataTableDO dataTable = dataTableInfoDTO.getDataTable();
        Map<Long, Long> metaDataColumnIdMap = dataTableInfoDTO.getDataStaColumns().stream()
                .filter(a -> a.getMetaColumnId() != null && a.getId() != null)
                .collect(Collectors.toMap(TbDataStaTableColumnVO::getMetaColumnId, TbDataStaTableColumnVO::getId, (a, b) -> a));
        List<PatrolStoreSubmitParam.DataStaTableColumnParam> dataStaTableColumnParamList =
                param.getDataStaTableColumnParamList().stream()
                        .map(a -> PatrolStoreSubmitParam.DataStaTableColumnParam.builder()
                                .id(metaDataColumnIdMap.get(a.getMetaColumnId())).checkPics(a.getCheckPics())
                                .checkResult(a.getCheckResult()).checkResultId(a.getCheckResultId())
                                .awardTimes(a.getAwardTimes()).scoreTimes(a.getScoreTimes())
                                .checkResultName(a.getCheckResultName()).checkScore(a.getCheckScore()).checkText(a.getCheckText())
                                .build())
                        .collect(Collectors.toList());
        // 提交数据
        submit(enterpriseId, PatrolStoreSubmitParam.builder().businessId(businessId).dataTableId(dataTable.getId())
                .dataStaTableColumnParamList(dataStaTableColumnParamList).submit(submit).build(), userId);
        if (submit) {
            // 签退
            signOut(dingCorpId, enterpriseId, PatrolStoreSignOutParam.builder().businessId(businessId)
                    .signEndTime(param.getSignEndTime()).signOutStatus(1).build(), storeCheckSettingDO, userId, userName,appType, enterpriseSettingDO);
//            //签退的时候添加修改钉钉端待办状态
//            sendUpcomingFinish(dingCorpId,enterpriseId,tbPatrolStoreRecordDO.getSubTaskId());
//            log.info("submitOnline.sendUpcomingFinish");
        }
        return businessId;
    }

    public void sendUpcomingFinish(String dingCorpId, String enterpriseId, Long unifyTaskSubId, String appType, String subTaskNodeNo) {
        log.info("dingCorpId={},enterpriseId={},unifyTaskSubId={}", dingCorpId, enterpriseId, unifyTaskSubId);
        //查询corpID
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("unifyTaskSubId", unifyTaskSubId);
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, unifyTaskSubId);
        if (taskSubDO == null) {
            log.info("没有该子任务 unifyTaskSubId={}", unifyTaskSubId);
            return;
        }
        Long unifyTaskId = taskSubDO.getUnifyTaskId();
        List<Long> taskSubVOStream = new ArrayList<>();
        Set<String> cancelUserIds = new HashSet<>();
        List<TaskSubVO> taskSubVOS = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, unifyTaskId, taskSubDO.getStoreId(), taskSubDO.getLoopCount());
        if (taskSubVOS == null) {
            log.info("没有对应的子任务 unifyTaskId = {}", unifyTaskId);
            return;
        }
        taskSubVOS.stream().filter(e -> e.getStoreId().equals(taskSubDO.getStoreId())).forEach(u -> {
            taskSubVOStream.add(u.getSubTaskId());
            cancelUserIds.add(u.getHandleUserId());
        });

        jsonObject.put("unifyTaskSubIdList", taskSubVOStream);
        jsonObject.put("appType",appType);
        simpleMessageService.send(jsonObject.toString(),RocketMqTagEnum.UPCOMING_FINISH);
        // 发新任务取消待办
        if (TaskTypeEnum.isCombineNoticeTypes(taskSubDO.getTaskType())) {
            unifyTaskService.cancelCombineUpcoming(enterpriseId, unifyTaskId, taskSubDO.getLoopCount(), taskSubDO.getStoreId(), subTaskNodeNo, new ArrayList<>(cancelUserIds), dingCorpId, appType);
        }
    }

    @Override
    public TbPatrolStoreRecordVO recordInfo(String enterpriseId, Long businessId, String accessToken,String key, EnterpriseStoreCheckSettingDO settingDO) {
        TbPatrolStoreRecordDO storeRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (storeRecordDO == null) {
            return null;
        }

        TbPatrolStoreRecordVO tbPatrolStoreRecordVO = new TbPatrolStoreRecordVO();
        BeanUtils.copyProperties(storeRecordDO, tbPatrolStoreRecordVO);

        TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, storeRecordDO.getId());
        if(tbPatrolStoreRecordInfoDO!=null){
            tbPatrolStoreRecordVO.setSignInRemark(tbPatrolStoreRecordInfoDO.getSignInRemark());
            tbPatrolStoreRecordVO.setSignOutRemark(tbPatrolStoreRecordInfoDO.getSignOutRemark());
        }
        tbPatrolStoreRecordVO.setOverdue(false);
        if (tbPatrolStoreRecordVO.getStatus() == 1 && tbPatrolStoreRecordVO.getSubEndTime() != null
                && tbPatrolStoreRecordVO.getSignEndTime() != null) {
            tbPatrolStoreRecordVO.setOverdue(tbPatrolStoreRecordVO.getSignEndTime().after(tbPatrolStoreRecordVO.getSubEndTime()));
        } else if (tbPatrolStoreRecordVO.getSubEndTime() != null) {
            tbPatrolStoreRecordVO.setOverdue(new Date().after(tbPatrolStoreRecordVO.getSubEndTime()));
        }
        tbPatrolStoreRecordVO.setActualPatrolStoreDuration(DateUtils.formatBetween(storeRecordDO.getSignStartTime(),storeRecordDO.getSignEndTime()));
        //封装处理人
        if (tbPatrolStoreRecordVO.getStatus() != 1 && tbPatrolStoreRecordVO.getTaskId() > 0) {
            List<String> userIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, storeRecordDO.getTaskId(),
                    storeRecordDO.getStoreId(), UnifyNodeEnum.FIRST_NODE.getCode(), storeRecordDO.getLoopCount());
            List<UnifyPersonDTO> list = getList(enterpriseId, userIdList);
            tbPatrolStoreRecordVO.setHanderUserList(list);
        }
        //封装审批人列表
        if (tbPatrolStoreRecordVO.getStatus() == 0 && tbPatrolStoreRecordVO.getTaskId() > 0) {
            //待处理时审批列表
            Map<String, List<String>> nodePersonMap = unifyTaskStoreService.selectTaskStorAllNodePerson(enterpriseId, storeRecordDO.getTaskId(), storeRecordDO.getStoreId(), storeRecordDO.getLoopCount());
            List<String> userIdList = nodePersonMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
            List<UnifyPersonDTO> list = getList(enterpriseId, userIdList);
            tbPatrolStoreRecordVO.setApproverList(list);
        }else if(tbPatrolStoreRecordVO.getStatus() == 2 && tbPatrolStoreRecordVO.getTaskId() > 0){
            //待审批时审批列表
            List<String> userIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, storeRecordDO.getTaskId(),
                    storeRecordDO.getStoreId(), UnifyNodeEnum.SECOND_NODE.getCode(), storeRecordDO.getLoopCount());
            List<UnifyPersonDTO> list = getList(enterpriseId, userIdList);
            tbPatrolStoreRecordVO.setApproverList(list);
        }
        //总项数
        Integer num = tbDataStaTableColumnMapper.dataStaColumnNumCount(enterpriseId, storeRecordDO.getId());
        tbPatrolStoreRecordVO.setTotalColumnNum(num);

        //封装抄送人
        Map<String, List<UnifyPersonDTO>> listMap = getTaskPerson(enterpriseId, Collections.singletonList(storeRecordDO.getTaskId()), storeRecordDO.getStoreId(), storeRecordDO.getLoopCount());
        tbPatrolStoreRecordVO.setCcUserList(listMap.get(UnifyNodeEnum.CC.getCode()));
        tbPatrolStoreRecordVO.setAduitUserList(listMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
        if (CollectionUtils.isEmpty(tbPatrolStoreRecordVO.getHanderUserList())) {
            tbPatrolStoreRecordVO.setHanderUserList(listMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
        }

        String username = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, storeRecordDO.getCreateUserId());
        tbPatrolStoreRecordVO.setCreateUserName(username);
        if (Constants.SYSTEM_USER_ID.equals(storeRecordDO.getCreateUserId())) {
            tbPatrolStoreRecordVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }

        TbPatrolStoreRecordInfoDO patrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, storeRecordDO.getId());
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(storeRecordDO.getPatrolType())) {
            StorePartnerSignatureVO storePartnerSignatureVO = new StorePartnerSignatureVO();
            if(patrolStoreRecordInfoDO != null && StringUtils.isNotBlank(patrolStoreRecordInfoDO.getSignatureUserId())){

                storePartnerSignatureVO.setSignatureUrl(patrolStoreRecordInfoDO.getSignatureUrl());
                storePartnerSignatureVO.setSignatureResult(patrolStoreRecordInfoDO.getSignatureResult());
                storePartnerSignatureVO.setSignatureRemark(patrolStoreRecordInfoDO.getSignatureRemark());
                storePartnerSignatureVO.setSignatureUserId(patrolStoreRecordInfoDO.getSignatureUserId());
                storePartnerSignatureVO.setSignatureTime(patrolStoreRecordInfoDO.getSignatureTime());
                tbPatrolStoreRecordVO.setFinishTime(patrolStoreRecordInfoDO.getFinishTime());
                EnterpriseUserDO signatureUser = enterpriseUserDao.selectByUserId(enterpriseId, patrolStoreRecordInfoDO.getSignatureUserId());
                if (signatureUser != null) {
                    storePartnerSignatureVO.setSignatureUserName(signatureUser.getName());
                    storePartnerSignatureVO.setAvatar(signatureUser.getAvatar());
                    storePartnerSignatureVO.setJobnumber(signatureUser.getJobnumber());
                    storePartnerSignatureVO.setMobile(signatureUser.getMobile());
                }
            }
            ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, storeRecordDO.getId());
            if(safetyCheckFlowDO != null){
                storePartnerSignatureVO.setSelectSignatureUser(safetyCheckFlowDO.getSignatureUser());
            }
            tbPatrolStoreRecordVO.setStorePartnerSignatureVO(storePartnerSignatureVO);
        }
        String params = Optional.ofNullable(patrolStoreRecordInfoDO).map(o->o.getParams()).orElse(null);
        tbPatrolStoreRecordVO.setParams(params);
        List<ApproveDTO> taskList = new ArrayList<>();
        if (patrolStoreRecordInfoDO != null && StringUtils.isNotBlank(patrolStoreRecordInfoDO.getAuditUserId())){
            ApproveDTO approveDTO = new ApproveDTO();
            approveDTO.setApproveTime(patrolStoreRecordInfoDO.getAuditTime());
            approveDTO.setUserId(patrolStoreRecordInfoDO.getAuditUserId());
            approveDTO.setUserName(patrolStoreRecordInfoDO.getAuditUserName());
            taskList.add(approveDTO);
        }
        if (taskList.size() != 0) {
            //任务审批完成的情况下，审批人确定
            tbPatrolStoreRecordVO.setApprover(taskList);
            List<UnifyPersonDTO> list = new ArrayList<>();
            UnifyPersonDTO unifyPersonDTO = new UnifyPersonDTO();
            unifyPersonDTO.setUserName(taskList.get(0).getUserName());
            unifyPersonDTO.setUserId(taskList.get(0).getUserId());
            list.add(unifyPersonDTO);
            tbPatrolStoreRecordVO.setApproverList(list);
        }
        //封装提交状态

        tbPatrolStoreRecordVO.setSubmitStatus(storeRecordDO.getSubmitStatus());
        tbPatrolStoreRecordVO.setIsExpired(false);
        if(key!=null){
            String isExpire = redisUtilPool.getString(key);
            if (StringUtils.isNotEmpty(isExpire)) {
                tbPatrolStoreRecordVO.setIsExpired(true);
            }
        }
        //兼容老数据
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, storeRecordDO.getMetaTableId());
        tbPatrolStoreRecordVO.setTable(metaTableDO);
        //返回数组，多个检查表
        List<TbDataTableDO> dataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, storeRecordDO.getId(), PATROL_STORE);
        List<Long> metaTableIdList = dataTableDOList.stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaTableDO> metaTableDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(metaTableIdList)) {
            metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        }
        tbPatrolStoreRecordVO.setMetaTableList(metaTableDOList);
        //循环周期
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, storeRecordDO.getTaskId());
        if (taskParentDO != null) {
            tbPatrolStoreRecordVO.setTaskCycle(taskParentDO.getTaskCycle() != null ? taskParentDO.getTaskCycle() : Constants.ONCE);
            //任务说明
            tbPatrolStoreRecordVO.setTaskDesc(taskParentDO.getTaskDesc());
            tbPatrolStoreRecordVO.setAiAudit(taskParentDO.getAiAudit());
        }
        //isExpire 为false 且token为null，将数据设置null，防止客户数据暴露
        if (!tbPatrolStoreRecordVO.getIsExpired() && accessToken == null) {
            TbPatrolStoreRecordVO patrolStoreRecordVO = new TbPatrolStoreRecordVO();
            patrolStoreRecordVO.setIsExpired(tbPatrolStoreRecordVO.getIsExpired());
            return patrolStoreRecordVO;
        }

        tbPatrolStoreRecordVO.setOverdueRun(settingDO.getOverdueTaskContinue());
        if(taskParentDO != null){
            //流程信息处理
            List<TaskProcessVO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessVO.class);
            // 节点配置信息组装
            for (TaskProcessVO taskProcessVO : process) {
                taskProcessVO.setTaskId(storeRecordDO.getTaskId());
            }
            Map<Long, TaskProcessVO> taskProcessVOMap = unifyTaskService.dealTaskProcess(enterpriseId, process);
            if(MapUtils.isNotEmpty(taskProcessVOMap)){
                TaskProcessVO taskProcessVO = taskProcessVOMap.get(tbPatrolStoreRecordVO.getTaskId());
                tbPatrolStoreRecordVO.setAssignPeopleRang(taskProcessVO);
            }
        }
        HandlerUserVO handlerUserVO = new HandlerUserVO();
        if(StringUtils.isNotBlank(tbPatrolStoreRecordVO.getSupervisorId())){
            String actualHandleUserId = tbPatrolStoreRecordVO.getSupervisorId();
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, actualHandleUserId);
            if(Objects.nonNull(enterpriseUserDO)){
                handlerUserVO.setUserId(enterpriseUserDO.getUserId());
                handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
                handlerUserVO.setUserName(enterpriseUserDO.getName());
                handlerUserVO.setUserMobile(enterpriseUserDO.getMobile());
                handlerUserVO.setJobnumber(enterpriseUserDO.getJobnumber());
                List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
                if (CollectionUtils.isNotEmpty(sysRoleList)){
                    handlerUserVO.setUserRoles(sysRoleList);
                }
            }
        }
        tbPatrolStoreRecordVO.setHandlerUserVO(handlerUserVO);
        return tbPatrolStoreRecordVO;
    }


    public List<UnifyPersonDTO> getList(String enterpriseId,List<String> userIdList){
        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
            Map<String, UnifyPersonDTO> peopleMap = new HashMap<>();
            for (EnterpriseUserDO enterpriseUserDO : userList) {
                UnifyPersonDTO unifyPersonDTO = new UnifyPersonDTO();
                unifyPersonDTO.setAvatar(enterpriseUserDO.getAvatar());
                unifyPersonDTO.setUserName(enterpriseUserDO.getName());
                unifyPersonDTO.setUserId(enterpriseUserDO.getUserId());
                peopleMap.put(enterpriseUserDO.getUserId(), unifyPersonDTO);
            }
            List<UnifyPersonDTO> list = new ArrayList<>();
            for (String userId : userIdList) {
                if (peopleMap.containsKey(userId)) {
                    list.add(peopleMap.get(userId));
                }
            }
            return  list;
        }
        return  null;
    }


    @Override
    public List<DataTableInfoDTO> dataTableInfoList(String enterpriseId, Long businessId, String userId) {

        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (tbPatrolStoreRecordDO==null){
            throw new ServiceException(ErrorCodeEnum.PATROL_STORE_RECORD_IS_NOT_NULL);
        }
        String businessType = PATROL_STORE;
        if(BusinessCheckType.PATROL_RECHECK.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())){
            businessType = tbPatrolStoreRecordDO.getBusinessCheckType();
        }
        List<TbDataTableDO> tbDataTableDOS =
                tbDataTableMapper.selectByBusinessId(enterpriseId, businessId, businessType);
        if (CollectionUtils.isEmpty(tbDataTableDOS)) {
            return new ArrayList<>();
        }
        boolean isAdmin = false;
        if(StringUtils.isNotBlank(userId)){
            isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        }

        Long taskId = tbPatrolStoreRecordDO.getTaskId();
        Map<String, Boolean> mappingMap = new HashMap<>();
        Map<String, Boolean> aiAuditMap = new HashMap<>();
        if(taskId != null && taskId > 0){
            //模板权限
            List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);
            mappingMap = formDataList.stream().collect(Collectors.toMap(UnifyFormDataDTO::getOriginMappingId, UnifyFormDataDTO::getCheckTable));
            aiAuditMap = formDataList.stream()
                    .filter(dto -> dto.getOriginMappingId() != null && dto.getAiAudit() != null)
                    .collect(Collectors.toMap(UnifyFormDataDTO::getOriginMappingId, UnifyFormDataDTO::getAiAudit));
        }


        // Map:dataTableId->dataStaColumnList
        // Map:metaTableId->metaStaColumnList
        Map<Long, List<TbDataStaTableColumnVO>> dataTableIdStaColumnsMap = new HashMap<>();
        Map<Long, List<MetaStaColumnVO>> metaTableIdStaColumnsMap = new HashMap<>();
        List<TbDataStaTableColumnDO> dataStaColumnList =
                tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, businessType);
        //统计分类的合格率/得分率信息
        List<CategoryStatisticsVO> categoryStatisticsVOList =
                tbDataStaTableColumnMapper.selectCategoryStatisticsListByBusinessId(enterpriseId, businessId, businessType);
        Map<Long, List<CategoryStatisticsVO>> categoryStatisticsVOMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(categoryStatisticsVOList)){
            categoryStatisticsVOMap =
                    categoryStatisticsVOList.stream().collect(Collectors.groupingBy(CategoryStatisticsVO::getDataTableId));
        }
        Map<Long, TbDataStaTableColumnDO> lastTimeCheckResultMap = new HashMap<>();
        Set<String> hasCategoryNameList = new HashSet<>();

        // 是否有发稽核工单的权限
        Boolean sendProblemAuth = false;
        ScSafetyCheckFlowDO safetyCheckFlowDO = null;
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, businessId);
            sendProblemAuth = scSafetyCheckFlowService.checkSendProblemAuth(enterpriseId, businessId, userId);
        }

        if (CollectionUtils.isNotEmpty(dataStaColumnList)) {
            TbDataTableDO tbDataTableDO = tbDataTableDOS.get(Constants.INDEX_ZERO);
            TbDataTableDO tb = tbDataTableMapper.getLastTimeDataTableDO(enterpriseId, tbDataTableDO.getStoreId(), tbDataTableDO.getMetaTableId());
            //查询该门店该表已完成巡店中 最近一次各项的检查结果
            if(tb!=null){
                List<TbDataStaTableColumnDO> lastTimeCheckResulList = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId,tb.getBusinessId(),PATROL_STORE);
                lastTimeCheckResultMap = lastTimeCheckResulList.stream().collect(Collectors.toMap(TbDataStaTableColumnDO::getMetaColumnId, Function.identity()));
            }
            Map<Long, TbDataColumnCommentAppealVO> commentAppealMap = Maps.newHashMap();
            Map<Long, DataColumnHasHistoryVO> dataColumnHistoryMap = Maps.newHashMap();
            Map<String, String> handlerUserMap = Maps.newHashMap();
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                List<Long> dataColumnIdList = dataStaColumnList.stream()
                        .map(TbDataStaTableColumnDO::getId).collect(Collectors.toList());
                commentAppealMap = scSafetyCheckFlowService.getLatestCommentAppealInfo(enterpriseId, tbPatrolStoreRecordDO.getId(), dataColumnIdList);
                dataColumnHistoryMap = scSafetyCheckFlowService.checkDataColumnHasHistory(enterpriseId, tbPatrolStoreRecordDO.getId(), dataColumnIdList);
                List<String> handlerUserIdList = dataStaColumnList.stream()
                        .map(TbDataStaTableColumnDO::getHandlerUserId).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(handlerUserIdList)){
                    List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, handlerUserIdList);
                    handlerUserMap = ListUtils.emptyIfNull(userDOList).stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));
                }
            }
            Map<Long, TbDataStaTableColumnDO> finalLastTimeCheckResultMap = lastTimeCheckResultMap;
            Map<Long, TbDataColumnCommentAppealVO> finalCommentAppealMap = commentAppealMap;
            Map<String, String> finalHandlerUserMap = handlerUserMap;
            Map<Long, DataColumnHasHistoryVO> finalDataColumnHistoryMap = dataColumnHistoryMap;

            Set<Long> metaStaColumnIds = CollStreamUtil.toSet(dataStaColumnList, TbDataStaTableColumnDO::getMetaColumnId);
            List<TbMetaStaTableColumnDO> metaStaColumnList =
                    tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaStaColumnIds));
            Map<Long, TbMetaStaTableColumnDO> metaColumnMap = CollStreamUtil.toMap(metaStaColumnList, TbMetaStaTableColumnDO::getId, v -> v);
            // 设置名字数据
            tbMetaTableService.setNameData(enterpriseId, metaStaColumnList);
            hasCategoryNameList = CollStreamUtil.toSet(metaStaColumnList, TbMetaStaTableColumnDO::getCategoryName);

            // 结果项
            List<TbMetaColumnResultDO> columnResultDOList =
                    tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, new ArrayList<>(metaStaColumnIds));
            List<TbMetaColumnResultDTO> columnResultDTOList = metaTableService.getMetaColumnResultList(enterpriseId, columnResultDOList);
            Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap = CollStreamUtil.groupByKey(columnResultDTOList, TbMetaColumnResultDTO::getMetaColumnId);

            //不合格原因
            List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnIdList(enterpriseId, new ArrayList<>(metaStaColumnIds));
            Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(columnReasonDTOList)){
                columnIdReasonMap = CollStreamUtil.groupByKey(columnReasonDTOList, TbMetaColumnReasonDTO::getMetaColumnId);
            }

            //不合格原因
            List<TbMetaColumnAppealDTO> columnAppealDTOList = metaColumnAppealDao.getListByColumnIdList(enterpriseId, new ArrayList<>(metaStaColumnIds));
            Map<Long, List<TbMetaColumnAppealDTO>> columnAppealMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(columnAppealDTOList)){
                columnAppealMap = CollStreamUtil.groupByKey(columnAppealDTOList, TbMetaColumnAppealDTO::getMetaColumnId);
            }

            Boolean finalSendProblemAuth = sendProblemAuth;
            List<TbDataStaTableColumnVO> tbDataStaTableColumnVOS = dataStaColumnList.stream().map(a -> {
                TbDataStaTableColumnVO tbDataStaTableColumnVO = new TbDataStaTableColumnVO();
                BeanUtils.copyProperties(a, tbDataStaTableColumnVO);
                TbMetaStaTableColumnDO metaColumnDO = metaColumnMap.get(a.getMetaColumnId());
                tbDataStaTableColumnVO.setIsAiCheck(Objects.nonNull(metaColumnDO) ? metaColumnDO.getIsAiCheck() : 0);
                //异步创建任务延迟，从缓存中取值
                setTaskQuestionId(enterpriseId, tbDataStaTableColumnVO);
                //每个项上次的检查结果，没有结果显示null
                TbDataStaTableColumnDO lastDataStaColumn = finalLastTimeCheckResultMap.get(tbDataStaTableColumnVO.getMetaColumnId());
                if(lastDataStaColumn != null){
                    tbDataStaTableColumnVO.setLastTimeCheckResult(lastDataStaColumn.getCheckResult());
                    tbDataStaTableColumnVO.setLastDataColumnId(lastDataStaColumn.getId());
                }
                Boolean canSendProblem = CheckResultEnum.FAIL.getCode().equals(tbDataStaTableColumnVO.getCheckResult()) &&
                        (tbDataStaTableColumnVO.getTaskQuestionId() == null || tbDataStaTableColumnVO.getTaskQuestionId() == 0);
                if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                    TbDataColumnCommentAppealVO commentAppealVO= finalCommentAppealMap.get(a.getId());
                    DataColumnHasHistoryVO dataColumnHasHistoryVO = finalDataColumnHistoryMap.get(a.getId());
                    if(dataColumnHasHistoryVO != null){
                        tbDataStaTableColumnVO.setHasCheckHistory(dataColumnHasHistoryVO.getHasCheckHistory());
                        if(commentAppealVO.getTbDataColumnCommentVO() != null ){
                            commentAppealVO.getTbDataColumnCommentVO().setHasCommentHistory(dataColumnHasHistoryVO.getHasCommentHistory());
                        }
                        if(commentAppealVO.getTbDataColumnAppealVO() != null ){
                            commentAppealVO.getTbDataColumnAppealVO().setHasAppealHistory(dataColumnHasHistoryVO.getHasAppealHistory());
                        }
                    }
                    tbDataStaTableColumnVO.setCommentAppealVO(commentAppealVO);
                    if(StringUtils.isNotBlank(finalHandlerUserMap.get(a.getHandlerUserId()))){
                        tbDataStaTableColumnVO.setHandlerUserName(finalHandlerUserMap.get(a.getHandlerUserId()));
                    }
                    canSendProblem = canSendProblem && finalSendProblemAuth;
                }
                tbDataStaTableColumnVO.setCanSendProblem(canSendProblem);
                return tbDataStaTableColumnVO;
            }).collect(Collectors.toList());



            dataTableIdStaColumnsMap.putAll(
                    tbDataStaTableColumnVOS.stream().collect(Collectors.groupingBy(TbDataStaTableColumnVO::getDataTableId)));

            Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
            Map<Long, List<TbMetaColumnAppealDTO>> finalColumnAppealMap = columnAppealMap;
            List<MetaStaColumnVO> metaStaColumnVOList = metaStaColumnList.stream().map(a -> {
                MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
                BeanUtils.copyProperties(a, metaStaColumnVO);
                //如果是采集项
                if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())){
                    metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                    metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
                }
                metaStaColumnVO
                        .setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
                // 填充结果项
                metaStaColumnVO.fillColumnResultList();
                metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
                metaStaColumnVO.setColumnAppealList(finalColumnAppealMap.get(a.getId()));

                JSONObject extendInfo = JSONObject.parseObject(a.getExtendInfo());
                if (Objects.nonNull(extendInfo)) {
                    metaStaColumnVO.setDescRequired(extendInfo.getBoolean(Constants.TableColumn.DESC_REQUIRED));
                    metaStaColumnVO.setAutoQuestionTaskValidity(extendInfo.getInteger(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY));
                    metaStaColumnVO.setIsSetAutoQuestionTaskValidity(extendInfo.getBoolean(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY));
                    metaStaColumnVO.setMinCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MIN_CHECK_PIC_NUM));
                    metaStaColumnVO.setMaxCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MAX_CHECK_PIC_NUM));
                }
                return metaStaColumnVO;
            }).collect(Collectors.toList());
            metaTableIdStaColumnsMap
                    .putAll(metaStaColumnVOList.stream().collect(Collectors.groupingBy(MetaStaColumnVO::getMetaTableId)));
        }
        // Map:dataTableId->dataDefColumnList
        // Map:metaTableId->metaDefColumnList
        Map<Long, List<TbDataDefTableColumnDO>> dataTableIdDefColumnsMap = new HashMap<>();
        Map<Long, List<TbMetaDefTableColumnDO>> metaTableIdDefColumnsMap = new HashMap<>();
        List<TbDataDefTableColumnDO> dataDefColumnList =
                tbDataDefTableColumnMapper.selectByBusinessId(enterpriseId, businessId, businessType);
        if (CollectionUtils.isNotEmpty(dataDefColumnList)) {
            dataTableIdDefColumnsMap.putAll(
                    dataDefColumnList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getDataTableId)));
            Set<Long> metaDefColumnIds =
                    dataDefColumnList.stream().map(TbDataDefTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(metaDefColumnIds)) {
                List<TbMetaDefTableColumnDO> metaDefColumnList =
                        tbMetaDefTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaDefColumnIds));
                metaTableIdDefColumnsMap.putAll(
                        metaDefColumnList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId)));
            }
        }
        // mateTableIds
        Set<Long> metaTableIds = tbDataTableDOS.stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toSet());
        // Map:metaTableId->metaTableDO
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
        List<TbMetaTableUserAuthDO> tableAuth = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, new ArrayList<>(metaTableIds));
        Map<String, TbMetaTableUserAuthDO> tableAuthMap = ListUtils.emptyIfNull(tableAuth).stream().collect(Collectors.toMap(k -> k.getBusinessId() + Constants.MOSAICS + k.getUserId(), v -> v, (k, v) -> k));
        Set<String> finalHasCategoryNameList = hasCategoryNameList;
        boolean finalIsAdmin = isAdmin;
        Map<Long, TbMetaTableInfoVO> idMetaTableMap =
                tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, data ->
                        TbMetaTableInfoVO.builder().id(data.getId()).createTime(data.getCreateTime()).editTime(data.getEditTime())
                                .tableName(data.getTableName()).description(data.getDescription()).createUserId(data.getCreateUserId())
                                .createUserName(data.getCreateUserName()).supportScore(data.getSupportScore()).locked(data.getLocked())
                                .active(data.getActive()).tableType(data.getTableType()).shareGroup(data.getShareGroup()).deleted(data.getDeleted())
                                .editUserId(data.getEditUserId()).editUserName(data.getEditUserName()).shareGroupName(data.getShareGroupName())
                                .resultShareGroup(data.getResultShareGroup()).resultShareGroup(data.getResultShareGroup()).resultShareGroupName(data.getResultShareGroupName())
                                .levelRule(data.getLevelRule()).levelInfo(data.getLevelInfo()).storeSceneId(data.getStoreSceneId()).defaultResultColumn(data.getDefaultResultColumn())
                                .noApplicableRule(data.getNoApplicableRule()).viewResultAuth(getTableViewResultAuth(data.getId(), userId, finalIsAdmin, tableAuthMap))
                                .categoryNameList(CollectionUtils.isEmpty(JSONObject.parseArray(data.getCategoryNameList(), String.class)) ||
                                        CollectionUtils.isEmpty(finalHasCategoryNameList) ? null :
                                        ListUtils.retainAll(JSONObject.parseArray(data.getCategoryNameList(), String.class), new ArrayList<>(finalHasCategoryNameList)))
                                .orderNum(data.getOrderNum()).status(data.getStatus()).totalScore(data.getTotalScore()).tableProperty(data.getTableProperty())
                                .useRange(data.getUseRange())
                                .resultViewRange(data.getResultViewRange())
                                .build(), (a, b) -> a));

        List<PatrolStoreStatisticsDataStaTableCountDTO>  dataStaTableCountList = tbDataStaTableColumnMapper.statisticsColumnCountByBusinessIdGroupByDataTableId(enterpriseId, Collections.singletonList(businessId));

        Map<Long, PatrolStoreStatisticsDataStaTableCountDTO> dataTableByEveryColumnCountMap = ListUtils.emptyIfNull(dataStaTableCountList).stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableCountDTO::getDataTableId, Function.identity(), (a, b) -> a));

        Map<Long, List<CategoryStatisticsVO>> finalCategoryStatisticsVOMap = categoryStatisticsVOMap;
        Boolean finalSendProblemAuth1 = sendProblemAuth;
        ScSafetyCheckFlowDO finalSafetyCheckFlowDO = safetyCheckFlowDO;
        Map<String, Boolean> finalMappingMap = mappingMap;
        Map<String, Boolean> finalAiAuditMap = aiAuditMap;
        // 获取检查表停留时间
        Map<Long, String> dataTableDwellTimeMap = patrolStoreRecordsService.getDataTableDwellTimeMap(enterpriseId, Lists.newArrayList(businessId));
        return tbDataTableDOS.stream().map(a -> {
            Long dataTableId = a.getId();
            Long metaTableId = a.getMetaTableId();
            TbDataTableInfoVO dataTableInfoVO = new TbDataTableInfoVO();
            BeanUtils.copyProperties(a, dataTableInfoVO);
            DataTableInfoDTO dataTableInfoDTO =
                    DataTableInfoDTO.builder().dataTable(dataTableInfoVO).dataStaColumns(dataTableIdStaColumnsMap.get(dataTableId))
                            .dataDefColumns(dataTableIdDefColumnsMap.get(dataTableId))
                            .metaTable(idMetaTableMap.get(metaTableId)).build();
            // 检查表停留时间
            dataTableInfoDTO.setDwellTime(dataTableDwellTimeMap.get(dataTableId));
            Boolean canBatchSendProblem = ListUtils.emptyIfNull(dataTableIdStaColumnsMap.get(dataTableId)).stream().anyMatch(dataColumn -> dataColumn.getCanSendProblem());
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                canBatchSendProblem = canBatchSendProblem && finalSendProblemAuth1;
            }
            dataTableInfoVO.setCanBatchSendProblem(canBatchSendProblem);
            //是否表单提交
            Integer status = a.getSubmitStatus() & Constants.INDEX_ONE;
            if (Constants.INDEX_ONE.equals(status)
                    || (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType()) && finalSafetyCheckFlowDO != null && finalSafetyCheckFlowDO.getCycleCount() > 0)){
                Integer failNum = getNum(a.getFailNum());
                Integer passNum = getNum(a.getPassNum());
                Integer inapplicableNum = getNum(a.getInapplicableNum());
                Integer collectColumnNum = getNum(a.getCollectColumnNum());
                dataTableInfoDTO.setTotalColumn(failNum+passNum+inapplicableNum+collectColumnNum);
                dataTableInfoDTO.setFailNum(failNum);
                dataTableInfoDTO.setPassNum(passNum);
                dataTableInfoDTO.setInapplicableNum(inapplicableNum);
                dataTableInfoDTO.setCollectColumnNum(collectColumnNum);
                dataTableInfoDTO.setTaskCalTotalScore(a.getTaskCalTotalScore());
                dataTableInfoDTO.setTotalCalColumnNum(a.getTotalCalColumnNum());
                dataTableInfoDTO.setScore(a.getCheckScore());
                dataTableInfoDTO.setCheckTable(finalMappingMap.get(String.valueOf(metaTableId)));
                //总项数
                dataTableInfoVO.setTotalColumnNum(dataTableInfoDTO.getTotalColumn());
                boolean isDefine = TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType());
                BigDecimal allColumnCheckScorePercent = new BigDecimal(Constants.ZERO_STR);
                if (!isDefine) {
                    PatrolStoreStatisticsDataStaTableCountDTO pts = dataTableByEveryColumnCountMap.getOrDefault(a.getId(),new PatrolStoreStatisticsDataStaTableCountDTO());
                    BigDecimal checkScore = pts.getCheckScore();
                    if (MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.getCode().equals(a.getTableProperty())){
                        checkScore = a.getTotalScore().subtract(checkScore);
                    }
                    dataTableInfoDTO.setAllColumnCheckScore(checkScore);
                    if (new BigDecimal(Constants.ZERO_STR).compareTo(checkScore) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(dataTableInfoDTO.getTaskCalTotalScore()) != 0) {
                        allColumnCheckScorePercent = (checkScore.divide(dataTableInfoDTO.getTaskCalTotalScore(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
                    }
                    dataTableInfoDTO.setAllColumnCheckScorePercent(allColumnCheckScorePercent);
                }
            }
            //分类统计信息
            dataTableInfoDTO.setCategoryStatisticsList(finalCategoryStatisticsVOMap.get(dataTableId));
            // 检查表类型
            Boolean isDefine = TableTypeUtil.isUserDefinedTable(a.getTableProperty(), a.getTableType());
            if (!isDefine) {
                //标准表
                List<MetaStaColumnVO> metaStaColumnVOList = metaTableIdStaColumnsMap.get(metaTableId);
                if (metaStaColumnVOList == null) {
                    log.error("##标准检查项column 不存在，enterpriseId={},businessId={},metaTableId={}", enterpriseId, businessId, metaTableId);
                }
                dataTableInfoDTO.setMetaStaColumns(metaStaColumnVOList);
            } else {
                //自定义表
                List<TbMetaDefTableColumnDO> metaDefColumns = metaTableIdDefColumnsMap.get(metaTableId);
                if (metaDefColumns == null) {
                    log.error("##自定义检查项column 不存在，enterpriseId={},businessId={},metaTableId={}", enterpriseId, businessId, metaTableId);
                } else {
                    //检查项排序
                    metaDefColumns = metaDefColumns.stream().sorted(Comparator.comparing(TbMetaDefTableColumnDO::getOrderNum)).collect(Collectors.toList());
                }
                dataTableInfoDTO.setMetaDefColumns(metaDefColumns);
            }
            dataTableInfoDTO.setAiAudit(finalAiAuditMap.get(String.valueOf(metaTableId)));
            return dataTableInfoDTO;
        }).collect(Collectors.toList());
    }

    private void setTaskQuestionId(String enterpriseId , TbDataStaTableColumnVO dataStaTableColumnVO){
        try{
            if(dataStaTableColumnVO.getTaskQuestionId() > 0){
                return;
            }
            String taskQuestionId = redisUtilPool.getString(redisConstantUtil.getQuestionTaskLockKey(enterpriseId, String.valueOf(dataStaTableColumnVO.getId())));
            if(StringUtils.isNotBlank(taskQuestionId)){
                dataStaTableColumnVO.setTaskQuestionId(Long.valueOf(taskQuestionId));
            }
        } catch (Exception exception) {
            log.error("##setTaskQuestionId报错enterpriseId={},dataColumnId={}", enterpriseId, dataStaTableColumnVO.getId());
        }

    }

    @Override
    public List<StoreTaskMapVO> listStoreTaskMap(String enterpriseId, StoreTaskMapParam param) {
        List<String> userIds = param.getUserIds();
        Date startTime = param.getStartTime();
        Date endTime = param.getEndTime();
        if (OTHER.equals(param.getType())) {
            // 查询该用户巡过的店
            List<String> storeIds =
                    tbPatrolStoreRecordMapper.selectStoreIdsBySupervisorIds(enterpriseId, userIds, startTime, endTime);
            if (CollectionUtils.isNotEmpty(storeIds)) {
                // 查询这些店巡过的人
                userIds =
                        tbPatrolStoreRecordMapper.selectSupervisorIdsByStoreIds(enterpriseId, storeIds, startTime, endTime);
            }
        }
        // 查询已选人头像、名字
        List<EnterpriseUserDO> user = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        Map<String, EnterpriseUserDO> userIdUserMap =
                user.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity(), (a, b) -> a));
        // 巡店记录
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordDOS =
                tbPatrolStoreRecordMapper.selectBySupervisorIds(enterpriseId, userIds, startTime, endTime);
        Map<String, List<TbPatrolStoreRecordDO>> supervisorIdRecordListMap =
                tbPatrolStoreRecordDOS.stream().collect(Collectors.groupingBy(TbPatrolStoreRecordDO::getSupervisorId));
        return userIds.stream().map(userId -> {
            StoreTaskMapVO taskMapVO = new StoreTaskMapVO();
            taskMapVO.setUserId(userId);
            EnterpriseUserDO userDO = userIdUserMap.get(userId);
            if (userDO != null) {
                taskMapVO.setUserName(userDO.getName());
                taskMapVO.setAvatar(userDO.getAvatar());
            }
            List<TbPatrolStoreRecordDO> recordDOList =
                    supervisorIdRecordListMap.getOrDefault(userId, new ArrayList<>());
            List<StoreTaskMapVO.StoreTaskHistoryDTO> historyDTOList = recordDOList.stream()
                    .map(record -> StoreTaskMapVO.StoreTaskHistoryDTO.builder()
                            .longitudeLatitude(record.getStoreLongitudeLatitude()).endTime(record.getSignEndTime())
                            .storeId(record.getStoreId()).storeName(record.getStoreName()).score(null).templateName(null)
                            .corpId(null).recordId(record.getId()).templateId(null).taskDetailId(record.getTaskId()).build())
                    .sorted(((o1, o2) -> o2.getEndTime().compareTo(o1.getEndTime()))).collect(Collectors.toList());
            taskMapVO.setHistory(historyDTOList);
            return taskMapVO;
        }).collect(Collectors.toList());
    }

    @Override
    public TbRecordVO getPatrolMetaTable(String enterpriseId, String storeId, CurrentUser user, Long subTaskId,
                                         String patrolType, EnterpriseStoreCheckSettingDO settingDO, Long businessId) {
        // 判断是否是任务巡店
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO;
        TbRecordVO tbRecordVO = new TbRecordVO();
        if (subTaskId != null && subTaskId > 0) {
            // 通过record表查出metaTableIds
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
            if (taskSubDO == null) {
                throw new ServiceException("该任务不存在或者该任务已被删除");
            }
            TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskSubDO.getUnifyTaskId());
            if(StringUtils.isBlank(patrolType)){
                patrolType = taskParentDO.getTaskType();
            }
            TaskPersonTaskInfoDTO taskPersonTaskInfoDTO = null;
            if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
                taskPersonTaskInfoDTO = JSONObject.parseObject(taskParentDO.getTaskInfo(), TaskPersonTaskInfoDTO.class);
                tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByStaffPlan(enterpriseId, subTaskId, storeId, taskSubDO.getLoopCount(), taskPersonTaskInfoDTO.getExecuteWay().getWay(), PatrolStoreRecordStatusEnum.UPCOMING_HANDLE.getStatus());
            }else {
                if (taskSubDO.getLoopCount() > 0) {
                    tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(),
                            taskSubDO.getLoopCount(), patrolType, null);
                    if (tbPatrolStoreRecordDO == null) {
                        tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordBySubTaskId(enterpriseId, subTaskId, patrolType);
                    }
                } else {
                    tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordBySubTaskId(enterpriseId, subTaskId, patrolType);
                }
            }

            if (tbPatrolStoreRecordDO == null) {
                log.info("该子任务找不到巡店记录,子任务id:{},补偿创建方法", subTaskId);
                // 补偿创建巡店记录方法
                patrolStoreRecordsService.makeUpPatrolMetaRecord(enterpriseId, subTaskId, patrolType, settingDO, storeId);
                if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
                    tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByStaffPlan(enterpriseId, subTaskId, storeId, taskSubDO.getLoopCount(), taskPersonTaskInfoDTO.getExecuteWay().getWay(), PatrolStoreRecordStatusEnum.UPCOMING_HANDLE.getStatus());
                }else {
                    tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(),
                            taskSubDO.getLoopCount(), patrolType, null);
                }
            }
        } else if (businessId != null) {
            tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        } else {
            // 通过时间，门店，userId查出当天的metaTableIds
            tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByStoreIdAndTime(enterpriseId, user.getUserId(),
                    storeId, new Date(System.currentTimeMillis()), patrolType);
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(patrolType)) {
                tbPatrolStoreRecordDO = null;
            }
        }
        //是否存在审批环节,默认是不存在审批人
        Boolean isExistApprove = Boolean.FALSE;
        if (tbPatrolStoreRecordDO != null && tbPatrolStoreRecordDO.getTaskId() != 0) {
            TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
            tbRecordVO.setTaskName(taskParentDO.getTaskName());
            tbRecordVO.setTaskDesc(taskParentDO.getTaskDesc());
            tbRecordVO.setAiAudit(taskParentDO.getAiAudit());
            //流程信息处理
            List<TaskProcessDTO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
            // 节点配置信息组装
            Map<String, String> nodeMap = ListUtils.emptyIfNull(process).stream()
                    .filter(a -> a.getNodeNo() != null && a.getApproveType() != null)
                    .collect(Collectors.toMap(TaskProcessDTO::getNodeNo, TaskProcessDTO::getApproveType, (a, b) -> a));

            String approveUser = nodeMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
            // 判断是否有审核节点，没有审核通过流程直接结束
            if (StringUtils.isNotBlank(approveUser)) {
                isExistApprove = Boolean.TRUE;
            }
        }
        //审批人是否有驳回操作
        Boolean isReject = Boolean.FALSE;
        if (tbPatrolStoreRecordDO != null && tbPatrolStoreRecordDO.getTaskId() != 0) {
            List<TaskSubVO> taskSubList = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount());
            String currentStoreId = tbPatrolStoreRecordDO.getStoreId();
            //当前父任务下门店所有的子任务----筛选节点为2，审批状态为reject 说明含有审批过程中有驳回操作
            List<TaskSubVO> rejectList = taskSubList.stream().
                    filter(taskSub -> taskSub.getStoreId().equals(currentStoreId) && taskSub.getFlowNodeNo().equals("2") && (taskSub.getActionKey() != null && taskSub.getActionKey().equals("reject"))).collect(Collectors.toList());
            if (rejectList.size() != 0) {
                isReject = Boolean.TRUE;
            }
        }
        if (tbPatrolStoreRecordDO != null) {
            Long recordId = tbPatrolStoreRecordDO.getId();
            Long taskId = tbPatrolStoreRecordDO.getTaskId();
            // 获取巡店记录相关的表
            List<TbDataTableDO> tbDataTableDOS = tbDataTableMapper.getSubmitTableByRecordId(enterpriseId, recordId, taskId);
            Map<String, Boolean> mappingMap = new HashMap<>();
            if(taskId != null && taskId > 0){
                //模板权限
                List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, taskId);
                mappingMap = formDataList.stream().collect(Collectors.toMap(UnifyFormDataDTO::getOriginMappingId, UnifyFormDataDTO::getCheckTable));
            }
            TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, tbPatrolStoreRecordDO.getId());
            if(tbPatrolStoreRecordInfoDO!=null){
                tbRecordVO.setSignInRemark(tbPatrolStoreRecordInfoDO.getSignInRemark());
                tbRecordVO.setSignOutRemark(tbPatrolStoreRecordInfoDO.getSignOutRemark());
                tbRecordVO.setParams(tbPatrolStoreRecordInfoDO.getParams());
            }
            Long metaTableId = 0L;
            if(CollectionUtils.isNotEmpty(tbDataTableDOS)){
                metaTableId = tbDataTableDOS.get(0).getMetaTableId();
                Map<String, Boolean> finalMappingMap = mappingMap;
                tbDataTableDOS.forEach(item->item.setCheckTable(finalMappingMap.get(String.valueOf(item.getMetaTableId()))));
            }
            tbRecordVO.setId(recordId);
            if(StringUtils.isBlank(tbRecordVO.getTaskName())){
                tbRecordVO.setTaskName(tbPatrolStoreRecordDO.getTaskName());
            }
            tbRecordVO.setSignInStatus(tbPatrolStoreRecordDO.getSignInStatus());
            tbRecordVO.setSignStartAddress(tbPatrolStoreRecordDO.getSignStartAddress());
            tbRecordVO.setSignStartTime(tbPatrolStoreRecordDO.getSignStartTime());
            String oldMetaTableIdsStr = tbPatrolStoreRecordDO.getMetaTableIds();
            if(oldMetaTableIdsStr.startsWith(Constants.COMMA) && oldMetaTableIdsStr.endsWith(Constants.COMMA)){
                oldMetaTableIdsStr = oldMetaTableIdsStr.substring(1,oldMetaTableIdsStr.length()-1);
            }
            tbRecordVO.setMetaTableIds(oldMetaTableIdsStr);
            tbRecordVO.setMetaTableId(tbPatrolStoreRecordDO.getMetaTableId());
            tbRecordVO.setTableList(tbDataTableDOS);
            tbRecordVO.setSubmitStatus(tbPatrolStoreRecordDO.getSubmitStatus());
            tbRecordVO.setSignOutStatus(tbPatrolStoreRecordDO.getSignOutStatus());
            tbRecordVO.setSignEndAddress(tbPatrolStoreRecordDO.getSignEndAddress());
            tbRecordVO.setStatus(tbPatrolStoreRecordDO.getStatus());
            tbRecordVO.setSignEndTime(tbPatrolStoreRecordDO.getSignEndTime());
            tbRecordVO.setOpenSummary(tbPatrolStoreRecordDO.getOpenSummary() ? 1 : 0);
            tbRecordVO.setOpenSignature(tbPatrolStoreRecordDO.getOpenSignature() ? 1 : 0);
            tbRecordVO.setOpenSubmitFirst(tbPatrolStoreRecordDO.getOpenSubmitFirst());
            tbRecordVO.setIsExistApprove(isExistApprove);
            tbRecordVO.setIsReject(isReject);
            tbRecordVO.setPatrolType(tbPatrolStoreRecordDO.getPatrolType());
            tbRecordVO.setScore(tbPatrolStoreRecordDO.getScore());
            tbRecordVO.setTaskCalTotalScore(tbPatrolStoreRecordDO.getTaskCalTotalScore());
            tbRecordVO.setCollectColumnNum(tbPatrolStoreRecordDO.getCollectColumnNum());
            tbRecordVO.setTotalCalColumnNum(tbPatrolStoreRecordDO.getTotalCalColumnNum());
            tbRecordVO.setCheckResultLevel(tbPatrolStoreRecordDO.getCheckResultLevel());
            tbRecordVO.setPassNum(tbPatrolStoreRecordDO.getPassNum());
            tbRecordVO.setFailNum(tbPatrolStoreRecordDO.getFailNum());
            tbRecordVO.setInapplicableNum(tbPatrolStoreRecordDO.getInapplicableNum());
            tbRecordVO.setTotalResultAward(tbPatrolStoreRecordDO.getTotalResultAward());
            tbRecordVO.setRecheckBusinessId(tbPatrolStoreRecordDO.getRecheckBusinessId());
            tbRecordVO.setBusinessCheckType(tbPatrolStoreRecordDO.getBusinessCheckType());
            tbRecordVO.setRecheckUserId(tbPatrolStoreRecordDO.getRecheckUserId());
            tbRecordVO.setRecheckUserName(tbPatrolStoreRecordDO.getRecheckUserName());
            tbRecordVO.setRecheckTime(tbPatrolStoreRecordDO.getRecheckTime());
            Integer columnNum = 0;
            TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbRecordVO.getMetaTableId());
            if(metaTableDO != null){
                //总项数
                if(!MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(metaTableDO.getTableProperty())){
                    columnNum = tbDataStaTableColumnMapper.dataStaColumnNumCount(enterpriseId, recordId);
                }else {
                    // 检查项总数
                    columnNum = tbMetaDefTableColumnMapper.selectColumnCountByTableId(enterpriseId, metaTableId);
                }
            }
            if(TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())){
                String captureStatus = redisUtilPool.getString(redisConstantUtil.getCapturePicture(enterpriseId +"_" + recordId));
                if(StringUtils.isNotBlank(captureStatus)){
                    tbRecordVO.setCaptureStatus(Integer.parseInt(captureStatus));
                }else {
                    Long pictureId = patrolStorePictureMapper.selectIdOne(enterpriseId, recordId);
                    tbRecordVO.setCaptureStatus(pictureId == null ? 0 : 2);
                }
            }
            tbRecordVO.setColumnNum(columnNum);
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
                ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId());
                if (safetyCheckFlowDO != null) {
                    tbRecordVO.setNodeNo(safetyCheckFlowDO.getCurrentNodeNo());
                    tbRecordVO.setCycleCount(safetyCheckFlowDO.getCycleCount());
                }
            }
        }
        return tbRecordVO;
    }

    @Override
    public void reallocatePatrolTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId) {
        String operateUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, operUserId);
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(),
                taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(),taskStoreDO.getTaskType(),null);
        TbPatrolStoreHistoryDo historyDO = new TbPatrolStoreHistoryDo();
        historyDO.setOperateType(DisplayConstant.ActionKeyConstant.REALLOCATE);
        historyDO.setOperateUserId(operUserId);
        historyDO.setOperateUserName(operateUserName);
        historyDO.setSubTaskId(0L);
        historyDO.setCreateTime(new Date());
        historyDO.setBusinessId(tbPatrolStoreRecordDO.getId());
        historyDO.setNodeNo(taskStoreDO.getNodeNo());
        historyDO.setRemark(operateUserName + "重新分配门店任务");
        historyDO.setDeleted(false);
        historyDO.setActionKey(PatrolStoreConstant.ActionKeyConstant.REALLOCATE);
        tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, historyDO);
    }

    @Override
    public PageInfo getGroupDataByStore(String enterpriseId, RecordByMetaStaColumnIdRequest request) {
        // 查询所有巡店记录的检查项列表
        SummaryByStoreVo summaryByStoreVo = new SummaryByStoreVo();
        String regionPath = null;
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, request.getRegionIds());
            if (CollectionUtils.isNotEmpty(regionList)) {
                regionPath = regionList.get(0).getFullRegionPath();
            }
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SummaryByStoreDTO> groupDataByStore = tbDataStaTableColumnMapper.getGroupDataByStore(enterpriseId, request.getMetaColumnId(),
                request.getBeginDate(), request.getEndDate(), regionPath,request.getStoreIds());
        PageInfo<SummaryByStoreDTO> summaryByStoreDTOPageInfo = new PageInfo<>(groupDataByStore);
        summaryByStoreDTOPageInfo.setTotal(groupDataByStore.size());
        return  summaryByStoreDTOPageInfo;
    }

    @Override
    public PageInfo getRecordList(String enterpriseId, RecordListRequest recordListRequest, SysRoleDO currUserRole) {
        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();
        Date endTime = new Date(System.currentTimeMillis());
        Calendar now = Calendar.getInstance();
        Integer recentDay = recordListRequest.getRecentDay() == null ? 30 : recordListRequest.getRecentDay();
        now.setTime(endTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - recentDay);
        Date beginTime = now.getTime();
        beginTime = recordListRequest.getBeginTime() == null ? beginTime : recordListRequest.getBeginTime();
        endTime = recordListRequest.getEndTime() == null ? endTime : recordListRequest.getEndTime();
        // 用户id
        List<String> userIdList = recordListRequest.getUserIdList();
        List<Long> roleIdList = recordListRequest.getRoleIdList();
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIdList = sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList);
            if (CollectionUtils.isEmpty(userIdList)) {
                return new PageInfo(new ArrayList());
            }
        }
        List<String> regionPathList = new ArrayList<>();
        List<String> regionIdList = new ArrayList<>();
        List<String> storeIdList = new ArrayList<>();
        //查询用户权限 2021-7-1 获得用户最高优先级角色从查库改为查缓存
//        SysRoleDO currUserRole = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterpriseId, recordListRequest.getUserId());
        if (currUserRole != null && !AuthRoleEnum.ALL.getCode().equals(currUserRole.getRoleAuth())
                && CollectionUtils.isEmpty(recordListRequest.getStoreIdList())) {
            List<UserAuthMappingDO> store = new ArrayList<>();
            List<UserAuthMappingDO> region = new ArrayList<>();
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, recordListRequest.getUserId());
            ListUtils.emptyIfNull(userAuthMappingList)
                    .forEach(data -> {
                        if (data.getType().equals(UserAuthMappingTypeEnum.STORE.getCode())) {
                            store.add(data);
                        } else {
                            region.add(data);
                        }
                    });
            storeIdList = ListUtils.emptyIfNull(store).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
            regionIdList = ListUtils.emptyIfNull(region).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
            if (AuthRoleEnum.INCLUDE_SUBORDINATE.getCode().equals(currUserRole.getRoleAuth())) {
                if (CollectionUtils.isNotEmpty(regionIdList)) {
                    regionIdList.forEach(e -> regionPathList.add(regionService.getRegionPath(enterpriseId, e).replace("]", ""))
                    );
                }
            }
            recordListRequest.setStoreIdList(storeIdList);
            //为空返回空
            if (CollectionUtils.isEmpty(storeIdList) && CollectionUtils.isEmpty(regionIdList)) {
                log.info("getRecordList记录为空");
                return new PageInfo(new ArrayList());
            }
        }
        PageHelper.startPage(recordListRequest.getPageNum(), recordListRequest.getPageSize());
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList =
                tbPatrolStoreRecordMapper.getPatrolRecordListForMobile(enterpriseId, beginTime, endTime, recordListRequest.getPatrolType(),
                        userIdList, recordListRequest.getStoreIdList(), recordListRequest.getStatus(), regionIdList, regionPathList);
        List<Long> recordIdList = tbPatrolStoreRecordList.stream()
                .map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordIdList)) {
            log.info("getRecordList记录为空");
            return new PageInfo(new ArrayList());
        }
        // 获取任务id
        Set<Long> unifyTaskIds =
                tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getTaskId).collect(Collectors.toSet());
        // 获取门店id
        List<String> storeIdListFilter =
                tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getStoreId).collect(Collectors.toList());


        // 查询所有巡店记录的检查项列表
        List<TbDataStaTableColumnDO> staTableColumnDOList =
                tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList);
        List<TbDataDefTableColumnDO> defTableColumnDOList =
                tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList, null);
        // map:recordId->StaColumnData
        Map<Long, List<TbDataStaTableColumnDO>> recordStaDateColumnMap =
                staTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId));
        // map:recordId->DefColumnData
        Map<Long, List<TbDataDefTableColumnDO>> recordDefDateColumnMap =
                defTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getBusinessId));
        //任务人员信息
        Map<String, List<PersonDTO>> personMap = getTaskPerson(enterpriseId, unifyTaskIds, storeIdListFilter, UnifyTaskConstant.ROLE_APPROVAL);

        //查询当前人的子任务

        tbPatrolStoreRecordList.forEach(tbPatrolStoreRecordDO -> {
            PatrolStoreRecordVO patrolStoreRecordVO = new PatrolStoreRecordVO();
            patrolStoreRecordVO.setTbPatrolStoreRecordDO(tbPatrolStoreRecordDO);
            List<TbDataStaTableColumnDO> staTableColumnDOS =
                    recordStaDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());
            List<TbDataDefTableColumnDO> defTableColumnDOS =
                    recordDefDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());
            int unPassCount =
                    (int) staTableColumnDOS.stream().filter(data -> "FAIL".equals(data.getCheckResult())).count();
            int canQuestionCount = (int) staTableColumnDOS.stream()
                    .filter(data -> "FAIL".equals(data.getCheckResult()) && data.getTaskQuestionId() == 0).count();

            patrolStoreRecordVO.setCanQuestion(canQuestionCount > 0);
            Integer staCount = staTableColumnDOS.size();
            Integer defCount = defTableColumnDOS.size();
            Integer allCount = staCount + defCount;
            //查询当前人的
            if (StringUtils.isNotBlank(tbPatrolStoreRecordDO.getSupervisorId())) {
                List<PersonDTO> personList = new ArrayList<>();
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(tbPatrolStoreRecordDO.getSupervisorId());
                personDTO.setUserName(tbPatrolStoreRecordDO.getSupervisorName());
                personList.add(personDTO);
                patrolStoreRecordVO.setPersonList(personList);
            } else {
                List<PersonDTO> personList = personMap.get(tbPatrolStoreRecordDO.getTaskId() + "#" + tbPatrolStoreRecordDO.getStoreId()+ "#" + tbPatrolStoreRecordDO.getLoopCount());
                patrolStoreRecordVO.setPersonList(personList);
            }

            patrolStoreRecordVO.setAllCount(allCount);
            patrolStoreRecordVO.setUnPassCount(unPassCount);
            patrolStoreRecordVOS.add(patrolStoreRecordVO);
        });
        PageInfo pageInfo = new PageInfo(tbPatrolStoreRecordList);
        pageInfo.setList(patrolStoreRecordVOS);
        return pageInfo;
    }

    @Override
    public RecordByCheckColumnIdVO getRecordListByMetaStaColumnId(String enterpriseId, RecordByMetaStaColumnIdRequest request) {
        // 查询所有巡店记录的检查项列表
        RecordByCheckColumnIdVO recordByCheckColumnIdVO = new RecordByCheckColumnIdVO();
        String regionPath = null;
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, request.getRegionIds());
            if (CollectionUtils.isNotEmpty(regionList)) {
                regionPath = regionList.get(Constants.INDEX_ZERO).getFullRegionPath();
            }
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<TbDataStaTableColumnDO> columnList = tbDataStaTableColumnMapper.getListByMetaColumnId(enterpriseId, request.getMetaColumnId(),
                request.getBeginDate(), request.getEndDate(), regionPath, request.getStoreIds(),request.getCheckResult());
        PageInfo pageInfo = new PageInfo(columnList);
        if (CollectionUtils.isEmpty(columnList)) {
            return new RecordByCheckColumnIdVO(Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO,Constants.INDEX_ZERO,null);
        }
        List<Map<String, Long>> countGroupByMetaColumnId = tbDataStaTableColumnMapper.getCountGroupByMetaColumnId(enterpriseId, request.getMetaColumnId(),
                request.getBeginDate(), request.getEndDate(), regionPath, request.getStoreIds());
        Map<String, Long> stringLongMap = convertListToMap(countGroupByMetaColumnId);
        Set<Long> businessIds = columnList.stream().map(TbDataStaTableColumnDO::getBusinessId).collect(Collectors.toSet());
        // Map: businessId->record
        List<TbPatrolStoreRecordDO> recordList = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, new ArrayList<>(businessIds));
        Map<Long, TbPatrolStoreRecordDO> idRecordMap = recordList.stream().collect(Collectors.toMap(a -> a.getId(), Function.identity(), (a, b) -> a));
        List<RecordByMetaStaColumnIdVO> resultList = columnList.stream().map(column -> RecordByMetaStaColumnIdVO.builder()
                .column(column)
                .record(idRecordMap.get(column.getBusinessId()))
                .build()).collect(Collectors.toList());
        pageInfo.setList(resultList);
        int failNum = stringLongMap.getOrDefault(MetaTableConstant.CheckResultConstant.FAIL,0L).intValue();
        int passNum = stringLongMap.getOrDefault(MetaTableConstant.CheckResultConstant.PASS,0L).intValue();
        int inapplicableNum = stringLongMap.getOrDefault(MetaTableConstant.CheckResultConstant.INAPPLICABLE,0L).intValue();
        recordByCheckColumnIdVO.setPageInfo(pageInfo);
        recordByCheckColumnIdVO.setPatrolNum(failNum+passNum+inapplicableNum);
        recordByCheckColumnIdVO.setFailNum(failNum);
        recordByCheckColumnIdVO.setPassNum(passNum);
        recordByCheckColumnIdVO.setInapplicableNum(inapplicableNum);
        return recordByCheckColumnIdVO;
    }

    @Override
    public List<QuestionListVO> getSimpleQuestionList(String enterpriseId, QuestionListRequest request) {
        log.info("运营看板工单列表入参{}", request);
        String regionId = request.getRegionId();
        if (StrUtil.isNotBlank(regionId)) {
            List<String> storeIds = getStoreIdByRegion(enterpriseId, regionId);
            request.setStoreIdList(storeIds);
        }
        List<String> userIdList = request.getUserIdList();
        List<String> storeIdList = request.getStoreIdList();
        List<Long> recordIdList = request.getRecordIdList();
        Date beginDate = request.getBeginDate();
        Date endDate = request.getEndDate();
        List<Long> roleIdList = request.getRoleIdList();
        // 根据职位选人
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIdList.addAll(sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList));
        }
        List<TbDataStaTableColumnDO> columnDOList = new ArrayList<>();
        List<TbQuestionRecordDO> questionRecordDOList = Lists.newArrayList();
        //通过人查询
        if (CollectionUtils.isNotEmpty(userIdList)) {
            if (request.getPageNum() != null && request.getPageSize() != null) {
                PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
            }
            questionRecordDOList = questionRecordDao.getUserCreateQuestionRecordByUserId(enterpriseId, userIdList, Arrays.asList(QuestionTypeEnum.PATROL_STORE.getCode()), beginDate, endDate);
            List<Long> dataColumnIds = ListUtils.emptyIfNull(questionRecordDOList).stream().map(o -> o.getDataColumnId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dataColumnIds)) {
                columnDOList = tbDataStaTableColumnMapper.selectByIds(enterpriseId, dataColumnIds);
            }
        }
        //通过门店查询
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            if (request.getPageNum() != null && request.getPageSize() != null) {
                PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
            }
            questionRecordDOList = questionRecordDao.getUserCreateQuestionRecordByStoreId(enterpriseId, storeIdList, Arrays.asList(QuestionTypeEnum.PATROL_STORE.getCode()), beginDate, endDate);
            List<Long> dataColumnIds = ListUtils.emptyIfNull(questionRecordDOList).stream().map(o -> o.getDataColumnId()).collect(Collectors.toList());
            columnDOList = tbDataStaTableColumnMapper.selectByIds(enterpriseId, dataColumnIds);
        }
        //通过记录查询
        if (CollectionUtils.isNotEmpty(recordIdList)) {
            if (request.getPageNum() != null && request.getPageSize() != null) {
                PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
            }
            columnDOList = tbDataStaTableColumnMapper.getPatrolQuestionListByRecordIdList(enterpriseId, recordIdList, beginDate, endDate);
            List<Long> dataColumnIds = ListUtils.emptyIfNull(columnDOList).stream().map(o -> o.getId()).collect(Collectors.toList());
            questionRecordDOList = questionRecordDao.selectListDataColumnIdList(enterpriseId, dataColumnIds);
        }
        //没查询到工单列表,直接返回
        if (CollectionUtils.isEmpty(columnDOList)) {
            return new ArrayList<>();
        }
        //子任务id
        List<Long> taskIdList = new ArrayList<>();
        //门店id
        List<String> finalStoreIdList = new ArrayList<>();
        List<Long> columnIdList = new ArrayList<>();

        columnDOList.forEach(data -> {
            taskIdList.add(data.getTaskQuestionId());
            finalStoreIdList.add(data.getStoreId());
            columnIdList.add(data.getMetaColumnId());
        });
        //门店
        List<StoreDO> storeDOList = storeMapper.getByStoreIds(enterpriseId, finalStoreIdList);
        //map:storeId -> storeDO
        Map<String, StoreDO> storeMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));

        // Map:taskId->taskParentDO
        List<TaskParentDO> taskParentDOS = taskParentMapper.selectTaskByIds(enterpriseId, taskIdList);
        Map<Long, TaskParentDO> taskIdTaskMap =
                taskParentDOS.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));

        // Map:userId->UserDO,工单创建人
        Set<String> createUserIds = taskParentDOS.stream().map(TaskParentDO::getCreateUserId).collect(Collectors.toSet());
        List<EnterpriseUserDO> userDOS = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(createUserIds));
        Map<String, EnterpriseUserDO> userIdUserMap =
                userDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, data -> data, (a, b) -> a));

        Map<Long, Date> taskIdDoneTimeMap = new HashMap<>();
        Map<Long, Long> dataColumnIdTaskStoreIdMap =
                questionRecordDOList.stream().collect(Collectors.toMap(TbQuestionRecordDO::getDataColumnId, TbQuestionRecordDO::getTaskStoreId, (a, b) -> a));
        List<Long> taskStoreIdList = questionRecordDOList.stream().map(TbQuestionRecordDO::getTaskStoreId).collect(Collectors.toList());
        List<TaskStoreDO> taskStores = taskStoreMapper.listByUnifyIds(enterpriseId, taskStoreIdList, request.getNodeNo());
        Map<Long, TaskStoreDO> taskStoreMap = new HashMap<>();
        Map<Long, Long> taskIdMap = new HashMap<>();
        for (TaskStoreDO taskStoreDO : taskStores) {
            taskStoreMap.put(taskStoreDO.getId(), taskStoreDO);
            taskIdMap.put(taskStoreDO.getUnifyTaskId(), taskStoreDO.getUnifyTaskId());
            taskIdDoneTimeMap.put(taskStoreDO.getId(), taskStoreDO.getHandleTime());
        }

        List<TbDataStaTableColumnDO> columnDOListTemp = new ArrayList<>();
        for (TbDataStaTableColumnDO tbDataStaTableColumnDO:columnDOList) {
            if (taskIdMap.containsKey(tbDataStaTableColumnDO.getTaskQuestionId())){
                columnDOListTemp.add(tbDataStaTableColumnDO);
            }
        }

        Map<Long, TbMetaStaTableColumnDO> metaColumnMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnIdList)) {
            List<TbMetaStaTableColumnDO> columnDOS = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, columnIdList);
            for (TbMetaStaTableColumnDO columnDO : columnDOS) {
                metaColumnMap.put(columnDO.getId(), columnDO);
            }
        }

        return columnDOListTemp.stream().map(a -> {
            Long taskStoreId = dataColumnIdTaskStoreIdMap.get(a.getId());
            QuestionListVO questionListVO = new QuestionListVO();
            questionListVO.setColumnDO(a);
            TaskParentDO taskParentDO = taskIdTaskMap.get(a.getTaskQuestionId());
            if (taskParentDO != null) {
                questionListVO.setTaskParentDO(taskParentDO);
                EnterpriseUserDO user = userIdUserMap.get(taskParentDO.getCreateUserId());
                questionListVO.setUser(user);
            }
            if(taskStoreId != null){
                questionListVO.setTaskStoreDO(taskStoreMap.get(taskStoreId));
                questionListVO.setDoneTime(taskIdDoneTimeMap.get(taskStoreId));
            }
            questionListVO.setStore(storeMap.get(a.getStoreId()));
            questionListVO.setMetaColumn(metaColumnMap.get(a.getMetaColumnId()));
            return questionListVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionListVO> getDefaultSimpleQuestionList(String enterpriseId, QuestionListRequest request, CurrentUser user) {
        //全公司查询根区域
        if (AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())) {
            request.setRegionId(Constants.ROOT_REGION_ID);
            return this.getSimpleQuestionList(enterpriseId, request);
        }
        String regionId = null;
        List<String> storeIds = new ArrayList<>();
        List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, user.getUserId());
        for (UserAuthMappingDO userAuthMappingDO : userAuthMappingDOS) {
            if (UserAuthMappingTypeEnum.REGION.getCode().equals(userAuthMappingDO.getType())) {
                regionId = userAuthMappingDO.getMappingId();
                break;
            }
            if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuthMappingDO.getType())) {
                storeIds.add(userAuthMappingDO.getMappingId());
            }
        }
        //默认查询第一个区域
        if (regionId != null) {
            request.setRegionId(regionId);
            return this.getSimpleQuestionList(enterpriseId, request);
        }
        //无区域则查询所有门店
        if (CollectionUtils.isNotEmpty(storeIds)) {
            request.setStoreIdList(storeIds);
            return this.getSimpleQuestionList(enterpriseId, request);
        }
        return new ArrayList<>();
    }

    @Override
    public PageInfo getPatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest, CurrentUser currentUser) {
        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();
        // 默认30天
        String beginTimeStr = null;
        String endTimeStr = null;
        if (patrolRecordRequest.getRecentDay() != null) {
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime beginTime = endTime.minusDays(patrolRecordRequest.getRecentDay());
            beginTimeStr = DateUtils.convertDateTimeToString(beginTime);
            endTimeStr = DateUtils.convertDateTimeToString(endTime);
        } else if (patrolRecordRequest.getStatus() != null && patrolRecordRequest.getStatus() == 1) {
            beginTimeStr = patrolRecordRequest.getBeginTime() + " 00:00:00";
            endTimeStr = patrolRecordRequest.getEndTime() + " 23:59:59";
        }

        // 用户id
        List<String> userIdList = patrolRecordRequest.getUserIdList() == null ? new ArrayList<>() : patrolRecordRequest.getUserIdList();
        List<Long> roleIdList = patrolRecordRequest.getRoleIdList();
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            List<String> roleUserIdList = sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList);
            if (CollectionUtils.isEmpty(roleUserIdList) && CollectionUtils.isEmpty(userIdList)) {
                return new PageInfo<PatrolStoreRecordVO>(new ArrayList<>());
            }
            if (CollectionUtils.isNotEmpty(roleUserIdList)) {
                userIdList.addAll(roleUserIdList);
            }
        }
        List<String> regionPathList = new ArrayList<>();
        List<String> regionIdList = new ArrayList<>();
        List<String> storeIdList = patrolRecordRequest.getStoreIdList();

        //查询用户权限 2021-7-1 获得用户最高优先级角色从查库改为查缓存
//        SysRoleDO currUserRole = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterpriseId, patrolRecordRequest.getUserId());
        SysRoleDO currUserRole = currentUser.getSysRoleDO();

        if (currUserRole != null && !AuthRoleEnum.ALL.getCode().equals(currUserRole.getRoleAuth()) && CollectionUtils.isEmpty(storeIdList) ) {
            List<UserAuthMappingDO> store = new ArrayList<>();
            List<UserAuthMappingDO> region = new ArrayList<>();
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, patrolRecordRequest.getUserId());
            ListUtils.emptyIfNull(userAuthMappingList)
                    .forEach(data -> {
                        if (data.getType().equals(UserAuthMappingTypeEnum.STORE.getCode())) {
                            store.add(data);
                        } else {
                            region.add(data);
                        }
                    });
            storeIdList = ListUtils.emptyIfNull(store).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
            regionIdList = ListUtils.emptyIfNull(region).stream()
                    .map(UserAuthMappingDO::getMappingId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
            patrolRecordRequest.setStoreIdList(storeIdList);
            //为空返回空
            if (CollectionUtils.isEmpty(storeIdList) && CollectionUtils.isEmpty(regionIdList)) {
                log.info("getRecordList记录为空");
                return new PageInfo<PatrolStoreRecordVO>(new ArrayList<>());
            }
            if (AuthRoleEnum.INCLUDE_SUBORDINATE.getCode().equals(currUserRole.getRoleAuth())) {
                if (CollectionUtils.isNotEmpty(regionIdList)) {
                    regionPathList = regionMapper.getFullPathByIds(enterpriseId, regionIdList);
                    regionIdList = null;
                }
            }
        }
        String queryRegionPath = null;
        if (StringUtils.isNotBlank(patrolRecordRequest.getRegionId())) {
            queryRegionPath = regionService.getRegionPath(enterpriseId, patrolRecordRequest.getRegionId());
        }
        PageHelper.clearPage();
        PageHelper.startPage(patrolRecordRequest.getPageNum(), patrolRecordRequest.getPageSize(), true);

        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList = new ArrayList<>();
        if (!patrolRecordRequest.getUserCreateTimeFilterDate()){
            tbPatrolStoreRecordList =  tbPatrolStoreRecordMapper.getNewPatrolRecordListForMobile(enterpriseId, beginTimeStr, endTimeStr, patrolRecordRequest.getPatrolType(),
                    patrolRecordRequest.getPatrolMode(), patrolRecordRequest.getPatrolOverdue(),
                    userIdList, storeIdList, patrolRecordRequest.getStatus(), regionIdList,
                    regionPathList, patrolRecordRequest.getMetaTableId(), patrolRecordRequest.getTaskName(), patrolRecordRequest.getStoreName(),
                    patrolRecordRequest.getRegionId(),patrolRecordRequest.getGetDirectStore(),queryRegionPath,
                    patrolRecordRequest.getOverdueTaskContinue(), patrolRecordRequest.getBusinessCheckType(),
                    patrolRecordRequest.getRecheckUserIdList(), patrolRecordRequest.getRecheckStatus(), patrolRecordRequest.getPatrolTypeList());
        }else {
            //移动端 报表覆盖数据跳转到详情
            tbPatrolStoreRecordList= tbPatrolStoreRecordMapper.getDirectorStoreNewPatrolRecordListForMobile(enterpriseId, beginTimeStr, endTimeStr,
                    patrolRecordRequest.getStatus(), patrolRecordRequest.getRegionId(),storeIdList,
                    patrolRecordRequest.getGetDirectStore(), patrolRecordRequest.getUserCreateTimeFilterDate());
        }

        return handleTbPatrolStoreRecordList(enterpriseId, patrolRecordRequest.getDbName(), patrolStoreRecordVOS, tbPatrolStoreRecordList);
    }

    @Override
    public PatrolRecordDataVO getPatrolRecordData(String enterpriseId,CurrentUser currentUser) {
        PatrolRecordDataVO patrolRecordData = tbPatrolStoreRecordMapper.getPatrolRecordData(enterpriseId, currentUser.getUserId());
        return patrolRecordData;
    }

    private PageInfo handleTbPatrolStoreRecordList(String enterpriseId, String dbName, List<PatrolStoreRecordVO> patrolStoreRecordVOS, List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList) {
        List<Long> recordIdList = tbPatrolStoreRecordList.stream()
                .map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordIdList)) {
            log.info("getRecordList记录为空");
            return new PageInfo(new ArrayList());
        }

        // 获取metaTableId
        List<Long> metaTableIdList = new ArrayList<>();
        Map<Long, List<TbDataTableDO>> dataTableMap = Maps.newHashMap();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, recordIdList, PATROL_STORE);
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
            dataTableMap = dataTableList.stream()
                    .collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));
        }
        List<TbMetaTableDO> metaTableDOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metaTableIdList)){
            metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        }

        // map:recordId->tbMetaTableDOMap
        Map<Long, TbMetaTableDO> tbMetaTableDOMap =
                metaTableDOList.stream()
                        .filter(a -> a.getId() != null && a.getTableName() != null)
                        .collect(Collectors.toMap(TbMetaTableDO::getId, data->data));

        // 查询所有巡店记录的检查项列表
        List<TbDataStaTableColumnDO> staTableColumnDOList =
                tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList);
        List<TbDataDefTableColumnDO> defTableColumnDOList =
                tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList, null);
        // map:recordId->StaColumnData
        Map<Long, List<TbDataStaTableColumnDO>> recordStaDateColumnMap =
                staTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId));
        // map:recordId->DefColumnData
        Map<Long, List<TbDataDefTableColumnDO>> recordDefDateColumnMap =
                defTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getBusinessId));

        // map:recordId->StaColumnData
        Map<Long, List<TbDataStaTableColumnDO>> recordStaDateColumnDataTableMap =
                staTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getDataTableId));
        // map:recordId->DefColumnData
        Map<Long, List<TbDataDefTableColumnDO>> recordDefDateColumnDataTableMap =
                defTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getDataTableId));

        List<TbPatrolStoreRecordInfoDO> tbPatrolStoreRecordInfoList = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfoList(enterpriseId, recordIdList);
        Map<Long, TbPatrolStoreRecordInfoDO> tbPatrolStoreRecordInfoMap = ListUtils.emptyIfNull(tbPatrolStoreRecordInfoList)
                .stream().collect(Collectors.toMap(TbPatrolStoreRecordInfoDO::getId, data -> data, (a, b) -> a));

        List<Long> unifyTaskIds = tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getTaskId).collect(Collectors.toList());
        Map<Long,TaskParentDO> parentDOMap = Maps.newHashMap();
        if(CollectionUtils.isNotEmpty(unifyTaskIds)){
            List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, unifyTaskIds);
            if(CollectionUtils.isNotEmpty(parentDOList)){
                parentDOMap = parentDOList.stream().collect(Collectors.toMap(TaskParentDO::getId, data -> data,(a, b)->a));
            }
        }

        Map<Long, TaskParentDO> finalParentDOMap = parentDOMap;

        // 区域统计异步缓存
        Map<Long, Future<List<PersonDTO>>> idTaskMap = new HashMap<>();

        List<String> supervisorIdList = tbPatrolStoreRecordList.stream().filter(data -> StringUtils.isNotBlank(data.getSupervisorId()))
                .map(TbPatrolStoreRecordDO::getSupervisorId).collect(Collectors.toList());
        List<String> auditUserIdList = tbPatrolStoreRecordInfoList.stream().filter(data -> StringUtils.isNotBlank(data.getAuditUserId()))
                .map(TbPatrolStoreRecordInfoDO::getAuditUserId).collect(Collectors.toList());
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, Stream.of(supervisorIdList, auditUserIdList).flatMap(Collection::stream).collect(Collectors.toList()));
        Map<String, EnterpriseUserDO> userMap = ListUtils.emptyIfNull(userDOList).stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity()));

        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);

        Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
        tbPatrolStoreRecordList.forEach(tbPatrolStoreRecordDO -> {
            PatrolStoreRecordVO patrolStoreRecordVO = new PatrolStoreRecordVO();
            tbPatrolStoreRecordDO.setOverdueRun(settingDO.getOverdueTaskContinue());
            patrolStoreRecordVO.setTbPatrolStoreRecordDO(tbPatrolStoreRecordDO);
            EnterpriseUserDO supervisor = userMap.get(tbPatrolStoreRecordDO.getSupervisorId());
            if(Objects.nonNull(supervisor)){
                patrolStoreRecordVO.setSupervisorAvatar(supervisor.getAvatar());
            }
            List<TbDataStaTableColumnDO> staTableColumnDOS =
                    recordStaDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());
            List<TbDataDefTableColumnDO> defTableColumnDOS =
                    recordDefDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());
            int unPassCount =
                    (int) staTableColumnDOS.stream().filter(data -> "FAIL".equals(data.getCheckResult())).count();
            int canQuestionCount = (int) staTableColumnDOS.stream()
                    .filter(data -> "FAIL".equals(data.getCheckResult()) && data.getTaskQuestionId() == 0).count();

            patrolStoreRecordVO.setCanQuestion(canQuestionCount > 0);
            Integer staCount = staTableColumnDOS.size();
            Integer defCount = defTableColumnDOS.size();
            Integer allCount = staCount + defCount;
            TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMap.get(tbPatrolStoreRecordDO.getId());
            if(Objects.nonNull(tbPatrolStoreRecordInfoDO)){
                patrolStoreRecordVO.setParams(tbPatrolStoreRecordInfoDO.getParams());
            }
            //查询当前人的已完成
            TaskParentDO taskParentDO =  finalParentDOMap.get(tbPatrolStoreRecordDO.getTaskId());
            if (tbPatrolStoreRecordDO.getStatus() != null && tbPatrolStoreRecordDO.getStatus() == 1) {
                List<PersonDTO> personList = new ArrayList<>();
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(tbPatrolStoreRecordDO.getSupervisorId());
                personDTO.setUserName(tbPatrolStoreRecordDO.getSupervisorName());
                if(Objects.nonNull(supervisor)){
                    personDTO.setAvatar(supervisor.getAvatar());
                }
                personList.add(personDTO);
                patrolStoreRecordVO.setPersonList(personList);
                if (tbPatrolStoreRecordInfoDO != null && StringUtils.isNotBlank(tbPatrolStoreRecordInfoDO.getAuditUserId())) {
                    List<PersonDTO> aduitList = new ArrayList<>();
                    PersonDTO personAduit = new PersonDTO();
                    personAduit.setUserId(tbPatrolStoreRecordInfoDO.getAuditUserId());
                    personAduit.setUserName(tbPatrolStoreRecordInfoDO.getAuditUserName());
                    EnterpriseUserDO aduitUser = userMap.get(personAduit.getUserId());
                    if(Objects.nonNull(aduitUser)){
                        personAduit.setAvatar(aduitUser.getAvatar());
                    }
                    aduitList.add(personAduit);
                    patrolStoreRecordVO.setAduitPersonList(aduitList);
                }
            } else if (tbPatrolStoreRecordDO.getTaskId() != null && tbPatrolStoreRecordDO.getTaskId() == 0 || tbPatrolStoreRecordDO.getLoopCount() == 0
                    || (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType()))) {
                List<PersonDTO> personList = new ArrayList<>();
                PersonDTO personDTO = new PersonDTO();
                personDTO.setUserId(tbPatrolStoreRecordDO.getSupervisorId());
                personDTO.setUserName(tbPatrolStoreRecordDO.getSupervisorName());
                if(Objects.nonNull(supervisor)){
                    personDTO.setAvatar(supervisor.getAvatar());
                }
                personList.add(personDTO);
                patrolStoreRecordVO.setPersonList(personList);
            } else {
                String nodeNo = UnifyNodeEnum.FIRST_NODE.getCode();
                if (tbPatrolStoreRecordDO.getStatus() != null && tbPatrolStoreRecordDO.getStatus() == 2) {
                    nodeNo = UnifyNodeEnum.SECOND_NODE.getCode();
                }
                // 单查一条记录的人
                String finalNodeNo = nodeNo;
                idTaskMap.put(tbPatrolStoreRecordDO.getId(),
                        EXECUTOR_SERVICE.submit(() -> getTaskPersonList(enterpriseId, patrolStoreRecordVO, dbName, finalNodeNo)));
//                getTaskPersonList(enterpriseId, patrolStoreRecordVO, patrolRecordRequest.getDbName());
            }
            List<TbDataTableDO> tbDataTableDOList = finalDataTableMap.get(tbPatrolStoreRecordDO.getId());
            Long metaTableId = 0L;
            if(CollectionUtils.isNotEmpty(tbDataTableDOList)){
                metaTableId = tbDataTableDOList.get(0).getMetaTableId();
            }
            if (metaTableId != null && metaTableId > 0) {
                TbMetaTableDO tb = tbMetaTableDOMap.getOrDefault(metaTableId, new TbMetaTableDO());
                patrolStoreRecordVO.setMetaTableName(tb.getTableName());
                patrolStoreRecordVO.setTableProperty(tb.getTableProperty());
            }
            if(CollectionUtils.isNotEmpty(tbDataTableDOList)){
                List<TbDataTableVO> dataTableVOList = tbDataTableDOList.stream()
                                .map(dataTableDO -> TbDataTableVO.builder().id(dataTableDO.getId()).metaTableId(dataTableDO.getMetaTableId())
                                        .metaTableName(dataTableDO.getTableName()).tableProperty(dataTableDO.getTableProperty())
                                        .allCount(recordStaDateColumnDataTableMap.getOrDefault(dataTableDO.getId(), new ArrayList<>()).size() +
                                                recordDefDateColumnDataTableMap.getOrDefault(dataTableDO.getId(), new ArrayList<>()).size())
                                        .unPassCount(dataTableDO.getFailNum()).totalScore(dataTableDO.getTotalScore()).taskCalTotalScore(dataTableDO.getTaskCalTotalScore())
                                        .checkScore(dataTableDO.getCheckScore()).passCount(dataTableDO.getPassNum())
                                        .inApplicableCount(dataTableDO.getInapplicableNum())
                                        .build()).collect(Collectors.toList());
                patrolStoreRecordVO.setDataTableList(dataTableVOList);
            }

            patrolStoreRecordVO.setOverdue(false);
            //完成任务根据
            if (tbPatrolStoreRecordDO.getStatus() == 1 && tbPatrolStoreRecordDO.getSubEndTime() != null
                    && tbPatrolStoreRecordDO.getSignEndTime() != null) {
                patrolStoreRecordVO.setOverdue(tbPatrolStoreRecordDO.getSignEndTime().after(tbPatrolStoreRecordDO.getSubEndTime()));
            } else if (tbPatrolStoreRecordDO.getSubEndTime() != null) {
                patrolStoreRecordVO.setOverdue(new Date().after(tbPatrolStoreRecordDO.getSubEndTime()));
            }

            patrolStoreRecordVO.setAllCount(allCount);
            patrolStoreRecordVO.setUnPassCount(unPassCount);
            patrolStoreRecordVOS.add(patrolStoreRecordVO);
        });
        //同步等待10s

        tbPatrolStoreRecordList.forEach(e -> {
            Future future = idTaskMap.get(e.getId());
            try {
                if (future != null) {
                    future.get();
                }
            } catch (Exception exception) {
                log.error(" tbPatrolStoreRecordList 查询巡店记录失败 ", exception);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "查询巡店记录失败");
            }
        });
        PageInfo pageInfo = new PageInfo<>(tbPatrolStoreRecordList);
        pageInfo.setList(patrolStoreRecordVOS);
        return pageInfo;
    }

    @Override
    public PageInfo getStaffPlanPatrolRecordList(String enterpriseId, StaffPlanPatrolRecordRequest staffPlanPatrolRecordRequest, CurrentUser currentUser) {

        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();

        PageHelper.clearPage();
        PageHelper.startPage(staffPlanPatrolRecordRequest.getPageNum(), staffPlanPatrolRecordRequest.getPageSize(), true);

        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList = tbPatrolStoreRecordMapper.getStaffPlanPatrolRecordList(enterpriseId, staffPlanPatrolRecordRequest.getSubTaskId(), staffPlanPatrolRecordRequest.getStoreName());
        return handleTbPatrolStoreRecordList(enterpriseId, staffPlanPatrolRecordRequest.getDbName(), patrolStoreRecordVOS, tbPatrolStoreRecordList);
    }

    @Override
    public PatrolRecordAuthDTO getRecordAuth(String enterpriseId, CurrentUser currentUser, Long businessId, Long subTaskId) {
        String userId = currentUser.getUserId();
        PatrolRecordAuthDTO patrolRecordAuth = new PatrolRecordAuthDTO();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = null;
        String operId = null;
        String creater = null;
        String storeId = null;
        Long metaTableId = null;
        Long taskId = null;
        String nodeNo = UnifyNodeEnum.FIRST_NODE.getCode();
        if (businessId != null && subTaskId == null) {
            tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
            if (tbPatrolStoreRecordDO == null) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "巡店记录不存在");
            }
            if (tbPatrolStoreRecordDO.getStatus() == 2) {
                nodeNo = UnifyNodeEnum.SECOND_NODE.getCode();
            }
            operId = tbPatrolStoreRecordDO.getSupervisorId();
            subTaskId = tbPatrolStoreRecordDO.getSubTaskId();
            creater = tbPatrolStoreRecordDO.getCreateUserId();
            storeId = tbPatrolStoreRecordDO.getStoreId();
            patrolRecordAuth.setStatus(tbPatrolStoreRecordDO.getStatus());
            taskId = tbPatrolStoreRecordDO.getTaskId();
            //非自主巡店
            if (tbPatrolStoreRecordDO.getTaskId() > 0 && tbPatrolStoreRecordDO.getStatus() != 1) {
                TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                        userId, UnifyStatus.ONGOING.getCode(), nodeNo);
                if (taskSub == null) {
                    taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                            null, null, nodeNo);
                }
                TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
                if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
                    taskSub = taskSubMapper.selectSubTaskDetailByIdNew(enterpriseId, tbPatrolStoreRecordDO.getSubTaskId());
                }
                if(taskSub != null){
                    subTaskId = taskSub.getSubTaskId();
                }
            }
            List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(tbPatrolStoreRecordDO.getId()), PATROL_STORE);
            if(CollectionUtils.isNotEmpty(dataTableList)){
                metaTableId = dataTableList.get(0).getMetaTableId();
            }
        }
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        patrolRecordAuth.setAdmin(isAdmin);

        if (taskId != null && taskId == 0) {
            //自主巡店
            patrolRecordAuth.setCreater(userId.equals(creater));
            patrolRecordAuth.setHandler(userId.equals(operId));
            patrolRecordAuth.setCcPeople(false);
            patrolRecordAuth.setOverdue(false);
        } else {
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);

            if (taskSubDO == null) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "子任务不存在或已删除【"+subTaskId+"】");
            }
            TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskSubDO.getUnifyTaskId());
            taskId = taskSubDO.getUnifyTaskId();
            List<String> unifyPersonList = unifyTaskStoreService.selectCcPersonInfoByTaskList(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
            patrolRecordAuth.setCcPeople(false);
            for (String ccUserId : unifyPersonList) {
                if (userId.equals(ccUserId)) {
                    patrolRecordAuth.setCcPeople(true);
                    break;
                }
            }
            patrolRecordAuth.setCreater(userId.equals(taskSubDO.getCreateUserId()));
            //转交后任务不可处理
            patrolRecordAuth.setHandler(userId.equals(taskSubDO.getHandleUserId()) && !"turn".equals(taskSubDO.getActionKey()));
            // AI审批中，不可处理
            String taskStatusKey = RedisConstant.TASK_STATUS_KEY + enterpriseId + Constants.COLON + taskSubDO.getUnifyTaskId() + "_" + taskSubDO.getStoreId() + "_" + taskSubDO.getLoopCount();
            if (StringUtils.isNotBlank(redisUtilPool.getString(taskStatusKey))) {
                log.info("AI审批中，不可处理，taskStatusKey:{} ", taskStatusKey);
                patrolRecordAuth.setHandler(false);
            }
            patrolRecordAuth.setOverdue(false);

            //完成任务根据
            if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
                if(PatrolStoreRecordStatusEnum.UPCOMING_HANDLE.getStatus() == tbPatrolStoreRecordDO.getStatus()){
                    patrolRecordAuth.setOverdue(tbPatrolStoreRecordDO.getSubEndTime().getTime() < System.currentTimeMillis());
                }
                patrolRecordAuth.setStatus(tbPatrolStoreRecordDO.getStatus());
                if (tbPatrolStoreRecordDO.getSubBeginTime().getTime() > System.currentTimeMillis()) {
                    //未开始
                    patrolRecordAuth.setStatus(2);
                }
            }else if (UnifyStatus.COMPLETE.getCode().equals(taskSubDO.getSubStatus()) && taskSubDO.getHandleTime() != null) {
                patrolRecordAuth.setOverdue(taskSubDO.getHandleTime() > System.currentTimeMillis());
                patrolRecordAuth.setStatus(1);
            } else if (taskSubDO.getSubEndTime() != null) {
                patrolRecordAuth.setOverdue(taskSubDO.getSubEndTime() < System.currentTimeMillis());
                patrolRecordAuth.setStatus(0);
                if (taskSubDO.getSubBeginTime() > System.currentTimeMillis()) {
                    //未开始
                    patrolRecordAuth.setStatus(2);
                }
            }
            storeId = taskSubDO.getStoreId();
            if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
                storeId = tbPatrolStoreRecordDO.getStoreId();
            }

            if (tbPatrolStoreRecordDO == null) {
                tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(),
                        taskSubDO.getLoopCount(), null, null);
                if (tbPatrolStoreRecordDO != null) {
                    List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(tbPatrolStoreRecordDO.getId()), PATROL_STORE);
                    if(CollectionUtils.isNotEmpty(dataTableList)){
                        metaTableId = dataTableList.get(0).getMetaTableId();
                    }
                }
            }
        }
        if (tbPatrolStoreRecordDO != null && tbPatrolStoreRecordDO.getStatus() != 1) {
            patrolRecordAuth.setStatus(0);
        }
        patrolRecordAuth.setCheckTable(false);
        patrolRecordAuth.setResultAuth(false);
        if (metaTableId == null || metaTableId == 0L) {
            patrolRecordAuth.setCheckTable(true);
            patrolRecordAuth.setResultAuth(true);
        }
        if(Objects.nonNull(tbPatrolStoreRecordDO)){
            String metaTableIds = tbPatrolStoreRecordDO.getMetaTableIds();
            if(StringUtils.isBlank(metaTableIds)){
                patrolRecordAuth.setCheckTable(true);
                patrolRecordAuth.setResultAuth(true);
            }else{
                List<Long> metaTableIdList = Arrays.asList(metaTableIds.split(Constants.COMMA)).stream().filter(StringUtils::isNotBlank).map(Long::valueOf).collect(Collectors.toList());
                dealCheckTableAuth(enterpriseId, userId, isAdmin, patrolRecordAuth, metaTableIdList);
            }
        }
        if (patrolRecordAuth.getAdmin()) {
            patrolRecordAuth.setRegion(true);
        } else {
            StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, storeId);
            int storeAuth = userAuthMappingMapper.countUserAuthCountByUserIdAndStoreId(enterpriseId, userId, storeId);
            boolean regionAuth = storeAuth > 0;

            if (!regionAuth) {
                String storeRegion = storeDO.getRegionPath();
                String[] storeRegionPaths = storeRegion.split("/");
                if (storeRegionPaths.length > 0) {
                    regionAuth = userAuthMappingMapper.countUserAuthCountByUserIdAndRegionId(enterpriseId, userId, Arrays.asList(storeRegionPaths)) > 0;
                }
            }
            patrolRecordAuth.setRegion(regionAuth);
        }
        patrolRecordAuth.setViewAuth(false);
        //管理人、创建人、抄送人、处理人可以查看
        if(!patrolRecordAuth.getViewAuth()){

            if(patrolRecordAuth.getAdmin() || patrolRecordAuth.getCreater() != null && patrolRecordAuth.getCreater()
                    || patrolRecordAuth.getCcPeople() != null && patrolRecordAuth.getCcPeople()
                    || patrolRecordAuth.getHandler() != null && patrolRecordAuth.getHandler()){
                patrolRecordAuth.setViewAuth(true);
            }
        }
        //巡店记录，执行中巡店数据查看权限
        if(tbPatrolStoreRecordDO != null && !patrolRecordAuth.getViewAuth()){
            //已完成
            if(PatrolStoreRecordStatusEnum.FINISH.getStatus() == tbPatrolStoreRecordDO.getStatus()){
                patrolRecordAuth.setViewAuth(true);
            }

            //是否为相关处理人
            if(!patrolRecordAuth.getViewAuth()){
                TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                        userId, null, null);
                if(taskSubVO != null){
                    patrolRecordAuth.setViewAuth(true);
                }
            }
        }
        patrolRecordAuth.setNodeNo(nodeNo);
        patrolRecordAuth.setAppealValid(false);
        patrolRecordAuth.setAppealAuditAuth(false);
        patrolRecordAuth.setSelectReason(false);
        patrolRecordAuth.setCycleCount(0);
        if (tbPatrolStoreRecordDO != null  && TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            patrolRecordAuth.setHandler(scSafetyCheckFlowService.checkHandleAuth(enterpriseId, businessId, currentUser));
            ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, businessId);
            if(safetyCheckFlowDO != null){
                patrolRecordAuth.setNodeNo(safetyCheckFlowDO.getCurrentNodeNo());
                patrolRecordAuth.setCycleCount(safetyCheckFlowDO.getCycleCount());
                patrolRecordAuth.setSelectReason(safetyCheckFlowDO.getSelectReason());
                if(StringUtils.isNotBlank(safetyCheckFlowDO.getAppealReviewUser())) {
                    List<String> appealReviewUserList = JSONObject.parseArray(safetyCheckFlowDO.getAppealReviewUser(), String.class);
                    patrolRecordAuth.setAppealAuditAuth(appealReviewUserList.contains(currentUser.getUserId()));
                }
            }
            if (tbPatrolStoreRecordDO.getStatus() == 1) {
                EnterpriseSafetyCheckSettingsDTO enterpriseSafetyCheckSettingsDTO = enterpriseSettingRpcService.getSafetyCheckSettings(enterpriseId);
                TbPatrolStoreRecordInfoDO patrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, tbPatrolStoreRecordDO.getId());
                if (enterpriseSafetyCheckSettingsDTO != null) {
                    int appealValidDay = enterpriseSafetyCheckSettingsDTO.getAppealValidDay();
                    if (appealValidDay > 0) {
                        Date appealValidDate = DateUtil.plusDays(patrolStoreRecordInfoDO.getFinishTime(), appealValidDay);
                        patrolRecordAuth.setAppealValidDate(appealValidDate);
                        if (appealValidDate.after(new Date())) {
                            patrolRecordAuth.setAppealValid(true);
                        }
                    }
                }
            }
        }
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        patrolRecordAuth.setOverdueRun(settingDO.getOverdueTaskContinue());
        patrolRecordAuth.setSubTaskId(subTaskId);

        if(tbPatrolStoreRecordDO != null && BusinessCheckType.PATROL_RECHECK.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())
            && userId.equals(tbPatrolStoreRecordDO.getRecheckUserId())){
            patrolRecordAuth.setHandler(true);
        }
        DataSourceHelper.changeToMy();
        if("451c4fdf6b1645b79e439fea477c369e".equals(enterpriseId)  || "c0158c5197fd42a5abccf4570560d012".equals(enterpriseId)){
            patrolRecordAuth.setOverdueTaskContinue(Boolean.TRUE);
            return patrolRecordAuth;
        }
        List<TaskSubVO> byTaskId = taskSubMapper.getByTaskIdLimit1(enterpriseId, taskId);
        if (CollectionUtils.isNotEmpty(byTaskId)){
            TaskSubVO taskSubVO = byTaskId.get(0);
            patrolRecordAuth.setOverdueTaskContinue(taskSubVO.isOperateOverdue());
        }
        return patrolRecordAuth;
    }

    public void dealCheckTableAuth(String enterpriseId, String userId, boolean isAdmin, PatrolRecordAuthDTO patrolRecordAuth, List<Long> metaTableIdList){
        if(isAdmin){
            patrolRecordAuth.setResultAuth(true);
            patrolRecordAuth.setCheckTable(true);
            return;
        }
        patrolRecordAuth.setResultAuth(false);
        patrolRecordAuth.setCheckTable(false);
        List<TbMetaTableUserAuthDO> tableAuth = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, metaTableIdList);
        Map<String, TbMetaTableUserAuthDO> authMap = ListUtils.emptyIfNull(tableAuth).stream().collect(Collectors.toMap(o->o.getBusinessId() + Constants.MOSAICS + o.getUserId(), v -> v));
        for (TbMetaTableUserAuthDO tableAuthDO : tableAuth) {
            TbMetaTableUserAuthDO allUserAuth = authMap.get(tableAuthDO.getBusinessId() + Constants.MOSAICS + "all_user_id");
            TbMetaTableUserAuthDO userAuth = authMap.get(tableAuthDO.getBusinessId() + Constants.MOSAICS + tableAuthDO.getUserId());
            Boolean allUserUseAuth = Optional.ofNullable(allUserAuth).map(TbMetaTableUserAuthDO::getUseAuth).orElse(false);
            Boolean userUseAuth = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getUseAuth).orElse(false);
            Boolean allUserViewAuth = Optional.ofNullable(allUserAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
            Boolean userViewAuth = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
            if(patrolRecordAuth.getCheckTable() == false){
                patrolRecordAuth.setCheckTable(allUserUseAuth || userUseAuth);
            }
            if(patrolRecordAuth.getResultAuth() == false){
                patrolRecordAuth.setResultAuth(allUserViewAuth || userViewAuth);
            }

        }
    }

    @Override
    public List<QuestionListVO> getOperateQuestionList(String enterpriseId, QuestionListRequest request) {
        if (log.isInfoEnabled()) {
            log.info("运营看板工单列表入参{}", request);
        }
        String regionId = request.getRegionId();
        String regionPath = null;
        if (StrUtil.isNotBlank(regionId)) {
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, Collections.singletonList(regionId));
            if (CollectionUtils.isNotEmpty(regionPathDTOList)) {
                regionPath = regionPathDTOList.get(0).getRegionPath();
            }
        }
        List<String> storeIdList = request.getStoreIdList();
        if (StringUtils.isBlank(regionPath) && CollectionUtils.isEmpty(storeIdList)) {
            return new ArrayList<>();
        }
        Date beginDate = request.getBeginDate();
        Date endDate = request.getEndDate();

        if (request.getPageNum() != null && request.getPageSize() != null) {
            PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
        }
        List<TaskStoreDO> taskList = taskStoreMapper.getTaskStoreByRegionPathOrStoreId(enterpriseId, regionPath, storeIdList, beginDate, endDate,
                request.getGetDirectStore(),request.getNodeNo(),regionId,request.getQuestionType(), request.getMetaTableId(), request.getMetaColumnIds(), request.getIsOverDue());
        if (CollectionUtils.isEmpty(taskList)) {
            return new ArrayList<>();
        }
        List<Long> taskIds = taskList.stream().map(TaskStoreDO::getUnifyTaskId).collect(Collectors.toList());
        List<Long> taskStoreIdList = taskList.stream().map(TaskStoreDO::getId).collect(Collectors.toList());

        List<TaskParentDO> taskParentList = taskParentMapper.selectParentTaskBatch(enterpriseId, taskIds);
        Map<Long, TaskParentDO> taskMap = taskParentList.stream().collect(Collectors.toMap(TaskParentDO::getId, data -> data, (a, b) -> a));

        List<String> storeIds = taskList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeList = storeMapper.getByStoreIdList(enterpriseId, storeIds);
        Map<String, StoreDO> storeMap = storeList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));

        List<String> userIds = taskList.stream().map(TaskStoreDO::getCreateUserId).collect(Collectors.toList());
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        Map<String, EnterpriseUserDO> userMap = userList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, data -> data, (a, b) -> a));

        List<TbQuestionRecordDO> questionRecordDOList = questionRecordDao.questionListByTaskStoreIds(enterpriseId, taskStoreIdList);

        Map<Long, Long> taskColumnMap = new HashMap<>();
        List<Long> columnIdList = new ArrayList<>();
        for (TbQuestionRecordDO recordDO : questionRecordDOList) {
            taskColumnMap.put(recordDO.getTaskStoreId(), recordDO.getMetaColumnId());
            columnIdList.add(recordDO.getMetaColumnId());
        }
        Map<Long, TbMetaStaTableColumnDO> metaColumnMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnIdList)) {
            List<TbMetaStaTableColumnDO> columnDOS = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, columnIdList);
            for (TbMetaStaTableColumnDO columnDO : columnDOS) {
                metaColumnMap.put(columnDO.getId(), columnDO);
            }
        }

        return taskList.stream().map(data -> {
            QuestionListVO questionListVO = new QuestionListVO();
            questionListVO.setStore(storeMap.get(data.getStoreId()));
            questionListVO.setUser(userMap.get(data.getCreateUserId()));
            questionListVO.setTaskParentDO(taskMap.get(data.getUnifyTaskId()));
            questionListVO.setTaskStoreDO(data);
            questionListVO.setMetaColumn(metaColumnMap.get(taskColumnMap.get(data.getId())));
            return questionListVO;
        }).collect(Collectors.toList());
    }

    @Override
    public TbPatrolStoreRecordVO taskRecordInfo(String enterpriseId, Long taskStoreId, Long businessId) {
        TbPatrolStoreRecordDO storeRecordDO;
        if (businessId == null) {
            TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
            storeRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId,
                    taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null);

            if (storeRecordDO == null) {
                log.info("查询storeRecordDO 没有loopCount : taskStoreId : {}", taskStoreId);
                storeRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId,
                        taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), null, null, null);
            }
        } else {
            storeRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        }


        if (storeRecordDO == null) {
            throw new ServiceException("记录不存在");
        }
        TbPatrolStoreRecordVO tbPatrolStoreRecordVO = new TbPatrolStoreRecordVO();
        BeanUtils.copyProperties(storeRecordDO, tbPatrolStoreRecordVO);

        TbPatrolStoreRecordInfoDO patrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, storeRecordDO.getId());
        if (Objects.nonNull(patrolStoreRecordInfoDO)) {
            tbPatrolStoreRecordVO.setParams(patrolStoreRecordInfoDO.getParams());
        }

        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, storeRecordDO.getTaskId());

        if (taskParentDO != null) {
            tbPatrolStoreRecordVO.setRunRule(taskParentDO.getRunRule());
            tbPatrolStoreRecordVO.setTaskCycle(taskParentDO.getTaskCycle() != null ? taskParentDO.getTaskCycle() : Constants.ONCE);
            tbPatrolStoreRecordVO.setTaskDesc(taskParentDO.getTaskDesc());
        }

        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(enterpriseId, storeRecordDO.getCreateUserId());
        if (userDO != null) {
            tbPatrolStoreRecordVO.setCreateUserName(userDO.getName());
        }
        if (Constants.SYSTEM_USER_ID.equals(storeRecordDO.getCreateUserId())) {
            tbPatrolStoreRecordVO.setCreateUserName(Constants.SYSTEM_USER_NAME);
        }
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(storeRecordDO.getId()), PATROL_STORE);
        TbMetaTableDO metaTableDO = null;
        if(CollectionUtils.isNotEmpty(dataTableList)){
            metaTableDO = tbMetaTableMapper.selectById(enterpriseId, dataTableList.get(0).getMetaTableId());
        }
        if (metaTableDO != null) {
            tbPatrolStoreRecordVO.setMetaTableName(metaTableDO.getTableName());
            if(metaTableDO != null && !TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(), metaTableDO.getTableType())){
                Integer num = tbDataStaTableColumnMapper.dataStaColumnNumCount(enterpriseId, storeRecordDO.getId());
                tbPatrolStoreRecordVO.setTotalColumnNum(num);
            }
        }
        //多表名称展示
        if(CollectionUtils.isNotEmpty(dataTableList)) {
            List<String> metaTableNameList = dataTableList.stream().map(TbDataTableDO::getTableName).collect(Collectors.toList());
            String metaTableNames = StringUtils.join(metaTableNameList, Constants.PAUSE);
            tbPatrolStoreRecordVO.setMetaTableName(metaTableNames);
        }

        tbPatrolStoreRecordVO.setActualPatrolStoreDuration(DateUtils.formatBetween(storeRecordDO.getSignStartTime(),storeRecordDO.getSignEndTime()));
        TbPatrolStoreRecordInfoDO recordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, storeRecordDO.getId());
        if (recordInfoDO != null) {
            tbPatrolStoreRecordVO.setAuditTime(recordInfoDO.getAuditTime());
            tbPatrolStoreRecordVO.setAuditUserId(recordInfoDO.getAuditUserId());
            tbPatrolStoreRecordVO.setAuditUserName(recordInfoDO.getAuditUserName());
        }

        tbPatrolStoreRecordVO.setOverdue(false);
        if (tbPatrolStoreRecordVO.getStatus() == 1 && tbPatrolStoreRecordVO.getSubEndTime() != null
                && tbPatrolStoreRecordVO.getSignEndTime() != null) {
            tbPatrolStoreRecordVO.setOverdue(tbPatrolStoreRecordVO.getSignEndTime().after(tbPatrolStoreRecordVO.getSubEndTime()));
        } else if (tbPatrolStoreRecordVO.getSubEndTime() != null) {
            tbPatrolStoreRecordVO.setOverdue(new Date().after(tbPatrolStoreRecordVO.getSubEndTime()));
        }
        if (tbPatrolStoreRecordVO.getStatus() != 1 && tbPatrolStoreRecordVO.getTaskId() > 0) {
            List<String> userIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, storeRecordDO.getTaskId(),
                    storeRecordDO.getStoreId(), UnifyNodeEnum.FIRST_NODE.getCode(), storeRecordDO.getLoopCount());
            if (CollectionUtils.isNotEmpty(userIdList)) {
                List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
                Map<String, UnifyPersonDTO> peopleMap = new HashMap<>();
                for (EnterpriseUserDO enterpriseUserDO : userList) {
                    UnifyPersonDTO unifyPersonDTO = new UnifyPersonDTO();
                    unifyPersonDTO.setAvatar(enterpriseUserDO.getAvatar());
                    unifyPersonDTO.setUserName(enterpriseUserDO.getName());
                    unifyPersonDTO.setUserId(enterpriseUserDO.getUserId());
                    peopleMap.put(enterpriseUserDO.getUserId(), unifyPersonDTO);
                }
                List<UnifyPersonDTO> list = new ArrayList<>();
                for (String userId : userIdList) {
                    if (peopleMap.containsKey(userId)) {
                        list.add(peopleMap.get(userId));
                    }
                }
                tbPatrolStoreRecordVO.setHanderUserList(list);
            }
        }
        //查询
        if (taskParentDO != null) {
            Map<String, List<UnifyPersonDTO>> listMap = getTaskPerson(enterpriseId, Collections.singletonList(storeRecordDO.getTaskId()), storeRecordDO.getStoreId(), storeRecordDO.getLoopCount());
            tbPatrolStoreRecordVO.setCcUserList(listMap.get(UnifyNodeEnum.CC.getCode()));
            tbPatrolStoreRecordVO.setAduitUserList(listMap.get(UnifyNodeEnum.SECOND_NODE.getCode()));
            if (CollectionUtils.isEmpty(tbPatrolStoreRecordVO.getHanderUserList())) {
                tbPatrolStoreRecordVO.setHanderUserList(listMap.get(UnifyNodeEnum.FIRST_NODE.getCode()));
            }
        }
        if(tbPatrolStoreRecordVO.getTaskId() > 0 && taskParentDO != null){
            //流程信息处理
            List<TaskProcessVO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessVO.class);
            // 节点配置信息组装
            for (TaskProcessVO taskProcessVO : process) {
                taskProcessVO.setTaskId(storeRecordDO.getTaskId());
            }
            Map<Long, TaskProcessVO> taskProcessVOMap = unifyTaskService.dealTaskProcess(enterpriseId, process);
            if(MapUtils.isNotEmpty(taskProcessVOMap)){
                TaskProcessVO taskProcessVO = taskProcessVOMap.get(tbPatrolStoreRecordVO.getTaskId());
                tbPatrolStoreRecordVO.setAssignPeopleRang(taskProcessVO);
            }
        }
        HandlerUserVO handlerUserVO = new HandlerUserVO();
        if(StringUtils.isNotBlank(tbPatrolStoreRecordVO.getSupervisorId())){
            String actualHandleUserId = tbPatrolStoreRecordVO.getSupervisorId();
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, actualHandleUserId);
            if(enterpriseUserDO != null){
                handlerUserVO.setUserId(enterpriseUserDO.getUserId());
                handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
                handlerUserVO.setUserName(enterpriseUserDO.getName());
                handlerUserVO.setUserMobile(enterpriseUserDO.getMobile());
                handlerUserVO.setJobnumber(enterpriseUserDO.getJobnumber());
                List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
                if (CollectionUtils.isNotEmpty(sysRoleList)){
                    handlerUserVO.setUserRoles(sysRoleList);
                }
            }
            if(Constants.AI.equals(tbPatrolStoreRecordVO.getSupervisorId())){
                handlerUserVO.setUserId(tbPatrolStoreRecordVO.getSupervisorId());
                handlerUserVO.setUserName(Constants.AI);
            }

        }
        tbPatrolStoreRecordVO.setHandlerUserVO(handlerUserVO);
        return tbPatrolStoreRecordVO;
    }

    @Override
    public PageInfo taskStageRecordList(String enterpriseId, Long unifyTaskId, Long loopCount, Integer pageNum, Integer pageSize, Boolean levelInfo) {
        // 查询巡店检查表记录
        PageHelper.startPage(pageNum, pageSize);
        PatrolStoreStatisticsDataTableQuery query = new PatrolStoreStatisticsDataTableQuery();
        query.setTaskId(unifyTaskId);
        query.setLoopCount(loopCount);
        List<TbPatrolStoreRecordDO> tableRecordDOList =
                tbPatrolStoreRecordMapper.statisticsDataTable(enterpriseId, query, null);
        if (CollectionUtils.isEmpty(tableRecordDOList)) {
            return new PageInfo();
        }
        PageInfo pageInfo = new PageInfo(tableRecordDOList);
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, unifyTaskId);
        pageInfo.setList(statisticsStaTableDataList(enterpriseId, tableRecordDOList, taskParentDO, levelInfo, true, null, null));
        return pageInfo;
    }

    private List<String> getStoreIdByRegion(String enterpriseId, String regionId) {
        // 获取区域路径
        RegionService regionService = SpringContextUtil.getBean("regionServiceImpl", RegionService.class);
        List<RegionPathDTO> regionList = regionService.getRegionPathByList(enterpriseId, Collections.singletonList(regionId));
        RegionPathDTO regionPath = regionList.get(0);
        boolean isRoot = regionPath.getRegionId().equals(Constants.ROOT_REGION_ID);
        // 获取区域下门店
        List<PatrolStoreStatisticsRankDTO> storeList = storeMapper.getStoreByRegionPath(enterpriseId, isRoot, regionPath.getRegionPath());
        List<String> storeIds = storeList.stream().map(PatrolStoreStatisticsRankDTO::getStoreId).collect(Collectors.toList());
//        request.setStoreIdList(storeIds);
        // 获取工单列表
        return storeIds;
    }

    private Map<String, List<PersonDTO>> getTaskPerson(String enterpriseId, Set<Long> taskIdList, List<String> storeIdList, String roleType) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return Maps.newHashMap();
        }
        List<UnifyPersonDTO> unifyPersonList = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, new ArrayList<>(taskIdList), storeIdList, null);
        /*List<UnifyPersonDTO> unifyPersonList = taskMappingMapper.selectPersonInfoByTaskList(enterpriseId, new ArrayList<>(taskIdList), null,
                roleType);*/
        Map<String, List<PersonDTO>> listResultMap = unifyPersonList.stream().filter(e -> !UnifyTaskConstant.ROLE_CREATE.equals(e.getTaskRole()))
                .collect(Collectors.groupingBy(e -> e.getUnifyTaskId() + "#" + e.getStoreId() + "#" + e.getLoopCount(),
                        Collectors.mapping(s -> new PersonDTO(s.getUserId(), s.getUserName(), s.getAvatar()), Collectors.toList())));
        return listResultMap;
    }

    private List<PersonDTO> getTaskPersonList(String enterpriseId, PatrolStoreRecordVO patrolStoreRecordVO, String dbNmae, String nodeNo) {
        DataSourceHelper.changeToSpecificDataSource(dbNmae);
        List<String> userIdList = taskSubMapper.selectUserIdByLoopCount(enterpriseId, patrolStoreRecordVO.getTbPatrolStoreRecordDO().getTaskId(),
                patrolStoreRecordVO.getTbPatrolStoreRecordDO().getStoreId(), nodeNo, patrolStoreRecordVO.getTbPatrolStoreRecordDO().getLoopCount());
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIdList);
        Map<String, PersonDTO> peopleMap = new HashMap<>();
        for (EnterpriseUserDO enterpriseUserDO : userList) {
            peopleMap.put(enterpriseUserDO.getUserId(), new PersonDTO(enterpriseUserDO.getUserId(), enterpriseUserDO.getName(), enterpriseUserDO.getAvatar()));
        }
        List<PersonDTO> list = new ArrayList<>();
        for (String userId : userIdList) {
            if (peopleMap.containsKey(userId)) {
                list.add(peopleMap.get(userId));
            }
        }
        patrolStoreRecordVO.setPersonList(list);
        return list;
    }

    @Override
    public List<PatrolStoreStatisticsMetaStaTableVO> statisticsStaTableDataList(String enterpriseId,
                                                                                List<TbPatrolStoreRecordDO> tableRecordDOList,
                                                                                TaskParentDO taskParentDO, Boolean levelInfo, Boolean isColumn,
                                                                                List<Long> metaTableIds, String businessType) {
        if (CollectionUtils.isEmpty(tableRecordDOList)) {
            return new ArrayList<>();
        }
        if(StringUtils.isBlank(businessType)){
            businessType = PATROL_STORE;
        }
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        Set<Long> regionIdSet = new HashSet<>();
        Set<Long> taskIdSet = new HashSet<>();
        Set<String> userIdSet = new HashSet<>();
        Set<String> storeIdSet = new HashSet<>();
        List<StorePathDTO> storePathDTOList=new ArrayList<>();
        List<Long> idList=new ArrayList<>();
        for (TbPatrolStoreRecordDO recordDO : tableRecordDOList) {
            regionIdSet.add(recordDO.getRegionId());
            taskIdSet.add(recordDO.getTaskId());
            userIdSet.add(recordDO.getCreateUserId());
            storeIdSet.add(recordDO.getStoreId());
            idList.add(recordDO.getId());
            StorePathDTO storePathDTO =new StorePathDTO();
            storePathDTO.setStoreId(recordDO.getStoreId());
            storePathDTO.setRegionPath(recordDO.getRegionWay());
            storePathDTOList.add(storePathDTO);
        }
        List<TbPatrolStoreRecordInfoDO> tbPatrolStoreRecordInfoList = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfoList(enterpriseId, idList);
        Map<Long, TbPatrolStoreRecordInfoDO> tbPatrolStoreRecordInfoMap = ListUtils.emptyIfNull(tbPatrolStoreRecordInfoList)
                .stream()
                .collect(Collectors.toMap(TbPatrolStoreRecordInfoDO::getId, data -> data, (a, b) -> a));

        List<Long> recordIdList = tableRecordDOList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        List<Long> metaTableIdList = new ArrayList<>();
        Map<Long, List<TbDataTableDO>> dataTableMap = Maps.newHashMap();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), businessType);
        if(CollectionUtils.isNotEmpty(dataTableList) && CollectionUtils.isNotEmpty(metaTableIds)){
            dataTableList = dataTableList.stream().filter(dataTable -> metaTableIds.contains(dataTable.getMetaTableId())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
            dataTableMap = dataTableList.stream()
                    .collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));
        }


        Map<Long, TaskParentDO> taskMap = new HashMap<>();
        Map<Long, String> metaTableNameMap = new HashMap<>();
        Map<String, String> userNameMap = new HashMap<>();
        Map<Long, TbMetaTableDO> idMetaTableMap = new HashMap<>();
        if (taskParentDO != null) {
            taskMap.put(taskParentDO.getId(), taskParentDO);
        } else {
            if (CollectionUtils.isNotEmpty(taskIdSet)) {
                List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIdsForMap(enterpriseId, new ArrayList<>(taskIdSet));
                for (TaskParentDO taskParent : parentDOList) {
                    taskMap.put(taskParent.getId(), taskParent);
                }
            }
        }
        Map<Long, BigDecimal> idMetaStaColumnMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(metaTableIdList)) {
            List<TbMetaTableDO> metaTableList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
            //map:tableId -> table
            for (TbMetaTableDO table : metaTableList) {
                idMetaTableMap.put(table.getId(), table);
                metaTableNameMap.put(table.getId(), table.getTableName());
            }

            if (isColumn) {
                List<TbMetaStaTableColumnDO> metaStaColumnList =
                        tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, metaTableIdList,Boolean.FALSE);

                if (CollectionUtils.isNotEmpty(metaStaColumnList)) {
                    idMetaStaColumnMap = metaStaColumnList.stream()
                            .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getSupportScore));
                }
            }

        }

        if (CollectionUtils.isNotEmpty(userIdSet)) {
            List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(userIdSet));

            userNameMap = userList.stream().filter(a -> a.getUserId() != null && a.getName() != null)
                    .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));
        }


        List<RegionDO> regionList = regionMapper.getByIds(enterpriseId, new ArrayList<>(regionIdSet));
        Map<Long, RegionDO> regionIdNameMap = regionList.stream().filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(RegionDO::getId, data -> data, (a, b) -> a));

        List<StoreDO> storeList= storeMapper.getStoreByStoreIdList(enterpriseId, new ArrayList<>(storeIdSet));
        Map<String, StoreDO> storeMap = storeList.stream().filter(a -> a.getStoreId() != null)
                .filter(data-> StringUtils.isNotBlank(data.getStoreNum()))
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));


        // Map:dataTableId->columnCountMap
        Set<Long> businessIds =
                tableRecordDOList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toSet());
        List<PatrolStoreStatisticsDataStaTableDTO> columnCountList =
                tbDataStaTableColumnMapper.statisticsColumnCountByBusinessId(enterpriseId, new ArrayList<>(businessIds));
        Map<Long, PatrolStoreStatisticsDataStaTableDTO> dataTableIdColumnCountMap = columnCountList.stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableDTO::getBusinessId, Function.identity(), (a, b) -> a));
        List<PatrolStoreStatisticsDataStaTableCountDTO>  dataStaTableCountList = tbDataStaTableColumnMapper.statisticsColumnCountByBusinessIdGroupByDataTableId(enterpriseId, new ArrayList<>(businessIds));

        Map<Long, PatrolStoreStatisticsDataStaTableCountDTO> dataTableByEveryColumnCountMap = ListUtils.emptyIfNull(dataStaTableCountList).stream().collect(
                Collectors.toMap(PatrolStoreStatisticsDataStaTableCountDTO::getDataTableId, Function.identity(), (a, b) -> a));


        Map<Long, PatrolStoreStatisticsDataColumnCountDTO> dataDefTableIdColumnCountMap = new HashMap<>();
        Map<Long, List<TbMetaStaColumnVO>> staTableMap = new HashMap<>();
        if (isColumn) {
            //查询记录10条的详情
            List<TbMetaStaColumnVO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.getVOListByRecordIdList(enterpriseId, new ArrayList<>(businessIds), null);

            for (TbMetaStaColumnVO staColumnVO : tbDataStaTableColumnList) {
                staColumnVO.setSupportScore(idMetaStaColumnMap.get(staColumnVO.getMetaColumnId()));
                BigDecimal checkScore =staColumnVO.getCheckScore().multiply(staColumnVO.getScoreTimes()).setScale(2,BigDecimal.ROUND_HALF_UP);
                TbMetaTableDO tbMetaTableDO = idMetaTableMap.get(staColumnVO.getMetaTableId());
                if (tbMetaTableDO!=null&& MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(tbMetaTableDO.getTableProperty())){
                    checkScore = checkScore.multiply(staColumnVO.getWeightPercent().divide(new BigDecimal(Constants.ONE_HUNDRED),2,BigDecimal.ROUND_HALF_UP));
                }
                staColumnVO.setCheckScore(checkScore);
            }
            staTableMap = CollectionUtils.emptyIfNull(tbDataStaTableColumnList).stream()
                    .collect(Collectors.groupingBy(TbMetaStaColumnVO::getBusinessId));
        }

        List<PatrolStoreStatisticsDataColumnCountDTO> columnDefCountList =
                tbDataDefTableColumnMapper.statisticsColumnCountByBusinessIds(enterpriseId, new ArrayList<>(businessIds));
        dataDefTableIdColumnCountMap =
                columnDefCountList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDataColumnCountDTO::getBusinessId,
                        Function.identity(), (a, b) -> a));

        Map<Long, PatrolStoreStatisticsDataColumnCountDTO> dataDefTableEveryColumnCountMap =
                columnDefCountList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDataColumnCountDTO::getDataTableId,
                        Function.identity(), (a, b) -> a));

        // 返回值
        Map<String, String> finalUserNameMap = userNameMap;
        Map<Long, PatrolStoreStatisticsDataColumnCountDTO> finalDataDefTableIdColumnCountMap = dataDefTableIdColumnCountMap;
        Map<Long, List<TbMetaStaColumnVO>> finalStaTableMap = staTableMap;

        Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);
        Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
        return tableRecordDOList.stream().map(a -> {
            boolean isDefine = false;
            List<TbDataTableDO> dataTableDOList = finalDataTableMap.get(a.getId());
            Long metaTableId = 0L;
            if(CollectionUtils.isNotEmpty(dataTableDOList)){
                metaTableId = dataTableDOList.get(0).getMetaTableId();
            }
            TbMetaTableDO tbMetaTableDO = idMetaTableMap.get(metaTableId);
            List<StoreCheckSettingLevelVO> levelList = new ArrayList<>();
            if (tbMetaTableDO != null) {
                isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
                if (levelInfo) {
                    if (StringUtils.isNotBlank(tbMetaTableDO.getLevelInfo())) {
                        JSONObject jsonObject = JSONObject.parseObject(tbMetaTableDO.getLevelInfo());
                        levelList = JSONArray.parseArray(jsonObject.getString("levelList"), StoreCheckSettingLevelVO.class);
                    }
                }
            }
            TaskParentDO parentInfoDO = taskMap.get(a.getTaskId());


            String signInOutStatusStr = "";
            // signInStatus
            String signInStatusStr = null;
            if (a.getSignInStatus() == 1) {
                signInStatusStr = NORMAL;
            }
            if (a.getSignInStatus() == 2) {
                signInStatusStr = ABNORMAL;
            }
            // signEndStatus
            String signOutStatusStr = null;
            if (a.getSignOutStatus() == 1) {
                signOutStatusStr = NORMAL;
            }
            if (a.getSignOutStatus() == 2) {
                signOutStatusStr = ABNORMAL;
            }
            // patrolType
            String patrolType = null;
            if (PATROL_STORE_OFFLINE.getCode().equals(a.getPatrolType())) {
                patrolType = "线下巡店";
            }
            if (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(a.getPatrolType())) {
                patrolType = "视频巡店";
            }
            if (TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(a.getPatrolType())) {
                patrolType = "定时巡检";
            }
            if (TaskTypeEnum.STORE_SELF_CHECK.getCode().equals(a.getPatrolType())) {
                if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(enterpriseConfigDO.getAppType())) {
                    patrolType = "门店自检";
                }else{
                    patrolType = TaskTypeEnum.STORE_SELF_CHECK.getDesc();
                }
            }
            if (TaskTypeEnum.PATROL_STORE_FORM.getCode().equals(a.getPatrolType())) {
                patrolType = TaskTypeEnum.PATROL_STORE_FORM.getDesc();
            }
            if (TaskTypeEnum.PATROL_STORE_AI.getCode().equals(a.getPatrolType())) {
                patrolType = "AI巡检";
            }
            if (TaskTypeEnum.PRODUCT_FEEDBACK.getCode().equals(a.getPatrolType())) {
                patrolType = "货品反馈";
            }
            if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(a.getPatrolType())) {
                patrolType = "食安稽核";
            }
            if (TaskTypeEnum.PATROL_STORE_MYSTERIOUS_GUEST.getCode().equals(a.getPatrolType())) {
                patrolType = "神秘访客任务";
            }

            //巡店时长(规则) 默认为系统设置值 如果签到签退正常，切换为实际巡店时间
            String defaultTime = null;
            String defaultTimeBySeconds = null;
            //签退完成
            if(!Constants.INDEX_ZERO.equals(a.getSignOutStatus())){
                defaultTime = DateUtils.formatBetween(enterpriseStoreCheckSetting.getDefaultCheckTime()*60*1000L);
                defaultTimeBySeconds = String.valueOf(enterpriseStoreCheckSetting.getDefaultCheckTime()*60);
            }
            if (Constants.INDEX_ONE.equals(a.getSignInStatus())&&Constants.INDEX_ONE.equals(a.getSignOutStatus())){
                //实际巡店时长
                defaultTime = DateUtils.formatBetween(a.getSignStartTime(),a.getSignEndTime());
                defaultTimeBySeconds = DateUtils.calculateSecondsBetween(a.getSignStartTime(),a.getSignEndTime());
            }
            String storeAddress = null;
            String storeNum=null;
            if(storeMap.get(a.getStoreId())!=null){
                storeAddress  = storeMap.get(a.getStoreId()).getStoreAddress();
                storeNum= storeMap.get(a.getStoreId()).getStoreNum();
            }
            PatrolStoreStatisticsMetaStaTableVO build =
                    PatrolStoreStatisticsMetaStaTableVO.builder().tableProperty(tbMetaTableDO == null ? 0 : tbMetaTableDO.getTableProperty())
                            .id(a.getId()).taskId(a.getTaskId()).storeId(a.getStoreId()).regionId(a.getRegionId())
                            .tableType(MetaTableConstant.TableTypeConstant.STANDARD).subBeginTime(a.getSubBeginTime()).subEndTime(a.getSubEndTime())
                            .regionName(regionIdNameMap.get(a.getRegionId()).getName()).storeName(a.getStoreName()).storeNum(storeNum)
                            .supervisorName(a.getSupervisorName()).supervisorId(a.getSupervisorId()).recordPatrolType(a.getPatrolType())
                            .createrUserName(finalUserNameMap.get(a.getCreateUserId())).createTime(a.getCreateTime())
                            .metaTableId(metaTableId).score(a.getScore()).status(a.getStatus())
                            .signStartTime(a.getSignStartTime()).signEndTime(a.getSignEndTime()).tourTime(a.getTourTime())
                            .signStartAddress(a.getSignStartAddress()).signEndAddress(a.getSignEndAddress())
                            .summary(a.getSummary()).summaryPicture(a.getSummaryPicture()).supervisorSignature(a.getSupervisorSignature())
                            .signInStatus(a.getSignInStatus()).signOutStatus(a.getSignOutStatus()).patrolType(patrolType).summaryVideo(a.getSummaryVideo())
                            .signInStatusStr(signInStatusStr).signOutStatusStr(signOutStatusStr).signInDate(a.getSignStartTime())
                            .actualPatrolStoreDuration(DateUtils.formatBetween(a.getSignStartTime(),a.getSignEndTime()))
                            .storeAddress(storeAddress).tourTimeStr(defaultTime).totalScore(a.getTaskCalTotalScore())
                            .recheckBusinessId(a.getRecheckBusinessId()).businessCheckType(a.getBusinessCheckType())
                            .tourTimeStrBySeconds(defaultTimeBySeconds).actualPatrolStoreDurationBySeconds(DateUtils.formatBetweenForSeconds(a.getSignStartTime(),a.getSignEndTime()))
                            .recheckUserId(a.getRecheckUserId()).recheckUserName(a.getRecheckUserName()).recheckTime(a.getRecheckTime()).build();

            //多表训内容
            boolean finalIsDefine = isDefine;
            List<PatrolDataTableVO> patrolDataTableVOList = ListUtils.emptyIfNull(dataTableDOList).stream().map(metaTableDO ->{
                PatrolStoreStatisticsDataStaTableCountDTO pts = dataTableByEveryColumnCountMap.getOrDefault(metaTableDO.getId(),new PatrolStoreStatisticsDataStaTableCountDTO());
                BigDecimal checkScore = pts.getCheckScore();
                if (MetaTablePropertyEnum.DEDUCT_SCORE_TABLE.getCode().equals(Integer.valueOf(metaTableDO.getTableProperty()))){
                    checkScore = metaTableDO.getTotalScore().subtract(checkScore);
                }
                BigDecimal percent = null;
                BigDecimal allColumnCheckScorePercent = new BigDecimal(Constants.ZERO_STR);
                if (!finalIsDefine && a.getStatus() == 1) {
                    if (new BigDecimal(Constants.ZERO_STR).compareTo(metaTableDO.getCheckScore()) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(metaTableDO.getTaskCalTotalScore()) != 0) {
                        percent = (metaTableDO.getCheckScore().divide(metaTableDO.getTaskCalTotalScore(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        percent = new BigDecimal(Constants.ZERO_STR);
                    }
                    if (checkScore != null && new BigDecimal(Constants.ZERO_STR).compareTo(checkScore) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(metaTableDO.getTaskCalTotalScore()) != 0) {
                        allColumnCheckScorePercent = (checkScore.divide(metaTableDO.getTaskCalTotalScore(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        allColumnCheckScorePercent = new BigDecimal(Constants.ZERO_STR);
                    }
                }
                return PatrolDataTableVO.builder().metaTableId(metaTableDO.getMetaTableId()).submitStatus(metaTableDO.getSubmitStatus())
                            .metaTableName(metaTableDO.getTableName()).totalColumnCount(getTotalNum(dataDefTableEveryColumnCountMap, dataTableByEveryColumnCountMap, metaTableDO.getId()))
                            .passColumnCount(metaTableDO.getPassNum()).failColumnCount(metaTableDO.getFailNum()).percent(percent).taskCalTotalScore(metaTableDO.getTaskCalTotalScore())
                            .inapplicableColumnCount(metaTableDO.getInapplicableNum()).score(metaTableDO.getCheckScore()).allColumnCheckScore(checkScore).allColumnCheckScorePercent(allColumnCheckScorePercent)
                            .totalScore(metaTableDO.getTotalScore()).checkResult(LevelRuleEnum.getDescription(metaTableDO.getCheckResultLevel()))
                            .rewardPenaltMoney(metaTableDO.getTotalResultAward()).build();
            }).collect(Collectors.toList());


            build.setDataTableVOList(patrolDataTableVOList);


            if(Constants.SYSTEM_USER_ID.equals(a.getCreateUserId()) || Constants.AI_USER_ID.equals(a.getCreateUserId())){
                build.setCreaterUserName(Constants.SYSTEM_USER_NAME);
            }

            build.setMetaTableName(metaTableNameMap.get(metaTableId));
            if(MapUtils.isNotEmpty(fullRegionNameMap)){
                build.setFullRegionName(StringUtils.join(fullRegionNameMap.get(a.getStoreId()), Constants.SPLIT_LINE));
                build.setRegionNameList(fullRegionNameMap.get(a.getStoreId()));
            }
            if (tbMetaTableDO != null) {
                build.setTableProperty(tbMetaTableDO.getTableProperty());
            }
                TbPatrolStoreRecordInfoDO tbPatrolStoreRecordInfoDO = MapUtils.isNotEmpty(tbPatrolStoreRecordInfoMap)?tbPatrolStoreRecordInfoMap.get(a.getId()):null;
                if(tbPatrolStoreRecordInfoDO!=null){
                    String signInWay = tbPatrolStoreRecordInfoDO.getSignInWay();
                    String signOutWay = tbPatrolStoreRecordInfoDO.getSignOutWay();
                    String signInTemp = StringUtils.isBlank(signInWay) ? "-" : tbPatrolStoreRecordInfoDO.getSignInWay();
                    String signOutTemp = StringUtils.isBlank(signOutWay) ? "-" : tbPatrolStoreRecordInfoDO.getSignInWay();
                    build.setSignWay(signInTemp+"/"+signOutTemp);
                    if (a.getSignInStatus() == 1) {
                        build.setSignInRemark(NORMAL);

                    }
                    if (a.getSignInStatus() == 2) {
                        build.setSignInRemark(ABNORMAL+"("+tbPatrolStoreRecordInfoDO.getSignInRemark()+")");
                    }
                    // signEndStatus
                    if (a.getSignOutStatus() == 1) {
                        build.setSignOutRemark(NORMAL);
                    }
                    if (a.getSignOutStatus() == 2) {
                        build.setSignOutRemark(ABNORMAL+"("+tbPatrolStoreRecordInfoDO.getSignOutRemark()+")");
                    }
                    // 签到、签退图片
                    String params = tbPatrolStoreRecordInfoDO.getParams();
                    if (StringUtils.isNotBlank(params)) {
                        JSONObject paramsJson = JSONObject.parseObject(params);
                        build.setSignInImg(paramsJson.getString("signInImg"));
                        build.setSignOutImg(paramsJson.getString("signOutImg"));
                    }
                }else{
                    if (a.getSignInStatus() == 1) {
                        build.setSignInRemark(NORMAL);

                    }
                    if (a.getSignInStatus() == 2) {
                        build.setSignInRemark(ABNORMAL);
                    }
                    // signEndStatus
                    if (a.getSignOutStatus() == 1) {
                        build.setSignOutRemark(NORMAL);
                    }
                    if (a.getSignOutStatus() == 2) {
                        build.setSignOutRemark(ABNORMAL);
                    }
                }

            // TbDataTableDO dataTableDO = finalDataTableMap.get(a.getId());
            if (tbPatrolStoreRecordInfoDO != null) {
                build.setAuditOpinion(tbPatrolStoreRecordInfoDO.getAuditOpinion());
                build.setAuditPicture(tbPatrolStoreRecordInfoDO.getAuditPicture());
                build.setAuditRemark(tbPatrolStoreRecordInfoDO.getAuditRemark());
                build.setAuditTime(tbPatrolStoreRecordInfoDO.getAuditTime());
                build.setAuditUserName(tbPatrolStoreRecordInfoDO.getAuditUserName());
                build.setAuditUserId(tbPatrolStoreRecordInfoDO.getAuditUserId());
            }

            // 有检查项统计数据数据
            PatrolStoreStatisticsDataStaTableDTO statisticsDataStaTableDTO =
                    dataTableIdColumnCountMap.get(a.getId());
            PatrolStoreStatisticsDataColumnCountDTO statisticsDataDefTableDTO =
                    finalDataDefTableIdColumnCountMap.get(a.getId());

            if (statisticsDataStaTableDTO != null) {
                build.setTotalColumnCount(statisticsDataStaTableDTO.getTotalColumnCount());
                build.setPassColumnCount(statisticsDataStaTableDTO.getPassColumnCount());
                build.setFailColumnCount(statisticsDataStaTableDTO.getFailColumnCount());
                build.setInapplicableColumnCount(statisticsDataStaTableDTO.getInapplicableColumnCount());
//                build.setScore(statisticsDataStaTableDTO.getScore());
                if (build.getScore() != null && new BigDecimal(Constants.ZERO_STR).compareTo(build.getScore()) > 0) {
                    build.setScore(new BigDecimal(Constants.ZERO_STR));
                }
                build.setRewardPenaltMoney(statisticsDataStaTableDTO.getRewardPenaltMoney());
            } else if (statisticsDataDefTableDTO != null) {
                build.setTotalColumnCount(statisticsDataDefTableDTO.getTotalColumnCount());
            }

            if (finalStaTableMap.get(a.getId()) != null) {
                build.setStaColumnList(finalStaTableMap.get(a.getId()));
            }
            // taskName
            build.setTaskName(a.getTaskName());
            if (parentInfoDO != null) {
                // taskName
                build.setTaskName(parentInfoDO.getTaskName());
                // taskDesc
                build.setTaskDesc(parentInfoDO.getTaskDesc());
            }

            if (a.getSubBeginTime() != null && a.getSubEndTime() != null) {
                // validTime
                String validTime = DateUtils.convertTimeToString(a.getSubBeginTime().getTime(), DATE_FORMAT_SEC_5) + "-"
                        + DateUtils.convertTimeToString(a.getSubEndTime().getTime(), DATE_FORMAT_SEC_5);
                build.setValidTime(validTime);
            }

            if (!isDefine && a.getStatus() == 1) {
                if (new BigDecimal(Constants.ZERO_STR).compareTo(build.getScore()) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(build.getTotalScore()) != 0) {
                    BigDecimal percent = (build.getScore().divide(build.getTotalScore(), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
                    build.setPercent(percent);
                } else {
                    build.setPercent(new BigDecimal(Constants.ZERO_STR));
                }

            }
            //计算结果项
            if (!isDefine && levelInfo && a.getStatus() == 1) {
                checkResult(build, levelList, tbMetaTableDO);
            } else if (a.getStatus() == 0) {
                build.setCheckResult(null);
            }
            signInOutStatusStr = (StringUtils.isBlank(signInStatusStr) ? "-" : signInStatusStr) + "/" + (StringUtils.isBlank(signOutStatusStr) ? "-" : signOutStatusStr);
            build.setSignInOutStatusStr(signInOutStatusStr);

            // overdue
            String overdue = "";
            if (a.getStatus() == 1 && a.getSignEndTime() != null && a.getSubEndTime() != null) {
                overdue = a.getSignEndTime().after(a.getSubEndTime()) ? "已过期" : "未过期";
            } else if (a.getStatus() != 1) {
                overdue = "";
                build.setPassColumnCount(null);
                build.setFailColumnCount(null);
                build.setInapplicableColumnCount(null);
                build.setScore(null);
                build.setRewardPenaltMoney(null);
                build.setPercent(null);
                build.setTourTime(null);
            }
            build.setOverdue(overdue);
            return build;
        }).collect(Collectors.toList());
    }

    @Override
    public PatrolStoreStatisticsMetaStaColumnVO taskStageRecordDetailList(String enterpriseId, Long businessId, Long metaTableId, Integer pageNum, Integer pageSize) {

        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if (recordDO == null) {
            throw new ServiceException("巡店记录不存在");
        }
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, recordDO.getTaskId());
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(recordDO.getId()), PATROL_STORE);
        List<String> metaTableNameList = dataTableList.stream().map(TbDataTableDO::getTableName).collect(Collectors.toList());
        String metaTableNames = StringUtils.join(metaTableNameList, Constants.PAUSE);
        if(metaTableId == null && CollectionUtils.isNotEmpty(dataTableList)){
            metaTableId = dataTableList.get(0).getMetaTableId();
        }
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
        PatrolStoreStatisticsMetaStaColumnVO vo = new PatrolStoreStatisticsMetaStaColumnVO();
        vo.setId(businessId);
        vo.setStoreId(recordDO.getStoreId());
        vo.setMetaTableId(metaTableId);
        vo.setTableType(MetaTableConstant.TableTypeConstant.STANDARD);
        vo.setSubBeginTime(recordDO.getSubBeginTime());
        vo.setSubEndTime(recordDO.getSubEndTime());
        vo.setStoreName(recordDO.getStoreName());
        vo.setTableProperty(metaTableDO.getTableProperty());
        RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, recordDO.getRegionId());
        if (regionDO != null) {
            vo.setRegionName(regionDO.getName());
        }
        //是否为自定义表
        boolean isDefine = false;
        if (metaTableDO != null) {
            vo.setMetaTableName(metaTableDO.getTableName());
            isDefine = TableTypeUtil.isUserDefinedTable(metaTableDO.getTableProperty(), metaTableDO.getTableType());
        }
        vo.setMetaTableName(metaTableNames);
        vo.setTaskName(taskParentDO.getTaskName());
        vo.setTaskDesc(taskParentDO.getTaskDesc());
        List<TbMetaStaColumnDetailVO> staColumnList = new ArrayList<>();

        PageInfo pageInfo;
        // validTime
        String validTime = DateUtils.convertTimeToString(recordDO.getSubBeginTime().getTime(), DATE_FORMAT_SEC_5) + "-"
                + DateUtils.convertTimeToString(recordDO.getSubEndTime().getTime(), DATE_FORMAT_SEC_5);

        if (!isDefine) {
            PageHelper.startPage(pageNum, pageSize);
            PatrolStoreStatisticsDataTableQuery query = new PatrolStoreStatisticsDataTableQuery();
            query.setMetaTableIds(Collections.singletonList(metaTableId));
            List<TbMetaStaColumnVO> tbDataStaTableColumnList = tbDataStaTableColumnMapper.getVOListByRecordIdList(enterpriseId, Collections.singletonList(businessId), query);

            List<TbMetaStaTableColumnDO> list = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, Collections.singletonList(metaTableId));
            pageInfo = new PageInfo(list);

            if (CollectionUtils.isEmpty(tbDataStaTableColumnList)) {
                pageInfo.setList(tbDataStaTableColumnList);
                vo.setColumnList(pageInfo);
                return vo;
            }
            Set<Long> sopIds = new HashSet<>();
            Boolean accessCoolCollege = false;
            DataSourceHelper.reset();
            EnterpriseSettingDO settingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
            accessCoolCollege = settingDO.getAccessCoolCollege();
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

            for (TbMetaStaTableColumnDO tbMetaStaColumnVO : list) {
                if (tbMetaStaColumnVO.getSopId() != null && tbMetaStaColumnVO.getSopId() > 0) {
                    sopIds.add(tbMetaStaColumnVO.getSopId());
                }
            }
            Map<Long, String> taskSopMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(sopIds)) {
                List<TaskSopVO> taskSopList = taskSopMapper.listByIdList(enterpriseId, new ArrayList<>(sopIds));
                taskSopMap = taskSopList.stream().collect(Collectors.toMap(TaskSopVO::getId, TaskSopVO::getFileName));
            }


            Map<Long, TbMetaStaTableColumnDO> idMetaTableColumnMap = list.stream()
                    .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));

            Map<Long, String> finalTaskSopMap = taskSopMap;
            Boolean finalAccessCoolCollege = accessCoolCollege;
            tbDataStaTableColumnList.forEach(e -> {
                TbMetaStaColumnDetailVO metaStaColumnVO = new TbMetaStaColumnDetailVO();
                metaStaColumnVO.setColumnName(e.getColumnName());
                metaStaColumnVO.setTaskDesc(taskParentDO.getTaskDesc());
                metaStaColumnVO.setStoreName(recordDO.getStoreName());
                metaStaColumnVO.setTaskName(taskParentDO.getTaskName());
                metaStaColumnVO.setCheckResultName(e.getCheckResultName());
                metaStaColumnVO.setStatisticalDimension(e.getStatisticalDimension());
                metaStaColumnVO.setCheckResult(e.getCheckResult());
                metaStaColumnVO.setCheckPics(e.getCheckPics());
                metaStaColumnVO.setCheckVideo(e.getCheckVideo());
                metaStaColumnVO.setCheckText(e.getCheckText());
                metaStaColumnVO.setCheckInfo(e.getCheckInfo());
                metaStaColumnVO.setStoreSceneId(e.getStoreSceneId());
                metaStaColumnVO.setStoreSceneName(e.getStoreSceneName());
                if (regionDO != null) {
                    metaStaColumnVO.setRegionName(regionDO.getName());
                }
                if (metaTableDO != null) {
                    metaStaColumnVO.setMetaTableName(metaTableDO.getTableName());
                    metaStaColumnVO.setTableProperty(metaTableDO.getTableProperty());
                }
                metaStaColumnVO.setStoreId(recordDO.getStoreId());
                metaStaColumnVO.setRegionPath(recordDO.getRegionWay());
                metaStaColumnVO.setSubBeginTime(recordDO.getSubBeginTime());
                metaStaColumnVO.setSubEndTime(recordDO.getSubEndTime());
                TbMetaStaTableColumnDO tableColumnDO = idMetaTableColumnMap.get(e.getMetaColumnId());
                if (tableColumnDO != null) {
                    String coolCourseAndSop = null;
                    metaStaColumnVO.setCoolCourse(tableColumnDO.getCoolCourse());
                    if (tableColumnDO.getSopId() != null) {
                        coolCourseAndSop = finalTaskSopMap.get(tableColumnDO.getSopId());
                    }
                    String course;
                    if (finalAccessCoolCollege) {
                        course = tableColumnDO.getCoolCourse();
                    } else {
                        course = tableColumnDO.getFreeCourse();
                    }

                    if (StringUtils.isNotBlank(course)) {
                        CoolCourseVO coolCourseVO = JSON.parseObject(course, CoolCourseVO.class);
                        if (coolCourseVO != null) {
                            coolCourseAndSop = StringUtils.isNotBlank(coolCourseAndSop) ? coolCourseAndSop + "/" + coolCourseVO.getTitle() : coolCourseVO.getTitle();
                        }
                    }
                    metaStaColumnVO.setCoolCourseAndSop(coolCourseAndSop);
                    metaStaColumnVO.setCategoryName(tableColumnDO.getCategoryName());
                    metaStaColumnVO.setSupportScore(tableColumnDO.getSupportScore());
                    metaStaColumnVO.setPunishMoney(tableColumnDO.getPunishMoney());
                    metaStaColumnVO.setAwardMoney(tableColumnDO.getAwardMoney());
                    metaStaColumnVO.setStandardPic(tableColumnDO.getStandardPic());
                    metaStaColumnVO.setSopId(tableColumnDO.getSopId());
                    metaStaColumnVO.setDescription(tableColumnDO.getDescription());
                    //
                    String checkAwardPunish = "";
                    if (e.getCheckResultId() != null && e.getCheckResultId() != 0){
                        metaStaColumnVO.setAwardPunish(String.format("奖:%s,罚:%s",e.getColumnMaxAward(), e.getColumnMaxAward()));
                        if (PASS.equals(e.getCheckResult())) {
                            checkAwardPunish = String.format("奖:%s", e.getRewardPenaltMoney().multiply(e.getAwardTimes()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        }
                        if (FAIL.equals(e.getCheckResult())) {
                            checkAwardPunish = String.format("罚:%s", e.getRewardPenaltMoney().multiply(e.getAwardTimes()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        }
                    }
                    metaStaColumnVO.setCheckAwardPunish(checkAwardPunish);
                    BigDecimal checkScore = e.getCheckScore().multiply(e.getScoreTimes()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    if (MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(metaTableDO.getTableProperty())){
                        checkScore = checkScore.multiply(e.getWeightPercent().divide(new BigDecimal(Constants.ONE_HUNDRED),2,BigDecimal.ROUND_HALF_UP));
                    }
                    metaStaColumnVO.setCheckScore(checkScore);
                }
                metaStaColumnVO.setValidTime(validTime);
                staColumnList.add(metaStaColumnVO);
            });
        } else {
            PageHelper.startPage(pageNum, pageSize);
            List<TbDataDefTableColumnDO> tbDataStaTableColumnList = tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId, Collections.singletonList(businessId), null);
            pageInfo = new PageInfo(tbDataStaTableColumnList);
            if (CollectionUtils.isEmpty(tbDataStaTableColumnList)) {
                pageInfo.setList(staColumnList);
                vo.setColumnList(pageInfo);
                return vo;
            }
            List<TbMetaDefTableColumnDO> list = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, Collections.singletonList(metaTableId));

            Map<Long, TbMetaDefTableColumnDO> idMetaDefTableColumnMap = list.stream()
                    .collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, Function.identity(), (a, b) -> a));
            tbDataStaTableColumnList.forEach(e -> {
                TbMetaStaColumnDetailVO metaStaColumnVO = new TbMetaStaColumnDetailVO();
                metaStaColumnVO.setRegionPath(e.getRegionPath());
                metaStaColumnVO.setColumnName(e.getMetaColumnName());
                metaStaColumnVO.setTaskDesc(taskParentDO.getTaskDesc());
                metaStaColumnVO.setStoreName(recordDO.getStoreName());
                metaStaColumnVO.setTaskName(taskParentDO.getTaskName());
                metaStaColumnVO.setValue1(e.getValue1());
                metaStaColumnVO.setValue2(e.getValue2());
                metaStaColumnVO.setCheckVideo(e.getCheckVideo());
                TbMetaDefTableColumnDO metaDefTableColumnDO = idMetaDefTableColumnMap.get(e.getMetaColumnId());
                if (metaDefTableColumnDO != null) {
                    metaStaColumnVO.setFormat(metaDefTableColumnDO.getFormat());
                }
                if (regionDO != null) {
                    metaStaColumnVO.setRegionName(regionDO.getName());
                }
                if (metaTableDO != null) {
                    metaStaColumnVO.setMetaTableName(metaTableDO.getTableName());
                }
                metaStaColumnVO.setDescription(e.getDescription());
                metaStaColumnVO.setStoreId(recordDO.getStoreId());
                metaStaColumnVO.setSubBeginTime(recordDO.getSubBeginTime());
                metaStaColumnVO.setSubEndTime(recordDO.getSubEndTime());
                metaStaColumnVO.setValidTime(validTime);
                staColumnList.add(metaStaColumnVO);
            });
        }
        pageInfo.setList(staColumnList);
        vo.setColumnList(pageInfo);
        return vo;
    }

    private void checkResult(PatrolStoreStatisticsMetaStaTableVO patrolStoreStatisticsMetaStaTableVO,
                             List<StoreCheckSettingLevelVO> levelList, TbMetaTableDO tableDO) {
        if (CollectionUtils.isEmpty(levelList)) {
            return;
        }
        if (LevelRuleEnum.SCORING_RATE.getCode().equals(tableDO.getLevelRule())) {
            levelList.sort(Comparator.comparingInt(StoreCheckSettingLevelVO::getPercent).reversed());
            for (StoreCheckSettingLevelVO levelVO : levelList) {
                if (patrolStoreStatisticsMetaStaTableVO.getPercent().intValue() >= levelVO.getPercent()) {
                    patrolStoreStatisticsMetaStaTableVO.setCheckResult(levelVO.getKeyName());
                    return;
                }
            }
        } else {
            levelList.sort(Comparator.comparingInt(StoreCheckSettingLevelVO::getQualifiedNum).reversed());
            for (StoreCheckSettingLevelVO levelVO : levelList) {
                if (patrolStoreStatisticsMetaStaTableVO.getPassColumnCount().intValue() >= levelVO.getQualifiedNum()) {
                    patrolStoreStatisticsMetaStaTableVO.setCheckResult(levelVO.getKeyName());
                    return;
                }
            }
        }

    }

    @Override
    public void countScore(String eid, TbPatrolStoreRecordDO recordDO, TbMetaTableDO tbMetaTable, Long dataTableId) {
        List<TbDataStaTableColumnDO> staColumnList = new ArrayList<>();
        //计算奖罚得分
        List<TbDataStaTableColumnDO> dataStaTableColumnList = tbDataStaTableColumnMapper.selectByDataTableId(eid, dataTableId);
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByMetaTableId(eid, tbMetaTable.getId());
        Map<Long, TbMetaColumnResultDO> columnIdResultMap = columnResultDOList.stream().collect(Collectors.toMap(TbMetaColumnResultDO::getId, Function.identity(), (a, b) -> a));
        List<TbMetaStaTableColumnDO> list = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(eid, Collections.singletonList(tbMetaTable.getId()));

        Map<Long, TbMetaStaTableColumnDO> idMetaTableColumnMap = list.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
        //每一项最高奖金
        Map<Long, BigDecimal> columnMaxAwardMap = AbstractColumnObserver.getColumnMaxAwardMap(tbMetaTable, columnResultDOList);
        AtomicInteger failNum = new AtomicInteger();
        AtomicInteger passNum = new AtomicInteger();
        AtomicInteger inapplicableNum = new AtomicInteger();
        for(TbDataStaTableColumnDO a : dataStaTableColumnList) {
            TbDataStaTableColumnDO tableColumnDO = TbDataStaTableColumnDO.builder().id(a.getId()).build();
            TbMetaStaTableColumnDO tbMetaStaTableColumnDO = idMetaTableColumnMap.get(a.getMetaColumnId());
            // 修改标准检查项
            if (a.getCheckResultId() != null && a.getCheckResultId() > 0) {
                TbMetaColumnResultDO tbMetaColumnResultDO = columnIdResultMap.get(a.getCheckResultId());
                if (tbMetaColumnResultDO != null) {
                    tableColumnDO.setRewardPenaltMoney(tbMetaColumnResultDO.getMoney());
                }
            } else {
                if (tbMetaStaTableColumnDO != null) {
                    if (PASS.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getAwardMoney());
                    }
                    if (FAIL.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getPunishMoney().abs().multiply(new BigDecimal("-1")));
                    }
                }
            }
            BigDecimal columnMaxAward = new BigDecimal(Constants.ZERO_STR);
            if (columnMaxAwardMap.get(a.getMetaColumnId()) != null) {
                columnMaxAward = columnMaxAwardMap.get(a.getMetaColumnId());
            }
            if (tbMetaStaTableColumnDO != null) {
                tableColumnDO.setWeightPercent(tbMetaStaTableColumnDO.getWeightPercent());
                tableColumnDO.setColumnMaxScore(tbMetaStaTableColumnDO.getSupportScore());
                tableColumnDO.setColumnMaxAward(columnMaxAward);
            }
            //获取检查结果
            String checkResult = a.getCheckResult();
            //采集项不参与计算
            if (StringUtils.isNotBlank(checkResult) && tbMetaStaTableColumnDO != null &&
                    !MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(tbMetaStaTableColumnDO.getColumnType())) {
                //计算合格项数
                switch (checkResult) {
                    case PASS:
                        passNum.getAndIncrement();
                        break;
                    case FAIL:
                        failNum.getAndIncrement();
                        break;
                    case INAPPLICABLE:
                        inapplicableNum.getAndIncrement();
                        break;
                    default:
                }
            }
            //用于计算
            a.setWeightPercent(tableColumnDO.getWeightPercent());
            a.setColumnMaxScore(tableColumnDO.getColumnMaxScore());
            a.setColumnMaxAward(tableColumnDO.getColumnMaxAward());
            if(tableColumnDO.getRewardPenaltMoney() != null){
                a.setRewardPenaltMoney(tableColumnDO.getRewardPenaltMoney());
            }
            staColumnList.add(tableColumnDO);
        }
        tbDataStaTableColumnMapper.batchUpdate(eid, recordDO.getId(), PATROL_STORE,
                dataTableId, staColumnList, true);
        //计算得分
        List<CalColumnScoreDTO> calColumnScoreList = buildCalColumnStore(dataStaTableColumnList, idMetaTableColumnMap);
        CalTableResultDTO checkResult = AbstractColumnObserver.getSingleTableResult(new CalTableScoreDTO(dataTableId, tbMetaTable, calColumnScoreList));
        //计算得分项数--

        TbDataTableDO dataTableDO = new TbDataTableDO();
        dataTableDO.setId(dataTableId);
        dataTableDO.setTaskCalTotalScore(checkResult.getCalTotalScore());
        dataTableDO.setTotalScore(tbMetaTable.getTotalScore());
        dataTableDO.setCheckScore(checkResult.getResultScore());
        dataTableDO.setTotalResultAward(checkResult.getResultAward());
        dataTableDO.setNoApplicableRule(tbMetaTable.getNoApplicableRule());
        dataTableDO.setFailNum(failNum.get());
        dataTableDO.setPassNum(passNum.get());
        dataTableDO.setInapplicableNum(inapplicableNum.get());
        dataTableDO.setTotalCalColumnNum(checkResult.getTotalCalColumnNum());
        dataTableDO.setCollectColumnNum(checkResult.getCollectColumnNum());
        dataTableDO.setCheckResultLevel(getCheckResultLevel(passNum.get(), tbMetaTable, checkResult.getResultScore(), checkResult.getCalTotalScore()));
        tbDataTableMapper.updateByPrimaryKeySelective(dataTableDO, eid);
    }

    /**
     * 构建项分数信息
     * @param dataStaTableColumnList
     * @param metaTableColumnMap
     * @return
     */
    @Override
    public List<CalColumnScoreDTO> buildCalColumnStore(List<TbDataStaTableColumnDO> dataStaTableColumnList, Map<Long, TbMetaStaTableColumnDO> metaTableColumnMap){
        if(CollectionUtils.isEmpty(dataStaTableColumnList)){
            return null;
        }
        List<CalColumnScoreDTO> resultList = new ArrayList<>();
        for (TbDataStaTableColumnDO dataColumn : dataStaTableColumnList) {
            TbMetaStaTableColumnDO tbMetaStaTableColumn = metaTableColumnMap.get(dataColumn.getMetaColumnId());
            if(Objects.isNull(tbMetaStaTableColumn)){
                continue;
            }
            CalColumnScoreDTO calColumnScore = CalColumnScoreDTO.builder().score(dataColumn.getCheckScore()).columnName(dataColumn.getMetaColumnName())
                    .scoreTimes(dataColumn.getScoreTimes()).awardTimes(dataColumn.getAwardTimes()).weightPercent(dataColumn.getWeightPercent())
                    .columnTypeEnum(MetaColumnTypeEnum.getColumnType(tbMetaStaTableColumn.getColumnType())).categoryName(tbMetaStaTableColumn.getCategoryName())
                    .checkResult(CheckResultEnum.getCheckResultEnum(dataColumn.getCheckResult())).columnMaxScore(dataColumn.getColumnMaxScore())
                    .rewardPenaltMoney(dataColumn.getRewardPenaltMoney()).build();
            resultList.add(calColumnScore);
        }
        return resultList;
    }

    @Override
    public void turnPatrolStoreRecord(String enterpriseId, PatrolStoreRecordTurnRequest recordTurnRequest, CurrentUser user) {
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, recordTurnRequest.getBusinessId());
        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, recordDO.getTaskId(), recordDO.getStoreId(), recordDO.getLoopCount(), user.getUserId(), UnifyStatus.ONGOING.getCode(), null);
        if (taskSubVO == null) {
            log.info("没有需要转交的子任务，businessId:{} ", recordTurnRequest.getBusinessId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "没有需要转交的子任务");
        }
        Long subTaskId = taskSubVO.getSubTaskId();
        UnifyTaskTurnDTO task = new UnifyTaskTurnDTO();
        task.setSubTaskId(subTaskId);
        task.setTurnUserId(recordTurnRequest.getTurnUserId());
        if (StringUtils.isNotBlank(recordTurnRequest.getRemark())){
            task.setRemark(recordTurnRequest.getRemark());
        }
        unifyTaskService.turnTask(enterpriseId, task, UserHolder.getUser());
    }

    /**
     * 巡店记录审核
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void audit(String enterpriseId, CurrentUser user, PatrolStoreAuditParam patrolStoreAuditParam) {

        String actionKey = patrolStoreAuditParam.getActionKey();
        if (StringUtils.isNotBlank(actionKey) && !PatrolStoreConstant.ActionKeyConstant.ACTION_KEY_SET.contains(actionKey)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "审核类型参数有误");
        }
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, patrolStoreAuditParam.getBusinessId());
        if (recordDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "巡店记录不存在");
        }
        TaskSubVO taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, recordDO.getTaskId(), recordDO.getStoreId(), recordDO.getLoopCount(), user.getUserId(), UnifyStatus.ONGOING.getCode(), UnifyNodeEnum.SECOND_NODE.getCode());
        if (taskSubVO == null) {
            taskSubVO = taskSubMapper.getLatestSubId(enterpriseId, recordDO.getTaskId(), recordDO.getStoreId(), recordDO.getLoopCount(), null, UnifyStatus.ONGOING.getCode(), UnifyNodeEnum.SECOND_NODE.getCode());
        }
        if (taskSubVO == null) {
            log.info("巡店记录对应的审批任务不存在，不能进行审核，businessId:{} ", patrolStoreAuditParam.getBusinessId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "巡店记录对应的审批任务不存在，不能进行审核");
        }
        if(Objects.nonNull(taskSubVO.getStatusType()) && Constants.INDEX_ZERO.equals(taskSubVO.getStatusType())){
            log.info("任务已停止");
            throw new ServiceException(ErrorCodeEnum.TASK_IS_STOP);
        }
        Long subTaskId = taskSubVO.getSubTaskId();
        //审批任务
        TaskSubDO taskSub = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);

        //只能审核自己的任务
        /*if (!user.getUserId().equals(taskSub.getHandleUserId())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "只能审核自己的审批任务");
        }*/

        Boolean flag = workflowService.subSubmitCheck(taskSub);
        if (flag == null || !flag) {
            log.info("该记录已被其他人操作，businessId:" + patrolStoreAuditParam.getBusinessId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录已被其他人操作");
        }

        // 发送流程引擎
        String bizCode = PatrolStoreConstant.BizCodeConstant.PATROLSTORE_APPROVE;
        String redisKeyPrefix = PatrolStoreConstant.PATROL_STORE_APPROVE;

        String taskKey = redisKeyPrefix + "_" + enterpriseId + "_" + taskSub.getUnifyTaskId() + "_" + taskSub.getStoreId() + "_" + taskSub.getLoopCount();
        //加两分钟防止重复审核
        if (StringUtils.isNotBlank(redisUtilPool.getString(taskKey))) {
            log.info("该记录已被其他人操作，businessId:" + patrolStoreAuditParam.getBusinessId());
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该记录已被其他人操作");
        }
        redisUtilPool.setString(taskKey, user.getUserId(), 10);
        // 新增审核记录
        tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, TbPatrolStoreHistoryDo.builder().createTime(new Date())
                .updateTime(new Date()).actionKey(patrolStoreAuditParam.getActionKey()).businessId(patrolStoreAuditParam.getBusinessId())
                .deleted(false).nodeNo(UnifyNodeEnum.SECOND_NODE.getCode()).operateType(PatrolStoreConstant.PatrolStoreOperateTypeConstant.APPROVE)
                .operateUserName(user.getName()).operateUserId(user.getUserId()).subTaskId(subTaskId).photo(patrolStoreAuditParam.getAuditPicture()).remark(patrolStoreAuditParam.getAuditRemark()).build());

        if (CollectionUtils.isNotEmpty(patrolStoreAuditParam.getSubmitParamList())){
            log.info("审批时修改检查项检查结果submitParamList:{}", JSONObject.toJSONString(patrolStoreAuditParam.getSubmitParamList()));
            patrolStoreAuditParam.getSubmitParamList().forEach(param -> {
                param.setSubmit(true);
                this.submit(enterpriseId, param, user.getUserId());
            });
        }
        // 审核通过
        if (PatrolStoreConstant.ActionKeyConstant.PASS.equals(actionKey)) {
            // 修改巡店记录状态、及相关datatable状态
            completePotral(enterpriseId, patrolStoreAuditParam.getBusinessId(), user.getUserId(), user.getName(), recordDO.getSubTaskId());
        } else if (PatrolStoreConstant.ActionKeyConstant.REJECT.equals(actionKey)) {
            // 修改巡店记录
            tbPatrolStoreRecordMapper.updateById(enterpriseId,
                    TbPatrolStoreRecordDO.builder().id(patrolStoreAuditParam.getBusinessId()).status(0).build());
        }
        // 修改审批人信息
        tbDataTableMapper.updateAuditInfo(enterpriseId, patrolStoreAuditParam.getBusinessId(), PATROL_STORE, user.getUserId()
                , user.getName(), patrolStoreAuditParam.getAuditPicture(), patrolStoreAuditParam.getActionKey(), patrolStoreAuditParam.getAuditRemark());

        tbPatrolStoreRecordInfoMapper.updateAuditInfo(enterpriseId, patrolStoreAuditParam.getBusinessId(), user.getUserId()
                , user.getName(), patrolStoreAuditParam.getAuditPicture(), patrolStoreAuditParam.getActionKey(), patrolStoreAuditParam.getAuditRemark());

        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(taskSub.getUnifyTaskId(), taskSub.getStoreId(), taskSub.getNodeNo(),
                taskSub.getGroupItem(), taskSub.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder()
                .subStatus(UnifyStatus.COMPLETE.getCode())
                .build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, taskSub.getId());
        //工作流引擎
        WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, patrolStoreAuditParam.getBusinessId(), taskSub, bizCode,
                actionKey, null, user.getUserId(), patrolStoreAuditParam.getAuditRemark(), null, null);
        mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), taskSub.getId(), JSONObject.toJSONString(workflowDataDTO));
        simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void overPatrol(String enterpriseId, Long businessId, String userId, String userName, String dingCorpId, Boolean isSignOut,String appType, String signatureUser,EnterpriseStoreCheckSettingDO storeCheckSettingDO, EnterpriseSettingDO enterpriseSettingDO) {

        boolean approve = false;

        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);

        if (tbPatrolStoreRecordDO.getStatus() != 0) {
            return;
        }

        if (tbPatrolStoreRecordDO.getSignInStatus() == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店未签到，无法提交");
        }
        if (tbPatrolStoreRecordDO.getSignOutStatus() == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店未签退，无法提交");
        }
        String businessType = tbPatrolStoreRecordDO.getBusinessCheckType();
        if(StringUtils.isBlank(businessType)){
            businessType = PATROL_STORE;
        }
        boolean aiAudit = false;
        TaskParentDO taskParentDO = null;
        if (tbPatrolStoreRecordDO.getTaskId() != 0) {
            taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
            //流程信息处理
            List<TaskProcessDTO> process = JSONArray.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
            // 节点配置信息组装
            Map<String, String> nodeMap = ListUtils.emptyIfNull(process).stream()
                    .filter(a -> a.getNodeNo() != null && a.getApproveType() != null)
                    .collect(Collectors.toMap(TaskProcessDTO::getNodeNo, TaskProcessDTO::getApproveType, (a, b) -> a));

            String approveUser = nodeMap.get(UnifyNodeEnum.SECOND_NODE.getCode());
            // 判断是否有审核节点，没有审核通过流程直接结束
            if (StringUtils.isNotBlank(approveUser)) {
                approve = Boolean.TRUE;
            }
            aiAudit = taskParentDO.getAiAudit()  == null ? false : taskParentDO.getAiAudit();
        }
        //签退时，如果允许先签后提交，或者需要审核，则不结束巡店
        if (isSignOut && approve) {
            return;
        }
        //判断为自主巡店，如果结束巡店，删除巡检中的检查表
        if (tbPatrolStoreRecordDO.getTaskId() == 0) {
            log.info("自主巡店工作通知参数，enterpriseId：{}，tbPatrolStoreRecordDO：{}，storeCheckSettingDO：{}",enterpriseId,JSONObject.toJSONString(tbPatrolStoreRecordDO),JSONObject.toJSONString(storeCheckSettingDO));
            sendMessageBySelfPatrol(enterpriseId,tbPatrolStoreRecordDO,storeCheckSettingDO);
            List<TbDataTableDO> dataTableDOList = tbDataTableMapper.selectByBusinessId(enterpriseId, tbPatrolStoreRecordDO.getId(), businessType);
            int beforeSize = dataTableDOList.size();
            dataTableDOList = dataTableDOList.stream().filter(dataTable -> (dataTable.getSubmitStatus() & 1) == 0).collect(Collectors.toList());
            int afterSize = dataTableDOList.size();
            //表单全部未提交
            if (beforeSize == afterSize) {
                throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店存在未提交检查表，无法提交巡店");
            }
            if (CollectionUtils.isNotEmpty(dataTableDOList)) {
                // 移除
                List<Long> rmMetaTableIds =
                        dataTableDOList.stream().map(TbDataTableDO::getMetaTableId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(rmMetaTableIds)) {
                    rmMetaTable(enterpriseId, tbPatrolStoreRecordDO.getId(), rmMetaTableIds);
                }
                tbPatrolStoreRecordMapper.updateSubmitStatus(enterpriseId, tbPatrolStoreRecordDO.getId(), Constants.INDEX_ONE | tbPatrolStoreRecordDO.getSubmitStatus());
                tbPatrolStoreRecordDO.setSubmitStatus(Constants.INDEX_ONE | tbPatrolStoreRecordDO.getSubmitStatus());

            }
        }
        //判定为视频（线上）巡店，则查找抄送规则
        if (TaskTypeEnum.PATROL_STORE_ONLINE.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())){
            log.info("视频巡店工作通知参数，enterpriseId：{}，tbPatrolStoreRecordDO：{}，storeCheckSettingDO：{}",enterpriseId,JSONObject.toJSONString(tbPatrolStoreRecordDO),JSONObject.toJSONString(storeCheckSettingDO));
            sendMessageByVideoPatrol(enterpriseId,tbPatrolStoreRecordDO,storeCheckSettingDO);
        }

        List<UnifyFormDataDTO> formDataList = taskMappingMapper.selectMappingDataByTaskId(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
        List<Long> metaTableIdList = ListUtils.emptyIfNull(formDataList).stream()
                .map(dto -> Long.parseLong(dto.getOriginMappingId()))
                .collect(Collectors.toList());

        // 全为AI检查项的，无需校验提交状态
        boolean noNeedVerify =  tbMetaStaTableColumnMapper.isAllAiCheckColumnByMetaTableId(enterpriseId, tbPatrolStoreRecordDO.getMetaTableId());
        //自主巡店在完成巡店中校验表单提交
        if(!noNeedVerify || aiAudit){
            //表单提交
            checkSubmitStatus(enterpriseId, businessId, tbPatrolStoreRecordDO, isSignOut);
        }
        // 结束巡店时，生成稽核流程表
        if (TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            scSafetyCheckFlowService.generateSafetyCheckFlowData(enterpriseId, UnifyNodeEnum.FIRST_NODE.getCode(), signatureUser, tbPatrolStoreRecordDO, dingCorpId, appType);
            approve = Boolean.TRUE;
            ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowService.getByBusinessId(enterpriseId, businessId);
            // 如果是第二轮完成巡检，设置处理人及修改时间
            if(safetyCheckFlowDO.getCycleCount() > 0){
                tbDataStaTableColumnMapper.updateHandlerUserIdByBusinessId(enterpriseId, businessId, businessType, userId, new Date());
            }
            // 完成巡店 插入历史记录
            scSafetyCheckFlowService.buildColumnCheckHistory(enterpriseId, businessId, null, userId);

        }
        // 修改任务完成状态
        Long subTaskId = 0L;
        if(taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())){
            subTaskId = tbPatrolStoreRecordDO.getSubTaskId();
        }else if (tbPatrolStoreRecordDO.getTaskId() != 0) {
            TaskSubVO taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                    userId, UnifyStatus.ONGOING.getCode(), UnifyNodeEnum.FIRST_NODE.getCode());
            //非处理人处理任务，随机取一条任务
            if(taskSub == null){
                taskSub = taskSubMapper.getLatestSubId(enterpriseId, tbPatrolStoreRecordDO.getTaskId(), tbPatrolStoreRecordDO.getStoreId(), tbPatrolStoreRecordDO.getLoopCount(),
                        null, UnifyStatus.ONGOING.getCode(), UnifyNodeEnum.FIRST_NODE.getCode());
            }
            if (taskSub != null) {
                subTaskId = taskSub.getSubTaskId();
            }
        }
        //查询检查表是否包含AI项
        Integer count = 0;
        if(CollectionUtils.isNotEmpty(metaTableIdList)){
            count = tbMetaStaTableColumnMapper.aiCheckColumnCountByMetaTableIdList(enterpriseId, metaTableIdList);
        }

        int status = 1;
        if (approve || aiAudit && count > 0) {
            status = 2;
        }

        if(BusinessCheckType.PATROL_RECHECK.getCode().equals(tbPatrolStoreRecordDO.getBusinessCheckType())){
            // 修改复审巡店记录
            tbPatrolStoreRecordMapper.updateById(enterpriseId,
                    TbPatrolStoreRecordDO.builder().id(businessId).recheckUserId(userId).recheckUserName(userName).status(status).recheckTime(new Date()).build());
        }else {
            // 修改巡店记录
            tbPatrolStoreRecordMapper.updateById(enterpriseId,
                    TbPatrolStoreRecordDO.builder().id(businessId).supervisorId(userId).supervisorName(userName).status(status).subTaskId(subTaskId).build());
        }
        String oldSubTaskNodeNo = UnifyNodeEnum.FIRST_NODE.getCode();
        if (status == 1) {
            oldSubTaskNodeNo = handleFinishPatrolTask(enterpriseId, businessId, userId, userName,
                    subTaskId, taskParentDO, tbPatrolStoreRecordDO, dingCorpId, appType);
        }

        //结束巡店的时候添加修改钉钉端待办状态 处理人
        if (taskParentDO != null && !TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            sendUpcomingFinish(dingCorpId, enterpriseId, subTaskId, appType, oldSubTaskNodeNo);

            TaskSubDO oldSub = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
            if(oldSub != null){
                CombineUpcomingCancelData combineUpcomingCancelData = new CombineUpcomingCancelData();
                combineUpcomingCancelData.setEnterpriseId(enterpriseId);
                combineUpcomingCancelData.setDingCorpId(dingCorpId);
                combineUpcomingCancelData.setAppType(appType);
                combineUpcomingCancelData.setUnifyTaskId(taskParentDO.getId());
                combineUpcomingCancelData.setLoopCount(oldSub.getLoopCount());
                combineUpcomingCancelData.setHandleUserId(userId);
                simpleMessageService.send(JSONObject.toJSONString(combineUpcomingCancelData), RocketMqTagEnum.COMBINE_UPCOMING_CANCEL_QUEUE, System.currentTimeMillis() + 10000);
            }
        }


        log.info("overPotral.sendUpcomingFinish");
        if(TaskTypeEnum.PATROL_STORE_AI.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())){
            userName = Constants.AI;
            userId = Constants.AI;
        }

        //插入处理记录
        //插入巡店历史操作表数据
        tbPatrolStoreHistoryMapper.insertPatrolStoreHistory(enterpriseId, TbPatrolStoreHistoryDo.builder().createTime(new Date()).updateTime(new Date()).actionKey("")
                .businessId(businessId).deleted(false).nodeNo(UnifyNodeEnum.FIRST_NODE.getCode()).operateType(PatrolStoreConstant.PatrolStoreOperateTypeConstant.HANDLE)
                .operateUserName(userName).operateUserId(userId).subTaskId(tbPatrolStoreRecordDO.getSubTaskId()).photo("").remark(null).build());


            // 线下任务巡店 开启AI审批,且存在AI项
        if (needDealOfflineAiAudit(tbPatrolStoreRecordDO, aiAudit, enterpriseSettingDO)  && count > 0) {
            log.info("线下任务开启AI审批, aiAudit :{}, count:{}", aiAudit, count);
            patrolStoreAiAuditService.processOfflineAiAudit(enterpriseId, businessId, tbPatrolStoreRecordDO, businessType,
                    subTaskId, metaTableIdList, enterpriseSettingDO, userId, approve,
                    userName, dingCorpId, appType, taskParentDO);
            return;
        }

        if(status == 1){
            log.info("已完成不需要审批");
            return;
        }

        // 需要人工审核
        if (approve && !TaskTypeEnum.PATROL_STORE_SAFETY_CHECK.getCode().equals(tbPatrolStoreRecordDO.getPatrolType())) {
            TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(enterpriseId, subTaskId);
            if (taskSubDO == null) {
                return;
            }
            updateSubTaskStatus(enterpriseId, taskSubDO, userId);
            //发送消息处理
            WorkflowDataDTO workflowDataDTO = workflowService.getFlowJsonObject(enterpriseId, taskSubDO.getId(), taskSubDO,
                    TbDisplayConstant.BizCodeConstant.DISPLAY_HANDLE, TbDisplayConstant.ActionKeyConstant.PASS, null,
                    userId, null, null, null);
            mqMessageDAO.addMessage(enterpriseId, workflowDataDTO.getPrimaryKey(), taskSubDO.getId(), JSONObject.toJSONString(workflowDataDTO));
            simpleMessageService.send(JSONObject.toJSONString(workflowDataDTO), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);

        }
    }

    private boolean needDealOfflineAiAudit(TbPatrolStoreRecordDO recordDO, boolean aiAudit, EnterpriseSettingDO enterpriseSettingDO) {
        AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
        log.info("needDealOfflineAiAudit开启线下巡店AI,aiConfigDTO：{}", JSONObject.toJSONString(aiConfigDTO));
        return recordDO.getTaskId() != 0
                && TaskTypeEnum.PATROL_STORE_OFFLINE.getCode().equals(recordDO.getPatrolType())
                && aiAudit && aiConfigDTO != null && aiConfigDTO.aiEnable(AIBusinessModuleEnum.PATROL_STORE_OFFLINE);
    }





    private String handleFinishPatrolTask(String enterpriseId, Long businessId, String userId, String userName,
                                        Long subTaskId, TaskParentDO taskParentDO, TbPatrolStoreRecordDO tbPatrolStoreRecordDO,
                                        String dingCorpId, String appType) {
        String oldSubTaskNodeNo = null;
        // 完成巡店任务
        completePotral(enterpriseId, businessId, userId, userName, subTaskId);
        // 修改任务完成状态
        if (Objects.nonNull(subTaskId) && subTaskId != 0 && taskParentDO != null
                && !TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            oldSubTaskNodeNo = unifyTaskService.completeSubTask(enterpriseId, subTaskId, null, null);
        }
        // 计划巡店
        if (taskParentDO != null && TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskParentDO.getTaskType())) {
            unifyTaskPersonService.updateTaskPersonWhenCompletePotral(enterpriseId, tbPatrolStoreRecordDO, dingCorpId, appType);
        }
        // 行事历
        tbPatrolPlanDetailDao.updateFinishTimeAndStatus(enterpriseId, businessId);
        return  oldSubTaskNodeNo;
    }

    private void updateAuditInfoForAi(String enterpriseId, Long businessId) {
        // 修改审批人信息
        tbDataTableMapper.updateAuditInfo(enterpriseId, businessId, PATROL_STORE, Constants.AI, Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");
        tbPatrolStoreRecordInfoMapper.updateAuditInfo(enterpriseId, businessId, Constants.AI, Constants.AI, "", PatrolStoreConstant.ActionKeyConstant.PASS, "");
    }

    private void updateSubTaskStatus(String enterpriseId, TaskSubDO taskSubDO, String userId) {
        //同一批次同一节点的同一的必是已完成
        TaskSubDO queryDO = new TaskSubDO(taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getNodeNo(),
                taskSubDO.getGroupItem(), taskSubDO.getLoopCount());
        TaskSubDO updateDO = TaskSubDO.builder().subStatus(UnifyStatus.COMPLETE.getCode()).build();
        taskSubMapper.updateSubDetailExclude(enterpriseId, queryDO, updateDO, taskSubDO.getId());
        //记录实际处理人
        if (UnifyNodeEnum.FIRST_NODE.getCode().equals(taskSubDO.getNodeNo())) {
            TaskStoreDO taskStoreDO = taskStoreMapper.getTaskStore(enterpriseId, taskSubDO.getUnifyTaskId(), taskSubDO.getStoreId(), taskSubDO.getLoopCount());
            taskStoreMapper.updatedHandlerUserByTaskStoreId(enterpriseId, taskStoreDO.getId(), userId);
        }
    }




    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completePotral(String enterpriseId, Long businessId, String userId, String userName, Long subTaskId) {
        TbPatrolStoreRecordDO patrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        String businessType = patrolStoreRecordDO.getBusinessCheckType();
        if(StringUtils.isBlank(businessType)){
            businessType = PATROL_STORE;
        }

        // 修改巡店记录
        tbPatrolStoreRecordMapper.updateById(enterpriseId,
                TbPatrolStoreRecordDO.builder().id(businessId).status(1).build());
        String supervisorId = patrolStoreRecordDO.getSupervisorId() == null ? "" : patrolStoreRecordDO.getSupervisorId();
        // 修改数据检查标准项任务状态
        tbDataStaTableColumnMapper.updateBusinessStatus(enterpriseId, businessId, patrolStoreRecordDO.getSubTaskId(), businessType, supervisorId);
        // 修改数据检查自定义项任务状态
        tbDataDefTableColumnMapper.updateBusinessStatus(enterpriseId, businessId, patrolStoreRecordDO.getSubTaskId(), businessType, supervisorId);
        // 修改数据检查表任务状态
        tbDataTableMapper.updateBusinessStatus(enterpriseId, businessId, patrolStoreRecordDO.getSubTaskId(), businessType, supervisorId);

        //异步处理数据
        try {
            //计算得分 发送消息异步 延迟1秒
            simpleMessageService.send(JSONObject.toJSONString(new PatrolStoreScoreMsgDTO(enterpriseId, businessId, subTaskId, supervisorId, userName, false)),
                    RocketMqTagEnum.PATROL_STORE_SCORE_COUNT_QUEUE, System.currentTimeMillis() + 1000);
        } catch (Exception e) {
            log.error("发送计算得分消息失败 businessId:{}", businessId, e);
        }
    }

    @Override
    public List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryList(String enterpriseId, String businessId) {
        List<TbPatrolStoreHistoryDo> TbPatrolStoreHistoryList = tbPatrolStoreHistoryMapper.selectPatrolStoreHistoryList(enterpriseId, businessId);
        //获取巡店记录
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, Long.valueOf(businessId));
        if (tbPatrolStoreRecordDO != null && tbPatrolStoreRecordDO.getTaskId() == 0) {
            //是自主寻巡店
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, tbPatrolStoreRecordDO.getCreateUserId());
            TbPatrolStoreHistoryDo tb = TbPatrolStoreHistoryDo.builder().avatar(enterpriseUserDO.getAvatar()).operateUserName(enterpriseUserDO.getName()).
                    createTime(tbPatrolStoreRecordDO.getCreateTime()).operateType("create").build();
            TbPatrolStoreHistoryList.add(tb);
        }
        if (tbPatrolStoreRecordDO != null) {
            //封装任务创建者到  TbPatrolStoreHistoryList
            TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
            if(taskParentDO != null){

                EnterpriseUserDO createUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(enterpriseId, taskParentDO.getCreateUserId());

                if (Constants.SYSTEM_USER_ID.equals(taskParentDO.getCreateUserId())) {
                    taskParentDO.setCreateUserName(Constants.SYSTEM_USER_NAME);
                }else if(Constants.AI.equals(taskParentDO.getCreateUserId())){
                    taskParentDO.setCreateUserName(Constants.AI);
                }else {
                    taskParentDO.setCreateUserName(createUserDO.getName());
                }
                TbPatrolStoreHistoryDo createHistory = new TbPatrolStoreHistoryDo();
                createHistory.setCreateTime(new Date(taskParentDO.getCreateTime()));
                createHistory.setOperateType("create");
                createHistory.setOperateUserId(taskParentDO.getCreateUserId());
                createHistory.setOperateUserName(taskParentDO.getCreateUserName());
                createHistory.setAvatar("");
                if(createUserDO != null){
                    createHistory.setAvatar(createUserDO.getAvatar());
                }
                TbPatrolStoreHistoryList.add(createHistory);
            }
        }
        //按照时间排序返回
        return TbPatrolStoreHistoryList.stream().sorted(Comparator.comparing(TbPatrolStoreHistoryDo::getCreateTime)).collect(Collectors.toList());
    }

    @Override
    public Boolean recordInfoShare(String enterpriseId, Long businessId,String key) {
        try {
            redisUtilPool.setString(key, String.valueOf(businessId), 7 * 24 * 60 * 60);
        } catch (Exception e) {
            log.info("redis执行出错");
            return false;
        }
        return true;
    }

    /**
     * 分组获取人员
     *
     * @param enterpriseId
     * @param taskIdList
     * @return
     */
    private Map<String, List<UnifyPersonDTO>> getTaskPerson(String enterpriseId, List<Long> taskIdList, String storeId, Long loopCount) {
        List<UnifyPersonDTO> unifyPersonDTOS = unifyTaskStoreService.selectALLNodeUserInfoList(enterpriseId, taskIdList, Collections.singletonList(storeId), loopCount);
        // List<UnifyPersonDTO> unifyPersonDTOS = taskMappingMapper.selectPersonInfoByTaskList(enterpriseId, taskIdList, storeId, UnifyTaskConstant.ROLE_CC);
        if (CollectionUtils.isEmpty(unifyPersonDTOS)) {
            return new HashMap<>();
        }

        return unifyPersonDTOS.stream()
                .collect(Collectors.groupingBy(UnifyPersonDTO::getNode));
    }

    /**
     * 更新合格数、不合格数、不适用数等冗余字段 用作统计
     *
     * @param eid
     * @param businessId
     * @param columns
     * @Author chenyupeng
     * @Date 2021/7/14
     * @return: void
     */
    private void checkResultTrans(String eid, Long businessId, List<TbDataStaTableColumnDO> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }
        int failNum = 0;
        int passNum = 0;
        int inapplicableNum = 0;
        for (TbDataStaTableColumnDO column : columns) {
            switch (column.getCheckResult()) {
                case PASS:
                    passNum++;
                    break;
                case FAIL:
                    failNum++;
                    break;
                case INAPPLICABLE:
                    inapplicableNum++;
                    break;
                default:
            }
        }


        tbPatrolStoreRecordMapper.updateCheckResultById(eid, businessId, failNum, passNum, inapplicableNum);
    }

    private void checkSubmitStatus(String enterpriseId, Long businessId, TbPatrolStoreRecordDO tbPatrolStoreRecordDO, Boolean isSignOut) {
        //表单提交
        if ((tbPatrolStoreRecordDO.getSubmitStatus() & 1) == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "该巡店存在未提交检查表，无法签退");
        }
        int existNotCheck =
                tbDataStaTableColumnMapper.existNotCheckByBusinessId(enterpriseId, businessId, PATROL_STORE);
        log.info("existNotCheck:{}", existNotCheck);
        if (existNotCheck == 1) {
            String msg = "该巡店存在无检查结果的检查项，无法签退";
            if (!isSignOut) {
                msg = "该巡店存在无检查结果的检查项，无法提交";
            }
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), msg);
        }


        int signatureStatus = tbPatrolStoreRecordDO.getSubmitStatus() & 4;
        //
        if (tbPatrolStoreRecordDO.getOpenSignature() && signatureStatus == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "请先提交签名");
        }
        int summaryStatus = tbPatrolStoreRecordDO.getSubmitStatus() & 2;
        //
        if (tbPatrolStoreRecordDO.getOpenSummary() && summaryStatus == 0) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR.getCode(), "请先提交总结");
        }
    }

    @Override
    public PageInfo getAutonomyPatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest, CurrentUser currentUser) {
        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();
        PageHelper.clearPage();
        PageHelper.startPage(patrolRecordRequest.getPageNum(), patrolRecordRequest.getPageSize());
        List<String> patrolTypeList = new ArrayList<>();
        if(StringUtils.isNotBlank(patrolRecordRequest.getPatrolType())){
            patrolTypeList = Arrays.asList(patrolRecordRequest.getPatrolType().split(","));
            patrolRecordRequest.setPatrolType(null);
            patrolRecordRequest.setPatrolTypeList(patrolTypeList);
        }
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList =
                tbPatrolStoreRecordMapper.getAutonomyPatrolRecordList(enterpriseId, patrolRecordRequest);
        List<Long> recordIdList = tbPatrolStoreRecordList.stream()
                .map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordIdList)) {
            log.info("getRecordList记录数据为空");
            return new PageInfo(new ArrayList<TbPatrolStoreRecordDO>());
        }
        patrolStoreRecordVOS = getPatrolStoreRecordVO(enterpriseId, tbPatrolStoreRecordList, recordIdList);
        PageInfo pageInfo = new PageInfo<>(tbPatrolStoreRecordList);
        pageInfo.setList(patrolStoreRecordVOS);
        return pageInfo;
    }

    private List<PatrolStoreRecordVO>  getPatrolStoreRecordVO(String enterpriseId,
                     List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList, List<Long> recordIdList){
        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();
        // 获取metaTableId
        List<Long> metaTableIdList = new ArrayList<>();
        Map<Long, List<TbDataTableDO>> dataTableMap = Maps.newHashMap();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), PATROL_STORE);
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            metaTableIdList = dataTableList.stream().map(TbDataTableDO::getMetaTableId).distinct().collect(Collectors.toList());
            dataTableMap = dataTableList.stream()
                    .collect(Collectors.groupingBy(TbDataTableDO::getBusinessId));
        }
        List<TbMetaTableDO> metaTableDOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metaTableIdList)){
            metaTableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        }
        // map:recordId->tbMetaTableDOMap
        Map<Long, String> tbMetaTableDOMap =
                metaTableDOList.stream()
                        .filter(a -> a.getId() != null && a.getTableName() != null)
                        .collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));
        List<TbDataDefTableColumnDO> defTableColumnDOList =
                tbDataDefTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList, null);

        Map<Long, List<TbDataDefTableColumnDO>> recordDefDateColumnMap =
                defTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getBusinessId));

        // 查询所有巡店记录的检查项列表
        List<TbDataStaTableColumnDO> staTableColumnDOList =
                tbDataStaTableColumnMapper.getListByRecordIdList(enterpriseId, recordIdList);

        // map:recordId->StaColumnData
        Map<Long, List<TbDataStaTableColumnDO>> recordStaDateColumnMap =
                staTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getBusinessId));
        // map:recordId->StaColumnData
        Map<Long, List<TbDataStaTableColumnDO>> recordStaDateColumnDataTableMap =
                staTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataStaTableColumnDO::getDataTableId));
        // map:recordId->DefColumnData
        Map<Long, List<TbDataDefTableColumnDO>> recordDefDateColumnDataTableMap =
                defTableColumnDOList.stream().collect(Collectors.groupingBy(TbDataDefTableColumnDO::getDataTableId));

        Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
        tbPatrolStoreRecordList.forEach(tbPatrolStoreRecordDO -> {
            PatrolStoreRecordVO patrolStoreRecordVO = new PatrolStoreRecordVO();
            patrolStoreRecordVO.setTbPatrolStoreRecordDO(tbPatrolStoreRecordDO);
            List<TbDataStaTableColumnDO> staTableColumnDOS =
                    recordStaDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());

            int unPassCount =
                    (int) staTableColumnDOS.stream().filter(data -> "FAIL".equals(data.getCheckResult())).count();
            //不适用检查项
            int inApplicableCount =
                    (int) staTableColumnDOS.stream().filter(data -> MetaTableConstant.CheckResultConstant.INAPPLICABLE.equals(data.getCheckResult())).count();

            List<TbDataDefTableColumnDO> defTableColumnDOS =
                    recordDefDateColumnMap.getOrDefault(tbPatrolStoreRecordDO.getId(), new ArrayList<>());

            int canQuestionCount = (int) staTableColumnDOS.stream()
                    .filter(data -> "FAIL".equals(data.getCheckResult()) && data.getTaskQuestionId() == 0).count();

            patrolStoreRecordVO.setCanQuestion(canQuestionCount > 0);
            Integer staCount = staTableColumnDOS.size();
            Integer defCount = defTableColumnDOS.size();
            Integer allCount = staCount + defCount;

            List<PersonDTO> personList = new ArrayList<>();
            PersonDTO personDTO = new PersonDTO();
            personDTO.setUserId(tbPatrolStoreRecordDO.getSupervisorId());
            personDTO.setUserName(tbPatrolStoreRecordDO.getSupervisorName());
            personList.add(personDTO);
            patrolStoreRecordVO.setPersonList(personList);


 /*           Long metaTableId = tbPatrolStoreRecordDO.getMetaTableId();
            if (metaTableId != null && metaTableId > 0) {
                String metaTableName = tbMetaTableDOMap.get(metaTableId);
                patrolStoreRecordVO.setMetaTableName(metaTableName);
            }*/
            List<TbDataTableDO> tbDataTableDOList = finalDataTableMap.get(tbPatrolStoreRecordDO.getId());
            if (CollectionUtils.isNotEmpty(tbDataTableDOList)) {
                Long metaTableId = tbDataTableDOList.get(0).getMetaTableId();
                String metaTableName = tbMetaTableDOMap.get(metaTableId);
                patrolStoreRecordVO.setMetaTableName(metaTableName);
            }

            if(CollectionUtils.isNotEmpty(tbDataTableDOList)){
                List<TbDataTableVO> dataTableVOList = tbDataTableDOList.stream()
                        .map(dataTableDO -> TbDataTableVO.builder().id(dataTableDO.getId()).metaTableId(dataTableDO.getMetaTableId())
                                .metaTableName(dataTableDO.getTableName()).tableProperty(dataTableDO.getTableProperty())
                                .allCount(recordStaDateColumnDataTableMap.getOrDefault(dataTableDO.getId(), new ArrayList<>()).size() +
                                        recordDefDateColumnDataTableMap.getOrDefault(dataTableDO.getId(), new ArrayList<>()).size())
                                .unPassCount(dataTableDO.getFailNum()).totalScore(dataTableDO.getTotalScore()).taskCalTotalScore(dataTableDO.getTaskCalTotalScore())
                                .checkScore(dataTableDO.getCheckScore()).passCount(dataTableDO.getPassNum())
                                .inApplicableCount(dataTableDO.getInapplicableNum())
                                .build()).collect(Collectors.toList());
                patrolStoreRecordVO.setDataTableList(dataTableVOList);
            }

            patrolStoreRecordVO.setOverdue(false);
            if (tbPatrolStoreRecordDO.getStatus() != null &&
                    PatrolStoreRecordStatusEnum.FINISH.getStatus() == tbPatrolStoreRecordDO.getStatus() && tbPatrolStoreRecordDO.getSubEndTime() != null
                    && tbPatrolStoreRecordDO.getSignEndTime() != null) {
                patrolStoreRecordVO.setOverdue(tbPatrolStoreRecordDO.getSignEndTime().after(tbPatrolStoreRecordDO.getSubEndTime()));
            } else if (tbPatrolStoreRecordDO.getSubEndTime() != null) {
                patrolStoreRecordVO.setOverdue(new Date().after(tbPatrolStoreRecordDO.getSubEndTime()));
            }
            patrolStoreRecordVO.setAllCount(allCount);
            patrolStoreRecordVO.setUnPassCount(unPassCount);
            patrolStoreRecordVO.setInApplicableCount(inApplicableCount);
            patrolStoreRecordVOS.add(patrolStoreRecordVO);
        });
        return patrolStoreRecordVOS;
    }

    @Override
    public PageInfo getCompletePatrolRecordList(String enterpriseId, PatrolRecordRequest patrolRecordRequest) {
        List<PatrolStoreRecordVO> patrolStoreRecordVOS = new ArrayList<>();
        PageHelper.clearPage();
        PageHelper.startPage(patrolRecordRequest.getPageNum(), patrolRecordRequest.getPageSize(), false);
        String regionPath = null;
        if (StrUtil.isNotEmpty(patrolRecordRequest.getRegionId())) {
            regionPath = regionService.getRegionPath(enterpriseId, patrolRecordRequest.getRegionId());
        }
        //门店自检不在这里展示
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList =
                tbPatrolStoreRecordMapper.getPatrolRecordListForPerson(enterpriseId, patrolRecordRequest.getBeginTime(),
                        patrolRecordRequest.getEndTime(), patrolRecordRequest.getPatrolType(), patrolRecordRequest.getPatrolMode(),
                        patrolRecordRequest.getPatrolOverdue(), patrolRecordRequest.getCreateBeginTime(), patrolRecordRequest.getCreateEndTime(),
                        patrolRecordRequest.getStoreIdList(), regionPath, patrolRecordRequest.getMetaTableId(),
                        patrolRecordRequest.getSupervisorId(), patrolRecordRequest.getTaskName());
        List<Long> recordIdList = tbPatrolStoreRecordList.stream()
                .map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recordIdList)) {
            log.info("getRecordList记录数据为空");
            return new PageInfo(new ArrayList<TbPatrolStoreRecordDO>());
        }
        patrolStoreRecordVOS = getPatrolStoreRecordVO(enterpriseId, tbPatrolStoreRecordList, recordIdList);
        PageInfo pageInfo = new PageInfo<>(tbPatrolStoreRecordList);
        pageInfo.setList(patrolStoreRecordVOS);
        return pageInfo;
    }

    @Override
    public PageDTO<PatrolStoreDetailExportVO> getPatrolStoreDetail(String enterpriseId, PatrolStoreDetailRequest request) {
        if (request.getPageNumber()==null||request.getPageSize()==null){
            request.setPageSize(10);
            request.setPageNumber(1);
        }
        //查询巡店记录
        PageHelper.startPage(request.getPageNumber(),request.getPageSize());
        if (CollectionUtils.isEmpty(request.getPatrolStoreMode())){
            throw new ServiceException(ErrorCodeEnum.PARTROL_STORE_MODE);
        }
        Integer patrolStoreMode = null;
        if (request.getPatrolStoreMode().size()==1){
            if (request.getPatrolStoreMode().get(0)==1){
                patrolStoreMode = 0;
            }
            if (request.getPatrolStoreMode().get(0)==2){
                patrolStoreMode = 1;
            }
        }
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordDOS = null;
        if(request.getMetaTableId() == null){
            tbPatrolStoreRecordDOS = tbPatrolStoreRecordMapper.selectPatrolStoreByCondition(enterpriseId, request.getBeginTime(),
                    request.getEndTime(), request.getMetaTableId(), request.getPatrolTypeList(), patrolStoreMode);
        }else {
            List<Long> recordIdList = tbDataTableMapper.selectPatrolStoreByCondition(enterpriseId, request.getBeginTime(),
                    request.getEndTime(), request.getMetaTableId(), request.getPatrolTypeList(), patrolStoreMode);
            if (CollectionUtils.isEmpty(recordIdList)){
                throw new ServiceException(ErrorCodeEnum.PATROL_STORE_RECORD_IS_NULL);
            }
            if (BailiEnterpriseEnum.bailiAffiliatedCompany(enterpriseId)) {
                tbPatrolStoreRecordDOS = tbPatrolStoreRecordMapper.selectByIdsandType(enterpriseId, recordIdList);
            }else {
                tbPatrolStoreRecordDOS = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, recordIdList);
            }
//            tbPatrolStoreRecordDOS = tbPatrolStoreRecordMapper.selectByIds(enterpriseId, recordIdList);
//            tbPatrolStoreRecordDOS = tbPatrolStoreRecordMapper.selectByIdsandType(enterpriseId, recordIdList);
        }
        if (CollectionUtils.isEmpty(tbPatrolStoreRecordDOS)){
            throw new ServiceException(ErrorCodeEnum.PATROL_STORE_RECORD_IS_NULL);
        }
        //检查表
        // TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbPatrolStoreRecordDOS.get(0).getMetaTableId());

        List<String> storeIds = tbPatrolStoreRecordDOS.stream().map(TbPatrolStoreRecordDO::getStoreId).collect(Collectors.toList());
        List<Long> businessIds = tbPatrolStoreRecordDOS.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());

        List<TbDataTableDO> dataTableDOList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, businessIds, PATROL_STORE);

        dataTableDOList = dataTableDOList.stream().filter(o -> o.getMetaTableId().equals(request.getMetaTableId())).collect(Collectors.toList());

        Map<Long, TbDataTableDO> dataTableDOMap = dataTableDOList.stream().collect(Collectors.toMap(TbDataTableDO::getBusinessId, Function.identity()));


        List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeNumMap = storeListByStoreIds.stream().filter(x->StringUtils.isNotEmpty(x.getStoreNum())).collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getStoreNum));
        Map<String, String> storeAddressMap = storeListByStoreIds.stream().filter(x->StringUtils.isNotEmpty(x.getStoreAddress())).collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getStoreAddress));

        regionServiceImpl.storesExtendFieldHandle(enterpriseId,storeListByStoreIds);
        Map<String, List<ExtendFieldInfoVO>> extendFieldMap = storeListByStoreIds.stream().filter(x->CollectionUtils.isNotEmpty(x.getExtendFieldInfoList())).collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getExtendFieldInfoList));


        List<Long> regionIds = tbPatrolStoreRecordDOS.stream().map(TbPatrolStoreRecordDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> regionDOS = regionMapper.getRegionPathByIds(enterpriseId,regionIds);
        Map<Long, String> regionMap = regionDOS.stream().collect(Collectors.toMap(RegionDO::getId, RegionDO::getName));

        PageDTO<PatrolStoreDetailExportVO> objectPageDTO = new PageDTO<>();
        List<PatrolStoreDetailExportVO> result = new ArrayList<>();

        List<StorePathDTO> storePathDTOList=new ArrayList<>();
        for (TbPatrolStoreRecordDO tbPatrolStoreRecordDO:tbPatrolStoreRecordDOS) {
            StorePathDTO storePathDTO =new StorePathDTO();
            storePathDTO.setStoreId(tbPatrolStoreRecordDO.getStoreId());
            storePathDTO.setRegionPath(tbPatrolStoreRecordDO.getRegionWay());
            storePathDTOList.add(storePathDTO);
            Map<String, List<String>> fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);
            PatrolStoreDetailExportVO patrolStoreDetailExportVO = new PatrolStoreDetailExportVO();
            patrolStoreDetailExportVO.setPatrolStoreDate(tbPatrolStoreRecordDO.getSignEndTime());
            patrolStoreDetailExportVO.setRegionNameList(fullRegionNameMap.get(tbPatrolStoreRecordDO.getStoreId()));
            patrolStoreDetailExportVO.setPatrolStoreDuration(DateUtils.formatBetween(tbPatrolStoreRecordDO.getSignStartTime(),tbPatrolStoreRecordDO.getSignEndTime()));
            patrolStoreDetailExportVO.setStoreId(tbPatrolStoreRecordDO.getStoreId());
            patrolStoreDetailExportVO.setStoreName(tbPatrolStoreRecordDO.getStoreName());
            patrolStoreDetailExportVO.setStoreNum(storeNumMap.getOrDefault(tbPatrolStoreRecordDO.getStoreId(),""));
            patrolStoreDetailExportVO.setStoreAddress(storeAddressMap.getOrDefault(tbPatrolStoreRecordDO.getStoreId(),""));
            BigDecimal divide = new BigDecimal(0);
            TbDataTableDO dataTableDO = dataTableDOMap.get(tbPatrolStoreRecordDO.getId());
            if(Objects.isNull(dataTableDO)){
                continue;
            }
            patrolStoreDetailExportVO.setStoreTotalScore(dataTableDO.getCheckScore());
            patrolStoreDetailExportVO.setStoreTotalScoreStr(dataTableDO.getCheckScore().toString());
            if (dataTableDO.getTaskCalTotalScore().compareTo(new BigDecimal(0))!=0){
                divide = dataTableDO.getCheckScore().multiply(new BigDecimal(100)).divide(dataTableDO.getTaskCalTotalScore(), 2, BigDecimal.ROUND_HALF_UP);
            }
            patrolStoreDetailExportVO.setStoreScoreRate(divide + "%");


            BigDecimal passRate = new BigDecimal(0);
            if (dataTableDO.getTotalCalColumnNum()!=0){
                passRate = new BigDecimal(dataTableDO.getPassNum()).multiply(new BigDecimal(100)).divide(new BigDecimal(dataTableDO.getTotalCalColumnNum()), 2, BigDecimal.ROUND_HALF_UP);
            }
            patrolStoreDetailExportVO.setPassRate(passRate + "%");

            BigDecimal failRate = new BigDecimal(0);
            if (dataTableDO.getTotalCalColumnNum()!=0){
                failRate = new BigDecimal(dataTableDO.getFailNum()).multiply(new BigDecimal(100)).divide(new BigDecimal(dataTableDO.getTotalCalColumnNum()), 2, BigDecimal.ROUND_HALF_UP);
            }
            patrolStoreDetailExportVO.setFailureRate(failRate + "%");
            //自定义表不显示
            if (MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().toString().equals(dataTableDO.getTableProperty().toString())){
                patrolStoreDetailExportVO.setStoreTotalScoreStr("/");
                patrolStoreDetailExportVO.setStoreScoreRate("/");
                patrolStoreDetailExportVO.setPassRate("/");
                patrolStoreDetailExportVO.setFailureRate("/");
            }
            patrolStoreDetailExportVO.setSignInTime(tbPatrolStoreRecordDO.getSignStartTime());
            patrolStoreDetailExportVO.setSignOutTime(tbPatrolStoreRecordDO.getSignEndTime());
            patrolStoreDetailExportVO.setStoreExtendField(extendFieldMap.getOrDefault(tbPatrolStoreRecordDO.getStoreId(),new ArrayList<>()));
            // String checkResultLevel =  getCheckResultLevel(tbPatrolStoreRecordDO.getPassNum(), tbMetaTableDO, tbPatrolStoreRecordDO.getScore(), tbPatrolStoreRecordDO.getTaskCalTotalScore());
            String checkResultLevel =  dataTableDO.getCheckResultLevel();
            String checkResultName = "";
            if("excellent".equals(checkResultLevel)){
                checkResultName =  "优秀";
            }else if("good".equals(checkResultLevel)){
                checkResultName =  "良好";
            }else if("eligible".equals(checkResultLevel)){
                checkResultName = "合格";
            } else if ("disqualification".equals(checkResultLevel)) {
                checkResultName = "不合格";
            } else if("qualifiedNum".equals(checkResultLevel)){
                checkResultName = "不合格";
            } else {
                checkResultName = "";
            }
            patrolStoreDetailExportVO.setPatrolStoreResult(checkResultName);
            patrolStoreDetailExportVO.setCheckAwardPunish(dataTableDO.getTotalResultAward());
            patrolStoreDetailExportVO.setPatrolStoreUserName(tbPatrolStoreRecordDO.getSupervisorName());
            patrolStoreDetailExportVO.setPatrolStoreUserId(tbPatrolStoreRecordDO.getSupervisorId());
            List<SysRoleDO> sysRoleDOS = sysRoleMapper.listRoleByUserId(enterpriseId, tbPatrolStoreRecordDO.getSupervisorId());
            if (CollectionUtils.isNotEmpty(sysRoleDOS)){
                String sysRoleNameList = sysRoleDOS.stream().map(SysRoleDO::getRoleName).collect(Collectors.joining(","));
                patrolStoreDetailExportVO.setPatrolStoreUserRoleName(sysRoleNameList);
            }
            patrolStoreDetailExportVO.setRegionName(regionMap.get(tbPatrolStoreRecordDO.getRegionId()));
            patrolStoreDetailExportVO.setBusinessIds(businessIds);
            patrolStoreDetailExportVO.setBusinessId(tbPatrolStoreRecordDO.getId());
            result.add(patrolStoreDetailExportVO);
        }
        objectPageDTO.setList(result);
        return objectPageDTO;
    }

    @Override
    public ResponseResult<List<String>> patrolStoreReminder(String enterpriseId, Long taskId, String appType) {
        //钉钉类型的企业返回需要催办的人即可，其他类型的企业，发送通知
        //先查询对应节点的人
        String content;
        TaskParentDO taskParentDO = taskParentMapper.selectParentTaskById(enterpriseId, taskId);
        if(taskParentDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        String endTimeStr = DateUtils.convertTimeToString(taskParentDO.getEndTime(), DATE_FORMAT_SEC_5);
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, taskParentDO.getCreateUserId());
        content = AppTypeEnum.isQwType(appType) ? "【$userName={0}$】给你分配了一个任务，请你于【{1}】前完成" : "【{0}】给你分配了一个任务，请你于【{1}】前完成";
        content = MessageFormat.format(content, createUserName, endTimeStr);
        // 王晓鹏给你分配了一个任务，请你于2022.11.24 17:45前完成
        //未完成的人员集合,用于发催办
        List<UnifyParentUser> urgingUserList =
                taskSubMapper.selectUnCompleteUser(enterpriseId, Collections.singletonList(taskId), null, null, null, null,
                        null, null, null);
        Set<String> urgingUserIdSet = urgingUserList.stream().map(UnifyParentUser::getUserId).collect(Collectors.toSet());
        List<String> pendingUserList = new ArrayList<>(urgingUserIdSet);
        if(AppTypeEnum.isDingType(appType)){
            return ResponseResult.success(pendingUserList);
        }
        //发送通知
        jmsTaskService.sendPatrolStoreReminder(enterpriseId, taskParentDO, pendingUserList, content);
        return ResponseResult.success(pendingUserList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long recheckPatrol(String enterpriseId, Long businessId, String userId, String userName) {
        TbPatrolStoreRecordDO storeRecordD = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);

        if (storeRecordD == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if (storeRecordD.getStatus() != Constants.ONE) {
            throw new ServiceException(ErrorCodeEnum.STORE_APPROVE_NOT);
        }
        //进行中的复审任务
        TbPatrolStoreRecordDO currentRecord = tbPatrolStoreRecordMapper.selectByRecheckBusinessId(enterpriseId, businessId, userId);
        if(currentRecord != null){
            return currentRecord.getId();
        }
        String createDate = DateUtils.getTime(new Date());
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, Collections.singletonList(businessId), PATROL_STORE);
        storeRecordD.setId(null);
        storeRecordD.setTaskId(0L);
        storeRecordD.setSubTaskId(0L);
        storeRecordD.setStatus(0);
        storeRecordD.setCreateTime(new Date());
        storeRecordD.setCreateUserId(userId);
        storeRecordD.setRecheckBusinessId(businessId);
        storeRecordD.setBusinessCheckType(BusinessCheckType.PATROL_RECHECK.getCode());
        storeRecordD.setSubmitStatus(Constants.INDEX_SIX & storeRecordD.getSubmitStatus());
        storeRecordD.setCreateDate(createDate);
        storeRecordD.setRecheckUserId(userId);
        storeRecordD.setRecheckUserName(userName);
        storeRecordD.setOpenSubmitFirst(true);
        tbPatrolStoreRecordMapper.insertSelective(storeRecordD, enterpriseId);
        dataTableList.forEach(e -> {
            e.setBusinessId(storeRecordD.getId());
            e.setSubmitStatus(0);
            e.setTaskId(0L);
            e.setSubTaskId(0L);
            e.setBusinessType(BusinessCheckType.PATROL_RECHECK.getCode());
            e.setCreateDate(createDate);
            e.setCreateTime(new Date());
            e.setCreateUserId(userId);
        });
        tbDataTableMapper.batchInsert(enterpriseId, dataTableList);

        List<TbDataStaTableColumnDO> dataStaTableColumnDOList = tbDataStaTableColumnMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);
        List<TbDataDefTableColumnDO> defTableColumnDOList = tbDataDefTableColumnMapper.selectByBusinessId(enterpriseId, businessId, PATROL_STORE);

        Map<Long, Long> dataTableIdMap = ListUtils.emptyIfNull(dataTableList).stream().collect(Collectors.toMap(TbDataTableDO::getMetaTableId, TbDataTableDO::getId, (a, b) -> a));
        dataStaTableColumnDOList.forEach(dataStaTableColumnDO -> {
            dataStaTableColumnDO.setBusinessType(BusinessCheckType.PATROL_RECHECK.getCode());
            dataStaTableColumnDO.setBusinessId(storeRecordD.getId());
            dataStaTableColumnDO.setBusinessStatus(0);
            dataStaTableColumnDO.setDataTableId(dataTableIdMap.get(dataStaTableColumnDO.getMetaTableId()));
            dataStaTableColumnDO.setTaskId(0L);
            dataStaTableColumnDO.setSubTaskId(0L);
            dataStaTableColumnDO.setCreateTime(new Date());
            dataStaTableColumnDO.setCreateDate(createDate);
            dataStaTableColumnDO.setId(null);
            dataStaTableColumnDO.setTaskQuestionId(0L);
            dataStaTableColumnDO.setSubmitStatus(0);
            dataStaTableColumnDO.setCreateUserId(userId);
        });
        if(CollectionUtils.isNotEmpty(dataStaTableColumnDOList)){
            tbDataStaTableColumnMapper.batchInsert(enterpriseId, dataStaTableColumnDOList);
        }
        defTableColumnDOList.forEach(dataDefTableColumnDO -> {
            dataDefTableColumnDO.setBusinessType(BusinessCheckType.PATROL_RECHECK.getCode());
            dataDefTableColumnDO.setBusinessId(storeRecordD.getId());
            dataDefTableColumnDO.setBusinessStatus(0);
            dataDefTableColumnDO.setDataTableId(dataTableIdMap.get(dataDefTableColumnDO.getMetaTableId()));
            dataDefTableColumnDO.setTaskId(0L);
            dataDefTableColumnDO.setSubTaskId(0L);
            dataDefTableColumnDO.setCreateTime(new Date());
            dataDefTableColumnDO.setCreateDate(createDate);
            dataDefTableColumnDO.setId(null);
            dataDefTableColumnDO.setTaskQuestionId(0L);
            dataDefTableColumnDO.setSubmitStatus(0);
            dataDefTableColumnDO.setCreateUserId(userId);
        });
        if(CollectionUtils.isNotEmpty(defTableColumnDOList)){
            tbDataDefTableColumnMapper.batchInsert(enterpriseId, defTableColumnDOList);
        }
        TbPatrolStoreRecordInfoDO recordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, businessId);
        if(recordInfoDO != null){
            recordInfoDO.setId(storeRecordD.getId());
            tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId, recordInfoDO);
        }
        return storeRecordD.getId();
    }

    @Override
    public PatrolOverviewDTO recheckOverview(String enterpriseId, Long beginTime, Long endTime, String userId, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        String beginDate = DateUtils.convertTimeToString(beginTime, DateUtils.DATE_FORMAT_SEC);
        String endDate = DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_DAY) + " 23:59:59";
        PatrolOverviewDTO patrolOverviewDTO = new PatrolOverviewDTO();
        //可复审数量
        Long canRecheck = 0L;
        //已复审数量
        Long alreadyRecheck = 0L;
        //复审率
        patrolOverviewDTO.setCanRecheck(canRecheck);
        patrolOverviewDTO.setAlreadyRecheck(alreadyRecheck);
        List<String> regionPathList = new ArrayList<>();
        if(!isAdmin){
            List<UserAuthMappingDO> userAuthMappingDOList =  userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, userId);
            List<String> regionIdList =  userAuthMappingDOList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(regionIdList)){
                return patrolOverviewDTO;
            }
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, regionIdList);
            regionPathList = regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(regionPathList)){
                return patrolOverviewDTO;
            }
        }
        canRecheck = tbPatrolStoreRecordMapper.patrolOverviewNeedRecheckCount(enterpriseId, regionPathList, beginDate, endDate);
        alreadyRecheck =  tbPatrolStoreRecordMapper.patrolOverviewRecheckCount(enterpriseId, userId, beginDate, endDate);
        patrolOverviewDTO.setCanRecheck(canRecheck);
        patrolOverviewDTO.setAlreadyRecheck(alreadyRecheck);
        return patrolOverviewDTO;
    }

    @Override
    public ImportTaskDO patrolStoreReviewListExport(CurrentUser user, String enterpriseId,PatrolStoreStatisticsDataTableQuery query) {
        //导出数量
        query.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        Integer recheckStatus = query.getRecheckStatus();
        List<String> recordIds=new ArrayList<>();

        int pageSize = Constants.BATCH_INSERT_COUNT; // 每页记录数
        int pageNum = 1; // 初始页码，从1开始
        //分页查
        while (true){
            if(query.getRegionId() != null){
                String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
                query.setRegionPath(regionPath);
            }
            if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
                List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
                if(CollectionUtils.isNotEmpty(regionPathDTOList)){
                    List<String> regionPathList =  regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
                    query.setRegionPathList(regionPathList);
                }
            }
            PageHelper.startPage(pageNum,pageSize);
            List<String> partRecordIds=new ArrayList<>();
            if(recheckStatus!= null && Constants.ONE == recheckStatus ){
                //已复审
                query.setBusinessCheckType(BusinessCheckType.PATROL_RECHECK.getCode());
                partRecordIds=tbPatrolStoreRecordMapper.selectRecheckRecordIdsByQuery(enterpriseId,query,null);
            }else {
                //可复审
                query.setStatus(1);
                query.setPatrolTypeList(Arrays.asList(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode(), TaskTypeEnum.PATROL_STORE_ONLINE.getCode(),
                        TaskTypeEnum.STORE_SELF_CHECK.getCode()));
                partRecordIds=tbPatrolStoreRecordMapper.selectRecordIdsByQuery(enterpriseId,query,null);
            }
            recordIds.addAll(partRecordIds);
            if (partRecordIds.size() < pageSize) {
                // 当前页记录数小于每页记录数，说明已经查完所有记录，退出循环
                break;
            }
            pageNum++;
        }

        if (CollectionUtils.isEmpty(recordIds)) {
            throw new ServiceException("当前无记录可导出");
        }
        //0.查询待复审数据,1.查询已复审数据
        final Long[] total = {0L};
        ListUtils.partition(recordIds,pageSize).stream().forEach(partIds->{
            Long count= tbDataStaTableColumnMapper.selectCountPatrolStoreReviewListByBusinessId(enterpriseId,partIds);
            total[0] +=count;
        });

        if (total[0] == 0) {
            throw new ServiceException("当前无记录可导出");
        }

        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.PATROL_STORE_REVIEW_LIST_EXPORT);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.PATROL_STORE_REVIEW_LIST_EXPORT);

        PatrolStoreReviewExportRequest msg = new PatrolStoreReviewExportRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setDbName(user.getDbName());
        msg.setImportTaskDO(importTaskDO);
        msg.setTotalNum(total[0]);
        msg.setRecordIds(recordIds);


        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.PATROL_STORE_REVIEW_LIST_EXPORT.getCode());

        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRecord(String enterpriseId, TbDisplayDeleteParam tbDisplayDeleteParam, CurrentUser currentUser,String isDone, EnterpriseConfigDO config) {
        Long taskStoreId = tbDisplayDeleteParam.getTaskStoreId();
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        if(taskStoreDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if (isDone.equals("notDone") && !taskStoreDO.getSubStatus().equals("ongoing")){
            log.info("当前门店任务没有进行中的任务");
            return;
        }
        TbPatrolStoreRecordDO patrolStoreRecordDO = tbPatrolStoreRecordMapper.getRecordByTaskLoopCount(enterpriseId,
                taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount(), null, null);
        if(patrolStoreRecordDO == null){
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        List<TaskSubVO> taskSubList = taskSubMapper.selectSubTaskByTaskIdAndStoreIdAndLoop(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        taskStoreMapper.delTaskStoreById(enterpriseId, taskStoreId);
        taskSubMapper.delSubTaskByTaskIdAndStoreIdAndLoopCount(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
        TbPatrolStoreRecordInfoDO patrolStoreRecordInfoDO = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfo(enterpriseId, patrolStoreRecordDO.getId());
        if (patrolStoreRecordInfoDO == null) {
            //记录签到额外信息
            patrolStoreRecordInfoDO = new TbPatrolStoreRecordInfoDO();
            patrolStoreRecordInfoDO.setEid(enterpriseId);
            patrolStoreRecordInfoDO.setId(patrolStoreRecordDO.getId());
            tbPatrolStoreRecordInfoMapper.saveTbPatrolStoreRecordInfo(enterpriseId, patrolStoreRecordInfoDO);
        }
        if(isDone.equals("notDone")){
            patrolStoreRecordDO.setTaskStatus((TaskStatusEnum.STOP.getCode()));
            tbPatrolStoreRecordMapper.updateByPrimaryKeySelective(patrolStoreRecordDO, enterpriseId);
        }
        tbPatrolStoreRecordInfoMapper.updateDeleteUserInfo(enterpriseId, patrolStoreRecordDO.getId(), currentUser.getUserId(), currentUser.getName());
        this.delPatrolStoreByBusinessIds(enterpriseId, Collections.singletonList(patrolStoreRecordDO.getId()));
        taskStoreDO.setDeleted(1);
        // 取消待办
        if (TaskTypeEnum.isCombineNoticeTypes(taskStoreDO.getTaskType())) {
            List<String> userIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getHandleUserId);
            unifyTaskService.cancelCombineUpcoming(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getLoopCount(), taskStoreDO.getStoreId(), taskStoreDO.getNodeNo(), userIds, config.getDingCorpId(), config.getAppType());
        } else {
            List<Long> subTaskIds = CollStreamUtil.toList(taskSubList, TaskSubVO::getSubTaskId);
            unifyTaskService.cancelUpcoming(enterpriseId, subTaskIds, config.getDingCorpId(), config.getAppType());
        }
    }


    @Override
    public PageInfo<TbDisplayTableRecordDeleteVO> getDeleteRecordList(String enterpriseId, Long unifyTaskId, Integer pageNum, Integer pageSize,String unifyTaskIds,
                                                                      String taskStatus) {
        PageHelper.startPage(pageNum, pageSize);
        List<TbPatrolStoreRecordDO> list = new ArrayList<>();
        if (StringUtils.isNotBlank(unifyTaskIds)){
            String[] idArray = StringUtils.split(unifyTaskIds, ",");
            List<Long> unifyTaskIdList = Arrays.stream(idArray)
                    .map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
            list = tbPatrolStoreRecordMapper.deleteListByUnifyTaskIds(enterpriseId, unifyTaskIdList,taskStatus);
        }else {
            list = tbPatrolStoreRecordMapper.deleteListByUnifyTaskId(enterpriseId, unifyTaskId,taskStatus);
        }
        PageInfo pageInfo = new PageInfo(list);
        if(CollectionUtils.isEmpty(list)){
            return pageInfo;
        }

        List<Long> businessIdList = list.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        List<TbPatrolStoreRecordInfoDO> storeRecordInfoDOList = tbPatrolStoreRecordInfoMapper.selectTbPatrolStoreRecordInfoList(enterpriseId, businessIdList);
        Map<Long, TbPatrolStoreRecordInfoDO> storeRecordInfoDOMap = ListUtils.emptyIfNull(storeRecordInfoDOList).stream().collect(Collectors.toMap(TbPatrolStoreRecordInfoDO::getId, data -> data, (a, b) -> a));
        List<TbDisplayTableRecordDeleteVO> result = new ArrayList<>();
        list.forEach(recordDO -> {
            TbDisplayTableRecordDeleteVO deleteVO = new TbDisplayTableRecordDeleteVO();
            deleteVO.setId(recordDO.getId());
            deleteVO.setStoreId(recordDO.getStoreId());
            deleteVO.setStoreName(recordDO.getStoreName());
            deleteVO.setUnifyTaskId(recordDO.getTaskId());
            TbPatrolStoreRecordInfoDO storeRecordInfoDO = storeRecordInfoDOMap.get(recordDO.getId());
            if(storeRecordInfoDO != null){
                deleteVO.setDeleteUserId(storeRecordInfoDO.getDeleteUserId());
                deleteVO.setDeleteTime(storeRecordInfoDO.getDeleteTime());
                deleteVO.setDeleteUserName(storeRecordInfoDO.getDeleteUserName());
            }
            result.add(deleteVO);
        });
        pageInfo.setList(result);
        return pageInfo;
    }

    /**
     * 分组返回的List<Map<String, Long>> list 转为 Map
     * @param list
     * @return
     */
  public Map<String, Long> convertListToMap(List<Map<String, Long>> list){
      Map<String, Long> map = new HashMap<>();
      if (list != null && !list.isEmpty()) {
          for (Map<String, Long> hashMap : list) {
              String key = null;
              Long value = null;
              for (Map.Entry<String, Long> entry : hashMap.entrySet()) {
                  if ("checkResult".equals(entry.getKey())) {
                      key = String.valueOf(entry.getValue());
                  } else if ("count".equals(entry.getKey())) {
                      //我需要的是int型所以做了如下转换，实际上返回的object应为Long。
                      value = Long.valueOf((entry.getValue()).intValue());
                  }
              }
              map.put(key, value);
          }
      }
      return map;
  }

  /**
  * 是否是自定义表 是 返回false  不是返回true
  * @param tableProperty
  * @return Boolean
  */
  public Boolean isUserDefinedTable(Integer tableProperty){
      boolean equals = MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(tableProperty);
      return !equals;
  }

    /**
     * 计算巡店等级
     *
     * @param passNum
     * @param tableDO
     * @param score
     * @param taskCalTotalScore
     * @return
     */
    @Override
    public String getCheckResultLevel(Integer passNum, TbMetaTableDO tableDO, BigDecimal score, BigDecimal taskCalTotalScore) {
        if (StringUtils.isBlank(tableDO.getLevelInfo())) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(tableDO.getLevelInfo());
        List<TableCheckSettingLevelVO> levelList = JSONArray.parseArray(jsonObject.getString("levelList"), TableCheckSettingLevelVO.class);
        if (CollectionUtils.isEmpty(levelList)) {
            return null;
        }
        if (LevelRuleEnum.SCORING_RATE.getCode().equals(tableDO.getLevelRule()) && score != null && taskCalTotalScore != null) {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getPercent).reversed());
            BigDecimal percent = BigDecimal.ZERO;
            if (new BigDecimal(Constants.ZERO_STR).compareTo(score) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(taskCalTotalScore) != 0) {
                percent = (score.divide(taskCalTotalScore, 2, RoundingMode.DOWN).multiply(new BigDecimal(Constants.ONE_HUNDRED)));
            }
            for (TableCheckSettingLevelVO levelVO : levelList) {
                if (percent.intValue() >= levelVO.getPercent()) {
                    return levelVO.getKeyName();
                }
            }
        } else {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getQualifiedNum).reversed());
            for (TableCheckSettingLevelVO levelVO : levelList) {
                if (passNum >= levelVO.getQualifiedNum()) {
                    return levelVO.getKeyName();
                }
            }
        }
        return null;
    }

    @Override
    public Long getBusinessId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount) {
        return tbPatrolStoreRecordMapper.selectIdByTaskLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
    }


    /**
     * getNum 获取数据 防止出现null情况统一处理
     * @param num
     * @return
     */
    public Integer getNum(Integer num){

        if (null == num){
            return 0;
        }
        return num;
    }


    private Integer getTotalNum(Map<Long, PatrolStoreStatisticsDataColumnCountDTO> dataDefTableEveryColumnCountMap, Map<Long, PatrolStoreStatisticsDataStaTableCountDTO> dataTableIdColumnCountMap, Long dataTableId){
        PatrolStoreStatisticsDataStaTableCountDTO countDTO = dataTableIdColumnCountMap.get(dataTableId);
        if(countDTO != null){
            return countDTO.getTotalColumnCount();
        }
        PatrolStoreStatisticsDataColumnCountDTO patrolStoreStatisticsDataColumnCountDTO = dataDefTableEveryColumnCountMap.get(dataTableId);

        if(patrolStoreStatisticsDataColumnCountDTO != null){
            return patrolStoreStatisticsDataColumnCountDTO.getTotalColumnCount();
        }
        return 0;
    }

    private boolean getTableViewResultAuth(Long tableId, String userId, boolean isAdmin, Map<String, TbMetaTableUserAuthDO> tableAuthMap) {
        if(isAdmin){
            return true;
        }
        TbMetaTableUserAuthDO allUser = tableAuthMap.get(tableId + Constants.MOSAICS + "all_user_id");
        TbMetaTableUserAuthDO userAuth = tableAuthMap.get(tableId + Constants.MOSAICS + userId);
        Boolean allUserView = Optional.ofNullable(allUser).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        Boolean userAuthView = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        return userAuthView || allUserView;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean stopTask(String enterpriseId, StopTaskDTO stopTaskDTO,EnterpriseConfigDO enterpriseConfig) {
        log.info("stopTask enterpriseId：{},stopTaskDTO:{}",enterpriseId,JSONObject.toJSONString(stopTaskDTO));
        if (StringUtils.isBlank(enterpriseId)){
            throw new ServiceException(ErrorCodeEnum.EID_NOT_EXIST);
        }
        if (Objects.isNull(stopTaskDTO)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //停止任务
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, stopTaskDTO.getParentTaskId());
        if (taskParentDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        if(taskParentDO.getStatusType() == 0){
            throw new ServiceException(ErrorCodeEnum.TASK_IS_STOP);
        }
        //父任务切换为【已停止】状态
        taskParentMapper.stopTask(enterpriseId,stopTaskDTO.getParentTaskId());
        String requestId = MDC.get(Constants.REQUEST_ID);
        CompletableFuture.runAsync(()->{asyncStopTask(enterpriseId, taskParentDO, enterpriseConfig, requestId);});
        return Boolean.TRUE;
    }

    private void asyncStopTask(String enterpriseId, TaskParentDO taskParentDO, EnterpriseConfigDO enterpriseConfig, String requestId){
        MDCUtils.put(Constants.REQUEST_ID, requestId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        Long unifyTaskId = taskParentDO.getId();
        List<TaskSubDO> subTask = taskSubMapper.getUnFinishHandleUserIds(enterpriseId,unifyTaskId);
        //只删除待处理的任务
        List<TaskStoreDO> taskStoreDOS = unifyTaskStoreService.selectByUnifyTaskId(enterpriseId,unifyTaskId);
        for (TaskStoreDO taskStoreDO : taskStoreDOS) {
            TbDisplayDeleteParam tbDisplayDeleteParam = new TbDisplayDeleteParam();
            tbDisplayDeleteParam.setTaskStoreId(taskStoreDO.getId());
            //待审批中的门店任务只删除待办表
            if (!UnifyNodeEnum.FIRST_NODE.getCode().equals(taskStoreDO.getNodeNo()) && !UnifyNodeEnum.END_NODE.getCode().equals(taskStoreDO.getNodeNo())) {
                taskSubMapper.updateSubStatusComplete(enterpriseId, taskStoreDO.getUnifyTaskId(), taskStoreDO.getStoreId(), taskStoreDO.getLoopCount());
                continue;
            }
            CurrentUser user = UserHolder.getUser();
            if (taskStoreDO.getTaskType().equals(TB_DISPLAY_TASK.getCode())){//陈列任务删除子任务
                tbDisplayTableRecordService.deleteRecord(enterpriseId,tbDisplayDeleteParam,user,"notDone", enterpriseConfig);
            }else {//其他类型删除子任务
                deleteRecord(enterpriseId,tbDisplayDeleteParam,user,"notDone", enterpriseConfig);
            }
        }
        String scheduleId = taskParentDO.getScheduleId();
        if (taskParentDO.getRunRule().equals(TaskRunRuleEnum.LOOP.getCode()) && StringUtils.isNotBlank(scheduleId)){
            Boolean success = scheduleService.deleteSchedule(enterpriseId, scheduleId);
            if (!success) {
                log.error("定时调度器删除失败，enterpriseId={},scheduleId={}", enterpriseId, scheduleId);
                //throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "定时调度器删除失败");
            }
        }
        //移除待办
        List<Long> subTaskIdList = ListUtils.emptyIfNull(subTask).stream().map(TaskSubDO::getId).collect(Collectors.toList());
        log.info("移除待办的subTaskIdList：{}",JSONObject.toJSONString(subTaskIdList));
        if (CollectionUtils.isNotEmpty(subTaskIdList)){
            log.info("into cancelUpcoming");
            cancelUpcoming(enterpriseId,subTaskIdList,enterpriseConfig.getDingCorpId(),enterpriseConfig.getAppType());
        }
        // 发新任务取消待办
        if (TaskTypeEnum.isCombineNoticeTypes(taskParentDO.getTaskType()) && CollectionUtils.isNotEmpty(subTask)) {
            Map<Long, Map<String, List<TaskSubDO>>> group = CollStreamUtil.groupBy2Key(subTask, TaskSubDO::getLoopCount, TaskSubDO::getNodeNo);
            group.forEach((loopCount, map) -> {
                map.forEach((nodeNo, taskSubList) -> {
                    List<String> userIds = CollStreamUtil.toList(taskSubList, TaskSubDO::getHandleUserId);
                    unifyTaskService.cancelCombineUpcoming(enterpriseId, taskParentDO.getId(), loopCount, null, nodeNo, userIds, enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType());
                });
            });
        }
    }

    /**
     * 独立事物处理
     * @param enterpriseId
     */
    @Override
    public void cancelUpcoming(String enterpriseId, List<Long> subTaskIdList, String dingCorpId, String appType) {
        //重新分配的时候处理待办
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("unifyTaskSubIdList", subTaskIdList);
        jsonObject.put("appType", appType);

        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    @Override
    public Map<String, Object> getTaskDetail(String enterpriseId, Long businessId, EnterpriseConfigDO enterpriseConfig) {
        Map<String, Object> resultMap = new HashMap<>();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        String createUserName = enterpriseUserDao.selectNameIgnoreActiveByUserId(enterpriseId, tbPatrolStoreRecordDO.getCreateUserId());
        resultMap.put("createUserName",createUserName);
        resultMap.put("createTime",tbPatrolStoreRecordDO.getCreateTime());
        resultMap.put("taskType",TaskTypeEnum.getByCode(tbPatrolStoreRecordDO.getPatrolType()).getDesc());
        if (tbPatrolStoreRecordDO.getTaskId() == 0){
            EnterpriseStoreCheckSettingVO enterpriseStoreCheckSettingVO = enterpriseStoreCheckSettingService.queryEnterpriseStoreCheckSettingVO(enterpriseId);
            resultMap.put("isOpenSummary",enterpriseStoreCheckSettingVO.getAutonomyOpenSummary());
            resultMap.put("isOpenAutograph",enterpriseStoreCheckSettingVO.getAutonomyOpenSignature());
        }else if (tbPatrolStoreRecordDO.getTaskId() != null){
            TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, tbPatrolStoreRecordDO.getTaskId());
            JSONObject taskInfoJsonObj = JSON.parseObject(taskParentDO.getTaskInfo());
            JSONObject patrolStoreDefined = taskInfoJsonObj.getJSONObject("patrolStoreDefined");
            Boolean isOpenSummary,isOpenAutograph;
            if (patrolStoreDefined != null) {
                //巡店总结
                isOpenSummary = patrolStoreDefined.getBoolean("isOpenSummary");
                //巡店签名
                isOpenAutograph = patrolStoreDefined.getBoolean("isOpenAutograph");
                resultMap.put("isOpenSummary",isOpenSummary);
                resultMap.put("isOpenAutograph",isOpenAutograph);

        }
    }
        return resultMap;
}

    @Override
    public List<Long> deleteRecords(String enterpriseId, TbDisplayDeleteParam param, CurrentUser currentUser, String isDone) {
        List<Long> ids = param.getIds();
        if (CollectionUtils.isEmpty(ids)){
            throw new ServiceException("id不能为空");
        }
        if (ids.size()>50){
            throw new ServiceException("一次最多删除50条记录");
        }
        List<Long> failIds=Lists.newArrayList();
        DataSourceHelper.reset();
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        EnterpriseConfigDO config = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        for (Long id : ids) {
            TbDisplayDeleteParam data = new TbDisplayDeleteParam();
            data.setTaskStoreId(id);
            Future<Boolean> submit = deleteThread.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(currentUser.getDbName());
                deleteRecord(enterpriseId, data, currentUser, isDone, config);
                return Boolean.TRUE;
            });
            try {
                Boolean flag = submit.get();
                if (flag==null){
                    log.error("删除失败",id);
                    failIds.add(id);
                }
            } catch (Exception e) {
                log.error("删除失败",e);
                failIds.add(id);
            }
        }
        return failIds;
    }

    /**
     * 设置AI字段
     */
    private void setAiField(TbDataStaTableColumnDO columnDO,
                            TbDataStaColumnExtendInfoDO updateColumnExtendInfo,
                            AIResolveDTO aiResolveDTO) {
        // 成功返回
        TbMetaColumnResultDO matchResult = aiResolveDTO.getColumnResult();
        String comment = aiResolveDTO.getAiComment();

        // 设置原表数据
        columnDO.setCheckResult(matchResult.getMappingResult());
        columnDO.setCheckResultId(matchResult.getId());
        columnDO.setCheckResultName(matchResult.getResultName());
        columnDO.setCheckText(comment);
        columnDO.setCheckScore(aiResolveDTO.getAiScore());

        // 设置扩展表字段
        updateColumnExtendInfo.setAiCheckResult(matchResult.getMappingResult());
        updateColumnExtendInfo.setAiCheckResultId(matchResult.getId());
        updateColumnExtendInfo.setAiCheckResultName(matchResult.getResultName());
        updateColumnExtendInfo.setAiCheckPics(aiResolveDTO.getAiImageUrl()); // 如果有返回图，设置
        updateColumnExtendInfo.setAiCheckText(comment);
        updateColumnExtendInfo.setAiCheckScore(aiResolveDTO.getAiScore());

        updateColumnExtendInfo.setUpdateUserId(Constants.AI);
        updateColumnExtendInfo.setUpdateTime(new Date());
    }



}

