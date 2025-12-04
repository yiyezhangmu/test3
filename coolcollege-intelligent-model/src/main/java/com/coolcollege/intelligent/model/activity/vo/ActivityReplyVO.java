package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.model.activity.entity.ActivityReplyDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ActivityReplyVO
 * @Description:活动回复
 * @date 2023-07-04 10:14
 */
@Data
public class ActivityReplyVO {

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论id")
    private Long commentId;

    @ApiModelProperty("回复id")
    private Long replyId;

    @ApiModelProperty("回复父id")
    private Long parentReplyId;

    @ApiModelProperty("回复人id")
    private String replyUserId;

    @ApiModelProperty("回复人姓名")
    private String replyUsername;

    @ApiModelProperty("被回复人")
    private String repliedUserId;

    @ApiModelProperty("被回复人姓名")
    private String repliedUsername;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;


    public static List<ActivityReplyVO> convertVO(List<ActivityReplyDO> replyList, Map<String, EnterpriseUserDO> userMap, List<ActivityReplyDO> parentReplyList){
        if(CollectionUtils.isEmpty(replyList)){
            return Lists.newArrayList();
        }
        Map<Long, String> parentReplyUserIdMap = parentReplyList.stream().filter(o-> Objects.nonNull(o.getReplyUserId())).collect(Collectors.toMap(k -> k.getId(), v -> v.getReplyUserId()));
        List<ActivityReplyVO> resultList = new ArrayList<>();
        for (ActivityReplyDO activityReply : replyList) {
            ActivityReplyVO result = new ActivityReplyVO();
            result.setActivityId(activityReply.getActivityId());
            result.setCommentId(activityReply.getCommentId());
            result.setReplyId(activityReply.getId());
            result.setParentReplyId(activityReply.getParentReplyId());
            result.setReplyUserId(activityReply.getReplyUserId());
            EnterpriseUserDO enterpriseUser = userMap.get(activityReply.getReplyUserId());
            String username = Optional.ofNullable(enterpriseUser).map(EnterpriseUserDO::getName).orElse("");
            result.setReplyUsername(username);
            String repliedUserId = parentReplyUserIdMap.get(activityReply.getParentReplyId());
            EnterpriseUserDO repliedUser = userMap.get(repliedUserId);
            String repliedUsername = Optional.ofNullable(repliedUser).map(EnterpriseUserDO::getName).orElse("");
            result.setRepliedUserId(repliedUserId);
            result.setRepliedUsername(repliedUsername);
            result.setContent(activityReply.getContent());
            result.setCreateTime(activityReply.getCreateTime());
            resultList.add(result);
        }
        return resultList;
    }
}
