package com.coolcollege.intelligent.model.question;

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
 * @date   2022-08-16 03:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionParentUserMappingDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("处理人id/审批人id")
    private String handleUserId;

    @ApiModelProperty("工单id")
    private Long questionParentId;

    @ApiModelProperty("工单名称")
    private String questionParentName;

    @ApiModelProperty("工单状态 0:待处理  1:已处理")
    private Integer status;

    @ApiModelProperty("是否是处理人")
    private Boolean isHandleUser;

    @ApiModelProperty("是否是抄送认")
    private Boolean isCcUser;

    @ApiModelProperty("发起人")
    private String createId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("截止时间")
    private Date endTime;

    @ApiModelProperty("是否为审批人")
    private Boolean isApproveUser;
}