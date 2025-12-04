package com.coolcollege.intelligent.model.aianalysis.dto;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * AI分析规则查询DTO
 * </p>
 *
 * @author wangff
 * @since 2025/7/1
 */
@Data
public class AiAnalysisRuleQueryDTO extends PageBaseRequest {
    @ApiModelProperty("规则名称")
    private String ruleName;
}
