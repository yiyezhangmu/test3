package com.coolcollege.intelligent.model.aianalysis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI模型VO
 * </p>
 *
 * @author wangff
 * @since 2025/7/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisModelVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String aiModelName;
}
