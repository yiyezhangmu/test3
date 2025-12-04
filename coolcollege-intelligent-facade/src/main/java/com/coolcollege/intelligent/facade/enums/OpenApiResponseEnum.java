package com.coolcollege.intelligent.facade.enums;


/**
 * @author byd
 */

public enum OpenApiResponseEnum {


    /**
     *
     */
    TIME_NOT_NULL(1000001, "时间范围不能为空"),
    TABLE_TYPE_NOT_NULL(1000002, "检查表类型不能为空"),
    ;
    private final int code;

    private final String message;

    OpenApiResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
