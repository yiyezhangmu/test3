package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author byd
 */
@ApiModel
@Data
public class StoreWorkStoreDetailStatisticRequest {

    @ApiModelProperty(value = "执行类型 MONTH:月 WEEK:周 DAY:天", required = true)
    private String workCycle;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty(value = "开始时间(时间戳)", required = true)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间(时间戳)", required = true)
    private Long endTime;

    @ApiModelProperty(value = "门店id", required = true)
    private String storeId;
}
