package com.coolcollege.intelligent.model.patrolstore;

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
 * @date   2023-07-11 01:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePlanDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("计划日期")
    private Date planDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径(新)")
    private String regionPath;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店状态 0:未完成 1:已完成")
    private Integer status;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}