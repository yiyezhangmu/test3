package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HomeSalesProfileResponse {

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



    @ApiModelProperty("今日销售台数")
    private String todaySalesNum;
    @ApiModelProperty("昨日销售台数")
    private String yesterdaySalesNum;
    @ApiModelProperty("去年今日销售台数")
    private String lastYearTodaySalesNum;
    @ApiModelProperty("销售台数日同比")
    private String salesNumDayYoy;
    @ApiModelProperty("销售台数日环比")
    private String salesNumDayMom;

    @ApiModelProperty("本周销售台数")
    private String weekSalesNum;
    @ApiModelProperty("上周销售台数")
    private String afterWeekSalesNum;
    @ApiModelProperty("去年本周销售台数")
    private String lastYearWeekSalesNum;
    @ApiModelProperty("销售台数周同比")
    private String salesNumWeekYoy;
    @ApiModelProperty("销售台数周环比")
    private String salesNumWeekMom;


    @ApiModelProperty("本月销售台数")
    private String monthSalesNum;
    @ApiModelProperty("上月销售台数")
    private String lastMonthSalesNum;
    @ApiModelProperty("去年本月销售台数")
    private String lastYearMonthSalesNum;
    @ApiModelProperty("销售台数月同比")
    private String salesNumMonthYoy;
    @ApiModelProperty("销售台数月环比")
    private String salesNumMonthMom;



    @ApiModelProperty("今日门店数")
    private String todayStoreNum;
    @ApiModelProperty("昨日门店数")
    private String yesterdayStoreNum;
    @ApiModelProperty("去年今日门店数")
    private String lastYearTodayStoreNum;
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


    @ApiModelProperty("本周回转率")
    private String weekConversionRate;
    @ApiModelProperty("本周回转率同比")
    private String conversionRateWeekYoy;
    @ApiModelProperty("本周回转率环比")
    private String conversionRateWeekMom;


    @ApiModelProperty("本月回转率")
    private String monthConversionRate;
    @ApiModelProperty("本月回转率同比")
    private String conversionRateMonthYoy;
    @ApiModelProperty("本月回转率环比")
    private String conversionRateMonthMom;


}
