package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AchieveReportDDListRes {
    //
    @ApiModelProperty("组织名称")
    private String deptName;
    //
    @ApiModelProperty("完成率")
    private BigDecimal finishRate;
    //
    @ApiModelProperty("业绩同比")
    private BigDecimal achieveYoy;
    //
    @ApiModelProperty("单产")
    private BigDecimal output;
    //
    @ApiModelProperty("单产同比")
    private BigDecimal outputYoy;

    @ApiModelProperty("销量达成率")
    private BigDecimal salesVolumeRate;

    @ApiModelProperty("客单价")
    private BigDecimal perCustomer;

    @ApiModelProperty("业绩达成率")
    private BigDecimal grossSalesRate;

    private Date updateTime;
    @ApiModelProperty("销量")
    private BigDecimal salesVolume;
}
