package com.coolcollege.intelligent.model.patrolstore.statistics;

import java.text.NumberFormat;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 检查项的相关统计表
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
public class PatrolStoreStatisticsColumnDTO extends BaseQuestionStatisticsDTO {

    private static final long serialVersionUID = 1L;

    private long tableId;//meta表的记录
    private Long columnId;
    @Excel(name = "检查表")
    private String tableName;//检查表名称
    @Excel(name = "使用人数")
    private int usePersonNum;//使用人数
    @Excel(name = "检查次数")
    private int patrolStoreNum;// 巡店检查次数
    @Excel(name = "巡店检查次数")
    private int patrolNum;// 巡店检查次数
    @Excel(name = "合格检查次数")
    private int patrolOkNum;// 合格检查次数
    @Excel(name = "不合格检查次数")
    private int patrolFailNum;// 不合格检查次数
    @Excel(name = "不适用检查次数")
    private int patrolInapplicableNum;// 不适用检查次数
    @Excel(name = "检查项")
    private String columnName;//检查项名称
    @Excel(name = "检查项分类")
    private String categoryName;//检查项分类
    
    @Deprecated
    @Excel(name = "门店合格率")
    private String okPercent;//巡查次数中的门店合格次数
    private double okRate;// 巡店合格率
    
    //=========================================================
    public String getOkPercent() {
        if(patrolNum<=0) {
            return "100%";
        }
        return NumberFormat.getPercentInstance().format((patrolOkNum*1d)/patrolNum);
    }

    public double getOkRate() {
        if (patrolNum <= 0) {
            return 1;
        }
        return (patrolOkNum * 1d) / patrolNum;
    }

}
