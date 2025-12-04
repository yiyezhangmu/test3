package com.coolcollege.intelligent.common.enums.boss;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统一状态枚举
 * @author xugk
 */

public enum StandardStateEnum {

    //删除
    DELETE("-1","删除"),
    //冻结
    FREEZE("0","禁用"),
    //正常
    NORMAL("1","正常");


    private static final Map<String, StandardStateEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(StandardStateEnum::getCode, Function.identity()));


    StandardStateEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }


    private String code;
    private String msg;


    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static StandardStateEnum getByCode(String code) {
        return MAP.get(code);
    }

}
