package com.coolcollege.intelligent.common.enums.songxia;

import com.coolcollege.intelligent.common.constant.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SongxiaRoleEnum {

    PROMOTER("91000000", "促销员"),
    ;

    private String code;
    private String desc;

    SongxiaRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
