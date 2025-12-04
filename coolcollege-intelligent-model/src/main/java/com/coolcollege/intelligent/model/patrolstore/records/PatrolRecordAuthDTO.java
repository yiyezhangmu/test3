package com.coolcollege.intelligent.model.patrolstore.records;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author byd
 * @date 2021-04-25 11:01
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatrolRecordAuthDTO {

    /**
     * 是否为管理员
     */
    private Boolean admin;

    /**
     * 是否是创建这
     */
    private Boolean creater;

    /**
     * 是否有区域权限
     */
    private Boolean region;

    /**
     * 是否为抄送人
     */
    private Boolean ccPeople;

    /**
     * 是否为处理人
     */
    private Boolean handler;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 是否有检查表查看权限
     */
    private Boolean checkTable;

    /**
     * 是否逾期
     */
    private Boolean overdue;


    /**
     * 是否逾期可执行
     */
    private Boolean overdueRun;

    private Long subTaskId;

    /**
     * 是否拥有结果查看权限
     */
    private Boolean resultAuth;
    /**
     * 当前流程进度节点
     */
    private String nodeNo;

    /**
     * 是否拥有巡店内容查看权限
     */
    private Boolean viewAuth;

    /**
     * 是否可申诉
     */
    @ApiModelProperty("是否可申诉")
    private Boolean appealValid;

    /**
     * 是否可申诉
     */
    @ApiModelProperty("可申诉截止时间")
    private Date appealValidDate;

    /**
     * 是否可以对申诉审核
     */
    @ApiModelProperty("是否可以对申诉审核")
    private Boolean appealAuditAuth;

    @ApiModelProperty("处理批次，0,1,2")
    private Integer cycleCount;

    /**
     * 是否需要选择不合格不适用原因0不选择 1选择
     */
    @ApiModelProperty("稽核执行时是否需要选择不合格不适用原因0不选择 1选择")
    private Boolean selectReason;

    /**
     * 是否逾期可执行
     */
    private boolean overdueTaskContinue;


}
