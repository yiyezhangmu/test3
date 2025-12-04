package com.coolcollege.intelligent.model.achievement.qyy.message;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: UserSalesGoalDTO
 * @Description: 员工个人业绩目标卡片
 * @date 2023-04-25 9:41
 */
@Data
public class UserSalesGoalDTO {

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("目标金额")
    private BigDecimal goalAmt;

    @ApiModelProperty("查看链接")
    private String viewUrl;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("门店id")
    private String storeId;



    public UserSalesGoalDTO(String username, BigDecimal goalAmt, String viewUrl) {
        this.username = username;
        this.goalAmt = goalAmt;
        this.viewUrl = viewUrl;
    }

    public UserSalesGoalDTO(String username, BigDecimal goalAmt, String viewUrl, String userId) {
        this.username = username;
        this.goalAmt = goalAmt;
        this.viewUrl = viewUrl;
        this.userId = userId;
    }

    public UserSalesGoalDTO(String username, BigDecimal goalAmt, String viewUrl, String userId, String storeId) {
        this.username = username;
        this.goalAmt = goalAmt;
        this.viewUrl = viewUrl;
        this.userId = userId;
        this.storeId = storeId;
    }

    public String getUsername() {
        return username;
    }

    public String getGoalAmt() {
        if(Objects.nonNull(goalAmt)){
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            return decimalFormat.format(goalAmt.setScale(0,BigDecimal.ROUND_HALF_UP));
        }
        return "-";
    }
}
