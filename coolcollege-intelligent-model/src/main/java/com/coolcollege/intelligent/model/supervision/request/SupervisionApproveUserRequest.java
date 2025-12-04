package com.coolcollege.intelligent.model.supervision.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 19:37
 * @Version 1.0
 */
@ApiModel
@Data
public class SupervisionApproveUserRequest {

    @ApiModelProperty("person/store")
    private String type;

    @ApiModelProperty(value = "任务id", required = true)
    private Long taskId;

    @ApiModelProperty("门店任务id(一键催办不需要传)")
    private Long storeTaskId;

}
