package com.coolcollege.intelligent.dao.inspection;

import com.coolcollege.intelligent.model.inspection.AiInspectionStrategiesDTO;
import com.coolcollege.intelligent.model.inspection.entity.AiInspectionStrategiesDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-09-25 04:29
 */
public interface AiInspectionStrategiesMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-09-25 04:29
     */
    int insertSelective(@Param("record") AiInspectionStrategiesDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-09-25 04:29
     */
    int updateByPrimaryKeySelective(@Param("record") AiInspectionStrategiesDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 根据主键查询
     * dateTime:2025-09-25 04:29
     */
    AiInspectionStrategiesDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 根据主键删除
     * dateTime:2025-09-25 04:29
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 根据条件查询
     * dateTime:2025-09-25 04:29
     */
    List<AiInspectionStrategiesDO> selectByQuery(@Param("query") AiInspectionStrategiesDTO query, @Param("enterpriseId") String enterpriseId);
}