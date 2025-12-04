package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.common.enums.activity.ActivityStatusEnum;
import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ActivityH5PageVO
 * @Description:
 * @date 2023-07-04 9:53
 */
@Data
public class ActivityH5PageVO {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("活动导语")
    private String activityInstruction;

    @ApiModelProperty("封面图片")
    private String coverImage;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动截止时间")
    private Date endTime;

    @ApiModelProperty("活动状态")
    private Integer status;

    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    public static List<ActivityH5PageVO> convertVO(Page<ActivityInfoDO> pageList){
        if(CollectionUtils.isEmpty(pageList)){
            return Lists.newArrayList();
        }
        List<ActivityH5PageVO> resultList = new ArrayList<>();
        for (ActivityInfoDO activityInfo : pageList) {
            ActivityH5PageVO result = new ActivityH5PageVO();
            result.setActivityId(activityInfo.getId());
            result.setActivityTitle(activityInfo.getActivityTitle());
            result.setActivityInstruction(activityInfo.getActivityInstruction());
            result.setCoverImage(activityInfo.getCoverImage());
            result.setStartTime(activityInfo.getStartTime());
            result.setEndTime(activityInfo.getEndTime());
            result.setStatus(activityInfo.getStatus());
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
            result.setViewCount(activityInfo.getViewCount());
            resultList.add(result);
        }
        return resultList;
    }
}
