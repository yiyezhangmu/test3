package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: UserRoleEnum
 * @Description: 用户职位
 * @date 2024-03-22 16:37
 */
public enum NmxyUserRoleEnum {
    INVESTMENT_COMMISSIONER(100000000L, "招商专员"),
    SELECT_SITE_COMMISSIONER(110000000L, "选址专员"),
    REGION_MANAGER(120000000L, "大区执行总经理"),
    THEATER_MANAGER(130000000L, "战区经理"),
    OPERATIONS_MANAGER(140000000L, "营运经理"),
    TRAINER(150000000L, "训练"),
    NMXY_CLERK(160000000L, "店员"),
    NMXY_SHOPOWNER(170000000L, "店长"),
    SUPERVISION(180000000L, "督导"),
    SELECT_SITE_LEADER(190000000L, "选址组长"),
    SELECT_SITE_MANAGER(200000000L, "选址经理"),
    INVESTMENT_MANAGER(210000000L, "招商经理"),
    HEADQUARTERS_LEADER(220000000L, "总部负责人"),
    HR(230000000L, "HR"),
    DESIGN_MANAGER(240000000L, "品牌设计高级经理"),
    ENGINEER_DEP_SUPERVISOR(250000000L, "工建部监理"),
    ENGINEER_DEP_MANAGER(260000000L, "工程部高级经理"),
    FINANCE(270000000L, "财务"),
    TRAIN_TEACHER(280000000L, "培训专员"),
    PERSONNEL(290000000L, "人事专员"),
    MARKETING_SPECIALIST(300000000L, "市场专员"),
    PROCUREMENT(310000000L, "采购专员"),
    ;

    private Long code;
    private String desc;

    protected static final Map<String, NmxyUserRoleEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(NmxyUserRoleEnum::getDesc, Function.identity()));

    NmxyUserRoleEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static NmxyUserRoleEnum getByDesc(String desc) {
        return map.get(desc);
    }
}
