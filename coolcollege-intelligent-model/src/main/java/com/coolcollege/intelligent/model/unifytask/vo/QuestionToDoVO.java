package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 工单待办vo
 * @author byd
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionToDoVO {

    /**
     * ID
     */
    @ApiModelProperty("父工单id")
    private Long id;
    /**
     * 任务名称
     */
    @ApiModelProperty("工单名称")
    private String questionName;

    /**
     * 发起时间
     */
    @ApiModelProperty("发起时间")
    private Date createTime;
    /**
     * 发起人id
     */
    @ApiModelProperty("发起人id")
    private String createId;

    @ApiModelProperty("发起人名称")
    private String createName;

    /**
     * 工单来源
     */
    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    private String questionType;

    @ApiModelProperty("已完成数量")
    private Integer finishNum;

    @ApiModelProperty("工单总数量")
    private Integer totalNum;

    @ApiModelProperty("工单子任务结束时间")
    private Date subRecordEndTime;

}