package com.coolcollege.intelligent.common.enums.storework;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/9/8 15:17
 * @Version 1.0
 */
public enum StoreWorkCycleEnum {

    DAY("DAY","日清"),

    WEEK("WEEK","周清"),

    MONTH("MONTH","月清");

    private static final Map<String, String> map = Arrays.stream(values()).collect(
            Collectors.toMap(StoreWorkCycleEnum::getCode, StoreWorkCycleEnum::getMessage));

    private String code;

    private String message;

    StoreWorkCycleEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String getByCode(String code) {
        return map.get(code);
    }
}
