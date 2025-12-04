package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EnterpriseUserActiveTypeEnum {
    /**
     * 激活
     */
    ACTIVE("active"),
    /**
     * 未激活
     */
    UN_ACTIVE("unactive");

    private static final Map<String, EnterpriseUserActiveTypeEnum> MAP = Arrays.stream(values()).collect(Collectors.toMap(EnterpriseUserActiveTypeEnum::getValue, Function.identity()));

    private String value;

    EnterpriseUserActiveTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EnterpriseUserActiveTypeEnum parseValue(String value) {
        return MAP.get(value);
    }
}
