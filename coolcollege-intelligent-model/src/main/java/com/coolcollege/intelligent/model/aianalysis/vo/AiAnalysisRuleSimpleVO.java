package com.coolcollege.intelligent.model.aianalysis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI分析规则简单信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAnalysisRuleSimpleVO {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("有效期")
    private String validityPeriod;

    @ApiModelProperty("抓拍时间")
    private String captureTimes;

    @ApiModelProperty("AI分析模型id列表")
    private String models;

    @ApiModelProperty("AI分析模型名称列表")
    private String aiModelNames;

    @ApiModelProperty("门店范围")
    private String storeRange;

    @ApiModelProperty("报告推送人")
    private String reportPusher;
}
