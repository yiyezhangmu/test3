package com.coolcollege.intelligent.model.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: GetAiAndScoreDTO
 * @Description:
 * @date 2025-02-21 20:16
 */
@Data
public class AiCommentAndScoreDTO {

    @ApiModelProperty("图片链接")
    private String imageUrl;

    @ApiModelProperty("标准描述")
    private String standardDesc;

    @ApiModelProperty("标准分")
    private BigDecimal score;

    public AiCommentAndScoreDTO(String imageUrl, String standardDesc, BigDecimal score) {
        this.imageUrl = imageUrl;
        this.standardDesc = standardDesc;
        this.score = score;
    }

    public AiCommentAndScoreDTO() {
    }
}
