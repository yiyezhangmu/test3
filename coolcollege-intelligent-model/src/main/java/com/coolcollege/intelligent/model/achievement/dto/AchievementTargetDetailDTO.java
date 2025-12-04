package com.coolcollege.intelligent.model.achievement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 业绩门店目标详情dto
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Data
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTargetDetailDTO {
    /**
     * ID
     */
    private Long id;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 时间类型
     */
    private String timeType;
    /**
     * 开始时间
     */
    private Date beginDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 年份
     */
    private Integer achievementYear;
    /**
     * 业绩目标
     */
    private BigDecimal achievementTarget;

    /**
     * 年目标金额
     */
    private BigDecimal yearAchievementTarget;

    /**
     * 目标id
     */
    private Long targetId;
}
