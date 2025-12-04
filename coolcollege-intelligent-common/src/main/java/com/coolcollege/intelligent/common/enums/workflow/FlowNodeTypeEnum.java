package com.coolcollege.intelligent.common.enums.workflow;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2024-04-16 14:39
 */
public enum FlowNodeTypeEnum {
    NODE_NORMAL("normal"),
    NODE_SWITCH("switch");

    public static final Map<String, FlowNodeTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(FlowNodeTypeEnum::getValue, Function.identity()));

    private String value;

    private FlowNodeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static FlowNodeTypeEnum parse(String value) {
        return StringUtils.isEmpty(value) ? null :  map.get(value);
    }
}
