package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EnterpriseApi 应用异常码
 * @author xugk
 */

public enum EnterpriseApiErrorEnum {

    /**
     * 用户授权错误
     */
    USER_AUTH_ERROR("enterpriseapi.5001", "用户未授权"),
    INVALID_TOKEN("enterpriseapi.2001", "无效的token"),
    INVALID_TOKEN_NEW("801", "令牌（access_token）无效或者已超时"),
    ;


    protected static final Map<String, EnterpriseApiErrorEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(EnterpriseApiErrorEnum::getCode, Function.identity()));

    private String code;
    private String msg;

    EnterpriseApiErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static EnterpriseApiErrorEnum getByCode(String code) {
        return map.get(code);
    }
}
