package com.coolcollege.intelligent.model.patrolstore.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-09-04 11:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolPlanDealHistoryDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("处理人")
    private String handleUserId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("审核状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}