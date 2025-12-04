package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreAchieveListRes {
    //
    @ApiModelProperty("组织名称")
    private String deptName;

    //
    @ApiModelProperty("总销售额（业绩）")
    private BigDecimal grossSales;

    //
    @ApiModelProperty("销售额达成率")
    private BigDecimal grossSalesRate;

    //
    @ApiModelProperty("销量")
    private BigDecimal salesVolume;
    //
    @ApiModelProperty("销量达成率")
    private BigDecimal salesVolumeRate;

    //
    @ApiModelProperty("客单价")
    private BigDecimal perCustomer;
    //
    @ApiModelProperty("客单价达成率")
    private BigDecimal perCustomerRate;

    private Date updateTime;

}
