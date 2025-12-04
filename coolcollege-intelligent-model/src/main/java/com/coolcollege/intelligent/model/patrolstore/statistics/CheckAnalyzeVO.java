package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckAnalyzeVO {
    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("区域路径")
    private String regionWay;


    @ApiModelProperty("所属区域")
    @Excel(name = "区域", orderNum = "0")
    private String fullRegionName;

    @ApiModelProperty("所在二级架构名称")
//    @Excel(name = "所在二级架构名称", orderNum = "1")
    private String regionName1;

    @ApiModelProperty("所在三级架构名称")
//    @Excel(name = "所在三级架构名称", orderNum = "2")
    private String regionName2;

    @ApiModelProperty("所在四级架构名称")
//    @Excel(name = "所在四级架构名称", orderNum = "3")
    private String regionName3;

    /**
     * 区域名称列表
     */
    private List<String> regionNameList;


    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    @Excel(name = "巡店人", orderNum = "4")
    private String supervisorName;

    @ApiModelProperty("巡店人工号")
    @Excel(name = "巡店人工号", orderNum = "5")
    private String supervisorJobNum;

    @ApiModelProperty("检查表名称")
    @Excel(name = "检查表", orderNum = "6")
    private String tableName;

    @ApiModelProperty("稽核任务数")
    @Excel(name = "稽核任务数", orderNum = "7")
    private Integer checkTotalNum;

    @ApiModelProperty("大区已稽核任务数")
    @Excel(name = "大区已稽核任务数", orderNum = "8")
    private Integer bigRegionCheckNum;

    @ApiModelProperty("大区未稽核任务数")
    @Excel(name = "大区未稽核任务数", orderNum = "9")
    private Integer bigRegionNotCheckNum;

    @ApiModelProperty("大区稽核完成率")
    @Excel(name = "大区稽核完成率", orderNum = "10")
    private BigDecimal bigRegionCheckRate;

    @ApiModelProperty("战区已稽核任务数")
    @Excel(name = "战区已稽核任务数", orderNum = "11")
    private Integer warCheckNum;

    @ApiModelProperty("战区未稽核任务数")
    @Excel(name = "战区未稽核任务数", orderNum = "12")
    private Integer warNotCheckNum;

    @ApiModelProperty("战区稽核完成率")
    @Excel(name = "战区稽核完成率", orderNum = "13")
    private BigDecimal warCheckRate;
}
