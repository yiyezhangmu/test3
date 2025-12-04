package com.coolcollege.intelligent.model.patrolstore.dto;

import com.coolcollege.intelligent.common.constant.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zhangchenbiao
 * @date 2023-07-11 01:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePeopleCountDTO implements Serializable {

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "用户给Id")
    private String userName;

    @ApiModelProperty(value = "用户给Id")
    private String jobNum;

    @ApiModelProperty(value = "管辖门店数")
    private Integer storeCount;

    @ApiModelProperty(value = "巡店完成率")
    private BigDecimal patrolCompleteRate;

    @ApiModelProperty(value = "应巡门店")
    private Long planStoreCount;

    @ApiModelProperty(value = "已巡门店")
    private Long completeStoreCount;

    public BigDecimal getPatrolCompleteRate() {
        if (planStoreCount != null && planStoreCount != 0 && completeStoreCount != null && completeStoreCount != 0) {
            patrolCompleteRate = new BigDecimal(completeStoreCount).divide(new BigDecimal(planStoreCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(2, RoundingMode.HALF_UP);
        } else {
            patrolCompleteRate = new BigDecimal(Constants.ZERO_STR);
        }
        return patrolCompleteRate;
    }
}