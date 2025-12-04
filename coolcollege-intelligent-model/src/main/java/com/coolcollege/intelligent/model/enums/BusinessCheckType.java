package com.coolcollege.intelligent.model.enums;

/**
 * @author zhangchenbiao
 * @FileName: ActionTypeEnum
 * @Description:
 * @date 2022-08-16 11:28
 */
public enum BusinessCheckType {

    PATROL_STORE("PATROL_STORE", "处理"),
    PATROL_RECHECK("PATROL_RECHECK", "审批");

    private String code;
    private String message;

    BusinessCheckType(String code, String message) {
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
}
