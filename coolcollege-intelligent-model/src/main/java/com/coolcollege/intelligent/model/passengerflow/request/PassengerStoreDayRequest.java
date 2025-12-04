package com.coolcollege.intelligent.model.passengerflow.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/20
 */
@Data
public class PassengerStoreDayRequest extends PassengerBaseRequest{
    @ApiModelProperty("区域id")
    private Long regionId;
    @ApiModelProperty("门店id 多个,分隔")
    private String storeIdStr;
    @ApiModelProperty("页码")
    private Integer pageNo=1;
    @ApiModelProperty("每页大小")
    private Integer pageSize=10;


    
}
