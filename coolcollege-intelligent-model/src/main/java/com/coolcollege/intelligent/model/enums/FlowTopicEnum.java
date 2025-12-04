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
 * @date ：Created in 2020/9/9 10:35
 */
public enum FlowTopicEnum {

    /**
     * 任务规则
     */
    RECEIVE_QUESTION("receive_question", "发起"),
    DO_QUESTION("do_question", "处理"),
    CLOSE_QUESTION("close_question", "关闭"),
    DEAL_TURN_QUESTION("deal_turn_question", "处理转交"),
    REVIEW_TURN_QUESTION("review_turn_question", "复核转交"),
            ;

    private static final Map<String, FlowTopicEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(FlowTopicEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    FlowTopicEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static FlowTopicEnum getByCode(String code) {
        return map.get(code);
    }

}