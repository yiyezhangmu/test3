package com.coolcollege.intelligent.common.enums.xfsg;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 区域类型 0-根;1=大区;2=战区;3=小区（督导区）;4=门店
 * @author wxp
 */
public enum XfsgRegionTypeEnum {

    ROOT("0","根"),
    LARGE_REGION("1","大区"),
    WAR_REGION("2","战区"),
    COMMUNITY_REGION("3","小区"),
    STORE("4","门店");

    private String code;
    private String desc;

    public static final Map<String, XfsgRegionTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(XfsgRegionTypeEnum::getCode, Function.identity()));

    XfsgRegionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static XfsgRegionTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
