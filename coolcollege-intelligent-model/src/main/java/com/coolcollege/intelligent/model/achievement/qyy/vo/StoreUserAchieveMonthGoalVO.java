package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailStoreDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: StoreUserAchieveGoalVO
 * @Description: 用户业绩目标
 * @date 2023-03-31 14:47
 */
@Data
public class StoreUserAchieveMonthGoalVO {

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("是否离职")
    private Boolean isLeave;

    @ApiModelProperty("目标金额")
    private BigDecimal goalAmt;

    @ApiModelProperty("目标占比")
    private BigDecimal goalRate;

    public static List<StoreUserAchieveMonthGoalVO> convert(List<AchieveQyyDetailUserDO> storeAchieveUserList, AchieveQyyDetailStoreDO storeAchieve, List<EnterpriseUserDO> userList) {
        if(CollectionUtils.isEmpty(userList)){
            return Lists.newArrayList();
        }
        Map<String, AchieveQyyDetailUserDO> userAchieveMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(storeAchieveUserList)){
            userAchieveMap = storeAchieveUserList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        }
        List<StoreUserAchieveMonthGoalVO> resultList = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUser : userList) {
            AchieveQyyDetailUserDO achieveUser = userAchieveMap.get(enterpriseUser.getUserId());
            StoreUserAchieveMonthGoalVO result = new StoreUserAchieveMonthGoalVO();
            result.setUserId(enterpriseUser.getUserId());
            result.setUsername(enterpriseUser.getName());
            result.setAvatar(enterpriseUser.getAvatar());
            result.setIsLeave(!enterpriseUser.getActive());
            if(Objects.nonNull(achieveUser)){
                result.setGoalAmt(achieveUser.getGoalAmt());
                if(Objects.nonNull(achieveUser.getGoalAmt()) && Objects.nonNull(storeAchieve.getGoalAmt())){
                    BigDecimal goalRate = achieveUser.getGoalAmt().divide(storeAchieve.getGoalAmt(), Constants.INDEX_FOUR, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(Constants.INDEX_TWO, BigDecimal.ROUND_HALF_UP);
                    result.setGoalRate(goalRate);
                }
            }
            resultList.add(result);
        }
        return resultList;
    }
}
