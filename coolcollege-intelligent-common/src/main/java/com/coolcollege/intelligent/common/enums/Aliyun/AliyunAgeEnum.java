package com.coolcollege.intelligent.common.enums.Aliyun;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/22
 */
public enum  AliyunAgeEnum {
    /**
     * 全企业数据
     */
    AGE1("1", "0-5"),

    AGE2("2","5-20"),
    AGE3("3","20-40"),
    AGE4("4","40-60"),
    AGE5("5","60-99");

    private String code;
    private String msg;

    protected static final Map<String, AliyunAgeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AliyunAgeEnum::getCode, Function.identity()));

    AliyunAgeEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static AliyunAgeEnum getByCode(String code) {
        return map.get(code);
    }
}
