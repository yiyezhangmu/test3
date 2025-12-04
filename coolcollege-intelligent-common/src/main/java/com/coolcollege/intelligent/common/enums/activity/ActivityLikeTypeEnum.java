package com.coolcollege.intelligent.common.enums.activity;

/**
 * @author zhangchenbiao
 * @FileName: ActivityLikeTypeEnum
 * @Description:
 * @date 2023-07-05 14:37
 */
public enum ActivityLikeTypeEnum {

    ACTIVITY(0, "活动"),
    COMMENT(1, "评论"),
    ;

    private Integer code;

    private String message;

    ActivityLikeTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
