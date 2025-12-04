package com.coolcollege.intelligent.model.supervision;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-02-01 02:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionTaskDO implements Serializable {
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

    @ApiModelProperty("检查门店ID或人员id ")
    private String checkObjectIds;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("完成时间")
    private Date completeTime;

    @ApiModelProperty("完成比例（0或1，或小数，本次只有0或1）")
    private BigDecimal completePercent;

    @ApiModelProperty("已逾期，进行中，已完成")
    private Integer taskState;

    @ApiModelProperty("手动填写的文本")
    private String manualText;

    @ApiModelProperty("手动上传的图片")
    private String manualPics;

    @ApiModelProperty("手动上传的附件")
    private String manualAttach;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("取消标识")
    private Integer cancelStatus;
    @ApiModelProperty("表单ID")
    private String formId;
    @ApiModelProperty("附件ID")
    private String sopIds;
    @ApiModelProperty("优先级")
    private String priority;


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
    @ApiModelProperty("提交时间")
    private Date submitTime;
    @ApiModelProperty("任务执行人ID")
    private String supervisionHandleUserId;
    @ApiModelProperty("任务执行人名称")
    private String supervisionHandleUserName;
    @ApiModelProperty("审批状态 0无需操作 1 待审批 2 审批通过 3审批驳回")
    private Integer approveStatus;

}