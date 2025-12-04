package com.coolcollege.intelligent.model.passengerflow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/20
 */
@ApiModel
@Data
public class PassengerStoreRankVO {

    @ApiModelProperty("门店storeId")
    private String storeId;
    @ApiModelProperty("门店名称")
    private String storeName;
    private String personCount;
    @ApiModelProperty("排名")
    private Integer rank;
    
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
