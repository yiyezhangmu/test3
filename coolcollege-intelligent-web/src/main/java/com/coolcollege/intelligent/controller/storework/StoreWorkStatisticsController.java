package com.coolcollege.intelligent.controller.storework;


import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsExecutiveDTO;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 14:15
 */
@Api(tags = "店务报表")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/storeWorkStatistics")
@ErrorHelper
@Slf4j
public class StoreWorkStatisticsController {

    @Autowired
    @Lazy
    private StoreWorkStatisticsService storeWorkStatisticsService;


    @ApiOperation("获取店务概况-总览")
    @PostMapping("/getStoreWorkStatistic")
    public ResponseResult<StoreWorkDataVO> getStoreWorkStatistic(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.getStoreWorkStatistic(enterpriseId, param));
    }

    @ApiOperation("获取店务概况-折线图")
    @PostMapping("/getStoreWorkCharStatistic")
    public ResponseResult<List<StoreWorkDataVO>> getStoreWorkCharStatistic(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.getStoreWorkCharStatistic(enterpriseId, param));
    }

    @ApiOperation("获取店务概况--门店执行力等级分布")
    @PostMapping("/storeExecutiveStatistics")
    public ResponseResult<StoreWorkStatisticsExecutiveDTO> storeExecutiveStatistics(@PathVariable("enterprise-id") String enterpriseId,
                                                                                    @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveStatistics(enterpriseId, param));
    }


    @ApiOperation("获取店务概况--区域执行力红黑榜")
    @PostMapping("/regionExecutiveRank")
    public ResponseResult<List<StoreWorkRegionRankDataVO>> regionExecutiveRank(@PathVariable("enterprise-id") String enterpriseId,
                                                                               @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.regionExecutiveRank(enterpriseId, param));
    }

    @ApiOperation("获取店务概况--门店执行力红黑榜")
    @PostMapping("/storeExecutiveRank")
    public ResponseResult<List<StoreWorkStoreRankDataVO>> storeExecutiveRank(@PathVariable("enterprise-id") String enterpriseId,
                                                                             @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveRank(enterpriseId, param));
    }

    @ApiOperation("获取店务概况--不合格项top5")
    @PostMapping("/columnFailRank")
    public ResponseResult<List<StoreWorkColumnRankDataVO>> columnFailRank(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.columnFailRank(enterpriseId, param));
    }

    @ApiOperation("获取店务概况--事项完成率排名")
    @PostMapping("/completeRateRank")
    public ResponseResult<List<ColumnCompleteRateRankDataVO>> completeRateRank(@PathVariable("enterprise-id") String enterpriseId,
                                                                               @RequestBody StoreWorkDataStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.completeRateRank(enterpriseId, param));
    }

    @ApiOperation("店务报表--区域执行力红明细列表")
    @PostMapping("/regionExecutiveList")
    public ResponseResult<List<StoreWorkStatisticsOverviewVO>> regionExecutiveList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                   @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.regionExecutiveList(enterpriseId, param));
    }

    @ApiOperation("店务报表--区域执行力红汇总列表")
    @PostMapping("/regionExecutiveSummaryList")
    public ResponseResult<List<StoreWorkStatisticsOverviewListVO>> regionExecutiveSummaryList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                   @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.regionExecutiveSummaryList(enterpriseId, param));
    }


    @ApiOperation("店务报表--区域执行力汇总列表导出")
    @PostMapping("/exportRegionExecutiveList")
    public ResponseResult<ImportTaskDO> exportRegionExecutiveList(@PathVariable("enterprise-id") String enterpriseId,
                                                                  @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.exportRegionExecutiveList(enterpriseId, param, UserHolder.getUser().getDbName()));
    }

    @ApiOperation("店务报表--区域执行力红明细列表导出")
    @PostMapping("/exportRegionExecutiveSummaryList")
    public ResponseResult<ImportTaskDO> exportRegionExecutiveSummaryList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                              @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.exportRegionExecutiveSummaryList(enterpriseId, param, UserHolder.getUser().getDbName()));
    }

    @ApiOperation("店务报表--门店执行力红汇总列表")
    @PostMapping("/storeExecutiveSummaryList")
    public ResponseResult<PageInfo<StoreWorkStoreSummaryVO>> storeExecutiveSummaryList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                       @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveSummaryList(enterpriseId, param));
    }

    @ApiOperation("店务报表--门店执行力明细列表导出")
    @PostMapping("/exportStoreExecutiveSummaryList")
    public ResponseResult<ImportTaskDO> exportStoreExecutiveSummaryList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                       @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.exportStoreExecutiveSummaryList(enterpriseId, param, UserHolder.getUser()));
    }

    @ApiOperation("店务报表--门店执行力明细-门店详情")
    @PostMapping("/storeExecutiveDetail")
    public ResponseResult<StoreWorkStoreDetailVO> storeExecutiveDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                                       @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveDetail(enterpriseId, param, UserHolder.getUser()));
    }

    @ApiOperation("店务报表--门店执行力红-门店详情-列表")
    @PostMapping("/storeExecutiveDetailList")
    public ResponseResult<PageInfo<StoreWorkDataTableDetailListVO>> storeExecutiveDetailList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                       @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveDetailList(enterpriseId, param, UserHolder.getUser()));
    }

    @ApiOperation("店务报表--门店执行力红-门店详情-门店作业事项明细")
    @PostMapping("/storeExecutiveDetailColumnList")
    public ResponseResult<List<StoreWorkDataTableDetailColumnListVO>> storeExecutiveDetailColumnList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                             @RequestBody StoreWorkDataColumnListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.storeExecutiveDetailColumnList(enterpriseId, param));
    }

    @ApiOperation("店务报表--门店执行力红-门店详情-门店作业事项明细-导出")
    @PostMapping("/exportStoreExecutiveDetailColumnList")
    public ResponseResult<ImportTaskDO> exportStoreExecutiveDetailColumnList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                                     @RequestBody StoreWorkDataColumnListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.exportStoreExecutiveDetailColumnList(enterpriseId, param, UserHolder.getUser()));
    }

    @ApiOperation("店务报表--事项完成率列表")
    @PostMapping("/columnCompleteRateList")
    public ResponseResult<PageInfo<StoreWorkDataTableColumnListVO>> columnCompleteRateList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                             @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkStatisticsService.columnCompleteRateList(enterpriseId, param, user));
    }

    @ApiOperation("店务报表--事项完成率列表-导出")
    @PostMapping("/exportColumnCompleteRateList")
    public ResponseResult<ImportTaskDO> exportColumnCompleteRateList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                                 @RequestBody StoreWorkDataListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.exportColumnCompleteRateList(enterpriseId, param, UserHolder.getUser()));
    }

    @ApiOperation("店务报表--事项完成率列表-检查项详情列表(分页)")
    @PostMapping("/columnStoreCompleteList")
    public ResponseResult<PageInfo<StoreWorkColumnStoreListVO>> columnStoreCompleteList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                            @RequestBody StoreWorkColumnDetailListRequest param) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(storeWorkStatisticsService.columnStoreCompleteList(enterpriseId, param, user));
    }

    @ApiOperation("店务报表--事项完成率列表-检查项详情列表-导出)")
    @PostMapping("/exportColumnStoreCompleteList")
    public ResponseResult<ImportTaskDO> exportColumnStoreCompleteList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                        @RequestBody StoreWorkColumnDetailListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.exportColumnStoreCompleteList(enterpriseId, param, UserHolder.getUser()));
    }


    @ApiOperation("店务报表--不合格检查项的门店列表")
    @PostMapping("/failColumnStoreList")
    public ResponseResult<PageInfo<StoreWorkFailColumnStoreListVO>> failColumnStoreList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                                 @RequestBody StoreDataListRequest param) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeWorkStatisticsService.failColumnStoreList(enterpriseId, param, UserHolder.getUser()));
    }


    @ApiOperation("门店详情-店务数据总览")
    @PostMapping("/getStoreDetailWorkStatistic")
    public ResponseResult<StoreWorkDataStoreDetailVO> getStoreDetailWorkStatistic(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody StoreWorkStoreDetailStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.getStoreDetailWorkStatistic(enterpriseId, param));
    }

    @ApiOperation("门店详情-店务数据列表")
    @PostMapping("/getStoreDetailWorkStatisticList")
    public ResponseResult<List<StoreWorkDataStoreDetailList>> getStoreDetailWorkStatisticList(@PathVariable("enterprise-id") String enterpriseId,
                                                                                  @RequestBody StoreWorkStoreDetailStatisticRequest param) {
        DataSourceHelper.changeToMy();
        param.setUserId(UserHolder.getUser().getUserId());
        return ResponseResult.success(storeWorkStatisticsService.getStoreDetailWorkStatisticList(enterpriseId, param));
    }

}
