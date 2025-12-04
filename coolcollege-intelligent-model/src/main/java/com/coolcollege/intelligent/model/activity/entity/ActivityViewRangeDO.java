package com.coolcollege.intelligent.model.activity.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityViewRangeDO implements Serializable {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("部门ID")
    private String regionId;

    @ApiModelProperty("人员ID")
    private String personalId;

    @ApiModelProperty("人:personal;部门:region")
    private String nodeType;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}