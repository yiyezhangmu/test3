package com.coolcollege.intelligent.dao.metatable;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaDefTableDTO;

/**
 * PatrolMetaDefColumnMapper继承基类
 */
@Mapper
public interface TbMetaDefTableColumnMapper {
    List<TbMetaDefTableColumnDO> selectByTableId(@Param("enterpriseId") String enterpriseId,
        @Param("metaTableId") Long metaTableId);

    List<TbMetaDefTableColumnDO> selectByTableIds(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> metaTableIds);

    int batchInsert(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList);

    /**
     * 根据检查表id硬删除
     */
    int delByTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);

    /**
     * 根据检查表id软删除
     */
    int updateDelByTableIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> metaTableIdList);

    List<TbMetaDefTableColumnDO> selectAll(@Param("enterpriseId") String enterpriseId);

    List<TbMetaDefTableColumnDO> selectByIds(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> ids);

    /**
     * 通过表id查询所有检查项(包括已删除的)
     *
     * @param enterpriseId
     * @param tableIdList
     * @return
     */
    List<TbMetaDefTableColumnDO> getAllColumnByMetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList);

    Integer selectColumnCountByTableId(@Param("enterpriseId") String enterpriseId,
                                       @Param("tableId") Long tableId);

    List<PatrolStoreStatisticsMetaDefTableDTO> statisticsColumnNum(@Param("enterpriseId") String enterpriseId,
        @Param("metaTableIds") List<Long> metaTableIds);

    void batchUpdateStaColumn(@Param("enterpriseId") String enterpriseId, @Param("columnList") List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList);


    int copyDefTableColumn(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList);

    int deleteAllDefTable(@Param("enterpriseId") String enterpriseId);





}