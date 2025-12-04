package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author byd
 */
@Data
public class TaskParentQuery implements Serializable {

    private static final long serialVersionUID = -7897701301446156205L;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 10;
    /**
     *
     */
    private String taskName;
    /**
     *
     */
    @NotNull(message = "任务类型不能为空")
    private String taskType;

    /**
     * 任务循环类型    MONTH("MONTH", "月"),
     *   WEEK("WEEK", "周"),
     *   DAY("DAY", "日"),
     */
    private String taskCycle;

    private String runRule;
    /**
     * 父任务状态
     */
    private String status;

    /**
     * 是否可继续执行
     */
    private Boolean overdueTaskContinue;


    /**
     * 创建人
     */
    private List<String> userIdList;

    private Long beginTimeStart;
    private Long beginTimeEnd;
    /**
     * create:我创建的  manage：我管理的
     */
    private String type;
}
