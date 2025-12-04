package com.coolcollege.intelligent.model.supervision.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 19:20
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionApproveDataVO {

    @ApiModelProperty("按人任务ID或者按门店任务ID")
    private Long id;
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("任务状态")
    private Integer taskState;
    @ApiModelProperty("任务状态中文")
    private String taskStateStr;
    @ApiModelProperty("执行人")
    private String supervisionHandleUserId;
    @ApiModelProperty("执行人名称")
    private String supervisionHandleUserName;
    @ApiModelProperty("提交时间")
    private Date submitTime;
    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("门店名称")
    private String storeName;
    @ApiModelProperty("转交重新分配标识")
    private Integer TransferReassignFlag;
    private String priority;
}
