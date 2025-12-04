package com.coolcollege.intelligent.common.enums.store;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OnePartyStoreStatusEnum {

    /**
     * OPEN
     */
    OPEN("OPENING", "开启", "open"),
    /**
     * CLOSED
     */
    CLOSED("ds_default", "关闭", "closed"),
    /**
     * NOT_OPEN
     */
    NOT_OPEN("40000000", "未开启", "not_open"),
   ;


    private static final Map<String, OnePartyStoreStatusEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(OnePartyStoreStatusEnum::getCode, Function.identity()));

    private String code;
    private String name;
    private String roleEnum;


    OnePartyStoreStatusEnum(String code, String name, String roleEnum) {
        this.code = code;
        this.name = name;
        this.roleEnum=roleEnum;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getRoleEnum() {
        return roleEnum;
    }

    public static OnePartyStoreStatusEnum getByCode(String code) {
        return MAP.get(code);
    }

    public static String getEnumByCode(String code) {
        OnePartyStoreStatusEnum e = MAP.get(code);
        if(Objects.isNull(e)) {
            return null;
        }
        return e.getRoleEnum();
    }
}
