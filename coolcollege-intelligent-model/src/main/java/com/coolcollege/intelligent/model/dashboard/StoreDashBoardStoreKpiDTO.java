package com.coolcollege.intelligent.model.dashboard;

import lombok.Data;

/**
 * @ClassName StoreDashBoardStoreKpiDTO
 * @Description 门店看板kpi
 */
@Data
public class StoreDashBoardStoreKpiDTO {
    /**
     * 门店数
     */
    private Integer storeCount;
    /**
     * 巡店数
     */
    private Integer patrolStoreCount;
    /**
     * 巡店覆盖率
     */
    private String patrolStoreCoverage;
    /**
     * 巡店次数
     */
    private Integer patrolStoreTimes;
    /**
     * 店均被巡次数
     */
    private Double patrolAverageTimes;
    /**
     * 平均巡店时长（分钟）
     */
    private Long patrolAverageDuration;

}
