package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 检查项枚举
 *
 * @author 邵凌志
 */
public enum CheckItemEnum {

    /**
     * 巡店模板
     */
    ON_APPLY("unapplicable", "不适用"),
    FAILED("failed", "不合格"),
    PASS("pass", "合格"),
    ;

    private static final Map<String, CheckItemEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(CheckItemEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    CheckItemEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CheckItemEnum getByCode(String code) {
        return map.get(code);
    }
}
