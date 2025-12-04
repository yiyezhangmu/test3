package com.coolcollege.intelligent.common.enums.position;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PositionSourceEnum {
    /**
     * 自建
     */
    CREATE("create"),
    /**
     * 同步角色
     */
    SYNC("sync"),
    /**
     * 同步职位
     */
    SYNC_POSITION("sync_position"),

    EHR("ehr"),
    /**
     * 自定义
     */
    CUSTOM("custom");
    private static final Map<String, PositionSourceEnum> MAP = Arrays.stream(values()).collect(Collectors.toMap(PositionSourceEnum::getValue, Function.identity()));

    private String value;

    PositionSourceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PositionSourceEnum parseValue(String value) {
        return MAP.get(value);
    }
}
