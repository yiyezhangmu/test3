package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.common.enums.activity.ActivityStatusEnum;
import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInfoPCVO implements Serializable {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("活动主题")
    private String activityTitle;

    @ApiModelProperty("活动导语")
    private String activityInstruction;

    @ApiModelProperty("封面")
    private String coverImage;

    @ApiModelProperty("活动内容")
    private String activityContent;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论人次")
    private Integer commentsCount;

    @ApiModelProperty("评论人数")
    private Integer commentsUserCount;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态:0未开始; 1进行中; 2已结束; 3停止")
    private Integer status;

    @ApiModelProperty("可见范围")
    private String viewRangeType;

    @ApiModelProperty("可见范围")
    private List<ViewRangeVO> viewRangeList;

    public static ActivityInfoPCVO convertVO(ActivityInfoDO activityInfo){
        if(Objects.isNull(activityInfo)){
            return null;
        }
        ActivityInfoPCVO result = new ActivityInfoPCVO();
        result.setActivityId(activityInfo.getId());
        result.setActivityTitle(activityInfo.getActivityTitle());
        result.setActivityInstruction(activityInfo.getActivityInstruction());
        result.setCoverImage(activityInfo.getCoverImage());
        result.setActivityContent(activityInfo.getActivityContent());
        result.setStartTime(activityInfo.getStartTime());
        result.setEndTime(activityInfo.getEndTime());
        result.setStatus(activityInfo.getStatus());
        result.setLikeCount(activityInfo.getLikeCount());
        result.setCommentsCount(activityInfo.getCommentsCount());
        result.setCommentsUserCount(activityInfo.getCommentsUserCount());
        result.setViewCount(activityInfo.getViewCount());
        result.setStartTime(activityInfo.getStartTime());
        result.setEndTime(activityInfo.getEndTime());
        result.setViewRangeType(activityInfo.getViewRangeType());
        if(!ActivityStatusEnum.STOP.getCode().equals(activityInfo.getStatus())){
            long currentTime = System.currentTimeMillis();
            if(currentTime >= activityInfo.getStartTime().getTime() && currentTime <= activityInfo.getEndTime().getTime()){
                //进行中
                result.setStatus(ActivityStatusEnum.ONGOING.getCode());
            }
            if(currentTime >= activityInfo.getEndTime().getTime()){
                //已结束
                result.setStatus(ActivityStatusEnum.END.getCode());
            }
        }
        return result;
    }

}