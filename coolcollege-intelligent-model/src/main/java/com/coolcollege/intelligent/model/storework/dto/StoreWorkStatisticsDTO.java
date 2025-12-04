package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wxp
 * @Date 2022/9/16 15:01
 * @Version 1.0
 */
@Data
public class StoreWorkStatisticsDTO {

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("已完成门店数")
    private Integer finishNum;

    @ApiModelProperty("未完成门店数")
    private Integer unFinishNum;

    @ApiModelProperty("总门店数")
    private Integer totalNum;

}
