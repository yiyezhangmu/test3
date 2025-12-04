package com.coolcollege.intelligent.model.unifytask.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按人任务-查询request
 * @author zhangnan
 * @date 2022-04-15 18:03
 */
@Data
public class GetTaskDetailByPersonRequest {

    @ApiModelProperty(value = "父任务id，必传", example = "0")
    private Long unifyTaskId;

    @ApiModelProperty(value = "子任务id，子任务id不传根据父任务查询执行要求", example = "0")
    private Long subTaskId;
}
