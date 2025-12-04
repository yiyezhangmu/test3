package com.coolcollege.intelligent.model.achievement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AchievementTotalAmountDTO {

    /**
     * 总销售额
     */
    private BigDecimal totalAmount;

    /**
     * 订单数
     */
    private Integer orderNum;
}
