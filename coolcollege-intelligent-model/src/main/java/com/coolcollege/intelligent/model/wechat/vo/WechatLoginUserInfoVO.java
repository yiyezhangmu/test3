package com.coolcollege.intelligent.model.wechat.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: WechatLoginUserInfoVO
 * @Description:
 * @date 2024-09-25 14:19
 */
@Data
@Builder
public class WechatLoginUserInfoVO {

    @ApiModelProperty("微信的accessToken")
    private String wechatAccessToken;

    @ApiModelProperty("公众号的openid")
    private String openid;

    @ApiModelProperty("公众号的unionid")
    private String unionid;

    @ApiModelProperty("内部系统登录token")
    private String accessToken;

    @ApiModelProperty("是否绑定手机号")
    private Boolean isBand;

    public static WechatLoginUserInfoVO convert(JSONObject jsonObject) {
        return WechatLoginUserInfoVO.builder()
                .openid(jsonObject.getString("openid"))
                .build();
    }
}
