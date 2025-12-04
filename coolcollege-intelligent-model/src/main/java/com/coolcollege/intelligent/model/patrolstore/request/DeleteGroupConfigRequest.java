package com.coolcollege.intelligent.model.patrolstore.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: huhu
 * @Date: 2024/9/6 14:14
 * @Description:
 */
@Data
public class DeleteGroupConfigRequest {
    @NotNull(message = "群组id不能为空")
    @ApiModelProperty("群组id")
    private Long groupId;

}
