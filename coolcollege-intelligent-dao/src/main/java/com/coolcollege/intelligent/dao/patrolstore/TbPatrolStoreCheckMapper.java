package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.PatrolStoreCheckDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.CheckAnalyzeVO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreCheckVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zhangchenbiao
 * @date 2024-09-03 11:23
 */
public interface TbPatrolStoreCheckMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-03 11:23
     */
    int insertSelective(@Param("record")   PatrolStoreCheckDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-09-03 11:23
     */
    PatrolStoreCheckDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-09-03 11:23
     */
    PatrolStoreCheckDO selectByBusinessId(@Param("businessId") Long businessId, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-03 11:23
     */
    int updateByPrimaryKeySelective(@Param("record") PatrolStoreCheckDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-09-03 11:23
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<PatrolStoreCheckVO> getPatrolStoreCheckList(@Param("enterpriseId") String enterpriseId,@Param("query") PatrolStoreCheckQuery query);

    List<CheckAnalyzeVO> getCheckAnalyzeList(@Param("enterpriseId")String enterpriseId,@Param("query") PatrolStoreCheckQuery query);

    List<PatrolStoreCheckDO> selectListBySupervisorId(@Param("enterpriseId")String enterpriseId,@Param("supervisorIds") List<String> supervisorIdSet);

    List<PatrolStoreCheckVO> getPatrolStoreCheckListById(@Param("enterpriseId")String enterpriseId,@Param("query") PatrolStoreCheckQuery query);

    List<PatrolStoreCheckDO> getPatrolStoreCheckListByBusinessId(@Param("enterpriseId")String enterpriseId,
                                                                 @Param("businessIds") List<Long> businessIds);
}
