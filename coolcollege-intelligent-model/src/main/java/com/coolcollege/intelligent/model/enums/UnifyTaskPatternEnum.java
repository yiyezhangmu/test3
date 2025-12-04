package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/8 14:57
 */
public enum UnifyTaskPatternEnum {

    /**
     * 任务类型
     */
    NORMAL("NORMAL", "常规任务"),
    WORKFLOW("WORKFLOW", "审批流任务"),
    ;

    private static final Map<String, UnifyTaskPatternEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskPatternEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTaskPatternEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTaskPatternEnum getByCode(String code) {
        return map.get(code);
    }

}
