package com.coolcollege.intelligent.model.achievement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StoreRealDataVO {
    @ApiModelProperty("门店Id")
    private String storeId;

    @ApiModelProperty("目标销售额")
    private String achievementTarget;

    @ApiModelProperty("销售额")
    private String achievementAmount;

    @ApiModelProperty("销售台数")
    private String goodsNum;

    @ApiModelProperty("完成率")
    private String completionRate;

}
