package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.common.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * @author byd
 */
@ApiModel
@Data
public class PatrolOverviewDTO {

    /**
     * 可复审数量
     */
    @ApiModelProperty("可复审数量")
    private Long canRecheck;

    /**
     * 已复审数量
     */
    @ApiModelProperty("已复审数量")
    private Long alreadyRecheck;

    @ApiModelProperty("复审率")
    private BigDecimal recheckPercent;

    public BigDecimal getRecheckPercent() {
        if(canRecheck == null || alreadyRecheck ==null){
            return recheckPercent;
        }
        if (canRecheck != 0L) {
            recheckPercent = (new BigDecimal(alreadyRecheck).divide(new BigDecimal(canRecheck), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(Constants.ONE_HUNDRED))).setScale(2, RoundingMode.HALF_UP);
        } else {
            recheckPercent = new BigDecimal(Constants.ZERO_STR);
        }
        return recheckPercent;
    }
}
