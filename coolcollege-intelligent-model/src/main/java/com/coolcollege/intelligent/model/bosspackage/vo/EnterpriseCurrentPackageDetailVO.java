package com.coolcollege.intelligent.model.bosspackage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/24 14:11
 */
@Data
public class EnterpriseCurrentPackageDetailVO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("套餐id")
    private Long packageId;

    @ApiModelProperty("套餐名称")
    private String packageName;


}
