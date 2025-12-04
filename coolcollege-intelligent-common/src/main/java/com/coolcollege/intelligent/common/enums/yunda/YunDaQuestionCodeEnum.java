package com.coolcollege.intelligent.common.enums.yunda;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 韵达工单编码配置
 *
 * @author wxp
 * @FileName: YunDaQuestionCodeEnum
 * @Description:
 */
public enum YunDaQuestionCodeEnum {

    QUESTION_CODE_ONE("1703", "三级巡检强化学习-《装车作业》", 1945814126278348800L),

    QUESTION_CODE_TWO("1704", "三级巡检强化学习-《卸车作业》", 1945814347959898112L),

    QUESTION_CODE_THREE("1707", "三级巡检强化学习-《发出手工集包作业》", 1945814763191799808L),
    ;

    private String code;

    private String title;

    private Long projectId;

    YunDaQuestionCodeEnum(String code, String title, Long projectId) {
        this.code = code;
        this.title = title;
        this.projectId = projectId;
    }

    public static final Map<String, YunDaQuestionCodeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(YunDaQuestionCodeEnum::getCode, Function.identity()));

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public Long getProjectId() {
        return projectId;
    }
}
