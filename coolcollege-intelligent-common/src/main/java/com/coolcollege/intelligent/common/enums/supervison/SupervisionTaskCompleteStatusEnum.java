package com.coolcollege.intelligent.common.enums.supervison;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 督导任务完成状态
 */
public enum SupervisionTaskCompleteStatusEnum {

    NO(0, "未完成"),
    YES(1, "已完成"),
    OVERDUE_COMPLETION(3, "逾期完成"),
    ;

    public static final Map<Integer, SupervisionTaskCompleteStatusEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(SupervisionTaskCompleteStatusEnum::getCode, Function.identity()));


    private Integer code;
    private String desc;

    SupervisionTaskCompleteStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static SupervisionTaskCompleteStatusEnum getByCode(Integer code) {
        return map.get(code);
    }
}
