package com.coolcollege.intelligent.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe: 根据旧密码修改密码 DTO
 *
 * @author wangff
 * @date 2024/11/7
 */
@Data
@ApiModel("根据密码修改密码DTO")
public class ModifyPasswordByOldDTO {

    @ApiModelProperty("手机号码")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty("原密码")
    @NotBlank(message = "原密码不能为空")
    private String originalPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
