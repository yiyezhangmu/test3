package com.coolcollege.intelligent.model.achievement.qyy.dto.josiny;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class achieveReportProductListReq {
    @ApiModelProperty("钉钉部门id")
    private String synDingDeptId;

    private String storeThirdDeptId;

    private String timeType;
}
