package com.coolcollege.intelligent.model.achievement.qyy.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class QyyNewspaperExportVO {

    @Excel(name = "门店名称",orderNum = "1")
    private String storeName;
    @Excel(name = "所属分公司",orderNum = "2")
    private String compName;
    @Excel(name = "已读人数",orderNum = "3")
    private String readNum;
    @Excel(name = "周业绩",orderNum = "4")
    private String weekAchieve;
    @Excel(name = "本周总结",orderNum = "5")
    private String weekZj;
    @Excel(name = "下周计划",orderNum = "6")
    private String nextPlan;
    @Excel(name = "竞品收集",orderNum = "7")
    private String jpsj;
    @Excel(name = "周报时间",orderNum = "8")
    private String createTime;

}
