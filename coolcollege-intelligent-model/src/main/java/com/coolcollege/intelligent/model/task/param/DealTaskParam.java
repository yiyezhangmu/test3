package com.coolcollege.intelligent.model.task.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 处理任务
 * @author byd
 */
@ApiModel("任务处理请求")
@Data
public class DealTaskParam {
    /**
     * 父任务id
     */
    @ApiModelProperty("父任务id")
    @NotNull(message = "父任务id不能为空")
    private Long unifyTaskId;
    /**
     * 父任务id
     */
    @ApiModelProperty("门店id")
    @NotNull(message = "门店id不能为空")
    private String storeId;
    /**
     * 父任务id
     */
    @ApiModelProperty("节点1：处理  2：审批")
    @NotNull(message = "处理节点不能为空")
    private String nodeNo;
    /**
     * 父任务id
     */
    @ApiModelProperty("轮次")
    private Long loopCount;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;
    /**
     * 操作pass通过 reject拒绝
     */
    @ApiModelProperty("操作pass通过 reject拒绝")
    private String activeKey;

    /**
     * 整改操作
     */
    @ApiModelProperty("处理操作: pass通过 reject拒绝 rectified已整改 unneeded无需整改")
    private String handleAction;
    /**
     * 审批数据
     */
    @ApiModelProperty("审批数据")
    private String data;
}
