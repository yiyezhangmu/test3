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
public class ActivityReplyDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("回复人")
    private String replyUserId;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("回复父id")
    private Long parentReplyId;

    @ApiModelProperty("发起人")
    private String createUserId;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}