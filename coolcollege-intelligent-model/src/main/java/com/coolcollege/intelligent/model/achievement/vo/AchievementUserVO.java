package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenyupeng
 * @since 2021/10/27
 */
@Data
public class AchievementUserVO {

    /**
     * 业绩产生人id
     */
    private String produceUserId;

    /**
     * 业绩产生人名称
     */
    private String produceUserName;

    /**
     * 已完成业绩
     */
    private BigDecimal achievementAmount;
}
