package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: YesOrNoEnum
 * @Description:
 * @date 2024-05-27 16:55
 */
public enum YesOrNoEnum {

    YES(1, "是"),

    NO(0, "否");

    private Integer code;

    private String message;

    YesOrNoEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
