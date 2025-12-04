package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description: 检查表报表失分项、合格项
 * @Author chenyupeng
 * @Date 2021/7/8
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsTableColumnDTO {
    /**
     * 检查项id
     */
    Long metaColumnId;

    /**
     * 检查项名称
     */
    String metaColumnName;

    /**
     * 检查项标准
     */
    String metaColumnDescription;

    /**
     * 不合格数
     */
    Integer failTimes;

    /**
     * 失分总数
     */
    BigDecimal losePoints;

    /**
     * 检查次数
     */
    Integer checkTimes;

    /**
     * 总得分
     */
    BigDecimal checkScore;
}
