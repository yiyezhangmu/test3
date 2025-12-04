package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenyupeng
 * @since 2021/10/26
 */
@Data
public class AchievementTargetStoreVO {

    /**
     * 目标详情id
     */
    private Long id;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店编号
     */
    private String storeNum;

    /**
     * 月目标金额
     */
    private BigDecimal achievementTarget;

    /**
     * 年目标金额
      */
    private BigDecimal yearAchievementTarget;

    private Long targetId;
}
