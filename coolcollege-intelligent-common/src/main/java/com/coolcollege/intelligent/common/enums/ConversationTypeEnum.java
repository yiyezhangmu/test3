package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: NodeTypeEnum
 * @Description:
 * @date 2023-04-10 14:50
 */
public enum ConversationTypeEnum {

    other("other","其他群"),
    region("region","分子公司群"),
    store("store","门店群"),
    ;

    private String code;

    private String message;

    ConversationTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
