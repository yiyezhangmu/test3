package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 门店删除状态枚举
 */
public enum StoreIsDeleteEnum {
    EFFECTIVE("effective"),//有效

    INVALID("invalid"),//无效

    IGNORED("ignored"), //忽略

    UN_SYNC("unSync");   // 未同步

    private final String value;

    private static final Map<String, StoreIsDeleteEnum> map = Arrays.stream(values()).collect(Collectors.toMap(StoreIsDeleteEnum::getValue, Function.identity()));

    StoreIsDeleteEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StoreIsDeleteEnum parse(int value) {
        return map.get(value);
    }
}
