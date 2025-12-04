package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangchenbiao
 * @FileName: ImproveUserInfoDTO
 * @Description: 完善用户信息
 * @date 2021-07-20 10:07
 */
@Data
public class ImproveUserInfoDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String smsCode;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户头像
     */
    private String avatar;

}
