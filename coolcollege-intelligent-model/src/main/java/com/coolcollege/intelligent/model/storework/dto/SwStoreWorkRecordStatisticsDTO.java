package com.coolcollege.intelligent.model.storework.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkRecordStatisticsDTO{

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("store_work_id+store_id+store_work_date  md5")
    private String tcBusinessId;

    @ApiModelProperty("合格作业数")
    private Integer passColumnNum;

    @ApiModelProperty("不合格作业数")
    private Integer failColumnNum;

    @ApiModelProperty("不适用作业数")
    private Integer inapplicableColumnNum;

    @ApiModelProperty("已完成作业数")
    private Integer finishColumnNum;

    @ApiModelProperty("采集项数")
    private Integer collectColumnNum;

    @ApiModelProperty("总作业数")
    private Integer totalColumnNum;

    @ApiModelProperty("总得分")
    private BigDecimal totalGetScore;

    @ApiModelProperty("总分")
    private BigDecimal totalFullScore;

    @ApiModelProperty("任务数量，即检查表个数")
    private Integer tableNum;

    @ApiModelProperty("需要点评的检查表数量")
    private Integer needCommentTableNum;

    @ApiModelProperty("点评过的检查表数量")
    private Integer commentTableNum;

    @ApiModelProperty("子工单数量")
    private Integer questionNum;

    @ApiModelProperty("待处理子工单数量")
    private Integer unHandleQuestionNum;

    @ApiModelProperty("待审批子工单数量")
    private Integer unApproveQuestionNum;

    @ApiModelProperty("已完成子工单数量")
    private Integer finishQuestionNum;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    private Integer commentStatus;

    @ApiModelProperty("开始执行时间")
    private Date beginHandleTime;

    @ApiModelProperty("完成执行时间 随表处理时间变化")
    private Date endHandleTime;

    @ApiModelProperty("是否为同一个人执行 0否 1是")
    private Boolean sameHandleUser;

    @ApiModelProperty("同一个执行人时，实际处理人id")
    private String actualHandleUserId;

    @ApiModelProperty("平均合格率（表上的合格率累加/表数量）")
    private BigDecimal avgPassRate;

    @ApiModelProperty("平均得分率（表上的得分率累加/表数量）")
    private BigDecimal avgScoreRate;

    @ApiModelProperty("完成率(已完成作业数/总作业数)")
    private BigDecimal finishRate;

    @ApiModelProperty("自定义项数")
    private Integer defColumnNum;

}