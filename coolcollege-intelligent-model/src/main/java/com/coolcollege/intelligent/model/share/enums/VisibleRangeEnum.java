package com.coolcollege.intelligent.model.share.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum VisibleRangeEnum {
    ALL_ENTERPRISE(0,"全公司可见"),
    TASK(1,"任务相关"),
    DESIGNATED_PERSON(2,"指定人可见");

    private static final Map<Integer,VisibleRangeEnum> map = Arrays.stream(values()).collect(Collectors.toMap(VisibleRangeEnum::getCode, data -> data));
    private Integer code;
    private String describe;

    VisibleRangeEnum(Integer code,String describe){
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }
    public static VisibleRangeEnum getVisibleRangeEnumByCode(Integer code){
        return map.get(code);
    }
}
