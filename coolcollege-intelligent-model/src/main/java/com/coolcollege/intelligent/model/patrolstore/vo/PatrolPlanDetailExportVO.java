package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanDetailPageVO
 * @Description:
 * @date 2024-09-04 11:37
 */
@Data
public class PatrolPlanDetailExportVO {

    @Excel(name = "计划月份", orderNum = "1", width = 30)
    private String planMonth;

    @Excel(name = "计划名称", orderNum = "2", width = 30)
    private String planName;

    @Excel(name = "巡店人", orderNum = "3", width = 20)
    private String supervisorUsername;

    @Excel(name = "巡店日期", orderNum = "4", width = 20)
    private String planDate;

    @Excel(name = "门店授权号", orderNum = "5", width = 20)
    private String storeNum;

    @Excel(name = "门店名称", orderNum = "6", width = 20)
    private String storeName;

    @Excel(name = "完成状态", orderNum = "7", width = 20)
    private String status;
}
