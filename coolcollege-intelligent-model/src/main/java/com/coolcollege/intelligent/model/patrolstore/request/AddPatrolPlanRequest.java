package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: AddPatrolPlanRequest
 * @Description:
 * @date 2024-09-04 11:41
 */
@Data
public class AddPatrolPlanRequest {

    @NotBlank(message = "计划月份不能为空")
    @ApiModelProperty("计划月份")
    private String planMonth;

    @NotBlank(message = "计划月份不能为空")
    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("巡店人")
    @NotBlank(message = "巡店人不能为空")
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

    public static UpdatePatrolPlanRequest convert2Update(Long planId, AddPatrolPlanRequest param) {
        UpdatePatrolPlanRequest updateParam = new UpdatePatrolPlanRequest();
        updateParam.setPlanId(planId);
        updateParam.setSupervisorId(param.getSupervisorId());
        updateParam.setAuditUserId(param.getAuditUserId());
        updateParam.setMetaTableIds(param.getMetaTableIds());
        updateParam.setIsOpenSummary(param.getIsOpenSummary());
        updateParam.setIsOpenAutograph(param.getIsOpenAutograph());
        updateParam.setStorePlanList(param.getStorePlanList());
        return updateParam;
    }

}
