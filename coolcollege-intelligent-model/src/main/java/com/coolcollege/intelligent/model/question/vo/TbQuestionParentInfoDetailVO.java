package com.coolcollege.intelligent.model.question.vo;

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
 * @date   2022-08-04 11:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionParentInfoDetailVO implements Serializable {

    @ApiModelProperty("工单编号")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("状态 0:未完成  1:已完成")
    private Integer status;

    @ApiModelProperty("工单名称")
    private String questionName;

    @ApiModelProperty("AI工单 AI、普通检查项 common、巡店工单 patrolStore")
    private String questionType;

    @ApiModelProperty("已完成数量")
    private Integer finishNum;

    @ApiModelProperty("工单总数量")
    private Integer totalNum;

    @ApiModelProperty("创建人")
    private String createId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("待整改数量")
    private Integer waitRectifiedNum;

    @ApiModelProperty("待审批数量")
    private Integer waitApproveNum;

    @ApiModelProperty("巡店记录id, null表示没有巡店记录")
    private Long businessId;

    @ApiModelProperty("店务businessId")
    private String tcBusinessId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("签退时间")
    private Date signEndTime;
}