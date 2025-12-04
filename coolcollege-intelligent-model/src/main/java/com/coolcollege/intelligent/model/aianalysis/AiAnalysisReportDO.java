package com.coolcollege.intelligent.model.aianalysis;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI分析报告
 * @author   zhangchenbiao
 * @date   2025-06-30 05:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisReportDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("规则id")
    private Long ruleId;

    @ApiModelProperty("报告名称")
    private String reportName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("报告推送时间")
    private LocalDateTime reportPushTime;

    @ApiModelProperty("报告分析日期")
    private LocalDate reportDate;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("AI分析结果")
    private String aiResult;
}