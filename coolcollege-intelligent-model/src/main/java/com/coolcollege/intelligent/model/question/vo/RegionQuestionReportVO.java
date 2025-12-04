package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author suzhuhong
 * @Date 2022/8/16 10:33
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionQuestionReportVO {
    @ApiModelProperty(value = "区域ID")
    private Long regionId;

    @ApiModelProperty(value = "区域名称")
    @Excel(name = "区域/门店名称",orderNum = "0",width =20)
    private String regionName;

    @ApiModelProperty(value = "门店数量")
    @Excel(name = "门店数",orderNum = "2")
    private Integer storeNum;

    @ApiModelProperty(value = "总工单数")
    @Excel(name = "工单数",orderNum = "3")
    private Integer totalQuestionCount;

    @ApiModelProperty(value = "待整改工单数")
    @Excel(name = "待整改",orderNum = "4",groupName = "整改阶段",width = 20)
    private Integer toBeRectifiedQuestionCount;

    @ApiModelProperty(value = "已整改工单数")
    @Excel(name = "已整改",orderNum = "5",groupName = "整改阶段",width = 20)
    private Integer rectifiedQuestionCount;

    @ApiModelProperty(value = "工单整改率")
    @Excel(name = "整改率",orderNum = "6",groupName = "整改阶段",width = 20)
    private String rectificationStageQuestionCorrectionRate;

    @ApiModelProperty(value = "整改阶段逾期数")
    @Excel(name = "整改逾期数",orderNum = "7",groupName = "整改阶段",width = 20)
    private Integer rectificationStageOverdueCount;

    @ApiModelProperty(value = "整改逾期率")
    @Excel(name = "整改逾期率",orderNum = "8",groupName = "整改阶段",width = 20)
    private String rectificationStageOverdueRate;

    @ApiModelProperty(value = "待审批工单数")
    @Excel(name = "待审批",orderNum = "9",groupName = "审批阶段",width = 20)
    private Integer approveQuestionCount;

    @ApiModelProperty(value = "审批通过次数")
    @Excel(name = "审批通过次数",orderNum = "10",groupName = "审批阶段",width = 20)
    private Integer approvePassCount;

    @ApiModelProperty(value = "审批驳回次数")
    @Excel(name = "审批驳回次数",orderNum = "11",groupName = "审批阶段",width = 20)
    private Integer approveRejectCount;

    @ApiModelProperty(value = "审批通过率")
    @Excel(name = "审批通过率",orderNum = "12",groupName = "审批阶段",width = 20)
    private String approvePassRate;

    @ApiModelProperty(value = "已完成")
    @Excel(name = "已完成",orderNum = "13",groupName = "完成阶段",width = 20)
    private Integer completeQuestionCount;

    @ApiModelProperty(value = "逾期完成")
    @Excel(name = "逾期完成",orderNum = "14",groupName = "完成阶段",width = 20)
    private Integer completeOverDueCount;

    @ApiModelProperty(value = "逾期完成率")
    @Excel(name = "逾期完成率",orderNum = "15",groupName = "完成阶段",width = 20)
    private String completeOverDueRate;

    @ApiModelProperty(value = "工单完成率")
    @Excel(name = "工单完成率",orderNum = "16",groupName = "完成阶段",width = 20)
    private String questionCompleteRate;

    @ApiModelProperty(value = "总完成时长")
    @Excel(name = "总完成时长(小时)",orderNum = "17",groupName = "完成阶段",width = 20)
    private BigDecimal completeTotalDuration;

    @ApiModelProperty(value = "完成平均时长")
    @Excel(name = "完成平均时长(小时)",orderNum = "18",groupName = "完成阶段",width = 20)
    private BigDecimal completeAvgDuration;
}
