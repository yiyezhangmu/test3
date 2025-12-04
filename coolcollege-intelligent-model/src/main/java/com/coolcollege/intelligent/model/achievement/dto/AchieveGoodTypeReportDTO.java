package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2024-03-25 10:35
 */
@Data
public class AchieveGoodTypeReportDTO {


    @ApiModelProperty("销售台数")
    private Long goodsNum;

    @ApiModelProperty("销售金额")
    private BigDecimal achievementAmount;

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("中类")
    private String middleClass;

    @ApiModelProperty("型号")
    private String type;
}
