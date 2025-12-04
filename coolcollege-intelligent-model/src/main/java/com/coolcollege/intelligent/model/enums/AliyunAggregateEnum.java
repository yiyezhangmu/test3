package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/11 10:25
 */
public enum AliyunAggregateEnum {

    ALL("all", "全部"),
    DAY("day", "按天分"),
    EACH("each", "全部，按类型分"),
    ;

    private static final Map<String, AliyunAggregateEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AliyunAggregateEnum::getCode, Function.identity()));

    AliyunAggregateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AliyunAggregateEnum getByCode(String code) {
        return map.get(code);
    }
}
