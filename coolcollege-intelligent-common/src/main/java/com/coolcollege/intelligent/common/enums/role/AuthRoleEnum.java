package com.coolcollege.intelligent.common.enums.role;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/13
 */
public enum  AuthRoleEnum {
    /**
     * 全企业数据
     */
    ALL("all", "全企业数据"),

    /**
     * 所在组织架构包含下级
     */
    INCLUDE_SUBORDINATE("include_subordinate","所在组织架构包含下级"),


//    /**
//     * 所在的组织架构不包含下级
//     */
    NOT_INCLUDE_SUBORDINATE("not_include_subordinate","所在的组织架构不包含下级"),

    /**
     * 仅自己的数据
     */
    PERSONAL("personal","仅自己的数据");

    private String code;
    private String msg;

    protected static final Map<String, AuthRoleEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(AuthRoleEnum::getCode, Function.identity()));

    AuthRoleEnum(String code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public static AuthRoleEnum getByCode(String code) {
        return map.get(code);
    }

}
