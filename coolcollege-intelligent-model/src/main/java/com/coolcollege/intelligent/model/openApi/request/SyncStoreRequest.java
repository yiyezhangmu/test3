package com.coolcollege.intelligent.model.openApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SyncStoreRequest {

    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty("企业id")
    private String enterpriseId;

    @NotBlank(message = "区域id不能为空")
    @ApiModelProperty("区域id")
    private String regionId;

    @NotBlank(message = "门店名称不能为空")
    @ApiModelProperty("门店名称")
    private String shopName;

    @NotBlank(message = "门店编号不能为空")
    @ApiModelProperty("门店编号")
    private String shopCode;

}
