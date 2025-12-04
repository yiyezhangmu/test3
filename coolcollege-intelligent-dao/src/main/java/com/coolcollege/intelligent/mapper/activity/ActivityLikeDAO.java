package com.coolcollege.intelligent.mapper.activity;

import com.coolcollege.intelligent.common.enums.activity.ActivityLikeTypeEnum;
import com.coolcollege.intelligent.dao.activity.ActivityLikeMapper;
import com.coolcollege.intelligent.model.activity.entity.ActivityLikeDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ActivityLikeDAO
 * @Description:
 * @date 2023-07-03 16:20
 */
@Repository
public class ActivityLikeDAO {

    @Resource
    private ActivityLikeMapper activityLikeMapper;

    /**
     * 分页获取点赞列表
     * @param enterpriseId
     * @param activityId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<ActivityLikeDO> getActivityLikePage(String enterpriseId, Long activityId, Integer pageNum, Integer pageSize){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return new Page<>();
        }
        PageHelper.startPage(pageNum, pageSize);
        return activityLikeMapper.getLikePage(enterpriseId, activityId, ActivityLikeTypeEnum.ACTIVITY.getCode());
    }

    /**
     * 点赞或取消点赞
     * @param enterpriseId
     * @param param
     * @return
     */
    public Integer addOrCancelActivityLike(String enterpriseId, ActivityLikeDO param){
        if(StringUtils.isAnyBlank(enterpriseId, param.getLikeUserId()) || Objects.isNull(param.getLikeType()) || Objects.isNull(param.getTargetId())){
            return null;
        }
        return activityLikeMapper.addOrCancelActivityLike(param, enterpriseId);
    }

    public List<Long> getUserLikeCommentIds(String enterpriseId, String userId, List<Long> commentIds){
        if(StringUtils.isAnyBlank(enterpriseId, userId) || CollectionUtils.isEmpty(commentIds)){
            return Lists.newArrayList();
        }
        return activityLikeMapper.getUserLikeTargetIds(enterpriseId, userId, commentIds, ActivityLikeTypeEnum.COMMENT.getCode());
    }

    public Integer getLikeCount(String enterpriseId, Long targetId, ActivityLikeTypeEnum likeType){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(targetId) || Objects.isNull(likeType)){
            return null;
        }
        return activityLikeMapper.getLikeCount(enterpriseId, targetId, likeType.getCode());
    }

    public Boolean isLikeActivity(String enterpriseId, Long activityId, String userId){
        List<Long> userLikeTargetIds = activityLikeMapper.getUserLikeTargetIds(enterpriseId, userId, Arrays.asList(activityId), ActivityLikeTypeEnum.ACTIVITY.getCode());
        return CollectionUtils.isNotEmpty(userLikeTargetIds);
    }

}
