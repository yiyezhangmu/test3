package com.coolcollege.intelligent.model.storework.vo;

import lombok.Data;

@Data
public class WeeklyNewspaperCountVO {
    private String compName;
    private Integer writeNum;
    private Integer noWriteNum;
    private String rate;
    private float realRate;
    private String anotherSynDingDeptId;
}
