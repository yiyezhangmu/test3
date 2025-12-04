package com.coolcollege.intelligent.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.AiResolveBusinessTypeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceEncryptEnum;
import com.coolcollege.intelligent.common.enums.lelecha.LelechaEnterpriseEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.http.YingshiHttpClient;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.aliyun.AliyunPersonMapper;
import com.coolcollege.intelligent.dao.aliyun.PersonNotifyRecordMapper;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.device.EnterpriseDeviceInfoMapper;
import com.coolcollege.intelligent.dao.device.FictitiousInstanceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDepartmentDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaColumnExtendInfoMapper;
import com.coolcollege.intelligent.dao.question.TbQuestionRecordMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.store.StoreDeviceMappingMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dto.AppEnterpriseOpenDto;
import com.coolcollege.intelligent.dto.EnterpriseMqInformConfigDTO;
import com.coolcollege.intelligent.facade.NewSyncFacade;
import com.coolcollege.intelligent.facade.SyncDeptFacade;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateStoreDTO;
import com.coolcollege.intelligent.facade.dto.openApi.QuestionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.QuestionRecordListVO;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.facade.meta.MetaTableColumnFacade;
import com.coolcollege.intelligent.facade.request.supervison.SupervisionRemindRequest;
import com.coolcollege.intelligent.facade.supervison.SupervisionFacadeImpl;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.ai.AICommonPromptDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIResolveRequestDTO;
import com.coolcollege.intelligent.model.ai.AiInspectionResult;
import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetAiResultDTO;
import com.coolcollege.intelligent.model.aianalysis.dto.AiAnalysisRequestDTO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonDO;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunEventDTO;
import com.coolcollege.intelligent.model.authentication.UserAuthScopeDTO;
import com.coolcollege.intelligent.model.baili.request.BailiEmployeeRequest;
import com.coolcollege.intelligent.model.baili.request.BailiOrgRequest;
import com.coolcollege.intelligent.model.baili.request.BailiStoreRequest;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import com.coolcollege.intelligent.model.device.dto.OpenChannelDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.device.dto.OpenTrustDevicePageDTO;
import com.coolcollege.intelligent.model.device.vo.EnterpriseDeviceCountVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbDataStaColumnExtendInfoDO;
import com.coolcollege.intelligent.model.question.dto.QuestionStageDateDTO;
import com.coolcollege.intelligent.model.question.request.RegionQuestionReportRequest;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.senyu.request.SenYuBaseRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuEmployeeInfoRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuStoreRequest;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreGroupDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.storework.request.StoreTaskResolveRequest;
import com.coolcollege.intelligent.model.supervision.dto.HsUserStoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupAddRequest;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiDeviceKitPeoplecountingDTO;
import com.coolcollege.intelligent.mqtt.JfyMqttService;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.rpc.config.VideoLiveApiRpcService;
import com.coolcollege.intelligent.rpc.enterprise.EnterpriseMqInformConfigService;
import com.coolcollege.intelligent.service.activity.ActivityService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.impl.HuoshanAIOpenServiceImpl;
import com.coolcollege.intelligent.service.ai.impl.ShuZiMaLiAiOpenServiceImpl;
import com.coolcollege.intelligent.service.aianalysis.AiAnalysisRuleService;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonService;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.baili.EhrService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.device.auth.OpenAuthStrategyFactory;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.dingtalk.DingOrderService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import com.coolcollege.intelligent.service.jms.JmsSendMessageInfoHelperService;
import com.coolcollege.intelligent.service.jms.JmsSendMessageLogicService;
import com.coolcollege.intelligent.service.jms.vo.JmsSendMessageVo;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreCheckService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.qywxSync.impl.QywxUserSyncServiceImpl;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.senyu.SenYuService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.service.supervison.SupervisionTaskParentService;
import com.coolcollege.intelligent.service.supervison.open.HsStrategyCenterService;
import com.coolcollege.intelligent.service.sync.SyncUtils;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.service.video.YushiDeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.video.openapi.impl.HikCloudOpenServiceImpl;
import com.coolcollege.intelligent.service.video.openapi.impl.YingShiOpenServiceImpl;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolstore.base.utils.CommonContextUtil;
import com.dingtalk.api.response.OapiCallGetuserlistResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageSerializable;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/07
 */
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/test","/v3/enterprises/{enterprise-id}/test","/test"})
@BaseResponse
@Slf4j
public class TestController {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private SimpleMessageService simpleMessageService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private AliyunService aliyunService;

    @Resource
    private AliyunPersonMapper aliyunPersonMapper;

    @Resource
    private FictitiousInstanceMapper fictitiousInstanceMapper;

    @Resource
    private StoreDeviceMappingMapper storeDeviceMappingMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private AuthVisualService authVisualService;
    @Resource
    StoreWorkService storeWorkService;
    @Autowired
    JmsTaskService jmsTaskService;

    @Autowired
    private StoreService storeService;
    @Resource
    HsStrategyCenterService hsStrategyCenterService;
    @Resource
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Resource
    private PersonNotifyRecordMapper personNotifyRecordMapper;

    @Resource
    private DingService dingService;

    @Autowired
    private AliyunPersonService aliyunPersonService;

    @Autowired
    private DingOrderService dingOrderService;

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    @Autowired
    private YushiDeviceService yushiDeviceService;

    @Autowired
    private EhrService ehrService;

    @Autowired
    private SenYuService senYuService;

    @Autowired
    private EnterpriseInitService enterpriseInitService;

    @Autowired
    private EnterpriseUserDepartmentDao enterpriseUserDepartmentDao;
    @Autowired
    private RegionDao regionDao;

    @Autowired
    private UserRegionMappingDAO userRegionMappingDAO;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;
    @Autowired
    private FsService fsService;
    @Resource
    ActivityService activityService;
    private final Integer ONE = 1;

    private final Integer TWO = 2;

    private final Integer THREE = 3;

    private final Integer FOUR = 4;
    @Resource(name = "thirdPartyThreadPool")
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private EnterpriseMqInformConfigService enterpriseMqInformConfigService;
    @Autowired
    private SyncFacade syncFacade;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private SyncUserFacade syncUserFacade;
    @Autowired
    private NewSyncFacade newSyncFacade;
    @Autowired
    private TaskSubMapper taskSubMapper;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Resource
    private JmsSendMessageLogicService jmsSendMessageLogicService;
    @Resource
    private JmsSendMessageInfoHelperService jmsSendMessageInfoHelperService;
    @Autowired
    private RegionService regionService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private VideoLiveApiRpcService videoLiveApiRpcService;

    @Resource
    private VideoServiceApi videoServiceApi;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    @Resource
    private UnifyTaskService unifyTaskService;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private SyncDeptFacade syncDeptFacade;
    @Resource
    private EnterpriseOperateLogService enterpriseOperateLogService;
    @Resource
    private MetaTableColumnFacade metaTableColumnFacade;
    @Resource
    private EnterpriseDeviceInfoMapper enterpriseDeviceInfoMapper;
    @Resource
    private YingShiOpenServiceImpl yingShiOpenServiceImpl;
    @Resource
    private OpenAuthStrategyFactory openAuthStrategyFactory;
    @Resource
    private AiAnalysisRuleService aiAnalysisRuleService;
    @Resource
    private TbDataStaColumnExtendInfoMapper tbDataStaColumnExtendInfoMapper;
    @Resource
    private JfyMqttService jfyMqttService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private UserPersonInfoService userPersonInfoService;
    @Resource
    private AIService aiService;
    @Resource
    private SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private HuoshanAIOpenServiceImpl huoshanAIOpenService;
    @Resource
    private ShuZiMaLiAiOpenServiceImpl shuZhiMaLiAiOpenService;
    @Resource
    private AiInspectionCapturePictureService aiInspectionCapturePictureService;
    @Resource
    private DeviceService deviceService;

    @GetMapping("getPrompt")
    public ResponseResult<AICommonPromptDTO> getPrompt(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("aiCheckStdDesc") String aiCheckStdDesc, @RequestParam("style") String style) {
        AICommonPromptDTO prompt = aiService.getStoreWorkPrompt(enterpriseId, aiCheckStdDesc, style, null);
        return ResponseResult.success(prompt);
    }

    @GetMapping("batchDealStoreWorkAiResolve")
    public ResponseResult<AICommonPromptDTO> batchDealStoreWorkAiResolve(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.changeToMy();
        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDao.selectAiColumn(enterpriseId);
        huoshanAIOpenService.batchDealStoreWorkAiResolve(enterpriseId, DynamicDataSourceContextHolder.getDataSourceType(), swStoreWorkDataTableColumnDOS);
        return ResponseResult.success(null);
    }

    @PostMapping("/ai/analysis/deleteAndAiAnalysis")
    public ResponseResult deleteAndAiAnalysis(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto){
        aiAnalysisRuleService.deleteAndAiAnalysis(enterpriseId, dto.getDate(), dto.getRetryRuleIds());
        return ResponseResult.success();
    }

    @PostMapping("/ai/analysis/deleteAndSubmitCaptureTask")
    public ResponseResult deleteAndSubmitCaptureTask(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto) {
        aiAnalysisRuleService.deleteAndSubmitCaptureTask(enterpriseId, dto.getDate(), dto.getRetryRuleIds());
        return ResponseResult.success();
    }

    @PostMapping("/ai/analysis/reportUserMappingReset")
    public ResponseResult reportUserMappingReset(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto) {
        aiAnalysisRuleService.reportUserMappingReset(enterpriseId, dto.getDate(), dto.getRetryRuleIds());
        return ResponseResult.success();
    }

    @GetMapping("/mqtt/jfy/reconnect")
    public ResponseResult jfyMqttReconnect() {
        jfyMqttService.disconnect();
        return ResponseResult.success(jfyMqttService.connect());
    }

    @GetMapping("/mqtt/jfy/disconnect")
    public ResponseResult jfyMqttDisconnect() {
        return ResponseResult.success(jfyMqttService.disconnect());
    }

    @PostMapping("/ai/analysis/submitCaptureTask")
    public ResponseResult submitCaptureTask(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto) {
        aiAnalysisRuleService.submitCaptureTask(enterpriseId, dto.getDate(), dto.getRetryRuleIds());
        return ResponseResult.success();
    }

    @PostMapping("/ai/analysis/aiAnalysis")
    public ResponseResult aiAnalysis(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto) {
        aiAnalysisRuleService.aiAnalysis(enterpriseId, dto.getDate(), dto.getRetryRuleIds());
        return ResponseResult.success();
    }

    @PostMapping("/ai/analysis/reportPush")
    public ResponseResult reportPush(String enterpriseId, @RequestBody AiAnalysisRequestDTO dto) {
        aiAnalysisRuleService.reportPush(enterpriseId, dto.getPushTime());
        return ResponseResult.success();
    }

    @GetMapping("/redis")
    public ResponseResult testForm(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        redisUtilPool.setString("ceshi123" + enterpriseId, "enterpriseId", 10);
        String zhouyiping = redisUtilPool.getString("ceshi123" + enterpriseId);
        System.out.println(zhouyiping);
        return ResponseResult.success(zhouyiping);
    }

    @GetMapping("/deviceEvent")
    public ResponseResult deviceEvent(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                      @RequestParam("corpId") String corpId,
                                      @RequestParam("eventType") String eventType) {

        AliyunEventDTO dto = new AliyunEventDTO();
        dto.setEventType(eventType);
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setStartTime(1598284800000L);
        dto.setEndTime(System.currentTimeMillis());
        Object o = aliyunService.listEventAlgorithm(corpId, dto);
        log.info("阿里云事件返回结果");
        return ResponseResult.success(o);
    }


    @GetMapping("/child")
    public ResponseResult groupList(
            @RequestParam(value = "id", required = false) Long id) {

        sysMenuService.deleteMenuOrAuthById(id);
        return ResponseResult.success(true);

    }

    @GetMapping("/menu/all")
    public ResponseResult allMenu(
            @RequestParam(value = "id", required = false) Long id) {

        return ResponseResult.success(sysMenuService.getAllMenus());
    }

    @GetMapping("/auth")
    public ResponseResult auth(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestParam(value = "userId", required = false) String userId) {
        DataSourceHelper.changeToMy();
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(enterpriseId, userId);
        return ResponseResult.success(authVisualDTO);

    }

    @GetMapping("/vds/create")
    public ResponseResult testvdsCreate() {
        String result = aliyunService.createVdsProject("测试12345671");
        return ResponseResult.success(result);
    }

    @GetMapping("/vds/bind")
    public ResponseResult testvdsbind(@RequestParam("vdsCorpId") String vdsCorpId,
                                      @RequestParam("deviceList") String deviceList) {
        List<String> deviceIdList = StrUtil.splitTrim(deviceList, ",");
        String enterpriseId = UserHolder.getUser().getEnterpriseId();
//        Object o = aliyunService.bindDeviceToVds(enterpriseId,vdsCorpId, deviceIdList);
        return ResponseResult.success(true);
    }

    @GetMapping("dingScopeUser")
    public Object getDingUserList(String deptId, String accessToken) throws ApiException {
        return dingService.getDeptUserByAsync(deptId, accessToken);
    }

    @GetMapping("/delete/person")
    public Object deltePerson(@PathVariable(value = "enterprise-id") String eid) {
        DataSourceHelper.changeToMy();
        List<AliyunPersonDO> aliyunPersonDOS = aliyunPersonMapper.listAliyunPerson(eid, null);
        List<String> deleteCustomerId = ListUtils.emptyIfNull(aliyunPersonDOS)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getTaskId()))
                .map(AliyunPersonDO::getCustomerId)
                .filter(data -> !StringUtils.equals("f227700ce76643d8a388f0d738f33c2b", data))
                .collect(Collectors.toList());
        ListUtils.emptyIfNull(deleteCustomerId).forEach(data -> {
            aliyunPersonService.deleteAliyunPerson(eid, data);
        });
        return ResponseResult.success(true);
    }

    @GetMapping("/dingOrder/get")
    public ResponseResult getDingOrder(@PathVariable(value = "enterprise-id") String eid,
                                       @RequestParam("orderId") Long orderId,
                                       @RequestParam(value = "appType", required = false) String appType) {
        String dingCorpId = UserHolder.getUser().getDingCorpId();
        return ResponseResult.success(dingOrderService.getOrderDetail(dingCorpId, orderId, appType));
    }

    @GetMapping("/dingOrder/finish")
    public ResponseResult finishDingOrder(@PathVariable(value = "enterprise-id") String eid,
                                          @RequestParam("orderId") Long orderId,
                                          @RequestParam(value = "appType", required = false) String appType) {
        String dingCorpId = UserHolder.getUser().getDingCorpId();
        return ResponseResult.success(dingOrderService.finishOrder(dingCorpId, orderId, appType));
    }

    @GetMapping("/dingOrder/unFinishOrderList")
    public ResponseResult unFinishOrderList(@PathVariable(value = "enterprise-id") String eid,
                                            @RequestParam("itemCode") String itemCode,
                                            @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType) {
        String dingCorpId = UserHolder.getUser().getDingCorpId();
        return ResponseResult.success(dingOrderService.unFinishOrderList(itemCode, 1L, 10L, appType));
    }

    @GetMapping("/auth/getAuthScopes")
    public ResponseResult getAuthScopes(@RequestParam("token") String token) throws ApiException {
        return ResponseResult.success(dingTalkClientService.getAuthScopes(token));
    }

    @GetMapping("/personal/mainCorpId")
    public ResponseResult getMainCorpId(@PathVariable(value = "enterprise-id") String eid,
                                        @RequestParam(value = "userId", required = false) String userId) throws ApiException {
        dingService.getManiCorpId("9cf2ce45bcde3dc58646896748613c48", "izffjwasksmbh3l2jnrcfwka2y");
        return ResponseResult.success(true);
    }

    @GetMapping("/phone/admin/list")
    public ResponseResult phoneTest(@PathVariable(value = "enterprise-id") String eid,
                                    @RequestParam(value = "userId", required = false) String userId,
                                    @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType) throws ApiException {
        DataSourceHelper.reset();
        List<String> userIdList = new ArrayList<>();
        Long startOffset = 0L;
        OapiCallGetuserlistResponse response = dingService.getCallUserList(startOffset, 100L, appType);
        if (response != null && response.isSuccess()) {
            if (CollectionUtils.isNotEmpty(response.getResult().getStaffIdList())) {
                userIdList.addAll(response.getResult().getStaffIdList());
            }
            while (response != null && response.getResult().getHasMore()) {
                startOffset++;
                response = dingService.getCallUserList(startOffset, 100L, appType);
                if (response != null && response.isSuccess()) {

                    if (CollectionUtils.isNotEmpty(response.getResult().getStaffIdList())) {
                        userIdList.addAll(response.getResult().getStaffIdList());
                    }
                }

            }
        }
        return ResponseResult.success(userIdList);
    }

    @GetMapping("/baili/test")
    public ResponseResult listEmployeeBaseInfo() {
        return ResponseResult.success(ehrService.listEmployeeBaseInfo(new BailiEmployeeRequest()));
    }

    @GetMapping("/baili/test/org")
    public ResponseResult listOrg() {
        return ResponseResult.success(ehrService.listOrg(new BailiOrgRequest()));
    }

    @GetMapping("/baili/test/store")
    public ResponseResult liststore(@RequestParam(value = "employeeCode") String employeeCode) {
        BailiStoreRequest request = new BailiStoreRequest();
        request.setEmployeeCode(employeeCode);
        return ResponseResult.success(ehrService.liststoreInfo(request));
    }

    @GetMapping("/senyu/test")
    public ResponseResult senyuTest() {
        return ResponseResult.success(senYuService.test(new SenYuBaseRequest()));
    }

    @GetMapping("/senyu/getEmployeeInfoByIdCard")
    public ResponseResult getEmployeeInfoByIdCard(@RequestParam(value = "idCard", required = false) String idCard) {
        SenYuEmployeeInfoRequest request = new SenYuEmployeeInfoRequest();
        request.setIdCard(idCard);
        return ResponseResult.success(senYuService.getEmployeeInfoByIdCard(request));
    }

    @GetMapping("/senyu/listAllStoreByPage")
    public ResponseResult listAllStoreByPage() {
        SenYuStoreRequest request = new SenYuStoreRequest();
        return ResponseResult.success(senYuService.listAllStoreByPage(request));
    }

    @GetMapping("/senyu/listAuthStores")
    public ResponseResult listAuthStores(@RequestParam(value = "idCard", required = false) String idCard) {
        SenYuEmployeeInfoRequest request = new SenYuEmployeeInfoRequest();
        request.setIdCard(idCard);
        return ResponseResult.success(senYuService.listAuthStores(request));
    }

    @GetMapping("/senyu/listAllRoles")
    public ResponseResult listAllRoles() {
        SenYuBaseRequest request = new SenYuBaseRequest();
        return ResponseResult.success(senYuService.listAllRoles(request));
    }

    @GetMapping("/senyu/listDirectEmployees")
    public ResponseResult listDirectEmployees(@RequestParam(value = "parentCode", required = false) String parentCode,
                                              @RequestParam(value = "idCard", required = false) String idCard) {
        SenYuEmployeeInfoRequest request = new SenYuEmployeeInfoRequest();
        if (StrUtil.isNotEmpty(parentCode)) {
            request.setParentCode(parentCode);
        }
        if (StrUtil.isNotEmpty(idCard)) {
            request.setIdCard(idCard);
        }
        return ResponseResult.success(senYuService.listDirectEmployees(request));
    }

    @GetMapping("/senyu/listEmployeesByRoldIds")
    public ResponseResult listEmployeesByRoldIds(@RequestParam(value = "roleIds", required = false) String roleIds) {
        SenYuEmployeeInfoRequest request = new SenYuEmployeeInfoRequest();
        if (StrUtil.isNotEmpty(roleIds)) {
            request.setRoleIds(roleIds);
        }
        return ResponseResult.success(senYuService.listEmployeesByRoldIds(request));
    }

    @GetMapping("/enterpriseInit")
    public ResponseResult enterpriseInit(@PathVariable(value = "enterprise-id") String eid,
                                         @RequestParam(value = "corpId") String corpId,
                                         @RequestParam(value = "appType") String appType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        enterpriseInitService.enterpriseInit(corpId, AppTypeEnum.getAppType(appType), eid, enterpriseConfigDO.getDbName(), null);
        return ResponseResult.success();
    }

    @GetMapping("/enterpriseInitDepartmentAndRegion")
    public ResponseResult enterpriseInitDepartmentAndRegion(@PathVariable(value = "enterprise-id") String eid,
                                                            @RequestParam(value = "corpId") String corpId,
                                                            @RequestParam(value = "appType") String appType,
                                                            @RequestParam(value = "operation") Integer operation,
                                                            @RequestParam(value = "deptIds", required = false) String deptIdStrs,
                                                            @RequestParam(value = "isScopeChange") Boolean isScopeChange) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        //同步部门和区域
        if (ONE.equals(operation)) {
            enterpriseInitService.enterpriseInitDepartment(corpId, eid, AppTypeEnum.getAppType(appType), enterpriseConfigDO.getDbName());
        }
        //同步部门下的人，以及关系
        if (TWO.equals(operation)) {
            enterpriseInitService.enterpriseInitUser(corpId, eid, AppTypeEnum.getAppType(appType), enterpriseConfigDO.getDbName(), isScopeChange);
        }
        //补全部门和区域下的order值
        if (THREE.equals(operation)) {
            List<String> deptIds = new ArrayList<>();
            if (StringUtils.isNotEmpty(deptIdStrs)) {
                String[] split = deptIdStrs.split(",");
                List<String> list = Arrays.asList(split);
                deptIds = ListUtils.emptyIfNull(list)
                        .stream()
                        .map(s ->s)
                        .collect(Collectors.toList());
            } else {
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                deptIds = sysDepartmentService.selectIdList(eid);
            }
            //过滤根节点
            deptIds.remove(SyncConfig.ROOT_DEPT_ID);
            enterpriseInitService.enterpriseInitDeptOrder(corpId, AppTypeEnum.getAppType(appType), eid, enterpriseConfigDO.getDbName(), deptIds);
        }
        if (FOUR.equals(operation)) {
            enterpriseInitService.onlySyncUser(corpId, eid, AppTypeEnum.getAppType(appType), enterpriseConfigDO.getDbName());
        }
        return ResponseResult.success();
    }

    public static void main(String[] args) {

        List<EnterpriseUserDO> enterpriseUserDOS = new ArrayList<>();

        EnterpriseUserDO enterpriseUserDO1 = new EnterpriseUserDO();
        enterpriseUserDO1.setDepartments("[1,620248021]");
        enterpriseUserDO1.setUserId("1");
        enterpriseUserDOS.add(enterpriseUserDO1);
        EnterpriseUserDO enterpriseUserDO2 = new EnterpriseUserDO();
        enterpriseUserDO2.setDepartments("[1]");
        enterpriseUserDO2.setUserId("1");
        enterpriseUserDOS.add(enterpriseUserDO2);
        for (EnterpriseUserDO enterpriseUserDO : enterpriseUserDOS) {
            if (StringUtils.isBlank(enterpriseUserDO.getDepartments())) {
                continue;
            }
            //处理departments订正
            List<String> departmentsHandler = Arrays.stream(enterpriseUserDO.getDepartments()
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")
                    .split(","))
                    .map(item -> {
                        item = item.startsWith("/") ? item : "/" + item;
                        item = item.endsWith("/") ? item : item + "/";
                        return item;
                    }).collect(Collectors.toList());
            enterpriseUserDO.setDepartments("[" + String.join(",", departmentsHandler) + "]");
        }
        System.out.println(enterpriseUserDOS.toString());
    }

    @PostMapping("/appEnterpriseOpen")
    public ResponseResult appEnterpriseOpen(@RequestBody AppEnterpriseOpenDto dto) {
        String appType = AppTypeEnum.APP.getValue();
        String corpId = appType + UUIDUtils.get32UUID();
        dto.setAppType(appType);
        dto.setCorpId(corpId);
        enterpriseInitConfigApiService.appEnterpriseOpen(dto);
        return ResponseResult.success();
    }

    /**
     * 通讯录授权变更
     * @param enterpeiseId
     * @param corpId
     * @param appType
     * @return
     */
    @GetMapping("/scopeChange")
    public ResponseResult scopeChange(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                      @RequestParam(value = "corpId", required = false) String corpId,
                                      @RequestParam(value = "appType", required = false) String appType,
                                      @RequestParam(value = "permanentCode", required = false) String permanentCode) {
        String authKey = SyncUtils.getAuthKey(corpId);
        Long value = redisUtilPool.setStringIfNotExists(authKey, MDC.get(Constants.REQUEST_ID));
        if(value == 0){
            return ResponseResult.success("有请求正在处理");
        }
        syncFacade.scopeChange(corpId, appType, true, permanentCode);
        return ResponseResult.success();
    }

    // 获取钉钉组织详情
    @GetMapping("/cancelPersonalUpcomingFinish")
    public ResponseResult cancelPersonalUpcomingFinish(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                       @RequestParam(value = "userId", required = false) String userId,
                                                       @RequestParam(value = "status", required = false) String status) {
        if (StringUtils.isBlank(userId)) {
            return ResponseResult.success();
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", enterpriseConfigDO.getDingCorpId());
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<Long> taskSubIdList = taskSubMapper.getSubTaskIdsByUserIdAndStatus(enterpriseId, userId, status);
        jsonObject.put("unifyTaskSubIdList", taskSubIdList);
        jsonObject.put("appType", enterpriseConfigDO.getAppType());
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
        return ResponseResult.success(true);
    }

    /**
     * 推送部门去酷学院
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    @GetMapping("/sendDepartmentsToCoolCollege")
    public ResponseResult sendDepartmentsToCoolCollege(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                       @RequestParam(value = "regionIds", required = false) List<Long> regionIds,
                                                       @RequestParam(value = "regionId", required = false) Long regionId) {

        coolCollegeIntegrationApiService.sendDepartmentsToCoolCollege(enterpriseId, regionIds,regionId);
        return ResponseResult.success();
    }

    /**
     * 推送职位去酷学院
     * @param enterpriseId
     * @param positionIds
     * @return
     */
    @GetMapping("/sendPositionsToCoolCollege")
    public ResponseResult sendPositionsToCoolCollege(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                     @RequestParam(value = "positionIds", required = false) List<Long> positionIds) {
        coolCollegeIntegrationApiService.sendPositionsToCoolCollege(enterpriseId, positionIds);
        return ResponseResult.success();
    }

    /**
     * 删除酷学院 指定职位
     * @param enterpriseId
     * @param positionIds
     * @return
     */
    @GetMapping("/sendDeletePositionsToCoolCollege")
    public ResponseResult sendDeletePositionsToCoolCollege(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                     @RequestParam(value = "positionIds", required = false) List<Long> positionIds) {

        List<SysRoleDO> coolRoleList = Lists.newArrayList();
        positionIds.forEach(positionId -> {
            SysRoleDO sysRoleDO = new SysRoleDO();
            sysRoleDO.setId(positionId);
            sysRoleDO.setRoleName(String.valueOf(positionId));
            sysRoleDO.setUpdateTime(new Date());
            coolRoleList.add(sysRoleDO);
        });
        coolCollegeIntegrationApiService.sendDelPositionsToCoolCollege(enterpriseId,coolRoleList);
        return ResponseResult.success();
    }

    /**
     * 推送人员去酷学院
     * @param enterpriseId
     * @param userDataIds
     * @return
     */
    @GetMapping("/sendUsersToCoolCollege")
    public ResponseResult sendUsersToCoolCollege(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                 @RequestParam(value = "userDataIds", required = false) List<String> userDataIds,
                                                 @RequestParam(value = "regionId", required = false) Long regionId) {
        coolCollegeIntegrationApiService.sendUsersToCoolCollege(enterpriseId, userDataIds,regionId);
        return ResponseResult.success();
    }

    @Autowired
    QuestionRecordService questionRecordService;
    /**
     * 推送人员去酷学院
     * @param enterpriseId
     * @return
     */
    @GetMapping("/questionList")
    public ResponseResult questionList(@PathVariable(value = "enterprise-id") String enterpriseId) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setPageSize(10);
        questionDTO.setPageNum(1);
        PageDTO<QuestionRecordListVO> questionRecordListVOPageDTO = questionRecordService.questionList(enterpriseId, questionDTO);
        return ResponseResult.success();
    }

    @GetMapping("/getAuthInfo")
    public ResponseResult getAuthInfo(@RequestParam("corpId") String corpId, @RequestParam("appType") String appType) throws ApiException {
        return ResponseResult.success(enterpriseInitConfigApiService.getAuthInfo(corpId, appType));
    }


    @GetMapping("/userDetail")
    public ResponseResult userDetial(@PathVariable(value = "enterprise-id") String enterpriseId,
                                     @RequestParam(value = "userId", required = false) String userId,
                                     @RequestParam(value = "dingAccessToken", required = false) String dingAccessToken) {
        OapiV2UserGetResponse.UserGetResponse userDetail = null;
        try {
            userDetail = dingTalkClientService.getUserDetail(userId, dingAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(userDetail));
        return ResponseResult.success(userDetail);
    }

    @GetMapping("/sendOAMessageLogicDynamic")
    public ResponseResult sendOAMessageLogicDynamic(@RequestParam(value = "content",required = false) String content, @RequestParam(value = "userId",required = false)String userId
            , @RequestParam(value = "picUrl", required = false)String picUrl, @RequestParam(value = "title",required = false)String title, @RequestParam(value = "mobileParam", required = false)String mobileParam) {
        DataSourceHelper.changeToMy();
        JmsSendMessageVo jmsSendMessageVo = new JmsSendMessageVo();
        jmsSendMessageVo.setAppType("dingding");
        jmsSendMessageVo.setContent(content);
        jmsSendMessageVo.setDingCorpId("dingef2502a50df74ccc35c2f4657eb6378f");
        jmsSendMessageVo.setMobileParam(mobileParam);
        jmsSendMessageVo.setIsStaticUrl(false);
        jmsSendMessageVo.setContainPcUrl(false);
        jmsSendMessageVo.setOutBusinessId(UUIDUtils.get8UUID());
        jmsSendMessageVo.setTitle(title);
        jmsSendMessageVo.setPicUrl(picUrl);
        jmsSendMessageVo.setUserIds(Arrays.asList("123836131931284423",userId));
        jmsSendMessageVo.setMqQueueName("StoreDingQueue");
        jmsSendMessageInfoHelperService.sendOAMessageLogicDynamic(jmsSendMessageVo, true, false);
        return ResponseResult.success();
    }

    @Autowired
    TbQuestionRecordMapper tbQuestionRecordMapper;
    @PostMapping("/get")
    public ResponseResult get(@PathVariable(value = "enterprise-id") String enterpriseId,
                              @RequestBody RegionQuestionReportRequest request) {
        DataSourceHelper.changeToMy();
        List<QuestionStageDateDTO> questionStageDateDTOS = tbQuestionRecordMapper.selectQuestionStageDate(enterpriseId,request,"");
        return ResponseResult.success();
    }


    @GetMapping("/getDepts")
    public ResponseResult getDepts(@RequestParam("corpId") String corpId,
                                   @RequestParam("appType") String appType,
                                   @RequestParam("deptId") String deptId) {
        return ResponseResult.success(fsService.getSubDepts(deptId,corpId, appType,false));
    }


    @PostMapping("/dayClearTaskResolve")
    public ResponseResult dayClearTaskResolve(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                              @RequestBody StoreTaskResolveRequest storeTaskResolveRequest) {
        DataSourceHelper.changeToMy();
        storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        return ResponseResult.success(Boolean.TRUE);
    }



    @GetMapping("/getUserAuthStoreIdsAndUserIds")
    public ResponseResult getUserAuthStoreIdsAndUserIds(@RequestParam("enterpriseId")String enterpriseId,@RequestParam("userId")String userId){
        DataSourceHelper.changeToMy();
        UserAuthScopeDTO userAuthStoreIdsAndUserIds = authVisualService.getUserAuthStoreIdsAndUserIds(enterpriseId, userId);
        return ResponseResult.success(userAuthStoreIdsAndUserIds);
    }

    @GetMapping("/testStore")
    public ResponseResult testStore(@RequestParam("enterpriseId")String enterpriseId){
        HikCloudOpenServiceImpl bean = CommonContextUtil.getBean(HikCloudOpenServiceImpl.class);
        return ResponseResult.success(bean.getStoreInfo(enterpriseId, AccountTypeEnum.PRIVATE,Constants.INDEX_ONE,Constants.PAGE_SIZE));
    }

    @GetMapping("/deviceCapture")
    public ResponseResult capture(@PathVariable(value = "enterprise-id") String enterpriseId, String deviceId, String channelNo){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.capture(enterpriseId, deviceId, channelNo, null));
    }

    @GetMapping("/getDeviceList")
    public ResponseResult getDeviceList(@PathVariable(value = "enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.getDeviceList(enterpriseId, YunTypeEnum.TP_LINK, AccountTypeEnum.PRIVATE,Constants.INDEX_ONE,Constants.PAGE_SIZE_TEN));
    }

    @GetMapping("/getDeviceDetail")
    public ResponseResult getDeviceDetail(@PathVariable(value = "enterprise-id") String enterpriseId, @RequestParam("deviceId")String deviceId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(videoServiceApi.getDeviceDetail(enterpriseId, deviceId));
    }

    @GetMapping("/getHsToken")
    public ResponseResult getHsToken(@RequestParam("username")String username,
                                     @RequestParam("password")String password){
        long l = System.currentTimeMillis();
        return ResponseResult.success(hsStrategyCenterService.getToken(String.valueOf(l),username,password));
    }

    @GetMapping("/getSupervisorStores")
    public ResponseResult< List<HsUserStoreDTO>> getSupervisorStores(@RequestParam("username")String username,
                                                                     @RequestParam("eid")String eid,
                                                                     @RequestParam("dingDingUserIds")List<String> dingDingUserIds){
        return ResponseResult.success(hsStrategyCenterService.getSupervisorStores(eid,dingDingUserIds));
    }


    @PostMapping("/testData")
    public ResponseResult<Boolean> testData(@RequestBody List<HsUserStoreDTO> list){
        redisUtilPool.setString("testData",JSONObject.toJSONString(list));
        return ResponseResult.success(Boolean.TRUE);
    }

    @GetMapping("/getTestData")
    public ResponseResult<List<HsUserStoreDTO>> getTestData(){
        String testData = redisUtilPool.getString("testData");
        if (StringUtils.isNotEmpty(testData)){
            List<HsUserStoreDTO> hsUserStoreDTOS = JSONObject.parseArray(testData, HsUserStoreDTO.class);
            return ResponseResult.success(hsUserStoreDTOS);
        }

        return ResponseResult.success();
    }

    @Resource
    private SupervisionFacadeImpl supervisionFacadeImpl;


    @PostMapping("/supervisionRemind")
    public ResponseResult<Boolean> supervisionRemind(@RequestBody SupervisionRemindRequest request){
        supervisionFacadeImpl.supervisionRemind(request);
        return ResponseResult.success(true);
    }

    @PostMapping("/supervisionData")
    public ResponseResult<Boolean> supervisionData(@RequestBody SupervisionRemindRequest request){
        supervisionFacadeImpl.supervisionData(request);
        return ResponseResult.success(true);
    }

    @Resource
    SupervisionTaskParentService supervisionTaskParentService;

    @GetMapping("/SupervisionHistoryCorrect")
    public ResponseResult<Boolean> SupervisionHistoryCorrect(@RequestParam("eid")String eid){
        supervisionTaskParentService.SupervisionHistoryCorrect(eid);
        return ResponseResult.success(true);
    }


    @Resource
    EnterpriseUserGroupService enterpriseUserGroupService;
    @ApiOperation(value = "更新")
    @PostMapping("/updateUserGroup")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "更新用户分组")
    public ResponseResult<Boolean> updateUserGroup(@PathVariable("enterprise-id")String enterpriseId,
                                                   @Valid @RequestBody UserGroupAddRequest userGroupAddRequest){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseUserGroupService.updateUserGroup(enterpriseId, userGroupAddRequest.getGroupId(),userGroupAddRequest.getUserIdList()));
    }



    @PostMapping("/updateStoreGroup")
    public Boolean updateStoreGroup(@PathVariable("enterprise-id")String eId,@RequestBody StoreGroupDTO storeGroupDTO){
        DataSourceHelper.changeToMy();
        return storeService.updateStoreGroupStoreList(eId,storeGroupDTO.getGroupId(),storeGroupDTO.getStoreIds());
    }

    @GetMapping("/getCompsMapByAuthRegionIds")
    public ResponseResult< Map<String, String>> getCompsMapByAuthRegionIds(@PathVariable("enterprise-id")String eid,
                                                                     @RequestParam("regionIds")List<String> regionIds){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(regionService.getCompsMapByAuthRegionIds(eid, regionIds));
    }

    @PostMapping("/getFullRegionName")
    public ResponseResult<List<String>> getFullRegionName(@PathVariable("enterprise-id")String eId,@RequestBody List<String> storePathDTOList){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(activityService.getFullRegionNameList(eId,storePathDTOList));
    }

    @PostMapping("/sendMq")
    public ResponseResult<Boolean> sendMq(@RequestBody JSONObject jsonObject, @RequestParam("mqTag")RocketMqTagEnum mqTag){
        simpleMessageService.send(jsonObject.toJSONString(), mqTag);
        return ResponseResult.success();
    }


    @PostMapping("/testcc")
    public ResponseResult cc(String enterpriseId,Integer status){
        try {
            DataSourceHelper.reset();
            EnterpriseMqInformConfigDTO enterpriseMqInformConfigDTO = enterpriseMqInformConfigService.queryByStatus(enterpriseId, status);
            return ResponseResult.success(enterpriseMqInformConfigDTO);
        }catch (Exception e){
            log.error("异常",e);
        }
        return null;
    }


    @Resource
    private com.coolcollege.intelligent.service.ai.HikvisionAIService hikvisionAIService;

    @GetMapping("/aiTest")
    public ResponseResult aiTest(@PathVariable("enterprise-id") String eid,
                                 @RequestParam("picUrl") String picUrl,
                                 @RequestParam("aiType") String aiType) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(hikvisionAIService.aiDetection(eid, picUrl, aiType));
    }

    @GetMapping("/deletedHikUrl")
    public ResponseResult deletedHikRedis(@PathVariable("enterprise-id") String eid,
                                          @RequestParam("deviceId") String deviceId,
                                          @RequestParam("channelId") String channelId) {
        String key = String.format(Constants.HIK_URL, deviceId, channelId);
        return ResponseResult.success(redisUtilPool.delKey(key));
    }

    @GetMapping("/deletedHikAuth")
    public ResponseResult deletedHikAuth(@PathVariable("enterprise-id") String eid) {
        String hik_auth_key = String.format(Constants.HIK_AUTH, eid);
        return ResponseResult.success(redisUtilPool.delKey(hik_auth_key));
    }

   //离职测试
@Resource
private QywxUserSyncServiceImpl qywxUserSyncService;
    @GetMapping(path = "/test")
    public ResponseResult test(String eid){

        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String userId = "wpayJeDAAARqAioXhVZ7UnYeXrg_DG0w_woayJeDAAA4FVy3K1ZOc92YJ-Tu3IlWg";
        String dbName = config.getDbName();
        qywxUserSyncService.syncDeleteWeComUser(eid, userId, dbName);
        return ResponseResult.success();

    }

    @Autowired
    private PatrolStoreCheckService patrolStoreCheckService;

    @GetMapping("/patrolCheck")
    public ResponseResult patrolCheck(@PathVariable("enterprise-id") String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        patrolStoreCheckService.patrolCheck(eid, 6735L, "11");
        return ResponseResult.success();
    }



    @GetMapping("/getDeviceCount")
    public void test2(HttpServletResponse response){
        DataSourceHelper.reset();
        BossEnterpriseExportRequest param = new BossEnterpriseExportRequest();
        param.setTag("数仓");
        List<EnterpriseBossDTO> enterpriseList = enterpriseMapper.listEnterprise(param);
        Integer deviceNum = 0, channelNum = 0;
        List<EnterpriseDeviceCountVO> resultList = new ArrayList<>();
        for (EnterpriseBossDTO enterprise : enterpriseList) {
            String enterpriseId = enterprise.getId();
            DataSourceHelper.changeToSpecificDataSource(enterprise.getDbName());
            Integer count = deviceMapper.count(enterpriseId);
            Integer deviceChannelCount = deviceChannelMapper.getDeviceChannelCount(enterpriseId);
            deviceNum = deviceNum + count;
            channelNum = channelNum + deviceChannelCount;
            resultList.add(new EnterpriseDeviceCountVO(enterpriseId, enterprise.getName(), count, deviceChannelCount, count + deviceChannelCount));
        }
        log.info("设备总数：{}, 子通道总数：{}", deviceNum, channelNum);
        ExportParams params = new ExportParams("企业设备数量", null, ExcelType.XSSF);
        try {
            Workbook sheets = ExcelExportUtil.exportBigExcel(params, EnterpriseDeviceCountVO.class, resultList);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("content-disposition", "attachment; filename=企业设备数量.xlsx");
            OutputStream outputStream = response.getOutputStream();
            sheets.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ExcelExportUtil.closeExportBigExcel();
            } catch (Exception ex) {
                log.info("导出异常");
            }
        }
    }

    @GetMapping("/markDeviceKit")
    public ResponseResult markDeviceKit(@PathVariable(value = "enterprise-id") String enterpriseId,
                                        @RequestParam("deviceId")String deviceId,
                                        @RequestParam("channelNo")String channelNo){
        DataSourceHelper.changeToMy();
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        StoreDTO storeDTO = storeService.getStoreByStoreId(enterpriseId, device.getBindStoreId());
        YunTypeEnum yunTypeEnum = YunTypeEnum.getByCode(device.getResource());
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        return ResponseResult.success(videoServiceApi.markDeviceKit(enterpriseId, device, channelNo, storeDTO.getStoreNum(), yunTypeEnum, accountTypeEnum));
    }

    @GetMapping("/statisticPeoplecounting")
    public ResponseResult<YingshiDeviceKitPeoplecountingDTO> statisticPeoplecounting(@PathVariable(value = "enterprise-id") String enterpriseId,
                                        @RequestParam("deviceId")String deviceId,
                                        @RequestParam("startTime")String startTime,
                                        @RequestParam("endTime")String endTime){
        DataSourceHelper.changeToMy();
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        StoreDTO storeDTO = storeService.getStoreByStoreId(enterpriseId, device.getBindStoreId());
        YunTypeEnum yunTypeEnum = YunTypeEnum.getByCode(device.getResource());
        return ResponseResult.success(videoServiceApi.statisticPeoplecounting(enterpriseId, device, storeDTO.getStoreNum(), startTime, endTime, yunTypeEnum));
    }

    @GetMapping(path = "/dealDeptParentIds")
    public ResponseResult dealDeptParentIds(@RequestParam(value = "enterpriseIds", required = false)List<String> enterpriseIds){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfigDO : enterpriseConfigList) {
                String enterpriseId = enterpriseConfigDO.getEnterpriseId();
                DataSourceHelper.reset();
                EnterpriseSettingVO enterpriseSetting = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
                if(!YesOrNoEnum.YES.getCode().equals(enterpriseSetting.getEnableDingSync())){
                    continue;
                }
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                List<SysDepartmentDO> departmentList = sysDepartmentMapper.selectAll(enterpriseId);
                syncDeptFacade.dealDept(departmentList, enterpriseSetting, Constants.ROOT_DEPT_ID_STR);
                ListUtils.partition(departmentList, 1000).forEach(deptList->sysDepartmentMapper.batchUpdateDept(enterpriseId, deptList));
            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/syncDeptAndUser")
    public void syncDeptAndUser(@RequestParam("enterpriseId") String enterpriseId, @RequestParam(value = "regionId", required = false) Long regionId){
        newSyncFacade.syncDeptAndUser(enterpriseId, null, null, regionId);
    }

    @GetMapping("/syncUser")
    public void syncUser(@RequestParam("enterpriseId") String enterpriseId, @RequestParam(value = "regionId", required = false) Long regionId){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        Boolean isDingType = AppTypeEnum.isDingType(enterpriseConfigDO.getAppType());
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(enterpriseId).operateDesc(isDingType ? "钉钉同步" : "企业微信同步")
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(UserHolder.getUser().getName()).createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_ONGOING).userId(UserHolder.getUser().getUserId()).build();
        enterpriseOperateLogService.insert(logDO);
        EnterpriseSettingVO enterpriseSetting = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        try {
            syncUserFacade.syncSpecifyNodeUser(enterpriseId, regionId, true, enterpriseConfigDO, enterpriseSetting);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/updateMetaTableUser")
    public void updateMetaTableUser(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds,
                                    @RequestParam(value = "excludeEnterpriseIds", required = false) List<String> excludeEnterpriseIds){
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
        for (EnterpriseConfigDO enterpriseConfigDO : enterpriseConfigList){
            if(CollectionUtils.isNotEmpty(excludeEnterpriseIds) && excludeEnterpriseIds.contains(enterpriseConfigDO.getEnterpriseId())){
                continue;
            }
            metaTableColumnFacade.updateMetaTableUser(enterpriseConfigDO.getEnterpriseId());
        }
    }

    @GetMapping("/updateQuickColumnUseUser")
    public void updateQuickColumnUseUser(){
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectByEnterpriseIds(null);
        for (EnterpriseConfigDO enterpriseConfigDO : enterpriseConfigList){
            metaTableColumnFacade.updateQuickColumnUseUser(enterpriseConfigDO.getEnterpriseId());
        }
    }

    @GetMapping("/getDeviceDetailNew")
    public Object getDeviceDetailNew(@RequestParam("enterpriseId")String enterpriseId, @RequestParam("deviceId")String deviceId,
                                    @RequestParam("yunType")YunTypeEnum yunType, @RequestParam("accountType")AccountTypeEnum accountType){
        return videoServiceApi.getDeviceDetail(enterpriseId, deviceId, yunType,accountType);
    }

    @GetMapping("/syncDevice")
    public Object syncDevice(@RequestParam("yunType")YunTypeEnum yunType, @RequestParam("accountType")AccountTypeEnum accountType){
        String enterpriseId = "e17cd2dc350541df8a8b0af9bd27f77d";
        DataSourceHelper.reset();
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.FIFTY_INT;
        boolean isContinue = true;
        while (isContinue){
            try {
                PageInfo<OpenDevicePageDTO> deviceInfo = videoServiceApi.getDeviceList(enterpriseId, yunType, accountType, pageNum, pageSize);
                List<OpenDevicePageDTO> deviceList = Optional.ofNullable(deviceInfo).map(PageSerializable::getList).orElse(org.apache.commons.compress.utils.Lists.newArrayList());
                if(CollectionUtils.isEmpty(deviceList)){
                    log.info("设备列表为空");
                    break;
                }
                if(deviceList.size() < pageSize){
                    isContinue = false;
                }
                pageNum++;
                List<DeviceDO> deviceDOList = new ArrayList<>();
                List<DeviceChannelDO> deviceChannelList = new ArrayList<>();
                for (OpenDevicePageDTO openDevice : deviceList) {
                    OpenDeviceDTO deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, openDevice.getDeviceId(), yunType, accountType);;
                    if(Objects.isNull(deviceDetail)){
                        continue;
                    }
                    DeviceDO deviceDO = OpenDeviceDTO.convertDO(deviceDetail, "system");
                    if(Objects.isNull(deviceDO)){
                        continue;
                    }
                    deviceDO.setDeviceName(StringUtils.isNotBlank(deviceDetail.getDeviceName()) ? deviceDetail.getDeviceName() : openDevice.getDeviceName());
                    deviceDO.setAccountType(AccountTypeEnum.PRIVATE.getCode());
                    List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
                    deviceDO.setHasChildDevice(false);
                    if(CollectionUtils.isNotEmpty(channelList)){
                        deviceDO.setHasChildDevice(true);
                        List<DeviceChannelDO> collect = channelList.stream().map(channel -> OpenChannelDTO.mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                        deviceChannelList.addAll(collect);
                    }
                    deviceDOList.add(deviceDO);
                }
                DataSourceHelper.reset();
                enterpriseDeviceInfoMapper.batchInsertOrUpdateV2(EnterpriseDeviceInfoDO.convertEnterpriseDeviceInfo(enterpriseId, deviceDOList, deviceChannelList));
            } catch (Exception e) {

            }
        }
        return null;
    }

    @GetMapping("/syncTrustDevice")
    public Object syncDevice(@RequestParam("accessToken") String accessToken){
        String enterpriseId = "ying_shi_trust_device_sync";
        DataSourceHelper.reset();
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.FIFTY_INT;
        boolean isContinue = true;
        while (isContinue){
            try {
                PageInfo<OpenTrustDevicePageDTO> deviceInfo = yingShiOpenServiceImpl.getTrustDeviceList(enterpriseId, accessToken, pageNum, pageSize);
                List<OpenTrustDevicePageDTO> deviceList = Optional.ofNullable(deviceInfo).map(PageSerializable::getList).orElse(org.apache.commons.compress.utils.Lists.newArrayList());
                if(CollectionUtils.isEmpty(deviceList)){
                    log.info("设备列表为空");
                    break;
                }
                if(deviceList.size() < pageSize){
                    isContinue = false;
                }
                pageNum++;
                List<EnterpriseDeviceInfoDO> enterpriseDeviceList = OpenTrustDevicePageDTO.convertList(deviceList, YunTypeEnum.YINGSHIYUN_GB, AccountTypeEnum.PRIVATE, enterpriseId);
                DataSourceHelper.reset();
                enterpriseDeviceInfoMapper.batchInsertOrUpdateV2(enterpriseDeviceList);
            } catch (Exception e) {

            }
        }
        return null;
    }

    @GetMapping("/cancelAuth")
    public Object cancelAuth(@RequestParam("accessToken") String accessToken, @RequestParam(value = "parentDeviceId", required = false)String parentDeviceId){
        String enterpriseId = "ying_shi_trust_device_sync";
        DataSourceHelper.reset();
        int pageNum = Constants.INDEX_ONE, pageSize = 100000;
        List<String> deleteDeviceIds = new ArrayList<>();
        List<EnterpriseDeviceInfoDO> allDeviceList = new ArrayList<>();
        List<String> errorDeviceIds = new ArrayList<>();
        boolean isContinue = true;
        while (isContinue){
            try {
                PageHelper.startPage(pageNum, pageSize);
                List<EnterpriseDeviceInfoDO> deviceList = enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceIdV2(enterpriseId, parentDeviceId);
                if(CollectionUtils.isEmpty(deviceList)){
                    log.info("设备列表为空");
                    break;
                }
                if(deviceList.size() < pageSize){
                    isContinue = false;
                }
                Map<String, List<EnterpriseDeviceInfoDO>> parentMap = deviceList.stream().filter(o->Objects.nonNull(o.getParentDeviceId())).collect(Collectors.groupingBy(EnterpriseDeviceInfoDO::getParentDeviceId));
                for (EnterpriseDeviceInfoDO deviceInfo : deviceList) {
                    List<EnterpriseDeviceInfoDO> deleteDevice = new ArrayList<>();
                    if("ipc".equals(deviceInfo.getDeviceType())){
                        if(deleteDeviceIds.contains(deviceInfo.getDeviceId())){
                            continue;
                        }
                        deleteDevice.add(deviceInfo);
                        deleteDeviceIds.add(deviceInfo.getDeviceId());
                    }else{
                        if(deleteDeviceIds.contains(deviceInfo.getParentDeviceId())){
                            continue;
                        }
                        deleteDeviceIds.add(deviceInfo.getParentDeviceId());
                        List<EnterpriseDeviceInfoDO> channelList = null;
                        List<EnterpriseDeviceInfoDO> enterpriseDeviceInfo = parentMap.get(deviceInfo.getDeviceId());
                        if("nvr".equals(deviceInfo.getDeviceType())){
                            //企业存在的通道
                            channelList = enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceIdV3(deviceInfo.getDeviceId());
                            enterpriseDeviceInfo = parentMap.get(deviceInfo.getDeviceId());
                        }
                        if("nvr_ipc".equals(deviceInfo.getDeviceType())){
                            channelList = enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceIdV3(deviceInfo.getParentDeviceId());
                            if((deviceInfo.getDeviceId().endsWith("_1") || deviceInfo.getDeviceId().endsWith("_0")) &&
                                    CollectionUtils.isNotEmpty(channelList) && channelList.size() ==1 && channelList.get(0).getDeviceType().equals("ipc")){
                                continue;
                            }
                            enterpriseDeviceInfo = parentMap.get(deviceInfo.getParentDeviceId());
                        }
                        if(CollectionUtils.isEmpty(channelList)){
                            EnterpriseDeviceInfoDO channel0 = new EnterpriseDeviceInfoDO();
                            channel0.setDeviceId(deviceInfo.getDeviceId());
                            channel0.setParentDeviceId(deviceInfo.getParentDeviceId());
                            channel0.setChannelNo("0");
                            channel0.setDeviceType("nvr_ipc");
                            deleteDevice.add(channel0);
                        }
                        List<String> channelIds = ListUtils.emptyIfNull(channelList).stream().map(EnterpriseDeviceInfoDO::getDeviceId).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(enterpriseDeviceInfo)){
                            enterpriseDeviceInfo = enterpriseDeviceInfo.stream().filter(enterpriseDevice -> !channelIds.contains(enterpriseDevice.getDeviceId())).collect(Collectors.toList());
                            deleteDevice.addAll(enterpriseDeviceInfo);
                        }
                    }
                    allDeviceList.addAll(deleteDevice);
                    log.info("deleteDevice:{}", JSONObject.toJSONString(deleteDevice));
                    List<String> error = yingShiOpenServiceImpl.cancelAuth(accessToken, deleteDevice);
                    if(CollectionUtils.isNotEmpty(error)){
                        errorDeviceIds.addAll(error);
                    }
                }
            } catch (Exception e) {

            }
            log.info("allDeviceList:size:{},{}", allDeviceList.size(), JSONObject.toJSONString(allDeviceList));

            log.info("errorDeviceIds:size:{},{}", errorDeviceIds.size(), JSONObject.toJSONString(errorDeviceIds));
        }
        return null;
    }


    @GetMapping("/deleteDevice")
    public Object deleteDevice(@RequestParam("accessToken")String accessToken){
        String enterpriseId = "e17cd2dc350541df8a8b0af9bd27f77d";
        DataSourceHelper.reset();
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.FIFTY_INT;
        boolean isContinue = true;
        List<String> deleteDeviceIds = new ArrayList<>();
        List<String> canDeleteDeviceIds = new ArrayList<>();
        while (isContinue){
            try {
                String url = "https://open.ys7.com/api/lapp/device/list";
                url = MessageFormat.format(url, accessToken, pageNum, pageSize);
                Map<String, String> map = new HashMap<>(4);
                map.put("pageStart", String.valueOf(pageNum));
                map.put("pageSize", String.valueOf(pageSize));
                map.put("accessToken", accessToken);
                String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
                log.info("getDeviceList result={}",resultStr);
                JSONObject result = JSONObject.parseObject(resultStr);
                JSONArray data = result.getJSONArray("data");
                for (Object datum : data) {
                    JSONObject device = (JSONObject) datum;
                    String deviceSerial = device.getString("deviceSerial");
                    Long addTime = device.getLong("addTime");
                    List<EnterpriseDeviceInfoDO> enterpriseIdsByDeviceIdV3 = enterpriseDeviceInfoMapper.getEnterpriseIdsByDeviceIdV3(deviceSerial);
                    if(CollectionUtils.isEmpty(enterpriseIdsByDeviceIdV3) ){
                        if(addTime <= 1743496008000L && device.getInteger("status") == 0){
                            //2025-04-01之前的设备删除
                            String deleteUrl =  "https://open.ys7.com/api/lapp/device/delete";
                            Map<String, String> deleteMap = new HashMap<>(2);
                            map.put("deviceSerial", deviceSerial);
                            map.put("accessToken", accessToken);
                            String deleteResultStr = YingshiHttpClient.post(deleteUrl, map);
                            JSONObject deleteResult = JSONObject.parseObject(deleteResultStr);
                            if ("200".equals(deleteResult.getInteger("code"))) {
                                log.info("主账号设备取消授权成功：{}", deviceSerial);
                            }
                            deleteDeviceIds.add(deviceSerial);
                        }else{
                            canDeleteDeviceIds.add(deviceSerial);
                        }
                    }
                }
                if(data.size() < pageSize){
                    isContinue = false;
                }
                pageNum++;
            } catch (Exception e) {

            }
        }
        log.info("pageNum:{}", pageNum);
        log.info("deleteDeviceIds:{}, ##{}", deleteDeviceIds.size(), JSONObject.toJSONString(deleteDeviceIds));
        log.info("canDeleteDeviceIds:{}, ##{}", canDeleteDeviceIds.size(), JSONObject.toJSONString(canDeleteDeviceIds));
        return null;
    }

    @GetMapping("/deleteLikesKey")
    public void deleteLikesKey(@RequestParam("likeKey")String likeKey){
        redisUtilPool.delKeysLike(likeKey);
    }

    @GetMapping("/authDevice")
    public Object authDevice(@RequestBody EnterpriseAuthDeviceDO request, @RequestParam String appId){
        return openAuthStrategyFactory.authDevice(request, appId);
    }

    @GetMapping("/cancelDevice")
    public Object cancelDevice(@RequestBody EnterpriseAuthDeviceDO request, @RequestParam String appId){
        return openAuthStrategyFactory.cancelDevice(request, appId);
    }

    @GetMapping("/updateDataStaColumnExtendInfo")
    public void updateDataStaColumnExtendInfo(@RequestParam(value = "enterpriseId", required = true) String enterpriseId,
                                              @RequestParam(value = "dataColumnId", required = true) Long dataColumnId){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        TbDataStaColumnExtendInfoDO dataStaColumnExtendInfoDO = tbDataStaColumnExtendInfoMapper.selectById(enterpriseId, dataColumnId);
        dataStaColumnExtendInfoDO.setAiStatus(2);
        tbDataStaColumnExtendInfoMapper.batchInsertOrUpdateDataColumnExtendInfo(enterpriseId, Collections.singletonList(dataStaColumnExtendInfoDO));
    }

    @PostMapping("/updateStoreInfo")
    public ResponseResult<Boolean> updateStoreInfo(@RequestParam(value = "enterpriseId") String enterpriseId, @RequestBody OpenApiUpdateStoreDTO param){
        DataSourceHelper.changeToMy();
        Boolean b = storeService.updateStoreInfo(enterpriseId, param);
        return ResponseResult.success(b);

    }

    @PostMapping("/asyncAiResolve")
    public ResponseResult<Boolean> asyncAiResolve(@RequestParam(value = "enterpriseId") String enterpriseId, @RequestBody AIResolveRequestDTO param){
        DataSourceHelper.changeToMy();
        shuZhiMaLiAiOpenService.asyncAiResolve(enterpriseId, AiResolveBusinessTypeEnum.PATROL, param);
        return ResponseResult.success();
    }

    @PostMapping("/getAiResult")
    public ResponseResult<ShuZhiMaLiGetAiResultDTO> getAiResult(@RequestParam(value = "enterpriseId") String enterpriseId, @RequestBody AIResolveRequestDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(shuZhiMaLiAiOpenService.getAiResult(enterpriseId, AiResolveBusinessTypeEnum.PATROL, param));
    }

    @GetMapping("/aiInspectionResolve")
    public AiInspectionResult aiInspectionResolve(@RequestParam("enterpriseId") String enterpriseId,
                                                  @RequestParam("sceneId")Long sceneId,
                                                  @RequestParam("imageList")  List<String> imageList){
        return aiService.aiInspectionResolve(enterpriseId, sceneId, imageList);
    }
    @GetMapping("/capturePicture")
    public ResponseResult capturePicture(@RequestParam("enterpriseId") String enterpriseId,

                                           @RequestParam("captureTime")String captureTime){
        DataSourceHelper.changeToMy();
        aiInspectionCapturePictureService.capturePicture(enterpriseId, captureTime);
        return ResponseResult.success();
    }

    @GetMapping("/aiInspectionResolveBatch")
    public ChatCompletionResult aiInspectionResolveBatch(@RequestParam("enterpriseId") String enterpriseId,
                                           @RequestParam("sceneId")Long sceneId,
                                           @RequestParam("imageList")  List<String> imageList) {
        DataSourceHelper.reset();
        ChatCompletionResult result = huoshanAIOpenService.aiInspectionResolve(enterpriseId, sceneId, imageList);
        log.info("result:{}", JSONObject.toJSONString(result));
        return result;
    }

    @GetMapping("/aiAsyncInspectionResolve")
    public AIResolveDTO aiAsyncInspectionResolve(@RequestParam("enterpriseId") String enterpriseId,
                                                         @RequestParam("sceneId")Long sceneId,
                                                         @RequestParam("imageList")  List<String> imageList) {
        DataSourceHelper.reset();
        AIResolveDTO aiResolveDTO = aiService.aiAsyncInspectionResolve(enterpriseId, sceneId, imageList, 1011L);
        log.info("result:{}", JSONObject.toJSONString(aiResolveDTO));
        return aiResolveDTO;
    }


    @GetMapping("/queryDeviceCaptureResult")
    public void queryDeviceCaptureResult(@RequestParam("enterpriseId") String enterpriseId) {
        DataSourceHelper.reset();
        aiInspectionCapturePictureService.queryDeviceCaptureResult(enterpriseId);
    }

    @GetMapping("/initRedis")
    public void initRedis(){
        for (LelechaEnterpriseEnum value : LelechaEnterpriseEnum.values()) {
            log.info("key:{}, value:{}",  value.getCode(), value.getMessage());
            redisUtilPool.hashSet(RedisConstant.STORE_ARE_NON_LEAF_NODES, value.getCode(), value.getMessage());
        }
    }

    @GetMapping("/refreshAllDevice")
    public void refreshAllDevice(){
        DataSourceHelper.reset();
        BossEnterpriseExportRequest param = new BossEnterpriseExportRequest();
        param.setTag("数仓");
        List<EnterpriseBossDTO> enterpriseList = enterpriseMapper.listEnterprise(param);
        for (EnterpriseBossDTO enterprise : enterpriseList) {
            String enterpriseId = enterprise.getId();
            DataSourceHelper.changeToSpecificDataSource(enterprise.getDbName());
            deviceService.refreshAllDevice(enterpriseId);
        }
    }


}
