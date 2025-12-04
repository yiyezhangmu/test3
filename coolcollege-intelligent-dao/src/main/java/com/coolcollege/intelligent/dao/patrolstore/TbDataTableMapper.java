package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaDataVO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardRankDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardStatisticDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardTrendDTO;
import com.coolcollege.intelligent.model.operationboard.query.TableBoardQuery;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreRecordStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsTableQuery;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolRecordRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Mapper
public interface TbDataTableMapper {

    int insertSelective(@Param("record") TbDataTableDO record, @Param("enterpriseId") String enterpriseId);

    int updateByPrimaryKeySelective(@Param("record") TbDataTableDO record, @Param("enterpriseId") String enterpriseId);

    int updateForeachByPrimaryKeySelective(@Param("records") List<TbDataTableDO> records, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量插入
     *
     * @param enterpriseId
     * @param tbDataTableDOList
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbDataTableDO> tbDataTableDOList);

    /**
     * 根据businessId更新tbDateTable数据
     *
     * @param enterpriseId
     * @param tbDataTableDO
     */
    int updateTbDateTableByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("tbDataTableDO") TbDataTableDO tbDataTableDO);

    List<TbDataTableDO> selectListByIdList(@Param("enterpriseId") String enterpriseId,
                                           @Param("dataTableIdList") List<Long> dataTableIdList);

    /**
     * 根据businessIds删除
     */
    int updateDelByBusinessIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> businessIds,
                               @Param("businessType") String businessType);

    /**
     * 根据记录id和meta检查表ids硬删除
     *
     */
    int delByBusinessIdAndMetaTableIds(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                       @Param("businessType") String businessType,
                                       @Param("list") List<Long> metaStaTableIds);

    /**
     * 更新业务状态
     */
    int updateBusinessStatus(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId, @Param("subTaskId") Long subTaskId,
                             @Param("businessType") String businessType, @Param("supervisorId") String supervisorId);

    /**
     * 更新检查表提交状态
     */
    int updateSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,@Param("submitStatus")Integer submitStatus);

    /**
     * 更新审批人信息
     */
    int updateAuditInfo(@Param("enterpriseId") String enterpriseId
            ,@Param("businessId") Long businessId, @Param("businessType") String businessType
            ,@Param("auditUserId") String auditUserId, @Param("auditUserName") String auditUserName
            ,@Param("auditPicture") String auditPicture, @Param("auditOpinion") String auditOpinion
            ,@Param("auditRemark") String auditRemark);

    List<TbDataTableDO> getSubmitTableByRecordId(@Param("enterpriseId") String enterpriseId,
                                                 @Param("businessId") Long businessId, @Param("taskId") Long taskId);

    /**
     * 根据业务id查询是否存在未提交检查表
     */
    int existNotSubmitByBusinessId(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                   @Param("businessType") String businessType);

    /**
     * 通过子任务id和父任务id查询检查表信息
     * @param eId
     * @param subTaskIdList
     * @param taskId
     * @return
     */
    List<TbDataTableDO> getListBySubTaskIdAndTaskId(@Param("enterpriseId") String eId, @Param("subTaskIdList") List<Long> subTaskIdList, @Param("taskId") Long taskId);

    List<Long> getListByTaskId(@Param("enterpriseId") String eId, @Param("taskId") Long taskId);

    /**
     * 根据业务id获取检查表数据
     */
    List<TbDataTableDO> selectByBusinessId(@Param("enterpriseId") String enterpriseId,
                                           @Param("businessId") Long businessId, @Param("businessType") String businessType);

    List<TbDataTableDO> selectByBusinessIdAndTaskId(
            @Param("enterpriseId") String enterpriseId,
            @Param("businessId") Long businessId,
            @Param("taskId") Long taskId);

    /**
     * 根据业务id获取检查表数据
     */
    TbDataTableDO selectOneByBusinessId(@Param("enterpriseId") String enterpriseId,
                                        @Param("businessId") Long businessId, @Param("businessType") String businessType);

    /**
     * 根据业务id获取检查表数据
     */
    TbDataTableDO selectById(@Param("enterpriseId") String enterpriseId,
                                        @Param("dataTableId") Long dataTableId);

    String selectTableNameById(@Param("enterpriseId") String enterpriseId, @Param("dataTableId") Long dataTableId);

    List<TbDataTableDO> getListByBusinessIdList(@Param("enterpriseId") String enterpriseId,@Param("businessIdList") List<Long> businessIdList,@Param("businessType") String businessType);

    List<PersonNodeNoDTO> getUserListByBusinessIdList(@Param("enterpriseId") String enterpriseId, @Param("businessIdList") List<Long> businessIdList, @Param("businessType") String businessType);

    /**
     * 通过businessId物理删除
     * @param enterpriseId
     * @param businessId
     */
    void deleteAbsoluteByBusinessId(String enterpriseId, Long businessId);

    int updateSubTaskIdById(@Param("enterpriseId") String enterpriseId, @Param("updatedSubTaskId")Long updatedSubTaskId, @Param("id")Long id);

    /**
     * 修改submitstatus状态
     * @param enterpriseId
     * @param query
     */
    void changeSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("query")PatrolRecordRequest query);

    void resetSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("query")PatrolRecordRequest query);

    /**
     * 修改submitstatus状态
     * @param enterpriseId
     * @param query
     */
    void changeDataTableSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("query")PatrolRecordRequest query);

    /**
     * 获取submitStatus状态
     * @param enterpriseId
     * @param businessId
     * @return
     */
    Integer getSubmitStatus(@Param("enterpriseId") String enterpriseId, @Param("businessId")Long businessId);

    /**
     * 检查表配置之前保存submitStatus
     * @param enterpriseId
     * @param businessId
     */
    void insertBeforeConfigTbDateTable(@Param("enterpriseId") String enterpriseId,
                                       @Param("businessId")Long businessId,
                                       @Param("businessType")String businessType,
                                       @Param("submitStatus") Integer submitStatus,
                                       @Param("signStartTime") Date signStartTime,
                                       @Param("signEndTime") Date signEndTime,
                                       @Param("patrolType") String patrolType);

    /**
     * 获得指定门店的最新检查表使用情况,只查已完成任务的表
     * @param enterpriseId
     * @param storeId
     * @param metaTableId
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.patrolstore.TbDataTableDO>
     * @date: 2022/3/8 10:54
     */
    TaskStoreMetaDataVO selectLastMetaTableData(@Param("enterpriseId") String enterpriseId,
                                                @Param("storeId") String storeId,
                                                @Param("metaTableId") Long metaTableId);



    /**
     * 获取上次检查结果
     * @param enterpriseId
     * @param storeId
     * @param metaTableId
     * @return
     */
    TbDataTableDO getLastTimeDataTableDO(@Param("enterpriseId") String enterpriseId,
                                        @Param("storeId") String storeId,
                                        @Param("metaTableId") Long metaTableId);


    List<TbDataTableDO> getTbDataTableListByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds") List<Long> unifyTaskIds);


    int copyDataTable(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbDataTableDO> tbDataTableDOList);


    void deleteByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 更新签退签到时间
     */
    int updateSignStartEndTime(@Param("enterpriseId") String enterpriseId, @Param("signStartTime") Date signStartTime, @Param("signEndTime") Date signEndTime,
                               @Param("businessId") Long businessId, @Param("businessType") String businessType);

    // 算分时统计巡店记录上的信息
    TbPatrolStoreRecordStatisticsDTO statisticsWhenCountScore(@Param("enterpriseId") String enterpriseId,
                                                              @Param("businessId") Long businessId,
                                                              @Param("businessType") String businessType);

    /**
     * 计算得分率
     * @param enterpriseId
     * @param businessId
     * @param businessType
     * @return
     */
    TbPatrolStoreRecordStatisticsDTO getScoreRateByBusinessId(@Param("enterpriseId") String enterpriseId,
                                                              @Param("businessId") Long businessId,
                                                              @Param("businessType") String businessType);


    List<PatrolStoreStatisticsUserDTO> getListByMetaTableIdListAndTimeGroupBy(@Param("enterpriseId") String enterpriseId,
                                                                              @Param("metaTableIdList") List<Long> tableIdList,
                                                                              @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 运营看板方案汇总信息
     */
    TableBoardStatisticDTO tableBoardStatistics(@Param("enterpriseId") String enterpriseId,
                                                @Param("metaTableIds") List<Long> metaTableIds, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 运营看板方案排名
     */
    List<TableBoardRankDTO> tableBoardRank(@Param("enterpriseId") String enterpriseId,
                                           @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("metaTableIds") List<Long> metaTableIds);

    /**
     * 运营看板方案趋势
     */
    List<TableBoardTrendDTO> tableBoardTrend(@Param("enterpriseId") String enterpriseId,
                                             @Param("query") TableBoardQuery query);

    /**
     * 根据检查表模板id统计
     */
    List<PatrolStoreStatisticsMetaTableDTO> patrolStoreStatisticsMetaTable(@Param("enterpriseId") String enterpriseId,
                                                                           @Param("list") List<Long> metaTableIds, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

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

    List<Long> selectPatrolStoreByCondition(@Param("enterpriseId") String enterpriseId,
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

    /**
     * 人员执行力报表
     */
    List<SafetyCheckUserDTO> statisticsSafetyCheckUser(@Param("enterpriseId") String enterpriseId,
                                                            @Param("userIdList") List<String> userIdList, @Param("beginDate") String beginDate,
                                                            @Param("endDate") String endDate);

    List<SafetyCheckScoreUserDTO> statisticsSafetyCheckUserScore(@Param("enterpriseId") String enterpriseId,
                                                            @Param("userIdList") List<String> userIdList, @Param("beginDate") String beginDate,
                                                            @Param("endDate") String endDate);

    TbDataTableDO getTableInfo(@Param("enterpriseId") String enterpriseId,@Param("metaTableId") Long metaTableId,@Param("businessId") Long businessId);


    List<TbDataTableDO> getTableInfoByList(@Param("enterpriseId")String enterpriseId,
                                           @Param("businessId")Long businessId,
                                           @Param("metaTableIds")ArrayList<Long> metaTableIds
                                           );

    List<TbDataTableDO> getTableListByBusinessId(@Param("enterpriseId")String enterpriseId,@Param("businessIds") List<Long> businessIds);

    List<TbDataTableDO> getTableListByBusinessIds(@Param("enterpriseId")String enterpriseId,@Param("businessIds") List<Long> businessIds,@Param("metaTableIds")List<Long> tableIds);

    /**
     * 根据id批量修改
     * @param enterpriseId 企业id
     * @param records 实体列表
     */
    void updateBatchById(@Param("enterpriseId") String enterpriseId, @Param("records") List<TbDataTableDO> records);
}
