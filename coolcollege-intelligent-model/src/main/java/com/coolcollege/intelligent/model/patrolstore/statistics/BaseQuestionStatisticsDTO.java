package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * 工单统计项的DTO
 * 
 * @author jeffrey
 * @date 2020/12/09
 */
@NoArgsConstructor
public class BaseQuestionStatisticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private long questionId;
    @Excel(name = "总工单数")
    private int totalQuestionNum;// 总问题数
    @Excel(name = "待整改工单数")
    private int todoQuestionNum;// 待整改问题数
    @Excel(name = "待复检工单数")
    private int unRecheckQuestionNum;// 待复检问题数
    @Excel(name = "已解决工单数")
    private int finishQuestionNum;// 已经解决的问题数量
    @Deprecated
    @Excel(name = "工单整改率")
    private String doneQuestionPercent;// 问题整改率, 待复检问题数/totalQuestionNum
    @Deprecated
    @Excel(name = "工单解决率")
    private String finishQuestionPercent;// 问题解决率
    @Deprecated
    @Excel(name = "工单复检率")
    private String unRecheckPercent;//问题复检查率
    
    
    public long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public int getTotalQuestionNum() {
        return totalQuestionNum;
    }
    public void setTotalQuestionNum(int totalQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
    }
    public int getTodoQuestionNum() {
        return todoQuestionNum;
    }
    public void setTodoQuestionNum(int todoQuestionNum) {
        this.todoQuestionNum = todoQuestionNum;
    }
    public int getUnRecheckQuestionNum() {
        return unRecheckQuestionNum;
    }
    public void setUnRecheckQuestionNum(int unRecheckQuestionNum) {
        this.unRecheckQuestionNum = unRecheckQuestionNum;
    }
    public int getFinishQuestionNum() {
        return finishQuestionNum;
    }

    public void setFinishQuestionNum(int finishQuestionNum) {
        this.finishQuestionNum = finishQuestionNum;
    }
    
    
    //=========================================
    public String getDoneQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format(1-(todoQuestionNum*1d)/totalQuestionNum);
    }
    public String getFinishQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((finishQuestionNum*1d)/totalQuestionNum);
    }
    public String getUnRecheckPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((unRecheckQuestionNum*1d)/totalQuestionNum);
    }

    public BaseQuestionStatisticsDTO(int totalQuestionNum, int todoQuestionNum, int unRecheckQuestionNum, int finishQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
        this.todoQuestionNum = todoQuestionNum;
        this.unRecheckQuestionNum = unRecheckQuestionNum;
        this.finishQuestionNum = finishQuestionNum;
    }

}