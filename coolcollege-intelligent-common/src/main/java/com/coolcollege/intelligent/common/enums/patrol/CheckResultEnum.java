package com.coolcollege.intelligent.common.enums.patrol;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum CheckResultEnum {

    PASS("PASS","合格", 2),
    FAIL("FAIL","不合格", 0),
    INAPPLICABLE("INAPPLICABLE","不适用", 1);

    private String desc;

    private String code;

    private Integer calScorePriority;



    CheckResultEnum(String code, String desc, Integer calScorePriority) {
        this.code = code;
        this.desc = desc;
        this.calScorePriority = calScorePriority;
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

    public Integer getCalScorePriority() {
        return calScorePriority;
    }

    private void setCalScorePriority(Integer calScorePriority) {
        this.calScorePriority = calScorePriority;
    }

    public static String getByCode(String code){
        for (CheckResultEnum value : values()) {
            if(value.getCode().equals(code)){
                return value.getDesc();
            }
        }
        return null;
    }
    public static String getByDesc(String desc){
        for (CheckResultEnum value : values()) {
            if(value.getDesc().equals(desc)){
                return value.getCode();
            }
        }
        return null;
    }

    public static CheckResultEnum getCheckResultEnum(String checkResult){
        if(StringUtils.isBlank(checkResult)){
            return null;
        }
        for (CheckResultEnum value : CheckResultEnum.values()) {
            if(value.code.equals(checkResult)){
                return value;
            }
        }
        return null;
    }
    public static List<String> getDescList(){
        List<String> descList = new ArrayList<>();
        for (CheckResultEnum value : values()) {
            descList.add(value.getDesc());
        }
        return descList;
    }


}
