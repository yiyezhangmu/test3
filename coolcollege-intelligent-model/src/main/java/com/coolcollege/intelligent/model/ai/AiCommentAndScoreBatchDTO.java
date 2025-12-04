package com.coolcollege.intelligent.model.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * AI批量图片DTO
 * </p>
 *
 * @author wangff
 * @since 2025/3/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiCommentAndScoreBatchDTO {
    @ApiModelProperty("图片链接列表")
    private List<String> imageList;

    @ApiModelProperty("标准描述")
    private String standardDesc;

    @ApiModelProperty("标准分")
    private BigDecimal score;

    @ApiModelProperty("AI模型code")
    private String aiModel;

    public AiCommentAndScoreBatchDTO(List<String> imageList, String standardDesc, BigDecimal score) {
        this.imageList = imageList;
        this.standardDesc = standardDesc;
        this.score = score;
    }
}
