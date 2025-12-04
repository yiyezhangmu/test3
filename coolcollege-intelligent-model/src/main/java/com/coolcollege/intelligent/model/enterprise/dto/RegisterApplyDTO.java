package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 企业注册申请dto
 * @author ：xugangkun
 * @date ：2021/7/19 17:25
 */
@Data
public class RegisterApplyDTO {
    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;
    /**
     * 申请用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String applyUserName;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 申请用户手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String smsCode;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 企业类型
     */
    private String appType;
}
