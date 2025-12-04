package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.entity.ActivityViewRangeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-03 08:23
 */
public interface ActivityViewRangeMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-03 08:23
     */
    int batchInsertSelective(@Param("recordList") List<ActivityViewRangeDO> recordList, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-03 08:23
     */
    int updateByPrimaryKeySelective(@Param("record") ActivityViewRangeDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取用户可见的活动
     * @param enterpriseId
     * @param userId
     * @param regionIds
     * @return
     */
    List<Long> getUserViewActivityIds(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("regionIds") List<String> regionIds);

    /**
     * 获取可见范围列表
     * @param enterpriseId
     * @param activityId
     * @return
     */
    List<ActivityViewRangeDO> getViewRangeList(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);

    /**
     * 删除活动可见范围
     * @param enterpriseId
     * @param activityId
     * @return
     */
    Integer deleteViewRangeByActivityId(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);
}