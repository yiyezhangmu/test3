package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2023-01-05 14:33
 */
public enum PersonTypeEnum {

    /**
     *
     */
    PERSON("person", "人"),


    POSITION("position", "职位"),

    USER_GROUP("userGroup", "分组"),

    ORGANIZATION("organization", "组织架构");

    public static final Map<String, PersonTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PersonTypeEnum::getType, Function.identity()));


    private final String type;


    private final String desc;

    PersonTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static PersonTypeEnum getByType(String type) {
        return map.get(type);
    }
}
