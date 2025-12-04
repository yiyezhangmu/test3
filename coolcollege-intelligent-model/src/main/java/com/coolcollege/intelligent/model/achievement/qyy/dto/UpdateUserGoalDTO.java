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
public class UpdateUserGoalDTO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("人员id")
    private String userId;

    @ApiModelProperty("每日目标列表")
    public List<UserDateGoal> userGoalList;

    @Data
    public static class UserDateGoal{

        @ApiModelProperty("数据日期 yyyy-MM-dd")
        private String salesDt;

        @ApiModelProperty("目标金额")
        private BigDecimal goalAmt;

    }

}
