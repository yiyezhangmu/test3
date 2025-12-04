package com.coolcollege.intelligent.model.unifytask.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 获得催办子任务列表
 * @author ：xugangkun
 * @date ：2021/11/9 11:18
 */
@Data
public class ParentTaskReminderDTO {

    @NotEmpty(message = "父任务id不能为空")
    @ApiModelProperty("父任务列表")
    private List<Long> unifyTaskIds;

    @ApiModelProperty("门店id")
    private String storeId;
}
