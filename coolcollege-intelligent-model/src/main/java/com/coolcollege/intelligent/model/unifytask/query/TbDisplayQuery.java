package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
wxp
 */
@Data
public class TbDisplayQuery implements Serializable {

    private static final long serialVersionUID = -7897701301446156205L;

    /**
     *
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;
    /**
     *
     */
    private String taskName;
    /**
     *
     */
    /**
     * 父任务id
     */
    private Long unifyTaskId;
    /**
     * 子任务id
     */
    private Long subTaskId;
    /**
     * @see com.coolcollege.intelligent.model.enums.UnifyTaskQueryEnum
     * 查询方式
     */
    private String queryType;

    private String storeName;

    /**
     * 处理人
     */
    private String userId;
}
