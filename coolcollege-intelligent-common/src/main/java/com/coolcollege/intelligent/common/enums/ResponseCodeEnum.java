package com.coolcollege.intelligent.common.enums;


/**
 * @author Aaron
 * @Description 业务统一返回码
 * @date 2019/12/20
 */
public enum ResponseCodeEnum {
    /**
     * 成功返回
     */
    SUCCESS(200000, "SUCCESS");


    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;


    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
