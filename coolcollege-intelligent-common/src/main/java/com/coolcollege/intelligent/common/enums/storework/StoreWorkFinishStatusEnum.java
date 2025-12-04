package com.coolcollege.intelligent.common.enums.storework;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 完成状态
 */
public enum StoreWorkFinishStatusEnum {

    NO(0, "未完成"),
    YES(1, "已完成"),
    AI_ANALYZING(2, "AI分析中"),
    ;

    public static final Map<Integer, StoreWorkFinishStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(StoreWorkFinishStatusEnum::getCode, Function.identity()));


    private Integer code;
    private String desc;

    StoreWorkFinishStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StoreWorkFinishStatusEnum getByCode(Integer code) {
        return map.get(code);
    }
}
