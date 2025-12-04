package com.coolcollege.intelligent.dao.metatable;

import java.util.List;

import com.coolcollege.intelligent.model.metatable.dto.TableColumnCountDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDataStaColumnDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.dto.ColumnCategoryDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsMetaStaTableDTO;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Mapper
public interface TbMetaStaTableColumnMapper {

    /**
     *
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-14 08:43
     */
    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaStaTableColumnDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-14 08:43
     */
    int updateByPrimaryKeySelective(@Param("enterpriseId") String enterpriseId, @Param("record") TbMetaStaTableColumnDO record);

    int insert(@Param("enterpriseId") String enterpriseId, @Param("entity") TbMetaStaTableColumnDO entity);

    TbMetaStaTableColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 批量添加列
     *
     * @param enterpriseId
     * @param entityList
     * @return
     */
    int insertColumnList(@Param("enterpriseId") String enterpriseId,
        @Param("entityList") List<TbMetaStaTableColumnDO> entityList);

    /**
     * 根据表id删除对应的列(逻辑删除)
     *
     * @return
     */
    int deleteColumnByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList);

    /**
     * 批量获取检查表的检查项，tableIdList为空查询所有表的列
     * @param enterpriseId
     * @param tableIdList
     * @param isFilterFreezeColumn 是否查询冻结的项
     * @return
     */
    List<TbMetaStaTableColumnDO> selectColumnListByTableIdList(@Param("enterpriseId") String enterpriseId,
                                                               @Param("tableIdList") List<Long> tableIdList,
                                                               @Param("isFilterFreezeColumn") Boolean isFilterFreezeColumn);


    List<TbMetaStaTableColumnDO> selectAll(@Param("enterpriseId") String enterpriseId);

    List<TbMetaStaTableColumnDO> selectByIds(@Param("enterpriseId") String enterpriseId,
                                             @Param("list") List<Long> ids);

    List<ColumnCategoryDTO> getCategoryByTableId(@Param("enterpriseId") String enterpriseId, @Param("tableId") Long tableId);

    List<TbMetaStaTableColumnDO> getTableColumnByIdAndCategory(@Param("enterpriseId") String enterpriseId, @Param("tableId") Long tableId, @Param("category") String category);

    /**
     * 通过metaTableId获取所有检查项(包括已删除的)
     *
     * @param enterpriseId
     * @param tableIdList
     * @return
     */
    List<TbMetaStaTableColumnDO> getAllColumnBymetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> tableIdList);

    /**
     * 物理删除
     *
     * @param enterpriseId
     * @param deleteIdList
     */
    void deleteAbsoluteByTableIdList(@Param("enterpriseId") String enterpriseId, @Param("tableIdList") List<Long> deleteIdList);

    List<TbMetaStaTableColumnDO> getDetailByIdList(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);
    List<TbMetaStaTableColumnDO> getDetailByMetaTableIdList(@Param("enterpriseId") String enterpriseId,
                                                            @Param("idList") List<Long> idList);

    List<PatrolStoreStatisticsMetaStaTableDTO> statisticsColumnNum(@Param("enterpriseId") String enterpriseId,
        @Param("metaTableIds") List<Long> metaTableIds);

    Integer countByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableIds") List<Long> metaTableIds);


    Integer statisticsColumnMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);

    List<PatrolStoreStatisticsDataStaColumnDTO> statisticsColumnByMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("metaTableIds") List<Long> metaTableIds);

    void updateLevelByTableId(@Param("enterpriseId") String enterpriseId, @Param("tableId") Long tableId, @Param("level") String level);

    /**
     * 更新检查表中项的冻结状态
     * @param enterpriseId
     * @param tableId
     * @param columnIds
     * @param freezeStatus
     */
    void updateColumnFreeze(@Param("enterpriseId") String enterpriseId,
                            @Param("tableId") Long tableId,
                            @Param("columnIds") List<Long> columnIds,
                            @Param("freezeStatus") Integer freezeStatus);

    /**
     * 批量清空表中项的 处理人 审批人 抄送人
     * @param enterpriseId
     * @param tableId
     * @param columnIds
     * @param clearHandler
     * @param clearRechecker
     * @param clearCcPerson
     */
    void updateColumnInChenkTable(@Param("enterpriseId") String enterpriseId,
                                  @Param("tableId") Long tableId,
                                  @Param("columnIds") List<Long> columnIds,
                                  @Param("clearHandler") Boolean clearHandler,
                                  @Param("clearRechecker") Boolean clearRechecker,
                                  @Param("clearCcPerson") Boolean clearCcPerson);

    List<TbMetaStaTableColumnDO> selectByColumnType(@Param("enterpriseId") String enterpriseId, @Param("columnType")Integer columnType, @Param("beginTime") String beginTime);

    /**
     * 批量更新
     * @param enterpriseId
     * @param updateStaColumnList
     * @return
     */
    Integer batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("updateStaColumnList") List<TbMetaStaTableColumnDO> updateStaColumnList);


    int copyColumnList(@Param("enterpriseId") String enterpriseId, @Param("entityList") List<TbMetaStaTableColumnDO> entityList);

    int deleteAllColumn(@Param("enterpriseId") String enterpriseId);

    Integer batchUpdateExecuteDemand(@Param("enterpriseId") String enterpriseId, @Param("list")List<TbMetaStaTableColumnDO> list);

    List<Long> getMetaTableIdByQuickColumnId(@Param("enterpriseId") String enterpriseId,
                                             @Param("quickColumnIdList")List<Long> quickColumnIdList, @Param("metaColumnId") Long metaColumnId);

    Integer getMetaTableColumnCount(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") String metaTableId);

    List<TbMetaStaTableColumnDO> getMetaStaTableColumnList(@Param("enterpriseId")String enterpriseId,@Param("columnIds") List<Long> columnIds);
    List<TbMetaStaTableColumnDO> selectListExtend(@Param("enterpriseId")String enterpriseId);

    List<TableColumnCountDTO> getColumnCount(@Param("enterpriseId") String enterpriseId, @Param("metaTableIds") List<Long> metaTableIds);

    /**
     * 根据metaTableId判断是否全部为AI检查项
     * @param enterpriseId 企业id
     * @param metaTableId 检查表id
     * @return 是否全为AI检查项
     */
    boolean isAllAiCheckColumnByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId") Long metaTableId);


    Integer aiCheckColumnCountByMetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("metaTableIds") List<Long> metaTableIds);

    Integer unAiCheckColumnCountByMetaTableIdList(@Param("enterpriseId") String enterpriseId, @Param("metaTableIds") List<Long> metaTableIds);
}
