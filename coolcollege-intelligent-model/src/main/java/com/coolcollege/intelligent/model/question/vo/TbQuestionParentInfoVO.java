package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class TbQuestionParentInfoVO implements Serializable {

    @ApiModelProperty("工单编号")
    @Excel(name = "工单编号",orderNum = "0")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("状态 0:未完成  1:已完成")
    private Integer status;

    @ApiModelProperty("工单名称" )
    @Excel(name = "工单名称",orderNum = "2",width = 20)
    private String questionName;

    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    @Excel(name = "工单来源",replace = {"_null","AI工单_AI","普通工单_common","巡店工单_patrolStore"},orderNum = "1" ,width = 20)
    private String questionType;

    @ApiModelProperty("已完成数量")
    private Integer finishNum;

    @ApiModelProperty("进度 导出时使用")
    @Excel(name = "进度" ,orderNum = "3")
    private String plannedSpeed;

    @ApiModelProperty("工单总数量")
    @Excel(name = "子工单",orderNum = "4")
    private Integer totalNum;

    @ApiModelProperty("创建人")
    private String createId;

    @ApiModelProperty("创建人名称")
    @Excel(name = "创建人名称" ,orderNum = "5",width = 20)
    private String createUserName;

    @ApiModelProperty("创建时间" )
    @Excel(name = "发起时间",format = "yyyy.MM.dd HH:mm" ,orderNum = "6" ,width = 20)
    private Date createTime;
}