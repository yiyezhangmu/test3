package com.coolcollege.intelligent.common.enums.position;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PositionTypeEnum {
    /**
     * 岗位组
     */
    POST_GROUP("post_group"),
    /**
     * 岗位
     */
    POST("post");

    private static final Map<String, PositionTypeEnum> MAP = Arrays.stream(values()).collect(Collectors.toMap(PositionTypeEnum::getValue, Function.identity()));

    private String value;

    PositionTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PositionTypeEnum parseValue(String value) {
        return MAP.get(value);
    }
}
