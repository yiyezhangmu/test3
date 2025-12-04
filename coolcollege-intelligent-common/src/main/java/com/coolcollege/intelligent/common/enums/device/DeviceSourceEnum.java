package com.coolcollege.intelligent.common.enums.device;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/14
 */
public enum  DeviceSourceEnum {

    /**
     * 阿里云
     */
    ALIYUN("ali","阿里云"),

    /**
     * 宇视云
     */
    YUSHIYUN("yushi","宇视云"),

    /**
     * 萤石云
     */
    YINGSHIYUN("yingshi","萤石云"),


    /**
     * 萤石云国标
     */
    YINGSHIYUN_GB("yingshi_gb","国标"),

    HIKCLOUD("hikcloud","海康云眸"),


    /**
     * 钉钉B1
     */
    DINGDING_B1("dingding_b1","钉钉");


    private String code;
    private String msg;

    protected static final Map<String, DeviceSourceEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DeviceSourceEnum::getCode, Function.identity()));

    DeviceSourceEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static DeviceSourceEnum getByCode(String code) {
        return map.get(code);
    }

}
