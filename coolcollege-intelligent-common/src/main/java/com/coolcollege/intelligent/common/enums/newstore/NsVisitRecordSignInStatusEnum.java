package com.coolcollege.intelligent.common.enums.newstore;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 新店拜访记录状态
 */
public enum NsVisitRecordSignInStatusEnum {

    /**
     * 进行中
     */
    NORMAL(1, "正常"),
    ABNORMAL(2, "异常"),
    ;

    public static final Map<Integer, NsVisitRecordSignInStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(NsVisitRecordSignInStatusEnum::getCode, Function.identity()));


    private Integer code;
    private String desc;

    NsVisitRecordSignInStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NsVisitRecordSignInStatusEnum getByCode(Integer code) {
        return map.get(code);
    }
}
