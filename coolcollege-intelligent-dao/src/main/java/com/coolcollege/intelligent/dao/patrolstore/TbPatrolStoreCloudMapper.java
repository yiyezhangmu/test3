package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2024-11-27 01:47
 */
public interface TbPatrolStoreCloudMapper {
    /**
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-11-27 01:47
     */
    int insertSelective(@Param("record") TbPatrolStoreCloudDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-11-27 01:47
     */
    int updateByPrimaryKeySelective(@Param("record") TbPatrolStoreCloudDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取云图库
     *
     * @param businessId   巡店id
     * @param userId       用户id
     * @param enterpriseId 企业id
     * @return 云图库信息
     */
    TbPatrolStoreCloudDO getByBusinessId(@Param("businessId") Long businessId, @Param("userId") String userId, @Param("enterpriseId") String enterpriseId);

    /**
     * 删除云图库
     *
     * @param id           主键
     * @param enterpriseId 企业id
     * @return 删除结果
     */
    Integer deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据主键查询云图库
     *
     * @param id           主键
     * @param enterpriseId 企业id
     * @return 云图库信息
     */
    TbPatrolStoreCloudDO selectById(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);
}