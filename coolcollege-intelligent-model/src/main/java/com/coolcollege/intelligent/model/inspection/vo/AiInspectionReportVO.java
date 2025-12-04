package com.coolcollege.intelligent.model.inspection.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * AI巡检统计结果VO
 *
 * @author zhangchenbiao
 * @since 2025/10/11
 */
@Data
public class AiInspectionReportVO {


    /**
     * 巡检总次数
     */
    @ApiModelProperty("巡检总次数")
    private Long patrolTotalNum;

    /**
     * 不合格次数
     */
    @ApiModelProperty("不合格次数")
    private Long failNum;

    /**
     * 不合格率
     */
    @ApiModelProperty("不合格率")
    private BigDecimal failRate;
}
