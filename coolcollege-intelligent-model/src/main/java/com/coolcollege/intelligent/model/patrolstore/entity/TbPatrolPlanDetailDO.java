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
 * @date   2024-09-04 02:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolPlanDetailDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("计划日期")
    private Date planDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店状态 0:未完成 1:已完成")
    private Integer status;

    @ApiModelProperty("完成时间")
    private Date finishTime;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

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