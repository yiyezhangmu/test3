package com.coolcollege.intelligent.model.unifytask.vo;

import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import lombok.Data;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author wxp
 * @date 2021/6/23 10:00
 */
@Data
public class TaskReportVO extends TaskParentDO {


    private Long unifyTaskId;

    /**
     * 任务内容
     */
    private String taskContent;
    /**
     * 门店范围
     */
    private List<GeneralDTO> taskStoreRange;
    /**
     * 处理人
     */
    private List<GeneralDTO> handlePerson;
    /**
     * 审核人
     */
    private List<GeneralDTO> approvalPerson;
    /**
     * 复审人
     */
    private List<GeneralDTO> recheckPerson;

    /**
     * 已发起任务数
     */
    private Integer buildTaskNum;

    /**
     * 待处理任务数
     */
    private Integer waitDealTaskNum;

    /**
     * 待处理已逾期任务数
     */
    private Integer waitDealOverdueTaskNum;

    /**
     * 待处理逾期率
     */
    private String waitDealOverduePer;

    /**
     * 已完成任务数
     */
    private Integer completeTaskNum;
    /**
     * 已完成已逾期任务数
     */
    private Integer completeOverdueTaskNum;
    /**
     * 已完成逾期率
     */
    private String completeOverduePer;

    /**
     * 平均得分 平均得分=已完成任务得分总和/已完成任务数
     */
    private Integer averageScore;

    private Integer totalScore;

    /**
     * 待审批数量
     */
    private Integer waitApproveDealTaskNum;

    /**
     * 待审批逾期数量
     */
    private Integer waitApproveDealOverdueTaskNum;


    public String getWaitDealOverduePer() {
        if(buildTaskNum <=0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((waitDealOverdueTaskNum*1d)/buildTaskNum);
    }

    public String getCompleteOverduePer() {
        if(buildTaskNum <=0){
            return "-";
        }
        return NumberFormat.getPercentInstance().format((completeOverdueTaskNum*1d)/buildTaskNum);
    }

    public Integer getAverageScore() {
        if(completeTaskNum <= 0 || totalScore == null){
            return  0;
        }
        return totalScore/completeTaskNum;
    }
}
