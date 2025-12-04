package com.coolcollege.intelligent.common.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 */

public enum UserRangeTypeEnum {

    /**
     *
     */
    SELF("self", "仅自己"),


    ALL("all", "全部人员"),


    PART("part", "部分人员");

    public static final String ALL_USER_ID = "all_user_id";

    public static final Map<String, UserRangeTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UserRangeTypeEnum::getType, Function.identity()));


    private final String type;


    private final String desc;

    UserRangeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static UserRangeTypeEnum getByType(String type) {
        return map.get(type);
    }

}
