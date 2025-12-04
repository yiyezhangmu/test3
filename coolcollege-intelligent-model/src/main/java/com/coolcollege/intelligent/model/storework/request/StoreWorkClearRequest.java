package com.coolcollege.intelligent.model.storework.request;

import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author suzhuhong
 * @Date 2022/9/21 19:00
 * @Version 1.0
 */
@Data
@ApiModel
public class StoreWorkClearRequest {

    @NotBlank
    @ApiModelProperty("门店ID")
    private String storeId;

    @NotNull
    @ApiModelProperty("时间周期值")
    private Integer timeUnion;

    @NotNull
    @ApiModelProperty("时间周期")
    private TimeCycleEnum timeCycle;

}
