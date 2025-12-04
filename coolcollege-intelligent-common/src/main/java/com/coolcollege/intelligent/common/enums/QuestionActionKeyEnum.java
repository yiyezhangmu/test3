package com.coolcollege.intelligent.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 问题工单处理结果
 * pass通过 reject拒绝 rectified已整改 unneeded无需整改
 * @author zhangnan
 * @date 2021-12-29 11:10
 */
public enum QuestionActionKeyEnum {

    /**
     * 通过
     */
    PASS("pass", "通过"),

    /**
     * 拒绝
     */
    REJECT("reject", "拒绝"),

    /**
     * 已整改
     */
    RECTIFIED("rectified", "已整改"),

    /**
     * 无需整改
     */
    UNNEEDED("unneeded", "无问题，维持现状");

    QuestionActionKeyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    private String code;

    private String desc;

    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }

    protected static final Map<String, QuestionActionKeyEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(QuestionActionKeyEnum::getCode, Function.identity()));

    public static String getDescByCode(String code) {
        if(StringUtils.isBlank(code)) {
            return null;
        }
        return map.get(code).getDesc();
    }
}
