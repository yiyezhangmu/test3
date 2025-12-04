package com.coolcollege.intelligent.model.unifytask.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author byd
 */
@Data
public class StoreTaskListRequest {

    @NotBlank
    @ApiModelProperty("门店ID")
    private String storeId;

    @NotBlank
    @ApiModelProperty("日期时间 2022-09-01")
    private String date;
}
