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
public class AchieveStoreReportDTO {

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("所属区域id")
    private Long regionId;

    @ApiModelProperty("所属区域名称")
    private String regionName;

    @ApiModelProperty("销售台数")
    private Long goodsNum;

    @ApiModelProperty("销售金额")
    private BigDecimal achievementAmount;

    @ApiModelProperty("销售目标金额")
    private BigDecimal achievementTargetAmount;
}
