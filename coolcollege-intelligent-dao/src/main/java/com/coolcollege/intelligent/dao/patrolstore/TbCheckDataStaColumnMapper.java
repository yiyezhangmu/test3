package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.metatable.vo.CategoryStatisticsVO;
import com.coolcollege.intelligent.model.patrolstore.CheckDataStaColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDataStaTableCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zhangchenbiao
 * @date 2024-09-03 11:24
 */
public interface TbCheckDataStaColumnMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-03 11:24
     */
    int insertSelective(CheckDataStaColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2024-09-03 11:24
     */
    CheckDataStaColumnDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-03 11:24
     */
    int updateByPrimaryKeySelective(CheckDataStaColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2024-09-03 11:24
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<CheckDataStaColumnDO> getFailReason(@Param("enterpriseId") String enterpriseId,@Param("dataTableId") Long id,@Param("businessId") Long businessId);


    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<CheckDataStaColumnDO> list);

    int batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                    @Param("businessType") String businessType, @Param("checkDataTableId") Long checkDataTableId,
                    @Param("list") List<CheckDataStaColumnDO> dataStaColumnDOList);


    int batchUpdateColumn(@Param("enterpriseId") String enterpriseId, @Param("list") List<CheckDataStaColumnDO> dataStaColumnDOList);

    List<CheckDataStaColumnDO> selectDataColumn(@Param("enterpriseId")String enterpriseId,@Param("dataTableIdList") List<Long> dataTableIdList);

    List<CheckDataStaColumnDO> checkDataStaColumnDOList(@Param("enterpriseId")String enterpriseId, @Param("dataStaColumnIds")List<Long> dataStaColumnIds);

    List<CheckDataStaColumnDO> selectWarDataColumn(@Param("enterpriseId")String enterpriseId,@Param("dataTableIdList") List<Long> dataTableIdList);

    List<CheckDataStaColumnDO> warCheckDataStaColumnDOList(@Param("enterpriseId")String enterpriseId, @Param("dataStaColumnIds")List<Long> dataStaColumnIds);

    List<CheckDataStaColumnDO> selectByBusinessId(@Param("enterpriseId")String enterpriseId,
                                                    @Param("businessIds")Long businessId,
                                                    @Param("checkType")Integer checkType);

    List<CategoryStatisticsVO> selectCategoryStatisticsListByBusinessId(@Param("enterpriseId")String enterpriseId,
                                                                        @Param("businessId")Long businessId,
                                                                        @Param("checkType")Integer checkType);

    List<CheckDataStaColumnDO> selectDataColumnById(@Param("enterpriseId")String enterpriseId,
                                                    @Param("dataTableIds") List<Long> dataTableIdList,
                                                    @Param("checkType") Integer checkType);

    List<PatrolStoreStatisticsDataStaTableCountDTO> statisticsColumnCountByBusinessIdGroupByDataTableId(@Param("enterpriseId")String enterpriseId,
                                                                                                        @Param("list") List<Long> businessIds);
}
