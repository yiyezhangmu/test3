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
public class ActivityLikeDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("活动id/评论id")
    private Long targetId;

    @ApiModelProperty("点赞人")
    private String likeUserId;

    @ApiModelProperty("点赞类型:0活动; 1评论;")
    private Integer likeType;

    @ApiModelProperty("发起人")
    private String createUserId;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}