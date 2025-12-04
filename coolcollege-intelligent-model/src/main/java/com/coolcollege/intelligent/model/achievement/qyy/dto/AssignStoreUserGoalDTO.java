package com.coolcollege.intelligent.model.achievement.qyy.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: AssignStoreUserGoalDTO
 * @Description: 分配门店目标
 * @date 2023-04-03 10:09
 */
@Data
public class AssignStoreUserGoalDTO {

    @ApiModelProperty("数据日期 yyyy-MM-dd")
    private String salesDt;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店人员目标列表")
    public List<UserGoal> userGoalList;

    @Data
    public static class UserGoal{

        @ApiModelProperty("人员id")
        private String userId;

        @ApiModelProperty("目标金额")
        private BigDecimal goalAmt;

    }

}
