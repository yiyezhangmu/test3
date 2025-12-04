package com.coolcollege.intelligent.common.enums.messageboard;

/**
 * @author wxp
 * @FileName: 留言业务类型
 * @Description:
 * @date 2024-07-29 16:24
 */
public enum MessageBusinessTypeEnum {

    STOREWORK("storework", "店务"),
    OTHER("other", "其它"),
    ;

    private String code;

    private String message;

    MessageBusinessTypeEnum(String code, String message) {
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
