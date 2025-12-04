package com.coolcollege.intelligent.common.enums.store;

/**
 * @author 邵凌志
 * @date 2020/7/7 18:10
 */
public enum StoreEnum {

    /**
     * 缺少参数
     */
    FIELD_NOT_NULL(401001, "缺少参数"),
    /**
     * 门店批量上传异常
     */
    FILE_UPLOAD(402001, "门店批量上传异常"),
//    VIDEO_SERVER(401002, "阿里服务异常"),
//    VIDEO_RETURN(301003, ""),
    ;
    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

    StoreEnum(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

}
