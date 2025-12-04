package com.coolcollege.intelligent.dao.safetycheck;

import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2023-08-14 07:53
 */
public interface ScSafetyCheckFlowMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-08-14 07:53
     */
    int insertSelective(@Param("record")ScSafetyCheckFlowDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-08-14 07:53
     */
    ScSafetyCheckFlowDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-08-14 07:53
     */
    int updateByPrimaryKeySelective(@Param("record")ScSafetyCheckFlowDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-08-14 07:53
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    ScSafetyCheckFlowDO getByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId);

    void updateCurrentNodeAndCycleCount(@Param("enterpriseId") String enterpriseId, @Param("currentNodeNo") String currentNodeNo, @Param("cycleCount") Integer cycleCount,
                                        @Param("businessId") Long businessId);
    int updateDelByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("businessIds") List<Long> businessIds);

}