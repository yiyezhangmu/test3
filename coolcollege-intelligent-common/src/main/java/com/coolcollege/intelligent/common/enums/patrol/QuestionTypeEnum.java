package com.coolcollege.intelligent.common.enums.patrol;

public enum QuestionTypeEnum {

    AI("AI","AI工单"),

    PATROL_STORE("patrolStore","巡店工单"),

    STORE_WORK("storeWork","店务工单"),

    COMMON("common","普通工单"),

    PATROL_RECHECK("patrolRecheck","复审工单"),//已废弃 老的稽核工单 由于关联data_column_id 表不一样所以不能复用
    PATROL_RECHECK_REPORT("patrolRecheckReport","复审工单"),//大区战区稽核工单

    SAFETY_CHECK("safetyCheck","稽核工单"),
    MYSTERIOUS_GUEST("mysteriousGuest","神秘访客工单"),

    AI_INSPECTION("aiInspection","AI巡检工单"),

    ;


    private String desc;

    private String code;

    QuestionTypeEnum(String code, String desc) {
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

    public static String getByCode(String code){
        for (QuestionTypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value.getDesc();
            }
        }
        return code;
    }

    public static QuestionTypeEnum getQuestionTypeByCode(String code){
        for (QuestionTypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return COMMON;
    }
}
