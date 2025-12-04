package com.coolcollege.intelligent.mapper.achieve;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.dao.qyy.AchieveQyyDetailUserMapper;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.UserSalesDTO;
import com.coolcollege.intelligent.model.achievement.qyy.message.UserSalesGoalDTO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import com.coolcollege.intelligent.model.qyy.QyyGoalDO;
import com.coolcollege.intelligent.model.qyy.QyyNewspaperAchieveDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: AchieveQyyDetailUserDAO
 * @Description:
 * @date 2023-03-31 11:30
 */
@Service
@Slf4j
public class AchieveQyyDetailUserDAO {

    @Resource
    private AchieveQyyDetailUserMapper achieveQyyDetailUserMapper;

    /**
     * 获取门店某个员工的所有业绩
     * @param enterpriseId
     * @param storeId
     * @param userId
     * @param timeType
     * @param timeValues
     * @return
     */
    public List<AchieveQyyDetailUserDO> getStoreAchieveByUserAndTime(String enterpriseId, String storeId, String userId, TimeCycleEnum timeType, List<String> timeValues){
        if(StringUtils.isAnyBlank(enterpriseId, storeId, userId) || CollectionUtils.isEmpty(timeValues) || Objects.isNull(timeType)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getStoreAchieveByUserAndTime(enterpriseId, storeId, userId, timeType.getCode(), timeValues);
    }


    /**
     * 获取门店的所有员工业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValue
     * @return
     */
    public List<AchieveQyyDetailUserDO> getStoreAchievesByTime(String enterpriseId, String storeId, TimeCycleEnum timeType, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, storeId, timeValue) || Objects.isNull(timeType)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getStoreAchievesByTime(enterpriseId, storeId, timeType.getCode(), timeValue);
    }

    /**
     * 获取某个人某个时间的业绩
     * @param enterpriseId
     * @param storeId
     * @param userId
     * @param timeType
     * @param timeValue
     * @return
     */
    public AchieveQyyDetailUserDO getUserStoreAchieveByTime(String enterpriseId, String storeId, String userId, TimeCycleEnum timeType, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, storeId, userId, timeValue)  || Objects.isNull(timeType)){
            return null;
        }
        return achieveQyyDetailUserMapper.getUserStoreAchieveByTime(enterpriseId, storeId, userId, timeType.getCode(), timeValue);
    }

    /**
     * 插入或者更新用户目标
     * @param enterpriseId
     * @param insertList
     * @return
     */
    public int batchInsertOrUpdateUserGoal(String enterpriseId, List<AchieveQyyDetailUserDO> insertList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertList)){
            return -1;
        }
        return achieveQyyDetailUserMapper.batchInsertOrUpdateUserGoal(insertList, enterpriseId);
    }

    /**
     * 插入或更新用户销售数据
     * @param enterpriseId
     * @param insertList
     * @return
     */
    public int batchInsertOrUpdateUserSales(String enterpriseId, List<AchieveQyyDetailUserDO> insertList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertList)){
            return -1;
        }
        return achieveQyyDetailUserMapper.batchInsertOrUpdateUserSales(insertList, enterpriseId);
    }

    /**
     * 按天获取门店目标
     * @param enterpriseId
     * @param storeId
     * @param timeValues
     * @return
     */
    public Map<String, BigDecimal> getStoreMGoalSumGroupByDay(String enterpriseId, String storeId, List<String> timeValues){
        if(StringUtils.isAnyBlank(enterpriseId, storeId) || CollectionUtils.isEmpty(timeValues)){
            return Maps.newHashMap();
        }
        List<AchieveQyyDetailUserDO> storeGoalSumGroupByStore = achieveQyyDetailUserMapper.getStoreMGoalSumGroupByDay(enterpriseId, storeId, timeValues);
        if(CollectionUtils.isEmpty(storeGoalSumGroupByStore)){
            return Maps.newHashMap();
        }
        return storeGoalSumGroupByStore
                .stream()
                .filter(o->Objects.nonNull(o.getTimeValue()))
                .filter(o->Objects.nonNull(o.getGoalAmt()))
                .collect(Collectors.toMap(k->k.getTimeValue(), v->v.getGoalAmt()));
    }

    /**
     * 获取排行
     * @param enterpriseId
     * @param storeId
     * @param day
     * @return
     */
    public List<AchieveQyyDetailUserDO> getDaySalesAmtRank(String enterpriseId, String storeId, String day){
        if(StringUtils.isAnyBlank(enterpriseId, storeId, day)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getDaySalesAmtRank(enterpriseId, storeId, TimeCycleEnum.DAY.getCode(), day);
    }

    /**
     * 获取用户月完成综合 及目标总和
     * @param enterpriseId
     * @param storeId
     * @param userIds
     * @param daysOfMonth
     * @return
     */
    public List<AchieveQyyDetailUserDO> getUserMSalesAmtGroupByUserId(String enterpriseId, String storeId, List<String> userIds, List<String> daysOfMonth){
        if(StringUtils.isAnyBlank(enterpriseId, storeId) || CollectionUtils.isEmpty(daysOfMonth)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getUserMSalesAmtGroupByUserId(enterpriseId, storeId, userIds, daysOfMonth);
    }

    /**
     * 更新日的完成率
     * @param enterpriseId
     * @param storeIds
     * @param days
     */
    public void updateUserSalesRate(String enterpriseId, List<String> storeIds, List<String> days){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeIds) || CollectionUtils.isEmpty(days)){
            return;
        }
        achieveQyyDetailUserMapper.updateUserSalesRate(enterpriseId, storeIds, days);
    }

    /**
     * 获取今天的员工目标
     * @param enterpriseId
     * @param timeValue
     * @return
     */
    public List<AchieveQyyDetailUserDO> getTodayStoreUserGoal(String enterpriseId, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getTodayStoreUserGoal(enterpriseId, timeValue);
    }

    /**
     * 拉取员工目标
     * @param enterpriseId
     * @param day
     * @param userId
     * @param storeId
     * @return
     */
    public List<AchieveQyyDetailUserDO> pullUserSales(String enterpriseId, String day, String userId, String storeId){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.pullUserSales(enterpriseId, day, userId, storeId);
    }

    /**
     * 获取用户业绩
     * @param enterpriseId
     * @param userIds
     * @param months
     * @return
     */
    public List<AchieveQyyDetailUserDO> getUserSalesAmtByUserIdsAndTimes(String enterpriseId, List<String> userIds, List<String> months){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getUserSalesAmtByUserIdsAndTimes(enterpriseId, userIds, months);
    }

    public void batchInsertOrUpdate(String enterpriseId, List<UserSalesDTO.StoreGoal> userGoalList, String mth, String code) {
        if (StringUtils.isBlank(enterpriseId)){
            log.info("batchInsertOrUpdate企业id为空");
            return;
        }
        achieveQyyDetailUserMapper.batchInsertOrUpdate(enterpriseId,userGoalList,mth,code);
    }

    public List<AchieveQyyDetailUserDO> getDetailUserByTypeAndValue(String enterpriseId, String timeType, String timeValue) {
        if (StringUtils.isBlank(enterpriseId)){
            log.error("getDetailUserByTypeAndValue企业id为空");
            return Lists.newArrayList();
        }
        if (StringUtils.isBlank(timeType) && StringUtils.isBlank(timeValue)){
            log.error("timeType||timeValue为空：{}，{}",timeType,timeValue);
            return Lists.newArrayList();
        }
        return achieveQyyDetailUserMapper.getDetailUserByTypeAndValue(enterpriseId,timeType,timeValue);
    }

    public List<QyyNewspaperAchieveDO.UserGoalByWeek> getUserGoalByWeeks(String enterpriseId, String storeDOId,LocalDate startTime,LocalDate endTime ) {
        return achieveQyyDetailUserMapper.getUserGoalByWeeks(enterpriseId,storeDOId,startTime,endTime);
    }

    public List<QyyGoalDO> getUserGoalByMonth(String enterpriseId, List<String> userIdList, String storeDOId, String period) {
        return achieveQyyDetailUserMapper.getUserGoalByMonth(enterpriseId,userIdList,storeDOId,period);
    }

    public List<AchieveQyyDetailUserDO> getGoalAmtByMonth(String enterpriseId, List<String> userIds, LocalDate firstDay, LocalDate currentTime) {
        return achieveQyyDetailUserMapper.getGoalAmtByMonth(enterpriseId,userIds,firstDay,currentTime);
    }
}
