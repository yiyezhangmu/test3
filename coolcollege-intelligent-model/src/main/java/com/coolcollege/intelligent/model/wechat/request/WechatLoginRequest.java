package com.coolcollege.intelligent.model.wechat.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: WechatLoginRequest
 * @Description:
 * @date 2024-09-25 14:17
 */
@Data
public class WechatLoginRequest {

    @ApiModelProperty("公众号openId")
    @NotBlank(message = "openid不能为空")
    private String openid;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("短信验证码")
    private String smsCode;

}
