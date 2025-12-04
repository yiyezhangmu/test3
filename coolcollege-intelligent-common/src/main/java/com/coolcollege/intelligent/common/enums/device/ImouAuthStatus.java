package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/05/06
 */
public enum ImouAuthStatus {

    /**
     * test
     */
    ACCEPT("accept", "接收"),
    REFUSE("refuse", "拒绝");



    private String code;
    private String msg;

    protected static final Map<String, ImouAuthStatus> map = Arrays.stream(values()).collect(
            Collectors.toMap(ImouAuthStatus::getCode, Function.identity()));

    ImouAuthStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ImouAuthStatus getByCode(String code) {
        return map.get(code);
    }

}
