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
public class TbPatrolStorePeopleDTO implements Serializable {

    /**
     * 管辖人
     */
    @ApiModelProperty(value = "管辖人")
    private String userId;

    @ApiModelProperty(value = "用户Id")
    private List<String> userIdList;

    @ApiModelProperty(value = "开始时间(时间戳)")
    private Long beginTime;

    @ApiModelProperty(value = "结束时间(时间戳)")
    private Long endTime;
}