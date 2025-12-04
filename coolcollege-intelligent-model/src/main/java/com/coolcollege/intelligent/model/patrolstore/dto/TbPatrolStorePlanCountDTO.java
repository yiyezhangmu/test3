package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-11 01:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePlanCountDTO implements Serializable {

    @ApiModelProperty("计划日期")
    private String planDate;

    @ApiModelProperty("今日计划巡店门店数")
    private Long todayStoreNum;

    @ApiModelProperty("今日已巡店门店数")
    private Long todayPatrolStoreNum;

    @ApiModelProperty("本周计划巡店门店数")
    private Long weekStoreNum;

    @ApiModelProperty("本周已巡店门店数")
    private Long weekPatrolStoreNum;

    @ApiModelProperty("计划巡店门店列表")
    private List<TbPatrolStorePlanDTO> planStoreList;
}