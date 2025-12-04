package com.coolcollege.intelligent.model.dashboard;

import lombok.Data;

/**
 * @ClassName StoreDashBoardEmployeeKpiDTO
 * @Description 员工看板kpi
 */
@Data
public class StoreDashBoardEmployeeKpiDTO {
    /**
     * 巡店人数
     */
    private Integer patrolOperatorCount;
    /**
     * 巡店数
     */
    private Integer patrolStoreCount;
    /**
     * 人均巡店数
     */
    private Double patrolAverageCount;
    /**
     * 巡店次数
     */
    private Integer patrolStoreTimes;
    /**
     * 人均巡店次数
     */
    private Double patrolAverageTimes;
    /**
     * 平均巡店时长（分钟）
     */
    private Long patrolAverageDuration;
}
