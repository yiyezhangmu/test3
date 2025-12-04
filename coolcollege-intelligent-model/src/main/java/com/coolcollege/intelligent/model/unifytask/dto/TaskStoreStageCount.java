package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;


/**
 * @author byd
 */
@Data
public class TaskStoreStageCount {

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
     * 待审批数量
     */
    private Long approveCount;
}
