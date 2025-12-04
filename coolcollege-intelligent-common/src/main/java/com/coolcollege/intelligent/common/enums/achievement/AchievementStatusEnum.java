package com.coolcollege.intelligent.common.enums.achievement;

import com.coolcollege.intelligent.common.enums.device.DeviceBindEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum AchievementStatusEnum {

    //删除
    DELETE(-1,"删除"),
    //冻结
    FREEZE(0,"冻结"),
    //正常
    NORMAL(1,"正常");


    private static final Map<Integer, AchievementStatusEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(AchievementStatusEnum::getCode, Function.identity()));


    AchievementStatusEnum(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }


    private Integer code;
    private String msg;


    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static AchievementStatusEnum getByCode(Integer code) {
        return MAP.get(code);
    }

}
