package com.coolcollege.intelligent.model.patrolstore.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataColumnAppealHistoryDTO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

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

    @ApiModelProperty("申诉状态 ongoing申诉中,completed已完成")
    private String appealStatus;

    @ApiModelProperty("申诉内容")
    private String appealContent;

    @ApiModelProperty("申诉原因")
    private String appealRemark;

    @ApiModelProperty("申诉人")
    private String appealUserId;

    @ApiModelProperty("申诉人名称")
    private String appealUserName;

    @ApiModelProperty("申诉时间")
    private Date appealTime;

    @ApiModelProperty("申诉结果")
    private String appealResult;

    @ApiModelProperty("申诉审批备注")
    private String appealReviewRemark;

    @ApiModelProperty("申诉实际审批人")
    private String appealActualReviewUserId;

    @ApiModelProperty("申诉实际审批人名称")
    private String appealActualReviewUserName;

    @ApiModelProperty("申诉审批时间")
    private Date appealReviewTime;

    @ApiModelProperty("门店id")
    private String storeId;
}