package com.coolcollege.intelligent.facade.enums;

import com.coolcollege.intelligent.model.enums.RoleSourceEnum;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CardDeptTypeEnum {
    //root path store
    STORE("store","STORE"),
    COMP("path","COMP"),
    HQ("root","HQ"),



    ;

    private String regionType;

    private String cardDeptType;

    CardDeptTypeEnum(String regionType, String cardDeptType) {
        this.regionType = regionType;
        this.cardDeptType = cardDeptType;
    }

    private static final Map<String, String> map = Arrays.stream(values()).collect(
            Collectors.toMap(CardDeptTypeEnum::getRegionType, CardDeptTypeEnum::getCardDeptType));

    public String getRegionType() {
        return regionType;
    }

    public String getCardDeptType() {
        return cardDeptType;
    }

    public static String getByRegionType(String code) {
        return map.get(code);
    }
}
