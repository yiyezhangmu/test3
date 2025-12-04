package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zyp
 */
public enum UserScopeEnum {
    /**
     * 全企业数据
     */
    ALL("ALL", "全企业数据"),
    /**
     * 钉钉角色
     */
    DING_ROLE("DING_ROLE", "钉钉角色");
    private String code;
    private String msg;

    protected static final Map<String, UserScopeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UserScopeEnum::getCode, Function.identity()));

    UserScopeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static UserScopeEnum getByCode(String code) {
        return map.get(code);
    }
}
