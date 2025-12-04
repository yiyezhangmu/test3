package com.coolcollege.intelligent.model.unifytask.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 门店任务转交
 * @author byd
 */
@Data
public class UnifyStoreTaskTurnDTO {

    @NotNull(message = "该任务不存在")
    private Long taskStoreId;

    @NotBlank(message = "转交人不能为空")
    private String turnUserId;

    private String remark;
}
