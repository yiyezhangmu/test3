package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统一任务审批过程监听key
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/14 15:59
 */
public enum UnifyTaskActionEnum {

    PASS("pass", "通过"),
    REJECT("reject", "拒绝"),
    TURN("turn", "转交"),
    ;

    private static final Map<String, UnifyTaskActionEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskActionEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTaskActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTaskActionEnum getByCode(String code) {
        return map.get(code);
    }

}
