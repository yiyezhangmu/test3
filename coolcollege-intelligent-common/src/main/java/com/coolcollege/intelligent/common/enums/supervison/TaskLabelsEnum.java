package com.coolcollege.intelligent.common.enums.supervison;

import java.util.Arrays;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2024/9/19 14:28
 * @Version 1.0
 */
public enum TaskLabelsEnum {


    REGULAR_WORK("REGULAR_WORK","常规工作"),
    MARKETING_IMPLEMENTATION("MARKETING_IMPLEMENTATION","营销落地"),
    OTHER("OTHER","其他"),
    ;


    private String labelCode;

    private String labelName;


    TaskLabelsEnum( String labelCode,String labelName) {
        this.labelCode = labelCode;
        this.labelName = labelName;
    }


    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public static final List<TaskLabelsEnum> getAllTaskLabelEnum(){
        return Arrays.asList(TaskLabelsEnum.values());
    }
}
