package com.coolcollege.intelligent.common.enums.fsGroup;

public enum FsGroupTypeEnum {
    STORE("store", "门店"),
    REGION("region", "区域"),
    OTHER("other", "其他");
    private String code;
    private String msg;
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    FsGroupTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
