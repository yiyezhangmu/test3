package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: WeeklySalesVO
 * @Description: 周报业绩报告
 * @date 2023-05-05 14:59
 */
@Data
public class WeeklySalesVO {

    @ApiModelProperty("分公司排名")
    private Integer topComp;

    @ApiModelProperty("全国排名")
    private Integer topTot;

    @ApiModelProperty("销售额")
    private BigDecimal salesAmt;

    @ApiModelProperty("客单价")
    private BigDecimal cusPrice;

    @ApiModelProperty("毛利率")
    private BigDecimal profitRate;

    @ApiModelProperty("连带率")
    private BigDecimal jointRate;

    @ApiModelProperty("完成率")
    private BigDecimal salesRate;

    @ApiModelProperty("业绩目标")
    private BigDecimal goalAmt;

    public String getJointRate() {
        DecimalFormat df1 = new DecimalFormat("0.00");
        String formatJointRate = df1.format(jointRate);
        return formatJointRate;
    }

    public static WeeklySalesVO convert(AchieveQyyRegionDataDO regionData, BigDecimal goalAmt){
        if(Objects.isNull(regionData)){
            return null;
        }
        WeeklySalesVO result = new WeeklySalesVO();
        result.setTopComp(regionData.getTopComp());
        result.setTopTot(regionData.getTopTot());
        result.setSalesAmt(regionData.getSalesAmt());
        result.setCusPrice(regionData.getCusPrice());
        result.setProfitRate(regionData.getProfitRate());
        result.setJointRate(regionData.getJointRate());
        result.setSalesRate(regionData.getSalesRate());
        result.setGoalAmt(goalAmt);
        return result;
    }

}
