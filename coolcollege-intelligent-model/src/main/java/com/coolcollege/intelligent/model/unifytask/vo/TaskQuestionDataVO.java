package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.Data;

/**
 * 问题工单详情vo
 * @author ：xugangkun
 * @date ：2022/3/1 16:12
 */
@Data
public class TaskQuestionDataVO {
    /**
     * ID
     */
    private Long unifyTaskId;
    /**
     * 任务名称
     */
    private String unifyTaskName;
    /**
     * 发起人id
     */
    private String createUserId;
    /**
     * 发起人名称
     */
    private String createUserName;
    /**
     * 处理截止时间
     */
    private String handlerEndTime;
    /**
     * 处理人名称
     */
    private String handlerUserName;
    /**
     * 任务状态
     */
    private String subStatus;
}
