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
public class TbPatrolStorePlanAddDTO implements Serializable {

    @ApiModelProperty(value = "用户给Id", required = true)
    private String userId;

    @ApiModelProperty(value = "计划日期 2023-07-14", required = true)
    private String planDate;

    @ApiModelProperty(value = "计划巡店门店列表", required = true)
    List<String> storeIdList;
}