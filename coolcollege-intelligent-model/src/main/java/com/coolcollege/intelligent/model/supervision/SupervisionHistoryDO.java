package com.coolcollege.intelligent.model.supervision;

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
 * @date   2023-04-10 03:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisionHistoryDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("子任务ID或者门店任务ID")
    private Long taskId;

    @ApiModelProperty("操作类型handle(处理)  approve(审批)  turn(转交) reallocate(重新分配)")
    private String operateType;

    @ApiModelProperty("操作人id")
    private String operateUserId;

    @ApiModelProperty("操作人姓名")
    private String operateUserName;

    @ApiModelProperty("转交接收人ID")
    private String toUserId;

    @ApiModelProperty("转交接收人姓名")
    private String toUserName;

    @ApiModelProperty("审核行为:pass通过 reject拒绝")
    private String actionKey;

    @ApiModelProperty("当前流程进度节点 0 1 2 3")
    private String nodeNo;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("按人/按门店 person/store")
    private String type;
}