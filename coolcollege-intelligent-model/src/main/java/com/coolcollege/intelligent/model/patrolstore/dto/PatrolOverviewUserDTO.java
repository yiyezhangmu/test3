package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @author byd
 */
@ApiModel
@Data
public class PatrolOverviewUserDTO {

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

    /**
     * 复审人id
     */
    @ApiModelProperty("复审人id")
    private String userId;

    /**
     * 复审人名称
     */
    @ApiModelProperty("复审人名称")
    private String userName;
}
