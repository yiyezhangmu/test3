package com.coolcollege.intelligent.model.achievement.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端业绩管理首页
 *
 * @author chenyupeng
 * @since 2021/10/27
 */
@Data
public class AchievementStoreVO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店编号
     */
    private String storeNum;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 月目标金额
     */
    private BigDecimal achievementTarget;

    /**
     * 已完成业绩
     */
    private BigDecimal achievementAmount;


}
