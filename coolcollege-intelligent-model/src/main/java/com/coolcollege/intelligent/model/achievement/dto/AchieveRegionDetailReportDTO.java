package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author byd
 * @date 2024-03-25 10:35
 */
@Data
public class AchieveRegionDetailReportDTO {

    private Long regionId;

    @ApiModelProperty("日期")
    private String reportDateStr;

    @ApiModelProperty("日期时间")
    private Date reportDateTime;

    @ApiModelProperty("门店数量")
    private Long storeNum;

    @ApiModelProperty("销售台数")
    private Long goodsNum;

    @ApiModelProperty("销售金额")
    private BigDecimal achievementAmount;


}
