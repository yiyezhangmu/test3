package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.model.activity.entity.ActivityCommentDO;
import com.coolcollege.intelligent.model.activity.entity.ActivityReplyDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: ActivityCommentPageVO
 * @Description: 活动评论列表
 * @date 2023-07-04 10:10
 */
@Data
public class ActivityCommentPageVO {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("评论人")
    private String commentUserId;

    @ApiModelProperty("评论人姓名")
    private String commentUsername;

    @ApiModelProperty("评论人头像")
    private String commentUserAvatar;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论附件照片json字段[{},{}]")
    private String contentPics;

    @ApiModelProperty("评论附件视频json字段[{},{}]")
    private String contentVideo;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("回复数")
    private Integer replyCount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("当前用户是否点赞过")
    private Boolean isLike;

    @ApiModelProperty("是否置顶")
    private Boolean isTop;

    @ApiModelProperty("评论楼层")
    private Integer floorNum;

    @ApiModelProperty("评论回复")
    private List<ActivityReplyVO> replyList;

    /**
     * 转vo
     * @param commentList
     * @param userMap
     * @param replyMap
     * @param parentReplyList
     * @param replyCountMap
     * @return
     */
    public static List<ActivityCommentPageVO> convertVO(List<ActivityCommentDO> commentList, Map<String, EnterpriseUserDO> userMap, Map<Long, List<ActivityReplyDO>> replyMap, List<ActivityReplyDO> parentReplyList, List<Long> userLikeCommentIds){
        if(CollectionUtils.isEmpty(commentList)){
            return Lists.newArrayList();
        }
        List<ActivityCommentPageVO> resultList = new ArrayList<>();
        for (ActivityCommentDO activityComment : commentList) {
            ActivityCommentPageVO result = new ActivityCommentPageVO();
            result.setActivityId(activityComment.getActivityId());
            result.setCommentId(activityComment.getId());
            result.setCommentUserId(activityComment.getCommentUserId());
            EnterpriseUserDO enterpriseUser = userMap.get(activityComment.getCommentUserId());
            String username = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getName).orElse("");
            String userAvatar = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getAvatar).orElse("");
            result.setCommentUsername(username);
            result.setCommentUserAvatar(userAvatar);
            result.setContent(activityComment.getContent());
            result.setContentPics(activityComment.getContentPics());
            result.setContentVideo(activityComment.getContentVideo());
            result.setLikeCount(activityComment.getLikeCount());
            result.setReplyCount(activityComment.getReplyCount());
            result.setCreateTime(activityComment.getCreateTime());
            result.setIsTop(activityComment.getIsTop());
            result.setFloorNum(activityComment.getFloorNum());
            result.setIsLike(userLikeCommentIds.contains(activityComment.getId()));
            List<ActivityReplyDO> activityReply = replyMap.get(activityComment.getId());
            result.setReplyList(ActivityReplyVO.convertVO(activityReply, userMap, parentReplyList));
            resultList.add(result);
        }
        return resultList;
    }



}
