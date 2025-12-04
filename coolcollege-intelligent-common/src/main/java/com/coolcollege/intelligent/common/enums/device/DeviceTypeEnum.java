package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备类型
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/6/25 15:53
 */
public enum DeviceTypeEnum {

    /**
     * b1设备
     */
    DEVICE_B1("b1", "b1"),
    /**
     * 摄像头类型
     */
    DEVICE_VIDEO("video", "摄像头");

    private static final Map<String, DeviceTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceTypeEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    DeviceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DeviceTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
