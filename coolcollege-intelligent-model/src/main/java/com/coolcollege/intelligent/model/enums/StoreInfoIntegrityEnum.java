package com.coolcollege.intelligent.model.enums;

/**
 * 门店信息完整度
 * @author xugk
 */
public enum StoreInfoIntegrityEnum {

    /**
     * 是否完善
     */
    PERFECT("1", "已完善"),
    IMPERFECT("2", "未完善"),
    NOT_SET("3", "未设置"),
    ;

    private String code;
    private String desc;

    StoreInfoIntegrityEnum(String code, String desc) {
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
