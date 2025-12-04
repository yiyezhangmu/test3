package com.coolcollege.intelligent.common.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 工单创建类型
 * @author byd
 */

public enum QuestionCreateTypeEnum {
    /**
     *
     */
    MANUAL (1,"手动"),
    /**
     *
     */
    AUTOMATIC (2,"自动");


    private final Integer code;
    private final String msg;

    QuestionCreateTypeEnum(Integer code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    protected static final Map<Integer, QuestionCreateTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(QuestionCreateTypeEnum::getCode, Function.identity()));

    public static String getMsgByCode(Integer code) {
        return map.get(code).getMsg();
    }
}
