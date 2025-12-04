package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DeviceBindEnum {
    /**
     *
     */
    BIND("bind", "绑定"),

    /**
     *
     */
    UNBIND("unbind", "未绑定");


    private static final Map<String, DeviceBindEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceBindEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    DeviceBindEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceBindEnum getByCode(String code) {
        return MAP.get(code);
    }

}
