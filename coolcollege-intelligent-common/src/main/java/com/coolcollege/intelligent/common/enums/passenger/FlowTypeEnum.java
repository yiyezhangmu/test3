package com.coolcollege.intelligent.common.enums.passenger;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 客流统计数据类型
 * @author zhouyiping
 */
public enum FlowTypeEnum {
    /**
     * 小时数据
     */
    HOUR("hour","小时数据"),
    /**
     * 按天数据
     */
    DAY("day","按天数据"),
    /**
     * 按周数据
     */
    WEEK("week","按周数据"),

    /**
     * 按月数据
     */
    MONTH("month","按月数据");


    private String code;
    private String msg;

    protected static final Map<String, FlowTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(FlowTypeEnum::getCode, Function.identity()));

    FlowTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static FlowTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
