package com.coolcollege.intelligent.model.achievement.qyy.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QyyPerformanceReportaVO {
    private String deptName;

    private BigDecimal grossSales;

    private BigDecimal grossSalesRate;
    private BigDecimal perCustomer;

    private BigDecimal salesVolume;

    private String storeBurst;

    private String goodsNo;

    private String year;

    private Integer InnerSalesVolume;

    private BigDecimal sales;

    private Integer inventory;

    private String season;

    private String goodsPic;

    private String rankIcon;
}
