package com.coolcollege.intelligent.mapper.achieve;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.dao.qyy.AchieveQyyDetailStoreMapper;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailStoreDO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: AchieveQyyDetailStoreDAO
 * @Description:
 * @date 2023-03-31 14:20
 */
@Service
@Slf4j
public class AchieveQyyDetailStoreDAO {

    @Resource
    private AchieveQyyDetailStoreMapper achieveQyyDetailStoreMapper;

    /**
     * 根据时间获取门店业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValue
     * @return
     */
    public AchieveQyyDetailStoreDO getStoreAchieveByTime(String enterpriseId, String storeId, TimeCycleEnum timeType, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, storeId, timeValue) || Objects.isNull(timeType)){
            return null;
        }
        return achieveQyyDetailStoreMapper.getStoreAchieveByTime(enterpriseId, storeId, timeType.getCode(), timeValue);
    }

    /**
     * 获取多个时间段的业绩
     * @param enterpriseId
     * @param storeId
     * @param timeType
     * @param timeValues
     * @return
     */
    public List<AchieveQyyDetailStoreDO> getStoreAchieveListByTime(String enterpriseId, String storeId, TimeCycleEnum timeType, List<String> timeValues){
        if(StringUtils.isAnyBlank(enterpriseId, storeId) || CollectionUtils.isEmpty(timeValues) || Objects.isNull(timeType)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailStoreMapper.getStoreAchieveListByTime(enterpriseId, storeId, timeType.getCode(), timeValues);
    }

    /**
     * 批量插入或者更新门店业绩
     * @param enterpriseId
     * @param insertOrUpdateList
     * @return
     */
    public int batchInsertOrUpdateStoreGoal(String enterpriseId, List<AchieveQyyDetailStoreDO> insertOrUpdateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertOrUpdateList)){
            return -1;
        }
        return achieveQyyDetailStoreMapper.batchInsertOrUpdateStoreGoal(insertOrUpdateList, enterpriseId);
    }

    /**
     * 更新完成率
     * @param enterpriseId
     * @param insertOrUpdateList
     * @return
     */
    public int batchInsertOrUpdateStoreSales(String enterpriseId, List<AchieveQyyDetailStoreDO> insertOrUpdateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertOrUpdateList)){
            return -1;
        }
        return achieveQyyDetailStoreMapper.batchInsertOrUpdateStoreSales(insertOrUpdateList, enterpriseId);
    }

    /**
     * 更新门店业绩
     * @param enterpriseId
     * @param record
     * @return
     */
    public int updateByPrimaryKeySelective(String enterpriseId, AchieveQyyDetailStoreDO record){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(record)){
            return -1;
        }
        return achieveQyyDetailStoreMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public List<AchieveQyyDetailStoreDO> getStoreAchieveListByStoreIds(String enterpriseId, List<String> storeIds, TimeCycleEnum timeType, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || CollectionUtils.isEmpty(storeIds) || Objects.isNull(timeType)){
            return Lists.newArrayList();
        }
        return achieveQyyDetailStoreMapper.getStoreAchieveListByStoreIds(enterpriseId, storeIds, timeType.getCode(), timeValue);
    }


    public BigDecimal getSalesGoalAmtSum(String enterpriseId, String storeId, TimeCycleEnum timeType, List<String> timeValues){
        if(StringUtils.isAnyBlank(enterpriseId, storeId) || CollectionUtils.isEmpty(timeValues) || Objects.isNull(timeType)){
            return null;
        }
        return achieveQyyDetailStoreMapper.getSalesGoalAmtSum(enterpriseId, storeId, timeType.getCode(), timeValues);
    }

    public List<AchieveQyyDetailStoreDO> getStoreByRegionId(String enterpriseId, String regionId) {
        if(StringUtils.isAnyBlank(enterpriseId, regionId)){
            return null;
        }
        String timeType = Constants.DAY;
        String timeValue = LocalDate.now().toString();
        return achieveQyyDetailStoreMapper.getStoreByRegionId(enterpriseId,regionId,timeType,timeValue);
    }

    public BigDecimal countRegionAmt(String enterpriseId, String hqRegionId) {
        String timeType = Constants.DAY;
        String timeValue = LocalDate.now().toString();
        return achieveQyyDetailStoreMapper.countRegionAmt(enterpriseId,hqRegionId,timeType,timeValue);
    }

    public List<AchieveQyyDetailStoreDO> getstoreAchieveListByOneWeek(String enterpriseId, List<String> storeId, String timeValue, String sundayOfWeek, TimeCycleEnum day) {
        String code = day.getCode();
        return achieveQyyDetailStoreMapper.getstoreAchieveListByOneWeek(enterpriseId,storeId,timeValue,sundayOfWeek,code);
    }
}
