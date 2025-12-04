package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Description:
* @Param: h5待办统计
* @Author: tangziqi
* @Date: 2023/6/6~10:35
*/
@Data
public class CommissionTotalDTO {
    /**
     * 巡店
     */
    @ApiModelProperty(value = "巡店数量")
    private Long storeTotal;
    /**
     * 陈列
     */
    @ApiModelProperty(value = "陈列数量")
    private Long displayTotal;
    /**
     * 信息采集
     */
    @ApiModelProperty(value = "信息采集数量")
    private Long messageTotal;
    /**
     * 工单
     */
    @ApiModelProperty(value = "工单数量")
    private Long workOrderTotal;

    /**
     * 稽核待办数量
     */
    @ApiModelProperty(value = "稽核待办数量")
    private Long safetyCheckTotal;


    @ApiModelProperty(value = "行事历数量")
    private Long patrolPlanTotal;

}
