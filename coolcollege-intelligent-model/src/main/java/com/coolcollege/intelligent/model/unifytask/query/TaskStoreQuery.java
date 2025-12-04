package com.coolcollege.intelligent.model.unifytask.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStoreQuery {


    /**
     * 门店id
     */
    private String storeId;

    /**
     * 子任务状态
     */
    private String subStatus;
    /**
     * 选择日期
     */
    private String selectTime;

    /**
     * 当天起始时间
     */
    private String selectBeginTime;

    private Boolean overdueTask = false;

    /**
     * 是否到当天
     */
    private Boolean currDay;

    /**
     * 任务周期 DAY MONTH YEAR
     */
    private String taskCycle;
    /**
     * 运行规则ONCE单次/LOOP循环
     */
    private String runRule;

    private Long unifyTaskId;

    private String dbName;

    private Boolean handlerOvertimeTaskContinue;

    private Boolean approverOvertimeTaskContinue;
}
