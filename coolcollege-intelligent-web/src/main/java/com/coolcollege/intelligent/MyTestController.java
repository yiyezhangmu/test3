package com.coolcollege.intelligent;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.ScriptUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.facade.consumer.listener.PatrolStoreScoreCountQueueListener;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSalesInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.facade.fsGroup.FsGroupTimerFacade;
import com.coolcollege.intelligent.facade.request.PageRequest;
import com.coolcollege.intelligent.mapper.mq.MqMessageDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.DataSourceEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.mq.MqMessageDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.schedule.ScheduleService;
import com.coolcollege.intelligent.service.songxia.SongXiaService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试入口
 *
 * @ClassName: TestController
 * @Author: xugangkun
 * @Date: 2021/3/26 19:04
 */
@Slf4j
@RestController
@RequestMapping("")
@BaseResponse
public class MyTestController {
    @Autowired
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    private ChatService chatService;

    @Autowired
    private SyncFacade syncFacade;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private WeComService weComService;

    @Resource
    private EnterpriseInitService enterpriseInitService;

    @Resource
    private PatrolStoreService patrolStoreService;

    @Autowired
    private ScriptUtil scriptUtil;
    @Autowired
    private SimpleMessageService simpleMessageService;

    @Autowired
    private TbMetaTableMapper metaTableMapper;
    @Autowired
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Autowired
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private EnterpriseService enterpriseService;
    @Value("${scheduler.callback.task.url}")
    private String appUrl;
    @Resource
    private MqMessageDAO mqMessageDAO;

    @Resource
    private FsGroupTimerFacade fsGroupTimerFacade;

    @Resource
    private SongXiaService songXiaService;

    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private RegionService regionService;
    @Resource
    private StoreService storeService;
    @Resource
    private LoginService loginService;
    @Resource
    private WechatService wechatService;
    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private VideoServiceApi videoServiceApi;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private NsVisitRecordService nsVisitRecordService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private ScheduleService scheduleService;

    @GetMapping(path = "/contact/search")
    public ResponseResult getUserDeptList(@RequestParam(name = "eid") String eid,
                                          @RequestParam(name = "userName", required = false) String userName,
                                          @RequestParam(name = "queryType", required = false, defaultValue = "1") String queryType,
                                          @RequestParam(name = "page_num", required = false, defaultValue = "1") Integer pageNum,
                                          @RequestParam(name = "page_size", required = false, defaultValue = "10") Integer pageSize) {
        DataSourceHelper.reset();
        Map<String, Object> resultMap = new HashMap<>();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        Pair<List<String>, List<Long>> result = chatService.searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), userName, queryType, pageNum, pageSize);
        resultMap.put("userId", result.getKey());
        resultMap.put("deptIds", result.getValue());
        return ResponseResult.success(resultMap);
    }

    @GetMapping(path = "/initFirstDisplayTask")
    public ResponseResult initFirstDisplayTask(@RequestParam(name = "eid") String eid) {
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(Constants.SYSTEM_USER_ID);
        currentUser.setName(Constants.SYSTEM_USER_NAME);
        DataSourceHelper.reset();
        Map<String, Object> resultMap = new HashMap<>();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        List<EnterpriseUserDO> enterpriseUserList = enterpriseUserMapper.getUserByAdmin(eid, true);
        TbMetaTableDO tableDO = new TbMetaTableDO();
        tableDO.setId(2L);
        tableDO.setTableName("全国门店月陈列反馈检查表");
        //初始化陈列任务和数据
        syncFacade.initFirstDisplayTask(eid, tableDO, enterpriseUserList, currentUser);
        return ResponseResult.success(resultMap);
    }

    @GetMapping(path = "/sendOpenSucceededMsg")
    public ResponseResult sendOpenSucceededMsg(@RequestParam(name = "eid") String eid) {
        weComService.sendOpenSucceededMsg(eid);
        return ResponseResult.success();
    }

    @GetMapping(path = "/sendOpenSucceededMsgByMq")
    public ResponseResult sendOpenSucceededMsgByMq(@RequestParam(name = "eid") String eid) {
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("eid", eid);
        simpleMessageService.send(jsonObject.toString(), RocketMqTagEnum.INIT_DEVICE_QUEUE);
        return ResponseResult.success();
    }

    @GetMapping(path = "/runEnterpriseScript")
    public ResponseResult runEnterpriseScript(@RequestParam("corpId") String corpId,
                                              @RequestParam("appType") String appType,
                                              @RequestParam("authUserId") String authUserId,
                                              @RequestParam("dbName") String dbName,
                                              @RequestParam("eid") String eid) {
        EnterpriseOpenMsg msg = new EnterpriseOpenMsg();
        msg.setAppType(appType);
        msg.setCorpId(corpId);
        msg.setEid(eid);
        msg.setAuthUserId(authUserId);
        msg.setDbName(dbName);
        enterpriseInitService.runEnterpriseScript(msg);
        return ResponseResult.success();
    }

    @GetMapping(path = "/onlyRunEnterpriseScript")
    public ResponseResult onlyRunEnterpriseScript(@RequestParam("corpId") String corpId,
                                              @RequestParam("appType") String appType,
                                              @RequestParam("authUserId") String authUserId,
                                              @RequestParam("dbName") String dbName,
                                              @RequestParam("eid") String eid) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //执行脚本代码
        ClassPathResource rc = new ClassPathResource("script/enterpriseInit.sql");
        EncodedResource er = new EncodedResource(rc, "utf-8");
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("enterpriseId", eid);
        String userId = authUserId;
        if (AppTypeEnum.isQwType(appType)) {
            userId = corpId + "_" + authUserId;
        }
        objectObjectHashMap.put("userId", userId);
        String groupId = UUIDUtils.get32UUID();
        objectObjectHashMap.put("groupId", groupId);
        scriptUtil.executeSqlScript(er, objectObjectHashMap);
        return ResponseResult.success();
    }

    @GetMapping(path = "/countScore")
    public ResponseResult countScore(@RequestParam("businessId") Long businessId,
                                     @RequestParam("enterpriseId") String enterpriseId,
                                     @RequestParam("metaTableId") Long metaTableId,
                                     @RequestParam("dataTableId") Long dataTableId) {
        DataSourceHelper.changeToMy();
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        TbMetaTableDO metaTableDO = metaTableMapper.selectById(enterpriseId, metaTableId);
        patrolStoreService.countScore(enterpriseId, tbPatrolStoreRecordDO, metaTableDO, dataTableId);
        return ResponseResult.success();
    }


    @GetMapping(path = "/sendMessage")
    public ResponseResult sendMessage(@RequestParam("enterpriseId") String enterpriseId,@RequestParam("unifyTaskId") Long unifyTaskId,
                                      @RequestParam("storeId") String storeId, @RequestParam("loopCount") Long loopCount, @RequestParam("operate")String operate) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("handlerUserName", "章臣彪");
        jmsTaskService.sendQuestionMessage(enterpriseId, unifyTaskId, storeId, loopCount, operate, hashMap);
        return ResponseResult.success();
    }


    @GetMapping(path = "/enterpriseRecycle")
    public ResponseResult enterpriseRecycle(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        Map<String, String> failEnterprise = new HashMap<>();
        List<String> successList = new ArrayList<>();
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        Date now = new Date();
        List<Integer> enterpriseStatus = Arrays.asList(EnterpriseStatusEnum.DELETED.getCode(), EnterpriseStatusEnum.FREEZE.getCode());
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds, enterpriseStatus);
            PageHelper.clearPage();
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            hasNext = enterpriseConfigList.size() >= pageSize;
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                DataSourceHelper.reset();
                EnterpriseDO enterprise = enterpriseService.selectById(enterpriseConfig.getEnterpriseId());
                if(Objects.isNull(enterprise) || enterprise.getStatus().equals(EnterpriseStatusEnum.NORMAL.getCode()) || DateUtils.dayBetween(enterprise.getUpdateTime(), now) < 30){
                    continue;
                }
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                try {
                    //执行脚本代码
                    ClassPathResource enterpriseResource = new ClassPathResource("script/clearEnterpriseData.sql");
                    EncodedResource enterpriseEncode = new EncodedResource(enterpriseResource, "utf-8");
                    HashMap<String, Object> objectObjectHashMap = new HashMap<>();
                    objectObjectHashMap.put("enterpriseId", enterpriseConfig.getEnterpriseId());
                    scriptUtil.executeSqlScript(enterpriseEncode, objectObjectHashMap);
                    //执行完之后 更新平台库信息  同时将enterprise_config 中的dingcorpId 置为空
                    DataSourceHelper.reset();
                    ClassPathResource platformResource= new ClassPathResource("script/clearPlatformData.sql");
                    EncodedResource platformEncoded = new EncodedResource(platformResource, "utf-8");
                    scriptUtil.executeSqlScript(platformEncoded, objectObjectHashMap);
                    successList.add(enterpriseConfig.getEnterpriseId());
                    CoolHttpClient.doGet(appUrl + "/special/deleteEnterpriseIndexData?enterpriseId=" + enterpriseConfig.getEnterpriseId());
                    CoolHttpClient.doGet(appUrl + "/datareport/home/page/" + enterpriseConfig.getEnterpriseId() + "/deleteEnterpriseData");
                }catch (Exception e){
                    log.info("企业库回收失败:enterpriseId:{}，name:{}", enterprise.getId(), enterprise.getName());
                    failEnterprise.put(enterprise.getId(), enterprise.getName());
                }
            }
        }
        failEnterprise.put("successList", JSONObject.toJSONString(successList));
        return ResponseResult.success(failEnterprise);
    }

    @GetMapping(path = "/dealTask")
    public ResponseResult dealTask(@RequestParam("enterpriseId")String enterpriseId, @RequestParam("subTaskId")Long subTaskId) {
        DataSourceHelper.changeToMy();
        MqMessageDO msgMessage = mqMessageDAO.getMsgMessageBySubTaskId(enterpriseId, subTaskId);
        if(Objects.isNull(msgMessage)){
            return ResponseResult.success("消息为空");
        }
        simpleMessageService.send(msgMessage.getMessage(), RocketMqTagEnum.WORKFLOW_SEND_TOPIC);
        return ResponseResult.success();
    }

    @Resource
    private PatrolStoreScoreCountQueueListener listener;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @GetMapping("/testSendFsCardMsg")
    public void testSendFsCardMsg() {
        com.coolcollege.intelligent.util.datasource.DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId("230579f7726a4edf83106f11de0d7011");
        listener.sendFsCardMsg(enterpriseConfigDO,"230579f7726a4edf83106f11de0d7011",6L,0);
        log.info("sendFsCardMsg success");
    }

    @GetMapping("/testFS")
    public void testFs(@RequestParam String eid){
        try {
        fsGroupTimerFacade.sendFsGroupNotice(eid);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/queryFsGroupNoticeReadNum")
    public void queryFsGroupNoticeReadNum(@RequestParam String eid){
        try {
            fsGroupTimerFacade.queryFsGroupNoticeReadNum(eid);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/getSalesInfo")
    public PageDTO<SongXiaSalesInfoVO> getSalesInfo(@RequestParam(value = "startReportDate", required = false) String startReportDate,
                                                    @RequestParam(value = "endReportDate", required = false) String endReportDate) {
        DataSourceHelper.changeToMy();
        SongXiaDTO songXiaDTO = new SongXiaDTO();
        songXiaDTO.setStartReportDate(startReportDate);
        songXiaDTO.setEndReportDate(endReportDate);
        PageDTO<SongXiaSalesInfoVO> salesInfo = songXiaService.getSalesInfo(songXiaDTO);
        return salesInfo;
    }

    @GetMapping("/getSampleInfo")
    public PageDTO<SongXiaSampleInfoVO> getSampleInfo(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        DataSourceHelper.changeToMy();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);
        PageDTO<SongXiaSampleInfoVO> sampleInfo = songXiaService.getSampleInfo(pageRequest);
        return sampleInfo;
    }

    @PostMapping("{enterprise-id}/addUser")
    public ResponseResult addUser(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiAddUserDTO param){
        DataSourceHelper.changeToMy();
        enterpriseUserService.addUser(enterpriseId, param);
        return ResponseResult.success();
    }

    @PostMapping("{enterprise-id}/insertOrUpdateRegion")
    public ResponseResult insertOrUpdateRegion(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiAddRegionDTO param){
        DataSourceHelper.changeToMy();
        regionService.insertOrUpdateRegion(enterpriseId, param);
        return ResponseResult.success();
    }

    @PostMapping("{enterprise-id}/insertOrUpdateStore")
    public ResponseResult insertOrUpdateStore(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiInsertOrUpdateStoreDTO param){
        DataSourceHelper.changeToMy();
        storeService.insertOrUpdateStore(enterpriseId, param);
        return ResponseResult.success();
    }

    @PostMapping(path = "/{enterpriseId}/getUserAccessToken")
    public ResponseResult getUserAccessToken(@PathVariable("enterpriseId")String enterpriseId, @RequestBody OpenApiGetUserAccessTokenDTO param){
        return ResponseResult.success(loginService.getUserAccessToken(enterpriseId, param));
    }

    @PostMapping(path = "sendWxMessage")
    public ResponseResult sendWxMessage(@RequestBody CoolCollegeMsgDTO param){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId("e17cd2dc350541df8a8b0af9bd27f77d");
        String urlParam = "";
        if (StringUtils.isNotBlank(param.getMessageUrl())) {
            //小程序链接
            String coolCollegeMessageUrl = param.getMessageUrl();
            String replaceUrl = StringUtils.replace(coolCollegeMessageUrl,   "", "corp=" + "ding0591901046fa905a4ac5d6980864d335" + "");
//                    //pc端链接 门店端没有pc端通知  暂时不做
//                    String pcMessageUrl = URLDecoder.decode(dto.getPcMessageUrl(), "UTF-8");
//                    String replacePcUrl = StringUtils.replace(pcMessageUrl, "corp=" + decrypt + "", "corp=" + cropId + "");
            //切割字符串
            urlParam = StringUtils.substringAfter(replaceUrl, "?");
        }
        wechatService.sendWXMsg(enterpriseConfigDO, param, urlParam);
        return ResponseResult.success();
    }

    @GetMapping("getQuestionDetail")
    public OpenApiResponseVO QuestionDetail(@RequestParam("enterpriseId")String enterpriseId, @RequestParam("questionId")Long questionId) {
        try {
            DataSourceHelper.changeToMy();
            return OpenApiResponseVO.success(questionRecordService.questionDetail(enterpriseId,questionId));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    @PostMapping("{enterprise-id}/insertOrUpdateRole")
    public ResponseResult insertOrUpdateRole(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiAddRoleDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(sysRoleService.insertOrUpdateSysRole(enterpriseId, param));
    }

    @PostMapping("{enterprise-id}/deleteRoles")
    public ResponseResult deleteRoles(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiDeleteRolesDTO param){
        DataSourceHelper.reset();
        Boolean enableDingSync = Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) ||
                Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);
        DataSourceHelper.changeToMy();
        List<SysRoleDO> roleList = sysRoleService.getRoleIdByThirdUniqueIds(enterpriseId, param.getThirdUniqueIds());
        List<Long> ids = roleList.stream().map(SysRoleDO::getId).collect(Collectors.toList());
        return ResponseResult.success(sysRoleService.batchDeleteRoles(enterpriseId, "syncUser", ids, enableDingSync));
    }

    @PostMapping("{enterprise-id}/getDeviceStore")
    public ResponseResult getDeviceStore(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiDeviceStoreDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getDeviceStorePage(enterpriseId, param));
    }

    @PostMapping("{enterprise-id}/getVideoInfo")
    public ResponseResult getVideoInfo(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiVideoDTO param){
        DataSourceHelper.changeToMy();
        LiveVideoVO liveVideo = videoServiceApi.getVideoInfo(enterpriseId, param);
        return ResponseResult.success(liveVideo);
    }

    @PostMapping("{enterprise-id}/getPastVideoUrl")
    public ResponseResult getPastVideoUrl(@PathVariable("enterprise-id")String enterpriseId, @RequestBody OpenApiVideoDTO param){
        DataSourceHelper.changeToMy();
        LiveVideoVO pastVideo = videoServiceApi.getPastVideoInfo(enterpriseId, param);
        return ResponseResult.success(pastVideo);
    }

    @PostMapping("{enterprise-id}/getVisitRecordList")
    public ResponseResult getVisitRecordList(@PathVariable("enterprise-id")String enterpriseId, @RequestBody NsVisitRecordDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(nsVisitRecordService.getVisitRecordList(enterpriseId, param));
    }

    @GetMapping(path = "/deleteToken")
    public ResponseResult deleteToken(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds) {
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        DataSourceHelper.reset();
        while(hasNext){
            PageHelper.startPage(pageNum, 10000, false);
            List<String> platformAllUserIds = enterpriseUserMapper.selectPlatformAllUserIds();
            for (String platformAllUserId : platformAllUserIds) {
                redisUtilPool.delKey(platformAllUserId);
            }
            if(CollectionUtils.isEmpty(platformAllUserIds) || platformAllUserIds.size() < 10000){
                hasNext = false;
            }
            pageNum++;
        }
        pageNum = 1;
        hasNext = true;
        List<Integer> enterpriseStatus = Arrays.asList(EnterpriseStatusEnum.DELETED.getCode(), EnterpriseStatusEnum.FREEZE.getCode());
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize, false);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds, enterpriseStatus);
            PageHelper.clearPage();
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            hasNext = enterpriseConfigList.size() >= pageSize;
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                try {
                    List<String> userIds = enterpriseUserMapper.selectAllUserIdsByActive(enterpriseConfig.getEnterpriseId(), null);
                    if(CollectionUtils.isNotEmpty(userIds)){
                        for (String userId : userIds) {
                            redisUtilPool.delKey(userId);
                        }
                    }
                }catch (Exception e){
                    log.info("deleteToken:{}", enterpriseConfig.getEnterpriseId());
                }
            }
        }
        return ResponseResult.success();
    }

    @GetMapping(path = "/deleteSchedule")
    public ResponseResult deleteSchedule(@RequestParam("enterpriseId") String enterpriseId, @RequestParam("scheduleId") String scheduleId){
        Boolean b = scheduleService.deleteSchedule(enterpriseId, scheduleId);
        return ResponseResult.success(b);
    }
}
