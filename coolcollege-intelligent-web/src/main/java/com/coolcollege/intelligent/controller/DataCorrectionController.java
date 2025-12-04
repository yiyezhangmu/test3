package com.coolcollege.intelligent.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.SendResult;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.passenger.FlowTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.vo.AuthMsg;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.ai.AiModelSceneMapper;
import com.coolcollege.intelligent.dao.ai.EnterpriseModelAlgorithmMapper;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.dataCorrection.DataCorrectionMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.metatable.*;
import com.coolcollege.intelligent.dao.passengerflow.PassengerFlowRecodeMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreHistoryMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.platform.EnterpriseStoreRequiredMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordExpandMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.data.correction.DataCorrectionService;
import com.coolcollege.intelligent.dto.OpStoreAndRegionDTO;
import com.coolcollege.intelligent.facade.*;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateUserAuthDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionOrderDTO;
import com.coolcollege.intelligent.facade.dto.openApi.SongXiaDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelSceneDO;
import com.coolcollege.intelligent.model.ai.entity.EnterpriseModelAlgorithmDO;
import com.coolcollege.intelligent.model.dataCorrection.BaiduChangeGaodeDTO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.openApi.request.SongXiaOpenApiRequest;
import com.coolcollege.intelligent.model.passengerflow.PassengerFlowRecordDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleJobRequest;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.unifytask.*;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifySubStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.workFlow.WorkflowDealDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.producer.util.RocketMqUtil;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.rpc.license.LicenseApiService;
import com.coolcollege.intelligent.service.ai.PatrolAIService;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.dingSync.DingUserSyncService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.form.FormInitializeService;
import com.coolcollege.intelligent.service.metatable.TbMetaQuickColumnService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.passengerflow.JieFengApiService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.question.QuestionParentUserMappingService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywxSync.QywxUserSyncService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.songxia.SongXiaService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.dingtalk.api.response.OapiUserListidResponse;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * describe: 平台库数据订正
 *
 * @author zhouyiping
 * @date 2020/09/21
 */
@RestController
@RequestMapping({"/v2/data/correction", "/v3/data/correction"})
@BaseResponse
@Slf4j
public class DataCorrectionController {

    @Resource
    private EnterpriseStoreRequiredMapper enterpriseStoreRequiredMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private FormInitializeService formInitializeService;
    @Autowired
    private DataCorrectionService dataCorrectionService;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private EnterpriseStoreSettingMapper storeSettingMapper;

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private StoreMapper storeMapper;
    @Autowired
    private DeviceService deviceService;
    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;
    @Autowired
    private AliyunService aliyunService;
    @Autowired
    private SyncSingleUserFacade syncSingleUserFacade;
    @Autowired
    private SyncUserFacade syncUserFacade;
    @Autowired
    private SyncRoleFacade syncRoleFacade;
    @Autowired
    private DingService dingService;
    @Autowired
    private DingTalkClientService dingTalkClientService;
    @Resource
    private RegionMapper regionMapper;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;


    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private DingDeptSyncService dingDeptSyncService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private RegionService regionService;

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseUserMappingService enterpriseUserMappingService;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private EnterpriseUserAppMenuMapper enterpriseUserAppMenuMapper;
    @Resource
    private ChatService chatService;

    @Resource
    private QywxUserSyncService qywxUserSyncService;

    @Resource
    private SyncDeptFacade syncDeptFacade;

    @Autowired
    private UnifyTaskStoreService unifyTaskStoreService;

    @Autowired
    private PatrolStoreService patrolStoreService;

    @Autowired
    private PatrolStoreRecordsService patrolStoreRecordsService;

    @Resource
    private TaskSubMapper taskSubMapper;

    @Resource
    private TaskParentMapper taskParentMapper;

    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private DingUserSyncService dingUserSyncService;

    @Resource
    private UserAuthMappingService userAuthMappingService;

    @Resource
    private LicenseApiService licenseApiService;

    @Resource
    private PassengerFlowRecodeMapper passengerFlowRecodeMapper;

    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource(name = "syncThreadPool")
    private ThreadPoolTaskExecutor syncThreadPoolTaskExecutor;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;
    @Resource
    private SyncStoreFacade syncStoreFacade;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private TbQuestionRecordMapper tbQuestionRecordMapper;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private TbQuestionRecordExpandMapper tbQuestionRecordExpandMapper;
    @Resource
    private QuestionParentUserMappingService questionParentUserMappingService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private StoreDao storeDao;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TbPatrolStoreHistoryMapper tbPatrolStoreHistoryMapper;
    @Resource
    private TbMetaColumnCategoryMapper tbMetaColumnCategoryMapper;
    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;
    @Resource
    private TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private TaskSopMapper taskSopMapper;
    @Resource
    private StoreService storeService;
    @Autowired
    private StoreWorkService storeWorkService;
    @Autowired
    private UserPersonInfoService userPersonInfoService;
    @Autowired
    private PatrolAIService patrolAIService;
    @Resource
    private SysRoleDao sysRoleDao;

    @Resource
    private TbMetaDisplayTableColumnMapper metaDisplayTableColumnMapper;

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    private FsService fsService;

    @Resource
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TbMetaQuickColumnService tbMetaQuickColumnService;
    @Resource
    private SongXiaService songXiaService;
    @Resource
    private DataCorrectionMapper dataCorrectionMapper;
    @Resource
    private ScheduleService scheduleService;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @GetMapping("/initEnterpriseSetting")
    public ResponseResult initEnterpriseSetting(@RequestParam("eid") String eid) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseMapper.getEnterpriseSetting(eid);
        if (enterpriseSetting == null) {
            EnterpriseSettingDO enterpriseSettingDO = new EnterpriseSettingDO();
            enterpriseSettingDO.setEnterpriseId(eid);
            enterpriseSettingDO.setManualTrain(false);
            enterpriseSettingDO.setCreateTime(System.currentTimeMillis());
            enterpriseSettingDO.setUpdateTime(System.currentTimeMillis());
            enterpriseMapper.saveOrUpdateSettings(eid, enterpriseSettingDO);
        }
        Long now = System.currentTimeMillis();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        if (storeCheckSettingDO == null) {
            storeCheckSettingDO = new EnterpriseStoreCheckSettingDO();
            storeCheckSettingDO.setCreateTime(now);
            storeCheckSettingMapper.insertOrUpdate(eid, storeCheckSettingDO);
        }
        EnterpriseStoreSettingDO storeSettingDO = storeSettingMapper.getEnterpriseStoreSetting(eid);
        if (storeSettingDO == null) {
            storeSettingDO = new EnterpriseStoreSettingDO();
            storeSettingDO.setCreateTime(now);
            storeSettingDO.setStoreLicenseEffectiveTime(Constants.THIRTY_DAY);
            storeSettingDO.setUserLicenseEffectiveTime(Constants.THIRTY_DAY);
            storeSettingMapper.insertOrUpdate(eid, storeSettingDO);
        }
        List<EnterpriseStoreRequiredDO> storeRequired = enterpriseStoreRequiredMapper.getStoreRequired(eid);
        if (CollectionUtils.isEmpty(storeRequired)) {
            List<EnterpriseStoreRequiredDO> requiredList = new ArrayList<>();
            requiredList.add(new EnterpriseStoreRequiredDO(eid, "store_name", "门店名称"));
            enterpriseStoreRequiredMapper.batchInsertStoreRequired(eid, requiredList);
        }
        return ResponseResult.success(true);
    }

    @GetMapping("/required")
    public ResponseResult storeRequired() {
        List<EnterpriseStoreRequiredDO> storeRequiredDOList = enterpriseStoreRequiredMapper.selectStoreRequiredAll();
        List<String> storeEnterpriseList = ListUtils.emptyIfNull(storeRequiredDOList)
                .stream()
                .map(EnterpriseStoreRequiredDO::getEnterpriseId)
                .collect(Collectors.toList());
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        //现在不存的配置的公司的ID
        List<String> enterpriseIdList = ListUtils.emptyIfNull(enterpriseConfigList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .map(EnterpriseConfigDO::getEnterpriseId)
                .filter(data -> !(storeEnterpriseList.contains(data)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enterpriseIdList)) {
            return ResponseResult.success("没有数据需要订正");
        }
        List<EnterpriseStoreRequiredDO> enterpriseStoreRequiredDOList = new LinkedList<>();
        enterpriseIdList.forEach(data -> {

            EnterpriseStoreRequiredDO storeName = new EnterpriseStoreRequiredDO(data, "store_name", "门店名称");
            EnterpriseStoreRequiredDO shopowner = new EnterpriseStoreRequiredDO(data, "shopowner", "店长");
            enterpriseStoreRequiredDOList.add(storeName);
            enterpriseStoreRequiredDOList.add(shopowner);
        });
        enterpriseStoreRequiredMapper.batchInsertStoreRequiredByInit(enterpriseStoreRequiredDOList);
        return ResponseResult.success(true);


    }

    @GetMapping("/setting")
    public ResponseResult settingCorrection(@RequestParam("eid") String eid) {
        formInitializeService.defaultEnterpriseSetting(eid, AppTypeEnum.DING_DING.getValue());
        return ResponseResult.success(true);
    }

    @GetMapping("/setting/all/view")
    public ResponseResult settingCorrectionAllView() {

        List<EnterpriseSettingDO> enterpriseSettingAll = enterpriseMapper.getEnterpriseSettingAll();
        List<String> settingEnterpriseIdList = ListUtils.emptyIfNull(enterpriseSettingAll)
                .stream()
                .map(EnterpriseSettingDO::getEnterpriseId)
                .collect(Collectors.toList());
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        List<String> enterpriseIdList = ListUtils.emptyIfNull(enterpriseConfigList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .map(EnterpriseConfigDO::getEnterpriseId)
                .collect(Collectors.toList());
        List<String> collect = enterpriseIdList.stream()
                .filter(data -> {
                    if (CollectionUtils.isEmpty(settingEnterpriseIdList)) {
                        return true;
                    }
                    return !(settingEnterpriseIdList.contains(data));
                })
                .collect(Collectors.toList());
        return ResponseResult.success(collect);
    }

    @GetMapping("/setting/all")
    public ResponseResult settingCorrectionAll() {

        List<EnterpriseSettingDO> enterpriseSettingAll = enterpriseMapper.getEnterpriseSettingAll();
        List<String> settingEnterpriseIdList = ListUtils.emptyIfNull(enterpriseSettingAll)
                .stream()
                .map(EnterpriseSettingDO::getEnterpriseId)
                .collect(Collectors.toList());
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        List<String> enterpriseIdList = ListUtils.emptyIfNull(enterpriseConfigList)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .map(EnterpriseConfigDO::getEnterpriseId)
                .filter(data -> {
                    if (CollectionUtils.isEmpty(settingEnterpriseIdList)) {
                        return true;
                    }
                    return !(settingEnterpriseIdList.contains(data));
                })
                .collect(Collectors.toList());
        ListUtils.emptyIfNull(enterpriseIdList).forEach(data -> formInitializeService.defaultEnterpriseSetting(data, AppTypeEnum.DING_DING.getValue()));
        return ResponseResult.success(true);
    }

    @GetMapping("/auth/menu")
    public ResponseResult authMenuChange() {
        dataCorrectionService.authMenuChange();
        return ResponseResult.success(true);
    }

    @GetMapping("/role/fix")
    public ResponseResult roleFix() {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        Map<String, List<EnterpriseConfigDO>> collect = enterpriseConfigDOList.stream()
                .collect(
                        Collectors.groupingBy(
                                EnterpriseConfigDO::getDbName/*, Collectors.counting()*/
                        )
                );
        AtomicInteger count = new AtomicInteger(0);
        for (Map.Entry<String, List<EnterpriseConfigDO>> entry : collect.entrySet()) {
            dataCorrectionService.roleDuplicateFix(entry.getKey(), entry.getValue(), count);
        }
        return ResponseResult.success(true);
    }

    @GetMapping("/store/regionId")
    public ResponseResult fixStoreRegionId() {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        Map<String, List<EnterpriseConfigDO>> collect = enterpriseConfigDOList.stream()
                .collect(
                        Collectors.groupingBy(
                                EnterpriseConfigDO::getDbName/*, Collectors.counting()*/
                        )
                );
        AtomicInteger count = new AtomicInteger(0);
        for (Map.Entry<String, List<EnterpriseConfigDO>> entry : collect.entrySet()) {
            dataCorrectionService.roleDuplicateFix(entry.getKey(), entry.getValue(), count);
        }
        return ResponseResult.success(true);
    }

    private String splitStoreName(String storeName) {
        String replace = StringUtils.replace(storeName, " ", "");
        if (storeName.length() > 8) {
            String substring = replace.substring(replace.length() - 8);
            substring = StringUtils.replace(substring, "(", "");
            substring = StringUtils.replace(substring, ")", "");
            substring = StringUtils.replace(substring, "（", "");
            substring = StringUtils.replace(substring, "）", "");
            return substring;
        }
        return null;
    }


    /**
     * 通过父任务id发送未完成通知
     *
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/notice/sendUnifyTaskDing")
    public ResponseResult sendUnifyTask(@RequestParam(value = "enterpriseId") String enterpriseId,
                                        @RequestParam(value = "taskId") Long taskId) {
        log.info(enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(unifyTaskService.sendUnifyTaskDing(enterpriseId, taskId, true));
    }

    /**
     * 通过父任务id发送未完成通知
     *
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/notice/sendUnifyTaskTestDing")
    public ResponseResult sendUnifyTaskTestDing(@RequestParam(value = "enterpriseId") String enterpriseId,
                                                @RequestParam(value = "taskId") Long taskId) {
        log.info(enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(unifyTaskService.sendUnifyTaskTestDing(enterpriseId, taskId));
    }

    @PostMapping(path = "/baidu/gaode")
    public ResponseResult baiduChangeGaode(@RequestParam(value = "enterpriseId") String enterpriseId,
                                           @RequestBody List<BaiduChangeGaodeDTO> baiduChangeGaodeDTOList) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        dataCorrectionService.baiduChangeGaode(enterpriseId, baiduChangeGaodeDTOList);
        return ResponseResult.success(true);
    }


    /**
     * 通补发任务钉钉工作通知
     *
     * @param enterpriseId
     * @param taskId
     * @return
     */
    @GetMapping(path = "/notice/reissueDingNotice")
    public ResponseResult reissueDingNotice(@RequestParam(value = "enterpriseId") String enterpriseId,
                                            @RequestParam(value = "taskId") Long taskId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        unifyTaskService.reissueDingNotice(enterpriseId, taskId, null, null, true);
        return ResponseResult.success(null);
    }

    @GetMapping(path = "/notice/reissueDingNoticeForStoreIdAndLoopCount")
    public ResponseResult reissueDingNotice(@RequestParam(value = "enterpriseId") String enterpriseId,
                                            @RequestParam(value = "taskId") Long taskId,
                                            @RequestParam(value = "storeId") String storeId,
                                            @RequestParam(value = "loopCount") Long loopCount) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        unifyTaskService.reissueDingNotice(enterpriseId, taskId, storeId, loopCount, true);
        return ResponseResult.success(null);
    }

    /**
     * 删除redis key
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/sync/removeKey")
    public ResponseResult removeKey(@RequestParam(value = "enterpriseId") String enterpriseId) {
        //同步失败删除拦截key
        redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(enterpriseId));
        return ResponseResult.success(null);
    }

    /**
     * 添加定时任务
     *
     * @param
     * @return
     */
    @GetMapping("/sync/setDingSyncScheduler")
    public void setDingSyncScheduler(@RequestParam(value = "enterpriseId") String enterpriseId) {
        dingDeptSyncService.setDingSyncScheduler(enterpriseId, UserHolder.getUser().getUserId(), UserHolder.getUser().getName());
    }



    /**
     * 根据部门同步
     *
     * @param deptId
     * @param eid
     * @throws
     * @Param:
     * @return: java.lang.String
     * @Author: xugangkun
     * @Date: 2021/4/9 15:39
     */
    @PostMapping(value = "/syncDeptUser")
    public OapiUserListidResponse.ListUserByDeptResponse syncDeptUser(@RequestParam(value = "deptId") Long deptId,
                                                                      @RequestParam(value = "eid") String eid) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        String token = dingService.getAccessToken(config.getDingCorpId(), config.getAppType());
        OapiUserListidResponse.ListUserByDeptResponse response = dingTalkClientService.getDeptUserIdList(String.valueOf(deptId), token);
        log.info("获取部门下用户id {} ,返回用户id {} ", deptId, response.getUseridList());
        for (String userId : response.getUseridList()) {
            try {
                syncSingleUserFacade.syncUser(userId, config, setting, true);
            } catch (Exception e) {
                log.error("fullSyncUser,当前用户同步失败 {} ", userId, e);
            }
        }
        return response;
    }

    /**
     * 同步门店通部门用户
     *
     * @param deptId
     * @param eid
     * @return
     * @throws ApiException
     */
    @PostMapping(value = "/syncOnePartyDeptUser")
    public OapiUserListidResponse.ListUserByDeptResponse syncOnePartyDeptUser(@RequestParam(value = "deptId") Long deptId,
                                                                              @RequestParam(value = "eid") String eid) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String token = dingService.getAccessToken(config.getDingCorpId(), config.getAppType());
        OapiUserListidResponse.ListUserByDeptResponse response = dingTalkClientService.getDeptUserIdList(String.valueOf(deptId), token);
        log.info("获取部门下用户id {} ,返回用户id {} ", deptId, response.getUseridList());
        for (String userId : response.getUseridList()) {
            try {
                syncSingleUserFacade.asyncOnePartyUser(eid, config.getDbName(), userId, config.getDingCorpId(), config.getAppType());
            } catch (Exception e) {
                log.error("syncOnePartyDeptUser,当前用户同步失败 {} ", userId, e);
            }
        }
        //钉钉指定部门与子部门列表
        List<SysDepartmentDO> subDepts = dingService.getSubDepts(deptId.toString(), null, config.getDingCorpId(), config.getAppType());
        if (CollectionUtils.isNotEmpty(subDepts)) {
            subDepts.forEach(departmentDO -> {
                OapiUserListidResponse.ListUserByDeptResponse deptResponse = null;
                try {
                    deptResponse = dingTalkClientService.getDeptUserIdList(departmentDO.getId(), token);
                    log.info("获取部门下用户id {} ,返回用户id {} ", deptId, deptResponse.getUseridList());
                    for (String userId : deptResponse.getUseridList()) {
                        try {
                            syncSingleUserFacade.asyncOnePartyUser(eid, config.getDbName(), userId, config.getDingCorpId(), config.getAppType());
                        } catch (Exception e) {
                            log.error("syncOnePartyDeptUser,当前用户同步失败 {} ", userId, e);
                        }
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            });
        }
        return response;
    }


    /**
     * 同步门店通用户信息 按指定用户同步
     * @param eid
     * @param userIds
     * @return
     */
    @GetMapping("/syncOnePartyUserByUserIds")
    public ResponseResult syncOnePartyUserByUserIds(@RequestParam(value = "eid") String eid,
                                                 @RequestParam(value = "userIds", required = false) List<String> userIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToMy();
        for (String userId : userIds) {
            try {
                syncSingleUserFacade.asyncOnePartyUser(eid, config.getDbName(), userId, config.getDingCorpId(), config.getAppType());
            } catch (Exception e) {
                log.error("syncOnePartyDeptUser,当前用户同步失败 {} ", userId, e);
            }
        }
        return ResponseResult.success();
    }




    /**
     * 根据用户同步
     *
     * @param eid
     * @param userId
     * @throws
     * @Param:
     * @return: java.lang.String
     * @Author: xugangkun
     * @Date: 2021/4/9 15:39
     */
    @PostMapping(value = "/syncUser")
    public String syncUser(@RequestParam(value = "eid") String eid,
                           @RequestParam(value = "userId") String userId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        syncSingleUserFacade.syncUser(userId, config, setting, true);
        return userId;
    }

    /**
     * 根据用户同步
     *
     * @param eid
     * @param userId
     * @throws
     * @Param:
     * @return: java.lang.String
     * @Author: xugangkun
     * @Date: 2021/4/9 15:39
     */
    @PostMapping(value = "/syncQwUser")
    public String syncQwUser(@RequestParam(value = "eid") String eid,
                             @RequestParam(value = "userId") String userId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String token = chatService.getPyAccessToken(config.getDingCorpId(), config.getAppType());
        qywxUserSyncService.syncWeComUser(config.getDingCorpId(), userId, token, eid, config.getDbName(), config.getAppType());
        return userId;
    }

    /**
     * 同步部门下用户
     *
     * @param eid
     * @param deptId
     * @throws
     * @Param:
     * @return: java.lang.String
     * @Author: xugangkun
     * @Date: 2021/4/9 15:39
     */
    @PostMapping(value = "/syncQwDeptUser")
    public Long syncQwDeptUser(@RequestParam(value = "eid") String eid,
                               @RequestParam(value = "deptId") Long deptId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String token = chatService.getPyAccessToken(config.getDingCorpId(), config.getAppType());
        boolean flag = chatService.checkWxCorpIdFromRedis(config.getDingCorpId());
        List<EnterpriseUserRequest> deptUsers = chatService.getDeptUsers(config.getDingCorpId(), String.valueOf(deptId), token, flag, config.getAppType());
        log.info("获取部门下用户的部门id {} ,返回用户详情列表 {} ", deptId, JSONObject.toJSONString(deptUsers));
        List<EnterpriseUserDO> collect = ListUtils.emptyIfNull(deptUsers)
                .stream()
                .map(EnterpriseUserRequest::getEnterpriseUserDO)
                .collect(Collectors.toList());
        for (EnterpriseUserDO deptUser : collect) {
            try {
                //切除获得userId"_"后半部
                String userId = deptUser.getUserId().substring(config.getDingCorpId().length() + 1);
                qywxUserSyncService.syncWeComUser(config.getDingCorpId(), userId, token, eid, config.getDbName(), config.getAppType());
            } catch (Exception e) {
                log.error("fullSyncUser,当前用户同步失败 {} ", deptUser.getUserId(), e);
            }
        }

        return deptId;
    }


    @PostMapping("/syncDeptAll")
    public ResponseResult syncDeptAll(@RequestParam(value = "eid") String eid) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        dingDeptSyncService.syncDingDepartmentAll(eid, config.getDingCorpId());
        return ResponseResult.success(null);
    }

    @GetMapping("/printLog")
    public void printLog() {
        log.info("这是info日志  log");
        log.error("这是error日志  log");
        throw new ServiceException("222");
    }

    /**
     * 订正全部历史区域冗余数据（修改数据需要）
     *
     * @return
     */
    @GetMapping("/region/all")
    public ResponseResult<Boolean> regionPathAll(@RequestParam(value = "eidStr", required = false) String eidStr,
                                                 @RequestParam(value = "skipEidStr", required = false) String skipEidStr) {
        Long rootId = 1L;
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bignowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))

                .forEach(enterpriseConfigDO -> {
                    String enterpriseId = enterpriseConfigDO.getEnterpriseId();
                    long nowDate = System.currentTimeMillis();
                    log.info("初始化区域eid={}", enterpriseId);
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    regionService.updateRegionPathAll(enterpriseId, 1L);
                    log.info("初始化区域eid={},话费的时间:{}ms", enterpriseId, System.currentTimeMillis() - nowDate);
                });
        log.info("初始化区域总花费时间:{}ms", System.currentTimeMillis() - bignowDate);
        return ResponseResult.success(true);

    }

    /**
     * 订正区域门店数量
     *
     * @return
     */
    @GetMapping("/region/storeNum/all")
    public ResponseResult<Boolean> regionStoreNumAll(@RequestParam(value = "eidStr", required = false) String eidStr,
                                                     @RequestParam(value = "skipEidStr", required = false) String skipEidStr) {
        Long rootId = 1L;

        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    String enterpriseId = enterpriseConfigDO.getEnterpriseId();
                    log.info("初始化区域门店数量eid={}", enterpriseId);
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    regionService.updateRecursionRegionStoreNum(enterpriseConfigDO.getEnterpriseId(), 1L);

                });
        return ResponseResult.success(true);

    }


    /**
     * 灰度环境中调用
     *
     * @return
     */
    @GetMapping("/ai/init")
    public ResponseResult<Boolean> aiInit(@RequestParam(value = "eidStr", required = false) String eidStr,
                                          @RequestParam(value = "skipEidStr", required = false) String skipEidStr) {
        DataSourceHelper.reset();
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    String eid = enterpriseConfigDO.getEnterpriseId();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    EnterpriseUserDTO ai = enterpriseUserMapper.getUserDetail(eid, AIEnum.AI_USERID.getCode());
                    if (ai == null) {
                        EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                        enterpriseUserDO.setId(AIEnum.AI_ID.getCode());
                        enterpriseUserDO.setName(AIEnum.AI_NAME.getCode());
                        enterpriseUserDO.setUserId(AIEnum.AI_USERID.getCode());
                        enterpriseUserDO.setMobile(AIEnum.AI_MOBILE.getCode());
                        enterpriseUserDO.setRoles(AIEnum.AI_ROLES.getCode());
                        enterpriseUserDO.setUnionid(AIEnum.AI_UUID.getCode());
                        enterpriseUserDO.setDepartments(AIEnum.AI_DEPARTMENT.getCode());
                        enterpriseUserDO.setIsAdmin(true);
                        enterpriseUserDO.setActive(true);
                        enterpriseUserDO.setSubordinateRange(UserSelectRangeEnum.ALL.getCode());
                        enterpriseUserDao.batchInsertOrUpdate(Collections.singletonList(enterpriseUserDO), eid);
                        Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());
                        EnterpriseUserRole enterpriseUserRole = new EnterpriseUserRole(roleIdByRoleEnum.toString(), AIEnum.AI_USERID.getCode());
                        sysRoleService.insertBatchUserRole(eid, Collections.singletonList(enterpriseUserRole));

                        DataSourceHelper.reset();
                        //添加用户企业关联信息
                        EnterpriseUserMappingDO enterpriseUserMappingDO = new EnterpriseUserMappingDO();
                        enterpriseUserMappingDO.setId(UUIDUtils.get32UUID());
                        enterpriseUserMappingDO.setUserId(enterpriseUserDO.getId());
                        enterpriseUserMappingDO.setEnterpriseId(eid);
                        enterpriseUserMappingDO.setUnionid(enterpriseUserDO.getUnionid());
                        enterpriseUserMappingDO.setUserStatus(enterpriseUserDO.getUserStatus());
                        enterpriseUserMappingDO.setCreateTime(new Date());
                        enterpriseUserMappingService.saveEnterpriseUserMapping(enterpriseUserMappingDO);
                    }
                });
        return ResponseResult.success(true);
    }

    /**
     * 订正全部历史区域冗余数据（修改数据需要）
     *
     * @return
     */
    @GetMapping("/region/redundance/all")
    public ResponseResult regionRedundanceAll(@RequestParam(value = "eidStr", required = false) String eidStr,
                                              @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                              @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("初始化冗余数据eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.syncRegionPath(enterpriseConfigDO.getEnterpriseId(), storeId, false, enterpriseConfigDO.getDbName());
                    log.info("初始化冗余数据eid={},花费的时间：{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);

                });
        log.info("初始化冗余数据总花费时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @GetMapping("/region/redundance/store")
    public ResponseResult regionRedundanceStore(@RequestParam(value = "eidStr", required = false) String eidStr,
                                                @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                                @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("初始化门店冗余数据eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.syncStoreRegionPath(enterpriseConfigDO.getEnterpriseId(), storeId);
                    log.info("初始化门店冗余数据eid={},花费的时间:{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);
                });
        log.info("初始化门店总花费的时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @GetMapping("/check/levelInfo")
    public ResponseResult checkLeveInfo() {

        DataSourceHelper.reset();
        List<EnterpriseStoreCheckSettingDO> allCheckList = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSettingAll();
        ListUtils.emptyIfNull(allCheckList)
                .forEach(data -> {
                    String levelInfo = "{\"levelList\":[{\"keyName\":\"excellent\",\"percent\":90},{\"keyName\":\"good\",\"percent\":80},{\"keyName\":\"eligible\",\"percent\":60},{\"keyName\":\"disqualification\",\"percent\":0}],\"open\":true}";
                    enterpriseStoreCheckSettingMapper.updateLevelInfo(data.getEnterpriseId(), levelInfo, data.getCheckResultInfo());
                });

        return ResponseResult.success(true);
    }

    @GetMapping("/baili/store/info")
    public ResponseResult bailiStoreInfo(@RequestParam(value = "oldEid", required = false) String oldEid,
                                         @RequestParam(value = "newEid", required = false) String newEid) {

        DataSourceHelper.reset();
        EnterpriseConfigDO xinyewu = enterpriseConfigMapper.selectByEnterpriseId(oldEid);
        DataSourceHelper.changeToSpecificDataSource(xinyewu.getDbName());
        List<StoreDTO> allStoresByLongitudeLatitude = storeMapper.getAllStoresByLongitudeLatitude(xinyewu.getEnterpriseId(), StoreIsDeleteEnum.EFFECTIVE.getValue());
        DataSourceHelper.reset();
        EnterpriseConfigDO balixinyewu = enterpriseConfigMapper.selectByEnterpriseId(newEid);
        DataSourceHelper.changeToSpecificDataSource(balixinyewu.getDbName());
        Map<String, StoreDTO> storeNumMap = ListUtils.emptyIfNull(allStoresByLongitudeLatitude)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getStoreNum()))
                .collect(Collectors.toMap(StoreDTO::getStoreNum, data -> data, (a, b) -> a));
        List<StoreDTO> allStoresByLongitudeLatitude1 = storeMapper.getAllStoresByLongitudeLatitude(balixinyewu.getEnterpriseId(), StoreIsDeleteEnum.EFFECTIVE.getValue());
        List<StoreDO> storeDOList = ListUtils.emptyIfNull(allStoresByLongitudeLatitude1)
                .stream()
                .map(data -> {
                            if (StringUtils.isNotBlank(data.getStoreNum()) && storeNumMap.get(data.getStoreNum()) != null) {
                                StoreDO storeDO = new StoreDO();
                                StoreDTO storeDTO = storeNumMap.get(data.getStoreNum());
                                storeDO.setStoreAddress(storeDTO.getStoreAddress());
                                storeDO.setStoreId(data.getStoreId());
                                storeDO.setLatitude(storeDTO.getLatitude());
                                storeDO.setLongitude(storeDTO.getLongitude());
                                storeDO.setLongitudeLatitude(storeDTO.getLongitudeLatitude());
                                if (StringUtils.isNotEmpty(storeDTO.getLongitudeLatitude())) {
                                    List<String> list = Arrays.asList(storeDTO.getLongitudeLatitude().split(","));
                                    storeDO.setAddressPoint("POINT(" + list.get(0) + " " + list.get(1) + ")");
                                }
                                return storeDO;
                            }
                            return null;
                        }
                ).filter(Objects::nonNull).collect(Collectors.toList());

        storeMapper.updateLongitudeLatitudeAndAddress(balixinyewu.getEnterpriseId(), storeDOList);

        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/device/storeBind", method = RequestMethod.GET)
    public ResponseResult deviceBindStoreId(@RequestParam(value = "eidStr", required = false) String eidStr,
                                            @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                            @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("初始化门店冗余数据eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.syncDeviceBindStoreId(enterpriseConfigDO.getEnterpriseId());
                    log.info("初始化门店冗余数据eid={},花费的时间:{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);
                });
        log.info("初始化门店总花费的时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/device/rootCorpId", method = RequestMethod.GET)
    public ResponseResult bindRootCorpId(@RequestParam(value = "eidStr", required = false) String eidStr,
                                         @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                         @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("订正vds数据eid={}", enterpriseConfigDO.getEnterpriseId());
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.syncRootCorpDevice(enterpriseConfigDO.getEnterpriseId());
                });
        log.info("订正vds总花费的时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/device/rootCorpId/delete", method = RequestMethod.GET)
    public ResponseResult deleteRootCorpId(@RequestParam(value = "rootCorpId") String rootCorpId) {

        DataSourceHelper.reset();
        dataCorrectionService.deleteRootCorpId(rootCorpId);
        return ResponseResult.success(true);
    }


    @GetMapping("/deal/user/status")
    public ResponseResult dealUserStatus(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        String cacheKeyPrefix = "dealUserStatus:";
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                String enterpriseIdKey = redisUtilPool.getString(cacheKeyPrefix + enterpriseId);
                if (StringUtils.isNotBlank(enterpriseIdKey)) {
                    continue;
                }
                //获取企业所有的用户
                int innerPageNum = 1;
                boolean innerHasNext = true;
                int totalCount = 0;
                while (innerHasNext) {
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                    PageHelper.startPage(innerPageNum, pageSize);
                    List<EnterpriseUserDO> enterpriseUserList = enterpriseUserMapper.getEnterpriseUserStatus(enterpriseId);
                    PageHelper.clearPage();
                    innerHasNext = enterpriseUserList.size() >= pageSize;
                    totalCount = totalCount + enterpriseUserList.size();
                    DataSourceHelper.reset();
                    log.info("userIds：{}", JSONObject.toJSONString(enterpriseUserList.stream().map(EnterpriseUserDO::getId).collect(Collectors.toList())));
                    for (EnterpriseUserDO enterpriseUser : enterpriseUserList) {
                        EnterpriseUserMappingDO enterpriseUserMapping = enterpriseUserMappingService.selectByEnterpriseIdAndUnionid(enterpriseId, enterpriseUser.getUnionid());
                        if (StringUtils.isBlank(enterpriseUser.getUnionid()) || Objects.isNull(enterpriseUser.getActive()) || !enterpriseUser.getActive()) {
                            if (Objects.nonNull(enterpriseUserMapping)) {
                                //删除映射关系
                                enterpriseUserMappingService.deleteUserMappingById(enterpriseUserMapping.getId());
                            }
                            continue;
                        }
                        if (Objects.nonNull(enterpriseUserMapping)) {
                            enterpriseUserMappingService.updateEnterpriseUserStatus(enterpriseUser.getUnionid(), enterpriseId, enterpriseUser.getUserStatus());
                        } else {
                            EnterpriseUserDO enterpriseConfigUser = enterpriseUserMapper.selectConfigUserByUnionid(enterpriseUser.getUnionid());
                            if (Objects.nonNull(enterpriseConfigUser)) {
                                enterpriseUserMappingService.insertEnterpriseUserMapping(enterpriseId, enterpriseConfigUser.getId(), enterpriseUser.getUnionid(), enterpriseUser.getUserStatus());
                            }
                        }
                    }
                    innerPageNum++;
                }
                redisUtilPool.setString(cacheKeyPrefix + enterpriseId, totalCount + "", 2 * 60 * 60);
            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/deal/user/delete")
    public ResponseResult deleteUserMapping(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        String cacheKeyPrefix = "deleteUserMapping:";
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                String enterpriseIdKey = redisUtilPool.getString(cacheKeyPrefix + enterpriseId);
                if (StringUtils.isNotBlank(enterpriseIdKey)) {
                    continue;
                }
                //获取企业所有的用户
                int innerPageNum = 1;
                boolean innerHasNext = true;
                int totalCount = 0;
                while (innerHasNext) {
                    DataSourceHelper.reset();
                    PageHelper.startPage(innerPageNum, pageSize);
                    List<EnterpriseUserMappingDO> enterpriseUserList = enterpriseUserMappingService.getUserMappingListByEnterpriseId(enterpriseId);
                    PageHelper.clearPage();
                    innerHasNext = enterpriseUserList.size() >= pageSize;
                    totalCount = totalCount + enterpriseUserList.size();
                    DataSourceHelper.reset();
                    for (EnterpriseUserMappingDO enterpriseUserMapping : enterpriseUserList) {
                        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                        EnterpriseUserDO enterpriseUser = enterpriseUserMapper.selectByUnionid(enterpriseId, enterpriseUserMapping.getUnionid());
                        if (Objects.isNull(enterpriseUser) || Objects.isNull(enterpriseUser.getActive()) || !enterpriseUser.getActive()) {
                            DataSourceHelper.reset();
                            enterpriseUserMappingService.deleteUserMappingById(enterpriseUserMapping.getId());
                        }
                    }
                    innerPageNum++;
                }
                redisUtilPool.setString(cacheKeyPrefix + enterpriseId, totalCount + "", 2 * 60 * 60);
            }
        }
        return ResponseResult.success();
    }


    @GetMapping("/get/user/count")
    public ResponseResult getUserCount(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        int totalCount = 0;
        Map<String, Integer> countMap = new HashMap<>();
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                Integer enterpriseUserCount = enterpriseUserMapper.getActiveUserCount(enterpriseId);
                countMap.put(enterpriseId, enterpriseUserCount);
                totalCount = totalCount + enterpriseUserCount;
            }
        }
        HashMap<String, Integer> sortMap = new LinkedHashMap<>();
        countMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> sortMap.put(ele.getKey(), ele.getValue()));
        log.info("count:{}", sortMap);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into enterprise_num1(`id`, `num`) values ");
        sortMap.forEach((k, v) -> {
            stringBuilder.append("('").append(k).append("',").append(" '").append(v).append("'),");
        });
        log.info("sql:{}", stringBuilder.toString());
        return ResponseResult.success(totalCount);
    }

    @GetMapping("/update/password")
    public ResponseResult updateUserPassword(@RequestParam("mobile") String mobile, @RequestParam("password") String password, @RequestParam("key") String key) {
        String smsCodeKey = SmsCodeTypeEnum.MODIFY_PWD + ":100000";
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!key.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseUserMapper.modifyPasswordByMobile(mobile, password));
    }

    @GetMapping("/cache/delete")
    public ResponseResult cacheDelete(@RequestParam("key") String key, @RequestParam("code") String code) {
        String smsCodeKey = SmsCodeTypeEnum.MODIFY_PWD + ":100001";
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!code.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        DataSourceHelper.reset();
        return ResponseResult.success(redisUtilPool.delKey(key));
    }


    /**
     * 百丽手动全量同步
     *
     * @param eid
     * @return
     */
    @PostMapping("/syncBailiAll")
    public ResponseResult syncBailiAll(@RequestParam(value = "eid") String eid,
                                       @RequestParam(value = "unitId") Integer unitId,
                                       @RequestParam(value = "regionId") Long regionId) {

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "未开启同步");
        }
        syncUserFacade.syncThirdOaAll(eid, "system", "system", enterpriseConfigDO, String.valueOf(unitId), null, null, regionId, null);
        return ResponseResult.success(null);
    }

    @PostMapping("/syncDingRoles")
    public ResponseResult syncDingRoles(@RequestParam(value = "eid") String eid) throws ApiException {

        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        if (Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_NOT_OPEN)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "未开启同步");
        }
        syncRoleFacade.syncDingRoles(eid);
        return ResponseResult.success(null);
    }


    @PostMapping("/syncDingOnePartyRoles")
    public ResponseResult syncDingOnePartyRoles(@RequestParam(value = "eid") String eid) throws ApiException {
        syncRoleFacade.syncDingOnePartyRoles(eid);
        return ResponseResult.success(null);
    }

    @PostMapping("/ehr/address")
    public ResponseResult syncEhrAddress(@RequestParam(value = "eid") String eid,
                                         @RequestParam(value = "unitId") Integer unitId,
                                         @RequestParam(value = "isChange", defaultValue = "0") Boolean isChange) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        dataCorrectionService.syncEhrAddress(eid, unitId, isChange);
        return ResponseResult.success(true);
    }

    /**
     * 客流 补充丢失的数据(切勿补充重新门店设备之前的日期数据)
     *
     * @param eid
     * @param time      yyyy-MM-dd HH:mm
     * @param deviceIds
     * @return
     */
    @GetMapping("/passenger/fix")
    public ResponseResult<Boolean> passengerFix(@RequestParam("eid") String eid,
                                                @RequestParam("time") String time,
                                                @RequestParam("deviceIds") String deviceIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        dataCorrectionService.passengerFix(eid, time, deviceIds);
        return ResponseResult.success(true);
    }

    /**
     * 给有业绩类型，无模板的企业设置通用模板
     *
     * @param eidStr
     * @param skipEidStr
     * @param storeId
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @author chenyupeng
     * @date 2021/11/1
     */
    @RequestMapping(value = "/device/setFormWork", method = RequestMethod.GET)
    public ResponseResult setFormWork(@RequestParam(value = "eidStr", required = false) String eidStr,
                                      @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                      @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("初始化业绩模板eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.setFormWork(enterpriseConfigDO.getEnterpriseId());
                    log.info("初始化业绩模板eid={},花费的时间:{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);
                });
        log.info("初始化业绩模板总花费的时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    /**
     * 巡店记录补漏
     *
     * @param eid
     * @param subTaskId
     * @param patrolType
     * @return
     */
    @PostMapping("/makeup/patrolMetaRecord")
    public ResponseResult makeUpPatrolMetaRecord(@RequestParam(value = "eid") String eid,
                                                 @RequestParam(value = "subTaskId") Long subTaskId,
                                                 @RequestParam(value = "patrolType") String patrolType) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        patrolStoreRecordsService.makeUpPatrolMetaRecord(eid, subTaskId, patrolType, enterpriseStoreSettingDO, null);
        return ResponseResult.success(true);
    }

    /**
     * 陈列记录补漏
     *
     * @param eid
     * @param subTaskId
     * @return
     */
    @PostMapping("/makeup/displayRecord")
    public ResponseResult makeUpDisplayRecord(@RequestParam(value = "eid") String eid,
                                              @RequestParam(value = "subTaskId") Long subTaskId) {


        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        TaskSubDO taskSubDO = taskSubMapper.selectSubTaskById(eid, subTaskId);
        if (taskSubDO == null) {
            throw new ServiceException(ErrorCodeEnum.TASK_NOT_EXIST);
        }
        List<TaskSubDO> subTaskDistinctList = new ArrayList<>();
        subTaskDistinctList.add(taskSubDO);
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(eid, taskSubDO.getUnifyTaskId());
        //子任务发布广播消息
        TaskMessageDTO taskMessage = new TaskMessageDTO(UnifyTaskConstant.TaskMessage.OPERATE_ADD, taskParentDO.getId(), taskParentDO.getTaskType(),
                taskParentDO.getCreateUserId(), System.currentTimeMillis(), JSON.toJSONString(subTaskDistinctList), eid, taskParentDO.getTaskInfo(), taskParentDO.getAttachUrl());
        unifyTaskService.sendTaskMessage(taskMessage);
        return ResponseResult.success(true);
    }


    /**
     * 删除自主巡店记录
     *
     * @param eid
     * @param supervisorId
     * @return
     */
    @PostMapping("/makeup/removePatrolRecord")
    public ResponseResult removePatrolRecord(@RequestParam(value = "eid") String eid,
                                             @RequestParam(value = "supervisorId") String supervisorId,
                                             @RequestParam(value = "beginTime") String beginTime,
                                             @RequestParam(value = "endTime") String endTime) {
        log.info("开始删除自主巡店: eid :{} supervisorId:{} beginTime:{} endTime:{}", eid, supervisorId, beginTime, endTime);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<TbPatrolStoreRecordDO> tbPatrolStoreRecordList = tbPatrolStoreRecordMapper.getRemoveAutonomyPatrolRecordList(eid, supervisorId, beginTime, endTime);

        if (CollectionUtils.isNotEmpty(tbPatrolStoreRecordList)) {
            List<Long> businessIdList = tbPatrolStoreRecordList.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
            Lists.partition(businessIdList, Constants.BATCH_INSERT_COUNT).forEach(businessId -> {
                log.info(" 删除自主巡店 businessId: {}", businessId);
                patrolStoreService.delPatrolStoreByBusinessIds(eid, businessId);
            });
        }
        log.info("删除自主巡店结束: eid :{} supervisorId:{} beginTime:{} endTime:{}", eid, supervisorId, beginTime, endTime);
        return ResponseResult.success(true);
    }


    @GetMapping("/updateUserMappingUnionid")
    public ResponseResult updateUserMappingUnionid() {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseUserMappingService.updateUserMappingUnionid());
    }

    /**
     * 新增门店区域化的key
     * 有值 需要把区域对应的门店也返回
     * 无  不需要返回门店
     *
     * @return
     */
    @GetMapping("/makeup/setShowStoreAuthKey")
    public ResponseResult setShowStoreAuthKey() {
        String showStoreAuthKey = redisConstantUtil.getShowStoreAuthKey();
        redisUtilPool.setString(showStoreAuthKey, String.valueOf(System.currentTimeMillis()));
        return ResponseResult.success(true);
    }

    @GetMapping("/makeup/getShowStoreAuthKey")
    public ResponseResult getShowStoreAuthKey() {
        String showStoreAuthKey = redisConstantUtil.getShowStoreAuthKey();
        String showStoreAuthValue = redisUtilPool.getString(showStoreAuthKey);
        return ResponseResult.success(showStoreAuthValue);
    }

    /**
     * 删除门店区域化的key
     * 有值 需要把区域对应的门店也返回
     * 无  不需要返回门店
     * <p>
     * 前端改造完 调用此方法
     *
     * @return
     */
    @GetMapping("/makeup/removeShowStoreAuthKey")
    public ResponseResult removeShowStoreAuthKey() {
        redisUtilPool.delKey(redisConstantUtil.getShowStoreAuthKey());
        return ResponseResult.success(true);
    }

    @GetMapping("/redis/removeHash")
    public ResponseResult removeHash(@RequestParam(value = "eidStr", required = false) String eidStr,
                                     @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                     @RequestParam(value = "mapKey") String mapKey,
                                     @RequestParam(value = "valueKey") String valueKey) {
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bignowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    redisUtilPool.hashDel(mapKey + enterpriseConfigDO.getEnterpriseId(), valueKey);
                });
        log.info("删除缓存总花费时间:{}ms", System.currentTimeMillis() - bignowDate);
        return ResponseResult.success(true);
    }

    @GetMapping("/redis/removeString")
    public ResponseResult removeString(@RequestParam(value = "eidStr", required = false) String eidStr,
                                       @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                       @RequestParam(value = "key") String key) {
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bignowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    redisUtilPool.delKey(key + enterpriseConfigDO.getEnterpriseId());
                });
        log.info("删除缓存总花费时间:{}ms", System.currentTimeMillis() - bignowDate);
        return ResponseResult.success(true);
    }

    @GetMapping("/setPassenger/record")
    public ResponseResult setPassengerRecord(@RequestParam("eid") String eid,
                                             @RequestParam("deviceList") List<String> deviceList,
                                             @RequestParam("month") Integer month) {
        DataSourceHelper.changeToMy();
        List<DeviceDO> deviceByDeviceIdList = deviceMapper.getDeviceByDeviceIdList(eid, deviceList);
        //昨天的00：00：00的时间戳
        LocalDateTime localDateTime = LocalDateTime.now();
        int year = localDateTime.getYear();
        int monthDays = getMonthDays(year, month);
        List<PassengerFlowRecordDO> passengerFlowRecordDOList = new ArrayList<>();
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(eid);
        Map<Long, String> sceneTypeMap = ListUtils.emptyIfNull(storeSceneList)
                .stream()
                .collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getSceneType, (a, b) -> a));
        for (int i = 1; i <= monthDays; i++) {
            for (int j = 0; j < deviceByDeviceIdList.size(); j++) {
                String yesterday = year + "-" + month + "-" + i;
                Date productDayTime = DateUtil.parse(yesterday, "yyyy-MM-dd");
                Integer inCount = 0;
                Integer outCount = 0;
                Integer inoutCount = 0;
                for (int k = 0; k < 24; k++) {
                    RandomUtil.randomInt(0, 100);
                    PassengerFlowRecordDO passengerFlowRecordDO = new PassengerFlowRecordDO();
                    DeviceDO deviceDO = deviceByDeviceIdList.get(j);
                    passengerFlowRecordDO.setStoreId(deviceDO.getBindStoreId());
                    passengerFlowRecordDO.setRegionPath(deviceDO.getRegionPath());
                    passengerFlowRecordDO.setDeviceId(deviceDO.getDeviceId());
                    passengerFlowRecordDO.setHasChildDevice(deviceDO.getHasChildDevice() == null ? false : deviceDO.getHasChildDevice());
                    passengerFlowRecordDO.setSceneId(deviceDO.getStoreSceneId());
                    passengerFlowRecordDO.setSceneType(sceneTypeMap.get(deviceDO.getStoreSceneId()));
                    passengerFlowRecordDO.setFlowType(FlowTypeEnum.HOUR.getCode());
                    passengerFlowRecordDO.setFlowHour(k);
                    int in = RandomUtil.randomInt(0, 100);
                    inCount += in;
                    passengerFlowRecordDO.setFlowIn(in);
                    int out = RandomUtil.randomInt(0, 100);
                    outCount += out;
                    passengerFlowRecordDO.setFlowOut(out);
                    int inout = in + out;
                    inoutCount += inout;
                    passengerFlowRecordDO.setFlowInOut(inout);
                    passengerFlowRecordDO.setFlowYear(year);
                    passengerFlowRecordDO.setFlowDay(productDayTime);
                    passengerFlowRecordDO.setFlowMonth(month);
                    passengerFlowRecordDOList.add(passengerFlowRecordDO);
                }
                PassengerFlowRecordDO passengerFlowRecordDO = new PassengerFlowRecordDO();
                DeviceDO deviceDO = deviceByDeviceIdList.get(j);
                passengerFlowRecordDO.setStoreId(deviceDO.getBindStoreId());
                passengerFlowRecordDO.setRegionPath(deviceDO.getRegionPath());
                passengerFlowRecordDO.setDeviceId(deviceDO.getDeviceId());
                passengerFlowRecordDO.setHasChildDevice(deviceDO.getHasChildDevice() == null ? false : deviceDO.getHasChildDevice());
                passengerFlowRecordDO.setSceneId(deviceDO.getStoreSceneId());
                passengerFlowRecordDO.setSceneType(sceneTypeMap.get(deviceDO.getStoreSceneId()));
                passengerFlowRecordDO.setFlowType(FlowTypeEnum.DAY.getCode());
                passengerFlowRecordDO.setFlowIn(inCount);
                passengerFlowRecordDO.setFlowOut(outCount);
                passengerFlowRecordDO.setFlowInOut(inoutCount);
                passengerFlowRecordDO.setFlowYear(year);
                passengerFlowRecordDO.setFlowDay(productDayTime);
                passengerFlowRecordDO.setFlowMonth(month);
                passengerFlowRecordDOList.add(passengerFlowRecordDO);
            }

        }
        ListUtils.partition(passengerFlowRecordDOList, Constants.BATCH_INSERT_COUNT).forEach(data -> {
            passengerFlowRecodeMapper.batchInsertPassengerFlowRecordDO(eid, data);
        });
        return ResponseResult.success(true);
    }

    private int getMonthDays(int year, int month) {
        if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                return 29;
            } else {
                return 28;
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 31;
        }
    }

    @GetMapping("/device/fix")
    public ResponseResult deviceFix(@RequestParam(value = "eidStr", required = false) String eidStr,
                                    @RequestParam(value = "skipEidStr", required = false) String skipEidStr) {
        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bignowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO ->
                {
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

                    List<DeviceDO> deviceByDeviceIdList = deviceMapper.getDeviceByDeviceIdList(enterpriseConfigDO.getEnterpriseId(), null);
                    ListUtils.emptyIfNull(deviceByDeviceIdList)
                            .stream()
                            .filter(data -> StringUtils.isBlank(data.getDeviceStatus()) ||
                                    StringUtils.equals(data.getDeviceStatus(), "1") ||
                                    data.getHasChildDevice() == null ||
                                    data.getHasPtz() == null ||
                                    data.getStoreSceneId() == null ||
                                    data.getSupportCapture() == null
                            )
                            .forEach(data -> {
                                if (StringUtils.isBlank(data.getDeviceStatus()) ||
                                        StringUtils.equals(data.getDeviceStatus(), "1")) {
                                    data.setDeviceStatus(DeviceStatusEnum.ONLINE.getCode());
                                }
                                if (data.getHasChildDevice() == null) {
                                    data.setHasChildDevice(false);
                                }
                                if (data.getHasPtz() == null) {
                                    data.setHasPtz(false);
                                }
                                if (data.getSupportCapture() == null) {
                                    data.setSupportCapture(0);
                                }
                                dataCorrectionService.fixDevice(enterpriseConfigDO.getEnterpriseId(), data);


                            });
                    //订正设备数据

                });
        log.info("修复设备表脏数据:{}ms", System.currentTimeMillis() - bignowDate);
        return ResponseResult.success(true);
    }


    /**
     * 赋予菜单权限
     *
     * @return
     */
    @PostMapping("/correct/role/menuV2")
    public ResponseResult correctionRoleMenu(@RequestParam(value = "menuIds") String menuIds,
                                             @RequestParam(value = "hasMenuId") Long hasMenuId,
                                             @RequestParam(value = "platFormType") String platFormType) {
        platFormType = StringUtils.isBlank(platFormType) ? PlatFormTypeEnum.PC.getCode() : platFormType;
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        String cacheKeyPrefix = "correctRoleHasSelfMenu:";

        if (StringUtils.isBlank(menuIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        String[] menuIdList = menuIds.split(",");
        log.info("开始订正企业correctionRoleMenu");
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize, false);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectEnterpriseConfigAll();
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                //茶颜不进行订正
                if("28c8c7dd86b146e1b164c1eacb2bd87e".equals(enterpriseId)){
                    log.info("28c8c7dd86b146e1b164c1eacb2bd87e#茶颜不进行订正");
                    continue;
                }
                if("45f92210375346858b6b6694967f44de".equals(enterpriseId)){
                    log.info("45f92210375346858b6b6694967f44de#AAA不进行订正");
                    continue;
                }
                if (StringUtils.isBlank(enterpriseConfig.getDingCorpId())) {
                    continue;
                }
                try {
                    log.info("correctionRoleMenu开始订正企:{}", enterpriseId);
                    String enterpriseIdKey = redisUtilPool.getString(cacheKeyPrefix + enterpriseId);
                    if (StringUtils.isNotBlank(enterpriseIdKey)) {
                        log.info("该企业订正企业中: eid :{}", enterpriseId);
                        continue;
                    }
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                    List<Long> hasRoleIdList = sysRoleMenuMapper.listSysRoleMenuIdByMenuId(enterpriseId, platFormType, hasMenuId);
                    //
                    if (CollectionUtils.isEmpty(hasRoleIdList)) {
                        log.info("不需要订正企业: eid :{}", enterpriseId);
                        continue;
                    }

                    List<SysRoleMenuDO> addList = new ArrayList<>();
                    for (Long roleId : hasRoleIdList) {
                        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(enterpriseId, roleId, platFormType);
                        List<Long> existMenuIdList = sysRoleMenuDOList.stream().map(SysRoleMenuDO::getMenuId).collect(Collectors.toList());
                        for (String menuId : menuIdList) {
                            if (existMenuIdList.contains(Long.valueOf(menuId))) {
                                log.info(" 该企业角色已包含该菜单不在插入 eid:{} roleId:{} menuId:{}", enterpriseId, roleId, menuId);
                                continue;
                            }
                            SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                            sysRoleMenuDO.setMenuId(Long.valueOf(menuId));
                            sysRoleMenuDO.setRoleId(roleId);
                            sysRoleMenuDO.setPlatform(platFormType);
                            addList.add(sysRoleMenuDO);
                        }
                    }
                    if (CollectionUtils.isEmpty(addList)) {
                        continue;
                    }
                    ListUtils.partition(addList, 100).forEach(data -> {
                        sysRoleMenuMapper.batchInsertRoleMenu(enterpriseId, data);
                    });
                    redisUtilPool.setString(cacheKeyPrefix + enterpriseId, "1", 5 * 60);
                    log.info("correctionRoleMenu订正完成企业:{}", enterpriseId);
                } catch (Exception e) {
                    log.error("订正企业菜单权限出错 ", e);
                }
            }
        }
        log.info("订正企业correctionRoleMenu结束");
        return ResponseResult.success();
    }

    @GetMapping("/initLicenseSetting")
    public ResponseResult initLicenseSetting(@RequestParam("eid") String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        licenseApiService.initLicense(eid, null, enterpriseConfig.getDbName());
        return ResponseResult.success(true);
    }

    @GetMapping("/updateStoreRegionPath")
    public ResponseResult updateStoreRegionPath(@RequestParam(value = "eidStr", required = false) String eidStr,
                                                @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                                @RequestParam(value = "storeId", required = false) String storeId) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info("初始化门店冗余数据eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.syncStoreRegionPath2(enterpriseConfigDO.getEnterpriseId(), storeId);
                    log.info("初始化门店冗余数据eid={},花费的时间:{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);
                });
        log.info("初始化门店总花费的时间:{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @GetMapping("/delFirstLogin")
    public ResponseResult<Boolean> delFirstLogin(@RequestParam(value = "enterpriseId") String enterpriseId,
                                                 @RequestParam(value = "userId") String userId,
                                                 @RequestParam(value = "loginWay") String loginWay) {

        String key = RedisConstant.FIRST_LOGIN + enterpriseId + "_" + userId + "_" + loginWay;
        redisUtilPool.delKey(key);
        return ResponseResult.success(true);
    }

    @GetMapping("/rocketMq/initLocalRockMqGourp")
    public ResponseResult initLocalRockMqGourp(@RequestParam("profiles") String profiles) throws Exception {
        RocketMqUtil.initLocalGroup(profiles);
        return ResponseResult.success(true);
    }

    @GetMapping("/rocketMq/initGroup")
    public ResponseResult initRockMqGourp(@RequestParam("profiles") String profiles) throws Exception {
        RocketMqUtil.initGroup(profiles);
        return ResponseResult.success(true);
    }

    @GetMapping("/rocketMq/initOnlineAndHdAndPreGroup")
    public ResponseResult initOnlineAndHdAndPreGroup(@RequestParam("profiles") String profiles) throws Exception {
        RocketMqUtil.initOnlineAndHdAndPreGroup(profiles);
        return ResponseResult.success(true);
    }

    @GetMapping("/deleteGroupById")
    public ResponseResult deleteGroupById(@RequestParam("groupId") String groupId) throws Exception {
        RocketMqUtil.deleteGroupById(groupId);
        return ResponseResult.success(true);
    }

    /**
     * 同步导入的用户信息到平台库
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/syncImportUserForPlatform")
    public ResponseResult syncImportUserForPlatform(@RequestParam(value = "enterpriseId") String enterpriseId) {
        return ResponseResult.success(dataCorrectionService.syncImportUserForPlatform(enterpriseId));
    }


    /**
     * 订正业务数据的冗余区域路径字段
     * 参数不传默认订正全部企业的全部门店
     * 参数带入多个是联合条件
     *
     * @param eidStr     要订正的企业id
     * @param skipEidStr 不需要订正的企业id
     * @param storeIds   要订正的门店id
     * @return ResponseResult
     */
    @GetMapping("/correctionBusinessRegionPathNew")
    public ResponseResult correctionBusinessRegionPath(@RequestParam(value = "eidStr", required = false) String eidStr,
                                                       @RequestParam(value = "skipEidStr", required = false) String skipEidStr,
                                                       @RequestParam(value = "storeIds", required = false) String storeIds) {
        long beginNowDate = System.currentTimeMillis();
        log.info("DataCorrection correctionBusinessRegionPathNew{} params:eidStr={},skipEidStr={},storeIds={}", beginNowDate, eidStr, skipEidStr, storeIds);
        // 获取要订正数据的企业
        List<EnterpriseConfigDO> syncEnterpriseConfigs = enterpriseConfigDao.getSyncEnterpriseConfigDOS(eidStr, skipEidStr);
        if (CollectionUtils.isEmpty(syncEnterpriseConfigs)) {
            return ResponseResult.success(Boolean.TRUE);
        }
        List<String> storeIdList = StrUtil.splitTrim(storeIds, ",");
        // 遍历订正数据的企业
        CountDownLatch countDownLatch = new CountDownLatch(syncEnterpriseConfigs.size());
        for (EnterpriseConfigDO syncEnterpriseConfig : syncEnterpriseConfigs) {
            DataSourceHelper.changeToSpecificDataSource(syncEnterpriseConfig.getDbName());
            List<StoreDO> allStoreIdList = storeMapper.getAllStoreByStoreIds(syncEnterpriseConfig.getEnterpriseId(), storeIdList);
            allStoreIdList.forEach(singleStore -> {
                // 线程池
                syncThreadPoolTaskExecutor.submit(() -> {
                    dataCorrectionService.syncRegionPath(syncEnterpriseConfig.getEnterpriseId(), singleStore, syncEnterpriseConfig.getDbName());
                    countDownLatch.countDown();
                });
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("DataCorrection correctionBusinessRegionPathNew error", e);
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR);
        }
        log.info("DataCorrection correctionBusinessRegionPathNew{} used {}ms", beginNowDate, System.currentTimeMillis() - beginNowDate);
        return ResponseResult.success(true);
    }

    /**
     * 清除业务数据
     *
     * @param enterpriseId
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @author chenyupeng
     * @date 2022/3/10
     */
    @PostMapping("/truncateBusinessData")
    public ResponseResult truncateBusinessData(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (enterpriseConfigDO == null) {
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        enterpriseService.truncateBusinessData(enterpriseId);
        return ResponseResult.success();
    }


    @GetMapping("/getUnclassifiedRegionDO")
    public ResponseResult getUnclassifiedRegionDO(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.getUnclassifiedRegionDO(enterpriseId));
    }

    @GetMapping("/dealTestStoreAuth")
    public ResponseResult dealTestStoreAuth(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        Map<Long, Long> menuIdMap = new HashMap<>();
        menuIdMap.put(4267L, 4260L);//添加区域
        menuIdMap.put(4268L, 4266L);//批量导入区域
        menuIdMap.put(4269L, 4263L);//编辑区域
        menuIdMap.put(4270L, 4264L);//删除区域
        List<Long> menuIdIds = menuIdMap.entrySet().stream().map(o -> o.getKey()).collect(Collectors.toList());
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                List<SysRoleMenuDO> menuList = sysRoleMenuMapper.listRoleMenuIdByMenuIds(enterpriseId, menuIdIds);
                if (CollectionUtils.isEmpty(menuList)) {
                    continue;
                }
                List<SysRoleMenuDO> addList = new ArrayList<>();
                for (SysRoleMenuDO roleMenu : menuList) {
                    //将原来拥有门店配置中“添加区域、批量导入区域、编辑区域”权限的用户默认给到新增的这些权限
                    addList.add(new SysRoleMenuDO(menuIdMap.get(roleMenu.getMenuId()), roleMenu.getRoleId(), roleMenu.getPlatform()));
                }
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                sysRoleMenuMapper.batchInsertRoleMenu(enterpriseId, addList);
                sysRoleMenuMapper.deleteAuthByMenuIds(enterpriseId, menuList.stream().map(SysRoleMenuDO::getId).collect(Collectors.toList()));

            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/dealOnlineStoreAuth")
    public ResponseResult dealOnlineStoreAuth(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        Map<Long, Long> menuIdMap = new HashMap<>();
        menuIdMap.put(621L, 4260L);//添加区域
        menuIdMap.put(622L, 4266L);//批量导入区域
        menuIdMap.put(623L, 4263L);//编辑区域
        menuIdMap.put(624L, 4264L);//删除区域
        List<Long> menuIdIds = menuIdMap.entrySet().stream().map(o -> o.getKey()).collect(Collectors.toList());
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                List<SysRoleMenuDO> menuList = sysRoleMenuMapper.listRoleMenuIdByMenuIds(enterpriseId, menuIdIds);
                if (CollectionUtils.isEmpty(menuList)) {
                    continue;
                }
                List<SysRoleMenuDO> addList = new ArrayList<>();
                for (SysRoleMenuDO roleMenu : menuList) {
                    //将原来拥有门店配置中“添加区域、批量导入区域、编辑区域”权限的用户默认给到新增的这些权限
                    addList.add(new SysRoleMenuDO(menuIdMap.get(roleMenu.getMenuId()), roleMenu.getRoleId(), roleMenu.getPlatform()));
                }
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                sysRoleMenuMapper.batchInsertRoleMenu(enterpriseId, addList);
                sysRoleMenuMapper.deleteAuthByMenuIds(enterpriseId, menuList.stream().map(SysRoleMenuDO::getId).collect(Collectors.toList()));
            }
        }
        return ResponseResult.success();
    }


    @GetMapping("/setHistoryEnterprise")
    public ResponseResult setHistoryEnterprise(@RequestParam(value = "enterpriseIds") List<String> enterpriseIds) {
        for (String enterpriseId : enterpriseIds) {
            redisUtilPool.hashSet(RedisConstant.HISTORY_ENTERPRISE, enterpriseId, enterpriseId);
        }
        return ResponseResult.success();
    }

    @GetMapping("/removeHistoryEnterprise")
    public ResponseResult removeHistoryEnterprise(@RequestParam(value = "enterpriseIds") List<String> enterpriseIds) {
        for (String enterpriseId : enterpriseIds) {
            redisUtilPool.hashDel(RedisConstant.HISTORY_ENTERPRISE, enterpriseId);
        }
        return ResponseResult.success();
    }

    @GetMapping("/updateRoleMenu")
    public ResponseResult updateRoleMenu(@RequestParam(value = "eidStr", required = false) String eidStr,
                                         @RequestParam(value = "skipEidStr", required = false) String skipEidStr) {

        List<String> skipEidList = StrUtil.splitTrim(skipEidStr, ",");
        List<String> eidList = StrUtil.splitTrim(eidStr, ",");
        long bigNowDate = System.currentTimeMillis();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        ListUtils.emptyIfNull(enterpriseConfigDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getDingCorpId()))
                .filter(data -> StringUtils.isNotBlank(data.getEnterpriseId()))
                .filter(data -> CollectionUtils.isEmpty(eidList) || eidList.contains(data.getEnterpriseId()))
                .filter(data -> !CollectionUtils.isNotEmpty(skipEidList) || !skipEidList.contains(data.getEnterpriseId()))
                .forEach(enterpriseConfigDO -> {
                    log.info(" 开始订正角色菜单数据 eid={}", enterpriseConfigDO.getEnterpriseId());
                    long nowDate = System.currentTimeMillis();
                    DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                    dataCorrectionService.updateRoleMenu(enterpriseConfigDO.getEnterpriseId());
                    log.info(" 结束订正角色菜单数据 eid={},花费的时间:{}ms", enterpriseConfigDO.getEnterpriseId(), System.currentTimeMillis() - nowDate);
                });
        log.info(" 角色菜单数据总花费的时间 :{}ms", System.currentTimeMillis() - bigNowDate);

        return ResponseResult.success(true);
    }

    @GetMapping("/deal/meta/column/category")
    public ResponseResult dealMetaColumnCategory(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                tbMetaColumnCategoryDAO.getOtherCategoryId(enterpriseId);
            }
        }
        return ResponseResult.success();
    }

    @PostMapping("/syncOnePartyAll")
    public ResponseResult syncOnePartyAll(@RequestParam(value = "eid") String eid) {

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        syncUserFacade.syncAllForOneParty(eid, "system", "system",
                enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getDbName());
        return ResponseResult.success(null);
    }

    @PostMapping("/syncOnePartyContact")
    public ResponseResult syncOnePartyContact(@RequestParam(value = "eid") String eid) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        syncStoreFacade.syncDingOnePartyStoreAndRegion(eid, enterpriseConfigDO.getDingCorpId(), Constants.SYSTEM_USER_ID, Constants.SYSTEM_USER_NAME, enterpriseSettingVO);
        return ResponseResult.success();
    }

    /**
     * 订正门店任务抄送人
     *
     * @param eid
     * @return
     * @throws ApiException
     */
    @PostMapping("/correctTaskStoreCcUserId")
    public ResponseResult correctTaskStoreCcUserId(@RequestParam(value = "eid") String eid,
                                                   @RequestParam(value = "taskId") Long taskId,
                                                   @RequestParam(value = "loopCount", required = false) Long loopCount) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TaskStoreLoopQuery query = new TaskStoreLoopQuery();
        query.setUnifyTaskId(taskId);
        query.setLoopCount(loopCount);
        List<TaskStoreDO> taskStoreList = taskStoreMapper.taskStoreAllList(eid, query);
        TaskParentDO taskParentDO = taskParentMapper.selectTaskById(eid, taskId);
        if (CollectionUtils.isNotEmpty(taskStoreList)) {
            List<TaskProcessDTO> process = JSON.parseArray(taskParentDO.getNodeInfo(), TaskProcessDTO.class);
            List<String> storeIdList = taskStoreList.stream().map(TaskStoreDO::getStoreId).collect(Collectors.toList());
            //第一次循环都取最新的门店集合储存
            List<TaskMappingDO> personList = Lists.newArrayList();
            unifyTaskService.getPerson(process, taskId, personList, new HashSet<>(storeIdList), eid, taskParentDO.getCreateUserId(), taskParentDO.getTaskType(), true,null);
            //抄送人门店映射关系
            Map<String, List<TaskMappingDO>> ccPersonMap = personList.stream()
                    .filter(f -> UnifyNodeEnum.CC.getCode().equals(f.getNode()))
                    .collect(Collectors.groupingBy(TaskMappingDO::getType));
            for (TaskStoreDO taskStoreDO : taskStoreList) {
                String ccUserIds = taskStoreDO.getCcUserIds();
                Set<String> ccNewUserSet = new HashSet<>();
                if (ccPersonMap.get(taskStoreDO.getStoreId()) != null) {
                    ccNewUserSet = ccPersonMap.get(taskStoreDO.getStoreId()).stream().map(TaskMappingDO::getMappingId).collect(Collectors.toSet());
                }
                Set<String> currentCcUserIdSet = new HashSet<>();
                if (StringUtils.isNotBlank(ccUserIds)) {
                    currentCcUserIdSet = Arrays.stream(StringUtils.split(taskStoreDO.getCcUserIds(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                }
                if (CollectionUtils.isNotEmpty(ccNewUserSet)) {
                    if (CollectionUtils.isNotEmpty(currentCcUserIdSet)) {
                        ccNewUserSet.removeAll(currentCcUserIdSet);
                    }
                }
                if (CollectionUtils.isNotEmpty(ccNewUserSet)) {
                    if (StringUtils.isNotBlank(ccUserIds)) {
                        ccUserIds = ccUserIds + String.join(Constants.COMMA, ccNewUserSet) + Constants.COMMA;
                    } else {
                        ccUserIds = Constants.COMMA + String.join(Constants.COMMA, ccNewUserSet) + Constants.COMMA;
                    }
                    taskStoreMapper.updatedCcInfoByTaskStoreId(eid, taskStoreDO.getId(), ccUserIds);
                }
            }
        }
        return ResponseResult.success();
    }

    @ApiOperation("拷贝任务数据")
    @GetMapping("/copyPatrolTaskData")
    public ResponseResult copyPatrolTaskData(@RequestParam(value = "fromEnterpriseId") String fromEnterpriseId,
                                             @RequestParam(value = "toEnterpriseId") String toEnterpriseId,
                                             @RequestParam("taskIds") List<Long> taskIds,
                                             @RequestParam(value = "beginTime", required = false) String beginTime,
                                             @RequestParam(value = "endTime", required = false) String endTime) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO fromEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(fromEnterpriseId);
        EnterpriseConfigDO toEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(toEnterpriseId);
        if (Objects.isNull(fromEnterpriseConfig) || Objects.isNull(toEnterpriseConfig)) {
            return ResponseResult.success();
        }
        for (Long unifyTaskId : taskIds) {
            List<Long> unifyTaskIds = Arrays.asList(unifyTaskId);
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TaskStoreDO> storeTaskList = taskStoreMapper.getStoreTaskByUnifyTaskId(fromEnterpriseId, unifyTaskId, beginTime, endTime);
            try {
                DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                taskStoreMapper.delTaskStoreByParentTaskId(toEnterpriseId, unifyTaskId);
                taskStoreMapper.copyTaskStore(toEnterpriseId, storeTaskList);
            } catch (Exception e) {
                log.error("拷贝门店任务出错", e);
            }
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TaskParentDO> taskParentList = taskParentMapper.selectParentTaskByTaskIds(fromEnterpriseId, unifyTaskIds);
            for (TaskParentDO taskParent : taskParentList) {
                try {
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    taskParentMapper.delParentTaskByTaskId(toEnterpriseId, unifyTaskId);
                    taskParentMapper.copyTaskParent(toEnterpriseId, taskParent);
                } catch (Exception e) {
                    log.error("拷贝任务出错", e);
                    continue;
                }
            }
            taskParentList.clear();
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TaskMappingDO> unifyStoreDTOS = taskMappingMapper.selectMappingByBatchTaskId(fromEnterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), unifyTaskIds);
            if (CollectionUtils.isNotEmpty(unifyStoreDTOS)) {
                try {
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    taskMappingMapper.insertTaskMapping(toEnterpriseId, UnifyTableEnum.TABLE_STORE.getCode(), unifyStoreDTOS);
                } catch (Exception e) {
                    log.error("拷贝store_mapping出错", e);
                }
                unifyStoreDTOS.clear();
            }
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TaskDataMappingDO> unifyFormDataDTOS = taskMappingMapper.getAllDataMappingByUnifyTaskIds(fromEnterpriseId, unifyTaskIds);
            if (CollectionUtils.isNotEmpty(unifyFormDataDTOS)) {
                try {
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    taskMappingMapper.insertDataTaskMapping(toEnterpriseId, unifyFormDataDTOS);
                } catch (Exception e) {
                    log.error("拷贝data_mapping出错", e);
                }
                unifyFormDataDTOS.clear();
            }
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbPatrolStoreRecordDO> patrolRecordList = tbPatrolStoreRecordMapper.getPatrolRecordByUnifyTaskIds(fromEnterpriseId, unifyTaskIds);
            List<Long> patrolRecordIds = patrolRecordList.stream().map(o -> o.getId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(patrolRecordList)) {
                try {
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    tbPatrolStoreRecordMapper.deleteByIds(toEnterpriseId, patrolRecordIds);
                    tbPatrolStoreRecordMapper.copyPatrolRecord(toEnterpriseId, patrolRecordList);
                } catch (Exception e) {
                    log.error("拷贝巡店记录出错", e);
                }
                patrolRecordList.clear();
            }
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbDataTableDO> tbDataTableList = tbDataTableMapper.getTbDataTableListByUnifyTaskIds(fromEnterpriseId, unifyTaskIds);
            if (CollectionUtils.isNotEmpty(tbDataTableList)) {
                List<Long> dataTableIds = tbDataTableList.stream().map(o -> o.getId()).collect(Collectors.toList());
                try {
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    tbDataTableMapper.deleteByIds(toEnterpriseId, dataTableIds);
                    tbDataTableMapper.copyDataTable(toEnterpriseId, tbDataTableList);
                } catch (Exception e) {
                    log.error("拷贝数据表出错", e);
                }
                tbDataTableList.clear();
            }
            boolean isColumnLoop = true;
            int innerPageNum = 1, innerPageSize = 5000;
            while (isColumnLoop) {
                DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
                PageHelper.startPage(innerPageNum, innerPageSize, false);
                List<TbDataStaTableColumnDO> tbDataColumnList = tbDataStaTableColumnMapper.getTbDataColumnListByUnifyTaskIds(fromEnterpriseId, unifyTaskIds);
                PageHelper.clearPage();
                if (tbDataColumnList.size() < innerPageSize) {
                    isColumnLoop = false;
                }
                innerPageNum++;
                try {
                    if (CollectionUtils.isNotEmpty(tbDataColumnList)) {
                        List<Long> tbDataColumnIds = tbDataColumnList.stream().map(o -> o.getId()).collect(Collectors.toList());
                        DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                        tbDataStaTableColumnMapper.deleteByIds(toEnterpriseId, tbDataColumnIds);
                        tbDataStaTableColumnMapper.copyDataColumn(toEnterpriseId, tbDataColumnList);
                        tbDataColumnList.clear();
                    }
                } catch (Exception e) {
                    log.error("拷贝数据项出错", e);
                }
            }
            if (CollectionUtils.isNotEmpty(patrolRecordIds)) {
                boolean isHistoryLoop = true;
                int innerHistoryPageNum = 1;
                while (isHistoryLoop) {
                    DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
                    PageHelper.startPage(innerHistoryPageNum, innerPageSize, false);
                    List<TbPatrolStoreHistoryDo> patrolHistoryRecordList = tbPatrolStoreHistoryMapper.selectPatrolStoreHistoryByBusinessIds(fromEnterpriseId, patrolRecordIds);
                    PageHelper.clearPage();
                    if (patrolHistoryRecordList.size() < innerPageSize) {
                        isHistoryLoop = false;
                    }
                    innerHistoryPageNum++;
                    if (CollectionUtils.isNotEmpty(patrolHistoryRecordList)) {
                        try {
                            List<Integer> historyIds = patrolHistoryRecordList.stream().map(o -> o.getId()).collect(Collectors.toList());
                            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                            tbPatrolStoreHistoryMapper.deleteByIds(toEnterpriseId, historyIds);
                            tbPatrolStoreHistoryMapper.copyHistory(toEnterpriseId, patrolHistoryRecordList);
                        } catch (Exception e) {
                            log.error("拷贝巡店历史记录出错", e);
                        }
                        patrolHistoryRecordList.clear();
                    }
                }
            }
        }
        return ResponseResult.success();
    }


    @ApiOperation("拷贝检查表数据")
    @GetMapping("/copySopData")
    public ResponseResult copySopData(@RequestParam(value = "fromEnterpriseId") String fromEnterpriseId,
                                      @RequestParam(value = "toEnterpriseId") String toEnterpriseId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO fromEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(fromEnterpriseId);
        EnterpriseConfigDO toEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(toEnterpriseId);
        if (Objects.isNull(fromEnterpriseConfig) || Objects.isNull(toEnterpriseConfig)) {
            return ResponseResult.success();
        }
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        //获取所有分类
        List<TbMetaColumnCategoryDO> categoryList = tbMetaColumnCategoryMapper.getAllCategoryList(fromEnterpriseId);
        //项
        List<TbMetaQuickColumnDO> tbMetaQuickColumnList = tbMetaQuickColumnMapper.selectAllColumnList(fromEnterpriseId);
        DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
        tbMetaColumnCategoryMapper.deleteAllCategory(toEnterpriseId);
        tbMetaColumnCategoryMapper.copyCategory(toEnterpriseId, categoryList);
        tbMetaQuickColumnMapper.deleteAllMetaColumn(toEnterpriseId);
        tbMetaQuickColumnResultMapper.deleteAllMetaQuickColumnResult(toEnterpriseId);
        //结果项
        for (TbMetaQuickColumnDO tbMetaQuickColumn : tbMetaQuickColumnList) {
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultMapper.getColumnResultList(fromEnterpriseId, Arrays.asList(tbMetaQuickColumn.getId()));
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaQuickColumnMapper.copyMetaColumn(toEnterpriseId, tbMetaQuickColumn);
            if (CollectionUtils.isEmpty(columnResultList)) {
                continue;
            }
            tbMetaQuickColumnResultMapper.copyMetaQuickColumnResult(toEnterpriseId, columnResultList);
        }
        //表
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        List<TbMetaTableDO> allMetaTable = tbMetaTableMapper.getAllMetaTable(fromEnterpriseId, null);
        DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
        tbMetaTableMapper.deleteAllTable(toEnterpriseId);
        tbMetaStaTableColumnMapper.deleteAllColumn(toEnterpriseId);
        tbMetaColumnResultMapper.deleteAll(toEnterpriseId);
        //项，结果项
        for (TbMetaTableDO tbMetaTable : allMetaTable) {
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbMetaStaTableColumnDO> allColumnTableIdList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(fromEnterpriseId, Arrays.asList(tbMetaTable.getId()));
            List<TbMetaColumnResultDO> tbMetaColumnResultList = tbMetaColumnResultMapper.selectByMetaTableId(fromEnterpriseId, tbMetaTable.getId());
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            if (CollectionUtils.isNotEmpty(allColumnTableIdList)) {
                tbMetaStaTableColumnMapper.copyColumnList(toEnterpriseId, allColumnTableIdList);
            }
            if (CollectionUtils.isNotEmpty(tbMetaColumnResultList)) {
                tbMetaColumnResultMapper.copyColumnResult(toEnterpriseId, tbMetaColumnResultList);
            }
            tbMetaTableMapper.copyMetaTable(toEnterpriseId, tbMetaTable);
        }
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnList = tbMetaDefTableColumnMapper.selectAll(fromEnterpriseId);
        if (CollectionUtils.isNotEmpty(tbMetaDefTableColumnList)) {
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaDefTableColumnMapper.deleteAllDefTable(toEnterpriseId);
            tbMetaDefTableColumnMapper.copyDefTableColumn(toEnterpriseId, tbMetaDefTableColumnList);
        }
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        List<TaskSopDO> allSop = taskSopMapper.getAllSop(fromEnterpriseId);
        if (CollectionUtils.isNotEmpty(allSop)) {
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            taskSopMapper.deleteAllSop(toEnterpriseId);
            taskSopMapper.copySop(toEnterpriseId, allSop);
        }
        return ResponseResult.success();
    }

    @GetMapping("/dealParentQuestion")
    public ResponseResult dealParentQuestion(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds,
                                             @RequestParam(value = "isTodo", required = false) Boolean isTodo) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                int innerPageNum = 1;
                boolean innerLoop = true;
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                while (innerLoop) {
                    PageHelper.startPage(innerPageNum, Constants.MAX_QUERY_SIZE, false);
                    List<TbQuestionRecordDO> tbQuestionRecord = tbQuestionRecordMapper.questionList(enterpriseId, new QuestionDTO());
                    if (CollectionUtils.isEmpty(tbQuestionRecord) || tbQuestionRecord.size() < Constants.MAX_QUERY_SIZE) {
                        innerLoop = false;
                    }
                    innerPageNum++;
                    List<TbQuestionRecordDO> questionList = tbQuestionRecord.stream().filter(o -> Objects.isNull(o.getParentQuestionId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(questionList)) {
                        //生成父工单, 更新子工单
                        for (TbQuestionRecordDO tbQuestionRecordDO : questionList) {
                            if (Objects.nonNull(tbQuestionRecordDO.getParentQuestionId())) {
                                if (isTodo != null && isTodo) {
                                    //订正待办
                                    TaskStoreDO taskStoreDO = taskStoreMapper.getTaskQuestionStore(enterpriseId, tbQuestionRecordDO.getUnifyTaskId(), tbQuestionRecordDO.getStoreId(), tbQuestionRecordDO.getLoopCount());
                                    if (taskStoreDO != null) {
                                        questionParentUserMappingService.updateByTaskStore(enterpriseId, taskStoreDO, true);
                                    }
                                }
                                continue;
                            }
                            try {
                                TaskParentDO taskParentDO = taskParentMapper.selectTaskById(enterpriseId, tbQuestionRecordDO.getUnifyTaskId());
                                TbQuestionParentInfoDO tbQuestionParentInfoDO = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, tbQuestionRecordDO.getUnifyTaskId());
                                Long questionParentId = null;
                                if (Objects.isNull(tbQuestionParentInfoDO)) {
                                    boolean isFinish = "endNode".equals(tbQuestionRecordDO.getStatus());
                                    TbQuestionParentInfoDO tbQuestionParentInfo = new TbQuestionParentInfoDO();
                                    tbQuestionParentInfo.setUnifyTaskId(tbQuestionRecordDO.getUnifyTaskId());
                                    tbQuestionParentInfo.setStatus(isFinish ? Constants.INDEX_ONE : Constants.ZERO);
                                    tbQuestionParentInfo.setQuestionName(tbQuestionRecordDO.getTaskName());
                                    tbQuestionParentInfo.setQuestionType(tbQuestionRecordDO.getQuestionType());
                                    tbQuestionParentInfo.setFinishNum(isFinish ? Constants.INDEX_ONE : Constants.ZERO);
                                    tbQuestionParentInfo.setTotalNum(Constants.INDEX_ONE);
                                    tbQuestionParentInfo.setCreateId(tbQuestionRecordDO.getCreateUserId());
                                    tbQuestionParentInfo.setCreateTime(tbQuestionRecordDO.getCreateTime());
                                    tbQuestionParentInfo.setUpdateTime(tbQuestionRecordDO.getUpdateTime());
                                    questionParentInfoDao.insertSelective(enterpriseId, tbQuestionParentInfo);
                                    questionParentId = tbQuestionParentInfo.getId();
                                } else {
                                    UnifySubStatisticsDTO unifySubStatistics = tbQuestionRecordMapper.selectQuestionTaskCount(enterpriseId, tbQuestionRecordDO.getUnifyTaskId());
                                    Integer completeNum = unifySubStatistics.getComplete();
                                    Integer totalNum = unifySubStatistics.getAll();
                                    boolean isFinish = totalNum < completeNum;
                                    tbQuestionParentInfoDO.setStatus(isFinish ? Constants.INDEX_ONE : Constants.ZERO);
                                    tbQuestionParentInfoDO.setQuestionName(tbQuestionRecordDO.getTaskName());
                                    tbQuestionParentInfoDO.setQuestionType(tbQuestionRecordDO.getQuestionType());
                                    tbQuestionParentInfoDO.setFinishNum(unifySubStatistics.getComplete());
                                    tbQuestionParentInfoDO.setTotalNum(unifySubStatistics.getAll());
                                    questionParentInfoDao.updateByPrimaryKeySelective(enterpriseId, tbQuestionParentInfoDO);
                                    questionParentId = tbQuestionParentInfoDO.getId();
                                }

                                log.info("parentQuestionId：{}", questionParentId);
                                tbQuestionRecordDO.setParentQuestionId(questionParentId);
                                tbQuestionRecordDO.setParentQuestionName(tbQuestionRecordDO.getTaskName());
                                tbQuestionRecordDO.setTaskDesc(taskParentDO.getTaskDesc());
                                tbQuestionRecordMapper.updateByPrimaryKeySelective(tbQuestionRecordDO, enterpriseId);
                                //订正工单附件信息
                                TbQuestionRecordExpandDO expandDO = tbQuestionRecordExpandMapper.selectByRecordId(enterpriseId, tbQuestionRecordDO.getId());
                                if (taskParentDO != null) {
                                    if (expandDO == null) {
                                        expandDO = new TbQuestionRecordExpandDO();
                                        expandDO.setCreateTime(new Date());
                                        expandDO.setRecordId(tbQuestionRecordDO.getId());
                                        expandDO.setUnifyTaskId(tbQuestionRecordDO.getUnifyTaskId());
                                        expandDO.setUpdateTime(new Date());
                                        expandDO.setDeleted(false);
                                    }
                                    expandDO.setTaskInfo(taskParentDO.getTaskInfo());
                                    if (expandDO.getId() != null) {
                                        tbQuestionRecordExpandMapper.updateByPrimaryKeySelective(expandDO, enterpriseId);
                                    } else {
                                        tbQuestionRecordExpandMapper.insertSelective(expandDO, enterpriseId);
                                    }
                                }
                                //订正待办
                                TaskStoreDO taskStoreDO = taskStoreMapper.getTaskQuestionStore(enterpriseId, tbQuestionRecordDO.getUnifyTaskId(), tbQuestionRecordDO.getStoreId(), tbQuestionRecordDO.getLoopCount());
                                if (taskStoreDO != null) {
                                    questionParentUserMappingService.updateByTaskStore(enterpriseId, taskStoreDO, true);
                                }
                            } catch (Exception e) {
                                log.error("dealParentQuestion:{},{}");
                            }
                        }
                    }
                }
            }
        }
        return ResponseResult.success();
    }

    /**
     * 订正单个工单待办
     *
     * @param enterpriseId
     * @param taskStoreId
     * @return
     */
    @GetMapping("/correctQuestionToDo")
    public ResponseResult correctQuestionToDoList(@RequestParam(value = "enterpriseId") String enterpriseId,
                                                  @RequestParam(value = "taskStoreId") Long taskStoreId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        //订正待办
        TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(enterpriseId, taskStoreId);
        if (taskStoreDO != null && TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskStoreDO.getTaskType())) {
            questionParentUserMappingService.updateByTaskStore(enterpriseId, taskStoreDO, true);
        }
        return ResponseResult.success();
    }


    @GetMapping("/dealParentQuestionTodo")
    public ResponseResult dealParentQuestion(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                int innerPageNum = 1;
                boolean innerLoop = true;
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                while (innerLoop) {
                    PageHelper.startPage(innerPageNum, Constants.MAX_QUERY_SIZE);
                    List<TbQuestionRecordDO> tbQuestionRecord = tbQuestionRecordMapper.questionList(enterpriseId, new QuestionDTO());
                    if (CollectionUtils.isEmpty(tbQuestionRecord) || tbQuestionRecord.size() < Constants.MAX_QUERY_SIZE) {
                        innerLoop = false;
                    }
                    innerPageNum++;
                    //生成父工单, 更新子工单
                    for (TbQuestionRecordDO tbQuestionRecordDO : tbQuestionRecord) {
                        if (Objects.nonNull(tbQuestionRecordDO.getParentQuestionId())) {
                            //订正待办
                            TaskStoreDO taskStoreDO = taskStoreMapper.getTaskQuestionStore(enterpriseId, tbQuestionRecordDO.getUnifyTaskId(), tbQuestionRecordDO.getStoreId(), tbQuestionRecordDO.getLoopCount());
                            if (taskStoreDO != null) {
                                questionParentUserMappingService.updateByTaskStore(enterpriseId, taskStoreDO, true);
                            }
                        }
                    }
                }
            }
        }
        return ResponseResult.success();
    }

    /**
     * 同步周大福单个部门用户测试
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/syncQwSelfDeptUser")
    public ResponseResult syncQwSelfDeptUser(@RequestParam(value = "enterpriseId") String enterpriseId,
                                             @RequestParam(value = "deptId") String deptId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        syncUserFacade.syncQwSelfDeptUser(enterpriseConfigDO.getDingCorpId(), enterpriseId, enterpriseConfigDO.getDbName(), new HashSet<>(), String.valueOf(deptId), enterpriseConfigDO.getAppType());
        return ResponseResult.success();
    }


    @GetMapping("/cancelUpcomingWhenDelStoreWork")
    public ResponseResult cancelUpcomingWhenDelStoreWork(@RequestParam(value = "enterpriseId") String enterpriseId,
                                                         @RequestParam("storeWorkId") Long storeWorkId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        storeWorkService.cancelUpcomingWhenDel(enterpriseId, storeWorkId, enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId(), user);
        return ResponseResult.success();
    }

    @PostMapping("/corrWorkFlowMsg")
    public ResponseResult corrWorkFlowMsg(@RequestParam(value = "enterpriseId") String enterpriseId,
                                          @RequestBody JSONObject jsonObj) {
        WorkflowDealDTO flow = JSONObject.parseObject(jsonObj.toJSONString(), WorkflowDealDTO.class);
        // 切数据源
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO checkSettingDO = storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        unifyTaskService.sendTask(enterpriseConfigDO.getDingCorpId(), flow, enterpriseId, checkSettingDO, enterpriseConfigDO.getAppType());
        DataSourceHelper.reset();
        return ResponseResult.success();
    }


    @GetMapping("/updateLimitStoreCount")
    public ResponseResult updateLimitStoreCount() {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(null, null);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                Integer limitStoreCount = storeDao.getStoreCount(enterpriseId);
                if (Objects.isNull(limitStoreCount) || limitStoreCount < Constants.TEN) {
                    //如果实际门店数量小于10  默认还是给10
                    limitStoreCount = Constants.TEN;
                }
                DataSourceHelper.reset();
                enterpriseService.updateLimitStoreCount(enterpriseId, limitStoreCount);
            }
        }
        return ResponseResult.success();
    }


    @GetMapping("/updateLimitDeviceCount")
    public ResponseResult updateLimitDeviceCount() {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(null, null);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                Integer limitDeviceCount = deviceService.getDeviceCountAndChannelCount(enterpriseId);
                if (Objects.isNull(limitDeviceCount) || limitDeviceCount < Constants.INDEX_FIVE) {
                    //如果实际门店数量小于10  默认还是给10
                    limitDeviceCount = Constants.INDEX_FIVE;
                }
                DataSourceHelper.reset();
                enterpriseService.updateDeviceCount(enterpriseId, limitDeviceCount);
            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/changeAuthScope")
    public SendResult changeAuthScope(@RequestParam("corpId") String corpId, @RequestParam("appType") String appType, @RequestParam("permanentCode") String permanentCode) {
        AuthMsg authMsg = new AuthMsg();
        authMsg.setCorpId(corpId);
        authMsg.setAppType(appType);
        authMsg.setScopeChange(true);
        authMsg.setPermanentCode(permanentCode);
        return simpleMessageService.send(JSON.toJSONString(authMsg), RocketMqTagEnum.AUTH_QUEUE);
    }


    @ApiOperation("迁移检查表数据")
    @GetMapping("/moveMetaTableData")
    public ResponseResult moveMetaTableData(@RequestParam(value = "fromEnterpriseId") String fromEnterpriseId,
                                            @RequestParam(value = "toEnterpriseId") String toEnterpriseId,
                                            @RequestParam(value = "metaTableIds", required = false) List<Long> metaTableIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO fromEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(fromEnterpriseId);
        EnterpriseConfigDO toEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(toEnterpriseId);
        if (Objects.isNull(fromEnterpriseConfig) || Objects.isNull(toEnterpriseConfig)) {
            return ResponseResult.success();
        }

        //表
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        List<TbMetaTableDO> allMetaTable;
        if (CollectionUtils.isNotEmpty(metaTableIds)) {
            allMetaTable = tbMetaTableMapper.selectByIds(fromEnterpriseId, metaTableIds);
        } else {
            allMetaTable = tbMetaTableMapper.getAllMetaTable(fromEnterpriseId, null);
        }
        //项，结果项
        for (TbMetaTableDO tbMetaTable : allMetaTable) {
            Long oldMetaTableId = tbMetaTable.getId();
            tbMetaTable.setId(null);
            tbMetaTable.setCreateTime(new Date());
            tbMetaTable.setEditTime(new Date());
            tbMetaTable.setCreateUserId("");
            tbMetaTable.setCreateUserName("");
            tbMetaTable.setShareGroup("");
            tbMetaTable.setEditUserId("");
            tbMetaTable.setEditUserName("");
            tbMetaTable.setShareGroupName("");
            tbMetaTable.setResultShareGroup("");
            tbMetaTable.setResultShareGroupName("");
            tbMetaTable.setUsePersonInfo("");
            tbMetaTable.setUseRange(UserRangeTypeEnum.SELF.getType());
            tbMetaTable.setResultViewPersonInfo("");
            tbMetaTable.setResultViewRange(UserRangeTypeEnum.SELF.getType());
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaTableMapper.insertTable(toEnterpriseId, tbMetaTable);
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbMetaStaTableColumnDO> allColumnTableIdList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(fromEnterpriseId, Collections.singletonList(oldMetaTableId));
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            if (CollectionUtils.isNotEmpty(allColumnTableIdList)) {
                for(TbMetaStaTableColumnDO metaStaTableColumnDO : allColumnTableIdList){
                    Long oldStaTableColumnId = metaStaTableColumnDO.getId();
                    metaStaTableColumnDO.setId(null);
                    metaStaTableColumnDO.setCreateTime(new Date());
                    metaStaTableColumnDO.setEditTime(new Date());
                    metaStaTableColumnDO.setMetaTableId(tbMetaTable.getId());
                    metaStaTableColumnDO.setQuestionHandlerType("");
                    metaStaTableColumnDO.setQuestionHandlerId("");
                    metaStaTableColumnDO.setQuestionRecheckerType("");
                    metaStaTableColumnDO.setQuestionRecheckerId("");
                    metaStaTableColumnDO.setQuestionCcType("");
                    metaStaTableColumnDO.setQuestionCcId("");
                    metaStaTableColumnDO.setCreateUserId("");
                    metaStaTableColumnDO.setCreateUserName("");
                    metaStaTableColumnDO.setEditUserId("");
                    metaStaTableColumnDO.setEditUserName("");
                    metaStaTableColumnDO.setSopId(0L);
                    metaStaTableColumnDO.setFreeCourse(null);
                    metaStaTableColumnDO.setCoolCourse(null);
                    metaStaTableColumnDO.setStoreSceneId(null);
                    metaStaTableColumnDO.setQuestionApproveUser("");
                    metaStaTableColumnDO.setQuickColumnId(null);
                    metaStaTableColumnDO.setCanModify(true);
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    tbMetaStaTableColumnMapper.insert(toEnterpriseId, metaStaTableColumnDO);
                    DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
                    List<TbMetaColumnResultDO> tbMetaColumnResultList = tbMetaColumnResultMapper.selectByColumnIds(fromEnterpriseId, Collections.singletonList(oldStaTableColumnId));
                    if(CollectionUtils.isNotEmpty(tbMetaColumnResultList)){
                        for(TbMetaColumnResultDO resultDO : tbMetaColumnResultList){
                            resultDO.setId(null);
                            resultDO.setCreateTime(new Date());
                            resultDO.setEditTime(new Date());
                            resultDO.setMetaTableId(tbMetaTable.getId());
                            resultDO.setMetaColumnId(metaStaTableColumnDO.getId());
                            resultDO.setCreateUserId("");
                        }
                        DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                        tbMetaColumnResultMapper.batchInsert(toEnterpriseId, tbMetaColumnResultList);
                    }
                }
            }
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbMetaDisplayTableColumnDO>  displayTableColumnDOList = metaDisplayTableColumnMapper
                    .selectAllColumnListByTableIdList(fromEnterpriseId, Collections.singletonList(oldMetaTableId));
            if(CollectionUtils.isNotEmpty(displayTableColumnDOList)){
                displayTableColumnDOList.forEach(displayTableColumnDO -> {
                    displayTableColumnDO.setId(null);
                    displayTableColumnDO.setMetaTableId(tbMetaTable.getId());
                    displayTableColumnDO.setCreateTime(new Date());
                    displayTableColumnDO.setEditTime(new Date());
                    displayTableColumnDO.setCreateUserId("");
                    displayTableColumnDO.setCreateUserName("");
                    displayTableColumnDO.setEditUserId("");
                    displayTableColumnDO.setEditUserName("");
                    DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
                    metaDisplayTableColumnMapper.insertSelective(toEnterpriseId, displayTableColumnDO);
                });
            }

        }
        return ResponseResult.success();
    }

    @ApiOperation("迁移sop检查项数据")
    @GetMapping("/moveMetaQuickColumnData")
    public ResponseResult moveMetaQuickColumnData(@RequestParam(value = "fromEnterpriseId") String fromEnterpriseId,
                                            @RequestParam(value = "toEnterpriseId") String toEnterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO fromEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(fromEnterpriseId);
        EnterpriseConfigDO toEnterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(toEnterpriseId);
        if (Objects.isNull(fromEnterpriseConfig) || Objects.isNull(toEnterpriseConfig)) {
            return ResponseResult.success();
        }
        //项
        DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
        List<TbMetaQuickColumnDO> tbMetaQuickColumnList = tbMetaQuickColumnMapper.selectAllColumnList(fromEnterpriseId);
        tbMetaQuickColumnList = tbMetaQuickColumnList.stream().filter(e -> e.getStatus() == 0).collect(Collectors.toList());
        //结果项
        for (TbMetaQuickColumnDO tbMetaQuickColumn : tbMetaQuickColumnList) {
            DataSourceHelper.changeToSpecificDataSource(fromEnterpriseConfig.getDbName());
            List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultMapper.getColumnResultList(fromEnterpriseId,
                    Collections.singletonList(tbMetaQuickColumn.getId()));
            tbMetaQuickColumn.setId(null);
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaQuickColumn.setQuestionApproveUser("");
            tbMetaQuickColumn.setQuestionHandlerType(null);
            tbMetaQuickColumn.setQuestionHandlerId("");
            tbMetaQuickColumn.setQuestionRecheckerId("");
            tbMetaQuickColumn.setQuestionRecheckerType(null);
            tbMetaQuickColumn.setQuestionCcType("");
            tbMetaQuickColumn.setQuestionCcId("");
            tbMetaQuickColumn.setQuestionHandlerName("");
            tbMetaQuickColumn.setQuestionRecheckerName("");
            tbMetaQuickColumn.setQuestionCcName("");
            tbMetaQuickColumn.setSopId(0L);
            tbMetaQuickColumn.setCoolCourse("");
            tbMetaQuickColumn.setFreeCourse("");
            tbMetaQuickColumn.setUsePersonInfo("");
            tbMetaQuickColumn.setUseRange(UserRangeTypeEnum.ALL.getType());
            tbMetaQuickColumn.setUseUserids("");
            tbMetaQuickColumn.setCommonEditUserids("");
            tbMetaQuickColumn.setEditTime(new Date());
            tbMetaQuickColumn.setCreateUser("");
            tbMetaQuickColumn.setCreateUserName("");
            tbMetaQuickColumn.setEditUserId("");
            tbMetaQuickColumn.setEditUserName("");
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaQuickColumnMapper.batchInsert(toEnterpriseId, Collections.singletonList(tbMetaQuickColumn));
            if (CollectionUtils.isEmpty(columnResultList)) {
                continue;
            }
            columnResultList.forEach(metaQuickColumnResultDO -> {
                metaQuickColumnResultDO.setMetaQuickColumnId(tbMetaQuickColumn.getId());
                metaQuickColumnResultDO.setCreateUserId("");
                metaQuickColumnResultDO.setCreateTime(new Date());
                metaQuickColumnResultDO.setUpdateTime(new Date());
            });
            DataSourceHelper.changeToSpecificDataSource(toEnterpriseConfig.getDbName());
            tbMetaQuickColumnResultMapper.batchInsert(columnResultList, toEnterpriseId);
        }
        return ResponseResult.success();
    }

    @PostMapping("/updateUseRoleAndAuth")
    public ResponseResult updateUseRoleAndAuth(@RequestParam("enterpriseId") String enterpriseId, @RequestBody OpenApiUpdateUserAuthDTO param) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        enterpriseUserService.updateUseRoleAndAuth(enterpriseConfig.getDingCorpId(), enterpriseId, param);
        return ResponseResult.success();
    }



    @GetMapping("/addRoles")
    public ResponseResult addRoles(@RequestParam("enterpriseId") String enterpriseId, @RequestParam List<String> addRoleIds) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<SysRoleDO> addRoles = new ArrayList<>();
        for (String roleId : addRoleIds) {
            SysRoleDO role = new SysRoleDO();
            role.setRoleName(roleId);
            role.setThirdUniqueId(roleId);
            role.setSource(RoleSourceEnum.SYNC.getCode());
            addRoles.add(role);
        }
        sysRoleDao.addRole(enterpriseId, addRoles);
        return ResponseResult.success();
    }

    @PostMapping(value = "/syncSingleOnePartyStoreAndRegion")
    public ResponseResult syncSingleOnePartyStoreAndRegion(@RequestParam(value = "eid") String eid,
                                                   @RequestParam(value = "code") String code,
                                                   @RequestParam(value = "nodeId") String nodeId) throws ApiException {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        OpStoreAndRegionDTO storeAndRegion = enterpriseInitConfigApiService.getStoreAndRegion(config.getDingCorpId(), config.getAppType(),
                code, nodeId);
        dingDeptSyncService.syncSingleOnePartyStoreAndRegion(config.getEnterpriseId(), storeAndRegion);
        return ResponseResult.success();
    }

    /**
     * 订正区域门店数量单个企业
     *
     * @return
     */
    @GetMapping("/region/storeNum/correct")
    public ResponseResult<Boolean> regionStoreNumCorrect(@RequestParam(value = "eid", required = false) String eid) {
        Long rootId = 1L;

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        regionService.updateRecursionRegionStoreNum(enterpriseConfigDO.getEnterpriseId(), 1L);
        return ResponseResult.success(true);

    }

    /**
     * 同步飞书用户
     */
    @PostMapping(value = "/syncFsUser")
    public ResponseResult<Boolean> syncFsUser(@RequestParam(value = "eid") String eid,
                                              @RequestParam(value = "userId") String userId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        syncSingleUserFacade.asyncFsUser(config.getDingCorpId(), userId, eid, config.getDbName(), config.getAppType());
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * 同步飞书部门用户
     * @return
     */
    @PostMapping(value = "/syncFsDeptUser")
    public ResponseResult syncFsDeptUser(@RequestParam(value = "eid") String eid,
                                         @RequestParam(value = "deptId") String deptId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        List<EnterpriseUserRequest> deptUsers = fsService.getDeptUsers(config.getDingCorpId(), deptId, config.getAppType());
        log.info("获取飞书部门下用户的部门id {} ,返回用户详情列表 {} ", deptId, JSONObject.toJSONString(deptUsers));
        if (CollectionUtils.isEmpty(deptUsers)) {
            return ResponseResult.fail(ErrorCodeEnum.EMPTY_REPORT_PARAM, "部门下没有用户");
        }
        for (EnterpriseUserRequest deptUser : deptUsers) {
            if (Objects.nonNull(deptUser.getEnterpriseUserDO())) {
                try {
                    String userId = deptUser.getEnterpriseUserDO().getUserId();
                    syncSingleUserFacade.asyncFsUser(config.getDingCorpId(), userId, eid, config.getDbName(), config.getAppType());
                } catch (Exception e) {
                    log.error("syncFsDeptUser,当前用户同步失败 {} ", deptUser.getEnterpriseUserDO().getUserId(), e);
                }
            }
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * sendYunDaMsg
     */
    @PostMapping(value = "/sendYunDaMsg")
    public ResponseResult<Boolean> sendYunDaMsg(@RequestBody QuestionOrderDTO questionOrderDTO) throws UnsupportedEncodingException {
        coolCollegeIntegrationApiService.sendYunDaMsg(questionOrderDTO.getEid(), questionOrderDTO.getJobNumList(), questionOrderDTO.getQuestionOrderCode());
        return ResponseResult.success(Boolean.TRUE);
    }

    @GetMapping("/updateMetaTableUser")
    public ResponseResult updateMetaTableUser(@RequestParam(value = "eid") String eid, @RequestParam(value = "tableIds", required = false) List<Long> tableIds){
        DataSourceHelper.changeToMy();
        tbMetaTableService.updateMetaTableUser(eid, tableIds);
        return ResponseResult.success();
    }

    @GetMapping("/updateQuickColumnUseUser")
    public ResponseResult updateQuickColumnUseUser(@RequestParam(value = "eid") String eid){
        DataSourceHelper.changeToMy();
        tbMetaQuickColumnService.updateQuickColumnUseUser(eid);
        return ResponseResult.success();
    }

    @PostMapping("/deleteRedisKey")
    public ResponseResult deleteRedisKey(@RequestParam(value = "eid") String eid,
                                         @RequestParam(value = "key") String key){
        DataSourceHelper.changeToMy();
        redisUtilPool.delKey(key);
        return ResponseResult.success();
    }

    @PostMapping("/getRedisKey")
    public ResponseResult getRedisKey(@RequestParam(value = "eid") String eid,
                                         @RequestParam(value = "key") String key){
        DataSourceHelper.changeToMy();
        String string = redisUtilPool.getString(key);
        return ResponseResult.success(string);
    }

    @PostMapping("/handleSongxiaUserRegion")
    public ResponseResult getRedisKey(@RequestParam(value = "eid") String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserMapper.selectAllList(eid);
        List<String> userIds = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUserDO : enterpriseUserDOS) {
            userIds.add(enterpriseUserDO.getUserId());
        }
        //调用订正用户表字段user_region_ids
        enterpriseUserService.updateUserRegionPathList(eid, userIds);
        return ResponseResult.success();
    }

    @PostMapping(path = "/songxia/getStockInfo")
    public OpenApiResponseVO getStockInfo(@RequestBody SongXiaOpenApiRequest SongXiaOpenApiRequest) {
        SongXiaDTO songXiaDTO = JSONObject.parseObject(SongXiaOpenApiRequest.getBizContent().toJSONString(), SongXiaDTO.class);
        String enterpriseId = SongXiaOpenApiRequest.getEnterpriseId();
        try {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            PageDTO<SongXiaSampleInfoVO> data = songXiaService.getStockInfo(songXiaDTO);
            return OpenApiResponseVO.success(data);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#/songxia/getStockInfo,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @GetMapping(path = "/getScheduleJob")
    public ResponseResult<List<String>> getScheduleJob(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        List<String> deleteEid = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource("coolcollege_scheduler");
        List<String> eidList = dataCorrectionMapper.getJobDistinctEid(enterpriseIds);
        for (String eid : eidList) {
            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
            if(Objects.nonNull(enterpriseConfig)){
                continue;
            }
            deleteEid.add(eid);
            log.info("未找到eid {} 的配置信息", eid);
            DataSourceHelper.changeToSpecificDataSource("coolcollege_scheduler");
            List<ScheduleJobRequest> jobList = dataCorrectionMapper.getJob(eid);
            log.info("获取定时任务 {}", JSON.toJSONString(jobList));
            for (ScheduleJobRequest scheduleJobRequest : jobList) {
                if(scheduleJobRequest.getAction().contains("https://store-api.coolcollege.cn") || scheduleJobRequest.getAction().contains("https://store-api.coolstore.cn")
                        || scheduleJobRequest.getAction().contains("https://hdstore-api.coolstore.cn") || scheduleJobRequest.getAction().contains("https://hdstore-api.coolcollege.cn")){
                    log.info("开始执行定时任务 {}", scheduleJobRequest);
                    scheduleService.deleteSchedule(scheduleJobRequest.getEid(), scheduleJobRequest.getScheduleId());
                }

            }
        }
        return ResponseResult.success(deleteEid);
    }

    @Resource
    private JieFengApiService jieFengApiService;

    @GetMapping("/addAllStoreNode")
    public ResponseResult addAllStoreNode(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        jieFengApiService.addAllStoreNode(enterpriseId);
        return ResponseResult.success();
    }

    @GetMapping("/getAllPassengerFlow")
    public ResponseResult getAllPassengerFlow(@RequestParam(value = "enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        jieFengApiService.getAllPassengerFlow(enterpriseId, "2025-09-24 00:00:00", "2025-09-24 23:59:59");
        return ResponseResult.success();
    }
    @Resource
    private AiModelSceneMapper aiModelSceneMapper;
    @Resource
    private EnterpriseModelAlgorithmMapper enterpriseModelAlgorithmMapper;

    @GetMapping("/ai/correctEnableAiModel")
    public ResponseResult correctEnableAiModel() {

        DataSourceHelper.reset();
        List<EnterpriseSettingDO> enterpriseSettingDOList = enterpriseSettingMapper.selectListExtend();
        ListUtils.emptyIfNull(enterpriseSettingDOList)
                .forEach(enterpriseSettingDO -> {
                    AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
                    List<AIConfigDTO.EnableAIModel> enableAiModelList = aiConfigDTO.getEnableAiModel();
                    enableAiModelList = enableAiModelList.stream()
                            .filter(v -> Boolean.TRUE.equals(v.getEnable())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(enableAiModelList)) {
                        log.info("当前企业 {} 无启用的模型 {}", enterpriseSettingDO.getEnterpriseId(), JSONObject.toJSONString(enableAiModelList));
                        return;
                    }
                    enableAiModelList.forEach(v -> {
                        AiModelSceneDO aiModelSceneDO = aiModelSceneMapper.selectCorrectByModelCode(v.getAiModel());
                        if (Objects.nonNull(aiModelSceneDO)) {
                            log.info("当前企业 {} 启用模型 {}", enterpriseSettingDO.getEnterpriseId(), JSONObject.toJSONString(aiModelSceneDO));
                            EnterpriseModelAlgorithmDO modelAlgorithmDO = enterpriseModelAlgorithmMapper.detail(enterpriseSettingDO.getEnterpriseId(), aiModelSceneDO.getId());
                            if (modelAlgorithmDO == null) {
                                log.info("当前企业 {} 启用模型插入场景数据 {}", enterpriseSettingDO.getEnterpriseId(), JSONObject.toJSONString(aiModelSceneDO));
                                modelAlgorithmDO = EnterpriseModelAlgorithmDO.builder()
                                        .enterpriseId(enterpriseSettingDO.getEnterpriseId())
                                        .sceneId(aiModelSceneDO.getId())
                                        .sceneName(aiModelSceneDO.getSceneName())
                                        .modelName(aiModelSceneDO.getModelName())
                                        .modelCode(aiModelSceneDO.getModelCode())
                                        .userPrompt(aiModelSceneDO.getUserPrompt())
                                        .systemPrompt(aiModelSceneDO.getSystemPrompt()).build();
                                enterpriseModelAlgorithmMapper.insertSelective(modelAlgorithmDO);
                            }
                        } else {
                            log.info("当前企业 {} 启用模型 未找到场景{}", enterpriseSettingDO.getEnterpriseId(), v.getAiModel());
                        }
                    });
                });

        return ResponseResult.success(true);
    }

    @GetMapping("/ai/correctMetaColumnAiModel")
    public ResponseResult correctMetaColumnAiModel() {
        DataSourceHelper.reset();
        List<EnterpriseSettingDO> enterpriseSettingDOList = enterpriseSettingMapper.selectListExtend();
        ListUtils.emptyIfNull(enterpriseSettingDOList)
                .forEach(enterpriseSettingDO -> {
                    AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
                    List<AIConfigDTO.EnableAIModel> enableAiModelList = aiConfigDTO.getEnableAiModel();
                    enableAiModelList = enableAiModelList.stream()
                            .filter(v -> Boolean.TRUE.equals(v.getEnable())).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(enableAiModelList)) {
                        log.info("当前企业 {} 无启用的模型 {}", enterpriseSettingDO.getEnterpriseId(), JSONObject.toJSONString(enableAiModelList));
                        return;
                    }
                    DataSourceHelper.reset();
                    EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(enterpriseSettingDO.getEnterpriseId());
                    DataSourceHelper.changeToSpecificDataSource(config.getDbName());
                    List<TbMetaStaTableColumnDO> metaStaTableColumnDOList = tbMetaStaTableColumnMapper.selectListExtend(enterpriseSettingDO.getEnterpriseId());
                    List<TbMetaQuickColumnDO> quickColumnDOList = tbMetaQuickColumnMapper.selectListExtend(enterpriseSettingDO.getEnterpriseId());
                    if (CollectionUtils.isEmpty(metaStaTableColumnDOList)) {
                        log.info("当前企业 {} 无启用的检查项 ", enterpriseSettingDO.getEnterpriseId());
                    }
                    if (CollectionUtils.isEmpty(quickColumnDOList)) {
                        log.info("当前企业 {} 无启用的快捷检查项 ", enterpriseSettingDO.getEnterpriseId());
                    }
                    metaStaTableColumnDOList.forEach(staTableColumnDO -> {
                        log.info("当前企业 {}, columnId:{}, aiModel:{}, aiSceneId:{}", enterpriseSettingDO.getEnterpriseId(), staTableColumnDO.getId(), staTableColumnDO.getAiModel(), staTableColumnDO.getAiSceneId());
                        if (StringUtils.isNotBlank(staTableColumnDO.getAiModel()) && staTableColumnDO.getAiSceneId() == null) {
                            DataSourceHelper.reset();
                            AiModelSceneDO aiModelSceneDO = aiModelSceneMapper.selectCorrectByModelCode(staTableColumnDO.getAiModel());
                            DataSourceHelper.changeToSpecificDataSource(config.getDbName());

                            if (Objects.nonNull(aiModelSceneDO)) {
                                JSONObject extendInfo = JSONObject.parseObject(staTableColumnDO.getExtendInfo());
                                extendInfo.put(Constants.STORE_WORK_AI.AI_SCENE_ID, aiModelSceneDO.getId());
                                staTableColumnDO.setExtendInfo(extendInfo.toJSONString());
                                tbMetaStaTableColumnMapper.updateByPrimaryKeySelective(enterpriseSettingDO.getEnterpriseId(), staTableColumnDO);
                            } else {
                                log.info("当前企业检查项未匹配{}, columnId:{}, aiModel:{}, aiSceneId:{}", enterpriseSettingDO.getEnterpriseId(), staTableColumnDO.getId(), staTableColumnDO.getAiModel(), staTableColumnDO.getAiSceneId());
                            }
                        }
                    });

                    quickColumnDOList.forEach(quickColumnDO -> {
                        log.info("当前企业 {}, quickColumnId:{}, aiModel:{}, aiSceneId:{}", enterpriseSettingDO.getEnterpriseId(), quickColumnDO.getId(), quickColumnDO.getAiModel(), quickColumnDO.getAiSceneId());
                        if (StringUtils.isNotBlank(quickColumnDO.getAiModel()) && quickColumnDO.getAiSceneId() == null) {
                            DataSourceHelper.reset();
                            AiModelSceneDO aiModelSceneDO = aiModelSceneMapper.selectCorrectByModelCode(quickColumnDO.getAiModel());
                            DataSourceHelper.changeToSpecificDataSource(config.getDbName());

                            if (Objects.nonNull(aiModelSceneDO)) {
                                JSONObject extendInfo = JSONObject.parseObject(quickColumnDO.getExtendInfo());
                                extendInfo.put(Constants.STORE_WORK_AI.AI_SCENE_ID, aiModelSceneDO.getId());
                                quickColumnDO.setExtendInfo(extendInfo.toJSONString());
                                tbMetaQuickColumnMapper.updateByPrimaryKeySelective(enterpriseSettingDO.getEnterpriseId(), quickColumnDO);
                            } else {
                                log.info("当前企业检查项未匹配{}, columnId:{}, aiModel:{}, aiSceneId:{}", enterpriseSettingDO.getEnterpriseId(), quickColumnDO.getId(), quickColumnDO.getAiModel(), quickColumnDO.getAiSceneId());
                            }
                        }
                    });

                });

        return ResponseResult.success(true);
    }
}
