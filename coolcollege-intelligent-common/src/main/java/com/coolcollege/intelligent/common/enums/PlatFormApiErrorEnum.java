package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PlatFormApi 应用异常码
 *
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-13 12:40
 */
public enum PlatFormApiErrorEnum {

    /**
     * 菜单类型
     */
    MULTI_TERMINAL_LOGIN_ERROR("platformapi.521006", "不支持多端登录"),
    LOGIN_FAILED("platformapi.521013", "登录失败，请稍后重试"),

    ENTERPRISE_NOT_FOUND("platformapi.521001", "企业不存在"),
    ENTERPRISE_INIT("platformapi.521002", "企业正在初始化"),
    ENTERPRISE_FROZEN("platformapi.521003", "企业已冻结"),

    NO_ENTERPRISE_AVAILABLE("platformapi.521004", "没有可用的企业"),
    ACCOUNT_IS_NOT_AUTHORIZED("platformapi.521020", "您的账号未在公司授权范围内"),
    USER_NOT_FIND("platformapi.521025", "用户不存在"),
    USER_NOT_ACTIVE("platformapi.521026", "用户未激活"),

    /**
     * v2.0 统一错误码
     */
    THIRD_OA_LOGIN_ERROR("600200", "thirdOA登陆失败"),
    ;

    private String code;
    private String msg;

    private final static Set<PlatFormApiErrorEnum> ENTERPRISE_ERROR = new HashSet<>();

    private final static Set<PlatFormApiErrorEnum> USER_ERROR = new HashSet<>();


    static {
        ENTERPRISE_ERROR.add(ENTERPRISE_NOT_FOUND);
        ENTERPRISE_ERROR.add(ENTERPRISE_INIT);
        ENTERPRISE_ERROR.add(ENTERPRISE_FROZEN);

        USER_ERROR.add(NO_ENTERPRISE_AVAILABLE);
        USER_ERROR.add(ACCOUNT_IS_NOT_AUTHORIZED);
        USER_ERROR.add(USER_NOT_FIND);
        USER_ERROR.add(USER_NOT_ACTIVE);
    }

    protected static final Map<String, PlatFormApiErrorEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PlatFormApiErrorEnum::getCode, Function.identity()));

    PlatFormApiErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static PlatFormApiErrorEnum getByCode(String code) {
        return map.get(code);
    }


    /**
     * 是否是企业原因
     *
     * @param errorEnum
     * @return
     */
    public static boolean isEnterpriseError(PlatFormApiErrorEnum errorEnum) {
        return ENTERPRISE_ERROR.contains(errorEnum);
    }


    /**
     * 是否是用户原因
     */
    public static boolean isUserError(PlatFormApiErrorEnum errorEnum) {
        return USER_ERROR.contains(errorEnum);
    }
}
