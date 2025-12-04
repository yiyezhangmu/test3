package com.coolcollege.intelligent.model.enums;

public enum LoginWayEnum {

    PC("pc", "pc端"),
    MOBILE("mobile","移动端")
    ;


    private String code;

    private String desc;

    LoginWayEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
