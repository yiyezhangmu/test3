package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalesProfileResponse {

    @ApiModelProperty("本月完成率")
    private String monthRate;

    @ApiModelProperty("今日销售额")
    private String todaySales;

    @ApiModelProperty("昨日销售额")
    private String yesterdaySales;

    @ApiModelProperty("去年今日销售额")
    private String lastYearTodaySales;

    @ApiModelProperty("销售额日同比")
    private String salesDayYoy;

    @ApiModelProperty("销售额日环比")
    private String salesDayMom;

    @ApiModelProperty("本周销售额")
    private String weekSales;

    @ApiModelProperty("上周销售额")
    private String afterWeekSales;

    @ApiModelProperty("去年本周销售额")
    private String lastYearWeekSales;

    @ApiModelProperty("销售额周同比")
    private String salesWeekYoy;

    @ApiModelProperty("销售额周环比")
    private String salesWeekMom;

    @ApiModelProperty("本月销售额")
    private String monthSales;

    @ApiModelProperty("上月销售额")
    private String afterMonthSales;

    @ApiModelProperty("去年本月销售额")
    private String lastYearMonthSales;

    @ApiModelProperty("销售额月同比")
    private String salesMonthYoy;

    @ApiModelProperty("销售额月环比")
    private String salesMonthMom;

    @ApiModelProperty("本月业绩目标")
    private String monthTarget;

    @ApiModelProperty("本月计划比")
    private String monthPlanRate;

    @ApiModelProperty("本季度销售额")
    private String quarterSales;

    @ApiModelProperty("上季度销售额")
    private String afterQuarterSales;

    @ApiModelProperty("去年本季度销售额")
    private String lastYearQuarterSales;

    @ApiModelProperty("销售额季度同比")
    private String salesQuarterYoy;

    @ApiModelProperty("销售额季度环比")
    private String salesQuarterMom;

    @ApiModelProperty("本季度业绩目标")
    private String quarterTarget;

    @ApiModelProperty("本季度计划比")
    private String quarterPlanRate;

    @ApiModelProperty("本年销售额")
    private String yearSales;

    @ApiModelProperty("去年销售额")
    private String lastYearSales;

    @ApiModelProperty("销售额年同比")
    private String salesYearYoy;

    @ApiModelProperty("销售额年环比")
    private String salesYearMom;

    @ApiModelProperty("本年业绩目标")
    private String yearTarget;

    @ApiModelProperty("本年计划比")
    private String yearPlanRate;




    @ApiModelProperty("今日销售台数")
    private Integer todaySalesNum;

    @ApiModelProperty("昨日销售台数")
    private Integer yesterdaySalesNum;

    @ApiModelProperty("去年今日销售台数")
    private Integer lastYearTodaySalesNum;

    @ApiModelProperty("销售台数日同比")
    private String salesNumDayYoy;

    @ApiModelProperty("销售台数日环比")
    private String salesNumDayMom;

//    @ApiModelProperty("本周销售台数")
//    private Integer weekSalesNum;
//
//    @ApiModelProperty("上周销售台数")
//    private Integer afterWeekSalesNum;
//
//    @ApiModelProperty("去年本周销售台数")
//    private Integer lastYearWeekSalesNum;
//
//    @ApiModelProperty("销售台数周同比")
//    private Integer salesNumWeekYoy;
//
//    @ApiModelProperty("销售台数周环比")
//    private Integer salesNumWeekMom;
//
//    @ApiModelProperty("本月销售台数")
//    private Integer monthSalesNum;
//
//    @ApiModelProperty("上月销售台数")
//    private Integer lastMonthSalesNum;
//
//    @ApiModelProperty("去年本月销售台数")
//    private Integer lastYearMonthSalesNum;
//
//    @ApiModelProperty("销售台数月同比")
//    private String salesNumMonthYoy;
//
//    @ApiModelProperty("销售台数月环比")
//    private String salesNumMonthMom;
//
//    @ApiModelProperty("本季度销售台数")
//    private Integer quarterSalesNum;
//
//    @ApiModelProperty("上季度销售台数")
//    private Integer afterQuarterSalesNum;
//
//    @ApiModelProperty("去年本季度销售台数")
//    private Integer lastYearQuarterSalesNum;
//
//    @ApiModelProperty("销售台数季度同比")
//    private String salesNumQuarterYoy;
//
//    @ApiModelProperty("销售台数季度环比")
//    private String salesNumQuarterMom;
//
//    @ApiModelProperty("本年销售台数")
//    private Integer yearSalesNum;
//
//    @ApiModelProperty("去年销售台数")
//    private Integer lastYearSalesNum;
//
//    @ApiModelProperty("销售台数年同比")
//    private String salesNumYearYoy;
//
//    @ApiModelProperty("销售台数年环比")
//    private String salesNumYearMom;

    @ApiModelProperty("今日门店数")
    private Integer todayStoreNum;

    @ApiModelProperty("昨日门店数")
    private Integer yesterdayStoreNum;

    @ApiModelProperty("去年今日门店数")
    private Integer lastYearTodayStoreNum;

    @ApiModelProperty("门店数日同比")
    private String storeNumDayYoy;

    @ApiModelProperty("门店数日环比")
    private String storeNumDayMom;

    @ApiModelProperty("今日回转率")
    private String todayConversionRate;

    @ApiModelProperty("今日回转率同比")
    private String conversionRateDayYoy;

    @ApiModelProperty("今日回转率环比")
    private String conversionRateDayMom;

//    @ApiModelProperty("本周回转率")
//    private String weekConversionRate;
//
//    @ApiModelProperty("本周回转率同比")
//    private String conversionRateWeekYoy;
//
//    @ApiModelProperty("本周回转率环比")
//    private String conversionRateWeekMom;
//
//    @ApiModelProperty("本月回转率")
//    private String monthConversionRate;
//
//    @ApiModelProperty("本月回转率同比")
//    private String conversionRateMonthYoy;
//
//    @ApiModelProperty("本月回转率环比")
//    private String conversionRateMonthMom;


}
