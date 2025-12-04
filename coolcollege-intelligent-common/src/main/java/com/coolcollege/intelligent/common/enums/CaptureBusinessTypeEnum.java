package com.coolcollege.intelligent.common.enums;

/**
 *
 * @author byd
 * @date 2025-11-06 18:39
 */
public enum CaptureBusinessTypeEnum {

    AI_INSPECTION_CAPTURE("aiInspectionCapture","AI巡检抓拍"),
    ;

    private String code;

    private String message;

    CaptureBusinessTypeEnum(String code, String message) {
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
