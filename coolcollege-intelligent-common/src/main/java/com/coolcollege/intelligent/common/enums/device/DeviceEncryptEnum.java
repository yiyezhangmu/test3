package com.coolcollege.intelligent.common.enums.device;

public enum DeviceEncryptEnum {

    ON("on", "加密"),

    OFF("off", "解密");

    private String code;
    private String msg;
    DeviceEncryptEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
