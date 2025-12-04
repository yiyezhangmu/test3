package com.coolcollege.intelligent.common.enums.activity;

/**
 * @author zhangchenbiao
 * @FileName: ActivityStatusEnum
 * @Description:
 * @date 2023-07-04 16:02
 */
public enum ActivityStatusEnum {

    NOT_STARTED(0, "未开始"),
    ONGOING(1, "进行中"),
    END(2, "已结束"),
    STOP(3, "停止")
    ;

    private Integer code;

    private String message;

    ActivityStatusEnum(Integer code, String message) {
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
