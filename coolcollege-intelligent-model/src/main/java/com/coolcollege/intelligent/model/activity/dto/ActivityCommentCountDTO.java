package com.coolcollege.intelligent.model.activity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCommentCountDTO implements Serializable {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论人次")
    private Integer commentsCount;

    @ApiModelProperty("评论人数")
    private Integer commentsUserCount;
}