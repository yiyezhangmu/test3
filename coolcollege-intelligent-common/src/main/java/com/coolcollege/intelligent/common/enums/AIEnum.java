package com.coolcollege.intelligent.common.enums;

public enum  AIEnum {

    AI_NAME("AI用户"),
    AI_USERID("a100000001"),
    AI_ID("a100000000"),
    AI_DEPARTMENT("[1]"),
    AI_ROLES("20000000"),
    AI_UUID("a100000002"),
    AI_MOBILE("AIAdminUser");



    private void setCode(String code) {
        this.code = code;
    }

    private String code;
    AIEnum(String code){
        this.code=code;
    }
    public String getCode() {
        return code;
    }
}
