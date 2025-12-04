package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: AuthLevelEnum
 * @Description: 企业认证等级
 * @date 2021-09-17 15:47
 */
public enum AuthLevelEnum {

    NO_AUTH(0,"未认证"),
    SENIOR_AUTH(1,"高级认证"),
    MIDDLE_AUTH(2,"中级认证"),
    PRIMARY_AUTH(3,"初级认证")
    ;

    /**
     * 企业认证等级，0：未认证，1：高级认证，2：中级认证，3：初级认证
     */

    private int code;
    private String message;

    AuthLevelEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(Integer code){
        if(Objects.isNull(code)){
            return "";
        }
        for (AuthLevelEnum value : AuthLevelEnum.values()) {
            if(code.equals(value.code)){
                return value.message;
            }
        }
        return "";
    }

    public static Integer getCode(String message){
        if(Objects.isNull(message)){
            return 1;
        }
        for (AuthLevelEnum value : AuthLevelEnum.values()) {
            if(message.equals(value.message)){
                return value.code;
            }
        }
        return 1;
    }
}
