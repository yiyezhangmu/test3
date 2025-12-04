package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum YingShiMsgTypeEnum {

    ONOFFLINE("ys.onoffline", "上下线消息"),
    YS_OPEN_CLOUD("ys.open.cloud", "云录制消息"),

    ;

    private String code;
    private String msg;

    protected static final Map<String, YingShiMsgTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(YingShiMsgTypeEnum::getCode, Function.identity()));

    YingShiMsgTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static YingShiMsgTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
