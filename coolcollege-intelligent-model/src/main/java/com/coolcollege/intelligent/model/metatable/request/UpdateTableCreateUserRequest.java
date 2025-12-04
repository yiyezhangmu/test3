package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateTableCreateUserRequest {

    @NotNull(message = "表id不能为空")
    @ApiModelProperty("表id")
    private Long metaTableId;

    @NotBlank(message = "创建人不能为空")
    @ApiModelProperty("创建者ID")
    private String createUserId;

}
