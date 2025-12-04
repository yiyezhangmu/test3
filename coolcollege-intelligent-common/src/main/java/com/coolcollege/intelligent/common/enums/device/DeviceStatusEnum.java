package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zyp
 * 设备状态
 */
public enum DeviceStatusEnum {
    /**
     * 离线
     */
    OFFLINE("offline", "离线"),
    /**
     * 在线
     */
    ONLINE("online","在线");



    private static final Map<String, DeviceStatusEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceStatusEnum::getCode, Function.identity()));

    private String code;
    private String desc;

    DeviceStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceStatusEnum getByCode(String code) {
        return MAP.get(code);
    }
}
