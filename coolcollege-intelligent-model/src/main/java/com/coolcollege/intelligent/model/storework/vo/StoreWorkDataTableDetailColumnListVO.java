package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDataTableDetailColumnListVO {

    @Excel(name = "检查时间", orderNum = "1")
    @ApiModelProperty("检查时间")
    private String checkTime;

    @ApiModelProperty("作业项id")
    private Long id;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @Excel(name = "檢查项名称", orderNum = "2")
    @ApiModelProperty("檢查项名称")
    private String metaColumnName;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("检查表的ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("执行人id")
    private String handlerUserId;

    @Excel(name = "执行人", orderNum = "3")
    @ApiModelProperty("执行人名称")
    private String handlerUserName;

    @Excel(name = "状态", replace = {"_null", "未完成 _0", "已完成_1"}, orderNum = "4")
    @ApiModelProperty("状态 0:未完成 1:已完成")
    private Integer submitStatus;

    @ApiModelProperty("店务日期")
    private String storeWorkDate;


}
