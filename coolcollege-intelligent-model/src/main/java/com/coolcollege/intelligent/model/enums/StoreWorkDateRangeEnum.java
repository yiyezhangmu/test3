package com.coolcollege.intelligent.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 店务日期类型枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/5/23
 */
@Getter
@RequiredArgsConstructor
public enum StoreWorkDateRangeEnum {

    DAY("day", "按日"),

    WEEKDAY("weekday", "周几"),

    WEEK_OF_YEAR("weekOfYear", "第几周"),

    MONTH("month", "第几月"),

    ;

    /**
     * 类型
     */
    private final String type;

    /**
     * 描述
     */
    private final String msg;

    public static StoreWorkDateRangeEnum getByType(String type) {
        for (StoreWorkDateRangeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
