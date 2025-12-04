package com.coolcollege.intelligent.common.enums.messageboard;

/**
 * @author wxp
 * @FileName: 留言操作类型
 * @Description:
 * @date 2024-07-29 16:24
 */
public enum MessageOperateTypeEnum {

    MESSAGE("message", "留言"),
    LIKE("like", "点赞"),
    ;

    private String code;

    private String message;

    MessageOperateTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
