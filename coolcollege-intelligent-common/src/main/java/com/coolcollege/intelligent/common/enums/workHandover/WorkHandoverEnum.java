package com.coolcollege.intelligent.common.enums.workHandover;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 交接内容模版
 *
 * @author byd
 */

public enum WorkHandoverEnum {
    /**
     * 任务类型
     */
    PATROL_STORE_ONLINE("PATROL_STORE_ONLINE", "线上巡店任务"),
    PATROL_STORE_OFFLINE("PATROL_STORE_OFFLINE", "线下巡店任务"),
    PATROL_STORE_PLAN("PATROL_STORE_PLAN", "巡店计划"),
    QUESTION_ORDER("QUESTION_ORDER", "工单"),
    DISPLAY_TASK("TB_DISPLAY_TASK", "陈列任务"),
    STORE_WORK("STORE_WORK", "店务"),
    ;

    public static final Map<String, WorkHandoverEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(WorkHandoverEnum::getCode, Function.identity()));


    private final String code;
    private final String desc;

    WorkHandoverEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static WorkHandoverEnum getByCode(String code) {
        return map.get(code);
    }

    public static String getNameByCode(String code) {
        WorkHandoverEnum workHandoverEnum = map.get(code);
        if (workHandoverEnum != null) {
            return workHandoverEnum.getDesc();
        }
        return null;
    }

}
