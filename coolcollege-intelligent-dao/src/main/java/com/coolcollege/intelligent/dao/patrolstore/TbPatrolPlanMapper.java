package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDO;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-04 11:16
 */
public interface TbPatrolPlanMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-04 11:16
     */
    int insertSelective(@Param("record") TbPatrolPlanDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-04 11:16
     */
    int updateByPrimaryKeySelective(@Param("record") TbPatrolPlanDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取计划列表
     * @param enterpriseId
     * @param param
     * @return
     */
    Page<TbPatrolPlanDO> getPatrolPlanList(@Param("enterpriseId") String enterpriseId, @Param("record") PatrolPlanPageRequest param);

    /**
     * 根据id查询计划
     * @param id
     * @param enterpriseId
     * @return
     */
    TbPatrolPlanDO selectById(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取待办事项列表
     * @param enterpriseId
     * @param auditUserId
     * @return
     */
    Page<TbPatrolPlanDO> getPatrolPlanToDo(@Param("enterpriseId") String enterpriseId, @Param("auditUserId") String auditUserId);

    /**
     * 获取行事历待办总数
     * @param enterpriseId
     * @param userId
     * @return
     */
    Long getPatrolPlanCount(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);


    /**
     * 获取巡店计划
     * @param enterpriseId
     * @param supervisorId
     * @param planMonth
     * @return
     */
    TbPatrolPlanDO getMyPatrolPlanMonthDetail(@Param("enterpriseId") String enterpriseId, @Param("supervisorId") String supervisorId, @Param("planMonth") String planMonth);

    /**
     * 根据id集合获取计划列表
     * @param enterpriseId
     * @param planIds
     * @return
     */
    List<TbPatrolPlanDO> selectByIds(@Param("enterpriseId")  String enterpriseId, @Param("planIds")  List<Long> planIds);
}