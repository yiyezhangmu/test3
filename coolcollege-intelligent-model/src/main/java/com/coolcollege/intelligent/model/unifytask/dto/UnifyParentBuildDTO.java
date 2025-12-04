package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;


/**
 * @author byd
 */
@Data
public class UnifyParentBuildDTO {

    /**
     * 任务id
     */
    private Long unifyTaskId;

    /**
     * 是否可以发起任务
     */
    private Boolean sendTask;
}
