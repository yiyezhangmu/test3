package com.coolcollege.intelligent.model.inspection.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI巡检抓拍时间段信息表
 * @author   zhangchenbiao
 * @date   2025-09-25 04:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionTimePeriodDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("ai巡检表id")
    private Long inspectionId;

    @ApiModelProperty("开始时间 如 09:00")
    private String beginTime;

    @ApiModelProperty("结束时间 如 21:00")
    private String endTime;

    @ApiModelProperty("间隔时间,分钟")
    private Integer period;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者ID")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人ID")
    private String updateUserId;

    @ApiModelProperty("删除标识:0-未删除,1-已删除")
    private Boolean deleted;
}