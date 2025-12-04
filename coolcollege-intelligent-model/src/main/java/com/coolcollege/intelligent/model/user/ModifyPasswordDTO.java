package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: ModifyPasswordDTO
 * @Description: 修改密码
 * @date 2021-07-20 14:23
 */
@Data
public class ModifyPasswordDTO {

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

}
