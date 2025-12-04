package com.coolcollege.intelligent.model.supervision;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
public class SupervisionTaskParentDO implements Serializable {
    @ApiModelProperty("父任务ID")
    private Long id;

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("结束时间")
    private Date taskEndTime;

    @ApiModelProperty("执行人")
    private String executePersons;

    @ApiModelProperty("优先级 固定选项：紧急,一般")
    private String priority;

    @ApiModelProperty("检验code")
    private String checkCode;

    @ApiModelProperty("处理方式 {code:1,name:',id:'} code值 0 无需操作,1 填写表单,2 点击按钮")
    private String handleWay;

    @ApiModelProperty("任务描述")
    private String description;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("检查门店ID")
    private String checkStoreIds;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("标签（支持多个 逗号隔开）")
    private String tags;

    @ApiModelProperty("表单的ID")
    private String formId;

    @ApiModelProperty("任务创建者")
    private String createUserId;

    @ApiModelProperty("任务更新者")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("取消状态 0正常 1取消")
    private Integer cancelStatus;

    @ApiModelProperty("附件ID集合")
    private String sopIds;

    @ApiModelProperty("失效状态 0正常 1失效")
    private Integer failureState;

    @ApiModelProperty("任务分组")
    private String taskGrouping;

    @ApiModelProperty("审批流程信息")
    private String processInfo;

    @ApiModelProperty("定时提醒具体信息")
    private String timingInfo;

    @ApiModelProperty("任务创建者")
    private String createUserName;

}