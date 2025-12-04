package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2024/9/26 11:29
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatrolPlanVO {

    /**
     * 子任务id
     */
    private Long subTaskId;
    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     *任务类型
     */
    private String taskType;

    /**
     * 审批链任务开始时间
     */
    private Long subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Long subEndTime;

    /**
     * 巡店计划巡店总数
     */
    private Integer totalPatrolStoreNum;

    /**
     * 巡店计划巡店数
     */
    private Integer patrolStoreNum;


    private String subStatus;
}
