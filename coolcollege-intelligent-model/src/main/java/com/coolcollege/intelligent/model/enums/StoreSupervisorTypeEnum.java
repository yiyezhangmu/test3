package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 门店人员类型
 */
public enum StoreSupervisorTypeEnum {
    OPERATOR("operator"),//运营

    SHOPOWNER("shopowner"),//店长

    CLERK("clerk"),// 店员

    ADMIN("admin"), //主管

    ORDINARYUSER("ordinaryUsers"),//普通员工

    INSPECTOR("inspector");//总监

    private final String value;

    private static final Map<String, StoreSupervisorTypeEnum> map = Arrays.stream(values()).collect(Collectors.toMap(StoreSupervisorTypeEnum::getValue, Function.identity()));

    StoreSupervisorTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StoreSupervisorTypeEnum parse(String value) {
        return map.get(value);
    }
}
