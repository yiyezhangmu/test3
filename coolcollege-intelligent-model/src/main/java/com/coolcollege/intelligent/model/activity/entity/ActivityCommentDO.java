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
public class ActivityCommentDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论人")
    private String commentUserId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论附件照片json字段[{},{}]")
    private String contentPics;

    @ApiModelProperty("评论附件视频json字段[{},{}]")
    private String contentVideo;

    @ApiModelProperty("是否包含图片")
    private Boolean isContainsPic;

    @ApiModelProperty("是否包含视频")
    private Boolean isContainsVideo;

    @ApiModelProperty("回复数")
    private Integer replyCount;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("是否置顶")
    private Boolean isTop;

    @ApiModelProperty("评论楼层")
    private Integer floorNum;

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