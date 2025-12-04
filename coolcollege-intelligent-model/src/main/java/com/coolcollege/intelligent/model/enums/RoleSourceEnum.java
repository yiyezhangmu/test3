package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:职位来源枚举
 *
 * @author zhouyiping
 * @date 2020/12/30
 */
public enum  RoleSourceEnum {
    /**
     * 职位来源
     */
    CREATE("create", "自建"),
    EHR("ehr", "EHR"),
    SYNC("sync", "钉钉同步");

    private static final Map<String, RoleSourceEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(RoleSourceEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    RoleSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static RoleSourceEnum getByCode(String code) {
        return map.get(code);
    }
}
