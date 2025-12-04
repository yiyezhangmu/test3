package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailStoreDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: StoreGroupAchieveGoalVO
 * @Description:门店群业绩目标
 * @date 2023-03-31 14:25
 */
@Data
public class StoreGroupAchieveGoalVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店月目标")
    private BigDecimal monthGoalAmt;

    @ApiModelProperty("待分配")
    private BigDecimal waitAssignedGoalAmt;

    @ApiModelProperty("月完成率")
    private BigDecimal monthSalesRate;

    @ApiModelProperty("是否存在业绩目标 true存在  false不存在")
    private Boolean isExistSaleGoal;

    @ApiModelProperty("每日的门店业绩")
    public List<StoreUserAchieveDayGoalVO> dayAchieveList;

    public static StoreGroupAchieveGoalVO convert(String storeId, AchieveQyyDetailStoreDO storeAchieve, List<String> daysOfMonth, List<AchieveQyyDetailStoreDO> storeDaysAchieveList){
        StoreGroupAchieveGoalVO result = new StoreGroupAchieveGoalVO();
        result.setStoreId(storeId);
        result.setIsExistSaleGoal(false);
        if(Objects.nonNull(storeAchieve)){
            result.setIsExistSaleGoal(true);
            result.setMonthGoalAmt(storeAchieve.getGoalAmt());
            if(Objects.isNull(storeAchieve.getAssignedGoalAmt())){
                storeAchieve.setAssignedGoalAmt(new BigDecimal(Constants.ZERO_STR));
            }
            if(Objects.nonNull(storeAchieve.getGoalAmt()) && Objects.nonNull(storeAchieve.getAssignedGoalAmt())){
                result.setWaitAssignedGoalAmt(storeAchieve.getGoalAmt().subtract(storeAchieve.getAssignedGoalAmt()));
            }
            result.setMonthSalesRate(storeAchieve.getSalesRate());
        }
        List<StoreUserAchieveDayGoalVO> dayAchieveList = new ArrayList<>();
        Map<String, AchieveQyyDetailStoreDO> daySaleMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeDaysAchieveList)){
            daySaleMap = storeDaysAchieveList.stream().collect(Collectors.toMap(k -> k.getTimeValue(), Function.identity()));
        }
        for (String day : daysOfMonth) {
            LocalDate parse = LocalDate.parse(day);
            LocalDate now = LocalDate.now();
            Boolean isCanModify = false;
            Integer monthDiff = (parse.getYear() - now.getYear()) * Constants.TWELVE + parse.getMonthValue() - now.getMonthValue();
            //当前日期之后的可以修改， 当前月6号之前的可以修改
            if(!parse.isBefore(now) || (monthDiff == 0 && parse.getDayOfMonth() < 6 && now.getDayOfMonth() < 6)){
                isCanModify = true;
            }
            StoreUserAchieveDayGoalVO dayGoal = new StoreUserAchieveDayGoalVO();
            dayGoal.setSalesDt(day);
            dayGoal.setIsCanModify(isCanModify);
            AchieveQyyDetailStoreDO achieveQyyDetailStore = daySaleMap.get(day);
            if(Objects.isNull(achieveQyyDetailStore)){
                dayGoal.setIsExistSaleGoal(false);
            }else{
                dayGoal.setIsExistSaleGoal(true);
                dayGoal.setGoalAmt(achieveQyyDetailStore.getGoalAmt());
                dayGoal.setSalesAmt(achieveQyyDetailStore.getSalesAmt());
            }
            dayAchieveList.add(dayGoal);
        }
        result.setDayAchieveList(dayAchieveList);
        return result;
    }

}
