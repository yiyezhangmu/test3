package com.coolcollege.intelligent.common.enums;

/**
 * @author zhangchenbiao
 * @FileName: NodeTypeEnum
 * @Description:
 * @date 2023-04-10 14:50
 */
public enum NodeTypeEnum {

    HQ("HQ","总部"),
    COMP("COMP","分子公司"),
    STORE("STORE","门店"),
    ;

    private String code;

    private String message;

    NodeTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static NodeTypeEnum getNodeType(String code){
        for (NodeTypeEnum value : values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }

}
