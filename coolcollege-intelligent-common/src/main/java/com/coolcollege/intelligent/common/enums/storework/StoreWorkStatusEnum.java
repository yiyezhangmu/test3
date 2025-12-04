package com.coolcollege.intelligent.common.enums.storework;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 店务状态
 * 店务状态 进行中ongoing 停止stop
 */
public enum StoreWorkStatusEnum {
    /**
     * 进行中
     */
    ONGOING("ongoing", "进行中"),
    STOP("stop", "停止");

    public static final Map<String, StoreWorkStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(StoreWorkStatusEnum::getCode, Function.identity()));

    private String code;
    private String desc;

    StoreWorkStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StoreWorkStatusEnum getByCode(String code) {
        return map.get(code);
    }
}
