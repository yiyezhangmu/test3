package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: MentTypeEnum
 * @Description:
 * @date 2021-09-23 17:59
 */
public enum MenuTypeEnum {


    MENU(1,"菜单"),
    AUTH(2,"权限");

    private Integer code;

    private String message;

    MenuTypeEnum(Integer code, String message) {
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
