package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: InviteUserRegisterDTO
 * @Description: 邀请用户注册
 * @date 2021-07-21 19:16
 */
@Data
public class InviteUserRegisterDTO {

    @NotBlank(message = "企业id不能为空")
    private String enterpriseId;

    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;

    private String email;

    @NotBlank(message = "分享key不能缺失")
    private String shareKey;

}
