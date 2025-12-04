package com.coolcollege.intelligent.model.achievement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PersonalAchievementVO {
    @ApiModelProperty("员工Id")
    private String userId;

    @ApiModelProperty("员工姓名")
    private String userName;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("所属区域")
    private String regionName;

    @ApiModelProperty("销售额")
    private String achievementAmount;

    @ApiModelProperty("销售台数")
    private Long goodsNum;

}
