package com.coolcollege.intelligent.model.wechat.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: WechatSignatureVO
 * @Description:
 * @date 2023-10-17 15:01
 */
@Data
public class WechatSignatureVO {

    @ApiModelProperty("时间戳")
    private long timestamp;

    @ApiModelProperty("随机字符串")
    private String nonceStr;

    @ApiModelProperty("签名")
    private String signature;

    public WechatSignatureVO(long timestamp, String nonceStr, String signature) {
        this.timestamp = timestamp;
        this.nonceStr = nonceStr;
        this.signature = signature;
    }
}
