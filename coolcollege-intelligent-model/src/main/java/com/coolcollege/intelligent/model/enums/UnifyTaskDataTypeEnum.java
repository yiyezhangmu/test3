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
 * @date ：Created in 2020/12/15 14:29
 */
public enum UnifyTaskDataTypeEnum {

    DEFINE("DEFINE", "自定义检查表"),
    STANDARD("STANDARD", "标准检查表"),
    DISPLAY_PG("DISPLAY_PG", "陈列检查表"),
    STA_COLUMN("STA_COLUMN", "标准检查项"),
    TB_DISPLAY("TB_DISPLAY", "新陈列检查表"),
    TB_DISPLAY_QUICK_COLUMN("TB_DISPLAY_QUICK_COLUMN", "新陈列快捷检查项"),
    VISIT("VISIT", "拜访表"),
    AI("AI", "AI检查表"),
    ;

    private static final Map<String, UnifyTaskDataTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTaskDataTypeEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTaskDataTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTaskDataTypeEnum getByCode(String code) {
        return map.get(code);
    }

}
