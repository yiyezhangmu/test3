package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/9/16 10:27
 * @Version 1.0
 */
@Data
@ApiModel(value = "门店日清周清月清执行页响应VO")
public class StoreWorkExecutionPageVO {
    @ApiModelProperty("检查表ID")
    private Long metaTableId;
    @ApiModelProperty("检查表名称")
    private String metaTableName;
    @ApiModelProperty("检查表总项数")
    private Integer totalColumnNum;
    @ApiModelProperty("采集项数量")
    private Integer collectColumnNum;
    @ApiModelProperty("检查表已完成项数")
    private Integer finishColumnNum;
    @ApiModelProperty("完成状态")
    private Integer completeStatus;
    @ApiModelProperty("点评状态")
    private Integer commentStatus;
    @ApiModelProperty("任务开始时间")
    private Date beginTime;
    @ApiModelProperty("任务结束时间")
    private Date endTime;
    @ApiModelProperty("businessId")
    private String businessId;
    @ApiModelProperty("dataTableId")
    private Long dataTableId;

    @ApiModelProperty("查询时间(日清 当天时间 周清 当周星期一  月清 当月1号)")
    private Date currentDate;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("逾期是否继续执行")
    private Integer overdueContinue;

    @ApiModelProperty("表时间设置信息")
    private String tableInfo;

    @ApiModelProperty("是否需要执行AI")
    private Integer isAiProcess;
}
