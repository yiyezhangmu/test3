package com.coolcollege.intelligent.model.wechat.request;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: huhu
 * @Date: 2024/9/25 17:43
 * @Description: 微信公众号消息
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WechatMessageRequest {

    @ApiModelProperty("appId")
    @NotBlank(message = "appId不能为空")
    private String appId;

    @ApiModelProperty("接收人openid")
    @NotBlank(message = "接收人不能为空")
    private String touser;

    @ApiModelProperty("模板id")
    @NotBlank(message = "模板不能为空")
    private String template_id;

    @ApiModelProperty("跳转地址")
    private String url;

    @ApiModelProperty("跳小程序所需数据")
    private MiniProgramRequest miniprogram;

    @ApiModelProperty("消息id")
    @NotBlank(message = "消息id不能为空")
    private String client_msg_id;

    @ApiModelProperty("消息内容")
    @NotNull(message = "消息内容不能为空")
    private JSONObject data;
}
