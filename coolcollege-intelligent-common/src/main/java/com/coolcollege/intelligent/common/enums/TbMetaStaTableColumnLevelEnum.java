package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 标准检查项等级
 * @author zhangnan
 * @date 2021-12-29 11:10
 */
public enum TbMetaStaTableColumnLevelEnum {

    /**
     * 标准
     */
    GENERAL("general", "一般"),

    /**
     * 红线
     */
    RED_LINE("redline", "红线"),

    /**
     * 重要
     */
    IMPORTANT("important", "重要"),

    /**
     * 不重要
     */
    UNIMPORTANT("unimportant", "不重要");

    TbMetaStaTableColumnLevelEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    private String value;

    private String desc;

    public String getValue() {
        return value;
    }
    public String getDesc() {
        return desc;
    }

    protected static final Map<String, TbMetaStaTableColumnLevelEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(TbMetaStaTableColumnLevelEnum::getValue, Function.identity()));

    public static String getDescByValue(String value) {
        return map.get(value).getDesc();
    }
}
