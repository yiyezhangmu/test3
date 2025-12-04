package com.coolcollege.intelligent.model.question;

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
public class TbQuestionParentInfoDO implements Serializable {

    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("状态 0:未完成  1:已完成")
    private Integer status;

    @ApiModelProperty("工单名称")
    private String questionName;

    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    private String questionType;

    @ApiModelProperty("已完成数量")
    private Integer finishNum;

    @ApiModelProperty("工单总数量")
    private Integer totalNum;

    @ApiModelProperty("创建人")
    private String createId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateId;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}