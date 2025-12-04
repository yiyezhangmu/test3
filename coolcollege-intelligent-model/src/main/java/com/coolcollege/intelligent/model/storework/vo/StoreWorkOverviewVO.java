package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/19 17:10
 * @Version 1.0
 */
@Data
@ApiModel(value = "店务概况VO")
public class StoreWorkOverviewVO extends StoreWorkAIFieldVO {
    @ApiModelProperty("开始时间")
    private  Date beginTime;
    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("开始时间")
    private  Date beginHandleTime;
    @ApiModelProperty("结束时间")
    private Date endHandleTime;
    @ApiModelProperty("检查表名称")
    private  String metaTableName;
    @ApiModelProperty("执行人信息")
    private HandlerUserVO handlerUserVO;
    @ApiModelProperty("dataTableId")
    private  Long dataTableId;
    @ApiModelProperty("businessId")
    private  String businessId;
    @ApiModelProperty("总项数")
    private Integer totalColumnNum;
    @ApiModelProperty("合格")
    private Integer passColumnNum;
    @ApiModelProperty("不合格")
    private Integer failColumnNum;
    @ApiModelProperty("不适用")
    private Integer inapplicableColumnNum;
    @ApiModelProperty("平均得分")
    private BigDecimal avgScore;
    @ApiModelProperty("得分")
    private BigDecimal score;
    @ApiModelProperty("平均得分率")
    private String avgScoreRate;
    @ApiModelProperty("得分率")
    private String scoreRate;
    @ApiModelProperty("平均合格率")
    private String avgPassRate;
    @ApiModelProperty("合格率")
    private String passRate;
    @ApiModelProperty("不合格率")
    private String failRate;
    @ApiModelProperty("不适用率")
    private String inapplicableRate;
    @ApiModelProperty("采集项")
    private Integer collectColumnNum;
    @ApiModelProperty("检查结果等级")
    private String checkResultLevel;
    @ApiModelProperty("完成状态")
    private Integer completeStatus;
    @ApiModelProperty("点评状态")
    private Integer commentStatus;
    @ApiModelProperty("表属性")
    private Integer tableProperty;
    @ApiModelProperty("表设置")
    private String tableInfo;
    @ApiModelProperty("点评显示标签flag")
    private Boolean commentTabDisplayFlag;
    @ApiModelProperty("日清类型")
    private String workCycle;
}
