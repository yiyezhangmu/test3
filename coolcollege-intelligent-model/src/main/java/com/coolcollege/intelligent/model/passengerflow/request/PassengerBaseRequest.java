package com.coolcollege.intelligent.model.passengerflow.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/18
 */
@Data
public class PassengerBaseRequest {
    /**
     * 开始时间结束时间 YYYY-MM-dd
     */
    @ApiModelProperty("开始时间")
    private Long startTime;

    /**
     * 开始时间结束时间  YYYY-MM-dd
     */
    @ApiModelProperty("结束时间")
    private Long endTime;

}
