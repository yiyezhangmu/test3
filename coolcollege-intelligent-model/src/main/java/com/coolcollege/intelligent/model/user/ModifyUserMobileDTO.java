package com.coolcollege.intelligent.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: ModifyUserMobileDTO
 * @Description: 修改手机号
 * @date 2021-07-21 17:11
 */
@Data
public class ModifyUserMobileDTO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "验证码不能为空")
    private String smsCode;

}
