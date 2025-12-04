package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
import com.coolcollege.intelligent.model.achievement.request.*;
import com.coolcollege.intelligent.model.achievement.vo.AchievementDetailVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementUserVO;
import com.coolcollege.intelligent.model.achievement.vo.NewProductDataVO;
import com.coolcollege.intelligent.model.achievement.vo.TOPTenVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author shuchang.wei
 * @date 2021/5/20 9:46
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievement")
public class AchievementController {
    @Resource
    private AchievementService achievementService;

    /**
     * 业绩上传
     */
    @PostMapping("uploadAchievement")
    public ResponseResult<AchievementDetailDO> uploadAchievement(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailDO achievementDetailDO) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementService.uploadAchievementDetail(enterpriseId, achievementDetailDO, user));
    }

    /**
     * 业绩详情删除
     */
    @PostMapping("deleteAchievementDetail")
    public ResponseResult<Boolean> deleteAchievementDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailDeleteRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementService.deleteAchievementDetail(enterpriseId, request.getId(), user));
    }

    /**
     * 移动端业绩详情列表
     */
    @PostMapping("listAchievementDetail")
    public ResponseResult<PageInfo<List<AchievementDetailListDTO>>> listAchievementDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailListRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementService.listAchievementDetail(enterpriseId, request, user));
    }

    /**
     * 移动端业绩详情列表
     */
    @PostMapping("listAchievementDetail/new")
    public ResponseResult<PageVO<AchievementDetailVO>> listAchievementDetailNew(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailListRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        List<AchievementDetailVO> achievementDetailVOList = achievementService.listAppAchievementDetailNew(enterpriseId, request, user, true);
        if (CollectionUtils.isNotEmpty(achievementDetailVOList)) {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(achievementDetailVOList)));
        } else {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>()));
        }
    }

    /**
     * pc端业绩详情列表
     */
    @PostMapping("achievementDetailList")
    public ResponseResult<PageInfo<List<AchievementDetailListDTO>>> achievementDetailList(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailListRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.achievementDetailList(enterpriseId, request));
    }

    /**
     * pc端业绩详情列表导出
     */
    @PostMapping("achievementDetailListExport")
    public ResponseResult<ImportTaskDO> achievementDetailListExport(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementDetailListRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementService.achievementDetailListExport(enterpriseId, request, user));
    }

    /**
     * 移动端业绩管理首页
     */
    @PostMapping("listByStore")
    public ResponseResult<Map<String, Object>> listByStore(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestBody @Valid AchievementRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageInfo(achievementService.listByStore(enterpriseId, request)));
    }

    /**
     * 移动端业绩产生人统计
     */
    @PostMapping("listGroupByUser")
    public ResponseResult<List<AchievementUserVO>> listGroupByUser(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.listGroupByUser(enterpriseId, request));
    }

    /**
     * 移动端业绩记录
     */
    @PostMapping("listDetail")
    public ResponseResult<Map<String, Object>> listDetail(@PathVariable("enterprise-id") String enterpriseId,
                                                          @RequestBody @Valid AchievementRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageInfo(achievementService.listDetail(enterpriseId, request)));
    }

    /**
     * 单个/批量上报
     */
    @PostMapping("report")
    public ResponseResult report(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestBody AchievementRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        achievementService.report(enterpriseId, request, user);
        return ResponseResult.success();
    }

    /**
     * 区域业绩报表导出
     */
    @PostMapping("exportRegionStatistics")
    public ResponseResult<ImportTaskDO> exportRegionStatistics(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportRegionStatistics(enterpriseId, request, user));
    }

    /**
     * 区域业绩报表-按月统计导出
     */
    @PostMapping("exportRegionStatisticsMonth")
    public ResponseResult<ImportTaskDO> exportRegionStatisticsMonth(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportRegionStatisticsMonth(enterpriseId, request, user));
    }

    /**
     * 门店业绩报表
     */
    @PostMapping("exportStoreStatistic")
    public ResponseResult<ImportTaskDO> exportStoreStatistic(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportStoreStatistic(enterpriseId, request, user));
    }

    /**
     * 门店业绩报表-按月导出
     */
    @PostMapping("exportStoreStatisticMonth")
    public ResponseResult<ImportTaskDO> exportStoreStatisticMonth(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportStoreStatisticMonth(enterpriseId, request, user));
    }

    /**
     * 业绩类型报表导出
     */
    @PostMapping("exportAchievementType")
    public ResponseResult<ImportTaskDO> exportAchievementType(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportAchievementType(enterpriseId, request, user));
    }

    /**
     * 业绩明细报表
     */
    @PostMapping("exportAchievementAllDetail")
    public ResponseResult<ImportTaskDO> exportAchievementAllDetail(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementService.exportAchievementAllDetail(enterpriseId, request, user));
    }

    @GetMapping("salesProfile")
    @ApiOperation("实需看板-实时概况")
    public ResponseResult<SalesProfileResponse> salesProfile(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.salesProfile(enterpriseId,mainClass));
    }

    @GetMapping("newSalesProfile")
    @ApiOperation("实需看板-新实时概况")
    public ResponseResult<NewSalesProfileResponse> newSalesProfile(@PathVariable("enterprise-id") String enterpriseId,
                                                                   @RequestParam("startTime") Long startTime,
                                                                   @RequestParam("endTime") Long endTime,
                                                                   @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.newSalesProfile(enterpriseId,mainClass,startTime,endTime));
    }

    @GetMapping("home/salesProfile")
    @ApiOperation("首页-实时概况")
    public ResponseResult<HomeSalesProfileResponse> homeSalesProfile(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.homeSalesProfile(enterpriseId));
    }


    @GetMapping("month/category/top5")
    @ApiOperation("品类销售额top5")
    public ResponseResult monthCategoryTop5(@PathVariable("enterprise-id") String enterpriseId,
                                            @RequestParam("startTime") Long startTime,
                                            @RequestParam("endTime") Long endTime,
                                            @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.monthTop5(enterpriseId,startTime,endTime,mainClass));
    }


    @GetMapping("/chooseCategory")
    public ResponseResult chooseCategory(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.chooseCategory(enterpriseId));
    }

    @GetMapping("month/middle/top5")
    @ApiOperation("中类销售额top5")
    public ResponseResult monthMiddleTop5(@PathVariable("enterprise-id") String enterpriseId,
                                          @RequestParam("categoryId") String categoryId,@RequestParam("startTime") Long startTime,@RequestParam("endTime") Long endTime,
                                          @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.monthMiddleTop5(enterpriseId, categoryId,startTime,endTime,mainClass));
    }

    @GetMapping("region/top5")
    @ApiOperation("区域销售额top5")
    public ResponseResult<List<RegionTop5Response>> regionTop5(@PathVariable("enterprise-id") String enterpriseId,@RequestParam("startTime") Long startTime,@RequestParam("endTime") Long endTime,
                                     @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.regionTop5(enterpriseId,startTime,endTime,mainClass));
    }

    @GetMapping("store/top5")
    @ApiOperation("门店销售额top5")
    public ResponseResult<List<RegionTop5Response>> storeTop5(@PathVariable("enterprise-id") String enterpriseId,@RequestParam("startTime") Long startTime,@RequestParam("endTime") Long endTime,
                                    @RequestParam(value = "mainClass",required = false) String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.storeTop5(enterpriseId,startTime,endTime,mainClass));
    }


    @ApiOperation("松下新增门店关系")
    @PostMapping("panasonic/add")
    public ResponseResult panasonicAdd(@PathVariable("enterprise-id") String enterpriseId,
                                       @RequestBody PanasonicAddRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.panasonicAdd(enterpriseId, request));
    }

    @ApiOperation("松下查询门店关系")
    @GetMapping("panasonic/find")
    public ResponseResult<List<PanasonicFindResponse>> panasonicFind(@PathVariable("enterprise-id") String enterpriseId,
                                                                     @RequestParam("storeId") String storeId,
                                                                     @RequestParam("category") String category) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.panasonicFind(enterpriseId, storeId, category));
    }

    @ApiOperation("松下查询门店关系")
    @GetMapping("panasonic/find2")
    public ResponseResult panasonicFind2(@PathVariable("enterprise-id") String enterpriseId,
                                         @RequestParam("storeId") String storeId,
                                         @RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "middleName", required = false) String middleName) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.panasonicFind2(enterpriseId, storeId, name,middleName));
    }


    @ApiOperation("获取品类层级关系")
    @GetMapping("/type/getStructure")
    public ResponseResult<List<PanasonicFindResponse>> getStructure (@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getStructure(enterpriseId));
    }

    @ApiOperation("获取所有品类")
    @GetMapping("/type/getAllCategory")
    public ResponseResult<List<String>> getAllCategory (@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getAllCategory(enterpriseId));
    }

    @ApiOperation("根据品类获取中类")
    @GetMapping("/type/getMiddleClassByCategory")
    public ResponseResult<List<String>> getMiddleClassByCategory (@PathVariable("enterprise-id") String enterpriseId,
                                                                 @RequestParam("category") String category) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getMiddleClassByCategory(enterpriseId, category));
    }

    @ApiOperation("根据品类和中类获取类型")
    @GetMapping("/type/getTypeByCategoryAndMiddleClass")
    public ResponseResult<List<String>> getTypeByCategoryAndMiddleClass (@PathVariable("enterprise-id") String enterpriseId,
                                                                       @RequestParam("category") String category,
                                                                       @RequestParam("middleClass") String middleClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getTypeByCategoryAndMiddleClass(enterpriseId, category, middleClass));
    }

    @ApiOperation("根据大类获取中类")
    @GetMapping("/type/getMiddleClassBymainClass")
    public ResponseResult<List<String>> getMiddleClassBymainClass (@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestParam("mainClass") String mainClass) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getMiddleClassByMainClass(enterpriseId, mainClass));
    }


    @ApiOperation("获取新品出样统计列表")
    @GetMapping("/newProduct/getNewProductDataList")
    public ResponseResult<PageInfo<NewProductDataVO>> getNewProductDataList (@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestParam(value = "category", required = false) String category,
                                                                         @RequestParam(value = "middleClass", required = false) String middleClass,
                                                                         @RequestParam(value = "type",required = false) String type,
                                                                         @RequestParam(value="pageNum",required = false,defaultValue = "1") Integer pageNum,
                                                                         @RequestParam(value="pageSize",required = false,defaultValue = "10") Integer pageSize) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getNewProductDataList(enterpriseId, category, middleClass, type, pageNum, pageSize));
    }

    @ApiOperation("获取TOP10型号列表")
    @GetMapping("/product/getTopTenList")
    public ResponseResult<List<TOPTenVO>> getTopTenList (@PathVariable("enterprise-id") String enterpriseId,
                                                         @RequestParam(value = "sortField",required = false,defaultValue = "achievementAmount") String sortField,
                                                         @RequestParam(value = "startTime") Long startTime,
                                                         @RequestParam(value = "endTime") Long endTime,
                                                         @RequestParam(value = "mainClass",required = false)String mainClass,
                                                         @RequestParam(value = "storeId",required = false)String storeId,
                                                         @RequestParam(value = "limit",required = false,defaultValue = "10")Integer limit){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getTopTenList(enterpriseId, sortField, new Date(startTime),new Date(endTime),mainClass,storeId,limit));
    }

    @ApiOperation("获取上市门店列表")
    @GetMapping("/store/getMarketStoreList")
    public ResponseResult<List<String>> getMarketStoreList (@PathVariable("enterprise-id") String enterpriseId,
                                                            @RequestParam(value = "type")String type){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementService.getMarketStoreList(enterpriseId,type));
    }


}
