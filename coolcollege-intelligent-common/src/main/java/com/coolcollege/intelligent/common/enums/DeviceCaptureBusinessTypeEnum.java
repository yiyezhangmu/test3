package com.coolcollege.intelligent.common.enums;

public enum DeviceCaptureBusinessTypeEnum {

    AI_INSPECTION("aiInspection", "AI巡检"),

    ;
    private final String code;
    private final String message;

    DeviceCaptureBusinessTypeEnum(String code, String message) {
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
