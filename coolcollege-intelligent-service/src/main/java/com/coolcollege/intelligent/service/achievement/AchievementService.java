package com.coolcollege.intelligent.service.achievement;

import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
import com.coolcollege.intelligent.model.achievement.request.*;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/5/20 10:29
 */
public interface AchievementService {
    /**
     * 业绩详情上传
     *
     * @param enterpriseId        企业id
     * @param achievementDetailDO 业绩详情
     * @param user                当前用户
     * @return true
     */
    AchievementDetailDO uploadAchievementDetail(String enterpriseId, AchievementDetailDO achievementDetailDO, CurrentUser user);

    /**
     * 删除业绩详情
     *
     * @param enterpriseId 企业id
     * @param id           详情id
     * @param user         当前用户
     * @return true
     */
    Boolean deleteAchievementDetail(String enterpriseId, Long id, CurrentUser user);

    /**
     * 移动端业绩详情列表
     *
     * @param enterpriseId 企业id
     * @param request      查询条件
     * @param user         当前用户
     * @return 业绩详情列表
     */
    PageInfo<List<AchievementDetailListDTO>> listAchievementDetail(String enterpriseId, AchievementDetailListRequest request, CurrentUser user);


    List<AchievementDetailVO> listAppAchievementDetailNew(String enterpriseId, AchievementDetailListRequest request, CurrentUser user,Boolean isPage);

    /**
     * pc端业绩详情列表
     *
     * @param enterpriseId 企业id
     * @param request      查询条件
     * @return 业绩详情列表
     */
    PageInfo<List<AchievementDetailListDTO>> achievementDetailList(String enterpriseId, AchievementDetailListRequest request);

    /**
     * pc端业绩报表导出
     *
     * @param enterpriseId 企业id
     * @param request      查询条件
     * @param user         当前用户
     */
    ImportTaskDO achievementDetailListExport(String enterpriseId, AchievementDetailListRequest request, CurrentUser user);

    /**
     * 业绩管理移动端首页
     * @author chenyupeng
     * @date 2021/10/27
     * @param eid
     * @param request
     * @return com.github.pagehelper.PageInfo<com.coolcollege.intelligent.model.achievement.vo.AchievementStoreVO>
     */
    PageInfo<AchievementStoreVO> listByStore(String eid, AchievementRequest request);

    /**
     * 业绩产生人统计
     * @author chenyupeng
     * @date 2021/10/27
     * @param eid
     * @param request
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.vo.AchievementUserVO>
     */
    List<AchievementUserVO> listGroupByUser(String eid, AchievementRequest request);

    /**
     * 移动端业绩记录
     * @author chenyupeng
     * @date 2021/10/27
     * @param eid
     * @param request
     * @return com.github.pagehelper.PageInfo<com.coolcollege.intelligent.model.achievement.vo.AchievementDetailVO>
     */
    PageInfo<AchievementDetailVO> listDetail(String eid, AchievementRequest request);

    /**
     * 单个/批量上报
     * @author chenyupeng
     * @date 2021/10/27
     * @param eid
     * @param request
     * @return void
     */
    void report(String eid, AchievementRequest request,CurrentUser user);

    /**
     * 区域业绩报表导出
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportRegionStatistics(String eid, AchievementExportRequest request, CurrentUser user);

    /**
     * 区域业绩报表导出 按月份
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportRegionStatisticsMonth(String eid, AchievementExportRequest request, CurrentUser user);

    /**
     * 门店业绩报表导出
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportStoreStatistic(String eid, AchievementExportRequest request, CurrentUser user);

    /**
     * 门店业绩报表导出 按月份
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportStoreStatisticMonth(String eid, AchievementExportRequest request, CurrentUser user);

    /**
     * 业绩类型报表导出
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportAchievementType(String eid, AchievementExportRequest request, CurrentUser user);

    /**
     * 业绩明细报表导出
     * @author chenyupeng
     * @date 2021/10/30
     * @param eid
     * @param request
     * @param user
     * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
     */
    ImportTaskDO exportAchievementAllDetail(String eid, AchievementExportRequest request, CurrentUser user);

    SalesProfileResponse salesProfile(String enterpriseId,String mainClass);

    List<AchieveMonthTop5Response> monthTop5(String enterpriseId,Long startTime,Long endTime,String mainClass);

    List<ChooseCategoryResponse> chooseCategory(String enterpriseId);

    List<AchieveMonthMiddleTop5Response> monthMiddleTop5(String enterpriseId, String categoryId,Long startTime,Long endTime,String mainClass);

    List<RegionTop5Response> regionTop5(String enterpriseId,Long startTime,Long endTime,String mainClass);

    List<RegionTop5Response> storeTop5(String enterpriseId,Long startTime,Long endTime,String mainClass);

    /**
     * 松下新增临时映射关系
     * @param enterpriseId
     * @param request
     * @return
     */
    boolean panasonicAdd(String enterpriseId, PanasonicAddRequest request);

    List<PanasonicFindResponse> panasonicFind(String enterpriseId, String storeId,String category);

    List<PanasonicFindResponse> panasonicFind2(String enterpriseId, String storeId,String name,String middleName);

    List<PanasonicFindResponse> getStructure(String enterpriseId);

    PageInfo<NewProductDataVO> getNewProductDataList(String enterpriseId,String category,String middleClass,String type,Integer pageNum,Integer pageSize);


    List<TOPTenVO> getTopTenList(String enterpriseId, String sortField, Date startTime,Date endTime,String mainClass,String storeId,Integer limit);

    List<String> getAllCategory(String enterpriseId);

    List<String> getMiddleClassByCategory(String enterpriseId,String category);

    List<String> getTypeByCategoryAndMiddleClass(String enterpriseId,String category,String middleClass);

    List<String> getMarketStoreList(String enterpriseId,String type);

    List<String> getMiddleClassByMainClass(String enterpriseId, String mainClass);

    HomeSalesProfileResponse homeSalesProfile(String enterpriseId);

    NewSalesProfileResponse newSalesProfile(String enterpriseId, String mainClass,Long startTime,Long endTime);
}
