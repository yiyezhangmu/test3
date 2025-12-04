package com.coolcollege.intelligent.mapper.activity;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServerException;
import com.coolcollege.intelligent.dao.activity.ActivityCommentMapper;
import com.coolcollege.intelligent.model.activity.dto.CommentCountDTO;
import com.coolcollege.intelligent.model.activity.dto.ActivityCommentCountDTO;
import com.coolcollege.intelligent.model.activity.entity.ActivityCommentDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ActivityCommentDAO
 * @Description:
 * @date 2023-07-03 16:19
 */
@Repository
public class ActivityCommentDAO {

    @Resource
    private ActivityCommentMapper activityCommentMapper;


    public Page<ActivityCommentDO> getActivityCommentPage(String enterpriseId, Long activityId, List<Long> commentIds, Boolean isContainsPic, String orderField, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Page<ActivityCommentDO> activityCommentPage = activityCommentMapper.getActivityCommentPage(enterpriseId, activityId, commentIds, isContainsPic, orderField);
        return activityCommentPage;
    }

    public Page<ActivityCommentDO> getActivityCommentList(String enterpriseId,Long activityId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Page<ActivityCommentDO> activityCommentPage = activityCommentMapper.getActivityCommentList(enterpriseId, activityId);
        return activityCommentPage;
    }

    public Long addActivityComment(String enterpriseId, ActivityCommentDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param) || Objects.isNull(param.getActivityId())){
           return null;
        }
        //获取当前活动评论的数量
        Integer floorNum = activityCommentMapper.getNextFloorNum(enterpriseId, param.getActivityId());
        param.setFloorNum(floorNum);
        activityCommentMapper.insertSelective(param, enterpriseId);
        return param.getId();
    }


    public Integer updateActivityComment(String enterpriseId, ActivityCommentDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param) || Objects.isNull(param.getId())){
            return null;
        }
        return activityCommentMapper.updateByPrimaryKeySelective(param, enterpriseId);
    }

    public ActivityCommentDO selectByPrimaryKeySelective(String enterpriseId,Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        return activityCommentMapper.selectByPrimaryKeySelective(id, enterpriseId);
    }

    public ActivityCommentCountDTO getActivityCommentCount(String enterpriseId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return null;
        }
        return activityCommentMapper.getActivityCommentCount(enterpriseId, activityId);
    }

    public List<CommentCountDTO> queryCommentUserCommentCount(String enterpriseId, Long activityId){
        return activityCommentMapper.queryCommentUserCommentCount(enterpriseId, activityId);
    }


    public Long queryActivityCommentCount(String enterpriseId, Long activityId){
        if (activityId==null){
            return 0L;
        }
        return activityCommentMapper.queryActivityCommentCount(enterpriseId, activityId);
    }

    public List<Long> getActivityCommentIdsByUserId(String enterpriseId, Long activityId, String userId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return null;
        }
        return activityCommentMapper.getActivityCommentIdsByUserId(enterpriseId, activityId, userId);
    }

    public Integer topAndUnTop(String enterpriseId, String userId, Long activityId, Long commentId){
        if(StringUtils.isAnyBlank(enterpriseId, userId) || Objects.isNull(activityId) || Objects.isNull(commentId)){
            return null;
        }
        return activityCommentMapper.topAndUnTop(enterpriseId, activityId, commentId);
    }


}
