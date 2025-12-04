package com.coolcollege.intelligent.model.enums;

/**
 * @author zhangchenbiao
 * @FileName: SendUserTypeEnum
 * @Description: 发送消息用户类型
 * @date 2022-08-17 10:37
 */
public enum SendUserTypeEnum {

    CREATE_USER(0, "发起人"),
    HANDLER_USER(1, "处理人"),
    APPROVE_USER(2, "审批人"),
    CC_USER(3, "抄送人");

    private Integer code;

    private String message;

    SendUserTypeEnum(Integer code, String message) {
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
