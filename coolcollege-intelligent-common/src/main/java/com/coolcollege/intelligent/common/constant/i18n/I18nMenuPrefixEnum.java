package com.coolcollege.intelligent.common.constant.i18n;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单国际化的前缀值枚举
 */
public enum I18nMenuPrefixEnum {
    MENUS("menus"),
    BUTTON("button");

    private static final Map<String, I18nMenuPrefixEnum> map = Arrays.stream(values()).collect(Collectors.toMap(I18nMenuPrefixEnum::getValue, Function.identity()));

    private String value;

    I18nMenuPrefixEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static I18nMenuPrefixEnum parseValue(String value) {
        return map.get(value);
    }

}
