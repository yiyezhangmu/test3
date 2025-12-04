package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewSalesProfileResponse {


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

    @ApiModelProperty("昨日回转率")
    private String yesterdayConversionRate;

    @ApiModelProperty("今日回转率同比")
    private String conversionRateDayYoy;

    @ApiModelProperty("今日回转率环比")
    private String conversionRateDayMom;



}
