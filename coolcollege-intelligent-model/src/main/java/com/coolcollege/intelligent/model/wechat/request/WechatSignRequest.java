package com.coolcollege.intelligent.model.wechat.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangchenbiao
 * @FileName: WechatSignRequest
 * @Description:
 * @date 2023-10-25 14:04
 */
@Data
public class WechatSignRequest {

    @NotBlank
    @ApiModelProperty("appId")
    private String appId;

    @NotBlank
    @ApiModelProperty("url")
    private String url;

}
