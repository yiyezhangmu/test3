package com.coolcollege.intelligent.common.sync.vo;


/**
 * 开通授权信息
 */
public class AuthInfo {

    private AuthCorpInfo auth_corp_info;

    private AuthUserInfo auth_user_info;

    private AuthAgentInfo auth_info;


    public AuthCorpInfo getAuth_corp_info() {
        return auth_corp_info;
    }

    public void setAuth_corp_info(AuthCorpInfo auth_corp_info) {
        this.auth_corp_info = auth_corp_info;
    }

    public AuthUserInfo getAuth_user_info() {
        return auth_user_info;
    }

    public void setAuth_user_info(AuthUserInfo auth_user_info) {
        this.auth_user_info = auth_user_info;
    }

    public AuthAgentInfo getAuth_info() {
        return auth_info;
    }

    public void setAuth_info(AuthAgentInfo auth_info) {
        this.auth_info = auth_info;
    }
}
