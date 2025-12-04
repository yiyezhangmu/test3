package com.coolcollege.intelligent.model.boss.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/4/8 10:37
 */
@ApiModel("修改用户密码相关实体")
@Data
public class BossUserChangePasswordDTO {

    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("旧密码")
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

}