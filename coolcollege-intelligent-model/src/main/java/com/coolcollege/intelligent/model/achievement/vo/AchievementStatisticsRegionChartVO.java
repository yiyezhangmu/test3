package com.coolcollege.intelligent.model.achievement.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 业绩报表统计表数据VO
 * @Author: mao
 * @CreateDate: 2021/5/25 13:45
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementStatisticsRegionChartVO {
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 31天目标业绩额
     */
    private double[] completionTarget;
}
