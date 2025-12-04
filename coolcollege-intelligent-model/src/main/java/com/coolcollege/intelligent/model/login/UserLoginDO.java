package com.coolcollege.intelligent.model.login;

import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: UserLoginDO
 * @Description: 用户登录相关信息
 * @date 2021-07-16 11:41
 */
@Data
public class UserLoginDO {

    private String id;

    private String userId;

    private String mobile;

    private String password;

    private String unionid;

    private String appType;

    private String enterpriseUserMobile;

    public UserLoginDO(String id, String userId, String mobile, String password, String unionid, String appType) {
        this.id = id;
        this.userId = userId;
        this.mobile = mobile;
        this.password = password;
        this.unionid = unionid;
        this.appType = appType;
    }
}
