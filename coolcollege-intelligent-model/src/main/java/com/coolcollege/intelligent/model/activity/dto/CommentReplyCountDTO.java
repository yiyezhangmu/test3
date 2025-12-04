package com.coolcollege.intelligent.model.activity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: CommentReplyCountDTO
 * @Description:
 * @date 2023-07-05 13:51
 */
@Data
public class CommentReplyCountDTO {

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("回复数量")
    private Integer replyCount;

}
