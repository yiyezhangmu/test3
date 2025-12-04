package com.coolcollege.intelligent.model.achievement.qyy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: StoreUserAchieveGoalVO
 * @Description: 用户业绩目标
 * @date 2023-03-31 14:47
 */
@Data
public class StoreUserAchieveGoalVO {

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("是否存在业绩目标 true存在  false不存在")
    private Boolean isExistSaleGoal;

    @ApiModelProperty("数据日期 yyyy-MM-dd")
    private String salesDt;

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

}
