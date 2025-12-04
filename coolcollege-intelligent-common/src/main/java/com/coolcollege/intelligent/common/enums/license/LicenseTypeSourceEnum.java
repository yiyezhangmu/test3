package com.coolcollege.intelligent.common.enums.license;

/**
 * @author xuanfeng
 * @Description 证照类型来源枚举
 */
public enum LicenseTypeSourceEnum {
    /**
     * 门店证照类型
     */
    STORE("store", "门店证照类型"),

    /**
     * 人员证照类型
     */
    USER("user", "人员证照类型")
    ;


    /**
     * 来源
     */
    private String source;

    /**
     * 信息
     */
    private String msg;

    LicenseTypeSourceEnum(String source, String msg) {
        this.source = source;
        this.msg = msg;
    }

    public String getSource() {
        return source;
    }

    public String getMsg() {
        return msg;
    }

}
