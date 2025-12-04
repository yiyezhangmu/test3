package com.coolcollege.intelligent.model.activity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: ActivityCommentReplyIdDTO
 * @Description:
 * @date 2023-07-06 10:40
 */
@Data
public class ActivityCommentReplyIdDTO {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("回复id")
    private Long replyId;

}
