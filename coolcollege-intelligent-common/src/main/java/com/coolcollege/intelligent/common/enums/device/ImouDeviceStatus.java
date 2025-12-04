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
public enum ImouDeviceStatus {

    /**
     * test
     */
    ONLINE("online", "在线"),

    OFFLINE("offline", "离线"),

    SLEEP("sleep", "休眠"),

    UPGRADING("upgrading", "升级中");

    private String code;
    private String msg;

    protected static final Map<String, ImouDeviceStatus> map = Arrays.stream(values()).collect(
            Collectors.toMap(ImouDeviceStatus::getCode, Function.identity()));

    ImouDeviceStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ImouDeviceStatus getByCode(String code) {
        return map.get(code);
    }

}
