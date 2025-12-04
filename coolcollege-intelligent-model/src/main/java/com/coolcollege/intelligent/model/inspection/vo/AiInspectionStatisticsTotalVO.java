package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionStatisticsTotalVO {


    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检总次数")
    private Long patrolTotalNum;

    /**
     * 不合格次数
     */
    @ApiModelProperty("巡检不合格次数")
    private Long failNum;

    /**
     * 合格次数
     */
    @ApiModelProperty("巡检合格次数")
    private Long passNum;


    @ApiModelProperty("问题TOP1")
    private String problemTop;

    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检问题TOP1不合格次数")
    private Long problemTotalNum;

}
