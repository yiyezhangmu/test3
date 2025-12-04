package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @FileName: UserSelectRangeEnum
 * @Description: 用户选取范围 共同编辑人范围：self-仅自己，all-全部人员，define-自定义
 * @date 2022-12-30 17:12
 */
public enum UserSelectRangeEnum {

    SELF("self", "仅自己"),
    ALL("all", "全部人员"),
    DEFINE("define", "自定义");

    private String code;

    private String msg;

    protected static final Map<String, UserSelectRangeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UserSelectRangeEnum::getCode, Function.identity()));

    UserSelectRangeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static UserSelectRangeEnum getByCode(String code) {
        return map.get(code);
    }
}
