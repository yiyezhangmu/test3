package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: AddPatrolPlanRequest
 * @Description:
 * @date 2024-09-04 11:41
 */
@Data
public class DeletePatrolPlanRequest {

    @NotNull(message = "计划id不能为空")
    @ApiModelProperty("计划id")
    private Long planId;

}
