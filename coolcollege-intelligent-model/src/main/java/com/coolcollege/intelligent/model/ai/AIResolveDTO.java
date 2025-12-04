package com.coolcollege.intelligent.model.ai;

import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * AI处理DTO
 * </p>
 *
 * @author wangff
 * @since 2025/3/21
 */
@Data
public class AIResolveDTO {
    /**
     * 评论
     */
    private String aiComment;

    /**
     * 标准分
     */
    private BigDecimal aiScore;

    /**
     * 回传图片地址
     */
    private String aiImageUrl;

    /**
     * 匹配结果项
     */
    private TbMetaColumnResultDO columnResult;

    public AIResolveDTO(String aiComment, BigDecimal aiScore, TbMetaColumnResultDO columnResult) {
        this.aiComment = aiComment;
        this.aiScore = aiScore;
        this.columnResult = columnResult;
    }

    public AIResolveDTO(BigDecimal aiScore) {
        this.aiScore = aiScore;
    }

    public AIResolveDTO() {
    }

}
