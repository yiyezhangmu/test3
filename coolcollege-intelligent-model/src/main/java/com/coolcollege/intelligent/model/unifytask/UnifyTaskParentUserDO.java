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
 * @author   zhangnan
 * @date   2022-02-23 09:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTaskParentUserDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("任务类型")
    private String taskType;

    @ApiModelProperty("处理人id")
    private String userId;

    @ApiModelProperty("开始时间")
    private Long beginTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("父任务状态")
    private String parentStatus;

    @ApiModelProperty("父任务创建时间")
    private Long parentCreateTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}