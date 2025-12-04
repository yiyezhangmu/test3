package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import com.coolcollege.intelligent.model.operationboard.dto.PatrolTypeStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TaskStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordInfoDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsTableQuery;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolRecordRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolRecordDataVO;
import com.coolcollege.intelligent.model.patrolstore.vo.StoreAcceptanceVO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public interface TbPatrolStoreRecordMapper {

    int insertSelective(@Param("record") TbPatrolStoreRecordDO record, @Param("enterpriseId") String enterpriseId);

    int updateByPrimaryKeySelective(@Param("record") TbPatrolStoreRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量插入
     *
     * @param enterpriseId
     * @param recordList
     * @return
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStoreRecordDO> recordList);

    /**
     * 根据子任务id更新
     *
     * @param enterpriseId
     * @param record
     * @return
     */
    int updateById(@Param("enterpriseId") String enterpriseId, @Param("record") TbPatrolStoreRecordDO record);

    /**
     * 放弃巡店清除部分数据
     * @param enterpriseId
     * @param record
     * @return
     */
    int clearDetail(@Param("enterpriseId") String enterpriseId, @Param("record") TbPatrolStoreRecordDO record);

    /**
     * 根据businessIds删除
     */
    int updateDelByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> ids);

    /**
     * 根据id查询
     *
     * @param enterpriseId
     * @param id
     * @return
     */
    TbPatrolStoreRecordDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 根据id查询
     *
     * @param enterpriseId
     * @param recheckBusinessId
     * @return
     */
    TbPatrolStoreRecordDO selectByRecheckBusinessId(@Param("enterpriseId") String enterpriseId, @Param("recheckBusinessId") Long recheckBusinessId
            , @Param("recheckUserId") String recheckUserId);

    List<TbPatrolStoreRecordDO> selectByIds(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> businessIdSet);

    List<TbPatrolStoreRecordDO> selectByIdsandType(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<Long> businessIdSet);

    /**
     * 根据子任务ids获取巡店记录ids
     */
    List<Long> selectIdsBySubTaskIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> subTaskIds);

    /**
     * 通过子任务id获取未完成的巡店记录
     *
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    @Deprecated
    TbPatrolStoreRecordDO getRecordBySubTaskId(@Param("enterpriseId") String enterpriseId,
        @Param("subTaskId") Long subTaskId, @Param("patrolType") String patrolType);

    TbPatrolStoreRecordDO getRecordByTaskLoopCount(@Param("enterpriseId") String enterpriseId,
                                                   @Param("taskId") Long taskId,
                                                   @Param("storeId") String storeId, @Param("loopCount") Long loopCount,
                                                   @Param("patrolType") String patrolType,
                                                   @Param("status") String status);

    TbPatrolStoreRecordDO getRecordByTaskLoopCountAndOne(@Param("enterpriseId") String enterpriseId,
                                                   @Param("taskId") Long taskId,
                                                   @Param("storeId") String storeId,
                                                   @Param("loopCount") Long loopCount);

    /**
     * 获取同一个子任务下 不同门店的  巡店记录
     * @param enterpriseId
     * @param subTaskId
     * @param storeId
     * @param loopCount
     * @param patrolType
     * @param status
     * @return
     */
    TbPatrolStoreRecordDO getRecordByStaffPlan(@Param("enterpriseId") String enterpriseId,
                                                   @Param("subTaskId") Long subTaskId,
                                                   @Param("storeId") String storeId, @Param("loopCount") Long loopCount,
                                                   @Param("patrolType") String patrolType,
                                                   @Param("status") Integer status);

    /**
     * 检查记录表记录
     *
     * @param enterpriseId
     * @param regionPath
     * @param beginTime
     * @param endTime
     * @return
     */
    Long tableRecordsCount(@Param("enterpriseId") String enterpriseId, @Param("regionPath") String regionPath,
                           @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,@Param("isComplete") Boolean isComplete,
                           @Param("metaTableId") Long metaTableId, @Param("supervisorId") String supervisorId,@Param("status") Integer status);

    /**
     * 查询门店当天未完成的自主巡店记录
     *
     * @param enterpriseId
     * @param userId
     * @param storeId
     * @param patrolType
     * @return
     */
    TbPatrolStoreRecordDO getSpontaneousPatrolStoreRecord(@Param("enterpriseId") String enterpriseId,
        @Param("userId") String userId, @Param("storeId") String storeId, @Param("patrolType") String patrolType);

    TbPatrolStoreRecordDO getRecordByStoreIdAndTime(@Param("enterpriseId") String enterpriseId,
        @Param("userId") String userId, @Param("storeId") String storeId, @Param("date") Date date,
        @Param("patrolType") String patrolType);

    @Deprecated
    Long selectIdBySubTaskId(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId);

    Long selectIdByTaskLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId,
            @Param("storeId") String storeId, @Param("loopCount") Long loopCount);

    List<PersonNodeNoDTO> selectUserByTaskLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId,
                                                    @Param("storeIdList") List<String> storeIdList, @Param("loopCount") Long loopCount);

    /**
     * 通过门店id列表和时间查询记录列表
     *
     * @param storeIdList
     * @param beginTime
     * @param endTime
     * @return
     */
    List<TbPatrolStoreRecordDO> getRecordByStoreIdListAndTime(@Param("enterpriseId") String enterpriseId,
                                                              @Param("storeIdList") List<String> storeIdList,
                                                              @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    /**
     * 获取巡店记录列表
     *
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @param patrolType
     * @param userIdList
     * @return
     */
    List<TbPatrolStoreRecordDO> getRecordList(@Param("enterpriseId") String enterpriseId,
        @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("patrolType") String patrolType,
        @Param("userIdList") List<String> userIdList, @Param("storeIdList") List<String> storeIdList,
        @Param("status") Integer status);


    /**
     * 获取巡店门店数
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @param patrolType
     * @param userIdList
     * @param storeIdList
     * @param status
     * @return
     */
    List<UserDetailStatisticsDTO> getPatrolStoreNum(@Param("enterpriseId") String enterpriseId,
                                              @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("patrolType") String patrolType,
                                              @Param("userIdList") List<String> userIdList, @Param("storeIdList") List<String> storeIdList,
                                              @Param("status") Integer status);

    /**
     * 获取巡店记录(权限)
     *
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @param patrolType
     * @param userIdList
     * @return
     */
    @Deprecated
    List<TbPatrolStoreRecordDO> getPatrolRecordListForMobile(@Param("enterpriseId") String enterpriseId,
                                              @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("patrolType") String patrolType,
                                              @Param("userIdList") List<String> userIdList, @Param("storeIdList") List<String> storeIdList,
                                                    @Param("status") Integer status,  @Param("regionIdList") List<String> regionIdList,
                                                    @Param("regionPathList") List<String> regionPathList);


    /**
     * 获取巡店记录(权限)
     *
     * @param enterpriseId
     * @param beginTime
     * @param endTime
     * @param patrolType
     * @param userIdList
     * @return
     */
    // FXIME metaTableId并不完全准确，代表多表的其中一张表，如果要准确，需要用metaTableIds like %,metaTableId,%
    List<TbPatrolStoreRecordDO> getNewPatrolRecordListForMobile(@Param("enterpriseId") String enterpriseId,
                                                             @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("patrolType") String patrolType,
                                                             @Param("patrolMode") Integer patrolMode,   @Param("patrolOverdue") Boolean patrolOverdue,
                                                             @Param("userIdList") List<String> userIdList, @Param("storeIdList") List<String> storeIdList,
                                                             @Param("status") Integer status,  @Param("regionIdList") List<String> regionIdList,
                                                             @Param("regionPathList") List<String> regionPathList,
                                                             @Param("metaTableId") Long metaTableId,
                                                             @Param("taskName") String taskName,
                                                                @Param("storeName") String storeName,
                                                             @Param("regionId") String regionId,
                                                             @Param("getDirectStore") Boolean getGetDirectStore,
                                                                @Param("queryRegionPath") String queryRegionPath,
                                                                @Param("overdueTaskContinue") Boolean overdueTaskContinue,
                                                                @Param("businessCheckType") String businessCheckType,
                                                                @Param("recheckUserIdList") List<String> recheckUserIdList,
                                                                @Param("recheckStatus") Integer recheckStatus,
                                                                @Param("patrolTypeList") List<String> patrolTypeList);


    /**
     * getPatrolRecordData
     * @param enterpriseId
     * @param userId
     * @return
     */
    PatrolRecordDataVO getPatrolRecordData(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取巡店记录
     * @param enterpriseId
     * @param beginTime
     * @param status
     * @param regionId
     * @param getGetDirectStore
     * @param userCreateTimeFilterDate
     * @return
     */
    List<TbPatrolStoreRecordDO> getDirectorStoreNewPatrolRecordListForMobile(@Param("enterpriseId") String enterpriseId,
                                                                @Param("beginTime") String beginTime,
                                                                @Param("endTime") String endTime,
                                                                @Param("status") Integer status,
                                                                @Param("regionId") String regionId,
                                                                @Param("storeIdList") List<String> storeIdList,
                                                                @Param("getDirectStore") Boolean getGetDirectStore,
                                                                @Param("userCreateTimeFilterDate") Boolean userCreateTimeFilterDate);

    /**
     * 获取员工计划----巡店记录
     * @param enterpriseId
     * @param subTaskId
     * @param storeName
     * @return
     */
    List<TbPatrolStoreRecordDO> getStaffPlanPatrolRecordList(@Param("enterpriseId") String enterpriseId,
                                                                @Param("subTaskId") Long subTaskId,
                                                                @Param("storeName") String storeName);

    /**
     * 检查记录表记录
     *
     * @param enterpriseId
     * @param regionPath
     * @param beginTime
     * @param endTime
     * @return
     */
    List<TbPatrolStoreRecordDO> tableRecords(@Param("enterpriseId") String enterpriseId, @Param("regionPath") String regionPath,
                                             @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,@Param("isComplete") Boolean isComplete,
                                             @Param("metaTableId") Long metaTableId, @Param("supervisorId") String supervisorId,@Param("status") Integer status);

    /**
     * 通过id物理删除
     *
     * @param enterpriseId
     * @param id
     */
    void deleteAbsoluteById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 获取特定签到时间之后的门店id
     *
     * @param enterpriseId
     * @param beginTime
     */
    List<String> getStoreIdListAfterSignInTime(@Param("enterpriseId") String enterpriseId, @Param("beginTime") Date beginTime);


    /**
     * 获取巡店人巡过店的idList
     */
    List<String> selectStoreIdsBySupervisorIds(@Param("enterpriseId") String enterpriseId,
        @Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据门店id获取巡店人idList
     */
    List<String> selectSupervisorIdsByStoreIds(@Param("enterpriseId") String enterpriseId,
        @Param("storeIds") List<String> storeIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<TbPatrolStoreRecordDO> selectBySupervisorIds(@Param("enterpriseId") String enterpriseId,
        @Param("userIds") List<String> userIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 移动端人员执行力个人巡店详情
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @param orderType
     * @return
     */
    List<UserDetailStatisticsDTO> userDetailStatistics(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("orderType") String orderType);

    /**
     * 移动端运营看板任务状态统计
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    TaskStatisticsDTO taskStatistics(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 移动端运营看板巡店方式统计
     *
     * @param enterpriseId
     * @param userIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    PatrolTypeStatisticsDTO patrolTypeStatistics(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 巡店门店维度统计
     */
    List<PatrolStoreStatisticsStoreDTO> patrolStoreStatisticsStore(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<String> storeIds, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);


    /**
     * 单个门店统计巡店数量
     * @param enterpriseId
     * @param storeId
     * @return
     */
    Integer patrolStoreNum(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 人员执行力报表
     */
    List<PatrolStoreStatisticsUserDTO> statisticsUser(@Param("enterpriseId") String enterpriseId,
                                                      @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate,
                                                      @Param("endDate") Date endDate);

    long statisticsUser_COUNT(@Param("enterpriseId") String enterpriseId,
                              @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate,
                              @Param("endDate") Date endDate);

    /**
     * 根据父任务查询所有巡店记录id
     */
    List<Long> selectIdsByTaskId(@Param("enterpriseId") String enterpriseId,
        @Param("taskId") Long taskId);

    List<PatrolStoreStatisticsRankDTO> regionPatrolNumRank(@Param("enterpriseId") String enterpriseId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("regionPath") String regionPath);

    PatrolStoreTaskStatisticsDTO statisticsPatrolTask(@Param("enterpriseId") String enterpriseId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("regionPath") String regionPath, @Param("patrolType") String patrolType);

    PatrolStoreTypeStatisticsDTO statisticsPatrolTypeNum(@Param("enterpriseId") String enterpriseId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("regionPath") String regionPath, @Param("patrolType") String patrolType);


    /**
     * 检查表详情报表数据查询
     *
     * @param enterpriseId
     * @param query
     * @param tableType
     * @return
     */
    List<TbPatrolStoreRecordDO> statisticsDataTable(@Param("enterpriseId") String enterpriseId,
                                                         @Param("query") PatrolStoreStatisticsDataTableQuery query, @Param("tableType") String tableType);

    List<String> selectRecordIdsByQuery(@Param("enterpriseId") String enterpriseId,
                                        @Param("query") PatrolStoreStatisticsDataTableQuery query, @Param("tableType") String tableType);

    /**
     * 检查表详情报表数据查询
     *
     * @param enterpriseId
     * @param query
     * @param tableType
     * @return
     */
    Long statisticsDataTableCount(@Param("enterpriseId") String enterpriseId,
                                  @Param("query") PatrolStoreStatisticsDataTableQuery query, @Param("tableType") String tableType);

    /**
     * 通过metaTableId获取记录
     *
     * @param enterpriseId
     * @param tableIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    @Deprecated
    List<TbPatrolStoreRecordDO> getListByMetaTableIdListAndTime(@Param("enterpriseId") String enterpriseId,
                                                                     @Param("metaTableIdList") List<Long> tableIdList,
                                                                     @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 通过metaTableId获取记录
     *
     * @param enterpriseId
     * @param tableIdList
     * @param beginDate
     * @param endDate
     * @return
     */
    @Deprecated
    List<PatrolStoreStatisticsUserDTO> getListByMetaTableIdListAndTimeGroupBy(@Param("enterpriseId") String enterpriseId,
                                                                @Param("metaTableIdList") List<Long> tableIdList,
                                                                @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    Long countStatisticsUser(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 更新合格数、不合格数、不适用数
     * @Author chenyupeng
     * @Date 2021/7/8
     * @param enterpriseId
     * @param failNum
     * @param passNum
     * @param inapplicableNum
     * @return: int
     */
    int updateCheckResultById(@Param("enterpriseId") String enterpriseId,
                              @Param("id") Long id,
                              @Param("failNum") Integer failNum,
                              @Param("passNum") Integer passNum,
                              @Param("inapplicableNum") Integer inapplicableNum);

    /**
     * 检查表报表详情-检查门店数
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsTableDTO getCheckedStore(@Param("enterpriseId") String enterpriseId,
                                                  @Param("regionPath") String regionPath,
                                                  @Param("storeId") String storeId,
                                                  @Param("query") PatrolStoreStatisticsTableQuery query);

    /**
     * 检查表报表详情-巡店结果比例
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @return: com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsTableDTO getGradeInfo(@Param("enterpriseId") String enterpriseId,
                                                  @Param("regionPath") String regionPath,
                                                  @Param("storeId") String storeId,
                                                  @Param("query") PatrolStoreStatisticsTableQuery query);

    /**
     * 分组查询检查次数
     * @Author chenyupeng
     * @Date 2021/7/16
     * @param enterpriseId
     * @param storeIds
     * @param beginDate
     * @param endDate
     * @param metaTableId
     * @return: java.util.List<com.coolcollege.intelligent.model.store.vo.StoreCoverVO>
     */
    List<PatrolStoreStatisticsTableDTO> getCheckedTimesGroup(@Param("enterpriseId") String enterpriseId,
                                                       @Param("storeIds") List<String> storeIds,
                                                       @Param("beginDate") Date beginDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("metaTableId") Long metaTableId);

    /**
     * 通过任务名称查询
     * @Author chenyupeng
     * @Date 2021/8/2
     * @param enterpriseId
     * @param regionPath
     * @param storeIdList
     * @param metaTableId
     * @param taskName
     * @param beginTime
     * @param endTime
     * @return: java.util.List<com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO>
     */
    List<TbPatrolStoreRecordDO> getRecordByTaskName(@Param("enterpriseId") String enterpriseId,
                                                              @Param("regionPath") String regionPath,
                                                              @Param("storeIdList") List<String> storeIdList,
                                                              @Param("metaTableId") Long metaTableId,
                                                              @Param("taskName") String taskName,
                                                              @Param("beginTime") Date beginTime, @Param("endTime") Date endTime,
                                                    @Param("completeBeginDate") Date completeBeginDate, @Param("completeEndDate") Date completeEndDate,
                                                              @Param("patrolType") String patrolType,
                                                    @Param("regionPathList") List<String> regionPathList,
                                                    @Param("status") Integer status);

    /**
     * 更新巡店记录里的巡店设置
     * @param enterpriseId
     * @param ids
     * @param setting
     */
    void updateStoreCheckSettings(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids, @Param("setting") EnterpriseStoreCheckSettingDO setting);

    void potralStoreSummarySave(@Param("enterpriseId") String enterpriseId, @Param("query") PatrolRecordRequest query);

    void patrolStoreSignatureSave(@Param("enterpriseId") String enterpriseId, @Param("query") PatrolRecordRequest query);

    int batchUpdateVideo(@Param("enterpriseId") String enterpriseId,@Param("list") List<TbPatrolStoreRecordDO> tbPatrolStoreRecordDOS);

    int updateVideo(@Param("enterpriseId") String enterpriseId,@Param("query") TbPatrolStoreRecordDO tbPatrolStoreRecordDO);

    List<TbPatrolStoreRecordDO> getAutonomyPatrolRecordList(@Param("enterpriseId") String enterpriseId,
                                                            @Param("query") PatrolRecordRequest query);

    List<PatrolStoreStatisticsGroupByPatrolTypeDto> getNumByPatrolType(@Param("eid") String eid,
                                                                 @Param("regionPath") String regionPath,
                                                                 @Param("isRoot") boolean isRoot,
                                                                 @Param("beginDate") Date beginDate,
                                                                 @Param("endDate") Date endDate,
                                                                 @Param("storeIds") List<String> storeIds);

    List<TbPatrolStoreRecordDO> getRemoveAutonomyPatrolRecordList(@Param("enterpriseId") String enterpriseId,
                                                            @Param("supervisorId") String supervisorId,
                                                            @Param("beginDate") String beginDate,
                                                            @Param("endDate") String endDate);

    /**
     * 个人已完成巡店记录
     * @return
     */
    List<TbPatrolStoreRecordDO> getPatrolRecordListForPerson(@Param("enterpriseId") String enterpriseId,
                                                             @Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("patrolType") String patrolType,
                                                             @Param("patrolMode") Integer patrolMode, @Param("patrolOverdue") Boolean patrolOverdue,
                                                             @Param("createBeginTime") String createBeginTime,
                                                             @Param("createEndTime") String createEndTime,
                                                             @Param("storeIdList") List<String> storeIdList,
                                                             @Param("regionPath") String regionPath, @Param("metaTableId") Long metaTableId,
                                                             @Param("supervisorId") String supervisorId, @Param("taskName") String taskName);

    /**
     * 查询指定时间段内指定检查表巡店的门店
     * @param enterpriseId
     * @param storeIds
     * @param beginDate
     * @param endDate
     * @param metaTableId
     * @return
     */
    List<String> getPatrolStoreIdBySpecifiedTime(@Param("enterpriseId") String enterpriseId,
                                                 @Param("storeIds") List<String> storeIds,
                                                 @Param("beginDate") Date beginDate,
                                                 @Param("endDate") Date endDate,
                                                 @Param("metaTableId") Long metaTableId,
                                                 @Param("regionIds") List<String> regionIds);

    /**
     * 门店巡店统计
     *
     * @param eid
     * @return
     */
    List<PatrolStoreStatisticsDTO> selectStorePatrolList(@Param("eid") String eid,
                                                         @Param("storeIds") List<String> storeIds,
                                                         @Param("beginDate") Date beginDate,
                                                         @Param("endDate") Date endDate,
                                                         @Param("metaTableId") Long metaTableId);

    int countAll(@Param("eid") String eid);

    int updateCheckResultLevel(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStoreRecordDO> list);

    List<TbPatrolStoreRecordDO> selectPatrolStoreByCondition(@Param("enterpriseId") String enterpriseId,
                                                             @Param("beginTime") Date beginTime,
                                                             @Param("endTime") Date endTime,
                                                             @Param("metaTableId") Long metaTableId,
                                                             @Param("patrolTypeList") List<String> patrolTypeList,
                                                             @Param("patrolStoreMode") Integer patrolStoreMode);

    Long countPatrolStoreByCondition(@Param("enterpriseId") String enterpriseId,
                                     @Param("beginTime") Date beginTime,
                                     @Param("endTime") Date endTime,
                                     @Param("metaTableId") Long metaTableId,
                                     @Param("patrolTypeList") List<String> patrolTypeList,
                                     @Param("patrolStoreMode") Integer patrolStoreMode);


    List<TbPatrolStoreRecordDO> getPatrolRecordByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds") List<Long> unifyTaskIds);


    int copyPatrolRecord(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStoreRecordDO> recordList);

    void deleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 更新巡店记录提交状态
     */
    int updateSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,@Param("submitStatus")Integer submitStatus);



    List<TbPatrolStoreRecordDO>  selectIdByTaskLoopCountAndSupervisorId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId,
                                 @Param("supervisorId") String supervisorId, @Param("loopCount") Long loopCount);

    /**
     * 根据businessIds删除
     */
    int updateSupervisorIdByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> ids, @Param("supervisorId") String supervisorId);

    /**
     * 获取最近七天巡店的门店
     * @param enterpriseId
     * @param supervisorId
     * @return
     */
    List<String> getLastSevenDayPatrolStoreIds(@Param("enterpriseId") String enterpriseId, @Param("supervisorId") String supervisorId);

    /**
     * 上次巡店时间
     * @param enterpriseId
     * @param storeIds
     * @return
     */
    List<LastPatrolStoreTimeDTO> getLastPatrolStoreTime(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);


    /**
     * 复审概览
     * @param enterpriseId
     * @param regionPathList
     * @param beginDate
     * @param endDate
     * @return
     */
    Long patrolOverviewNeedRecheckCount(@Param("enterpriseId") String enterpriseId, @Param("regionPathList") List<String> regionPathList,
                                          @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    Long patrolOverviewRecheckCount(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,
                                        @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /**
     * 更新巡店记录复审状态
     */
    int updateNeedRecheck(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<TbPatrolStoreRecordDO> getByRecheckBusinessId(@Param("enterpriseId")String enterpriseId,@Param("recheckBusinessId") Long businessId);

    List<String> selectRecheckRecordIdsByQuery(@Param("enterpriseId") String enterpriseId,
                                           @Param("query") PatrolStoreStatisticsDataTableQuery query, @Param("tableType") String tableType);

    /**
     * 人员执行力报表
     */
    List<PatrolStoreStatisticsUserDTO> statisticsSafetyCheckUser(@Param("enterpriseId") String enterpriseId,
                                                      @Param("userIdList") List<String> userIdList, @Param("beginDate") Date beginDate,
                                                      @Param("endDate") Date endDate);

    List<TbPatrolStoreRecordDO> deleteListByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId,
                                                        @Param("taskStatus")String taskStatus);

    List<TbPatrolStoreRecordDO> deleteListByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds")List<Long> unifyTaskIds,
                                                         @Param("taskStatus")String taskStatus);

    /**
     * 查询已巡记录
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param queryDate 查询日期
     * @return 巡店记录
     */
    List<TbPatrolStoreRecordDO> getFinishedRecord(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("queryDate") Date queryDate);

    /**
     * 根据store表订正巡店记录表的regionId和regionPath
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @return int
     */
    int correctRegionIdAndPath(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据第三方业务id查询巡店记录
     * @param enterpriseId 企业id
     * @param thirdBusinessId 第三方业务id
     * @return 巡店记录列表
     */
    List<TbPatrolStoreRecordDO> getByThirdBusinessId(@Param("enterpriseId") String enterpriseId, @Param("thirdBusinessId") String thirdBusinessId);
}