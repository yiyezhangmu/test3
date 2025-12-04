package com.coolcollege.intelligent.common.enums.importexcel;

/**
 * @author 邵凌志
 * @date 2020/12/11 18:11
 */
public enum ImportTaskStatusEnum {


    SUCCESS(2, "上传成功"),

    PROGRESS(1, "进行中"),

    ERROR(3, "上传失败"),

    PART_ERROR(4, "上传部分失败"),
    ;
    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

    ImportTaskStatusEnum(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    private void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    private void setMsg(String message) {
        this.msg = message;
    }
}
