package com.coolcollege.intelligent.common.enums.storework;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 点评状态
 */
public enum StoreWorkCommentStatusEnum {

    NO(0, "未点评"),
    YES(1, "已点评"),
    ;

    public static final Map<Integer, StoreWorkCommentStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(StoreWorkCommentStatusEnum::getCode, Function.identity()));


    private Integer code;
    private String desc;

    StoreWorkCommentStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static StoreWorkCommentStatusEnum getByCode(Integer code) {
        return map.get(code);
    }
}
