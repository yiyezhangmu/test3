package com.coolcollege.intelligent.model.activity.dto;

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
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReplyDTO implements Serializable {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("回复父id")
    private Long parentReplyId;

    @ApiModelProperty("回复内容")
    private String content;
}