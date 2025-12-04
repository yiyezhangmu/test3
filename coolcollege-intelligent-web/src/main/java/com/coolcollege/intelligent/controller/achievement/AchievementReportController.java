package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.vo.PersonalAchievementVO;
import com.coolcollege.intelligent.model.achievement.vo.StoreRealDataVO;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementReportDetailQuery;
import com.coolcollege.intelligent.model.unifytask.query.AchievementReportQuery;
import com.coolcollege.intelligent.model.unifytask.query.PersonalAchievementQuery;
import com.coolcollege.intelligent.service.achievement.AchievementReportService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Description: 松下统计controller
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Api("松下统计")
@Slf4j
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievementReport")
public class AchievementReportController {

    @Autowired
    private AchievementReportService achievementReportService;

    @ApiOperation("区域统计")
    @GetMapping(path = "/regionReport")
    public ResponseResult<List<AchieveRegionReportDTO>> regionReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                         AchievementReportQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.regionReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getMainClass(), query.getCategory(),
                query.getMiddleClass(), query.getRegionId()));
    }

    @ApiOperation("区域统计明细")
    @GetMapping(path = "/regionDetailReport")
    public ResponseResult<List<AchieveRegionDetailReportDTO>> regionDetailReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                                     AchievementReportDetailQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.regionDetailReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getReportType(), query.getCategory(),
                query.getMiddleClass(), query.getRegionId()));
    }


    @ApiOperation("门店统计统计")
    @GetMapping(path = "/storeReport")
    public ResponseResult<List<AchieveStoreReportDTO>> storeReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                   AchievementReportQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.storeReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getMainClass(), query.getCategory(),
                query.getMiddleClass(), query.getRegionId()));
    }

    @ApiOperation("门店统计明细")
    @GetMapping(path = "/storeDetailReport")
    public ResponseResult<List<AchieveStoreDetailReportDTO>> storeDetailReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                               AchievementReportDetailQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.storeDetailReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getReportType(), query.getCategory(),
                query.getMiddleClass(), query.getStoreId()));
    }

    @ApiOperation("型号统计")
    @GetMapping(path = "/goodTypeReport")
    public ResponseResult<PageInfo<AchieveGoodTypeReportDTO>> goodTypeReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                             AchievementReportQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.goodTypeReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getMainClass(), query.getCategory(),
                query.getMiddleClass(), query.getStoreId(), query.getType(), query.getPageNumber(), query.getPageSize()));
    }

    @ApiOperation("型号统计明细")
    @GetMapping(path = "/goodTypeDetailReport")
    public ResponseResult<List<AchieveStoreDetailReportDTO>> goodTypeDetailReport(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                                               AchievementReportDetailQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.goodTypeDetailReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getReportType(), query.getGoodType()));
    }

    @ApiOperation("品类报表")
    @GetMapping(path = "/categoryReport")
    public ResponseResult<PageInfo<AchieveGoodTypeReportDTO>> categoryReport(@PathVariable(value = "enterprise-id") String enterpriseId, AchievementReportQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.categoryReport(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getMainClass(), query.getPageNumber(), query.getPageSize()));
    }

    @ApiOperation("品类报表:查看中类")
    @GetMapping(path = "/queryMiddleClassInfoByCategory")
    public ResponseResult<PageInfo<AchieveGoodTypeReportDTO>> queryMiddleClassInfoByCategory(@PathVariable(value = "enterprise-id") String enterpriseId,AchievementReportQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.queryMiddleClassInfoByCategory(enterpriseId,query));
    }

    @ApiOperation("品类报表:趋势图")
    @GetMapping(path = "/categoryReportPic")
    public ResponseResult<List<AchieveStoreDetailReportDTO>> categoryReportPic(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                                                  AchievementReportDetailQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.categoryReportPic(enterpriseId, query.getBeginTime(), query.getEndTime(), query.getCategory(),query.getReportType(), query.getMiddleClass()));
    }

    @ApiOperation("获取个人业绩列表")
    @PostMapping(path = "/queryPersonalAchievement")
    public ResponseResult<PageInfo<PersonalAchievementVO>> queryPersonalAchievement(@PathVariable(value = "enterprise-id") String enterpriseId,@RequestBody PersonalAchievementQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.queryPersonalAchievement(enterpriseId, query));
    }

    @ApiOperation("获取个人业绩销售型号列表")
    @PostMapping(path = "/queryPersonalTypeAchievement")
    public ResponseResult<List<AchieveGoodTypeReportDTO>> queryPersonalTypeAchievement(@PathVariable(value = "enterprise-id") String enterpriseId,@RequestBody PersonalAchievementQuery query) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.queryPersonalTypeAchievement(enterpriseId, query));
    }



    @ApiOperation("获取所有职位")
    @GetMapping(path = "/getAllPosition")
    public ResponseResult<List<SysRoleBaseVO>> getAllPosition(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.getAllPosition(enterpriseId));
    }


    @ApiOperation("门店详情:实需数据")
    @GetMapping(path = "/getStoreRealData")
    public ResponseResult<StoreRealDataVO> getStoreRealData(@PathVariable(value = "enterprise-id") String enterpriseId,
                                                            @RequestParam(value = "storeId") String storeId,
                                                            @RequestParam(value = "beginDate",required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginDate,
                                                            @RequestParam(value = "endDate",required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementReportService.getStoreRealData(enterpriseId,storeId,beginDate,endDate));
    }
}
