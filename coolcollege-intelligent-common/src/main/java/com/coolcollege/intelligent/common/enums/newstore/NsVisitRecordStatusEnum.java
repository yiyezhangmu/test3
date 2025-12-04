package com.coolcollege.intelligent.common.enums.newstore;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 新店拜访记录状态
 */
public enum NsVisitRecordStatusEnum {

    /**
     * 进行中
     */
    ONGOING("ongoing", "进行中"),
    COMPLETED("completed", "已完成"),
    ;

    public static final Map<String, NsVisitRecordStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(NsVisitRecordStatusEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    NsVisitRecordStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NsVisitRecordStatusEnum getByCode(String code) {
        return map.get(code);
    }
}
