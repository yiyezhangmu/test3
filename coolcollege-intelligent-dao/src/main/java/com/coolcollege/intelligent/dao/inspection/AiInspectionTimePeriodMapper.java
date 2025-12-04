package com.coolcollege.intelligent.dao.inspection;

import com.coolcollege.intelligent.model.inspection.entity.AiInspectionTimePeriodDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiInspectionTimePeriodMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-09-25 04:44
     */
    int insertSelective(@Param("record") AiInspectionTimePeriodDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-09-25 04:44
     */
    int updateByPrimaryKeySelective(@Param("record") AiInspectionTimePeriodDO record, @Param("enterpriseId") String enterpriseId);


    /**
     *
     * 根据条件查询
     * dateTime:2025-09-25 04:44
     */
    List<AiInspectionTimePeriodDO> selectByInspectionIdList(@Param("inspectionIdList") List<Long> inspectionIdList, @Param("enterpriseId")String enterpriseId);
    
    /**
     *
     * 根据巡检策略ID删除
     * dateTime:2025-10-09
     */
    int deleteByInspectionId(@Param("inspectionId") Long inspectionId, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 根据巡检策略ID删除
     * dateTime:2025-10-09
     */
    int removeByInspectionId(@Param("inspectionId") Long inspectionId, @Param("enterpriseId") String enterpriseId);

    List<AiInspectionTimePeriodDO> findMatchingStrategiesWithPeriods(@Param("enterpriseId") String enterpriseId,
                                                                     @Param("currentTimeHourMin") String currentTimeHourMin,
                                                                     @Param("dayOfWeek") Integer dayOfWeek);

    AiInspectionTimePeriodDO getMatchingStrategyWithPeriod(@Param("enterpriseId") String enterpriseId,
                                                                    @Param("currentTimeHourMin") String currentTimeHourMin,
                                                                    @Param("inspectionId") Long inspectionId);
}