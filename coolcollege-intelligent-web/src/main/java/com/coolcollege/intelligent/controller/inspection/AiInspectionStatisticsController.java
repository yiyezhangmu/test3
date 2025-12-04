package com.coolcollege.intelligent.controller.inspection;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.inspection.AiInspectionStatisticsDTO;
import com.coolcollege.intelligent.model.inspection.vo.*;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.inspection.AiInspectionStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author byd
 * @date 2025-11-13 15:27
 */
@Slf4j
@Api(tags = "AI巡检分析")
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/aiInspectionStatistics")
@RestController
public class AiInspectionStatisticsController {

    @Resource
    private AiInspectionStatisticsService aiInspectionStatisticsService;

    @ApiOperation("AI巡检概览")
    @PostMapping("/inspectionOverview")
    public ResponseResult<AiInspectionReportVO> inspectionOverview(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionStatisticsDTO statisticsDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStatisticsService.inspectionOverview(enterpriseId, UserHolder.getUser().getUserId(), statisticsDTO));
    }

    @ApiOperation("AI巡检趋势")
    @PostMapping("/inspectionTrend")
    public ResponseResult<List<AiInspectionTendReportVO>> inspectionTrend(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionStatisticsDTO statisticsDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStatisticsService.inspectionTrend(enterpriseId, UserHolder.getUser().getUserId(), statisticsDTO));
    }

    @ApiOperation("AI巡检凸出问题")
    @PostMapping("/sceneProblemRate")
    public ResponseResult<List<AiInspectionProblemReportVO>> sceneProblemRate(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionStatisticsDTO statisticsDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStatisticsService.sceneProblemRate(enterpriseId, UserHolder.getUser().getUserId(), statisticsDTO));
    }

    @ApiOperation("问题门店TOP5")
    @PostMapping("/problemStoreTop")
    public ResponseResult<List<AiInspectionProblemStoreReportVO>> problemStoreTop(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionStatisticsDTO statisticsDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStatisticsService.problemStoreTop(enterpriseId, UserHolder.getUser().getUserId(), statisticsDTO));
    }

    @ApiOperation("问题分布时间段(key为时间点)")
    @PostMapping("/sceneProblemTimeList")
    public ResponseResult<List<AiInspectionProblemTimeReportListVO>> sceneProblemTimeList(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionStatisticsDTO statisticsDTO) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStatisticsService.sceneProblemTimeList(enterpriseId, UserHolder.getUser().getUserId(), statisticsDTO));
    }
}
