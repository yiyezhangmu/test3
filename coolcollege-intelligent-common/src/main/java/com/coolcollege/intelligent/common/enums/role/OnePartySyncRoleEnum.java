package com.coolcollege.intelligent.common.enums.role;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum OnePartySyncRoleEnum {

    /**
     * 管理员
     */
    MASTER("ds_admin", "管理员", "master"),
    /**
     * 普通员工
     */
    EMPLOYEE("ds_default", "未分配", "employee"),
    /**
     * 店长
     */
    SHOPOWNER("ds_shopowner", "店长", "shopowner"),

    /**
     * 督导
     */
    INIT_ROLE_SUPERVISE("ds_supervise", "督导", "supervise"),

    /**
     * 店员
     */
    CLERK("ds_clerk", "店员", "clerk");


    private static final Map<String, OnePartySyncRoleEnum> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(OnePartySyncRoleEnum::getCode, Function.identity()));

    private String code;
    private String name;
    private String roleEnum;


    OnePartySyncRoleEnum(String code, String name, String roleEnum) {
        this.code = code;
        this.name = name;
        this.roleEnum=roleEnum;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getRoleEnum() {
        return roleEnum;
    }

    public static OnePartySyncRoleEnum getByCode(String code) {
        return MAP.get(code);
    }

    public static String getEnumByCode(String code) {
        OnePartySyncRoleEnum e = MAP.get(code);
        if(Objects.isNull(e)) {
            return null;
        }
        return e.getRoleEnum();
    }

    /**
     * 是否是管理员
     * @param code
     * @return
     */
    public static boolean isAdmin(String code){
        if(MASTER.getCode().equals(code)){
            return true;
        }
        return false;
    }
}
