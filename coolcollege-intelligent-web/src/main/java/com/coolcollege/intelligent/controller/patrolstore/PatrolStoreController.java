package com.coolcollege.intelligent.controller.patrolstore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMqInformConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.vo.TbRecordVO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolOverviewDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.StopTaskDTO;
import com.coolcollege.intelligent.model.patrolstore.param.*;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolRecordAuthDTO;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayDeleteParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordDeleteVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * @author yezhe
 * @date 2020-12-08 17:56
 */

@Api(tags = "巡店")
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/patrolstore/patrolStore",
        "/v3/enterprises/{enterprise-id}/patrolstore/patrolStore"})
@BaseResponse
@Slf4j
public class PatrolStoreController {

    @Resource
    private PatrolStoreService patrolStoreService;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;
    @Resource
    private EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private PatrolStoreStatisticsService patrolStoreStatisticsService;

    @Resource
    private EnterpriseConfigMapper configMapper;

    @Resource
    private EnterpriseMqInformConfigMapper enterpriseMqInformConfigMapper;

    @Resource
    SendCardService sendCardService;
    @Resource
    EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    /**
     * 巡店任务初始化
     *
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/build")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "巡店任务初始化")
    public ResponseResult build(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestBody @Valid PatrolStoreBuildParam param) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        param.setStoreCheckSettingDO(enterpriseStoreCheckSettingDO);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.buildPatrolStore(enterpriseId, param));
    }

    /**
     * 门店签到
     *
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/signIn")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "门店签到")
    public ResponseResult signIn(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody @Valid PatrolStoreSignInParam param) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        param.setStoreCheckSetting(settingDO);
        param.setDingCorpId(config.getDingCorpId());
        param.setAppType(config.getAppType());
        return ResponseResult.success(patrolStoreService.signIn(enterpriseId, param));
    }

    /**
     * 门店签退
     *
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/signOut")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "门店签退")
    public ResponseResult signOut(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestBody @Valid PatrolStoreSignOutParam param) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.reset();
        // 企业配置
//        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
//                enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        Long businessId = patrolStoreService.signOut(config.getDingCorpId(), enterpriseId, param, storeCheckSettingDO, user.getUserId(), user.getName(), config.getAppType(), enterpriseSettingDO);
        return ResponseResult.success(true);
    }

    /**
     * 放弃本次巡店
     * 注释操作日志
     *
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/giveUp")
//    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "放弃本次巡店")
    public ResponseResult giveUp(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody @Valid PatrolStoreGiveUpParam param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.giveUp(enterpriseId, param.getBusinessId()));

    }

    /**
     * 巡店中配置检查表
     *
     * @param enterpriseId
     * @param param
     * @return
     */
    @PostMapping(path = "/configMetaTable")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "巡店中配置检查表")
    public ResponseResult configMetaTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          @RequestBody @Valid MetaTableConfigParam param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.configMetaTable(enterpriseId, param));
    }

    /**
     * 巡店内容提交
     * 注释操作日志
     */
    @PostMapping(path = "/submit")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "巡店内容提交")
    public ResponseResult submit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                 @RequestBody @Valid PatrolStoreSubmitParam param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.submit(enterpriseId, param, UserHolder.getUser().getUserId()));
    }

    /**
     * 线上巡店内容提交
     */
    @Deprecated
    @PostMapping(path = "/submitOnline")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "线上巡店内容提交")
    public ResponseResult submitAutoOnline(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody @Valid PatrolStoreSubmitOnlineParam param) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        boolean isMq = enterpriseMqInformConfigMapper.queryByStatus(enterpriseId, EnterpriseStatusEnum.NORMAL.getCode()) != null;
        DataSourceHelper.changeToMy();
        Long businessId = patrolStoreService.submitOnline(config.getDingCorpId(), enterpriseId, param, storeCheckSettingDO, user.getUserId(), user.getName(), config.getAppType(), enterpriseSettingDO);
        return ResponseResult.success(businessId);
    }

    /**
     * 巡店审核
     */
    @PostMapping(path = "/audit")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "巡店审核")
    public ResponseResult audit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                @RequestBody @Valid PatrolStoreAuditParam param) {
        DataSourceHelper.changeToMy();
        patrolStoreService.audit(enterpriseId, UserHolder.getUser(), param);
        return ResponseResult.success(null);
    }

    /**
     * 结束巡店(提交审核)
     */
    @PostMapping(path = "/overPotral")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_ADD, operateDesc = "结束巡店")
    @SysLog(func = "提交", opModule = OpModuleEnum.PATROL_STORE_FORM, opType = OpTypeEnum.INSERT)
    public ResponseResult overPotral(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                     @RequestBody @Valid PatrolStoreOverParam patrolStoreOverParam) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        //自主巡店&视频巡店工作通知
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        patrolStoreService.overPatrol(enterpriseId, patrolStoreOverParam.getBusinessId(), UserHolder.getUser().getUserId(), UserHolder.getUser().getName(),
                config.getDingCorpId(), false, config.getAppType(), patrolStoreOverParam.getSignatureUser(),storeCheckSettingDO, enterpriseSettingDO);
        return ResponseResult.success(null);
    }

    /**
     * 巡店记录详情
     */
    @GetMapping(path = "/recordInfo")
    public ResponseResult<TbPatrolStoreRecordVO> recordInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                            Long businessId, String access_token, String key) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(patrolStoreService.recordInfo(enterpriseId, businessId, access_token, key, settingDO));
    }

    /**
     * 巡店记录详情分享时间设置
     */
    @GetMapping(path = "/recordInfoShareExpire")
    public ResponseResult recordInfoShare(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                          Long businessId, String key) {
        return ResponseResult.success(patrolStoreService.recordInfoShare(enterpriseId, businessId, key));
    }

    /**
     * 巡店检查表详情
     */
    @ApiOperation("巡店检查表详情")
    @GetMapping(path = "/dataTableInfoList")
    public ResponseResult<List<DataTableInfoDTO>> dataTableInfoList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                    Long businessId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String userId = "";
        if (UserHolder.getUser() != null) {
            userId = UserHolder.getUser().getUserId();
        }
        return ResponseResult.success(patrolStoreService.dataTableInfoList(enterpriseId, businessId, userId));
    }

    @PostMapping(path = "listStoreTaskMap")
    public ResponseResult listStoreTaskMap(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                           @RequestBody StoreTaskMapParam param) {
        DataSourceHelper.changeToMy();
        List<StoreTaskMapVO> result = patrolStoreService.listStoreTaskMap(enterpriseId, param);
        return ResponseResult.success(result);
    }

    /**
     * 获取未完成的巡店下的所有检查表
     *
     * @param enterpriseId
     * @param storeId
     * @param subTaskId
     * @return
     */
    @ApiOperation("获取未完成的巡店下的所有检查表")
    @GetMapping("/getPatrolMetaTable")
    public ResponseResult getPatrolMetaTable(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestParam(value = "storeId", required = false, defaultValue = "") String storeId,
                                             @RequestParam(value = "subTaskId", required = false) Long subTaskId,
                                             @RequestParam(required = false) String patrolType, @RequestParam(value = "businessId", required = false) Long businessId) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO settingDO = enterpriseStoreCheckSettingService.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        TbRecordVO result = patrolStoreService.getPatrolMetaTable(enterpriseId, storeId, user, subTaskId, patrolType, settingDO, businessId);
        return ResponseResult.success(result);
    }

    /**
     * 获取巡店记录列表
     */
    @PostMapping("/getRecordList")
    public ResponseResult getRecordList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody RecordListRequest recordListRequest) {
        DataSourceHelper.changeToMy();
        recordListRequest.setUserId(UserHolder.getUser().getUserId());
        recordListRequest.setUserName(UserHolder.getUser().getName());
        PageInfo pageInfo = patrolStoreService.getRecordList(enterpriseId, recordListRequest, UserHolder.getUser().getSysRoleDO());
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    /**
     * 获取巡店记录列表
     */
    @GetMapping("/getRecordAuth")
    public ResponseResult getRecordAuth(@PathVariable("enterprise-id") String enterpriseId, @RequestParam(value = "businessId", required = false) Long businessId,
                                        @RequestParam(value = "subTaskId", required = false) Long subTaskId) {
        DataSourceHelper.changeToMy();
        PatrolRecordAuthDTO recordAuth = patrolStoreService.getRecordAuth(enterpriseId, UserHolder.getUser(), businessId, subTaskId);
        return ResponseResult.success(recordAuth);
    }

    /**
     * 根据检查项获取巡店记录列表
     */
    @PostMapping("/getRecordListByMetaStaColumnId")
    public ResponseResult getRecordListByMetaStaColumnId(@PathVariable("enterprise-id") String enterpriseId, @RequestBody RecordByMetaStaColumnIdRequest request) {
        DataSourceHelper.changeToMy();
        RecordByCheckColumnIdVO recordListByMetaStaColumnId = patrolStoreService.getRecordListByMetaStaColumnId(enterpriseId, request);
        return ResponseResult.success(recordListByMetaStaColumnId);
    }

    /**
     * 移动端获取工单列表
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @PostMapping("/getSimpleQuestionList")
    public ResponseResult getQuestionList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody QuestionListRequest request) {
        DataSourceHelper.changeToMy();
        List<QuestionListVO> questionListVO = patrolStoreService.getSimpleQuestionList(enterpriseId, request);
        return ResponseResult.success(new PageInfo<>(questionListVO));
    }

    @PostMapping("/getOperateQuestionList")
    public ResponseResult getOperateQuestionList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody QuestionListRequest request) {
        DataSourceHelper.changeToMy();
        List<QuestionListVO> questionListVO = patrolStoreService.getOperateQuestionList(enterpriseId, request);
        return ResponseResult.success(new PageInfo<>(questionListVO));
    }

    /**
     * 获取巡店记录列表
     */
    @PostMapping("/getPatrolRecordList")
    public ResponseResult getPatrolRecordList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolRecordRequest recordRecordRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        recordRecordRequest.setUserId(currentUser.getUserId());
        recordRecordRequest.setUserName(currentUser.getName());
        recordRecordRequest.setDbName(enterpriseConfigDO.getDbName());
        recordRecordRequest.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        PageInfo pageInfo = patrolStoreService.getPatrolRecordList(enterpriseId, recordRecordRequest, currentUser);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }


    @GetMapping("/getPatrolRecordData")
    public ResponseResult<PatrolRecordDataVO> getPatrolRecordData(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return  ResponseResult.success(patrolStoreService.getPatrolRecordData(enterpriseId, UserHolder.getUser()));
    }




    /**
     * 按人任务----巡店记录列表
     */
    @ApiOperation("按人任务-巡店记录列表")
    @PostMapping("/getStaffPlanPatrolRecordList")
    public ResponseResult getStaffPlanPatrolRecordList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody StaffPlanPatrolRecordRequest staffPlanPatrolRecordRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        staffPlanPatrolRecordRequest.setUserId(currentUser.getUserId());
        staffPlanPatrolRecordRequest.setUserName(currentUser.getName());
        staffPlanPatrolRecordRequest.setDbName(enterpriseConfigDO.getDbName());
        PageInfo pageInfo = patrolStoreService.getStaffPlanPatrolRecordList(enterpriseId, staffPlanPatrolRecordRequest, currentUser);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    @GetMapping(path = "/taskStoreRecord")
    @SysLog(func = "详情", subFunc = "催办", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.PATROL_STORE_TASK_STORE_REMIND)
    public ResponseResult<TbPatrolStoreRecordVO> taskStoreRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                 @RequestParam(value = "taskStoreId", required = false) Long taskStoreId,
                                                                 @RequestParam(value = "businessId", required = false) Long businessId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.taskRecordInfo(enterpriseId, taskStoreId, businessId));
    }

    /**
     * 阶段任务巡店记录列表
     *
     * @param enterpriseId
     * @return
     */
    @ApiOperation("任务阶段记录集合")
    @GetMapping(path = "/taskStageRecordList")
    public ResponseResult taskStageRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @RequestParam("unifyTaskId") Long unifyTaskId,
                                              @RequestParam("loopCount") Long loopCount,
                                              @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {

        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        boolean levelInfo = false;
        if (StringUtils.isNotBlank(storeCheckSettingDO.getLevelInfo())) {
            JSONObject jsonObject = JSONObject.parseObject(storeCheckSettingDO.getLevelInfo());
            levelInfo = jsonObject.getBoolean("open") == null ? false : jsonObject.getBoolean("open");
        }
        return ResponseResult.success(patrolStoreService.taskStageRecordList(enterpriseId, unifyTaskId, loopCount, pageNum, pageSize, levelInfo));
    }

    /**
     * 阶段任务巡店记录列表导出
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/taskStageRecordListExport")
    @SysLog(func = "数据", subFunc = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-巡店任务")
    public ResponseResult taskStageRecordListExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestParam("unifyTaskId") Long unifyTaskId, @RequestParam("loopCount") Long loopCount) {

        DataSourceHelper.changeToMy();
        PatrolStoreStatisticsDataTableQuery query = new PatrolStoreStatisticsDataTableQuery();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setTaskId(unifyTaskId);
        query.setLoopCount(loopCount);
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        boolean levelInfo = false;
        if (StringUtils.isNotBlank(storeCheckSettingDO.getLevelInfo())) {
            JSONObject jsonObject = JSONObject.parseObject(storeCheckSettingDO.getLevelInfo());
            levelInfo = jsonObject.getBoolean("open") == null ? false : jsonObject.getBoolean("open");
        }
        query.setLevelInfo(levelInfo);
        DataSourceHelper.changeToMy();
        ImportTaskDO result = patrolStoreStatisticsService.taskStageRecordListExport(enterpriseId, query);
        return ResponseResult.success(result);
    }

    /**
     * 阶段任务巡店记录列表明细详情列表
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/taskStageRecordDetailList")
    public ResponseResult taskStageRecordDetailList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestParam(value = "businessId", required = false) Long businessId,
                                                    @RequestParam(value = "metaTableId", required = false) Long metaTableId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.taskStageRecordDetailList(enterpriseId, businessId, metaTableId, pageNum, pageSize));
    }

    /**
     * 阶段任务巡店记录列表明细详情列表
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping(path = "/taskStageRecordDetailListExport")
    public ResponseResult taskStageRecordDetailListExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                          @RequestParam("businessId") Long businessId) {

        DataSourceHelper.changeToMy();
        patrolStoreStatisticsService.taskStageRecordDetailListExport(enterpriseId, businessId, UserHolder.getUser().getDbName());
        return ResponseResult.success(null);
    }


    /**
     * 巡店记录转交
     *
     * @param enterpriseId
     * @param recordTurnRequest
     * @return
     */
    @PostMapping(path = "/turn")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "巡店记录转交")
    public ResponseResult turnPatrolStoreRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestBody @Validated PatrolStoreRecordTurnRequest recordTurnRequest) {
        log.info("#turn body is ={}", JSON.toJSONString(recordTurnRequest));
        DataSourceHelper.changeToMy();
        patrolStoreService.turnPatrolStoreRecord(enterpriseId, recordTurnRequest, UserHolder.getUser());
        return ResponseResult.success(true);
    }


//    /**
//     * 移动端获取工单列表
//     * @param enterpriseId
//     * @param request
//     * @return
//     */
//    @PostMapping("/getQuestionListByRegion")
//    public ResponseResult getQuestionListByRegion(@PathVariable("enterprise-id")String enterpriseId,
//                                                  @RequestBody @Valid QuestionListRequest request){
//        DataSourceHelper.changeToMy();
//        List<QuestionListVO> questionListVO = patrolStoreService.getSimpleQuestionListByRegion(enterpriseId,request);
//        return ResponseResult.success(questionListVO);
//    }


    /**
     * 巡店记录操作历史列表
     *
     * @param enterpriseId
     * @param patrolStoreOverParam
     * @return
     */
    @PostMapping(path = "/historyExecutionQuery")
    public ResponseResult historyExecutionQuery(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestBody PatrolStoreOverParam patrolStoreOverParam) {
        log.info("historyExecutionQuery.enterprise-id ={},business-id={}", enterpriseId, patrolStoreOverParam.getBusinessId());
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (enterpriseConfigDO != null) {
            //切到指定的库
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        }
        List<TbPatrolStoreHistoryDo> tbPatrolStoreHistoryMappers = patrolStoreService.selectPatrolStoreHistoryList(enterpriseId, String.valueOf(patrolStoreOverParam.getBusinessId()));
        if (!StringUtils.isBlank(patrolStoreOverParam.getOpenConversionId())) {
            try {
                //当前结束巡店的这条任务的巡店记录详情
                List<DataTableInfoDTO> dataTableInfoDTOS = patrolStoreService.dataTableInfoList(enterpriseId, patrolStoreOverParam.getBusinessId(), UserHolder.getUser().getUserId());
                sendCardService.sendCardOfOne(enterpriseId, patrolStoreOverParam.getOpenConversionId(), dataTableInfoDTOS, patrolStoreOverParam.getBusinessId(), enterpriseConfigDO.getDingCorpId());
            } catch (Exception e) {
                log.info(e.getMessage());
                return ResponseResult.success(tbPatrolStoreHistoryMappers);
            }
        }
        return ResponseResult.success(tbPatrolStoreHistoryMappers);
    }

    /**
     * 获取我的自主巡店记录
     *
     * @param enterpriseId
     * @param recordRecordRequest
     * @return
     */
    @PostMapping("/getAutonomyPatrolRecordList")
    public ResponseResult getAutonomyPatrolRecordList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolRecordRequest recordRecordRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        if (CollectionUtils.isEmpty(recordRecordRequest.getUserIdList())) {
            recordRecordRequest.setUserId(currentUser.getUserId());
            recordRecordRequest.setSupervisorId(currentUser.getUserId());
        } else {
            recordRecordRequest.setUserId(recordRecordRequest.getUserIdList().get(0));
            recordRecordRequest.setSupervisorId(recordRecordRequest.getUserIdList().get(0));
        }
        recordRecordRequest.setUserName(currentUser.getName());
        recordRecordRequest.setDbName(enterpriseConfigDO.getDbName());
        PageInfo pageInfo = patrolStoreService.getAutonomyPatrolRecordList(enterpriseId, recordRecordRequest, currentUser);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }


    /**
     * 获取个人已完成巡店记录
     *
     * @param enterpriseId
     * @param recordRecordRequest
     * @return
     */
    @PostMapping("/getCompletePatrolRecordList")
    public ResponseResult getCompletePatrolRecordList(@PathVariable("enterprise-id") String enterpriseId,
                                                      @RequestBody PatrolRecordRequest recordRecordRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        if (StringUtils.isBlank(recordRecordRequest.getSupervisorId())) {
            recordRecordRequest.setSupervisorId(currentUser.getUserId());
        }
        PageInfo pageInfo = patrolStoreService.getCompletePatrolRecordList(enterpriseId, recordRecordRequest);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    /**
     * 根据检查项获取巡店记录列表
     */
    @PostMapping("/getGroupDataByStore")
    public ResponseResult getGroupDataByStore(@PathVariable("enterprise-id") String enterpriseId, @RequestBody RecordByMetaStaColumnIdRequest request) {
        DataSourceHelper.changeToMy();
        PageInfo pageInfo = patrolStoreService.getGroupDataByStore(enterpriseId, request);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }


    @PostMapping("/getPatrolStoreDetail")
    public ResponseResult getPatrolStoreDetail(@PathVariable("enterprise-id") String enterpriseId,
                                               @RequestBody PatrolStoreDetailRequest patrolStoreDetailRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.changeToMy();
        PageDTO<PatrolStoreDetailExportVO> patrolStoreDetail = patrolStoreService.getPatrolStoreDetail(enterpriseId, patrolStoreDetailRequest);
        return ResponseResult.success(patrolStoreDetail);
    }

    @PostMapping("/getPatrolStoreDetailExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-基础报表-巡店明细导出")
    public ResponseResult getPatrolStoreDetailExport(@PathVariable("enterprise-id") String enterpriseId,
                                                     @RequestBody PatrolStoreDetailRequest patrolStoreDetailRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        ImportTaskDO patrolStoreDetailExport = patrolStoreStatisticsService.getPatrolStoreDetailExport(enterpriseId, patrolStoreDetailRequest, user);
        return ResponseResult.success(patrolStoreDetailExport);
    }

    @ApiOperation("巡店催办")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "父任务id", required = true, dataType = "Long"),
    })
    @GetMapping("/patrolStoreReminder")
    @SysLog(func = "催办", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.PATROL_STORE_TASK_REMIND)
    public ResponseResult<List<String>> patrolStoreReminder(@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestParam(value = "taskId") Long taskId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String appType = user.getAppType();
        return patrolStoreService.patrolStoreReminder(enterpriseId, taskId, appType);
    }

    /**
     * 获取复审巡店记录
     */
    @ApiOperation("获取复审巡店记录(可复审以及已复审)")
    @PostMapping("/getRecheckPatrolRecordList")
    public ResponseResult<PageVO<PatrolStoreRecordVO>> getRecheckPatrolRecordList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody PatrolRecordRequest recordRecordRequest) {
        PageHelper.clearPage();
        // 切换数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        CurrentUser currentUser = UserHolder.getUser();
        recordRecordRequest.setUserId(currentUser.getUserId());
        recordRecordRequest.setUserName(currentUser.getName());
        recordRecordRequest.setDbName(enterpriseConfigDO.getDbName());
        recordRecordRequest.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        //已复审
        if (recordRecordRequest.getRecheckStatus() != null && Constants.ONE == recordRecordRequest.getRecheckStatus()) {
            recordRecordRequest.setBusinessCheckType(BusinessCheckType.PATROL_RECHECK.getCode());
        } else {
            recordRecordRequest.setStatus(1);
            recordRecordRequest.setPatrolTypeList(Arrays.asList(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode(), TaskTypeEnum.PATROL_STORE_ONLINE.getCode(),
                    TaskTypeEnum.STORE_SELF_CHECK.getCode()));
        }
        PageInfo pageInfo = patrolStoreService.getPatrolRecordList(enterpriseId, recordRecordRequest, currentUser);
        return ResponseResult.success(PageHelperUtil.getPageVO(pageInfo));
    }


    /**
     * 获取复审巡店记录
     */
    @ApiOperation("巡店复审")
    @PostMapping("/recheckPatrol")
    public ResponseResult<Long> recheckPatrol(@PathVariable("enterprise-id") String enterpriseId, @RequestBody @Validated IdDTO idDTO) {
        // 切换数据源
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.recheckPatrol(enterpriseId, idDTO.getId(), UserHolder.getUser().getUserId(),
                UserHolder.getUser().getName()));
    }

    /**
     * 获取复审巡店记录
     */
    @ApiOperation("巡店复审概览")
    @GetMapping("/recheckOverview")
    public ResponseResult<PatrolOverviewDTO> recheckOverview(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam(value = "beginTime") Long beginTime,
                                                             @RequestParam(value = "endTime") Long endTime) {
        // 切换数据源
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.recheckOverview(enterpriseId, beginTime, endTime, UserHolder.getUser().getUserId(), UserHolder.getUser().getDbName()));
    }

    @ApiOperation(value = "刪除巡店门店任务")
    @PostMapping(path = "/deleteRecord")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除巡店门店任务")
    @SysLog(func = "详情", subFunc = "删除", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.PATROL_STORE_TASK_BATCH_DELETE, preprocess = true)
    public ResponseResult deleteRecord(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestBody @Valid TbDisplayDeleteParam displayDeleteParam) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        patrolStoreService.deleteRecord(enterpriseId, displayDeleteParam, UserHolder.getUser(), "done", config);
        return ResponseResult.success(true);
    }

    @ApiOperation(value = "刪除巡店门店任务")
    @PostMapping(path = "/deleteRecords")
    @OperateLog(operateModule = CommonConstant.Function.PATROL_TASK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除巡店门店任务")
    @SysLog(func = "详情", subFunc = "删除", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.PATROL_STORE_TASK_BATCH_DELETE, preprocess = true)
    public ResponseResult<List<Long>> deleteRecords(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                       @RequestBody @Valid TbDisplayDeleteParam displayDeleteParam) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.deleteRecords(enterpriseId, displayDeleteParam, UserHolder.getUser(), "done"));

    }

    /**
     * 刪除陈列门店任务
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    @ApiOperation(value = "获取删除的陈列门店任务")
    @GetMapping(path = "/getDeleteRecordList")
    @ApiImplicitParam(name = "unifyTaskId", value = "任务id", required = true)
    public ResponseResult<PageInfo<TbDisplayTableRecordDeleteVO>> getDeleteRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                                                      @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                                                                      @RequestParam(value = "unifyTaskId", required = false) Long unifyTaskId,
                                                                                      @RequestParam(value = "unifyTaskIds", required = false) String unifyTaskIds,
                                                                                      @RequestParam(value = "taskStatus", required = false) String taskStatus) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.getDeleteRecordList(enterpriseId, unifyTaskId, pageNum, pageSize,unifyTaskIds, taskStatus));
    }

    /**
     * 循环/单个任务终止功能
     * 可使用范围：1.线下巡店任务、2.视频巡店任务、3.定时巡检、4.陈列任务
     * 有菜单权限控制
     * 批次不删除
     * 待办也被移除（钉钉待办、用户待办列表）
     *
     * @param enterpriseId
     * @return
     */
    @ApiOperation(value = "任务中止")
    @PostMapping(path = "/stopTask")
    @SysLog(func = "停止", opModule = OpModuleEnum.UNIFY_TASK, opType = OpTypeEnum.STOP)
    public ResponseResult stopTask(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                   @RequestBody StopTaskDTO stopTaskDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        return ResponseResult.success(patrolStoreService.stopTask(enterpriseId, stopTaskDTO, enterpriseConfig));
    }

    @ApiOperation(value = "巡店任务详情")
    @GetMapping(path = "/getTaskDetail")
    public ResponseResult getTaskDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                    @RequestParam Long businessId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        return ResponseResult.success(patrolStoreService.getTaskDetail(enterpriseId, businessId, enterpriseConfig));
    }

    @ApiOperation(value = "巡店任务详情")
    @GetMapping(path = "/getBusinessId")
    public ResponseResult getBusinessId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                        @RequestParam Long unifyTaskId, @RequestParam String storeId, @RequestParam Long loopCount) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreService.getBusinessId(enterpriseId, unifyTaskId, storeId, loopCount));
    }

}
