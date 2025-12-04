package com.coolcollege.intelligent.model.enums;

/**
 * @author zhangchenbiao
 * @FileName: SmsCodeTypeEnum
 * @Description: 短信验证码类型
 * @date 2021-07-20 9:47
 */
public enum SmsCodeTypeEnum {

    LOGIN("SMS_220325070","验证码登录", 10 * 60),
    FORGOT_PWD("SMS_220325070","忘记密码", 10 * 60),
    MODIFY_PWD("SMS_220325070","修改密码", 10 * 60),
    IMPROVE_INFO("SMS_220325070","完善用户信息", 10 * 60),
    USER_REGISTER("SMS_220325070","用户注册", 10 * 60),
    ENTERPRISE_REGISTER("SMS_220325070","企业注册", 10 * 60),

    /**
     * 酷店掌模板
     */
    LOGIN2("SMS_232163403","验证码登录", 10 * 60),

    LOGIN_INTERNATIONAL("SMS_474876096", "国际验证码登录", 10 * 60),
    ;

    private String templateCode;

    private String message;

    private int cacheSeconds;

    SmsCodeTypeEnum(String templateCode, String message, int cacheSeconds) {
        this.templateCode = templateCode;
        this.message = message;
        this.cacheSeconds = cacheSeconds;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getMessage() {
        return message;
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }
}
