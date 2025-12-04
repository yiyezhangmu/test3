package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author wxp
 * @Date 2022/9/16 15:01
 * @Version 1.0
 */
@Data
public class PageHomeStoreWorkStatisticsDTO {


    @ApiModelProperty("已完成门店数")
    private Long finishNum;

    @ApiModelProperty("总门店数")
    private Long totalNum;

    @ApiModelProperty("工单数")
    private Long questionNum;

    @ApiModelProperty("平均合格率")
    private BigDecimal totalPassRate;

    @ApiModelProperty("总得分")
    private BigDecimal totalGetScore;

    @ApiModelProperty("平均得分率")
    private BigDecimal totalScoreRate;

    @ApiModelProperty("完成评论数")
    private Long commentNum;

    @ApiModelProperty("待处理子工单数量")
    private Long unHandleQuestionNum;

    @ApiModelProperty("待审批子工单数量")
    private Long unApproveQuestionNum;

    @ApiModelProperty("已完成子工单数量")
    private Long finishQuestionNum;

    @ApiModelProperty("日期")
    private String storeWorkDate;
}
