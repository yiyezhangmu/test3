package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-04-16 03:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPersonPatrolStatisticsVO {

    /**
     *  已巡门店数  
     */
    @ApiModelProperty(value = "已巡门店数")
    private Integer patroledStoreNum;

    /**
     *  总巡店数量
     */
    @ApiModelProperty(value = "总巡店数量")
    private Integer totalPatrolStoreNum;

}