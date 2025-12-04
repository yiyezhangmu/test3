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
 * @FileName: UserMonthAchieveGoalVO
 * @Description: 用户月目标
 * @date 2023-04-03 14:31
 */
@Data
public class UserMonthAchieveGoalVO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("是否离职")
    private Boolean isLeave;

    @ApiModelProperty("月目标金额")
    private BigDecimal monthGoalAmt;

    @ApiModelProperty("月业绩占比")
    private BigDecimal monthGoalRate;

    @ApiModelProperty("每日业绩情况")
    public List<StoreUserDayAchieveGoal> dayAchieveList;

    @Data
    public static class StoreUserDayAchieveGoal{

        @ApiModelProperty("是否存在业绩目标 true存在  false不存在")
        private Boolean isExistSaleGoal;

        @ApiModelProperty("是否支持修改")
        private Boolean isCanModify;

        @ApiModelProperty("数据日期 yyyy-MM-dd")
        private String salesDt;

        @ApiModelProperty("目标金额")
        private BigDecimal goalAmt;

        @ApiModelProperty("完成业绩")
        private BigDecimal salesAmt;

        @ApiModelProperty("目标占比")
        private BigDecimal goalRate;
    }


    public static UserMonthAchieveGoalVO convert(String storeId, EnterpriseUserDO user, List<AchieveQyyDetailUserDO> userDayAchieve, AchieveQyyDetailStoreDO storeAchieve, List<String> daysOfMonth){
        UserMonthAchieveGoalVO result = new UserMonthAchieveGoalVO();
        result.setStoreId(storeId);
        result.setUserId(user.getUserId());
        result.setUsername(user.getName());
        result.setAvatar(user.getAvatar());
        result.setIsLeave(!user.getActive());
        List<StoreUserDayAchieveGoal> dayAchieveList = new ArrayList<>();
        Map<String, AchieveQyyDetailUserDO> dayAchieveMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(userDayAchieve)){
            dayAchieveMap = userDayAchieve.stream().collect(Collectors.toMap(k -> k.getTimeValue(), Function.identity()));
        }
        LocalDate now = LocalDate.now();
        BigDecimal monthStoreGoal = Optional.ofNullable(storeAchieve).map(AchieveQyyDetailStoreDO::getGoalAmt).orElse(null);
        BigDecimal monthGoalAmt = new BigDecimal(Constants.ZERO_STR);
        BigDecimal monthGoalRate = new BigDecimal(Constants.ZERO_STR);
        for (String day : daysOfMonth) {
            StoreUserDayAchieveGoal dayAchieveGoal = new StoreUserDayAchieveGoal();
            AchieveQyyDetailUserDO achieveDetailUser = dayAchieveMap.get(day);
            dayAchieveGoal.setIsExistSaleGoal(false);
            LocalDate parse = LocalDate.parse(day);
            Boolean isCanModify = false;
            //当前日期之后的可以修改， 当前月6号之前的可以修改
            Integer monthDiff = (parse.getYear() - now.getYear()) * Constants.TWELVE + parse.getMonthValue() - now.getMonthValue();
            //当前日期之后的可以修改， 当前月6号之前的可以修改
            if(!parse.isBefore(now) || (monthDiff == 0 && parse.getDayOfMonth() < 6 && now.getDayOfMonth() < 6)){
                isCanModify = true;
            }
            dayAchieveGoal.setIsCanModify(isCanModify);
            dayAchieveGoal.setSalesDt(day);
            if(Objects.nonNull(achieveDetailUser)){
                if (Objects.nonNull(achieveDetailUser.getGoalAmt())){
                    monthGoalAmt = monthGoalAmt.add(achieveDetailUser.getGoalAmt());
                    dayAchieveGoal.setGoalAmt(achieveDetailUser.getGoalAmt());
                }
               if (Objects.nonNull(achieveDetailUser.getSalesAmt())){
                   dayAchieveGoal.setSalesAmt(achieveDetailUser.getSalesAmt());
               }
                dayAchieveGoal.setIsExistSaleGoal(true);
                dayAchieveGoal.setGoalRate(null);
                if(Objects.nonNull(monthStoreGoal) && Objects.nonNull(achieveDetailUser.getGoalAmt())){
                    BigDecimal goalRate = achieveDetailUser.getGoalAmt().divide(monthStoreGoal, Constants.INDEX_FOUR, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(Constants.INDEX_TWO, BigDecimal.ROUND_HALF_UP);
                    dayAchieveGoal.setGoalRate(goalRate);
                }
            }
            dayAchieveList.add(dayAchieveGoal);
        }
        if(Objects.nonNull(monthStoreGoal) && Objects.nonNull(monthGoalAmt)){
            monthGoalRate = monthGoalAmt.divide(monthStoreGoal, Constants.INDEX_FOUR, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(Constants.INDEX_TWO, BigDecimal.ROUND_HALF_UP);
        }
        result.setMonthGoalAmt(monthGoalAmt);
        result.setMonthGoalRate(monthGoalRate);
        result.setDayAchieveList(dayAchieveList);
        return result;
    }

}
