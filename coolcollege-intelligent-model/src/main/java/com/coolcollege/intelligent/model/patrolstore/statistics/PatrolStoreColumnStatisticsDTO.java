package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.constant.Constants;
import lombok.Data;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * @author shuchang.wei
 * @date 2021/7/8 9:54
 */
@Data
public class PatrolStoreColumnStatisticsDTO {

    private Long metaColumnId;
    /**
     * 检查项名称
     */
    @Excel(name = "检查项名称")
    private String columnName;

    @Excel(name = "优先级")
    private String level;

    /**
     * 检查项分类
     */
    @Excel(name = "所属分类")
    private String category;

    @Excel(name = "门店场景名称")
    private String storeSceneName;

    /**
     * 检查项分值
     */
    @Excel(name = "检查项分值")
    private BigDecimal metaScore;

    /**
     * 检查次数
     */
    @Excel(name = "检查次数")
    private int checkNum;

    /**
     * 合格数
     */
    @Excel(name = "合格次数")
    private int qualifiedNum;

    /**
     * 不合格数
     */
    @Excel(name = "不合格次数")
    private int unqualifiedNum;

    /**
     * 不适用数
     */
    @Excel(name = "不适用次数")
    private int unsuitableNum;

    /**
     * 合格率
     */
    @Excel(name = "合格率")
    private String qualifiedRating;

    /**
     * 总分
     */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /**
     * 巡店得分
     */
    @Excel(name = "巡店得分")
    private BigDecimal realTotalScore;

    /**
     * 巡店失分
     */
    @Excel(name = "巡店失分")
    private BigDecimal lostScore;

    /**
     * 得分率
     */
    @Excel(name = "得分率")
    private String scoreRating;

    /**
     * 失分率
     */
    @Excel(name = "失分率")
    private String lostScoreRating;

    /**
     * 发起工单数
     */
    @Excel(name = "发起工单数")
    private int totalQuestionNum;

    /**
     * 完成工单数
     */
    @Excel(name = "完成工单数")
    private int completeQuestionNum;

    /**
     * 工单完成率
     */
    @Excel(name = "工单完成率")
    private String completeQuestionPercent;

    /**
     * String 数字+字符无法排序，如果遇到排序的情况，可以往里注入数据以排序
     */
    private double rate;



    public String getScoreRating(){
        if(Objects.isNull(totalScore) || totalScore.compareTo(new BigDecimal(Constants.ZERO_STR)) < 1){
            return "0%";
        }
        return NumberFormat.getPercentInstance().format(realTotalScore.divide(totalScore,2,BigDecimal.ROUND_HALF_UP));
    }

    public String getLostScoreRating(){
        if(Objects.isNull(totalScore) || this.totalScore.compareTo(new BigDecimal(Constants.ZERO_STR)) < 1){
            return "0%";
        }
        return NumberFormat.getPercentInstance().format(lostScore.divide(totalScore,2,BigDecimal.ROUND_HALF_UP));
    }

    public String getCompleteQuestionPercent(){
        if(this.totalQuestionNum <= 0){
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((completeQuestionNum*1d)/totalQuestionNum);
    }

    public String getQualifiedRating(){
        if(this.qualifiedNum <= 0){
            return "0%";
        }
        return NumberFormat.getPercentInstance().format((qualifiedNum*1d)/checkNum);
    }



}

