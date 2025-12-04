package com.coolcollege.intelligent.common.enums.statistics;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统计维度枚举
 * @author zyp
 */
public enum DimensionTypeEnum {


    /**
     * 区域维度统计
     */
    REGION_DIMENSION("region_dimension", "区域维度统计"),

    /**
     * 门店维度统计
     */
    STORE_DIMENSION("store_dimension","门店维度统计"),

    /**
     * 人员维度统计
     */
    PERSON_DIMENSION("person_dimension","人员维度统计");



    private String code;
    private String msg;

    protected static final Map<String, DimensionTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DimensionTypeEnum::getCode, Function.identity()));

    DimensionTypeEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static DimensionTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
