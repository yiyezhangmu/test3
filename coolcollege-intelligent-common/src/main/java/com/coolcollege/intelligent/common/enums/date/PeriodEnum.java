package com.coolcollege.intelligent.common.enums.date;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 时间周期枚举
 *
 * @author 邵凌志
 */
public enum PeriodEnum {

    /**
     * 操作类型
     */
    MONTH("month", "近一月"),
    WEEK("week", "近一周"),
    THREE("three", "近三天"),
    ONE("ONE", "近一天"),
    TODAY("today", "今日"),
    ;

    private static final Map<String, PeriodEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(PeriodEnum::getCode, Function.identity()));

    private String code;
    private String desc;

    PeriodEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PeriodEnum getByCode(String code) {
        return MAP.get(code);
    }
}
