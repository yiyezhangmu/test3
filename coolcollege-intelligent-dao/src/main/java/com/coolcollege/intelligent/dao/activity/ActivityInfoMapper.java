package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-03 08:23
 */
public interface ActivityInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-03 08:23
     */
    int insertSelective(@Param("record") ActivityInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-03 08:23
     */
    int updateByPrimaryKeySelective(@Param("record") ActivityInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取活动详情
     * @param enterpriseId
     * @param activityId
     * @return
     */
    ActivityInfoDO getActivityDetail(@Param("enterpriseId") String enterpriseId,@Param("activityId")Long activityId);

    /**
     * h5 获取活动分页
     * @param enterpriseId
     * @param activityIds
     * @return
     */
    Page<ActivityInfoDO> getH5ActivityPage(@Param("enterpriseId") String enterpriseId, @Param("activityIds") List<Long> activityIds);

    /**
     * 获取活动分页
     * @param enterpriseId
     * @param activityTitle
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    Page<ActivityInfoDO> getPCActivityPage(String enterpriseId, String activityTitle, Integer status, String startTime, String endTime);

    /**
     * 新增浏览次数
     * @param enterpriseId
     * @param activityId
     * @return
     */
    Integer addViewCount(@Param("enterpriseId") String enterpriseId, @Param("activityId") Long activityId);
}