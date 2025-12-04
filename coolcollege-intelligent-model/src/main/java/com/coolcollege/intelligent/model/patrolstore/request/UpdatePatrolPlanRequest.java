package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: AddPatrolPlanRequest
 * @Description:
 * @date 2024-09-04 11:41
 */
@Data
public class UpdatePatrolPlanRequest {

    @NotNull(message = "计划id不能为空")
    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("巡店人")
    private String supervisorId;

    @NotBlank(message = "审批人不能为空")
    @ApiModelProperty("审批人id")
    private String auditUserId;

    @NotEmpty(message = "任务内容不能为空")
    @ApiModelProperty("任务内容 检查表id")
    private List<Long> metaTableIds;

    @ApiModelProperty("巡店总结")
    private Boolean isOpenSummary;

    @ApiModelProperty("巡店签名")
    private Boolean isOpenAutograph;

    @NotEmpty(message = "门店计划列表不能为空")
    @ApiModelProperty("门店计划列表")
    private List<StorePlanDetailRequest> storePlanList;

}
