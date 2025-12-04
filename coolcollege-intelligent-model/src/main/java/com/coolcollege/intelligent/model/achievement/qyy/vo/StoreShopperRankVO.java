package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.qyy.AchieveQyyDetailUserDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: StoreShopperRankVO
 * @Description:门店导购排行
 * @date 2023-04-03 16:45
 */
@Data
@Slf4j
public class StoreShopperRankVO extends DataUploadVO{

    @ApiModelProperty("排名")
    private List<ShopperRank> rankList;

    @Data
    public static class ShopperRank{

        @ApiModelProperty("用户id")
        private String userId;

        @ApiModelProperty("用户名称")
        private String username;

        @ApiModelProperty("用户头像")
        private String avatar;

        @ApiModelProperty("是否离职")
        private Boolean isLeave;

        @ApiModelProperty("分公司排行")
        private Integer topComp;

        @ApiModelProperty("今日完成业绩")
        private BigDecimal todaySalesAmt;

        @ApiModelProperty("本月完成业绩")
        private BigDecimal monthSalesAmt;

        @ApiModelProperty("月完成率")
        private BigDecimal monthSalesRate;

        @ApiModelProperty("月目标")
        private BigDecimal monthGoalAmt;
    }

    public static StoreShopperRankVO convert(List<AchieveQyyDetailUserDO> daySalesAmtRank,
                                             List<AchieveQyyDetailUserDO> userMonthSalesAmt,
                                             List<EnterpriseUserDO> userList,
                                             List<AchieveQyyDetailUserDO> monthGoal){
        StoreShopperRankVO result = new StoreShopperRankVO();
        if(CollectionUtils.isEmpty(daySalesAmtRank)){
            return null;
        }
        List<ShopperRank> rankList = new ArrayList<>();
        Date etlTm = new Date();
        Map<String, EnterpriseUserDO> userMap = userList.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        Map<String, AchieveQyyDetailUserDO> userMonthSaleMap = userMonthSalesAmt.stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity(), (k1, k2) -> k1));
        for (AchieveQyyDetailUserDO achieveUser : daySalesAmtRank) {
            BigDecimal goalAmt = monthGoal.stream()
                    .filter(o -> o.getUserId().equals(achieveUser.getUserId()))
                    .filter(o -> o.getGoalAmt() != null && Objects.nonNull(o.getGoalAmt()))
                    .map(AchieveQyyDetailUserDO::getGoalAmt)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            log.info("goalAmt:{}",goalAmt);

            ShopperRank shopperRank = new ShopperRank();
            shopperRank.setUserId(achieveUser.getUserId());
            shopperRank.setMonthGoalAmt(goalAmt);
            EnterpriseUserDO userInfo = userMap.getOrDefault(achieveUser.getUserId(), new EnterpriseUserDO());
            if (Objects.isNull(userInfo)){
                continue;
            }
            AchieveQyyDetailUserDO monthSaleMap = userMonthSaleMap.getOrDefault(achieveUser.getUserId(), new AchieveQyyDetailUserDO());
            if (Objects.nonNull(userInfo)){
                if(!StringUtils.isBlank(userInfo.getName())){
                    shopperRank.setUsername(userInfo.getName());
                }
                if (!StringUtils.isBlank(userInfo.getAvatar())){
                    shopperRank.setAvatar(userInfo.getAvatar());
                }
                if (Objects.nonNull(userInfo.getActive())){
                    shopperRank.setIsLeave(!userInfo.getActive());
                }
                shopperRank.setTopComp(achieveUser.getTopComp());
                shopperRank.setTodaySalesAmt(achieveUser.getSalesAmt());
                shopperRank.setMonthSalesAmt(monthSaleMap.getSalesAmt());
                shopperRank.setMonthSalesRate(monthSaleMap.getSalesRate());
                if(Objects.nonNull(achieveUser.getEtlTm()) && etlTm.before(achieveUser.getEtlTm())){
                    etlTm = achieveUser.getEtlTm();
                }
            }
            rankList.add(shopperRank);
        }
        result.setEtlTm(DateUtil.format(etlTm, "yyyy-MM-dd HH:mm"));
        result.setRankList(rankList);
        return result;
    }

}
