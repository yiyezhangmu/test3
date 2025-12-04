package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/29
 */
@Data
public class AchievementStatisticsRegionMonthVO {
    /**
     * 区域名称
     */
    @Excel(name = "区域名称", width = 20, orderNum = "1")
    private String regionName;
    /**
     * 目标业绩额
     */
    @Excel(name = "本月业绩目标", width = 20, orderNum = "2",type = 10)
    private BigDecimal achievementTarget;
    /**
     * 完成业绩额
     */
    @Excel(name = "本月完成业绩", width = 20, orderNum = "3",type = 10)
    private BigDecimal completionTarget;
    /**
     * 完成率
     */
    @Excel(name = "本月业绩完成率", width = 20, orderNum = "4")
    private Double completionRate;
    /**
     * 各类型业绩详情
     */
    @Excel(name = "各类型业绩详情", width = 60, orderNum = "5")
    private String detail;
}
