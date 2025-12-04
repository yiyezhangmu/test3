package com.coolcollege.intelligent.common.enums.importexcel;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ImportUserGroupEnum {
    USER_ID_TYPE("USER_ID_TYPE","用户ID"),
    JOB_NUMBER_TYPE("JOB_NUMBER_TYPE","用户工号"),

    ;

    private String importType;
    private String desc;

    ImportUserGroupEnum(String importType, String desc) {
        this.importType = importType;
        this.desc = desc;
    }

    public String getImportType() {
        return importType;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map<String, String> MAP = Arrays.stream(values())
            .collect(Collectors.toMap(ImportUserGroupEnum::getImportType, ImportUserGroupEnum::getDesc));

    public static String getByImportType(String importType) {
        return MAP.get(importType);
    }

}
