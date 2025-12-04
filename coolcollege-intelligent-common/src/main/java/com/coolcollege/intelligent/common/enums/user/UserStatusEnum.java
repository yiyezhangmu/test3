package com.coolcollege.intelligent.common.enums.user;

/**
 * @author zhangchenbiao
 * @FileName: UserStatusEnum
 * @Description: 用户状态枚举
 * @date 2021-07-19 16:27
 */
public enum UserStatusEnum {
    //用户状态 0待审核 1正常 2冻结
    WAIT_AUDIT(0,"待审核"),
    NORMAL(1,"正常"),
    FREEZE(2, "冻结")
    ;

    private Integer code;

    private String message;

    UserStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public  static String getMessageByCode(Integer code){
        for(UserStatusEnum Enum: UserStatusEnum.values()){
            if(Enum.code.equals(code)){
                return Enum.message;
            }
        }
        return null;
    }

    public  static Integer getCodeByMessage(String message){
        for(UserStatusEnum Enum: UserStatusEnum.values()){
            if(Enum.message.equals(message)){
                return Enum.code;
            }
        }
        return null;
    }

}
