package com.coolcollege.intelligent.controller.storework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.question.vo.SubQuestionRecordListVO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkCreateDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkReturnDTO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.storework.*;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 14:15
 */
@Api(tags = "店务管理")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/storeWork")
@ErrorHelper
@Slf4j
public class SwStoreWorkController {

    @Autowired
    private StoreWorkService storeWorkService;
    @Autowired
    StoreWorkDataTableService storeWorkDataTableService;
    @Autowired
    StoreWorkDataTableColumnService storeWorkDataTableColumnService;
    @Autowired
    StoreWorkRecordService storeWorkRecordService;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private StoreWorkTableMappingService storeWorkTableMappingService;



    @Deprecated
    @ApiOperation(value = "店务管理-新建", notes = "注意：点评人、协作人信息")
    @PostMapping("/buildStoreWork")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_ADD, operateDesc = "新增店务")
    public ResponseResult<Long> buildStoreWork(@PathVariable("enterprise-id") String enterpriseId,
                                               @Valid @RequestBody BuildStoreWorkRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        Long storeWorkId = storeWorkService.buildStoreWork(enterpriseId, user, request);
        return ResponseResult.success(storeWorkId);
    }

    @ApiOperation(value = "店务管理-新建", notes = "注意：点评人、协作人信息")
    @PostMapping("/createStoreWork")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_ADD, operateDesc = "新增店务")
    @SysLog(func = "新建", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.INSERT)
    public ResponseResult<SwStoreWorkCreateDTO> createStoreWork(@PathVariable("enterprise-id") String enterpriseId,
                                               @Valid @RequestBody BuildStoreWorkRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        Long storeWorkId = storeWorkService.buildStoreWork(enterpriseId, user, request);
        SwStoreWorkCreateDTO storeWorkCreateDTO = new SwStoreWorkCreateDTO();
        storeWorkCreateDTO.setStoreWorkId(storeWorkId);
        storeWorkCreateDTO.setCanReissue(storeWorkService.checkCanReissue(enterpriseId, storeWorkId));
        return ResponseResult.success(storeWorkCreateDTO);
    }

    @ApiOperation(value = "自动保存创建数据", notes = "注意：新增自动保存临时id前端自行传唯一值，编辑时传店务id")
    @PostMapping("/autoSaveBuildData")
    public ResponseResult<Boolean> autoSaveBuildData(@PathVariable("enterprise-id") String enterpriseId,
                                           @RequestBody BuildStoreWorkRequest request) {
        String userId = UserHolder.getUser().getUserId();
        // 自动保存时
        String cacheKey = MessageFormat.format(RedisConstant.STOREWORK_BUILD_CACHE_KEY, enterpriseId, userId, request.getTempCacheDataId());
        redisUtilPool.setString(cacheKey, JSONObject.toJSONString(request), RedisConstant.ONE_DAY_SECONDS);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("获取创建缓存数据")
    @GetMapping("/getBuildCacheData")
    public ResponseResult<SwStoreWorkDetailVO> getBuildCacheData(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestParam(value = "tempCacheDataId", required = true) Long tempCacheDataId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.getBuildCacheData(enterpriseId, tempCacheDataId, user));
    }

    @ApiOperation("补发当天任务，pushFlag传true，reissueFlag传true")
    @PostMapping("/dayClearTaskResolve")
    public ResponseResult dayClearTaskResolve(@PathVariable(value = "enterprise-id", required = true) String enterpeiseId,
                                              @RequestBody StoreTaskResolveRequest storeTaskResolveRequest) {
        DataSourceHelper.changeToMy();
        storeTaskResolveRequest.setEnterpriseId(enterpeiseId);
        storeTaskResolveRequest.setCurrentDate(new Date());
        storeTaskResolveRequest.setPushFlag(true);
        storeTaskResolveRequest.setReissueFlag(true);
        storeWorkService.dayClearTaskResolve(storeTaskResolveRequest);
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * 店务编辑
     * @param enterpriseId
     * @param request
     * @return
     */
    @ApiOperation("店务编辑")
    @PostMapping(path = "/update")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑店务")
    @SysLog(func = "编辑", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.EDIT)
    public ResponseResult<Boolean> changeStoreWork(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @RequestBody @Validated BuildStoreWorkRequest request) {
        if(log.isInfoEnabled()){
            log.info("#changestorework body is ={}", JSON.toJSONString(request));
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        storeWorkService.changeStoreWork(enterpriseId, request, user, enterpriseConfigDO.getDingCorpId(),enterpriseConfigDO.getAppType());
        return ResponseResult.success(Boolean.TRUE);
    }


    @ApiOperation("店务管理-查询列表（分页）")
    @PostMapping("/list")
    public ResponseResult<PageInfo<SwStoreWorkVO>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody StoreWorkSearchRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.storeWorkList(enterpriseId, request, user));
    }

    @ApiOperation("检查项列表")
    @PostMapping("/columnList")
    public ResponseResult<List<TbMetaStaTableColumnDO>> columnList(@PathVariable("enterprise-id") String enterpriseId,
                                                                   @RequestBody ColumnListRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.columnList(enterpriseId, request, user));
    }


    /**
     * 店务详情
     * @param enterpriseId
     * @param storeWorkId
     * @return
     */
    @ApiOperation("店务管理-详情")
    @GetMapping(path = "/detail")
    public ResponseResult<SwStoreWorkDetailVO> getStoreWorkDetail(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                       @RequestParam(value = "storeWorkId") Long storeWorkId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkService.getStoreWorkDetail(enterpriseId, storeWorkId));
    }

    @ApiOperation("根据店务id查询检查表列表")
    @GetMapping(path = "/listTableMappingByStoreWorkId")
    public ResponseResult<List<SwStoreWorkTableMappingDO>> listTableMappingByStoreWorkId(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                         @RequestParam(value = "storeWorkId") Long storeWorkId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkTableMappingService.listByStoreWorkId(enterpriseId,storeWorkId));
    }

    @ApiOperation("店务管理-数据-门店统计-数据概况")
    @PostMapping("/storeWorkStoreStatisticsOverview")
    public ResponseResult<StoreWorkStatisticsOverviewVO> storeWorkStoreStatisticsOverview(@PathVariable("enterprise-id") String enterpriseId,
                                                                                          @RequestBody StoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkStoreStatisticsOverview(enterpriseId, storeWorkDataListRequest, user));
    }

    @ApiOperation("店务管理-数据-门店统计-数据明细(分页)")
    @PostMapping("/storeWorkStoreStatisticsList")
    public ResponseResult<PageInfo<StoreWorkDataDetailVO>> storeWorkStoreStatisticsList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                        @RequestBody StoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkStoreStatisticsList(enterpriseId, storeWorkDataListRequest, user));
    }

    @ApiOperation("店务管理-数据-未点评数据")
    @PostMapping("/StoreWorkDataList")
    public ResponseResult<PageInfo<StoreWorkDataDTO>> StoreWorkDataList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                        @RequestBody StoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.StoreWorkDataList(enterpriseId, storeWorkDataListRequest, user));
    }

    @ApiOperation("门店统计导出/门店执行力汇总")
    @PostMapping("/storeWorkStoreStatisticsListExport")
    public ResponseResult<ImportTaskDO> storeWorkStoreStatisticsListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestBody StoreWorkDataListRequest storeWorkDataListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkStoreStatisticsListExport(enterpriseId,storeWorkDataListRequest,user));
    }

    @ApiOperation("店务管理-数据-区域统计-数据明细")
    @PostMapping("/storeWorkRegionStatisticsList")
    public ResponseResult<List<StoreWorkStatisticsOverviewVO>> storeWorkRegionStatisticsList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestBody StoreWorkDataListRequest request){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRegionStatisticsList(enterpriseId,request,user));
    }

    @ApiOperation("区域统计导出")
    @PostMapping("/storeWorkRegionStatisticsListExport")
    public ResponseResult<ImportTaskDO> storeWorkRegionStatisticsListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestBody StoreWorkDataListRequest storeWorkDataListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkRegionStatisticsListExport(enterpriseId,storeWorkDataListRequest,user));
    }

    @ApiOperation("店务管理-数据-检查表统计")
    @PostMapping("/storeWorkTableStatisticsList")
    public ResponseResult<List<StoreWorkDataTableStatisticsVO>> storeWorkTableStatisticsList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                             @RequestBody StoreWorkDataListRequest request){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.storeWorkTableStatisticsList(enterpriseId, request, user));
    }

    @ApiOperation("店务管理-数据-日报表统计(分页)")
    @PostMapping("/storeWorkDayStatisticsList")
    public ResponseResult<PageInfo<StoreWorkDayStatisticsVO>> storeWorkDayStatisticsList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                         @RequestBody StoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkDayStatisticsList(enterpriseId, storeWorkDataListRequest, user));
    }

    @ApiOperation("日报表统计导出")
    @PostMapping("/storeWorkDayStatisticsListExport")
    public ResponseResult<ImportTaskDO> storeWorkDayStatisticsListExport(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestBody StoreWorkDataListRequest storeWorkDataListRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.storeWorkDayStatisticsListExport(enterpriseId,storeWorkDataListRequest,user));
    }

    @ApiOperation("店务催办")
    @PostMapping("/storeWorkRemind")
    @SysLog(func = "催办", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.REMIND)
    public ResponseResult<List<String>> storeWorkRemind(@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestBody StoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String appType = user.getAppType();
        return storeWorkService.storeWorkRemind(enterpriseId, storeWorkDataListRequest, appType);
    }

    @ApiOperation("移动端一键店务催办")
    @PostMapping("/pmdStoreWorkRemind")
    @SysLog(func = "催办", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.REMIND)
    public ResponseResult<List<String>> pmdStoreWorkRemind(@PathVariable("enterprise-id") String enterpriseId,
                                                        @RequestBody PmdStoreWorkDataListRequest storeWorkDataListRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        String appType = user.getAppType();
        return ResponseResult.success(storeWorkService.pmdStoreWorkRemind(enterpriseId, storeWorkDataListRequest, appType));
    }


    @ApiOperation("店务管理-停止")
    @PostMapping(path = "/stop")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "停止店务")
    @SysLog(func = "停止", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.STOP)
    public ResponseResult<Boolean> stopStoreWork(@PathVariable(value = "enterprise-id") String enterpriseId,
                                               @RequestParam("storeWorkId") Long storeWorkId) {
        DataSourceHelper.changeToMy();
        storeWorkService.stopStoreWork(enterpriseId, storeWorkId);
        return ResponseResult.success(Boolean.TRUE);
    }

    @ApiOperation("店务管理-删除")
    @PostMapping(path = "/delete")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除店务")
    @SysLog(func = "删除", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.DELETE, preprocess = true)
    public ResponseResult<Boolean> delStoreWork(@PathVariable(value = "enterprise-id") String enterpriseId,
                                       @RequestParam("storeWorkId") Long storeWorkId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        storeWorkService.delStoreWork(enterpriseId, storeWorkId, enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId(), user);
        return ResponseResult.success(true);
    }

    @ApiOperation("催办跳店务执行页查询执行人指定店务下的一个门店")
    @GetMapping("/selectDataTableByStoreWorkId")
    public ResponseResult<StoreWorkDataTableSimpleVO> selectDataTableByStoreWorkId(@PathVariable("enterprise-id") String enterpriseId,
                                                                               @RequestParam(value = "queryDate",required = true) Long queryDate,
                                                                               @RequestParam(value = "storeWorkId",required = true) Long storeWorkId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.selectDataTableByStoreWorkId(enterpriseId, queryDate, user.getUserId(), storeWorkId));
    }


    @ApiOperation("店务执行页 检查表数据项")
    @PostMapping("/getStoreWorkExecutionPage")
    public ResponseResult<List<StoreWorkExecutionPageVO>> getStoreWorkExecutionPage(@PathVariable("enterprise-id") String enterpriseId,
                                                                                    @RequestBody StoreWorkTableRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkExecutionPage(enterpriseId,user, request));
    }

    @ApiOperation("店务执行页 店务表中项的数据")
    @PostMapping("/getStoreWorkDataTableColumn")
    public ResponseResult<List<StoreWorkDataTableVO>> getStoreWorkDataTableColumn(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestParam(value = "user_id",required = false) String userId,
                                                                                  @RequestBody StoreWorkDataTableColumnRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(storeWorkDataTableColumnService.getStoreWorkDataTableColumn(enterpriseId,  request, userId));
    }


    @ApiOperation("店务执行页 指定门店执行人当天、当周、当日任务全部点评完成")
    @PostMapping("/currentUserStoreWorkAllCommentComplete")
    public ResponseResult<Boolean> currentUserStoreWorkAllCommentComplete(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody StoreWorkTableRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.currentUserStoreWorkAllCommentComplete(enterpriseId,user, request));
    }

    @ApiOperation("店务执行页 当前用户门店日清执行完成 店务概况(店务汇总数据) 改动-跟当前执行人不挂钩")
    @PostMapping("/currentUserStoreWorkOverViewData")
    public ResponseResult<StoreWorkOverviewVO> currentUserStoreWorkOverViewData(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestBody StoreWorkTableRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkOverViewData(enterpriseId,user, request));
    }

    @ApiOperation("店务执行页 当前用户门店日清执行完成 店务概况(多个表 按表汇总数据)")
    @GetMapping("/getStoreWorkTableDataList")
    public ResponseResult<List<StoreWorkOverviewVO>> getStoreWorkTableDataList(@PathVariable("enterprise-id") String enterpriseId,
                                                                               @RequestParam(value = "currentDate",required = false) Long currentDate,
                                                                               @RequestParam(value = "workCycle",required = false) String workCycle,
                                                                               @RequestParam(value = "storeId",required = false) String storeId,
                                                                               @RequestParam(value = "businessId",required = false) String businessId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkOverViewDataList(enterpriseId,workCycle,storeId,currentDate,user, businessId));
    }

    @ApiOperation("店务执行页 检查表完成之后 详情(单表数据详情)")
    @GetMapping("/getStoreWorkTableData")
    public ResponseResult<StoreWorkOverviewVO> getStoreWorkTableData(@PathVariable("enterprise-id") String enterpriseId,
                                                                     @PathVariable(value = "dataTableId",required = true) Long dataTableId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkTableData(enterpriseId,dataTableId));
    }

    @ApiOperation("店务执行页 店务日清是否完成日历")
    @PostMapping("/getStoreWorkClear")
    public ResponseResult<List<StoreWorkClearVO>> getStoreWorkClear(@PathVariable("enterprise-id") String enterpriseId,
                                                                    @RequestBody StoreWorkClearRequest storeWorkClearRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkClear(enterpriseId,user,storeWorkClearRequest));
    }

    @ApiOperation("店务执行页 单个项任务提交")
    @PostMapping("/singleColumnSubmit")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_ADD, operateDesc = "店务提交")
    public ResponseResult<Boolean> singleColumnSubmit(@PathVariable("enterprise-id") String enterpriseId,
                                                                    @RequestBody SingleExecutionRequest singleExecutionRequest) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableColumnService.singleColumnSubmit(enterpriseId,user,singleExecutionRequest));
    }

    @ApiOperation("店务执行页 转交")
    @PostMapping("/transferHandler")
    public ResponseResult<Boolean> transferHandler(@PathVariable("enterprise-id") String enterpriseId,
                                                      @RequestBody TransferHandlerRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.transferHandler(enterpriseId,user,request.getTransferUserId(),request.getStoreWorkDataTableIds()));
    }



    @ApiOperation("PC端 店务重新分配")
    @PostMapping("/transferHandlerAndComment")
    @SysLog(func = "数据", subFunc = "重新分配", opModule = OpModuleEnum.STORE_WORK, opType = OpTypeEnum.REALLOCATE)
    public ResponseResult<Boolean> transferHandlerAndComment(@PathVariable("enterprise-id") String enterpriseId,
                                                   @RequestBody List<TransferHandlerCommentRequest> requestList) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.transferHandlerAndComment(enterpriseId,user,requestList));
    }


    @ApiOperation("PC端 店务表执行人与点评人数据")
    @GetMapping("/getDataUser")
    public ResponseResult<SwStoreWorkReturnDTO> getDataUser(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestParam(value ="businessId",required = true )String businessId,
                                                                  @RequestParam(value ="storeId",required = true )String storeId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkDataTableService.getDataUser(enterpriseId,businessId,storeId));
    }

    @ApiOperation("店务点评页 查询当前人管辖门店 门店店务点评数据")
    @PostMapping("/getCurrentUserStoreWorkData")
    public ResponseResult<PageInfo<StoreDayClearDataVO>> getCurrentUserStoreWorkData(@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestBody StoreWorkClearDetailRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.getCurrentUserStoreWorkData(enterpriseId,user,request));
    }

    @ApiOperation("店务点评页 未点评数据集合")
    @PostMapping("/getCurrentUserStoreWorkNoCommentData")
    public ResponseResult<PageInfo<StoreWorkDataDTO>> getCurrentUserStoreWorkNoCommentData(@PathVariable("enterprise-id") String enterpriseId,
                                                                                     @RequestBody StoreWorkClearDetailRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.getCurrentUserStoreWorkNoCommentData(enterpriseId,user,request));
    }

    @ApiOperation("店务点评页 数据统计")
    @PostMapping("/getStoreWorkRecordStatistics")
    public ResponseResult<StoreWorkRecordStatisticsVO> getStoreWorkRecordStatistics(@PathVariable("enterprise-id") String enterpriseId,
                                                                                     @RequestBody StoreWorkClearDetailRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.getStoreWorkRecordStatistics(enterpriseId,user,request));
    }

    @ApiOperation("移动端 店务详情 检查表下拉框")
    @GetMapping("/getStoreWorkTableList")
    public ResponseResult<List<StoreWorkTableListVO>> getStoreWorkTableList(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam(value ="businessId",required = true )String businessId){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(storeWorkDataTableService.getStoreWorkTableList(enterpriseId,businessId));
    }

    @ApiOperation("点评人点评")
    @PostMapping("/commentScore")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_ADD, operateDesc = "店务点评")
    public ResponseResult<Boolean> commentScore(@PathVariable("enterprise-id") String enterpriseId,
                                                @RequestBody List<CommentScoreRequest> requestList){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.commentScore(enterpriseId,user,requestList));
    }


    @ApiOperation("点评人点评页数据")
    @GetMapping("/getTableColumn")
    public ResponseResult<StoreWorkDataTableDetailVO> getCommentData(@PathVariable("enterprise-id") String enterpriseId,Long dataTableId){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableColumnService.getTableColumn(enterpriseId,user,dataTableId));
    }


    @ApiOperation("点评页数据缓存")
    @PostMapping("/setTableColumnCache")
    public ResponseResult<Boolean> setTableColumnCache(@PathVariable("enterprise-id") String enterpriseId,
                                                       @RequestBody CommentScoreCacheRequest commentScoreCacheRequest){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableColumnService.setTableColumnCache(enterpriseId,user,commentScoreCacheRequest.getDataTableId(),commentScoreCacheRequest.getRequestList()));
    }

    @ApiOperation("工单追踪")
    @GetMapping("/questionQuery")
    public ResponseResult<List<SubQuestionRecordListVO>> questionQuery(@PathVariable("enterprise-id") String enterpriseId,
                                                                       @RequestParam(value ="businessId",required = true) String businessId){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkRecordService.getStoreWorkQuestionList(enterpriseId,businessId,user));
    }

    @ApiOperation("获取数据检查表或者店务记录信息（执行状态、点评状态、执行点评权限）")
    @GetMapping("/checkStoreWorkStatusAuth")
    public ResponseResult<StoreWorkTableAndRecordStatusInfo> checkStoreWorkStatusAuth(@PathVariable("enterprise-id") String enterpriseId,
                                                                                @RequestParam(value ="businessId",required = false) String businessId,
                                                                                @RequestParam(value ="dataTableId",required = false) Long dataTableId){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.checkStoreWorkStatusAuth(enterpriseId,user, businessId, dataTableId));
    }

    @ApiOperation("图片中心")
    @PostMapping("/getPictureCenterDataTableList")
    public ResponseResult<PageInfo<StoreWorkPictureListVO>> getPictureCenterDataTableList(@PathVariable("enterprise-id") String enterpriseId,
                                                                   @RequestBody PictureCenterRequest request){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkDataTableService.getPictureCenterDataTableList(enterpriseId,request,user));
    }

    /**
     * 店务上传图片 日清、月清、周清更改，该接口已废弃，不做更新，同前端确认过
     * @param enterpriseId
     * @return
     */
    @ApiOperation("店务管理-数据-区域统计-数据明细")
    @GetMapping("/getEnterpriseStoreWorkSetting")
    public ResponseResult<EnterpriseStoreWorkSettingsDTO> getEnterpriseStoreWorkSetting(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkService.getEnterpriseStoreWorkSetting(enterpriseId));
    }

    @ApiOperation("修正企业的店务点评人")
    @PostMapping("/fixCommentUser")
    public ResponseResult<Boolean> fixCommentUser(@RequestBody List<String> enterpriseIds, @PathVariable("enterprise-id") String enterpriseId) {
        return ResponseResult.success(storeWorkService.fixCommentUser(enterpriseIds));
    }

    @ApiOperation("删除店务子任务")
    @PostMapping(path = "/delStoreWorkSubtask")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除店务子任务")
    public ResponseResult<Boolean> delStoreWorkSubtask (@PathVariable(value = "enterprise-id") String enterpriseId, @RequestBody @Validated StoreWorkSubtaskDelRequest param) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkService.delStoreWorkSubtask(enterpriseId, param, user.getUserId()));
    }

    @ApiOperation("AI检查表AI分析重试")
    @GetMapping("/aiTableRetry")
    @ApiImplicitParam(value = "dataTableId", name = "数据表id", required = true, dataType = "Long")
    @OperateLog(operateModule = CommonConstant.Function.STOREWORK, operateType = CommonConstant.LOG_UPDATE, operateDesc = "AI检查表AI分析重试")
    public ResponseResult aiTableRetry(@PathVariable(value = "enterprise-id") String enterpriseId, @NotNull(message = "数据表id不能为空") Long dataTableId) {
        DataSourceHelper.changeToMy();
        storeWorkRecordService.aiRetry(enterpriseId, dataTableId);
        return ResponseResult.success();
    }
}
