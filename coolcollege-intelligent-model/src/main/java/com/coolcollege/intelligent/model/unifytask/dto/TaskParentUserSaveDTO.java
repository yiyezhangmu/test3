package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import java.util.List;

/**
 * 父任务处理人新增或覆盖
 * @author zhangnan
 * @date 2022-02-23 10:27
 */
@Data
public class TaskParentUserSaveDTO {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 处理人列表
     */
    private List<String> userIds;
}
