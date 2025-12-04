package com.coolcollege.intelligent.model.supervision;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-04-10 03:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionApproveDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("子任务ID或者门店任务ID")
    private Long taskId;

    @ApiModelProperty("督导父任务ID")
    private Long taskParentId;

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("操作人id")
    private String approveUserId;

    @ApiModelProperty("操作人姓名")
    private String approveUserName;

    @ApiModelProperty("按人/按门店 person/store")
    private String type;
}