package com.coolcollege.intelligent.model.activity.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class ActivityInfoDO implements Serializable {

    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("活动主题")
    private String activityTitle;

    @ApiModelProperty("活动导语")
    private String activityInstruction;

    @ApiModelProperty("封面")
    private String coverImage;

    @ApiModelProperty("活动内容")
    private String activityContent;

    @ApiModelProperty("参与率")
    private BigDecimal joinRate;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论人次")
    private Integer commentsCount;

    @ApiModelProperty("评论人数")
    private Integer commentsUserCount;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    @ApiModelProperty("需要参与的用户数")
    private Integer needJoinUserCount;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("可见范围 全部人员:all , 自定义:define")
    private String viewRangeType;

    @ApiModelProperty("活动状态:0未开始; 1进行中; 2已结束; 3停止")
    private Integer status;

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