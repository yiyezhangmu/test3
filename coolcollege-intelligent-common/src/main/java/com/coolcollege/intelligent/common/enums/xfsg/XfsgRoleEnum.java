package com.coolcollege.intelligent.common.enums.xfsg;

import com.coolcollege.intelligent.common.constant.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @FileName: UserRoleEnum
 * @Description: 用户职位
 * @date 2024-03-22 16:37
 */
public enum XfsgRoleEnum {
    INVESTMENT_COMMISSIONER(100000000L, "招商专员"),
    SELECT_SITE_COMMISSIONER(110000000L, "选址专员"),
    REGION_MANAGER(120000000L, "大区执行总经理"),
    THEATER_MANAGER(130000000L, "战区经理"),
    OPERATIONS_MANAGER(140000000L, "营运经理"),
    TRAINER(150000000L, "训练"),
    XFSG_CLERK(160000000L, "店员"),
    XFSG_SHOPOWNER(170000000L, "店长"),
    SUPERVISION(180000000L, "督导"),
    SELECT_SITE_LEADER(190000000L, "选址组长"),
    SELECT_SITE_MANAGER(200000000L, "选址经理"),
    INVESTMENT_MANAGER(210000000L, "招商经理"),
    HEADQUARTERS_LEADER(220000000L, "总部负责人"),
    HR(230000000L, "HR"),
    DESIGN_MANAGER(240000000L, "品牌设计高级经理"),
    ENGINEER_DEP_SUPERVISOR(250000000L, "工程部监理"),
    ENGINEER_DEP_MANAGER(260000000L, "工程部高级经理"),
    ;

    private Long code;
    private String desc;

    protected static final Map<String, XfsgRoleEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(XfsgRoleEnum::getDesc, Function.identity()));

    XfsgRoleEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static XfsgRoleEnum getByDesc(String desc) {
        return map.get(desc);
    }

    public static String translateToInitPosition(String position) {
        if(position.startsWith(Constants.HUMAN_TRAINING + Constants.SPLIT_LINE)){
            return TRAINER.getDesc();
        }
        if(position.contains(Constants.STORE_ASSISTANCE)){
            return XFSG_CLERK.getDesc();
        }
        for (XfsgRoleEnum xfsgRoleEnum : XfsgRoleEnum.values()) {
            if(xfsgRoleEnum == REGION_MANAGER || xfsgRoleEnum == THEATER_MANAGER
                    || xfsgRoleEnum == OPERATIONS_MANAGER || xfsgRoleEnum == TRAINER){
                if(position.startsWith(xfsgRoleEnum.getDesc() + Constants.SPLIT_LINE)){
                    return  xfsgRoleEnum.getDesc();
                }
            }else if(xfsgRoleEnum == XFSG_CLERK || xfsgRoleEnum == XFSG_SHOPOWNER
                    || xfsgRoleEnum == SUPERVISION || xfsgRoleEnum == TRAINER){
                if(position.contains(xfsgRoleEnum.getDesc())){
                    return  xfsgRoleEnum.getDesc();
                }
            }
        }
        return position;
    }
}
