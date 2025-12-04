package com.coolcollege.intelligent.model.patrolstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/8/23 13:36
 */
@Data
public class PatrolStoreReviewVO {
    @Excel(name = "巡店记录ID", orderNum = "1")
    private Long businessId;
    @Excel(name = "门店名称", orderNum = "2")
    private String storeName;
    @Excel(name = "所属区域", orderNum = "3")
    private String regionPathName;
    @Excel(name = "巡店人", orderNum = "4")
    private String patrolUserName;
    @Excel(name = "巡店日期", orderNum = "5")
    private String patrolDate;
    @Excel(name = "任务ID", orderNum = "6")
    private Long taskId;
    @Excel(name = "任务名称", orderNum = "7")
    private String taskName;
    @Excel(name = "检查表", orderNum = "8")
    private String tableName;
    @Excel(name = "点检项", orderNum = "9")
    private String metaColumnName;
    @Excel(name = "指导员点检结果", orderNum = "10")
    private String checkResultName;
    @Excel(name = "标准图", orderNum = "11")
    private String standardPics;
    @Excel(name = "得分", orderNum = "12")
    private BigDecimal checkScore;
    @Excel(name = "结果图", orderNum = "13")
    private String checkPics;
    @Excel(name = "大区审核结果", orderNum = "14")
    private String recheckResultName;
    @Excel(name = "不合格原因", orderNum = "15")
    private String checkResultReason;
    @Excel(name = "得分", orderNum = "16")
    private BigDecimal recheckScore;
    @Excel(name = "大区审核人", orderNum = "17")
    private String recheckUserName;
    @Excel(name = "大区审核日期", orderNum = "18")
    private String recheckDate;
    @Excel(name = "战区审核结果", orderNum = "19")
    private String secondRecheckResultName;
    @Excel(name = "不合格原因", orderNum = "20")
    private String secondCheckResultReason;
    @Excel(name = "得分", orderNum = "21")
    private BigDecimal secondRecheckScore;
    @Excel(name = "战区审核人", orderNum = "22")
    private String secondRecheckUserName;
    @Excel(name = "战区审核日期", orderNum = "23")
    private String secondRecheckDate;
    @Excel(name = "第三次审核结果", orderNum = "24")
    private String thirdRecheckResultName;
    @Excel(name = "不合格原因", orderNum = "25")
    private String thirdCheckResultReason;
    @Excel(name = "得分", orderNum = "26")
    private BigDecimal thirdRecheckScore;
    @Excel(name = "第三次审核人", orderNum = "27")
    private String thirdRecheckUserName;
    @Excel(name = "第三次审核日期", orderNum = "28")
    private String thirdRecheckDate;
}
