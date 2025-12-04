package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 门店任务转交
 * @author byd
 */
@ApiModel
@Data
public class UnifyStoreTaskBatchErrorDTO {

    @ApiModelProperty("门店任务id")
    private Long taskStoreId;

    @NotBlank(message = "门店名称")
    private String storeName;

    @ApiModelProperty("错误信息")
    private String errMsg;
}
