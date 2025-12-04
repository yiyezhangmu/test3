package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MetaTableVO {
    @ApiModelProperty(value = "mtaTableId")
    private Long mtaTableId;

    @ApiModelProperty(value = "任务内容")
    private String tableName;

    @ApiModelProperty(value = "检查项数")
    private Integer checkNum;

    @ApiModelProperty(value = "巡店合格项数")
    private Integer passNum;

    @ApiModelProperty(value = "巡店不合格项数")
    private Integer failNum;

    @ApiModelProperty(value = "总分值")
    private BigDecimal totalScore;

    @ApiModelProperty(value = "巡店得分")
    private BigDecimal checkScore;

    @ApiModelProperty(value = "巡店结果")
    private String checkResultLevel;

    @ApiModelProperty(value = "不合格原因汇总")
    private String checkResultReason;

    @ApiModelProperty(value = "大区稽核得分（值）")
    private BigDecimal bigRegionCheckScore;

    @ApiModelProperty(value = "大区稽核结果")
    private String bigRegionCheckResultLevel;

    @ApiModelProperty(value = "大区稽核不合格原因")
    private String bigRegionCheckResultReason;

    @ApiModelProperty(value = "战区稽核得分（值）")
    private BigDecimal warCheckScore;

    @ApiModelProperty(value = "战区稽核结果")
    private String warResultLevel;

    @ApiModelProperty(value = "战区稽核不合格原因")
    private String warResultReason;

}
