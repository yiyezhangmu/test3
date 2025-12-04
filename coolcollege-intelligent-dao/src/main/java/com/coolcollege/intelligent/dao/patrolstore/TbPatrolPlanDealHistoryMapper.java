package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDealHistoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-04 11:15
 */
public interface TbPatrolPlanDealHistoryMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-04 11:15
     */
    int insertSelective(@Param("record") TbPatrolPlanDealHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-04 11:15
     */
    int updateByPrimaryKeySelective(@Param("record") TbPatrolPlanDealHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取处理记录列表
     * @param enterpriseId
     * @param planId
     * @return
     */
    List<TbPatrolPlanDealHistoryDO> getProcessHistoryList(@Param("enterpriseId") String enterpriseId, @Param("planId")Long planId);
}