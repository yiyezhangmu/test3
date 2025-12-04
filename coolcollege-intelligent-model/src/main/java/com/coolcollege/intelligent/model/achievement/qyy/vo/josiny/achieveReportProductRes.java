package com.coolcollege.intelligent.model.achievement.qyy.vo.josiny;

import lombok.Data;

import java.util.List;

@Data
public class achieveReportProductRes {
    private String dingDeptId;

    private List<AchieveReportProductListRes> dataList;
}
