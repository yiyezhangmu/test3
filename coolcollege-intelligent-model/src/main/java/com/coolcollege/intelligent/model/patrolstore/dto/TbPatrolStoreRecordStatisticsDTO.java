package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 
 * @author   wxp
 * @date   2022-11-30 17:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStoreRecordStatisticsDTO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("得分")
    private BigDecimal score;

    @ApiModelProperty("参与计算的任务总分 根据适用项规则计算得出")
    private BigDecimal taskCalTotalScore;

    @ApiModelProperty("参与计算总项数")
    private Integer totalCalColumnNum;

    @ApiModelProperty("采集项数量")
    private Integer collectColumnNum;

    @ApiModelProperty("不合格数")
    private Integer failNum;

    @ApiModelProperty("合格数")
    private Integer passNum;

    @ApiModelProperty("不适用数")
    private Integer inapplicableNum;

    @ApiModelProperty("总得奖金额")
    private BigDecimal totalResultAward;

    @ApiModelProperty("巡店结果 excellent:优秀 good:良好 eligible:合格 disqualification:不合格")
    private String checkResultLevel;

    private BigDecimal avgScoreRate;
}