package com.coolcollege.intelligent.model.login.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 果然AskBot登录请求类
 * </p>
 *
 * @author wxp
 * @since 2025/8/13
 */
@Data
public class AskBotLoginRequest {
    @ApiModelProperty("企业id")
    @NotBlank(message = "企业id不能为空")
    private String enterpriseId;

    @ApiModelProperty("用户ID")
    @NotBlank(message = "用户ID不能为空")
    private String userId;
}
