package com.coolcollege.intelligent.common.enums.patrol;

public enum CapturePictureTypeEnum {

    TIMING("timing","定时抓拍"),

    AI("AI","AI抓拍");

    private String desc;

    private String code;

    CapturePictureTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    private void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }

}
