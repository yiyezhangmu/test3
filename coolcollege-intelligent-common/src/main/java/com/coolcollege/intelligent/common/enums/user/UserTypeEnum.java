package com.coolcollege.intelligent.common.enums.user;

/**
 * @author zhangchenbiao
 * @FileName: UserTypeEnum
 * @Description: 用户类型枚举
 * @date 2023-09-27 16:27
 */
public enum UserTypeEnum {

    INTERNAL_USER(0, "内部员工"),
    EXTERNAL_USER(1, "外部员工"),
    ;

    private int code;

    private String message;

    UserTypeEnum(int code, String message) {
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
