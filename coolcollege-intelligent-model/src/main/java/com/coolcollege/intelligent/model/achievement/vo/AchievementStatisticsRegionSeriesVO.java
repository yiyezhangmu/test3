package com.coolcollege.intelligent.model.achievement.vo;

import java.util.List;

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
public class AchievementStatisticsRegionSeriesVO {
    /**
     * series
     */
    private List<AchievementStatisticsRegionChartVO> series;
}
