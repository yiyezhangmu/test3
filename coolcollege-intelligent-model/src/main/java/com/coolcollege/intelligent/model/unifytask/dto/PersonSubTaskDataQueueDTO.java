package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import lombok.Data;

/**
 * 按人任务mq message
 * @author zhangnan
 * @date 2022-04-15 10:38
 */
@Data
public class PersonSubTaskDataQueueDTO {

    private String enterpriseId;

    private String dbName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 父任务
     */
    private TaskParentDO taskParent;

}
