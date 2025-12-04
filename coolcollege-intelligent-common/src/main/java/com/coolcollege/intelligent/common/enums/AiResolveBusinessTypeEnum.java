package com.coolcollege.intelligent.common.enums;

public enum AiResolveBusinessTypeEnum {

    PATROL("patrol", "巡察"),
    STORE_WORK("storeWork", "店务"),
    DISPLAY("display", "陈列"),
    AI_REPORT("ai_report", "AI报表"),
    AI_INSPECTION("ai_inspection", "AI巡检"),

    ;
    private final String code;
    private final String message;

    AiResolveBusinessTypeEnum(String code, String message) {
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
