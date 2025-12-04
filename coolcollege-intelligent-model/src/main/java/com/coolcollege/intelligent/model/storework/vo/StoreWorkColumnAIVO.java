package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  用户AI巡检的导出
 */
@Data
public class StoreWorkColumnAIVO extends StoreWorkColumnVO {

    @ApiModelProperty("AI检查项结果名称")
    @Excel(name = "AI点评结果", orderNum = "18")
    private String aiCheckResultName;

    @ApiModelProperty("AI检查项分值")
    @Excel(name = "AI点评得分", orderNum = "19")
    private BigDecimal aiCheckScore;

    @ApiModelProperty("AI点评内容")
    @Excel(name = "AI点评内容", orderNum = "20")
    private String aiCommentContent;
}
