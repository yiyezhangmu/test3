package com.coolcollege.intelligent.model.achievement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RegionReportVO {


    @ApiModelProperty("销售额")
    private BigDecimal achievementAmount;

    @ApiModelProperty("售卖数量")
    private Long goodsNum;

    private Date dayTime;

    private Date monthTime;

    private String groupType;

    private String goodsType;

    private String category;
}
