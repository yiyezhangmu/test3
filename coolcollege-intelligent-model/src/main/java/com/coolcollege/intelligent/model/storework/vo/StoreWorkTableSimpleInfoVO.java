package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/9/15 14:37
 */
@ApiModel
@Data
public class StoreWorkTableSimpleInfoVO {

    @ApiModelProperty("表名称")
    private String tableName;

    @ApiModelProperty("表Id")
    private Long metaTableId;

    @ApiModelProperty("表Id")
    private Long dataTableId;

    @ApiModelProperty("得分")
    private BigDecimal score;

    @ApiModelProperty("总分")
    private BigDecimal totalScore;

    @ApiModelProperty("得分率")
    private String scoreRate;

    @ApiModelProperty("合格项数")
    private Integer passColumnNum;

    @ApiModelProperty("总项数")
    private Integer totalColumnNum;

    @ApiModelProperty("合格率")
    private String passRate;

    @ApiModelProperty("是否点评 0:未点评 1:已点评")
    private Integer commentStatus;

    @ApiModelProperty("是否完成 0:未执行 1:已执行")
    private Integer completeStatus;

    @ApiModelProperty("作业开始时间")
    private String beginTime;

    @ApiModelProperty("作业结束时间")
    private String endTime;
}
