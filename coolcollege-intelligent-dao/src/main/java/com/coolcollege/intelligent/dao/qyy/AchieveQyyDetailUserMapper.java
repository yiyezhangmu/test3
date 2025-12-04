package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.UserSalesDTO;
import com.coolcollege.intelligent.model.achievement.qyy.message.UserSalesGoalDTO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import com.coolcollege.intelligent.model.qyy.QyyGoalDO;
import com.coolcollege.intelligent.model.qyy.QyyNewspaperAchieveDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @date 2023-03-29 04:02
 */
public interface AchieveQyyDetailUserMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-03-29 04:02
     */
    int batchInsertSelective(@Param("insertList") List<AchieveQyyDetailUserDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     *批量插入或者更新用户目标
     * @param insertList
     * @param enterpriseId
     * @return
     */
    int batchInsertOrUpdateUserGoal(@Param("insertList") List<AchieveQyyDetailUserDO> insertList, @Param("enterpriseId") String enterpriseId);


    /**
     * 插入或更新用户业绩
     * @param insertList
     * @param enterpriseId
     * @return
     */
    int batchInsertOrUpdateUserSales(@Param("insertList") List<AchieveQyyDetailUserDO> insertList, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-03-29 04:02
     */
    int updateByPrimaryKeySelective(AchieveQyyDetailUserDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取多个时间的数据
     * @param enterpriseId
     * @param storeId
     * @param userId
     * @param timeType
     * @param timeValues
     * @return
     */
    List<AchieveQyyDetailUserDO> getStoreAchieveByUserAndTime(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("userId")String userId,
                                                       @Param("timeType")String timeType, @Param("timeValues")List<String> timeValues);


    /**
     * 获取门店的员工业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValue
     * @return
     */
    List<AchieveQyyDetailUserDO> getStoreAchievesByTime(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId,
                                                       @Param("timeType")String timeType, @Param("timeValue")String timeValue);


    /**
     * 获取用户某个时间的业绩
     * @param enterpriseId
     * @param storeId
     * @param userId
     * @param timeType
     * @param timeValue
     * @return
     */
    AchieveQyyDetailUserDO getUserStoreAchieveByTime(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("userId")String userId,
                                                       @Param("timeType")String timeType, @Param("timeValue")String timeValue);


    /**
     * 获取每天的门店业绩目标总和
     * @param enterpriseId
     * @param storeId
     * @param timeValues
     * @return
     */
    List<AchieveQyyDetailUserDO> getStoreMGoalSumGroupByDay(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("timeValues")List<String> timeValues);

    /**
     * 获取实际完成业绩排行
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValue
     * @return
     */
    List<AchieveQyyDetailUserDO> getDaySalesAmtRank(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("timeType")String timeType, @Param("timeValue")String timeValue);

    /**
     * 用户月完成求和
     * @param enterpriseId
     * @param storeId
     * @param userIds
     * @param timeValues
     * @return
     */
    List<AchieveQyyDetailUserDO> getUserMSalesAmtGroupByUserId(@Param("enterpriseId") String enterpriseId, @Param("storeId")String storeId, @Param("userIds")List<String> userIds, @Param("timeValues")List<String> timeValues);

    /**
     * 更新用户日数据完成率
     * @param enterpriseId
     * @param storeIds
     * @param days
     * @return
     */
    int updateUserSalesRate(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds, @Param("days") List<String> days);

    /**
     * 获取今日目标
     * @param enterpriseId
     * @param timeValue
     * @return
     */
    List<AchieveQyyDetailUserDO> getTodayStoreUserGoal(@Param("enterpriseId") String enterpriseId, @Param("timeValue") String timeValue);

    /**
     *
     * @param enterpriseId
     * @param date
     * @param userId
     * @param storeId
     * @return
     */
    List<AchieveQyyDetailUserDO> pullUserSales(@Param("enterpriseId") String enterpriseId, @Param("date") String date, @Param("userId") String userId, @Param("storeId") String storeId);

    /**
     * 获取用户业绩
     * @param enterpriseId
     * @param userIds
     * @param months
     * @return
     */
    List<AchieveQyyDetailUserDO> getUserSalesAmtByUserIdsAndTimes(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds, @Param("months") List<String> months);

    void batchInsertOrUpdate(@Param("enterpriseId") String enterpriseId,
                             @Param("userGoalList")List<UserSalesDTO.StoreGoal> userGoalList,
                             @Param("mth")String mth,
                             @Param("code")String code);

    List<AchieveQyyDetailUserDO> getDetailUserByTypeAndValue(@Param("enterpriseId") String enterpriseId,
                                                       @Param("timeType") String timeType,
                                                       @Param("timeValue") String timeValue);

    List<QyyNewspaperAchieveDO.UserGoalByWeek> getUserGoalByWeeks(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("storeDOId") String storeDOId,
                                                                  @Param("startTime")LocalDate startTime,
                                                                  @Param("endTime")LocalDate endTime );

    List<QyyGoalDO> getUserGoalByMonth(@Param("enterpriseId") String enterpriseId,
                                       @Param("userIdList") List<String> userIdList,
                                       @Param("storeDOId") String storeDOId,
                                       @Param("period") String period);

    List<AchieveQyyDetailUserDO> getGoalAmtByMonth(@Param("enterpriseId") String enterpriseId,
                                                   @Param("userIds") List<String> userIds,
                                                   @Param("firstDay") LocalDate firstDay,
                                                   @Param("currentTime") LocalDate currentTime);
}