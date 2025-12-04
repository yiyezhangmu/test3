package com.coolcollege.intelligent.common.constant.i18n;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 国际化语言枚举类型
 */
public enum I18nLangTypeEnum {

    EN_US("en_us"),// 英语-美国
    ZH_CN("zh_cn"),// 中文-简体
    ZH_HK("zh_hk");// 中文-繁体-香港


    private static final Map<String, I18nLangTypeEnum> map = Arrays.stream(values()).collect(Collectors.toMap(I18nLangTypeEnum::getValue, Function.identity()));

    private static final Map<String, String> mapChinese = Maps.newHashMap();

    static {
        mapChinese.put(EN_US.value, "English");
        mapChinese.put(ZH_CN.value, "中文-简体");
        mapChinese.put(ZH_HK.value, "中文-繁體");
    }

    private String value;

    I18nLangTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static I18nLangTypeEnum parseValue(String value) {
        return map.get(value);
    }

    public static Map<String, String> getI18nLangTypesMap() {
        return mapChinese;
    }

}
