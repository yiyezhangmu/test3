package com.coolcollege.intelligent.model.safetycheck;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataColumnHistoryDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("操作历史id")
    private Long historyId;

    @ApiModelProperty("操作类型 submit提交 appeal申诉")
    private String operateType;

    @ApiModelProperty("操作人id")
    private String operateUserId;

    @ApiModelProperty("操作人姓名")
    private String operateUserName;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("数据检查项id")
    private Long dataColumnId;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("检查项结果id")
    private Long checkResultId;

    @ApiModelProperty("检查项结果名称")
    private String checkResultName;

    @ApiModelProperty("检查项的描述信息")
    private String checkText;

    @ApiModelProperty("检查项分值")
    private BigDecimal checkScore;

    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;

    @ApiModelProperty("奖罚金额 正数奖励金额 负数罚款金额")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("不合格原因")
    private String checkResultReason;

    @ApiModelProperty("申诉内容")
    private String appealContent;

    @ApiModelProperty("申诉备注")
    private String appealRemark;

    @ApiModelProperty("申诉人")
    private String appealUserId;

    @ApiModelProperty("申诉时间")
    private Date appealTime;

    @ApiModelProperty("申诉结果")
    private String appealResult;

    @ApiModelProperty("申诉审批备注")
    private String appealReviewRemark;

    @ApiModelProperty("申诉实际审批人")
    private String appealActualReviewUserId;

    @ApiModelProperty("申诉审批时间")
    private Date appealReviewTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("检查项上传的图片图片数组,[{'handle':'url1','final':'url2'}]")
    private String checkPics;

    @ApiModelProperty("门店id")
    private String storeId;

    /**
     * 得分倍数
     */
    @ApiModelProperty("得分倍数")
    private BigDecimal scoreTimes;

    /**
     * 奖罚倍数
     */
    @ApiModelProperty("奖罚倍数")
    private BigDecimal awardTimes;

    /**
     * 权重
     */
    @ApiModelProperty("权重")
    private BigDecimal weightPercent;
}