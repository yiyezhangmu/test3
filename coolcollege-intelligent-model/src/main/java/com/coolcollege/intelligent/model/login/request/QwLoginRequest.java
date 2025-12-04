package com.coolcollege.intelligent.model.login.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @date 2022-03-28 15:05
 */
@Data
public class QwLoginRequest {

    @ApiModelProperty(value = "企微企业id", required = true)
    private String corpId;

    private String loginType;

    private String loginWay;

    /**
     * 企微换取用户信息code
     */
    private String code;
}
