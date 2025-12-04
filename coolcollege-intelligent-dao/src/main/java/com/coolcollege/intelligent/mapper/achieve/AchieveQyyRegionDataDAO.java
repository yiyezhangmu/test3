package com.coolcollege.intelligent.mapper.achieve;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.dao.qyy.AchieveQyyRegionDataMapper;
import com.coolcollege.intelligent.model.achievement.qyy.vo.StoreBillingRankVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.TimeValue;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: AchieveQyyRegionDataDAO
 * @Description:
 * @date 2023-03-31 14:20
 */
@Service
@Slf4j
public class AchieveQyyRegionDataDAO {

    @Resource
    private AchieveQyyRegionDataMapper achieveQyyRegionDataMapper;

    /**
     * 批量插入或者更新
     * @param enterpriseId
     * @param insertOrUpdateList
     */
    public void batchInsertSelective(String enterpriseId, List<AchieveQyyRegionDataDO> insertOrUpdateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertOrUpdateList)){
            return;
        }
        achieveQyyRegionDataMapper.batchInsertSelective(insertOrUpdateList, enterpriseId);
    }

    /**
     * 获取开单排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getBillingRank(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycle, String timeValue,String storeStatus){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
        if ("open".equals(storeStatus)){
            return achieveQyyRegionDataMapper.getBillingRankOfOpen(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
        }else if ("close".equals(storeStatus)){
            return achieveQyyRegionDataMapper.getBillingRankOfClose(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
        }else {
            return achieveQyyRegionDataMapper.getBillingRank(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
        }
    }

    /**
     * 获取某个区域某一个时间的数据
     * @param enterpriseId
     * @param regionId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public AchieveQyyRegionDataDO getRegionDataByRegionIdAndTime(String enterpriseId, Long regionId , TimeCycleEnum timeCycle, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || Objects.isNull(regionId)){
            return null;
        }
        return achieveQyyRegionDataMapper.getRegionDataByRegionIdAndTime(enterpriseId, regionId, timeCycle.getCode(), timeValue);
    }

    public AchieveQyyRegionDataDO getRegionDataByRegionIdAndTimeByType(String enterpriseId, Long regionId, TimeCycleEnum timeCycle, String timeValue) {
        if(StringUtils.isAnyBlank(enterpriseId) || Objects.isNull(timeCycle) || Objects.isNull(regionId)){
            return null;
        }
        return achieveQyyRegionDataMapper.getRegionDataByRegionIdAndTimeTimeType(enterpriseId, regionId, timeCycle.getCode(), timeValue);
    }

    /**
     * 业绩排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getSalesRank(String enterpriseId,
                                                     List<Long> regionIds,
                                                     TimeCycleEnum timeCycle,
                                                     String timeValue,
                                                     boolean tag){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getSalesRank(enterpriseId, regionIds, timeCycle.getCode(), timeValue,tag);
    }

    /**
     * 获取分公司业绩排行
     * @param enterpriseId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getCompSalesRank(String enterpriseId, TimeCycleEnum timeCycle, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getCompSalesRank(enterpriseId, timeCycle.getCode(), timeValue);
    }

    /**
     * 完成率排行
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getFinishRateRank(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycle, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getFinishRateRank(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
    }

    /**
     * 分子公司的业绩排行
     * @param enterpriseId
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getCompFinishRateRank(String enterpriseId, TimeCycleEnum timeCycle, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getCompFinishRateRank(enterpriseId, timeCycle.getCode(), timeValue);
    }


    public Map<String, BigDecimal> getRegionSalesRateMap(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycleEnum, List<String> timeValues){
        List<AchieveQyyRegionDataDO> regionDataList = achieveQyyRegionDataMapper.getRegionDataByRegionIdsAndTimes(enterpriseId, regionIds, timeCycleEnum.getCode(), timeValues);
        if(CollectionUtils.isEmpty(regionDataList)){
            return Maps.newHashMap();
        }
        return regionDataList.stream().filter(o->Objects.nonNull(o.getSalesRate())).collect(Collectors.toMap(k->k.getRegionId()+ Constants.MOSAICS + k.getTimeValue() , v->v.getSalesRate(), (k1, k2)->k1));
    }

    /**
     * 获取多个区域 某个时间的数据
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @return
     */
    public List<AchieveQyyRegionDataDO> getRegionDataList(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycle, String timeValue){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
//        if (enterpriseId.equals("25ae082b3947417ca2c835d8156a8407")){
//
//        }
        return achieveQyyRegionDataMapper.getRegionDataList(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
    }

    /**
     * 获取排名
     * @param enterpriseId
     * @param regionIds
     * @param timeCycle
     * @param timeValue
     * @param orderField
     * @return
     */
    public List<AchieveQyyRegionDataDO> getRegionDataRankLimitThree(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycle, String timeValue, String orderField){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle) || CollectionUtils.isEmpty(regionIds)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getRegionDataRankLimitThree(enterpriseId, regionIds, timeCycle.getCode(), timeValue, orderField);
    }


    /**
     * 获取总部下分子公司的排名
     * @param enterpriseId
     * @param timeCycle
     * @param timeValue
     * @param orderField
     * @return
     */
    public List<AchieveQyyRegionDataDO> getHPSubCompRankLimitThree(String enterpriseId, TimeCycleEnum timeCycle, String timeValue, String orderField){
        if(StringUtils.isAnyBlank(enterpriseId, timeValue) || Objects.isNull(timeCycle)){
            return Lists.newArrayList();
        }
        return achieveQyyRegionDataMapper.getHPSubCompRankLimitThree(enterpriseId, timeCycle.getCode(), timeValue, orderField);
    }

    public StoreBillingRankVO countOpenAndNoNum(String enterpriseId, List<Long> regionIds, TimeCycleEnum timeCycle, String timeValue) {
        return achieveQyyRegionDataMapper.countOpenAndNoNum(enterpriseId, regionIds, timeCycle.getCode(), timeValue);
    }

    public Long getBillNumByStoreId(String enterpriseId, Long storeId, TimeCycleEnum day,String dateNow) {
        return achieveQyyRegionDataMapper.getBillNumByStoreId(enterpriseId,storeId,day.getCode(),dateNow);
    }


}
