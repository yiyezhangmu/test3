package com.coolcollege.intelligent.model.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2022-09-21 11:18
 */
@ApiModel
@Data
public class StoreWorkDataVO {

    @ApiModelProperty("总门店数/应完成门店数")
    private Long totalNum;

    @ApiModelProperty("未完成门店数")
    private Long unFinishNum;

    @ApiModelProperty("已完成门店数")
    private Long finishNum;

    @ApiModelProperty("店务完成率")
    private BigDecimal completeRate;

    @ApiModelProperty("工单数(总)")
    private Long questionNum;

    @ApiModelProperty("平均合格率")
    private BigDecimal averagePassRate;

    @ApiModelProperty("平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("平均得分率")
    private BigDecimal averageScoreRate;

    @ApiModelProperty("平均点评率")
    private BigDecimal averageCommentRate;

    @ApiModelProperty("待处理子工单数量")
    private Long unHandleQuestionNum;

    @ApiModelProperty("待审批子工单数量")
    private Long unApproveQuestionNum;

    @ApiModelProperty("已完成子工单数量")
    private Long finishQuestionNum;

    @ApiModelProperty("日期")
    private String storeWorkDate;
}
