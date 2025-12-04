package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 获得催办子任务列表
 * @author ：xugangkun
 * @date ：2021/11/9 11:18
 */
@Data
public class TaskReminderDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 父任务id
     */
    @NotNull(message = "父任务id不能为空")
    private Long unifyTaskId;
    /**
     * 循环任务的循环批次
     */
    private Long loopCount;
    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;
}
