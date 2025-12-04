package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.unifytask.query.AchievementReportQuery;
import com.coolcollege.intelligent.model.unifytask.query.PersonalAchievementQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/5/20 10:05
 */
@Mapper
public interface AchievementDetailMapper {
    /**
     * 批量插入
     *
     * @param enterpriseId         企业id
     * @param achievementDetailDOs 业绩详情
     * @return 插入数据条数
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("entity") AchievementDetailDO achievementDetailDOs);

    int insertBatchDetail(String eid, List<AchievementDetailDO> list);

    /**
     * 删除详情
     *
     * @param enterpriseId 奇异id
     * @param id           详情id
     * @return 删除的条数
     */
    int deleteAchievementDetail(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 移动端获取业绩详情列表
     *
     * @param enterpriseId 企业id
     * @param beginDate    开始时间
     * @param endDate      结束时间
     * @param storeIds     门店id
     * @param createUserId 上报人id
     * @return
     */
    List<AchievementDetailDO> listAchievementDetail(@Param("enterpriseId") String enterpriseId,
                                                    @Param("beginDate") Date beginDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("storeIds") List<String> storeIds,
                                                    @Param("createUserId") String createUserId);

    /**
     * pc获取业绩详情列表
     *
     * @param enterpriseId   企业id
     * @param beginDate      开始时间
     * @param endDate        结束时间
     * @param storeIds       门店id
     * @param typeIds        业绩类型id
     * @param produceUserIds 上报人id
     * @return
     */
    List<AchievementDetailDO> achievementDetailList(@Param("enterpriseId") String enterpriseId,
                                                    @Param("beginDate") Date beginDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("storeIds") List<String> storeIds,
                                                    @Param("formworkId") Long formworkId,
                                                    @Param("typeIds") List<Long> typeIds,
                                                    @Param("produceUserIds") List<String> produceUserIds,
                                                    @Param("isNullProduceUser") Boolean isNullProduceUser,
                                                    @Param("createUserId") String createUserId,
                                                    @Param("achievementFormworkType") String achievementFormworkType);

    List<AchievementDetailDO> achievementDetailListGroupByStore(@Param("enterpriseId") String enterpriseId,
                                                                @Param("beginDate") Date beginDate,
                                                                @Param("endDate") Date endDate,
                                                                @Param("storeIds") List<String> storeIds,
                                                                @Param("formworkId") Long formworkId,
                                                                @Param("typeIds") List<Long> typeIds,
                                                                @Param("produceUserIds") List<String> produceUserIds,
                                                                @Param("isNullProduceUser") Boolean isNullProduceUser,
                                                                @Param("createUserId") String createUserId,
                                                                @Param("formworkType") String formworkType);

    List<AchievementDetailVO> pageAchievementDetail(@Param("enterpriseId") String enterpriseId,
                                                    @Param("beginDate") Date beginDate,
                                                    @Param("endDate") Date endDate,
                                                    @Param("storeIds") List<String> storeIds,
                                                    @Param("formworkId") Long formworkId,
                                                    @Param("typeIds") List<Long> typeIds,
                                                    @Param("produceUserIds") List<String> produceUserIds,
                                                    @Param("isNullProduceUser") Boolean isNullProduceUser,
                                                    @Param("createUserId") String createUserId,
                                                    @Param("storeName") String storeName,
                                                    @Param("showCurrent") Boolean showCurrent,
                                                    @Param("regionPath") String regionPath,
                                                    @Param("regionId") Long regionId);

    Integer countAchievementDetail(@Param("enterpriseId") String enterpriseId,
                                   @Param("beginDate") Date beginDate,
                                   @Param("endDate") Date endDate,
                                   @Param("storeIds") List<String> storeIds,
                                   @Param("formworkId") Long formworkId,
                                   @Param("typeIds") List<Long> typeIds,
                                   @Param("produceUserIds") List<String> produceUserIds,
                                   @Param("isNullProduceUser") Boolean isNullProduceUser,
                                   @Param("createUserId") String createUserId,
                                   @Param("storeName") String storeName,
                                   @Param("showCurrent") Boolean showCurrent,
                                   @Param("regionPath") String regionPath,
                                   @Param("regionId") Long regionId);


    List<AchievementDetailVO> achievementDetailGroupByAchievementType(@Param("enterpriseId") String enterpriseId,
                                                                      @Param("beginDate") Date beginDate,
                                                                      @Param("endDate") Date endDate,
                                                                      @Param("storeIds") List<String> storeIds,
                                                                      @Param("formworkId") Long formworkId,
                                                                      @Param("typeIds") List<Long> typeIds,
                                                                      @Param("produceUserIds") List<String> produceUserIds,
                                                                      @Param("isNullProduceUser") Boolean isNullProduceUser,
                                                                      @Param("createUserId") String createUserId);


    /**
     * 根据id查询详情
     *
     * @param enterpriseId 企业id
     * @param id           详情id
     * @return
     */
    AchievementDetailDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);


    List<AchievementDetailDO> getRegionAmount(@Param("eid") String eid,
                                              @Param("regionPath") String regionPath,
                                              @Param("regionId") Long regionId,
                                              @Param("beginTime") String beginTime,
                                              @Param("endTime") String endTime,
                                              @Param("showCurrent") Boolean showCurrent);

    /**
     * 获取区域日业绩总额
     *
     * @param eid
     * @param regionPath
     * @param beginTime
     * @param endTime
     * @return List<AchievementDetailDO>
     * @author mao
     * @date 2021/5/25 15:36
     */
    List<AchievementDetailDO> getRegionDayAmount(@Param("eid") String eid,
                                                 @Param("regionPath") String regionPath,
                                                 @Param("regionId") Long regionId,
                                                 @Param("achievementTypeId") Long achievementTypeId,
                                                 @Param("beginTime") String beginTime,
                                                 @Param("endTime") String endTime,
                                                 @Param("showCurrent") Boolean showCurrent,
                                                 @Param("achievementFormworkId") Long achievementFormworkId);

    /**
     * 获取门店业绩总额
     *
     * @param eid
     * @param storeId
     * @param beginTime
     * @param endTime
     * @return BigDecimal
     * @author mao
     * @date 2021/5/25 19:50
     */
    AchievementDetailDO getStoreAmount(@Param("eid") String eid, @Param("storeId") String storeId,
                                       @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 计算导出业绩详情总数
     */
    Long countExportList(@Param("enterpriseId") String enterpriseId, @Param("beginDate") Date beginDate,
                         @Param("endDate") Date endDate, @Param("storeIds") List<String> storeIds,
                         @Param("typeIds") List<Long> typeIds, @Param("produceUserIds") List<String> produceUserIds,
                         @Param("isNullProduceUser") Boolean isNullProduceUser);

    /**
     * 导出业绩详情
     */
    List<AchievementDetailExportDTO> exportList(@Param("enterpriseId") String enterpriseId, @Param("beginDate") Date beginDate,
                                                @Param("endDate") Date endDate, @Param("storeIds") List<String> storeIds,
                                                @Param("typeIds") List<Long> typeIds, @Param("produceUserIds") List<String> produceUserIds,
                                                @Param("isNullProduceUser") Boolean isNullProduceUser);

    /**
     * 查找业绩相同数量
     *
     * @param eid
     * @param produceTime
     * @param storeId
     * @param achievementTypeId
     * @param achievementAmount
     * @return Integer
     * @author mao
     * @date 2021/6/18 10:58
     */
    Integer getAchievementSame(String eid, String produceTime, String storeId, Long achievementTypeId,
                               BigDecimal achievementAmount);

    /**
     * 获取门店某个月的业绩
     *
     * @param eid
     * @param storeIds
     * @param beginTime
     * @param endTime
     * @return java.util.List<com.coolcollege.intelligent.model.achievement.entity.AchievementDetailDO>
     * @author chenyupeng
     * @date 2021/10/27
     */
    List<AchievementStoreAmountDTO> getAmountByStores(@Param("eid") String eid, @Param("storeIds") List<String> storeIds,
                                                      @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    List<AchievementStoreAmountDTO> listGroupByUser(@Param("eid") String eid, @Param("storeIds") List<String> storeIds,
                                                    @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    AchievementTotalAmountDTO getAchievementTotalAmount(@Param("eid") String eid,@Param("storeIds") List<String> storeIds,@Param("regionPath") String regionPath,
                                                        @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    List<AchieveCountDTO> salesProfile(@Param("eid") String enterpriseId,
                                       @Param("mainClass") String mainClass);

    List<AchieveMonthTop5Response> getNumByFormwork(@Param("enterpriseId") String enterpriseId,
                                                    @Param("startTime") Date startTime,
                                                    @Param("endTime") Date endTime,
                                                    @Param("mainClass") String mainClass);

    List<AchieveMonthMiddleTop5Response> monthMiddleTop5(@Param("enterpriseId") String enterpriseId,
                                                         @Param("categoryId") String categoryId,
                                                         @Param("startTime") Date startTime,
                                                         @Param("endTime") Date endTime,
                                                         @Param("mainClass") String mainClass);

    List<RegionTop5Response> regionTop5(@Param("enterpriseId") String enterpriseId,
                                        @Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime,
                                        @Param("mainClass") String mainClass,@Param("timeType") String timeType);

    List<RegionTop5Response> storeTop5(@Param("enterpriseId") String enterpriseId,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime,
                                       @Param("mainClass") String mainClass,@Param("timeType") String timeType);

    List<NewProductDataVO> getNewProductDataList(@Param("enterpriseId")String enterpriseId,
                                                 @Param("category") String category,
                                                 @Param("middleClass")String middleClass,
                                                 @Param("type")String type);

    List<TOPTenVO> getTopTenList(@Param("enterpriseId") String enterpriseId,
                                 @Param("sortField") String sortField,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime,
                                 @Param("mainClass") String mainClass,
                                 @Param("storeId") String storeId,
                                 @Param("limit") Integer limit);

    Long getAchievementAmountByType(@Param("enterpriseId") String enterpriseId,@Param("type") String type);

    NewSalesProfileResponse getNewSalesProfile(@Param("enterpriseId") String enterpriseId,
                                                 @Param("timeType") String timeType,
                                                 @Param("mainClass") String mainClass);

    HomeSalesProfileResponse getHomeSalesProfile(@Param("enterpriseId") String eid);

    RegionReportVO getRegionProductData(@Param("enterpriseId") String enterpriseId, @Param("mainClass") String mainClass, @Param("category") String category, @Param("middleClass") String middleClass,
                                        @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("regionPath") String regionPath,
                                        @Param("storeId") String storeId);

    List<RegionReportVO> getRegionProductDataGroupList(@Param("enterpriseId") String enterpriseId, @Param("reportType") String reportType, @Param("category") String category, @Param("middleClass") String middleClass,
                                        @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("regionPath") String regionPath
            , @Param("storeId") String storeId, @Param("type") String type);

    List<RegionReportVO> getProductTypeDataGroupList(@Param("enterpriseId") String enterpriseId,
                                                     @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("storeId") String storeId
            , @Param("goodsTypeList") List<String> goodsTypeList);

    List<RegionReportVO> getInfoGroupByCategory(@Param("enterpriseId") String enterpriseId,
                                                @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    List<AchieveGoodTypeReportDTO> queryMiddleClassInfoByCategory(@Param("enterpriseId") String enterpriseId, @Param("query") AchievementReportQuery query,@Param("beginTime") String beginTime,@Param("endTime")String endTime);

    List<PersonalAchievementVO> queryPersonalAchievement(@Param("enterpriseId") String enterpriseId, @Param("query") PersonalAchievementQuery query);

    StoreRealDataVO getStoreRealData(@Param("enterpriseId") String eid, @Param("storeId")String storeId,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate);

    List<AchieveGoodTypeReportDTO> queryPersonalTypeAchievement(@Param("enterpriseId") String enterpriseId, @Param("query") PersonalAchievementQuery query);
}
