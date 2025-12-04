package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.activity.ActivityStatusEnum;
import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: ActivityH5PageVO
 * @Description:
 * @date 2023-07-04 9:53
 */
@Data
public class ActivityPCPageVO {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("封面图片")
    private String coverImage;

    @ApiModelProperty("参与率")
    private BigDecimal joinRate;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论次数")
    private Integer commentsCount;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动截止时间")
    private Date endTime;

    @ApiModelProperty("活动状态")
    private Integer status;

    @ApiModelProperty("发起人")
    private String createUsername;

    @ApiModelProperty("更新人")
    private String updateUsername;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public static List<ActivityPCPageVO> convertVO(Page<ActivityInfoDO> pageList, Map<String, String> userNameMap){
        if(CollectionUtils.isEmpty(pageList)){
            return Lists.newArrayList();
        }
        List<ActivityPCPageVO> resultList = new ArrayList<>();
        for (ActivityInfoDO activityInfo : pageList) {
            ActivityPCPageVO result = new ActivityPCPageVO();
            result.setActivityId(activityInfo.getId());
            result.setActivityTitle(activityInfo.getActivityTitle());
            result.setCoverImage(activityInfo.getCoverImage());
            BigDecimal joinRate = new BigDecimal("0.00");
            if(activityInfo.getNeedJoinUserCount() >= Constants.ZERO || activityInfo.getCommentsUserCount() >= 0){
                joinRate = new BigDecimal(activityInfo.getCommentsUserCount()).divide(new BigDecimal(activityInfo.getNeedJoinUserCount()), Constants.INDEX_THREE, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Constants.ONE_HUNDRED)).setScale(Constants.INDEX_ONE, BigDecimal.ROUND_HALF_UP);;
            }
            result.setJoinRate(joinRate);
            result.setLikeCount(activityInfo.getLikeCount());
            result.setCommentsCount(activityInfo.getCommentsCount());
            result.setCreateTime(activityInfo.getCreateTime());
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
            result.setCreateUsername(userNameMap.get(activityInfo.getCreateUserId()));
            result.setUpdateUsername(userNameMap.get(activityInfo.getUpdateUserId()));
            result.setUpdateTime(activityInfo.getUpdateTime());
            resultList.add(result);
        }
        return resultList;
    }
}
