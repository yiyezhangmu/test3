package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElemeStoreOpenRequest {

    @ApiModelProperty("门店id")
    private String thirdStoreId;

}
