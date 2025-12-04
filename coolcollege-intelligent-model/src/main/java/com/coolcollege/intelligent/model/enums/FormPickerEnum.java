package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 选人组件枚举
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/8/24 16:28
 */
public enum FormPickerEnum {

    /**
     * 任务状态
     */
    PERSON("person", "人"),
    POSITION("position", "岗位"),
    ;

    public static final Map<String, FormPickerEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(FormPickerEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    FormPickerEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FormPickerEnum getByCode(String code) {
        return map.get(code);
    }

}