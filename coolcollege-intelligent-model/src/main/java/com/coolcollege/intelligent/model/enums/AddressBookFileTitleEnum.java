package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通讯录文件标题枚举
 */
public enum AddressBookFileTitleEnum {
    ORDER_NUM("序号"),// 序号
    USER_ID("员工UserID"),// 用户ID
    MOBILE("手机号");// 手机号

    private final String value;

    private static final Map<String, AddressBookFileTitleEnum> map = Arrays.stream(values()).collect(Collectors.toMap(AddressBookFileTitleEnum::getValue, Function.identity()));

    AddressBookFileTitleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AddressBookFileTitleEnum parse(int value) {
        return map.get(value);
    }
}
