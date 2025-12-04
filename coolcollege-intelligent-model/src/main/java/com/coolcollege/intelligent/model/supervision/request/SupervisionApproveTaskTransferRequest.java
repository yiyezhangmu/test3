package com.coolcollege.intelligent.model.supervision.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 19:30
 * @Version 1.0
 */
@Data
public class SupervisionApproveTaskTransferRequest {

    @ApiModelProperty("person/store")
    private String type;

    @ApiModelProperty("按人任务ID或者按门店任务ID")
    private Long taskId;

    @ApiModelProperty("转交接收人ID")
    private String transferUserId;
    @ApiModelProperty("转交接收人名称")
    private String transferUserName;





}
