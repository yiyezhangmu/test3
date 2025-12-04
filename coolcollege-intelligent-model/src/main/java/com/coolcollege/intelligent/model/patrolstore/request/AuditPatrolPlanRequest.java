package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: AuditPatrolPlanRequest
 * @Description:
 * @date 2024-09-04 14:10
 */
@Data
public class AuditPatrolPlanRequest {

    @NotNull(message = "计划id不能为空")
    @ApiModelProperty("计划id")
    private Long planId;

    @Min(1)@Max(2)
    @NotNull(message = "状态不能为空")
    @ApiModelProperty("1通过 2拒绝")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

}
