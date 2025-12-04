package com.coolcollege.intelligent.model.achievement.vo;

import java.math.BigDecimal;
import java.util.List;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class AchievementStatisticsRegionTableVO {
    /**
     * 区域名称
     */
    @Excel(name = "区域名称", width = 20, orderNum = "1")
    private String regionName;
    /**
     * 目标业绩额
     */
    private BigDecimal achievementTarget;
    /**
     * 完成业绩额
     */
    @Excel(name = "合计", width = 20, orderNum = "2",type = 10)
    private BigDecimal completionTarget;
    /**
     * 完成率
     */
    private Double completionRate;
    /**
     * 各业绩详情
     */
    private List<AchievementFormworkDetailVO> typeData;

    @Excel(name = "详情", width = 60, orderNum = "3")
    private String detail;
}
