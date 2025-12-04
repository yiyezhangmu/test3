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
public class CheckDataStaColumnDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("记录id")
    private Long businessId;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("稽核数据表的ID")
    private Long checkDataTableId;

    @ApiModelProperty("巡店数据项id")
    private Long dataStaColumnId;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("columnID")
    private Long metaColumnId;

    @ApiModelProperty("属性名称")
    private String metaColumnName;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("巡店人")
    private String supervisorId;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("检查项结果id")
    private Long checkResultId;

    @ApiModelProperty("检查项结果名称")
    private String checkResultName;

    @ApiModelProperty("检查项的描述信息")
    private String checkText;

    @ApiModelProperty("检查项分值")
    private BigDecimal checkScore;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("奖罚金额 正数奖励金额 负数罚款金额")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("巡店时间")
    private Date patrolStoreTime;

    @ApiModelProperty("得分倍数")
    private BigDecimal scoreTimes;

    @ApiModelProperty("奖罚倍数")
    private BigDecimal awardTimes;

    @ApiModelProperty("权重百分比")
    private BigDecimal weightPercent;

    @ApiModelProperty("检查项最高分 根据不适用配置计算得出，各项累计可得出表的总分")
    private BigDecimal columnMaxScore;

    @ApiModelProperty("检查项最高奖励 根据不适用配置计算得出")
    private BigDecimal columnMaxAward;

    @ApiModelProperty("0普通项,1高级项,2红线项,3否决项,4加倍项,5采集项,6AI项")
    private Integer columnType;

    @ApiModelProperty("巡店类型:PATROL_STORE_OFFLINE,PATROL_STORE_ONLINE")
    private String patrolType;

    @ApiModelProperty("不合格原因")
    private String checkResultReason;

    @ApiModelProperty("稽核类型  1：大区稽核 2:战区稽核")
    private Integer checkType;

    @ApiModelProperty("检查项上传的图片")
    private String checkPics;

    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;
}