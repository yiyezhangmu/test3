package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailStoreDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-03-29 04:02
 */
public interface AchieveQyyDetailStoreMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-03-29 04:02
     */
    int batchInsertSelective(@Param("insertList") List<AchieveQyyDetailStoreDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量更新目标
     * @param insertList
     * @param enterpriseId
     * @return
     */
    int batchInsertOrUpdateStoreGoal(@Param("insertList") List<AchieveQyyDetailStoreDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     * 更新门店业绩数据
     * @param insertList
     * @param enterpriseId
     * @return
     */
    int batchInsertOrUpdateStoreSales(@Param("insertList") List<AchieveQyyDetailStoreDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-03-29 04:02
     */
    int updateByPrimaryKeySelective(AchieveQyyDetailStoreDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据时间获取门店业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValue
     * @return
     */
    AchieveQyyDetailStoreDO getStoreAchieveByTime(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("timeType")String timeType, @Param("timeValue")String timeValue);


    /**
     * 获取门店业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValues
     * @return
     */
    List<AchieveQyyDetailStoreDO> getStoreAchieveListByTime(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("timeType")String timeType, @Param("timeValues")List<String> timeValues);


    /**
     * 获取多个门店目标
     * @param enterpriseId
     * @param storeIds
     * @param timeType
     * @param timeValue
     * @return
     */
    List<AchieveQyyDetailStoreDO> getStoreAchieveListByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds")List<String> storeIds, @Param("timeType")String timeType, @Param("timeValue")String timeValue);

    /**
     * 获取目标和
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValues
     * @return
     */
    BigDecimal getSalesGoalAmtSum(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("timeType")String timeType, @Param("timeValues")List<String> timeValues);

    List<AchieveQyyDetailStoreDO> getStoreByRegionId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("regionId") String regionId,
                                                     @Param("timeType")String timeType,
                                                     @Param("timeValue")String timeValue);

    BigDecimal countRegionAmt(@Param("enterpriseId") String enterpriseId,
                              @Param("hqRegionId") String hqRegionId,
                              @Param("timeType") String timeType,
                              @Param("timeValue") String timeValue);

    List<AchieveQyyDetailStoreDO> getstoreAchieveListByOneWeek(@Param("enterpriseId") String enterpriseId,
                                                               @Param("storeId") List<String> storeId,
                                                               @Param("timeValue") String timeValue,
                                                               @Param("sundayOfWeek") String sundayOfWeek,
                                                               @Param("day") String day);
}