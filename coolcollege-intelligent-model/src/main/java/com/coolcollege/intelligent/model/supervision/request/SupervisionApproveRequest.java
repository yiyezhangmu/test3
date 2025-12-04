package com.coolcollege.intelligent.model.supervision.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 19:37
 * @Version 1.0
 */
@Data
public class SupervisionApproveRequest {

    @ApiModelProperty("审批备注")
    private String approveRemark;

    @ApiModelProperty("审核行为:pass通过 reject拒绝")
    private String actionKey;

    @ApiModelProperty("person/store")
    private String type;

    @ApiModelProperty("按人任务ID/按门店任务ID")
    private Long taskId;


}
