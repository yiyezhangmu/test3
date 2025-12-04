package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 邵凌志
 */
public enum OperateTypeEnum {

    /**
     * 操作类型
     */
    ADD("add", "新增"),
    UPDATE("update", "修改"),
    DELETE("delete", "删除"),
    TRANSMIT("transmit", "转发"),
    ;

    private static final Map<String, OperateTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(OperateTypeEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    OperateTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OperateTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
