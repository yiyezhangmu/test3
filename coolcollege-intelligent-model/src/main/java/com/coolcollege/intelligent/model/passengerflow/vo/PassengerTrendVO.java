package com.coolcollege.intelligent.model.passengerflow.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * describe:
 *
 * @author wxp
 * @date 2024/09/19
 */
@Data
public class PassengerTrendVO {

    @ApiModelProperty("日期")
    private Date flowDay;

    @ApiModelProperty("进店人数")
    private Integer flowIn;

    @ApiModelProperty("经店人数")
    private Integer flowInOut;

    @ApiModelProperty("经店人数进店率")
    private BigDecimal flowInPercent;

    public String getFlowInPercentStr() {
        if(flowInPercent == null){
            return "0%";
        }
        return flowInPercent.multiply(new BigDecimal(100)).intValue() + "%";
    }

}
