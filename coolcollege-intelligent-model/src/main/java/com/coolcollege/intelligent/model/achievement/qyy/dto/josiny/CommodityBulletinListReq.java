package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommodityBulletinListReq {
    @ApiModelProperty("类型快报类型\n" +
            "sales：销量\n" +
            "inventory：库存")
    private String type;
    @ApiModelProperty("钉钉部门id")
    private String synDingDeptId;
}
