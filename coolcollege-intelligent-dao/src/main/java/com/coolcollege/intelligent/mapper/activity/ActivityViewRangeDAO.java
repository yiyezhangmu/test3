package com.coolcollege.intelligent.mapper.activity;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.activity.ActivityViewRangeMapper;
import com.coolcollege.intelligent.model.activity.entity.ActivityViewRangeDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ActivityViewRangeDAO
 * @Description:
 * @date 2023-07-04 11:09
 */
@Repository
public class ActivityViewRangeDAO {

    @Resource
    private ActivityViewRangeMapper activityViewRangeMapper;

    public List<Long> getUserViewActivityIds(String enterpriseId, String userId, List<String> regionIds){
        if(StringUtils.isAnyBlank(enterpriseId, userId)){
            return Lists.newArrayList();
        }
        return activityViewRangeMapper.getUserViewActivityIds(enterpriseId, userId, regionIds);
    }

    /**
     * 获取活动的可见范围
     * @param enterpriseId
     * @param activityId
     * @return
     */
    public List<ActivityViewRangeDO> getViewRangeList(String enterpriseId, Long activityId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(activityId)){
            return Lists.newArrayList();
        }
        return activityViewRangeMapper.getViewRangeList(enterpriseId, activityId);
    }

    /**
     * 更新可见范围
     * @param enterpriseId
     * @param activityId
     * @param operateType
     * @param updateList
     * @return
     */
    public Integer updateViewRange(String enterpriseId, Long activityId, String operateType, List<ActivityViewRangeDO> updateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(updateList)){
            return Constants.ZERO;
        }
        if("update".equals(operateType)){
            //先删除
            activityViewRangeMapper.deleteViewRangeByActivityId(enterpriseId, activityId);
        }
        return activityViewRangeMapper.batchInsertSelective(updateList, enterpriseId);
    }

}
