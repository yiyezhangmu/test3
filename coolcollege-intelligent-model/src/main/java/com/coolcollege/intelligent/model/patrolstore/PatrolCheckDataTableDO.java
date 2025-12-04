package com.coolcollege.intelligent.model.patrolstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-09-03 11:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolCheckDataTableDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long taskId;

    @ApiModelProperty("子任务id")
    private Long subTaskId;

    @ApiModelProperty("稽核id")
    private Long checkBusinessId;

    @ApiModelProperty("稽核记录id")
    private Long businessId;

    @ApiModelProperty("巡检记录检查表")
    private Long metaTableId;

    @ApiModelProperty("检查表数据表id")
    private Long dataTableId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("巡检标准表 || 巡检自定义表")
    private String tableType;

    @ApiModelProperty("提交标识 0：未提交 1:已提交")
    private Integer submitStatus;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("检查表总分")
    private BigDecimal totalScore;

    @ApiModelProperty("检查表总得分")
    private BigDecimal checkScore;

    @ApiModelProperty("参与计算的任务总分 根据适用项规则计算得出")
    private BigDecimal taskCalTotalScore;

    @ApiModelProperty("表属性 0:普通表 1:高级表 2:加分表 3:权重表 4:扣分表 5:AI检查表 6:自定义表")
    private Integer tableProperty;

    @ApiModelProperty("任务提交后的获得金额")
    private BigDecimal totalResultAward;

    @ApiModelProperty("0:不计入总项数，1:计入总项数")
    private Boolean noApplicableRule;

    @ApiModelProperty("不合格数")
    private Integer failNum;

    @ApiModelProperty("合格数")
    private Integer passNum;

    @ApiModelProperty("不适用数")
    private Integer inapplicableNum;

    @ApiModelProperty("参与计算总项数,通过表中no_applicable_rule字段得出的结果")
    private Integer totalCalColumnNum;

    @ApiModelProperty("采集项")
    private Integer collectColumnNum;

    @ApiModelProperty("检查结果")
    private String checkResultLevel;

    @ApiModelProperty("巡店类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    private String patrolType;

    @ApiModelProperty("稽核类型  1：大区稽核 2:战区稽核")
    private Integer checkType;
}