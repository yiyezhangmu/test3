package com.coolcollege.intelligent.common.enums.newstore;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 新店状态
 * `status` varchar(64) NOT NULL COMMENT '新店状态：ongoing(进行中),completed(完成),failed(失败)',
 */
public enum NsStoreStatusEnum {
    /**
     * 进行中
     */
    ONGOING("ongoing", "进行中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "失败"),
    ;

    public static final Map<String, NsStoreStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(NsStoreStatusEnum::getCode, Function.identity()));

    private String code;
    private String desc;

    NsStoreStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NsStoreStatusEnum getByCode(String code) {
        return map.get(code);
    }
}
