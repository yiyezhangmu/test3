package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: huhu
 * @Date: 2024/9/5 16:27
 * @Description:
 */
@Data
public class UpdatePatrolPlanRemarkRequest {

    @NotNull(message = "计划id不能为空")
    @ApiModelProperty("计划id")
    private Long planId;

    @NotBlank(message = "备注不能为空")
    @ApiModelProperty("备注")
    private String remark;
}
