package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DeviceSceneEnum {
    /**
     *店外客流
     */
    STORE_OUTSIDE_PASSENGER_FLOW("store_outside_passenger_flow", "店外客流"),

    /**
     *店内客流
     */
    STORE_INSIDE_PASSENGER_FLOW("STORE_INSIDE_PASSENGER_FLOW", "店内客流"),

    /**
     * 试衣间客流
     */
    FITTING_ROOM_PASSENGER_FLOW("FITTING_ROOM_PASSENGER_FLOW","试衣间客流"),

    /**
     * 其他
     */
    OTHER("other","其他");
    private static final Map<String, DeviceSceneEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceSceneEnum::getCode, Function.identity()));
    private static final Map<String, DeviceSceneEnum> descMap = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceSceneEnum::getDesc, Function.identity()));


    private String code;
    private String desc;

    DeviceSceneEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceSceneEnum getByCode(String code) {
        return MAP.get(code);
    }

    public static DeviceSceneEnum getByDesc(String desc){
        return descMap.get(desc);
    }

}
