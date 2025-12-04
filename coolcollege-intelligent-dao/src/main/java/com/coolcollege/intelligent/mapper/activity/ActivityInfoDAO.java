package com.coolcollege.intelligent.mapper.activity;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.activity.ActivityInfoMapper;
import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ActivityInfoDAO
 * @Description:
 * @date 2023-07-03 16:19
 */
@Repository
public class ActivityInfoDAO {

    @Resource
    private ActivityInfoMapper activityInfoMapper;

    public Page<ActivityInfoDO> getPCActivityPage(String enterpriseId, String activityTitle, Integer status, String startTime, String endTime, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        return activityInfoMapper.getPCActivityPage(enterpriseId, activityTitle, status, startTime, endTime);
    }

    /**
     * 新增活动
     * @param enterpriseId
     * @param param
     * @return
     */
    public Long addActivity(String enterpriseId, ActivityInfoDO param){
        if(StringUtils.isBlank(enterpriseId)){
            return null;
        }
        activityInfoMapper.insertSelective(param, enterpriseId);
        return param.getId();
    }


    public Integer updateActivity(String enterpriseId, ActivityInfoDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param.getId())){
            return null;
        }
        return activityInfoMapper.updateByPrimaryKeySelective(param, enterpriseId);
    }

    /**
     * 获取活动详情
     * @param enterpriseId
     * @param activityId
     * @return
     */
    public ActivityInfoDO getActivityDetail(String enterpriseId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return null;
        }
        return activityInfoMapper.getActivityDetail(enterpriseId, activityId);
    }

    /**
     * 删除活动
     * @param enterpriseId
     * @param userId
     * @param activityId
     * @return
     */
    public Integer deleteActivity(String enterpriseId, String userId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return null;
        }
        ActivityInfoDO delete = new ActivityInfoDO();
        delete.setId(activityId);
        delete.setDeleted(Boolean.TRUE);
        delete.setUpdateUserId(userId);
        delete.setUpdateTime(new Date());
        return activityInfoMapper.updateByPrimaryKeySelective(delete, enterpriseId);
    }

    /**
     * 分页获取活动
     * @param enterpriseId
     * @param activityIds
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<ActivityInfoDO> getH5ActivityPage(String enterpriseId, List<Long> activityIds, Integer pageNum, Integer pageSize){
        if(StringUtils.isBlank(enterpriseId)){
            return new Page<>();
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ActivityInfoDO> activityPage = activityInfoMapper.getH5ActivityPage(enterpriseId, activityIds);
        return activityPage;
    }

    public Integer addViewCount(String enterpriseId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return Constants.ZERO;
        }
        return activityInfoMapper.addViewCount(enterpriseId, activityId);
    }

}
