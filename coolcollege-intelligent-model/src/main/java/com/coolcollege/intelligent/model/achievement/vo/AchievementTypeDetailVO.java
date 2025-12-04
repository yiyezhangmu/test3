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
public class AchievementTypeDetailVO {
    /**
     * id
     */
    private Long id;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 业绩类型总金额
     */
    private BigDecimal typeAmount;
}
