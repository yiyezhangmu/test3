package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.PatrolCheckDataTableDO;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-03 11:24
 */
public interface TbPatrolCheckDataTableMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-03 11:24
     */
    int insertSelective(@Param("record") PatrolCheckDataTableDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-09-03 11:24
     */
    PatrolCheckDataTableDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-03 11:24
     */
    int updateByPrimaryKeySelective(@Param("record") PatrolCheckDataTableDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-09-03 11:24
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<PatrolCheckDataTableDO> getTableInfo(@Param("enterpriseId") String enterpriseId,
                                        @Param("businessId") Long businessId,
                                        @Param("checkType") Integer checkType);

    List<PatrolCheckDataTableDO> getTableListByBusinessId(@Param("enterpriseId") String enterpriseId,@Param("businessIds") List<Long> businessIds);

    List<PatrolCheckDataTableDO> getWarTableListByBusinessId(@Param("enterpriseId")String enterpriseId, @Param("businessIds")List<Long> businessIdList);

    List<PatrolCheckDataTableDO> selectByBusinessId(@Param("enterpriseId")String enterpriseId,
                                                    @Param("businessId")Long businessId,
                                                    @Param("checkType") Integer checkType);
}
