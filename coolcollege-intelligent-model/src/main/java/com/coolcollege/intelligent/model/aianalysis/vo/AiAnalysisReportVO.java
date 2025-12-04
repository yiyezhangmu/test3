package com.coolcollege.intelligent.model.aianalysis.vo;

import com.coolcollege.intelligent.model.aianalysis.AiAnalysisReportDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * AI分析报告VO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAnalysisReportVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("报告分析日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    @ApiModelProperty("图片列表")
    private List<String> pictures;

    @ApiModelProperty("AI分析结果")
    private String aiResult;

    public static AiAnalysisReportVO convert(AiAnalysisReportDO reportDO) {
        return AiAnalysisReportVO.builder()
                .id(reportDO.getId())
                .storeId(reportDO.getStoreId())
                .reportDate(reportDO.getReportDate())
                .aiResult(reportDO.getAiResult())
                .build();
    }
}
