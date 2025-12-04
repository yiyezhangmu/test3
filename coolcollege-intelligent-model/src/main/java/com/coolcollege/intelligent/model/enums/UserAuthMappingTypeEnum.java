package com.coolcollege.intelligent.model.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zyp
 */
public enum UserAuthMappingTypeEnum {
    /**
     * 区域
     */
    REGION("region","区域"),
    /**
     * 门店
     */
    STORE("store","门店");

    private String code;
    private String desc;
    public static final Map<String, UserAuthMappingTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UserAuthMappingTypeEnum::getCode, Function.identity()));

    UserAuthMappingTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserAuthMappingTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
