package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wxp
 */
@Data
public class TaskReportQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Long startTime;

    private Long endTime;
    /**
     * 任务名称
     */
    private String taskName;
    // 任务类型  PATROL_STORE
    private String taskType;
    /**
     * 创建人id
     */
    private String createUserId;

    private String dbName;

    private String businessType;

}
