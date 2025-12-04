package com.coolcollege.intelligent.common.enums.message;

/**
 * @author zhangchenbiao
 * @FileName: MessageStatusEnums
 * @Description:
 * @date 2024-02-22 16:53
 */
public enum MessageStatusEnums {

    TODO("todo", "待处理"),
    FINISH("finish", "已完成");

    private String value;

    private String message;



    MessageStatusEnums(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
