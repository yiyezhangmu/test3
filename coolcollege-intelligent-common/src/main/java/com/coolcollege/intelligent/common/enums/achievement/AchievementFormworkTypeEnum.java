package com.coolcollege.intelligent.common.enums.achievement;

public enum AchievementFormworkTypeEnum {

    NORMAL("normal","通用"),
    TEMP("temp","临时");

    AchievementFormworkTypeEnum(String code,String value){
        this.code=code;
        this.value =value;
    }

    private String code;

    private String value;

    private void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
