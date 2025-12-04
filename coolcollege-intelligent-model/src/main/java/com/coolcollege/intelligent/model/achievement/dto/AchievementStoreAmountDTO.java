package com.coolcollege.intelligent.model.achievement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author chenyupeng
 * @since 2021/10/27
 */
@Data
public class AchievementStoreAmountDTO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 业绩产生人id
     */
    private String produceUserId;

    /**
     * 业绩产生人名称
     */
    private String produceUserName;

    /**
     * 业绩值
     */
    private BigDecimal achievementAmount;

}
