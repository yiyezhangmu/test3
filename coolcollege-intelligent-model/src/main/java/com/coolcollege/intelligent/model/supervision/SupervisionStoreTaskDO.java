package com.coolcollege.intelligent.model.supervision;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 14:59
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionStoreTaskDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("督导父任务ID")
    private Long taskParentId;

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("结束时间")
    private Date taskEndTime;

    @ApiModelProperty("督导ID")
    private String supervisionUserId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("完成时间")
    private Date completeTime;

    @ApiModelProperty("待完成 待审核 按时完成 逾期完成")
    private Integer taskState;

    @ApiModelProperty("手动填写的文本")
    private String formId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("取消状态 0正常 1取消")
    private Integer cancelStatus;

    @ApiModelProperty("提交时间")
    private Date submitTime;
    @ApiModelProperty("优先级")
    private String priority;
    @ApiModelProperty("附件ID集合")
    private String sopIds;
    @ApiModelProperty("督导任务表ID")
    private Long supervisionTaskId;


    @ApiModelProperty("任务分组")
    private String taskGrouping;
    @ApiModelProperty("一级审批人")
    private String firstApprove;
    @ApiModelProperty("二级审批人")
    private String secondaryApprove;
    @ApiModelProperty("三级审批人")
    private String thirdApprove;
    @ApiModelProperty("当前节点 0处理节点 1一级审批人 2 二级审批人 3三级审批人 9 完成")
    private Integer currentNode;
    @ApiModelProperty("开始前提醒时间")
    private Date reminderTimeBeforeStarting;
    @ApiModelProperty("结束前提醒时间")
    private Date reminderTimeBeforeEnd;
    @ApiModelProperty("转交重新分配标识 默认0 转交1 重新分配2")
    private Integer transferReassignFlag;
    @ApiModelProperty("执行是否逾期")
    private Integer handleOverTimeStatus;
    @ApiModelProperty("区域路径")
    private String regionPath;
    @ApiModelProperty("任务执行人ID")
    private String supervisionHandleUserId;
    @ApiModelProperty("任务执行人名称")
    private String supervisionHandleUserName;
    @ApiModelProperty("审批状态 0无需操作 1 待审批 2 审批通过 3审批驳回")
    private Integer approveStatus;
}