package com.coolcollege.intelligent.model.achievement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenyupeng
 * @since 2021/10/27
 */
@Data
public class AchievementDetailDTO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 业绩分类id
     */
    private Long achievementTypeId;

    /**
     * 业绩值
     */
    private BigDecimal achievementAmount;
}
