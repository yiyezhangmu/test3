package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class DataStaTableColumnVO implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("任务类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    private String patrolType;

    @Excel(name = "任务类型", orderNum = "1")
    private String patrolTypeName;

    @ApiModelProperty("子任务id")
    private Long subTaskId;

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

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域", orderNum = "4")
    private String fullRegionName;

    /**
     * 区域名称列表
     */
    private List<String> regionNameList;

    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("记录类型")
    private String businessType;

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

    @ApiModelProperty("检查项名称")
    @Excel(name = "检查项", orderNum = "8")
    private String metaColumnName;

    @ApiModelProperty("检查项结果")
    private String checkResult;

    @ApiModelProperty("检查项结果id")
    private Long checkResultId;

    @ApiModelProperty("检查项结果名称")
    @Excel(name = "检查结果", orderNum = "9")
    private String checkResultName;

    @ApiModelProperty("标准图")
    @Excel(name = "标准图", orderNum = "10")
    private String standardPic;

    @ApiModelProperty(value = "巡店合格项数")
    @Excel(name = "巡店合格项数", orderNum = "11")
    private Integer passNum;

    @ApiModelProperty("检查项的得分")
    @Excel(name = "得分", orderNum = "12")
    private BigDecimal checkScore;

    @ApiModelProperty("检查项不合格原因")
    private String checkResultReason;

    @ApiModelProperty(value = "巡店不合格原因汇总")
    @Excel(name = "不合格原因汇总", orderNum = "13")
    private String checkResultReasons;

    @ApiModelProperty("巡店结果图")
    @Excel(name = "巡店结果图", orderNum = "14")
    private String checkPics;

    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    @Excel(name = "大区稽核人", orderNum = "15")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核时间")
    @Excel(name = "大区稽核时间", orderNum = "16", exportFormat = "yyyy.MM.dd HH:mm")
    private Date bigRegionCheckTime;

    @ApiModelProperty(value = "大区稽核得分（值）")
    @Excel(name = "大区稽核得分（值）", orderNum = "17")
    private BigDecimal bigRegionCheckScore;

    @ApiModelProperty(value = "大区稽核结果")
    private String bigRegionCheckResultLevel;

    @Excel(name = "大区稽核结果", orderNum = "18")
    private String bigRegionCheckResultName;

    @ApiModelProperty(value = "大区稽核不合格原因")
    @Excel(name = "大区稽核不合格原因", orderNum = "19")
    private String bigRegionCheckResultReason;

    @ApiModelProperty("大区稽核结果图")
    @Excel(name = "大区稽核结果图", orderNum = "20")
    private String bigRegionCheckPics;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    @Excel(name = "战区稽核人", orderNum = "21")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    @Excel(name = "战区稽核人工号", orderNum = "22")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核时间")
    @Excel(name = "战区稽核时间", orderNum = "23", exportFormat = "yyyy.MM.dd HH:mm")
    private Date warZoneCheckTime;

    @ApiModelProperty(value = "战区稽核得分（值）")
    @Excel(name = "战区稽核得分（值）", orderNum = "24")
    private BigDecimal warCheckScore;

    @ApiModelProperty(value = "战区稽核结果")
    private String warResultLevel;

    @Excel(name = "战区稽核结果", orderNum = "25")
    private String warResultName;

    @ApiModelProperty(value = "战区稽核不合格原因")
    @Excel(name = "战区稽核不合格原因", orderNum = "26")
    private String warResultReason;

    @ApiModelProperty("战区稽核结果图")
    @Excel(name = "战区稽核结果图", orderNum = "27")
    private String warCheckPics;



    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date editTime;

    @ApiModelProperty("数据表id")
    private Long dataTableId;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("columnID")
    private Long metaColumnId;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;

    @ApiModelProperty("检查项的描述信息")
    private String checkText;

    @ApiModelProperty("上报人ID")
    private String handlerUserId;

    @ApiModelProperty("审核人员ID")
    private String checkUserId;

    @ApiModelProperty("复核人员ID")
    private String reCheckUserId;

    @ApiModelProperty("问题任务状态")
    private String taskQuestionStatus;

    @ApiModelProperty("问题工单ID")
    private Long taskQuestionId;

    @ApiModelProperty("检查项是否已经上报")
    private Integer submitStatus;

    @ApiModelProperty("业务记录状态")
    private Integer businessStatus;

    @ApiModelProperty("删除标记")
    private Integer deleted;

    @ApiModelProperty("创建日期")
    private String createDate;

    @ApiModelProperty("奖罚金额")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("门店场景名称")
    private String storeSceneName;

    @ApiModelProperty("门店场景id")
    private Long storeSceneId;

    /**
     * 巡店时间 项的处理时间
     */
    @ApiModelProperty("巡店时间 项的处理时间")
    private Date patrolStoreTime;

    /**
     * 得分倍数
     */
    @ApiModelProperty("得分倍数")
    private BigDecimal scoreTimes;

    /**
     * 奖罚倍数
     */
    @ApiModelProperty("奖罚倍数")
    private BigDecimal awardTimes;

    /**
     * 权重
     */
    @ApiModelProperty("权重")
    private BigDecimal weightPercent;

    /**
     * 检查项总分 根据不适用配置计算得出
     */
    @ApiModelProperty("检查项总分")
    private BigDecimal columnMaxScore;

    /**
     * 检查项最高奖励 根据不适用配置计算得出
     */
    @ApiModelProperty("检查项最高奖励")
    private BigDecimal columnMaxAward;

    /**
     *  0普通项,1高级项,2红线项,3否决项,4加倍项,5采集项,6AI项
     */
    @ApiModelProperty("检查项类型")
    private Integer columnType;


    private static final long serialVersionUID = 1L;
}
