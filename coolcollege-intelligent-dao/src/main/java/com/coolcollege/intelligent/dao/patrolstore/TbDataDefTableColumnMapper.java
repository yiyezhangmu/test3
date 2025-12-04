package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsDataColumnCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbDataDefTableColumnMapper {

    /**
     * 批量插入
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList);

    /**
     * 批量更新
     */
    int batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
        @Param("businessType") String businessType, @Param("dataTableId") Long dataTableId,
        @Param("list") List<TbDataDefTableColumnDO> tbDataDefTableColumnDOList,
        @Param("submit") boolean submit);

    /**
     * 通过父任务id和子任务id获取
     */
    List<TbDataDefTableColumnDO> getListBySubTaskIdAndTaskId(String enterpriseId, Long taskId,
        List<Long> subTaskIdList);

    /**
     * 根据businessIds删除
     */
    int updateDelByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> businessIds,
        @Param("businessType") String businessType);

    /**
     * 根据记录id和meta检查表ids硬删除
     */
    int delByBusinessIdAndMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
        @Param("businessType") String businessType, @Param("list") List<Long> metaStaTableIds);


    /**
     * 根据业务id获取自定义检查项数据
     */
    List<TbDataDefTableColumnDO> selectByBusinessIdAndMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                                                   @Param("list") List<Long> metaStaTableIds);

    /**
     * 更新业务状态
     */
    int updateBusinessStatus(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,  @Param("subTaskId") Long subTaskId,
        @Param("businessType") String businessType, @Param("supervisorId") String supervisorId);

    /**
     * 根据业务id获取自定义检查项数据
     */
    List<TbDataDefTableColumnDO> selectByBusinessId(@Param("enterpriseId") String enterpriseId,
        @Param("businessId") Long businessId, @Param("businessType") String businessType);

    /**
     * 根据业务id获取自定义检查项数据
     */
    TbDataDefTableColumnDO selectById(@Param("enterpriseId") String enterpriseId,
                                                    @Param("id") Long id);


    /**
     * 通过巡店记录id获取提交记录
     *
     * @param enterpriseId
     * @param recordIdList
     * @return
     */
    List<TbDataDefTableColumnDO> getListByRecordIdList(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                                       @Param("query") PatrolStoreStatisticsDataTableQuery query);

    List<TbDataDefTableColumnDO> getListByRecordIdListAndPatrolStoreTime(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                                       @Param("query") PatrolStoreStatisticsDataTableQuery query);

    Long getListByRecordIdListCount(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                    @Param("query") PatrolStoreStatisticsDataTableQuery query);

    List<TbDataDefTableColumnDO> getListByRecordIdListForMap(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList);

    /**
     * 自定义检查项数量统计
     *
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<PatrolStoreStatisticsDataColumnCountDTO> statisticsColumnCount(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> ids);

    /**
     * 自定义检查项数量统计
     *
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<PatrolStoreStatisticsDataColumnCountDTO> statisticsColumnCountByBusinessIds(@Param("enterpriseId") String enterpriseId,
                                                                        @Param("list") List<Long> ids);

    /**
     * 通过businessId物理删除
     *
     * @param enterpriseId
     * @param businessId
     */
    void deleteAbsoluteByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId);

    /**
     * 根据业务id集合获取标准检查项数据
     */
    List<TbDataDefTableColumnDO> selectByBusinessIdList(@Param("enterpriseId") String enterpriseId, @Param("businessIdList") List<Long> businessIdList);

    /**
     * 查询检查项值
     * @param eid
     * @param dataTableId
     * @param metaTableId
     * @param columnIds
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO>
     * @date: 2022/3/8 16:33
     */
    List<TaskStoreMetaTableColVO> selectDefColumnData(@Param("eid") String eid, @Param("dataTableId") Long dataTableId
            , @Param("metaTableId")Long metaTableId
            , @Param("columnIds") List<Long> columnIds);


    /**
     *
     */
    int updateDelVideo(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,
                               @Param("checkVideo") String checkVideo);

    /**
     * 检查项数量统计
     */
    Integer dataDefColumnNotSubmitCount(@Param("enterpriseId") String enterpriseId, @Param("dataTableId") Long dataTableId);

    /**
     * 根据store表订正采集数据表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int correctRegionIdAndPath(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);
}