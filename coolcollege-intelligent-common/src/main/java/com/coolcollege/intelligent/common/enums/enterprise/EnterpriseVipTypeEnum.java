package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseVipTypeEnum
 * @Description: \
 * @date 2021-09-17 15:57
 */
public enum EnterpriseVipTypeEnum {

    NORMAL(1,"普通用户"),
    PAY(2,"付费用户"),
    TRIAL(3,"试用用户"),
    CREATE(4,"共创用户"),
    ;

    /**
     * 用户类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */

    private int code;

    private String message;

    EnterpriseVipTypeEnum(int code, String message) {
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
        for (EnterpriseVipTypeEnum value : EnterpriseVipTypeEnum.values()) {
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
        for (EnterpriseVipTypeEnum value : EnterpriseVipTypeEnum.values()) {
            if(message.equals(value.message)){
                return value.code;
            }
        }
        return 1;
    }
}
