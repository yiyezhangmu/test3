package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @date 2023-07-11 01:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePlanDTO implements Serializable {

    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("计划日期")
    private Date planDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("门店照")
    private String avatar;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店状态 0:未完成 1:已完成")
    private Integer status;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("维度")
    private String latitude;

    @ApiModelProperty("门店地址")
    private String locationAddress;

    @ApiModelProperty("相对于传递经纬度的距离")
    private Double distance;

    @ApiModelProperty("本月计划巡店次数")
    private Long monthPatrolNum;
}