package com.coolcollege.intelligent.model.supervision.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 15:43
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionReassignDTO {

    @ApiModelProperty("person-纯人任务  store-按人按店任务")
    private String type;
    @ApiModelProperty("按人任务ID或者按门店任务ID")
    private List<Long> taskIdList;
    @ApiModelProperty("执行人id")
    private String handleUserId;
    @ApiModelProperty("执行人名称")
    private String handleUserName;
    @ApiModelProperty("一级审批人")
    private List<String> firstApproveList;
    @ApiModelProperty("二级审批人")
    private String secondaryApprove;
    @ApiModelProperty("三级审批人")
    private String thirdApprove;

}
