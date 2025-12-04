package com.coolcollege.intelligent.model.login.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 明厨亮灶登录请求类
 * </p>
 *
 * @author wangff
 * @since 2025/7/14
 */
@Data
public class MclzLoginRequest {
    @ApiModelProperty("企业id")
    @NotBlank(message = "企业id不能为空")
    private String enterpriseId;

    @ApiModelProperty("手机授权凭证")
    private String mobileCode;

    @ApiModelProperty("openid")
    private String openid;

    @ApiModelProperty("临时授权码")
    @NotBlank(message = "临时授权码不能为空")
    private String code;

    @ApiModelProperty("appType")
    private String appType;
}
