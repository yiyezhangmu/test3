package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.model.achievement.qyy.vo.StoreBillingRankVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-03-29 04:02
 */
public interface AchieveQyyRegionDataMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-03-29 04:02
     */
    int batchInsertSelective(@Param("insertList") List<AchieveQyyRegionDataDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-03-29 04:02
     */
    int updateByPrimaryKeySelective(@Param("record") AchieveQyyRegionDataDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取开单排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getBillingRank(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue);
    List<AchieveQyyRegionDataDO> getBillingRankOfOpen(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue);
    List<AchieveQyyRegionDataDO> getBillingRankOfClose(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue);


    /**
     *
     * @param enterpriseId
     * @param regionId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    AchieveQyyRegionDataDO getRegionDataByRegionIdAndTime(@Param("enterpriseId")String enterpriseId, @Param("regionId") Long regionId, @Param("timeType") String timeType, @Param("timeValue") String timeValue);

    AchieveQyyRegionDataDO getRegionDataByRegionIdAndTimeTimeType(@Param("enterpriseId")String enterpriseId, @Param("regionId") Long regionId, @Param("timeType") String timeType, @Param("timeValue") String timeValue);


    /**
     * 业绩排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getSalesRank(@Param("enterpriseId")String enterpriseId,
                                              @Param("regionIds") List<Long> regionIds,
                                              @Param("timeType") String timeType,
                                              @Param("timeValue") String timeValue,
                                              @Param("tag") boolean tag);

    /**
     * 获取分公司排行
     * @param enterpriseId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getCompSalesRank(@Param("enterpriseId")String enterpriseId, @Param("timeType") String timeType, @Param("timeValue") String timeValue);


    /**
     * 完成率排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getFinishRateRank(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue);

    /**
     * 获取分公司完成率排行
     * @param enterpriseId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getCompFinishRateRank(@Param("enterpriseId")String enterpriseId, @Param("timeType") String timeType, @Param("timeValue") String timeValue);

    /**
     * 根据时间和区域获取数据
     * @param enterpriseId
     * @param regionIds
     * @param timeType
     * @param timeValues
     * @return
     */
    List<AchieveQyyRegionDataDO> getRegionDataByRegionIdsAndTimes(@Param("enterpriseId") String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValues") List<String> timeValues);

    /**
     * 批量获取区域某个时间的数据
     * @param enterpriseId
     * @param regionIds
     * @param timeType
     * @param timeValue
     * @return
     */
    List<AchieveQyyRegionDataDO> getRegionDataList(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue);

    /**
     * 获取排行 limit3
     * @param enterpriseId
     * @param regionIds
     * @param timeType
     * @param timeValue
     * @param orderField
     * @return
     */
    List<AchieveQyyRegionDataDO> getRegionDataRankLimitThree(@Param("enterpriseId")String enterpriseId, @Param("regionIds") List<Long> regionIds, @Param("timeType") String timeType, @Param("timeValue") String timeValue, @Param("orderField")String orderField);

    /**
     * 获取总部下分子公司的排行
     * @param enterpriseId
     * @param timeType
     * @param timeValue
     * @param orderField
     * @return
     */
    List<AchieveQyyRegionDataDO> getHPSubCompRankLimitThree(@Param("enterpriseId")String enterpriseId, @Param("timeType") String timeType, @Param("timeValue") String timeValue, @Param("orderField")String orderField);

    StoreBillingRankVO countOpenAndNoNum(@Param("enterpriseId")String enterpriseId,
                                         @Param("regionIds") List<Long> regionIds,
                                         @Param("timeType") String timeType,
                                         @Param("timeValue") String timeValue);


    Long getBillNumByStoreId(@Param("enterpriseId") String enterpriseId,
                             @Param("storeId") Long storeId,
                             @Param("dayCode") String code,
                             @Param("date")String dateNow);
}