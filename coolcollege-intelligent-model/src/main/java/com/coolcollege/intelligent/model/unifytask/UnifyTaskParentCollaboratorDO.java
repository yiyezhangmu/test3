package com.coolcollege.intelligent.model.unifytask;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-02-13 01:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTaskParentCollaboratorDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务类型")
    private String taskType;

    @ApiModelProperty("协作人id")
    private String collaboratorId;

    @ApiModelProperty("父任务状态")
    private String parentStatus;

    @ApiModelProperty("开始时间")
    private Long beginTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}