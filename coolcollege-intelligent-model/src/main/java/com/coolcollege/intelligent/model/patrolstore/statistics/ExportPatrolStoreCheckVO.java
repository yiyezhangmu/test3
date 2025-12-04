package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ExportPatrolStoreCheckVO {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long taskId;

    @ApiModelProperty("巡店类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    @Excel(name = "任务类型", orderNum = "1")
    private String patrolType;

    @ApiModelProperty("稽核记录id")
    private Long businessId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店编号")
    @Excel(name = "门店编号", orderNum = "2")
    private String storeNum;

    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称", orderNum = "3")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域", orderNum = "4")
    private String fullRegionName;

    @ApiModelProperty("区域路径(新)")
    private String regionWay;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    @Excel(name = "巡店人", orderNum = "5")
    private String supervisorName;

    @ApiModelProperty("巡店人工号")
    @Excel(name = "巡店人工号", orderNum = "6")
    private String supervisorJobNum;

    @ApiModelProperty("任务名称")
    @Excel(name = "任务名称", orderNum = "7")
    private String taskName;

    @ApiModelProperty("巡店开始时间")
    private String signStartTime;

    @ApiModelProperty("巡店结束时间")
    private String signEndTime;

    @ApiModelProperty("子任务审批链开始时间")
    private String subBeginTime;

    @ApiModelProperty("子任务审批链结束时间")
    private String subEndTime;

    @Excel(name = "任务时间", orderNum = "8")
    private String dataTime;

    @ApiModelProperty(value = "任务内容")
    @Excel(name = "任务内容", orderNum = "9")
    private String tableName;

    @ApiModelProperty(value = "检查项数")
    @Excel(name = "检查项数", orderNum = "10",type = 10)
    private Integer checkNum;

    @ApiModelProperty(value = "巡店合格项数")
    @Excel(name = "巡店合格项数", orderNum = "11",type = 10)
    private Integer passNum;

    @ApiModelProperty(value = "巡店不合格项数")
    @Excel(name = "巡店不合格项数", orderNum = "12",type = 10)
    private Integer failNum;

    @ApiModelProperty(value = "总分值")
    @Excel(name = "总分值", orderNum = "13",type = 10)
    private BigDecimal totalScore;

    @ApiModelProperty(value = "巡店得分")
    @Excel(name = "巡店得分", orderNum = "14",type = 10)
    private BigDecimal checkScore;

    @ApiModelProperty(value = "巡店结果")
    @Excel(name = "巡店结果", orderNum = "15")
    private String checkResultLevel;

    @ApiModelProperty(value = "不合格原因汇总")
    @Excel(name = "不合格原因汇总", orderNum = "16")
    private String checkResultReason;

    @ApiModelProperty("稽核状态")
//    @Excel(name = "稽核状态", orderNum = "17")
    private String status;

    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    @Excel(name = "大区稽核人", orderNum = "17")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核时间")
    @Excel(name = "大区稽核时间", orderNum = "18")
    private String bigRegionCheckTime;

    @ApiModelProperty(value = "大区稽核得分（值）")
    @Excel(name = "大区稽核得分（值）", orderNum = "19",type = 10)
    private BigDecimal bigRegionCheckScore;

    @ApiModelProperty(value = "大区稽核结果")
    @Excel(name = "大区稽核结果", orderNum = "20")
    private String bigRegionCheckResultLevel;

    @ApiModelProperty(value = "大区稽核不合格原因")
    @Excel(name = "大区稽核不合格原因", orderNum = "21")
    private String bigRegionCheckResultReason;

    @ApiModelProperty("大区稽核状态 0: 待稽核 1:已稽核")
    private Integer bigRegionCheckStatus;

    @Excel(name = "大区稽核状态", orderNum = "22")
    private String bigRegionCheckStatusName;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    @Excel(name = "战区稽核人", orderNum = "23")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核时间")
    @Excel(name = "战区稽核时间", orderNum = "24")
    private String warZoneCheckTime;

    @ApiModelProperty(value = "战区稽核得分（值）")
    @Excel(name = "战区稽核得分（值）", orderNum = "25",type = 10)
    private BigDecimal warCheckScore;

    @ApiModelProperty(value = "战区稽核结果")
    @Excel(name = "战区稽核结果", orderNum = "26")
    private String warResultLevel;

    @ApiModelProperty(value = "战区稽核不合格原因")
    @Excel(name = "战区稽核不合格原因", orderNum = "27")
    private String warResultReason;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    private Integer warZoneCheckStatus;

    @ApiModelProperty("战区稽核状态 0: 待稽核 1:已稽核")
    @Excel(name = "战区稽核状态", orderNum = "28")
    private String warZoneCheckStatusName;

}
