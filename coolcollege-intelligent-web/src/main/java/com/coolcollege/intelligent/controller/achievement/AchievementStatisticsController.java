package com.coolcollege.intelligent.controller.achievement;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTotalAmountDTO;
import com.coolcollege.intelligent.model.achievement.request.AchievementDetailRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementStoreStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTotalStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTypeStatisticsRequest;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementStatisticsService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

/**
 * @Description: 业绩门店目标controller
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Slf4j
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievementStatistics")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementStatisticsController {

    private final AchievementStatisticsService achievementStatisticsService;

    @PostMapping("/queryTable")
    public ResponseResult<AchievementStatisticsRegionListVO> queryTable(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestBody @Valid AchievementStatisticsReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        req.setShowCurrent(false);
        return ResponseResult.success(achievementStatisticsService.getRegionStatisticsTable(enterpriseId, req, user));
    }

    /**
     * 业绩区域详细报表
     * @param enterpriseId
     * @param req
     * @return
     */
    @PostMapping("/region/detail")
    public ResponseResult<AchievementStatisticsRegionListVO> regionDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                                      @RequestBody @Valid AchievementStatisticsReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementStatisticsService.getRegionStatisticsTable(enterpriseId, req, user));
    }

    @PostMapping("/queryChart")
    public ResponseResult<AchievementStatisticsRegionSeriesVO> queryChart(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody @Valid AchievementStatisticsReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        req.setShowCurrent(false);
        return ResponseResult.success(achievementStatisticsService.getRegionStatisticsChart(enterpriseId, req, user));
    }

    /**
     * 业绩区域报表 图表
     * @param enterpriseId
     * @param req
     * @return
     */
    @PostMapping("/region/table")
    public ResponseResult<AchievementStatisticsRegionSeriesVO> regionTable(@PathVariable("enterprise-id") String enterpriseId,
                                                                          @RequestBody @Valid AchievementStatisticsReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementStatisticsService.getRegionStatisticsChart(enterpriseId, req, user));
    }

    @GetMapping("/queryStore")
    public ResponseResult<AchievementStatisticsStoreTableVO> queryStore(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestParam("storeId") String storeId,
                                                                        @RequestParam("beginDate") Date beginDate) {
        DataSourceHelper.changeToMy();
        return ResponseResult
            .success(achievementStatisticsService.getStoreStatistics(enterpriseId, storeId, beginDate));
    }

    /**
     * 业绩明细报表
     * @param request
     * @return
     */
    @PostMapping("/detail")
    public ResponseResult<PageVO<AchievementDetailVO>> detailStatistics(@RequestBody AchievementDetailRequest request) {
        DataSourceHelper.changeToMy();
        List<AchievementDetailVO> achievementDetailVOList = achievementStatisticsService.detailStatistics(UserHolder.getUser().getEnterpriseId(), request);

        if (CollectionUtils.isNotEmpty(achievementDetailVOList)) {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(achievementDetailVOList)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(new ArrayList<>())));
        }
    }

    /**
     * 业绩类型报表
     * @param request
     * @return
     */
    @PostMapping("/achievementType")
    public ResponseResult<PageVO<AchievementDetailVO>> achievementType(@RequestBody AchievementTypeStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult
                .success(achievementStatisticsService.achievementTypeStatistics(UserHolder.getUser().getEnterpriseId(),request));
    }

    /**
     * 业绩门店报表 日
     * @param request
     * @return
     */
    @PostMapping("/store/detail")
    public ResponseResult<PageVO<AchievementStoreDetailVO>> storeDetailStatistics(@RequestBody AchievementStoreStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult
                .success(achievementStatisticsService.storeDetailStatistics(UserHolder.getUser().getEnterpriseId(),request));
    }

    /**
     * 业绩门店报表 月
     * @param request
     * @return
     */
    @PostMapping("/store/month")
    public ResponseResult<PageVO<AchievementMonthDetailVO>> storeMonthStatistics(@RequestBody AchievementStoreStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult
                .success(achievementStatisticsService.storeMonthStatistics(UserHolder.getUser().getEnterpriseId(),request));
    }

    @PostMapping("totalAmount")
    public ResponseResult<AchievementTotalAmountDTO> totalAmount(@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestBody AchievementTotalStatisticsRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult
                .success(achievementStatisticsService.totalAmountStatistics(enterpriseId,request));
    }


    @InitBinder
    public void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new Date(Long.valueOf(text)));
            }
        });
    }

}
