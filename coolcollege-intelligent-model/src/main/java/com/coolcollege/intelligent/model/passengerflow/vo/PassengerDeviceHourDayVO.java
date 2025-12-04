package com.coolcollege.intelligent.model.passengerflow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/18
 */
@ApiModel
@Data
public class PassengerDeviceHourDayVO {

    private Date flowDay;

    @ApiModelProperty("进店人数")
    private Integer flowIn;

    @ApiModelProperty("出店人数")
    private Integer flowOut;

    @ApiModelProperty("经店人数")
    private Integer flowInOut;

    @ApiModelProperty("进店率")
    private BigDecimal flowInPercent;

    public String getFlowInPercentStr() {
        if(flowInPercent == null){
            return "0%";
        }
        return flowInPercent.multiply(new BigDecimal(100)).intValue() + "%";
    }

}
