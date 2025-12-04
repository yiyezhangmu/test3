package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/28
 */
@Data
public class AchievementProduceUserVO {

    /**
     * 人员名称（如果没有则是未选择）
     */
    private String userName;

    /**
     * 人员金额
     */
    private BigDecimal userAchievementAmount;
    /**
     * 人员金额所占份额
     */
    private BigDecimal amountPercent;

    private String amountPercentStr;

}
