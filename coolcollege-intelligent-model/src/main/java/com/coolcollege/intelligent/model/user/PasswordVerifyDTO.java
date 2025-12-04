package com.coolcollege.intelligent.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * describe: 密码校验DTO
 *
 * @author wangff
 * @date 2024/11/8
 */
@ApiModel("密码校验DTO")
@Data
public class PasswordVerifyDTO {

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty("原密码")
    @NotBlank(message = "原密码不能为空")
    private String password;
}
