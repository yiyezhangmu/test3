package com.coolcollege.intelligent.controller.inspection;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.ai.AIResultDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreBatchDTO;
import com.coolcollege.intelligent.model.ai.AiCommentAndScoreVO;
import com.coolcollege.intelligent.model.ai.vo.AiModelSceneVO;
import com.coolcollege.intelligent.model.common.IdDTO;
import com.coolcollege.intelligent.model.common.IdRequest;
import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesDTO;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportDetailRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionReportRequest;
import com.coolcollege.intelligent.model.inspection.request.AiInspectionStrategiesRequest;
import com.coolcollege.intelligent.model.inspection.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelSceneService;
import com.coolcollege.intelligent.service.inspection.AiInspectionStrategiesService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author byd
 * @date 2025-09-29 15:19
 */
@Slf4j
@Api(tags = "AI巡检")
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/aiInspection")
@RestController
public class AiInspectionController {

    @Resource
    private AiInspectionStrategiesService aiInspectionStrategiesService;

    @Autowired
    private AiModelSceneService aiModelSceneService;

    @Resource
    private AIService aiService;

    @ApiOperation("获取AI巡检策略列表")
    @GetMapping("/selectList")
    public ResponseResult<PageInfo<AiInspectionStrategiesVO>> selectList(
            @PathVariable("enterprise-id") String enterpriseId,
            AiInspectionStrategiesDTO query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStrategiesService.selectList(enterpriseId, query));
    }

    @ApiOperation("添加AI巡检策略")
    @PostMapping("/add")
    public ResponseResult<Long> add(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody @Valid AiInspectionStrategiesRequest strategiesRequest) {
        DataSourceHelper.reset();
        List<AiModelSceneVO> aiModelSceneList = aiModelSceneService.list(null, strategiesRequest.getSceneIdList());
        DataSourceHelper.changeToMy();
        // 获取当前用户ID
        CurrentUser currentUser = UserHolder.getUser();
        String userId = currentUser.getUserId();
        Long id = aiInspectionStrategiesService.add(enterpriseId, strategiesRequest, userId, aiModelSceneList);
        return ResponseResult.success(id);
    }

    @ApiOperation("更新AI巡检策略")
    @PostMapping("/update")
    public ResponseResult<Boolean> update(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody @Valid AiInspectionStrategiesRequest strategiesRequest) {
        DataSourceHelper.changeToMy();
        // 获取当前用户ID
        CurrentUser currentUser = UserHolder.getUser();
        String userId = currentUser.getUserId();
        boolean result = aiInspectionStrategiesService.update(enterpriseId, strategiesRequest, userId);
        return ResponseResult.success(result);
    }

    @ApiOperation("启用AI巡检策略")
    @PostMapping("/enable")
    public ResponseResult<Boolean> enable(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody IdRequest idDTO) {
        DataSourceHelper.changeToMy();
        // 获取当前用户ID
        CurrentUser currentUser = UserHolder.getUser();
        String userId = currentUser.getUserId();
        boolean result = aiInspectionStrategiesService.enable(enterpriseId, idDTO.getId(), userId);
        return ResponseResult.success(result);
    }

    @ApiOperation("禁用AI巡检策略")
    @PostMapping("/disable")
    public ResponseResult<Boolean> disable(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody IdRequest idDTO) {
        DataSourceHelper.changeToMy();
        // 获取当前用户ID
        CurrentUser currentUser = UserHolder.getUser();
        String userId = currentUser.getUserId();
        boolean result = aiInspectionStrategiesService.disable(enterpriseId, idDTO.getId(), userId);
        return ResponseResult.success(result);
    }

    @ApiOperation("获取AI巡检策略详情")
    @GetMapping("/getDetail")
    public ResponseResult<AiInspectionStrategiesVO> getDetail(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestParam("id") Long id) {
        DataSourceHelper.changeToMy();
        AiInspectionStrategiesVO detail = aiInspectionStrategiesService.getDetail(enterpriseId, id);
        return ResponseResult.success(detail);
    }

    @ApiOperation("删除AI巡检策略")
    @PostMapping("/delete")
    public ResponseResult<Boolean> delete(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody IdRequest idDTO) {
        DataSourceHelper.changeToMy();
        // 获取当前用户ID
        CurrentUser currentUser = UserHolder.getUser();
        String userId = currentUser.getUserId();
        boolean result = aiInspectionStrategiesService.delete(enterpriseId, idDTO.getId(), userId);
        return ResponseResult.success(result);
    }

    @ApiOperation("获取日报/周报列表")
    @PostMapping("/dailyReportList")
    public ResponseResult<PageInfo<AiInspectionStatisticsVO>> dailyReportList(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionReportRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStrategiesService.dailyReportList(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("获取日报/周报统计汇总")
    @PostMapping("/dailyReportCount")
    public ResponseResult<AiInspectionStatisticsTotalVO> dailyReportCount(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionReportRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStrategiesService.dailyReportCount(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("获取日报/周报详情信息")
    @PostMapping("/dailyReportDetail")
    public ResponseResult<AiInspectionStatisticsReportDetailVO> dailyReportDetail(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionReportDetailRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStrategiesService.dailyReportDetail(enterpriseId, query));
    }


    @ApiOperation("图片库列表")
    @PostMapping("/imageList")
    public ResponseResult<PageInfo<AiInspectionStatisticsPicListVO>> imageList(
            @PathVariable("enterprise-id") String enterpriseId,
            @RequestBody AiInspectionReportRequest query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aiInspectionStrategiesService.imageList(enterpriseId, query, UserHolder.getUser().getUserId()));
    }

    @ApiOperation("获取ai模型测试结果")
    @PostMapping("/aiInspectionResolveTest")
    public ResponseResult<AiCommentAndScoreVO> aiInspectionResolveTest(@PathVariable("enterprise-id") String enterpriseId,
                                                                       @RequestBody AiCommentAndScoreBatchDTO request){
        try {
            AIResultDTO aiResultDTO = aiService.aiInspectionResolveTest(enterpriseId, null, request.getImageList(), request.getStandardDesc(), request.getAiModel());
            AiCommentAndScoreVO aiCommentAndScoreVO = new AiCommentAndScoreVO(null, aiResultDTO.getAiResult(), null);
            return ResponseResult.success(aiCommentAndScoreVO);
        } catch (ServiceException e) {
            log.error("获取ai模型测试结果异常", e);
            throw e;
        } catch (Exception e) {
            log.error("获取ai模型测试结果异常", e);
        }
        return ResponseResult.success(null);
    }
}