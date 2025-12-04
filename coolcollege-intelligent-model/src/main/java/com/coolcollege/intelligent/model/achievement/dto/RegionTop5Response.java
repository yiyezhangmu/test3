package com.coolcollege.intelligent.model.achievement.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegionTop5Response {
    private String id;
    private String name;
    private String regionPath;
    private String totalAchievementAmount;
    private String achievementTarget;

    private String achievementAmount;

    private String completionRate;

    @ApiModelProperty("转化率")
    private String conversionRate;

    @ApiModelProperty("商品销售数量")
    private String goodsNum;

    @ApiModelProperty("门店数量")
    private String storeNum;
}
