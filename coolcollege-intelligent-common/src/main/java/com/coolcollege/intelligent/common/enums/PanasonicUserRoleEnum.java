package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: UserRoleEnum
 * @Description: 松下用户职位
 * @date 2024-03-22 16:37
 */
public enum PanasonicUserRoleEnum {
    PROMOTER(91000000L, "促销员"),
    PROMOTION_MANAGER(92000000L, "促管"),
    ACTUAL_REQUIREMENT_STAFF(93000000L, "营业担当"),
    ;

    private Long code;
    private String desc;

    protected static final Map<String, PanasonicUserRoleEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(PanasonicUserRoleEnum::getDesc, Function.identity()));

    PanasonicUserRoleEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PanasonicUserRoleEnum getByDesc(String desc) {
        return map.get(desc);
    }
}
