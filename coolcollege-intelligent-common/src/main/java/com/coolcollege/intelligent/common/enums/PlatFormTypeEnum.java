package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
public enum  PlatFormTypeEnum {

    /**
     * 菜单类型
     */
    PC("PC","pc端菜单"),
    APP("APP","app端菜单"),
    NEW_APP("NEW_APP","新的app端菜单");

    private String code;
    private String msg;

    protected static final Map<String, PlatFormTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PlatFormTypeEnum::getCode, Function.identity()));

    PlatFormTypeEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static PlatFormTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
