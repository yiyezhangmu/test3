package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/25 17:56
 */
@Data
public class UnifyTaskTurnDTO {

    @NotNull(message = "该任务不存在")
    private Long subTaskId;

    @NotBlank(message = "转交人不能为空")
    private String turnUserId;

    private String remark;
}
