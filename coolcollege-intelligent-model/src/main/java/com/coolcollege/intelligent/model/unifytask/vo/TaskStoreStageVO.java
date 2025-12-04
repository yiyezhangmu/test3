package com.coolcollege.intelligent.model.unifytask.vo;

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
@AllArgsConstructor
@NoArgsConstructor
public class TaskStoreStageVO {

    /**
     * 父任务id
     */
    private Long unifyTaskId;


    /**
     * 子任务状态
     */
    private String subStatus;


    /**
     * 循环任务循环轮次
     */
    private Long loopCount;

    /**
     * 审批链任务开始时间
     */
    private Date subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Date subEndTime;

    /**
     * 处理时间
     */
    private Date handleTime;


    /**
     * 是否逾期
     */
    private Boolean expireFlag = false;

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 完成任务数量
     */
    private Long completeCount;

    /**
     * 进行中数量
     */
    private Long ongoingCount;

    /**
     * 进行中已逾期数量
     */
    private Long ongoingCountOve;

    private String taskName;
}
