package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDataTableColumnListVO {


    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @Excel(name = "检查项名称", orderNum = "0")
    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("检查表的ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @Excel(name = "检查项分类", orderNum = "1")
    @ApiModelProperty("检查项分类")
    private String categoryName;

    @Excel(name = "应检查门店", orderNum = "2")
    @ApiModelProperty("应检查门店")
    private Long totalColumnNum;

    @Excel(name = "已完成检查门店", orderNum = "3")
    @ApiModelProperty("已完成检查门店")
    private Long finishColumnNum;

    @Excel(name = "未检查门店", orderNum = "4")
    @ApiModelProperty("未检查门店")
    private Long unFinishColumnNum;

    @ApiModelProperty("门店完成率")
    @Excel(name = "门店完成率", orderNum = "5", format = "#.##%")
    private BigDecimal finishPercent;

    @Excel(name = "合格次数", orderNum = "6")
    @ApiModelProperty("合格次数")
    private Long passColumnNum;

    @Excel(name = "合格率", orderNum = "7", format = "#.##%")
    @ApiModelProperty("合格率")
    private BigDecimal passRate;

    @Excel(name = "不合格次数", orderNum = "8")
    @ApiModelProperty("不合格次数")
    private Long failColumnNum;

    @Excel(name = "不合格率", orderNum = "9", format = "#.##%")
    @ApiModelProperty("不合格率")
    private BigDecimal failRate;
}
