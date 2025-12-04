package com.coolcollege.intelligent.model.unifytask.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/1 14:31
 */
@Data
public class TaskStoreQuestionDataVO {

    /**
     * ID
     */
    private Long unifyTaskId;

    /**
     * 门店任务id
     */
    private Long taskStoreId;

    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * task_info
     */
    private String taskInfo;
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
    private Date handlerEndTime;
    /**
     * 处理人名称
     */
    private String handlerUserName;
    /**
     * 任务状态
     */
    private String subStatus;
    /**
     * 当前流程进度节点
     */
    private String nodeNo;

    /**
     * 轮次
     */
    private Long loopCount;

}
