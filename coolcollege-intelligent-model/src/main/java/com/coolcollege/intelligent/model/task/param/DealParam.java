package com.coolcollege.intelligent.model.task.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/14 15:29
 */
@Data
public class DealParam {
    /**
     * 子任务id
     */
    @NotNull(message = "子任务id不能为空")
    private Long subTaskId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 操作pass通过 reject拒绝
     */
    private String activeKey;
    /**
     * 审批数据
     */
    private String data;
}
