package com.coolcollege.intelligent.common.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 钉钉免登错误枚举
 * @author xugangkun
 */
public enum DingLoginErrorEnum {

    /**
     * 钉钉免登错误枚举
     */
    NONEXISTENT_TEMPORARY_AUTH_CODE(40078, "不存在的临时授权码"),
    INVALID_SSOCODE(41007, "无效的ssocode"),
    MISSING_TMP_AUTH_CODE(41026, "参数缺少临时授权码"),
    NONEXISTENT_AUTH_CODE(40079, "授权信息不存在"),
    CREATE_PERMANENT_AUTH_CODE_FAILED(40087, "创建永久授权码失败"),
    CREATE_AUTH_CODE_FAILED(40091, "创建持久授权码失败，需要用户重新授权")
    ;

    protected static final Map<Integer, DingLoginErrorEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DingLoginErrorEnum::getCode, Function.identity()));

    private Integer code;
    private String msg;

    DingLoginErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static DingLoginErrorEnum getByCode(Integer code) {
        return map.get(code);
    }
}
