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
 * AI分析报告用户映射
 * @author   zhangchenbiao
 * @date   2025-06-30 05:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisReportUserMappingDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("AI分析报告id")
    private Long reportId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("报告推送时间")
    private LocalDateTime reportPushTime;

    @ApiModelProperty("报告分析时间")
    private LocalDate reportDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}