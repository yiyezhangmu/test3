package com.coolcollege.intelligent.model.enums;

/**
 * @author Admin
 * @date 2021-07-16 10:41
 */
public enum LoginTypeEnum {

    PASSWORD("账号密码", "passwordLoginService"),
    SMS("短信验证码","smsLoginService"),
    DEFAULT_TYPE("默认登录方式","defaultLoginService"),
    SWITCH_TYPE("切换账号登录方式","switchLoginService"),
    ;

    private String message;

    private String clazzName;

    LoginTypeEnum(String message, String clazzName) {
        this.message = message;
        this.clazzName = clazzName;
    }

    public String getMessage() {
        return message;
    }

    public String getClazzName() {
        return clazzName;
    }
}
