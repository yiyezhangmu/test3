package com.coolcollege.intelligent.model.aianalysis.dto;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * Ai分析报告查询DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Data
public class AiAnalysisReportQueryDTO extends PageBaseRequest {
    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("报告日期，本月month/本周week/昨日yesterday/今日today")
    private String reportDate;

    @ApiModelProperty("自定义起始日期，yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty("自定义结束日期，yyyy-MM-dd")
    private LocalDate endDate;
}
