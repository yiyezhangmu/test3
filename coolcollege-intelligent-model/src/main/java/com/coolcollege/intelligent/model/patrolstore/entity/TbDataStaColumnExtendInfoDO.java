package com.coolcollege.intelligent.model.patrolstore.entity;

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
 * @author   zhangchenbiao
 * @date   2025-07-10 02:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataStaColumnExtendInfoDO implements Serializable {
    @ApiModelProperty("数据项Id")
    private Long id;
    
    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("子任务id")
    private Long subTaskId;

    @ApiModelProperty("AI检查项结果:PASS,FAIL,INAPPLICABLE")
    private String aiCheckResult;

    @ApiModelProperty("AI检查项结果id")
    private Long aiCheckResultId;

    @ApiModelProperty("AI检查项结果名称")
    private String aiCheckResultName;

    @ApiModelProperty("AI检测结果图")
    private String aiCheckPics;

    @ApiModelProperty("AI点评内容")
    private String aiCheckText;

    @ApiModelProperty("AI检查项分值")
    private BigDecimal aiCheckScore;

    @ApiModelProperty("AI执行状态，0未执行 1分析中 2已完成 3失败")
    private Integer aiStatus;

    @ApiModelProperty("AI执行失败原因")
    private String aiFailReason;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}