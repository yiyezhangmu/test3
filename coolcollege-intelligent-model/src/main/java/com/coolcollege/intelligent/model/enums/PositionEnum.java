package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/13 15:09
 */
public enum PositionEnum {

    /**
     * 岗位信息
     */
    OPERATOR("operator", "运营"),
    CLERK("clerk", "店员"),
    SHOPOWNER("shopowner", "门店店长"),
    ;

    private static final Map<String, PositionEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PositionEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    PositionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PositionEnum getByCode(String code) {
        return map.get(code);
    }
}
