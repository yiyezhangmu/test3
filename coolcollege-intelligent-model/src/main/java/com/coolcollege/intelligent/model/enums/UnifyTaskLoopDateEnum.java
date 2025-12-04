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
 * @date ：Created in 2020/12/8 15:04
 */
public enum UnifyTaskLoopDateEnum {

    /**
     * 任务类型
     */
    DAY("DAY", "日"),
    WEEK("WEEK", "周"),
    MONTH("MONTH", "月"),
    QUARTER("QUARTER", "季"),
    HOUR("HOUR", "小时"),
    ;

    private static final Map<String, UnifyTaskLoopDateEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskLoopDateEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTaskLoopDateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTaskLoopDateEnum getByCode(String code) {
        return map.get(code);
    }

}