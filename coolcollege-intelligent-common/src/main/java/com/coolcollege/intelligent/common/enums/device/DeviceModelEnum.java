package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wxp
 * 设备型号
 */
public enum DeviceModelEnum {

    ISAPI("DS-2XD", "ISAPI协议"),

    YINGSHI("C4X","萤石云客流协议");

    private static final Map<String, DeviceModelEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceModelEnum::getCode, Function.identity()));

    private String code;
    private String desc;

    DeviceModelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceModelEnum getByCode(String code) {
        return MAP.get(code);
    }
}
