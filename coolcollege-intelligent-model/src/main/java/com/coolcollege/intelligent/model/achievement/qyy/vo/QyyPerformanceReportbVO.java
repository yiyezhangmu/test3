package com.coolcollege.intelligent.model.achievement.qyy.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QyyPerformanceReportbVO {
    private String storeName;

    private BigDecimal rate;

    private Integer salesVolume;

    private String goodsName;

    private String goodsNo;

    private String goodsPic;

    private String rankIcon;
}
