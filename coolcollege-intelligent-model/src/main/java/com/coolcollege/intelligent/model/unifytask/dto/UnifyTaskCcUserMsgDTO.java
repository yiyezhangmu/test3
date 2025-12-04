package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.util.List;

/**
 * 添加父任务与抄送人映射消息体
 * @author ：xugangkun
 * @date ：2021/11/30 10:31
 */
@Data
public class UnifyTaskCcUserMsgDTO {
    /**
     * 企业id
     */
    private String eid;

    /**
     * 任务类型
     * 陈列：DISPLAY_TASK
     */
    private String taskType;

    /**
     * 节点信息
     */
    private List<TaskProcessDTO> process;

    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 父任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;



}
