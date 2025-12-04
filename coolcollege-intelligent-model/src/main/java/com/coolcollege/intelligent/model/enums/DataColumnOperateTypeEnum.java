package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * wxp
 */
public enum DataColumnOperateTypeEnum {

    /**
     * 操作类型 操作类型 submit提交 appeal申诉
     */
    SUBMIT("submit", "提交"),
    APPEAL("appeal", "申诉")
    ;

    private static final Map<String, DataColumnOperateTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DataColumnOperateTypeEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    DataColumnOperateTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DataColumnOperateTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
