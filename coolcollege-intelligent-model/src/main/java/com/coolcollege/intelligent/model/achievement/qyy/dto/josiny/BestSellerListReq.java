package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BestSellerListReq {
    @ApiModelProperty("畅销类型ws:女鞋\n" +
            "ms:男鞋\n" +
            "bg:箱包")
    private String type;
    @ApiModelProperty("钉钉部门id")
    private String synDingDeptId;
}
