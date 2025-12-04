package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.metatable.vo.CategoryStatisticsVO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaTableColVO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardRankDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardTrendDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.query.TableBoardQuery;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnDetailListDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.SummaryByStoreDTO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataStaColumnQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsRegionQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsTableQuery;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Mapper
public interface TbDataStaTableColumnMapper {

    int insertSelective(@Param("record") TbDataStaTableColumnDO record, @Param("enterpriseId") String enterpriseId);

    int updateByPrimaryKeySelective(@Param("record") TbDataStaTableColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量插入
     *
     * @param enterpriseId
     * @param tbDataStaTableColumnDOList
     * @return
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList);

    /**
     * 批量更新
     *
     * @param enterpriseId
     * @param businessId
     * @param tbDataStaTableColumnDOList
     * @param submit
     * @return
     */
    int batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                    @Param("businessType") String businessType, @Param("dataTableId") Long dataTableId,
                    @Param("list") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList, @Param("submit") boolean submit);

    int batchUpdateResult(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList);

    /**
     * 通过父任务Id和子任务id获取
     *
     * @param enterpriseId
     * @param taskId
     * @return
     */
    List<TbDataStaTableColumnDO> getListBySubTaskIdAndTaskId(@Param("enterpriseId") String enterpriseId,
                                                             @Param("taskId") Long taskId, @Param("subTaskIdList") List<Long> subTaskIdList);

    /**
     * 巡店门店维度统计
     */
    List<PatrolStoreStatisticsStoreDTO> patrolStoreStatisticsStore(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("list") List<String> storeIds, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 巡店门店维度统计
     */
    List<PatrolStoreStatisticsStoreDTO> patrolStoreStatisticsRegionPathLeft(@Param("enterpriseId") String enterpriseId,
                                                                            @Param("regionPathLeft") String regionPathLeft, @Param("beginDate") Date beginDate,
                                                                            @Param("endDate") Date endDate);

    /**
     * 区域统计
     *
     * @param eid
     * @param regionPath
     * @param beginDate
     * @param endDate
     * @return
     */
    PatrolStoreStatisticsRegionDTO patrolStoreStatisticsRegionRecord(@Param("eid") String eid,
                                                                     @Param("regionPath") String regionPath,
                                                                     @Param("isRoot") boolean isRoot,
                                                                     @Param("beginDate") Date beginDate,
                                                                     @Param("endDate") Date endDate,
                                                                     @Param("storeIds") List<String> storeIds);

    /**
     * 区域统计
     *
     * @param eid
     * @param regionPath
     * @param beginDate
     * @param endDate
     * @return
     */
    PatrolStoreStatisticsRegionDTO patrolStoreStatisticsRegionColumn(@Param("eid") String eid,
                                                                     @Param("regionPath") String regionPath, @Param("isRoot") boolean isRoot, @Param("beginDate") Date beginDate,
                                                                     @Param("endDate") Date endDate, @Param("storeIds") List<String> storeId);

    /**
     * 人员执行力报表
     */
    List<PatrolStoreStatisticsUserDTO> statisticsUser(@Param("enterpriseId") String enterpriseId,
                                                      @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate,
                                                      @Param("endDate") Date endDate);

    List<TbDataStaTableColumnDO> getListByRecordIdListForMap(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList);

    /**
     * 根据businessIds删除
     */
    int updateDelByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> businessIds,
                               @Param("businessType") String businessType);

    /**
     * 根据记录id和meta检查表ids硬删除
     *
     * @param enterpriseId
     * @param businessId
     * @param metaStaTableIds
     * @return
     */
    int delByBusinessIdAndMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                       @Param("businessType") String businessType,
                                       @Param("list") List<Long> metaStaTableIds);

    /**
     * 根据业务id获取自定义检查项数据
     */
    List<TbDataStaTableColumnDO> selectByBusinessIdAndMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                                                   @Param("list") List<Long> metaStaTableIds);

    /**
     * 更新业务状态
     */
    int updateBusinessStatus(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId, @Param("subTaskId") Long subTaskId,
                             @Param("businessType") String businessType, @Param("supervisorId") String supervisorId);

    /**
     * 根据业务Id查询是否存在未检查的检查项
     */
    int existNotCheckByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                  @Param("businessType") String businessType);

    /**
     * 根据metaTableId获取
     */
    List<TbDataStaTableColumnDO> selectByMetaTableId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("metaTableId") Long metaTableId,
                                                     @Param("businessType") String businessType);

    /**
     * 根据metaTableId获取
     */
    List<TbDataStaTableColumnDO> selectByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("dataTableId") Long dataTableId);

    /**
     * Id获取
     */
    TbDataStaTableColumnDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 根据业务id获取标准检查项数据
     */
    List<TbDataStaTableColumnDO> selectByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                    @Param("businessId") Long businessId,
                                                    @Param("businessType") String businessType);

    List<TbDataStaTableColumnDO> selectByBusinessIdAndTaskId(@Param("enterpriseId") String enterpriseId,
                                                    @Param("businessId") Long businessId,
                                                    @Param("taskId") Long taskId);

    /**
     * 根据业务id获取标准检查项数据
     */
    List<CategoryStatisticsVO> selectCategoryStatisticsListByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                                        @Param("businessId") Long businessId,
                                                                        @Param("businessType") String businessType);

    /**
     * 根据业务id获取标准检查项数据
     */
    List<Long> selectStoreSceneIdByBusinessId(@Param("enterpriseId") String enterpriseId,
                                              @Param("businessId") Long businessId,
                                              @Param("businessType") String businessType);

    List<TbDataStaTableColumnDO> selectByTaskQuestionId(@Param("enterpriseId") String enterpriseId,
                                                        @Param("taskQuestionId") Long taskQuestionId);

    /**
     * 更新问题工单Id
     */
    int updateTaskQuestionId(@Param("enterpriseId") String enterpriseId,
                             @Param("record") TbDataStaTableColumnDO record);

    /**
     * 更新问题工单状态
     */
    int updateTaskQuestionStatus(@Param("enterpriseId") String enterpriseId,
                                 @Param("record") TbDataStaTableColumnDO build);

    /**
     * 删除问题工单
     */
    int delTaskQuestion(@Param("enterpriseId") String enterpriseId, @Param("taskQuestionId") Long taskQuestionId, @Param("id") Long id);

    /**
     * 检查项基础详情统计
     */
    List<TbDataStaTableColumnDO> dataStaColumnStatistics(@Param("enterpriseId") String enterpriseId,
                                                         @Param("query") PatrolStoreStatisticsDataStaColumnQuery query, @Param("regionPathLeft") String regionPathLeft,
                                                         @Param("businessType") String businessType);

    /**
     * 检查项基础详情统计
     */
    Long dataStaColumnStatisticsCount(@Param("enterpriseId") String enterpriseId,
                                      @Param("query") PatrolStoreStatisticsDataStaColumnQuery query, @Param("regionPathLeft") String regionPathLeft,
                                      @Param("businessType") String businessType);

    /**
     * 检查项数量统计
     */
    Integer dataStaColumnNumCount(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId);

    /**
     * 检查项数量统计
     */
    Integer dataStaColumnNotSubmitCount(@Param("enterpriseId") String enterpriseId, @Param("dataTableId") Long dataTableId);

    /**
     * 获取失败的检查项
     */
    List<TbDataStaTableColumnDO> selectFailByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                        @Param("businessId") Long businessId, @Param("businessType") String businessType);

    List<TbDataStaTableColumnDO> getListByRecordIdList(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList);

    List<TbMetaStaColumnVO> getVOListByRecordIdList(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                                    @Param("query") PatrolStoreStatisticsDataTableQuery query);

    List<TbMetaStaColumnVO> getVOListByRecordIdAndPatrolStoreTime(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                                    @Param("query") PatrolStoreStatisticsDataTableQuery query);

    Long getVOListByRecordIdListCount(@Param("enterpriseId") String enterpriseId, @Param("recordIdList") List<Long> recordIdList,
                                                    @Param("query") PatrolStoreStatisticsDataTableQuery query);

    /**
     * 根据检查表数据id统计
     */
    List<PatrolStoreStatisticsDataStaTableDTO> statisticsColumnCount(@Param("enterpriseId") String enterpriseId,
                                                                     @Param("list") List<Long> dataTableIds);

    /**
     * 根据检查表数据id统计
     */
    List<PatrolStoreStatisticsDataStaTableDTO> statisticsColumnCountByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                                                 @Param("list") List<Long> businessIds);

    /**
     * 根据检查表数据id统计
     */
    List<PatrolStoreStatisticsDataStaTableCountDTO> statisticsColumnCountByBusinessIdGroupByDataTableId(@Param("enterpriseId") String enterpriseId,
                                                                                 @Param("list") List<Long> businessIds);

    /**
     * 根据检查表模板id统计
     */
    List<PatrolStoreStatisticsMetaStaTableDTO> patrolStoreStatisticsMetaStaTable(@Param("enterpriseId") String enterpriseId,
                                                                                 @Param("list") List<Long> metaTableIds,
                                                                                 @Param("beginDate") Date beginDate,
                                                                                 @Param("endDate") Date endDate);

    /**
     * 根据metaTableId获取创建问题数，metaTableId为null，获取企业创建问题数
     */
    Integer getQuestionNum(@Param("enterpriseId") String enterpriseId,
                           @Param("metaTableIds") List<Long> metaTableIds,
                           @Param("beginDate") Date beginDate,
                           @Param("endDate") Date endDate);

    List<PatrolStoreStatisticsColumnDTO> statisticsColumnPerTable(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("columnIdList") List<Long> columnIdList,
                                                                  @Param("beginDate") Date beginDate,
                                                                  @Param("endDate") Date endDate);


    long statisticsColumnPerTable_COUNT(@Param("enterpriseId") String enterpriseId,
                                        @Param("columnIdList") List<Long> columnIdList,
                                        @Param("beginDate") Date beginDate,
                                        @Param("endDate") Date endDate);

    /**
     * 通过businessId物理删除
     *
     * @param enterpriseId
     * @param businessId
     */
    int deleteAbsoluteByBusinessId(@Param("enterpriseId") String enterpriseId,
                                   @Param("businessId") Long businessId);

    /**
     * 移动端人员执行力汇总
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    UserStatisticsDTO appUserStatistics(@Param("enterpriseId") String enterpriseId,
                                        @Param("userIdList") List<String> userIdList,
                                        @Param("beginDate") Date beginDate,
                                        @Param("endDate") Date endDate);

    /**
     * 通过人员查已提交工单的列表
     *
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TbDataStaTableColumnDO> getPatrolQuestionListByUserIdList(@Param("enterpriseId") String enterpriseId,
                                                                   @Param("userIdList") List<String> userIdList,
                                                                   @Param("beginDate") Date beginDate,
                                                                   @Param("endDate") Date endDate);

    /**
     * 通过门店查已提交工单的列表
     *
     * @param enterpriseId
     * @param storeIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TbDataStaTableColumnDO> getPatrolQuestionListByStoreIdList(@Param("enterpriseId") String enterpriseId,
                                                                    @Param("storeIdList") List<String> storeIdList,
                                                                    @Param("beginDate") Date beginDate,
                                                                    @Param("endDate") Date endDate);

    /**
     * 通过巡店记录查询已提交工单的列表
     *
     * @param enterpriseId
     * @param recordIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TbDataStaTableColumnDO> getPatrolQuestionListByRecordIdList(@Param("enterpriseId") String enterpriseId,
                                                                     @Param("recordIdList") List<Long> recordIdList,
                                                                     @Param("beginDate") Date beginDate,
                                                                     @Param("endDate") Date endDate);

    /**
     * 移动端运营看板人员执行力个人详情
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @param orderType
     * @return
     */
    List<UserDetailStatisticsDTO> userDetailStatistics(@Param("enterpriseId") String enterpriseId,
                                                       @Param("userIdList") List<String> userIdList,
                                                       @Param("beginDate") Date beginDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("orderType") String orderType);

    List<TableBoardRankDTO> tableBoardRankQuestionNum(@Param("enterpriseId") String enterpriseId,
                                                      @Param("beginDate") Date beginDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("metaTableIds") List<Long> metaTableIds);

    List<TableBoardTrendDTO> tableBoardTrendQuestionNum(@Param("enterpriseId") String enterpriseId,
                                                        @Param("query") TableBoardQuery query);

    /**
     * 获取门店巡店排行
     *
     * @param eid
     * @param path
     * @param isRoot
     * @return
     */
    List<PatrolStoreStatisticsRankDTO> selectStorePatrolNum(@Param("eid") String eid,
                                                            @Param("path") String path,
                                                            @Param("isRoot") boolean isRoot,
                                                            @Param("beginDate") Date beginDate,
                                                            @Param("endDate") Date endDate);

    /**
     * 获取门店巡店问题排行
     *
     * @param eid
     * @param path
     * @param isRoot
     * @return
     */
    List<PatrolStoreStatisticsRankDTO> selectStoreProblemNum(@Param("eid") String eid,
                                                             @Param("path") String path, @Param("isRoot") boolean isRoot, @Param("beginDate") Date beginDate,
                                                             @Param("endDate") Date endDate);

    /**
     * 门店巡店统计
     *
     * @param eid
     * @return
     */
    List<PatrolStoreStatisticsDTO> selectStorePatrolList(@Param("eid") String eid,
                                                         @Param("storeIds") List<String> storeIds,
                                                         @Param("query") PatrolStoreStatisticsRegionQuery query);

    List<TbDataStaTableColumnDO> getListByMetaColumnId(@Param("enterpriseId") String enterpriseId,
        @Param("metaColumnId") Long metaColumnId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
                                                       @Param("regionPath") String regionPath,@Param("storeIds") List<String> storeIds,@Param("checkResult") String checkResult);

    List<PatrolStoreStatisticsProblemRankDTO> regionQuestionNumRank(@Param("enterpriseId") String enterpriseId,
                                                                    @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                                    @Param("regionPath") String regionPath);

    List<TbDataStaTableColumnDO> getListByQuestionIds(@Param("enterpriseId") String enterpriseId,
                                                      @Param("questionIds") List<Long> questionIdList);

    /**
     * 检查表报表详情-检查门店数
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsTableDTO statisticWorkOrder(@Param("enterpriseId") String enterpriseId,
                                                  @Param("regionPath") String regionPath,
                                                  @Param("storeId") String storeId,
                                                  @Param("query") PatrolStoreStatisticsTableQuery query);

    /**
     * 检查表报表详情-检查门店数
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO
     */
    List<PatrolStoreStatisticsTableColumnDTO> statisticMetaColumn(@Param("enterpriseId") String enterpriseId,
                                               @Param("regionPath") String regionPath,
                                               @Param("storeId") String storeId,
                                               @Param("query") PatrolStoreStatisticsTableQuery query);

    /**
     * 巡店详情报表统计
     * @param enterpriseId
     * @param tableId
     * @param storeIds
     * @param regionPath
     * @return
     */
    List<PatrolStoreColumnStatisticsDTO> statisticsColumnDetail(@Param("enterpriseId") String enterpriseId,
                                                                @Param("tableId") Long tableId,
                                                                @Param("storeIds") List<String> storeIds,
                                                                @Param("regionPath") String regionPath,
                                                                @Param("beginDate") Date beginDate,
                                                                @Param("endDate") Date endDate,
                                                                @Param("metaColumnIds") List<Long> metaColumnIds);

    /**
     * 检查项巡店结果分析
     * @param enterpriseId
     * @param tableId
     * @param beginDate
     * @param endDate
     * @param storeIds
     * @param regionPath
     * @return
     */
    PatrolStoreResultAnalyzeDTO statisticsPatrolStoreResult(@Param("enterpriseId") String enterpriseId,
                                                            @Param("tableId") Long tableId,
                                                            @Param("beginDate") Date beginDate,
                                                            @Param("endDate") Date endDate,
                                                            @Param("storeIds") List<String> storeIds,
                                                            @Param("regionPath") String regionPath);

    /**
     * 检查项工单趋势
     * @param enterpriseId
     * @param tableId
     * @param beginDate
     * @param endDate
     * @param regionPath
     * @param storeIds
     * @return
     */
    LinkedList<ColumnQuestionTrendDTO> columnQuestionTrend(@Param("enterpriseId") String enterpriseId,
                                                           @Param("tableId") Long tableId,
                                                           @Param("beginDate") Date beginDate,
                                                           @Param("endDate") Date endDate,
                                                           @Param("regionPath") String regionPath,
                                                           @Param("storeIds") List<String> storeIds);

    /**
     * 检查项详情列表
     * @param enterpriseId
     * @param beginDate
     * @param endDate
     * @param metaColumnId
     * @param regionPath
     * @param storeIds
     * @param checkStatus
     * @return
     */
    List<ColumnDetailListDTO> columnDetailList(@Param("enterpriseId") String enterpriseId,
                                               @Param("beginDate") Date beginDate,
                                               @Param("endDate") Date endDate,
                                               @Param("metaColumnId") Long metaColumnId,
                                               @Param("regionPath") String regionPath,
                                               @Param("storeIds") List<String> storeIds,
                                               @Param("checkStatus") String checkStatus);

    /**
     * 查询工单列表
     * @param enterpriseId
     * @param beginDate
     * @param endDate
     * @param tableId
     * @param regionPath
     * @param storeIds
     * @return
     */
    List<TbDataStaTableColumnDO> questionList(@Param("enterpriseId") String enterpriseId,
                                              @Param("beginDate") Date beginDate,
                                              @Param("endDate") Date endDate,
                                              @Param("tableId") Long tableId,
                                              @Param("regionPath") String regionPath,
                                              @Param("storeIds") List<String> storeIds,
                                              @Param("getDirectStore") Boolean getDirectStore,
                                              @Param("status") String status,
                                              @Param("regionId") String regionId);

    /**
     * 检查项分析
     * @param enterpriseId
     * @param fullRegionPath
     * @param storeIds
     * @param beginDate
     * @param endDate
     * @param tableId
     * @return
     */
    ColumnAnalyzeDTO columnAnalyze(@Param("enterpriseId") String enterpriseId,
                                   @Param("regionPath") String fullRegionPath,
                                   @Param("storeIds") List<String> storeIds,
                                   @Param("beginDate") Date beginDate,
                                   @Param("endDate") Date endDate,
                                   @Param("tableId") Long tableId);

    /**
     * 根据业务id集合获取标准检查项数据
     */
    List<TbDataStaTableColumnDO> selectByBusinessIdList(@Param("enterpriseId") String enterpriseId,
                                                        @Param("businessIdList") List<Long> businessIdList,
                                                        @Param("metaColumnIdList") List<Long> metaColumnIdList);


    int batchUpdateVideo(@Param("enterpriseId") String enterpriseId,@Param("list") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList);

    int updateVideo(@Param("enterpriseId") String enterpriseId,@Param("query") TbDataStaTableColumnDO tbDataStaTableColumnDO);

    List<SummaryByStoreDTO> getGroupDataByStore(@Param("enterpriseId") String enterpriseId,
                                          @Param("metaColumnId") Long metaColumnId,
                                          @Param("beginDate") Date beginDate,
                                          @Param("endDate") Date endDate,
                                          @Param("regionPath") String fullRegionPath,@Param("storeIds") List<String> storeIds);
    List<Map<String,Long>>  getCountGroupByMetaColumnId(@Param("enterpriseId") String enterpriseId,
                                                  @Param("metaColumnId") Long metaColumnId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
                                                  @Param("regionPath") String regionPath,@Param("storeIds") List<String> storeIds);

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
    List<TaskStoreMetaTableColVO> selectStaColumnData(@Param("eid") String eid,
                                                      @Param("dataTableId") Long dataTableId,
                                                      @Param("metaTableId")Long metaTableId,
                                                      @Param("columnIds") List<Long> columnIds);

    /**
     * 根据id列表查询
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return List<TbDataStaTableColumnDO>
     */
    List<TbDataStaTableColumnDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 根据id查询
     * @param enterpriseId
     * @param id
     * @return
     */
    TbDataStaTableColumnDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);


    List<TbDataStaTableColumnDO> getTbDataColumnListByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds") List<Long> unifyTaskIds);

    void deleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    void copyDataColumn(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOList);

    List<TbDataStaTableColumnDO> selectPatrolStoreReviewListByBusinessId(@Param("enterpriseId")String enterpriseId,@Param("recordIds")List<String> recordIds);

    Long selectCountPatrolStoreReviewListByBusinessId(@Param("enterpriseId")String enterpriseId,@Param("recordIds") List<String> recordIds);

    /**
     * 更新handlerUserId 和  patrolStoreTime  项的修改时间 Date patrolStoreTime
     */
    int updateHandlerUserIdByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId, @Param("businessType") String businessType
                                        ,@Param("handlerUserId") String handlerUserId, @Param("patrolStoreTime") Date patrolStoreTime);

    int updateByList(@Param("tbDataStaTableColumnDOs") List<TbDataStaTableColumnDO> tbDataStaTableColumnDOs,
                     @Param("enterpriseId") String enterpriseId);

    TbDataStaTableColumnDO selectByTaskIdAndMetaColumnId(@Param("enterpriseId") String enterpriseId,
                                                         @Param("taskId") Long taskId,
                                                         @Param("metaColumnId") Long metaColumnId,
                                                         @Param("storeId") String storeId);

    List<String> getFailReason(@Param("enterpriseId") String enterpriseId,@Param("dataTableId") Long id,@Param("businessId") Long businessId);

    List<TbDataStaTableColumnDO> selectDataColumn(@Param("enterpriseId")String enterpriseId,@Param("dataTableIds") List<Long> dataTableIds);

    List<DataStaTableColumnVO> getCheckDetailList(@Param("enterpriseId")String enterpriseId,@Param("businessIds") List<Long> businessIds);

    /**
     * 根据store表订正采集数据表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int correctRegionIdAndPath(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);
}
