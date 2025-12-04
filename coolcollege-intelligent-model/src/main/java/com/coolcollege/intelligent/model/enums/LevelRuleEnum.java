package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/7/6 15:39
 * @Description 巡店等级枚举类
 */
public enum LevelRuleEnum {
    SCORING_RATE("SCORING_RATE","得分率"),
    ITEM_NUM("ITEM_NUM","检查项数"),
    EXCELLENT("excellent","优秀"),
    GOOD("good","良好"),
    ELIGIBLE("eligible","合格"),
    DISQUALIFICATION("disqualification","不合格"),
    ;

    private String code;

    private String description;

    LevelRuleEnum(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    private static final Map<String, LevelRuleEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(LevelRuleEnum::getCode, Function.identity()));

    public static String getDescription(String code) {
        LevelRuleEnum levelRuleEnum = map.get(code);
        if(levelRuleEnum != null){
            return levelRuleEnum.getDescription();
        }
        return null;
    }

}
