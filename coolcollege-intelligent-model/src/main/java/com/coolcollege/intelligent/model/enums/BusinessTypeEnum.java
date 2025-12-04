package com.coolcollege.intelligent.model.enums;

/**
 * 企业字典业务类型枚举
 * @author xugk
 */

public enum BusinessTypeEnum {

    /**
     * 用户人事类型
     */
    USER_PERSONNEL_STATUS("user_personnel_status", "用户人事类型"),

    /**
     * 新店类型
     */
    NEW_STORE_TYPE("new_store_type", "新店类型");

    /**
     * 类型code
     */
    private String code;

    /**
     * 类型描述
     */
    private String message;

    BusinessTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }



}
