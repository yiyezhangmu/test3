package com.coolcollege.intelligent.common.enums.storework;

/**
 * @Author byd
 * @Date 2022/9/8 15:17
 * @Version 1.0
 */
public enum DateStatisticQueryTypeEnum {

    COMPLETE_RATE("completeRate", "按完成率"),
    PASS_RATE("averagePassRate", "按合格率"),
    AVERAGE_SCORE("averageScore", "按得分"),

    AVERAGE_SCORE_RATE("averageScoreRate", "按得分率"),
    AVERAGE_COMMENT_RATE("averageCommentRate", "点评率"),

    QUESTION_NUM("questionNum", "按合工单数"),

    TOTAL_COLUMN_NUM("totalColumnNum", "按应作业数"),
    FINISH_COLUMN_NUM("finishColumnNum", "已完成作业数"),

    UN_FINISH_COLUMN_NUM("unFinishColumnNum", "未完成作业数"),
    ;

    private String code;

    private String message;

    DateStatisticQueryTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static DateStatisticQueryTypeEnum getEnumByCode(String code) {
        for (DateStatisticQueryTypeEnum value : DateStatisticQueryTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
