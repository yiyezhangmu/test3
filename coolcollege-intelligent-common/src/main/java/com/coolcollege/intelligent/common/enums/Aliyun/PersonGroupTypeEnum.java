package com.coolcollege.intelligent.common.enums.Aliyun;


import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zyp
 */
public enum PersonGroupTypeEnum {
    /**
     * 全企业数据
     */
    BASE_REQUIRED("base_required", "基本数据必填"),

    /**
     * 所在组织架构包含下级
     */
    NOT_REQUIRED("not_required","没有必填信息");

    private String code;
    private String msg;

    protected static final Map<String, PersonGroupTypeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PersonGroupTypeEnum::getCode, Function.identity()));

    PersonGroupTypeEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static PersonGroupTypeEnum getByCode(String code) {
        return map.get(code);
    }
}
