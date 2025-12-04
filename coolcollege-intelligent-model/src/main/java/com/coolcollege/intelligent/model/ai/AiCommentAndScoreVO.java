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
public class AiCommentAndScoreVO {

    @ApiModelProperty("合格/不合格")
    private String aiResult;

    @ApiModelProperty("评论")
    private String aiComment;

    @ApiModelProperty("标准分")
    private BigDecimal aiScore;

    public AiCommentAndScoreVO(String aiComment, BigDecimal aiScore) {
        this.aiComment = aiComment;
        this.aiScore = aiScore;
    }

    public AiCommentAndScoreVO(String aiResult, String aiComment, BigDecimal aiScore) {
        this.aiResult = aiResult;
        this.aiComment = aiComment;
        this.aiScore = aiScore;
    }
}
