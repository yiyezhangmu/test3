package com.coolcollege.intelligent.model.unifytask.query;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: TaskFinishStorePageRequest
 * @Description:
 * @date 2024-10-17 14:33
 */
@Data
public class TaskFinishStorePageRequest extends PageBaseRequest {

    @NotNull(message = "任务id不能为空")
    @ApiModelProperty("任务id")
    private Long unifyTaskId;

    @NotNull(message = "类型不能为空")
    @ApiModelProperty("0待完成任务，1已完成任务")
    private Integer statusType;

}
