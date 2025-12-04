package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 门店锁定状态枚举
 */
public enum StoreIsLockEnum {
    LOCKED("locked", "锁定"),//有效

    NOT_LOCKED("not_locked", "未锁定");//无效

    private final String value;
    private final String name;

    private static final Map<String, StoreIsLockEnum> map = Arrays.stream(values()).collect(Collectors.toMap(StoreIsLockEnum::getValue, Function.identity()));

    StoreIsLockEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }
    public String getName() {
        return name;
    }

    public static StoreIsLockEnum parse(String value) {
        return map.get(value);
    }
}
