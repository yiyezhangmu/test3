package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:数据来源枚举
 *
 * @author zhangnan
 * @date 2022/05/09
 */
public enum DataSourceEnum {
    /**
     * 自建
     */
    CREATE("create", "自建"),
    /**
     * 同步
     */
    SYNC("sync", "钉钉同步");

    private static final Map<String, DataSourceEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(DataSourceEnum::getCode, Function.identity()));


    private String code;
    private String desc;

    DataSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static DataSourceEnum getByCode(String code) {
        return map.get(code);
    }
}
