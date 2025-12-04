package com.coolcollege.intelligent.dao.achievement;

import com.coolcollege.intelligent.model.achievement.entity.AchievementTaskRecordDO;
import com.coolcollege.intelligent.model.unifytask.query.AchievementTaskStoreQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-03-16 01:48
 */
public interface AchievementTaskRecordMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-03-16 01:48
     */
    int insertSelective(@Param("record") AchievementTaskRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-03-16 01:48
     */
    AchievementTaskRecordDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-03-16 01:48
     */
    int updateByPrimaryKeySelective(AchievementTaskRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-03-16 01:48
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据taskId删除
     * dateTime:2024-03-16 01:48
     */
    int deleteByTaskId(@Param("taskId") Long taskId, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-03-16 01:48
     */
    List<AchievementTaskRecordDO> selectList(@Param("enterpriseId") String enterpriseId, @Param("query") AchievementTaskStoreQuery query);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-03-16 01:48
     */
    AchievementTaskRecordDO selectDetail(@Param("enterpriseId") String enterpriseId,@Param("taskId") Long taskId, @Param("storeId") String storeId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-03-16 01:48
     */
    List<AchievementTaskRecordDO> selectTaskRecordList(@Param("enterpriseId") String enterpriseId,@Param("storeId") String storeId, @Param("productType") String productType);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-03-16 01:48
     */
    List<AchievementTaskRecordDO> selectRemindList(@Param("enterpriseId") String enterpriseId, @Param("now")  String now, @Param("beginTime")  String beginTime);

    AchievementTaskRecordDO getIdByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId, @Param("storeId")String storeId, @Param("loopCount")Long loopCount);
}