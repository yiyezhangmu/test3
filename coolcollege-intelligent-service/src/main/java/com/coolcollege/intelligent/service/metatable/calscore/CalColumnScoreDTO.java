package com.coolcollege.intelligent.service.metatable.calscore;

import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: CalColumnScoreDTO
 * @Description: 计算项的分
 * @date 2022-04-02 10:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalColumnScoreDTO {

    /**
     * 检查项用户打的分
     */
    private BigDecimal score;

    /**
     * 用户给的奖罚
     */
    private BigDecimal rewardPenaltMoney;

    /**
     * 项名称
     */
    private String columnName;

    /**
     * 打分倍数
     */
    private BigDecimal scoreTimes;

    /**
     * 奖罚倍数
     */
    private BigDecimal awardTimes;

    /**
     * 权重
     */
    private BigDecimal weightPercent;

    /**
     * 检查项属性
     */
    private MetaColumnTypeEnum columnTypeEnum;

    /**
     * 检查结果
     */
    private CheckResultEnum checkResult;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 项的最高分
     */
    private BigDecimal columnMaxScore;

    /**
     * 是否继续算分
     */
    private Boolean isContinue;
}
