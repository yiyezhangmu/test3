package com.coolcollege.intelligent.controller.patrolstore;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.BusinessCheckType;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.param.CheckStoreOverParam;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.query.SetCheckUserQuery;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.statistics.CheckAnalyzeVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.DataStaTableColumnVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreCheckVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaStaTableVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolRecordListByDayVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolRecordStatusEveryDayVO;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreCheckRecordVO;
import com.coolcollege.intelligent.model.patrolstore.vo.TableInfoDTO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreRecordsService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2020-12-16
 */

@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolstore/patrolStoreRecords")
@BaseResponse
@Slf4j
public class PatrolStoreRecordsController {
    @Resource
    private PatrolStoreRecordsService patrolStoreRecordsService;
    @Resource
    private PatrolStoreStatisticsService patrolStoreStatisticsService;
    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    /**
     * 检查记录表记录
     * @param enterpriseId
     * @param tableRecordsRequest
     * @return
     */
    @Deprecated
    @PostMapping("/tableRecords")
    public ResponseResult tableRecords(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TableRecordsRequest tableRecordsRequest){
        DataSourceHelper.changeToMy();
        PageInfo list =
                patrolStoreRecordsService.tableRecords(enterpriseId,tableRecordsRequest);
        return ResponseResult.success(PageHelperUtil.getPageInfo(list));
    }

    /**
     * 检查记录表记录导出
     * @param enterpriseId
     * @return
     */
    @PostMapping("/tableRecordsExport")
    public Object tableRecordsExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TableRecordsRequest tableRecordsRequest){
        DataSourceHelper.changeToMy();
        tableRecordsRequest.setDbName(UserHolder.getUser().getDbName());
        return patrolStoreRecordsService.starRecordsExport(enterpriseId,tableRecordsRequest);

    }

    /**
     * 检查记录表记录导出
     * @param enterpriseId
     * @return
     */
    @PostMapping("/tableRecordsDynamicExport")
    public Object tableRecordsAsyncExport(@PathVariable("enterprise-id") String enterpriseId,
                                        @RequestBody TableRecordsRequest tableRecordsRequest) {
        DataSourceHelper.changeToMy();
        return patrolStoreRecordsService.tableRecordsAsyncExport(enterpriseId, tableRecordsRequest);
    }

    /**
     * 单表基础检查项平铺记录
     * @param enterpriseId
     * @param request
     * @return
     */
    @Deprecated
    @PostMapping("/singleTableColumnsRecords")
    public ResponseResult singleTableColumnsRecords(@PathVariable("enterprise-id") String enterpriseId, @RequestBody SingleTableColumnsRecordsRequest request){
        DataSourceHelper.changeToMy();
        PageInfo pageInfo = patrolStoreRecordsService.singleTableColumnsRecords(enterpriseId,request);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }



    /**
     * 巡店记录任务记录列表
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "/potralRecordList")
    public ResponseResult taskPotralRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestBody PatrolStoreStatisticsDataTableQuery query) {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        boolean levelInfo = false;
        if(StringUtils.isNotBlank(storeCheckSettingDO.getLevelInfo())){
            JSONObject jsonObject = JSONObject.parseObject(storeCheckSettingDO.getLevelInfo());
            levelInfo = jsonObject.getBoolean("open") == null ? false : jsonObject.getBoolean("open");
        }
        query.setLevelInfo(levelInfo);
        DataSourceHelper.changeToMy();
        query.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        return ResponseResult.success(patrolStoreRecordsService.potralRecordList(enterpriseId, query));
    }


    @PostMapping(path = "/potralRecordListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-基础报表-巡店记录表")
    public ResponseResult potralRecordListExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                    @RequestBody PatrolStoreStatisticsDataTableQuery query) {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        DataSourceHelper.reset();
        // 企业配置
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        boolean levelInfo = false;
        if(StringUtils.isNotBlank(storeCheckSettingDO.getLevelInfo())){
            JSONObject jsonObject = JSONObject.parseObject(storeCheckSettingDO.getLevelInfo());
            levelInfo = jsonObject.getBoolean("open") == null ? false : jsonObject.getBoolean("open");
        }
        query.setLevelInfo(levelInfo);
        query.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.potralRecordListExport(enterpriseId, query));
    }

    @PostMapping(path = "/potralRecordDetailList")
    public ResponseResult potralRecordDetailList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                               @RequestBody PatrolStoreStatisticsDataTableQuery query) {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.potralRecordDetailList(enterpriseId, query));
    }


    @PostMapping(path = "/potralRecordDetailListExport")
    @SysLog(func = "导出", opModule = OpModuleEnum.IMPORT_EXPORT, opType = OpTypeEnum.EXPORT, menus = "巡店-基础报表-基础详情表")
    public ResponseResult potralRecordDetailListExport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody PatrolStoreStatisticsDataTableQuery query) {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(patrolStoreRecordsService.potralRecordDetailListExport(enterpriseId, query));
    }

    @PostMapping(path = "/updatePatrolStoreSummarySave")
    public ResponseResult potralStoreSummarySave(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody PatrolRecordRequest query){
        DataSourceHelper.changeToMy();
        return patrolStoreRecordsService.potralStoreSummarySave(enterpriseId,query);
    }

    @PostMapping(path = "/updatePatrolStoreSignatureSave")
    public ResponseResult patrolStoreSignatureSave(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @RequestBody PatrolRecordRequest query){
        DataSourceHelper.changeToMy();
        return patrolStoreRecordsService.patrolStoreSignatureSave(enterpriseId,query);
    }

    /**
     * 复审任务列表
     * @param enterpriseId
     * @return
     */
    @PostMapping(path = "recheckRecordList")
    public ResponseResult<PageInfo<PatrolStoreStatisticsMetaStaTableVO>> recheckRecordList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                 @RequestBody PatrolStoreStatisticsDataTableQuery query) {
        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        query.setBusinessCheckType(BusinessCheckType.PATROL_STORE.getCode());
        //已复审
        if(query.getRecheckStatus() != null && Constants.ONE == query.getRecheckStatus() ){
            query.setBusinessCheckType(BusinessCheckType.PATROL_RECHECK.getCode());
        }else {
            query.setStatus(1);
            query.setPatrolTypeList(Arrays.asList(TaskTypeEnum.PATROL_STORE_OFFLINE.getCode(), TaskTypeEnum.PATROL_STORE_ONLINE.getCode(),
                    TaskTypeEnum.STORE_SELF_CHECK.getCode()));
        }
        query.setLevelInfo(false);
        return ResponseResult.success(patrolStoreRecordsService.potralRecordList(enterpriseId, query));
    }

    @PostMapping(path = "/getPatrolStoreCheckList")
    @ApiOperation("稽核概览列表")
    public ResponseResult<PageInfo<PatrolStoreCheckVO>> getPatrolStoreCheckList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) throws IOException {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.getPatrolStoreCheckList(enterpriseId, query));
    }
    @PostMapping(path = "/setCheckUser")
    @ApiOperation("线上稽核设置")
    public ResponseResult setCheckUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody SetCheckUserQuery query) {
        DataSourceHelper.reset();
        return ResponseResult.success(patrolStoreRecordsService.setCheckUser(enterpriseId, query));
    }
    @PostMapping(path = "/getCheckUser")
    @ApiOperation("查询线上稽核设置")
    public ResponseResult getCheckUser(@PathVariable(value = "enterprise-id", required = false) String enterpriseId) {
        DataSourceHelper.reset();
        return ResponseResult.success(patrolStoreRecordsService.getCheckUser(enterpriseId));
    }

    @PostMapping(path = "/getCheckDetailList")
    @ApiOperation("稽核详情列表")
    public ResponseResult<PageInfo<DataStaTableColumnVO>> getCheckDetailList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) throws IOException {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.getCheckDetailList(enterpriseId, query));
    }
    @PostMapping(path = "/getCheckAnalyzeList")
    @ApiOperation("稽核分析列表")
    public ResponseResult<PageInfo<CheckAnalyzeVO>> getCheckAnalyzeList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.getCheckAnalyzeList(enterpriseId, query));
    }
    @PostMapping(path = "/ExportCheckList")
    @ApiOperation("稽核列表导出")
    public ResponseResult ExportCheckList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) throws IOException {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        log.info("ExportCheckList:{}",query.getDbName());
        return ResponseResult.success(patrolStoreRecordsService.ExportCheckList(enterpriseId, query));
    }

    @PostMapping(path = "/ExportCheckDetailList")
    @ApiOperation("稽核详情列表导出")
    public ResponseResult ExportCheckDetailList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) throws IOException {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(patrolStoreRecordsService.ExportCheckDetailList(enterpriseId, query));
    }
    @PostMapping(path = "/ExportCheckAnalyzeList")
    @ApiOperation("稽核分析列表导出")
    public ResponseResult ExportCheckAnalyzeList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody PatrolStoreCheckQuery query) {

        DataSourceHelper.changeToMy();
        query.setDbName(UserHolder.getUser().getDbName());
        return ResponseResult.success(patrolStoreRecordsService.ExportCheckAnalyzeList(enterpriseId, query));
    }


    @GetMapping(path = "/taskStoreCheck")
    @ApiOperation("稽核详情")
    public ResponseResult<PatrolStoreCheckRecordVO> taskRecordInfo(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   @RequestParam(value = "id") Long id,
                                                                   @RequestParam(value = "checkType") Integer checkType)   {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.taskRecordInfo(enterpriseId, id,checkType));
    }
    @ApiOperation("稽核详情数据")
    @GetMapping(path = "/dataTableInfoList")
    public ResponseResult<List<TableInfoDTO>> dataTableInfoList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                          @RequestParam(value = "id")Long id,
                                                          @RequestParam(value = "checkType")Integer checkType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        String userId = "";
        if (UserHolder.getUser() != null) {
            userId = UserHolder.getUser().getUserId();
        }
        return ResponseResult.success(patrolStoreRecordsService.dataTableInfoList(enterpriseId, id, userId,checkType));
    }
    @PostMapping(path = "/historyExecutionQuery")
    @ApiOperation("稽核处理记录")
    public ResponseResult historyExecutionQuery(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                @RequestBody CheckStoreOverParam param) {
        log.info("historyExecutionQuery.enterprise-id ={},business-id={}", enterpriseId, param.getBusinessId());
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if (enterpriseConfigDO != null) {
            //切到指定的库
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        }
        List<TbPatrolStoreHistoryDo> tbPatrolStoreHistoryMappers = patrolStoreRecordsService.selectPatrolStoreHistoryList(enterpriseId, param.getBusinessId());

        return ResponseResult.success(tbPatrolStoreHistoryMappers);
    }

    @ApiOperation("根据日期查看每天门店是否已巡")
    @PostMapping(path = "/patrolRecordStatusEveryDay")
    public ResponseResult<List<PatrolRecordStatusEveryDayVO>> patrolRecordStatusEveryDay(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                   @RequestBody @Validated PatrolRecordStatusRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.patrolRecordStatusEveryDay(enterpriseId, param));
    }

    @ApiOperation("根据日期查询已巡记录")
    @PostMapping(path = "/patrolRecordListByDay")
    public ResponseResult<List<PatrolRecordListByDayVO>> patrolRecordListByDay(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                               @RequestBody @Validated PatrolRecordListByDayRequest param) {

        DataSourceHelper.changeToMy();
        return ResponseResult.success(patrolStoreRecordsService.patrolRecordListByDay(enterpriseId, param));
    }
}
