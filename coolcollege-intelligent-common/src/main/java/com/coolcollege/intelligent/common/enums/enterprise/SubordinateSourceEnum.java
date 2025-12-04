package com.coolcollege.intelligent.common.enums.enterprise;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @FileName: SubordinateSourceEnum
 * @Description: auto自动关联 select手动选择
 * @date 2022-12-30 17:12
 */
public enum SubordinateSourceEnum {

    AUTO("auto", "关联区域门店权限"),
    SELECT("select", "手动选择");

    private String code;

    private String msg;

    protected static final Map<String, SubordinateSourceEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(SubordinateSourceEnum::getCode, Function.identity()));

    SubordinateSourceEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static SubordinateSourceEnum getByCode(String code) {
        return map.get(code);
    }
}
