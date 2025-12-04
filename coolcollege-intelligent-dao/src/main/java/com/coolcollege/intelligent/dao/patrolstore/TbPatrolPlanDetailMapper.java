package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDetailDO;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolRecordTodoRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolPlanDetailExportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-04 02:29
 */
public interface TbPatrolPlanDetailMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-04 02:29
     */
    int insertSelective(@Param("record") TbPatrolPlanDetailDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-04 02:29
     */
    int updateByPrimaryKeySelective(@Param("record") TbPatrolPlanDetailDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     * @param list
     * @param enterpriseId
     * @return
     */
    int insertBatch(@Param("list") List<TbPatrolPlanDetailDO> list, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据计划id查询计划明细
     * @param planId
     * @param enterpriseId
     * @return
     */
    List<TbPatrolPlanDetailDO> getByPlanId(@Param("planId") Long planId, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量删除
     * @param deleteIds
     * @param enterpriseId
     * @return
     */
    int removeDetail(@Param("ids") List<Long> deleteIds, @Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 根据计划id删除
     * @param enterpriseId
     * @param userId
     * @param planId
     * @return
     */
    int removeDetailByPlanId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,@Param("planId") Long planId);

    /**
     * 获取最近巡店时间
     * @param enterpriseId
     * @param storeIds
     * @param metaTableId
     * @return
     */
    List<TbPatrolPlanDetailDO> getLatestPatrolTime(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds, @Param("metaTableId") Long metaTableId);

    /**
     * 批量更新
     * @param enterpriseId
     * @param updateList
     * @return
     */
    Integer batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("updateList") List<TbPatrolPlanDetailDO> updateList);

    /**
     * 更新完成时间及状态
     * @param enterpriseId
     * @param businessId
     * @return
     */
    Integer updateFinishTimeAndStatus(@Param("enterpriseId")String enterpriseId, @Param("businessId")Long businessId);

    /**
     * 根据业务id获取计划明细
     * @param enterpriseId
     * @param businessId
     * @return
     */
    TbPatrolPlanDetailDO getPlanDetailByBusinessId(@Param("enterpriseId")String enterpriseId, @Param("businessId")Long businessId);

    Integer getFinishNumByPlanId(@Param("enterpriseId")String enterpriseId, @Param("planId") Long planId);

    Integer getNumByPlanId(@Param("enterpriseId")String enterpriseId, @Param("planId") Long planId);

    /**
     * 获取当天（本月）的待办巡店任务
     * @param enterpriseId 企业id
     * @param param 查询参数
     * @return 待办巡店
     */
    Page<TbPatrolPlanDetailDO> getPatrolRecordToDo(@Param("enterpriseId") String enterpriseId, @Param("record") PatrolRecordTodoRequest param);

    long getPatrolPlanDetailCount(@Param("enterpriseId") String enterpriseId, @Param("record") PatrolPlanPageRequest record);

    List<PatrolPlanDetailExportVO> getPatrolPlanDetailExportList(@Param("enterpriseId") String enterpriseId, @Param("record") PatrolPlanPageRequest record);
}