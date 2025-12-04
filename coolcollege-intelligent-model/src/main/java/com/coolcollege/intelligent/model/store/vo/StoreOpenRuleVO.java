package com.coolcollege.intelligent.model.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-05-12 01:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreOpenRuleVO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则类型: TB_DISPLAY_TASK：新陈列任务， PATROL_STORE_ONLINE:视频巡店, PATROL_STORE_OFFLINE:线下巡店")
    private String ruleType;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("规则名称")
    private String taskName;

    @ApiModelProperty("任务创建者")
    private String createUserId;

    @ApiModelProperty("任务创建者-名称")
    private String createUserName;

    @ApiModelProperty("任务更新者")
    private String updateUserId;

    @ApiModelProperty("任务描述")
    private String taskDesc;

    @ApiModelProperty("门店规则开业日期天数")
    private Integer openDateDay;

    @ApiModelProperty("定时执行日期例周一到周五执行“1,2,3,4,5”例每月1号17号执行“1,17”")
    private String runDate;

    @ApiModelProperty("定时任务执行时间例12:00")
    private String calendarTime;

    @ApiModelProperty("子任务有效时间，例5个半小时,5.5")
    private Double limitHour;

    @ApiModelProperty("循环任务的循环轮次")
    private Integer loopCount;

    @ApiModelProperty("附件地址")
    private String attachUrl;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("节点信息")
    private String nodeInfo;

    @ApiModelProperty("非表单类任务传递内容例门店信息补全任务“store,address....”")
    private String taskInfo;

    @ApiModelProperty("检查内容")
    private String ruleInfo;
}