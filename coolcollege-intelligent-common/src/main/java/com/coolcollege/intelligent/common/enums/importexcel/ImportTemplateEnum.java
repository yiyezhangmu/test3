package com.coolcollege.intelligent.common.enums.importexcel;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ImportTemplateEnum {
    /**
     *
     */
    REGION("批量导入门店区域.xlsx", "region", "区域模板"),
    SOP_CHECK_ITEM("批量导入检查项.xlsx", "checkItem", "检查项模板"),
    STORE_GROUP("门店分组导入.xlsx", "storeGroup", "分组模板"),
    STORE("批量导入门店.xlsx", "store", "门店模板"),
    BASE_STORE("导入更新门店基本信息.xlsx", "baseStore", "导入门店基本信息"),
    USER("批量导入用户.xlsx", "user", "用户模板"),
    STORE_RANGE("门店范围导入模板.xlsx", "storeRange", "门店范围导入模板"),
    USER_GROUP("用户分组导入模板.xlsx", "userGroup", "用户分组导入模板"),
    STORE_IMPORT("门店导入模板.xlsx", "storeImport", "门店导入模板"),
    EXTERNAL_NODE_IMPORT("批量导入外部用户架构.xlsx", "externalNodeImport", "批量导入外部用户架构"),
    EXTERNAL_USER_IMPORT("外部用户导入模板.xlsx", "externalUserImport", "外部用户导入模板"),
    ;
    /**
     * 返回码
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 返回信息
     */
    private String dec;

    private static final Map<String, String> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(ImportTemplateEnum::getCode, ImportTemplateEnum::getName));


    ImportTemplateEnum(String name, String code, String dec) {
        this.name = name;
        this.code = code;
        this.dec = dec;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public static String getByCode(String code) {
        return MAP.get(code);
    }
}
