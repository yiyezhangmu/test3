package com.coolcollege.intelligent.common.enums.video;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AlarmTypeEnum {

    /**
     * 区域入侵
     */
    REGION_INTRUSION("region_intrusion","区域入侵"),
    /**
     * 排队告警
     */
    WAITING_ALARM("waiting_alarm","排队告警");

    private String code;
    private String msg;

    protected static final Map<String, AlarmTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AlarmTypeEnum::getCode, Function.identity()));

    AlarmTypeEnum(String code, String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static AlarmTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
