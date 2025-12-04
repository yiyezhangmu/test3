package com.coolcollege.intelligent.model.unifytask;

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
 * @date   2022-04-14 03:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTaskPersonDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("父任务id")
    private Long taskId;

    @ApiModelProperty("子任务id")
    private Long subTaskId;

    @ApiModelProperty("任务处理者")
    private String handleUserId;

    @ApiModelProperty("循环任务的循环批次")
    private Long loopCount;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("完成情况，同子任务一致")
    private String subStatus;

    @ApiModelProperty("计划执行要求{\"patrolParam\":{\"storeNum\":10,   巡店数量  \"isDistinct\":true    是否去重}}")
    private String executeDemand;

    @ApiModelProperty("已巡门店id集合,分隔")
    private String storeIds;

    @ApiModelProperty("完成时间")
    private Date completeTime;
    @ApiModelProperty("开始时间")
    private Date subBeginTime;
    @ApiModelProperty("结束时间")
    private Date subEndTime;
}