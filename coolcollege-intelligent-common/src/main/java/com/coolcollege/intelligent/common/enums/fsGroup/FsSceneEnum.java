package com.coolcollege.intelligent.common.enums.fsGroup;

public enum FsSceneEnum {
    REPORT("REPORT", "巡店报告");
    private String code;
    private String msg;
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    FsSceneEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
