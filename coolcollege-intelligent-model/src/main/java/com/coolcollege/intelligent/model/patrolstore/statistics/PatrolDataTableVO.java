package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2022-12-29 13:49
 */
@Builder
@ApiModel
@Data
public class PatrolDataTableVO {

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    /**
     * 检查表名称
     */
    @ApiModelProperty("检查表内容")
    @Excel(name = "任务内容")
    private String metaTableName;


    /**
     * 总检查项数
     */
    @ApiModelProperty("总检查项数")
    @Excel(name = "总检查项数")
    private Integer totalColumnCount;

    /**
     * 合格项数
     */
    @ApiModelProperty("合格项数")
    @Excel(name = "合格项数")
    private Integer passColumnCount;

    /**
     * 不合格项数
     */
    @ApiModelProperty("不合格项数")
    @Excel(name = "不合格项数")
    private Integer failColumnCount;

    /**
     * 不合格项数
     */
    @ApiModelProperty("不适用项数")
    @Excel(name = "不适用项数")
    private Integer inapplicableColumnCount;


    /**
     * 得分
     */
    @ApiModelProperty("得分")
    @Excel(name = "得分")
    private BigDecimal score;

    @ApiModelProperty("除红线否决外得分")
    @Excel(name = "除红线否决外得分")
    private BigDecimal allColumnCheckScore;

    @ApiModelProperty("除红线否决外得分率")
    @Excel(name = "除红线否决外得分率")
    private BigDecimal allColumnCheckScorePercent;


    /**
     * 总分
     */
    @ApiModelProperty("总分")
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /**
     * 巡店结果
     */
    @ApiModelProperty("巡店结果")
    @Excel(name = "巡店结果")
    private String checkResult;


    /**
     * 奖罚金额
     */
    @ApiModelProperty("奖罚金额")
    @Excel(name = "奖罚金额")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("提交状态")
    private Integer submitStatus;

    /**
     * 得分率
     */
    private BigDecimal percent;

    /**
     * 参与计算的任务总分 根据适用项规则计算得出
     */
    private BigDecimal taskCalTotalScore;

}
