package com.coolcollege.intelligent.common.constant;

/**
 * 企业微信
 * @author 王晓鹏
 */
public enum QyNameReplaceEnum {

    USER("userName"),
    DEPARTMENT("departmentName");

    private  String type;

    public String getValue() {
        return type;
    }

    QyNameReplaceEnum(String type){
        this.type = type;
    }
}
