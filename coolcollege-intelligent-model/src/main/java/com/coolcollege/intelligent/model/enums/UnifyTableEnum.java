package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 17:55
 */
public enum UnifyTableEnum {

    /**
     * 任务类型
     */
    TABLE_STORE("unify_store_mapping", "门店"),
    TABLE_DATA("unify_data_mapping", "数据"),
    TABLE_PERSON("unify_person_mapping", "人员"),
    ;

    private static final Map<String, UnifyTableEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(UnifyTableEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    UnifyTableEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UnifyTableEnum getByCode(String code) {
        return map.get(code);
    }

}
