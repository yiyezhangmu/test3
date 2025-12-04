package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2021/8/30 16:40
 * @Version 1.0
 */
@Data
public class PatrolStoreStatisticsGroupByPatrolTypeDto {
    /**
     * 巡店类型 （线上巡店 线上巡店 定时巡检）
     */
    private String patrolType;

    private Integer num;
}
