package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/10 20:40
 */
public enum AliyunStorePersonEnum {

    FREQ("tag_corp_freq", "到店人次"),
    AVG("tag_corp_freq_avg", "平均到店人次"),
    PERCENT("tag_corp_freq_percent", "进店（到店）百分比"),
    TIME("tag_corp_repeatcustomerstay", "平均停留时间"),
    SEX("tag_corp_personnum", "性别 1：男，2：女"),
    ALL_COUNT("tag_corp_total_personnum", "总人数"),
    AGE("tag_corp_agedistribution", "年龄段"),
    ;

    private static final Map<String, AliyunStorePersonEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AliyunStorePersonEnum::getCode, Function.identity()));

    AliyunStorePersonEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AliyunStorePersonEnum getByCode(String code) {
        return map.get(code);
    }
}
