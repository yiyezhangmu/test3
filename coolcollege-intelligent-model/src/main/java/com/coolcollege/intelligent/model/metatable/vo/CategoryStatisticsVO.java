package com.coolcollege.intelligent.model.metatable.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2022-12-20 10:40
 */
@ApiModel
@Data
public class CategoryStatisticsVO {

    private Long dataTableId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("不合格数量")
    private Long failNum;

    @ApiModelProperty("合格数量")
    private Long passNum;

    @ApiModelProperty("不适用数量")
    private Long inapplicableNum;

    @ApiModelProperty("总数量")
    private Long totalNum;

    @ApiModelProperty("分类总得分")
    private BigDecimal totalCheckScore;

    @ApiModelProperty("分类总分")
    private BigDecimal totalScore;
}
