package com.coolcollege.intelligent.model.unifytask.vo;

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
 * @date   2022-08-04 11:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyTaskParentItemVO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String itemName;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("任务创建者")
    private String createUserId;

    @ApiModelProperty("任务创建时间")
    private Date createTime;

    @ApiModelProperty("任务更新者")
    private String updateUserId;

    @ApiModelProperty("任务更新时间")
    private Date updateTime;

    @ApiModelProperty("任务描述")
    private String taskDesc;

    @ApiModelProperty("对应流程模板id")
    private String templateId;

    @ApiModelProperty("节点信息")
    private String nodeInfo;

    @ApiModelProperty("任务数据")
    private String taskInfo;
}