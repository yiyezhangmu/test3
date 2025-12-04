package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseStatusEnum
 * @Description:
 * @date 2021-09-17 15:53
 */
public enum EnterpriseStatusEnum {

    /**
     * 状态-1 已删除  0初始  1正常  100冻结  88创建失败
     */

    DELETED(-1,"已删除"),
    INIT(0,"初始"),
    NORMAL(1,"正常"),
    FREEZE(100,"冻结"),
    CREATE_FAIL(88,"创建失败"),
    ;


    private int code;
    private String message;

    EnterpriseStatusEnum(int code, String message) {
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
        for (EnterpriseStatusEnum value : EnterpriseStatusEnum.values()) {
            if(code.equals(value.code)){
                return value.message;
            }
        }
        return "";
    }
}
