package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/21 10:18
 */
public enum AliyunSchedulerEnum {

    /**
     * 巡店模板
     */
    SUMMARY ("浓缩视频"),
    ;


    private String desc;

    AliyunSchedulerEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
