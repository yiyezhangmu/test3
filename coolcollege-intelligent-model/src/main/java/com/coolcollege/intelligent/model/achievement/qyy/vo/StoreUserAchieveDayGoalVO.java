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
 * @FileName: StoreUserAchieveDayGoalVO
 * @Description:
 * @date 2023-03-31 18:45
 */
@Data
public class StoreUserAchieveDayGoalVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("是否支持修改")
    private Boolean isCanModify;

    @ApiModelProperty("是否存在业绩目标 true存在  false不存在")
    private Boolean isExistSaleGoal;

    @ApiModelProperty("数据日期")
    private String salesDt;

    @ApiModelProperty("目标金额")
    private BigDecimal goalAmt;

    @ApiModelProperty("完成业绩")
    private BigDecimal salesAmt;

    @ApiModelProperty("用户业绩列表")
    public List<StoreUserAchieveGoalVO> userAchieveList;

    public static StoreUserAchieveDayGoalVO convert(String storeId, String day, AchieveQyyDetailStoreDO storeAchieve, List<AchieveQyyDetailUserDO> storeAchieveUserList, List<EnterpriseUserDO> userList){
        LocalDate parse = LocalDate.parse(day);
        LocalDate now = LocalDate.now();
        Boolean isCanModify = false;
        //当前日期之后的可以修改， 当前月6号之前的可以修改
        Integer monthDiff = (parse.getYear() - now.getYear()) * Constants.TWELVE + parse.getMonthValue() - now.getMonthValue();
        //当前日期之后的可以修改， 当前月6号之前的可以修改
        if(!parse.isBefore(now) || (monthDiff == 0 && parse.getDayOfMonth() < 6 && now.getDayOfMonth() < 6)){
            isCanModify = true;
        }
        Map<String, AchieveQyyDetailUserDO> userSaleMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeAchieveUserList)){
            userSaleMap = storeAchieveUserList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        }
        StoreUserAchieveDayGoalVO dayGoal = new StoreUserAchieveDayGoalVO();
        dayGoal.setSalesDt(day);
        dayGoal.setIsCanModify(isCanModify);
        dayGoal.setIsExistSaleGoal(false);
        dayGoal.setStoreId(storeId);
        if(Objects.nonNull(storeAchieve)){
            dayGoal.setIsExistSaleGoal(true);
            BigDecimal goalAmt = new BigDecimal(0);
            for (AchieveQyyDetailUserDO achieveQyyDetailUserDO : storeAchieveUserList) {
                if (achieveQyyDetailUserDO.getGoalAmt()!=null){
                    goalAmt = goalAmt.add(achieveQyyDetailUserDO.getGoalAmt());
                }
            }
            dayGoal.setGoalAmt(goalAmt);
            dayGoal.setSalesAmt(storeAchieve.getSalesAmt());
        }
        List<StoreUserAchieveGoalVO> userAchieveList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userList)){
            for (EnterpriseUserDO enterpriseUser : userList) {
                StoreUserAchieveGoalVO userGoal = new StoreUserAchieveGoalVO();
                userGoal.setUserId(enterpriseUser.getUserId());
                userGoal.setUsername(enterpriseUser.getName());
                userGoal.setAvatar(enterpriseUser.getAvatar());
                userGoal.setIsLeave(!enterpriseUser.getActive());
                userGoal.setIsExistSaleGoal(false);
                userGoal.setSalesDt(day);
                AchieveQyyDetailUserDO userSales = userSaleMap.get(enterpriseUser.getUserId());
                if(Objects.nonNull(userSales) && Objects.nonNull(storeAchieve)){
                    userGoal.setIsExistSaleGoal(true);
                    userGoal.setGoalAmt(userSales.getGoalAmt());
                    if(Objects.nonNull(userSales.getGoalAmt()) && Objects.nonNull(storeAchieve.getGoalAmt())){
                        BigDecimal goalRate = userSales.getGoalAmt().divide(storeAchieve.getGoalAmt(), Constants.INDEX_FOUR, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(Constants.INDEX_TWO, BigDecimal.ROUND_HALF_UP);
                        userGoal.setGoalRate(goalRate);
                    }
                }
                userAchieveList.add(userGoal);
            }
        }
        dayGoal.setUserAchieveList(userAchieveList);
        return dayGoal;
    }

}
