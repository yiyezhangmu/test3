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
public class AchieveRegionReportDTO {

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("门店数量")
    private Integer storeNum;

    @ApiModelProperty("销售台数")
    private Long goodsNum;

    @ApiModelProperty("销售金额")
    private BigDecimal achievementAmount;
}
