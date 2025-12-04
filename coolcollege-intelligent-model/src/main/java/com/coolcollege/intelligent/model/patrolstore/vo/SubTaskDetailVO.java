package com.coolcollege.intelligent.model.patrolstore.vo;

import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Data
public class SubTaskDetailVO {

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 任务范围
     */
    private String taskRange;

    /**
     * 有效范围
     */
    private String effectiveRange;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 处理人
     */
    private String processUser;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态
     */
    private String taskStatus;
}
