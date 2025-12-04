package com.coolcollege.intelligent.model.patrolstore.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-09-04 11:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolPlanDO implements Serializable {

    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("计划月份")
    private String planMonth;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("巡店人")
    private String supervisorId;

    @ApiModelProperty("审核人")
    private String auditUserId;

    @ApiModelProperty("总门店数量")
    private Integer patrolTotalStoreNum;

    @ApiModelProperty("完成门店数量")
    private Integer patrolFinishStoreNum;

    @ApiModelProperty("审核状态")
    private Integer auditStatus;

    @ApiModelProperty("多个检查表的ID")
    private String metaTableIds;

    @ApiModelProperty("巡店总结")
    private Boolean isOpenSummary;

    @ApiModelProperty("巡店签名")
    private Boolean isOpenAutograph;

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