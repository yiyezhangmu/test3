package com.coolcollege.intelligent.model.achievement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TOPTenVO {

    @ApiModelProperty("品类")
    private String category;

    @ApiModelProperty("中类")
    private String middleClass;

    @ApiModelProperty("商品型号")
    private String type;

    @ApiModelProperty("销售额")
    private BigDecimal achievementAmount;

    @ApiModelProperty("售卖数量")
    private String goodsNum;

}
