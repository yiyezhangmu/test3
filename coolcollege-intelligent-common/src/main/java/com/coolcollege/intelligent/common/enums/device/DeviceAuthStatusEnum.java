package com.coolcollege.intelligent.common.enums.device;

public enum DeviceAuthStatusEnum {

    NO_AUTH(0, "未授权"),
    AUTH(1, "授权中"),
    CANCEL_AUTH(2, "取消授权"),
    ;

    private Integer code;

    private String message;

    DeviceAuthStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DeviceAuthStatusEnum getStatusEnumByCode(Integer code) {
        for (DeviceAuthStatusEnum value : DeviceAuthStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
