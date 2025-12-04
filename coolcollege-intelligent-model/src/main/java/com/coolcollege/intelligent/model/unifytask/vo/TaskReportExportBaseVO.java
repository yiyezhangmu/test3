package com.coolcollege.intelligent.model.unifytask.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author wxp
 * @date 2021/6/23 10:00
 */
@Data
public class TaskReportExportBaseVO {

    private static final long serialVersionUID = 1L;

    @Excel(name = "类型", orderNum = "1")
    private String taskTypeName;

    @Excel(name = "任务名称", orderNum = "2")
    private String taskName;

    @Excel(name = "任务范围", orderNum = "3")
    private String taskStoreRange;

    @Excel(name = "任务内容", orderNum = "4")
    private String taskContent;

    @Excel(name = "任务有效期", orderNum = "5")
    private String validTime;

    @Excel(name = "循环方式", orderNum = "6")
    private String taskCycle;

    @Excel(name = "创建人", orderNum = "7")
    private String createUserName;

    @Excel(name = "处理人", orderNum = "8")
    private String handlePerson;

    @Excel(name = "审批人", orderNum = "9")
    private String approvalPerson;

    @Excel(name = "已发起任务数", orderNum = "11")
    private Integer buildTaskNum;

    @Excel(name = "待处理任务数", orderNum = "12")
    private Integer waitDealTaskNum;

    @Excel(name = "待处理已逾期任务数", orderNum = "13")
    private Integer waitDealOverdueTaskNum;

    @Excel(name = "待处理逾期率", orderNum = "14")
    private String waitDealOverduePer;

    @Excel(name = "已完成任务数", orderNum = "15")
    private Integer completeTaskNum;

    @Excel(name = "已完成已逾期任务数", orderNum = "16")
    private Integer completeOverdueTaskNum;

    @Excel(name = "已完成逾期率", orderNum = "17")
    private String completeOverduePer;



}
