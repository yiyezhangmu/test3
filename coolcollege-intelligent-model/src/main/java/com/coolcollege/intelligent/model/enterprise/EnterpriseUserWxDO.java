package com.coolcollege.intelligent.model.enterprise;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-09-25 02:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseUserWxDO implements Serializable {
    @ApiModelProperty("用户主键id")
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户的唯一标识")
    private String openid;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private String sex;

    @ApiModelProperty("用户个人资料填写的省份")
    private String province;

    @ApiModelProperty("普通用户个人资料填写的城市")
    private String city;

    @ApiModelProperty("国家，如中国为CN")
    private String country;

    @ApiModelProperty("用户头像")
    private String headimgurl;

    @ApiModelProperty("只有在用户将公众号绑定到微信开放平台账号后，才会出现该字段。")
    private String unionid;
}