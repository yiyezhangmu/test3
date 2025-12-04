package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePlanDO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-07-11 01:57
 */
public interface TbPatrolStorePlanMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-07-11 01:57
     */
    int insertSelective(@Param("record") TbPatrolStorePlanDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-07-11 01:57
     */
    TbPatrolStorePlanDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-07-11 01:57
     */
    int updateByPrimaryKeySelective(@Param("record")TbPatrolStorePlanDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-07-11 01:57
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    List<TbPatrolStorePlanDO> getPlanList(@Param("enterpriseId") String enterpriseId,
                                          @Param("userId") String userId, @Param("planDate") String planDate);

    TbPatrolStoreCountDTO getPlanCount(@Param("enterpriseId") String enterpriseId,
                                       @Param("userId") String userId, @Param("planDateBegin") String planDateBegin,
                                       @Param("planDateEnd") String planDateEnd);

    List<TbPatrolStoreCountDTO> getPlanTimesCount(@Param("enterpriseId") String enterpriseId,
                                                  @Param("userId") String userId, @Param("planDateBegin") String planDateBegin,
                                                  @Param("planDateEnd") String planDateEnd,
                                                  @Param("storeIdList") List<String> storeIdList);

    List<TbPatrolStoreCountDTO> getPlanPeopleTimesCount(@Param("enterpriseId") String enterpriseId, @Param("planDateBegin") String planDateBegin,
                                                  @Param("planDateEnd") String planDateEnd,
                                                  @Param("supervisorIdList") List<String> supervisorIdList);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-07-11 01:57
     */
    TbPatrolStorePlanDO getPlanByUserId(@Param("enterpriseId") String enterpriseId,
                                           @Param("userId") String userId, @Param("planDate") String planDate,
                                           @Param("storeId") String storeId);
}