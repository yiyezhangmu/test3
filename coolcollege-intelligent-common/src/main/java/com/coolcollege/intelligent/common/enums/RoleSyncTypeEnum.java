package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: RoleSyncTypeEnum
 * @Description:
 * @date 2024-09-14 11:35
 */
public enum RoleSyncTypeEnum {

    SYNC(1,"同步过来的"),
    CREATE(2,"手动创建的"),
    ;

    RoleSyncTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private Integer code;

    private String message;
}
