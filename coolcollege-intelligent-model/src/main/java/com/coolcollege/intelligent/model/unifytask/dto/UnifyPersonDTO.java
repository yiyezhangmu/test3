package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/29 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyPersonDTO {
    /**
     * 任务ID
     */
    private Long unifyTaskId;
    /**
     * 映射主键
     */
    private String userId;
    /**
     * 映射类型
     */
    private String storeId;
    /**
     * 对应审批节点
     */
    private String node;
    /**
     * 任务角色
     * 目前只有审批者一种角色 approval
     */
    private String taskRole;
    /**
     * 人员专属
     */
    private String userName;
    /**
     * 头像
     */
    private String  avatar;
    /**
     * 子任务唯一编码
     * 父任务id#门店id
     */
    private String subTaskCode;

    private Long loopCount;
}
