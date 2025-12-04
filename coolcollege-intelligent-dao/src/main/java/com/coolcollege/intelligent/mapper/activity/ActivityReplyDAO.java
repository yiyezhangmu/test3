package com.coolcollege.intelligent.mapper.activity;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.activity.ActivityReplyMapper;
import com.coolcollege.intelligent.model.activity.dto.CommentReplyCountDTO;
import com.coolcollege.intelligent.model.activity.entity.ActivityReplyDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ActivityReplyDAO
 * @Description:
 * @date 2023-07-04 11:06
 */
@Repository
public class ActivityReplyDAO {

    @Resource
    private ActivityReplyMapper activityReplyMapper;

    /**
     * 获取跟我相关的评论id
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @return
     */
    public List<Long> getMyReplyCommentIds(String enterpriseId, String userId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return Lists.newArrayList();
        }
        return activityReplyMapper.getMyReplyCommentIds(enterpriseId, userId, activityId);
    }

    /**
     * 获取评论下面的回复
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @param userId
     * @return
     */
    public List<ActivityReplyDO> getActivityReplyList(String enterpriseId, Long activityId, List<Long> commentIds, String userId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return Lists.newArrayList();
        }
        return activityReplyMapper.getActivityReplyList(enterpriseId, activityId, commentIds, userId);
    }

    /**
     * 分页获取评论回复
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<ActivityReplyDO> getActivityReplyPage(String enterpriseId, String userId, Long activityId, Long commentId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return activityReplyMapper.getActivityReplyPage(enterpriseId, userId, activityId, commentId);
    }

    /**
     * 获取最新的3条评论
     * @param enterpriseId
     * @param activityId
     * @param commentIds
     * @param userId
     * @return
     */
    public List<ActivityReplyDO> getLastThreeReplyGroupComment(String enterpriseId, Long activityId, List<Long> commentIds, String userId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId) || CollectionUtils.isEmpty(commentIds)){
            return Lists.newArrayList();
        }
        return activityReplyMapper.getLastThreeReplyGroupComment(enterpriseId, activityId, commentIds, userId);
    }

    /**
     * 获取回复
     * @param enterpriseId
     * @param parentReplyIds
     * @return
     */
    public List<ActivityReplyDO> getReplyListByIds(String enterpriseId, List<Long> parentReplyIds){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(parentReplyIds)){
            return Lists.newArrayList();
        }
        return activityReplyMapper.getReplyListByIds(enterpriseId, parentReplyIds);
    }

    /**
     *
     * @param enterpriseId
     * @param commentIds
     * @return
     */
    public Map<Long, Integer> getCommentReplyCount(String enterpriseId, Long activityId, List<Long> commentIds){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId) || CollectionUtils.isEmpty(commentIds)){
            return Maps.newHashMap();
        }
        List<CommentReplyCountDTO> replyCountList = activityReplyMapper.getCommentReplyCount(enterpriseId, activityId, commentIds);
        return replyCountList.stream().collect(Collectors.toMap(k->k.getCommentId(), v->v.getReplyCount(), (k1, k2)->k1));
    }

    public List<ActivityReplyDO> getFistReply(String enterpriseId, Long activityId, List<Long> commentIds){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId) || CollectionUtils.isEmpty(commentIds)){
            return Lists.newArrayList();
        }
        List<ActivityReplyDO> activityReplyDOS = activityReplyMapper.getFistReply(enterpriseId, activityId, commentIds);
        return activityReplyDOS;
    }

    public Long addActivityReply(String enterpriseId, ActivityReplyDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param.getActivityId()) || Objects.isNull(param.getCommentId())){
            return null;
        }
        activityReplyMapper.insertSelective(param, enterpriseId);
        return param.getId();
    }

    public Integer updateActivityReply(String enterpriseId, ActivityReplyDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param.getId())){
            return null;
        }
        return activityReplyMapper.updateByPrimaryKeySelective(param, enterpriseId);
    }

}
