package com.coolcollege.intelligent.model.store.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCreateUserDTO {

    @NotNull(message = "新店任务id不能为空")
    @ApiModelProperty("新店任务id")
    private Long id;

    @NotBlank(message = "创建人不能为空")
    @ApiModelProperty("创建者ID")
    private String createUserId;

}
