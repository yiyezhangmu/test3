package com.coolcollege.intelligent.model.aianalysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * <p>
 * AI分析报告简单信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Data
public class AiAnalysisReportSimpleVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("报告分析日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;
}
