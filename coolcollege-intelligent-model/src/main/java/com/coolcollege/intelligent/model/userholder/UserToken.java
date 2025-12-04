package com.coolcollege.intelligent.model.userholder;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-12 21:11
 */
@Data
public class UserToken {

    /**
     * 登录短token，有效期四小时
     */
    @JSONField(name = "action_token")
    private String actionToken;

    /**
     * 登录长token，有效期七天
     */
    @JSONField(name = "long_token")
    private String longToken;

    /**
     * token有效期
     */
    private Integer expire;

    /**
     * 登录用户信息
     */
    private CurrentUser user;

    @JSONField(name = "open_id")
    private String openId;
}
