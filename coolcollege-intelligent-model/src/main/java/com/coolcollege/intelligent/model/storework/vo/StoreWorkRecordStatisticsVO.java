package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2022/9/23 11:41
 * @Version 1.0
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkRecordStatisticsVO {
    @ApiModelProperty(value = "门店店外完成率")
    private String storeCompleteRate;
    @ApiModelProperty(value = "已完成门店")
    private Integer completeStoreNum;
    @ApiModelProperty(value = "为完成门店")
    private Integer noCompleteStoreNum;
}
