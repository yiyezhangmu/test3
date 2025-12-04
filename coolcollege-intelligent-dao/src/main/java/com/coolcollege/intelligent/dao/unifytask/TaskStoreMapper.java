package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsProblemRankDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsRegionDTO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskFinishStorePageRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskFinishStoreVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskStoreStageVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * @author byd
 */
@Mapper
public interface TaskStoreMapper {

    /**
     * 批量新增
     *
     * @param enterpriseId 企业ID
     * @param saveList     saveList
     */
    void batchInsertTaskStore(@Param("enterpriseId") String enterpriseId, @Param("list") List<TaskStoreDO> saveList);

    /**
     * 批量插入  更新
     * @param enterpriseId
     * @param storeTaskList
     */
    Integer batchAddTaskStore(@Param("enterpriseId") String enterpriseId, @Param("storeTaskList") List<TaskStoreDO> storeTaskList);



    /**
     * 查询
     *
     * @param enterpriseId
     * @param id
     * @return
     */
    TaskStoreDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 查询列表
     * @param enterpriseId 企业id
     * @param query 查询条件
     * @return
     */
    List<TaskStoreDO> selectStoreClearList(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreQuery query);


    /**
     * 查询逾期可执行列表
     * @param enterpriseId 企业id
     * @param query 查询条件
     * @return
     */
    List<TaskStoreDO> selectOverdueStoreClearList(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreQuery query);

    List<TaskNumDTO> selectTaskStatusNum(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreQuery query);

    /**
     * 查询
     *
     * @return
     */
    TaskStoreDO getTaskStore(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                             @Param("storeId") String storeId, @Param("loopCount") Long loopCount);


    /**
     * 查询
     *
     * @return
     */
    TaskStoreDO getTaskQuestionStore(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                     @Param("storeId") String storeId, @Param("loopCount") Long loopCount);

    /**
     * 新增
     *
     * @param enterpriseId 企业ID
     * @param taskSubDO
     */
    Long updateByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("item") TaskStoreDO taskSubDO);

    /**
     * 删除任务
     * @param enterpriseId 企业ID
     * @param unifyTaskId 父任务id
     * @return
     */
    Integer delTaskStoreByParentTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 删除任务
     * @param enterpriseId 企业ID
     * @param id
     * @return
     */
    int delTaskStoreById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<TaskStoreDO> listByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    List<TaskStoreDO> listByStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                        @Param("storeIdList") List<String> storeIdList, @Param("loopCount") Long loopCount);

    /**
     * 统计父任务中间页 数据
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<TaskStoreDO> selectTbDisplayParentStatistics(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    List<String> listStoreIdByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    PatrolStoreStatisticsRegionDTO patrolStoreStatisticsRegionColumn(@Param("enterpriseId") String eid, @Param("regionPath") String regionPath, @Param("isRoot") boolean isRoot, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("storeIds") List<String> storeIds);

    List<PatrolStoreStatisticsProblemRankDTO> regionQuestionNumRank(@Param("enterpriseId") String enterpriseId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("regionPath") String regionPath);

    List<TaskStoreDO> getTaskStoreByRegionPathOrStoreId(@Param("enterpriseId") String enterpriseId,
                                                        @Param("regionPath") String regionPath,
                                                        @Param("storeIdList") List<String> storeIdList,
                                                        @Param("beginDate") Date beginDate,
                                                        @Param("endDate") Date endDate,
                                                        @Param("getDirectStore") Boolean getDirectStore,
                                                        @Param("nodeNo") String nodeNo,
                                                        @Param("regionId") String regionId,
                                                        @Param("questionType") String questionType,
                                                        @Param("metaTableId") Long metaTableId,
                                                        @Param("metaColumnIds") List<Long> metaColumnIds,
                                                        @Param("isOverDue") Boolean isOverDue);

    List<TaskStoreDO> listByUnifyTaskIds(@Param("enterpriseId") String enterpriseId,
                                         @Param("taskIdList") List<Long> taskIdList,
                                         @Param("nodeNo") String nodeNo);

    List<UnifyTaskStoreCount> selectTaskCount(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);

    UnifyTaskStoreCount selectTaskStoreCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId, @Param("loopCount") Long loopCount);
    UnifyTaskStoreCount selectTaskStoreCountByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("taskIds") List<String> taskIds, @Param("loopCount") Long loopCount);

    /**
     * 陈列任务数据列表
     * @param enterpriseId
     * @param taskId
     * @param loopCount
     * @return
     */
    UnifySubStatisticsDTO selectDisplayTaskCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId, @Param("loopCount") Long loopCount);


    Long selectTaskStageCount(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId,
                                 @Param("status")String status);

    List<TaskStoreDO> taskStoreList(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreLoopQuery query);

    List<TaskStoreDO> taskStoreListExistUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreLoopQuery query);

    List<TaskStoreDO> taskStoreAllList(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreLoopQuery query);

    List<TaskStoreStageVO> taskStageList(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId")Long unifyTaskId,
                                         @Param("status")String status);

    List<TaskReportVO> statisticsTaskNumByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);

    /**
     * 获取任务状态门店
     * @param enterpriseId
     * @param query
     * @return
     */
    Page<TaskFinishStoreVO> getTaskFinishStorePage(@Param("enterpriseId")String enterpriseId, @Param("query") TaskFinishStorePageRequest query);

    // pc端 子任务列表 全部
    List<TaskStoreDO> selectForSubTaskListByTaskId(@Param("enterpriseId") String enterpriseId, @Param("query") DisplayQuery query,
                                                  @Param("list") List<String> storeList, @Param("selectType") String selectType);

    // 我创建的
    List<TaskStoreDO> listMyCreateTask(@Param("enterpriseId") String enterpriseId, @Param("createUserId") String createUserId
            , @Param("storeIdList") List<String> storeIdList);

    /**
     * 根据taskType查询我创建的
     * @param enterpriseId
     * @param createUserId
     * @param storeIdList
     * @return
     */
    List<TaskStoreDO> listMyCreateTaskByTaskTypes(@Param("enterpriseId") String enterpriseId, @Param("createUserId") String createUserId,
                                                  @Param("storeIdList") List<String> storeIdList, @Param("taskTypes") List<String> taskTypes);

    /**
     * 根据门店任务id更新 门店任务节点信息
     * @param enterpriseId
     * @param id
     * @param extendInfo
     * @return
     */
    Long updateExtendAndCcInfoByTaskStoreId(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("extendInfo") String extendInfo, @Param("ccUserIds") String ccUserIds, @Param("originalExtendInfo") String originalExtendInfo);

    Long updatedCcInfoByTaskStoreId(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,  @Param("ccUserIds") String ccUserIds);

    Long updatedHandlerUserByTaskStoreId(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("userId") String userId);

    /**
     * 分页订正  门店任务节点人员
     * @param enterpriseId
     * @return
     */
    List<TaskStoreDO> listTaskStoreByEid(@Param("enterpriseId") String enterpriseId, @Param("isRunIncrement") Boolean isRunIncrement, @Param("maxId") Long maxId);

    List<UnifyStoreDTO> selectStoreByTaskIds(@Param("enterpriseId") String enterpriseId, @Param("taskIdList") List<Long> taskIdList);

    /**
     * 根据任务id、门店id集合 查找门店任务
     * @param enterpriseId
     * @param taskIdList
     * @param storeIdList
     * @return
     */
    List<TaskStoreDO> listByTaskIdAndStoreIdList(@Param("enterpriseId") String enterpriseId,
                                                 @Param("taskIdList") List<Long> taskIdList,
                                                 @Param("storeIdList") List<String> storeIdList,
                                                 @Param("loopCount") Long loopCount);

    List<TaskStoreDO> selectExtendAndCcInfoByTaskStoreIds(@Param("enterpriseId") String enterpriseId, @Param("taskStoreIdList") List<Long> taskStoreIdList);


    /**
     * 根据父任务id和时间查询门店任务数量
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param startTime 创建时间开始
     * @param endTime 创建时间结束
     * @return 门店任务数量
     */
    Integer countByUnifyTaskIdAndTime(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId, @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime);

    /**
     * 获得门店的最新工单
     * @param enterpriseId
     * @param storeId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.unifytask.TaskStoreDO
     * @date: 2022/3/7 15:39
     */
    TaskStoreDO selectLastStoreQuestion(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId);

    /**
     * 获取某个区域 某段时间的门店任务数据
     * @param enterpriseId
     * @param unifyTaskId
     * @param beginTime
     * @param endTime
     * @return
     */
    List<TaskStoreDO> getStoreTaskByUnifyTaskId(@Param("enterpriseId")String enterpriseId, @Param("unifyTaskId")Long unifyTaskId, @Param("beginTime")String beginTime, @Param("endTime")String endTime);


    void copyTaskStore(@Param("enterpriseId") String enterpriseId, @Param("list") List<TaskStoreDO> saveList);


    List<TaskStoreDO> listByUnifyIds(@Param("enterpriseId") String enterpriseId,
                                         @Param("idList") List<Long> idList, @Param("nodeNo") String nodeNo);

   int getCcUserCountByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                   @Param("ccUserId") String ccUserId);

    /**
     * 查询列表
     * @param enterpriseId 企业id
     * @return
     */
    List<TaskStoreDO> selectTaskStoreListByStoreId(@Param("enterpriseId") String enterpriseId,@Param("storeId") String storeId, @Param("beginTime") String beginTime,
                                                   @Param("endTime") String endTime);

    List<TaskStoreDO> selectByUnifyTaskId(@Param("enterpriseId") String enterpriseId,
                                   @Param("parentTaskId") Long parentTaskId);

    void deleteTaskStoreById(@Param("enterpriseId") String enterpriseId,
                             @Param("taskStoreId") Long taskStoreId);

    void deleteTaskStoreByIds(@Param("enterpriseId") String enterpriseId,
                             @Param("taskStoreIds") List<Long> taskStoreIds);

    List<TaskStoreDO> taskStoreListByIdList(@Param("enterpriseId") String enterpriseId, @Param("taskStoreIdList") List<Long> taskStoreIdList);

    /**
     * 陈列任务查询
     * @param enterpriseId 企业id
     * @param unifyTaskIds 父任务id列表
     * @param ids 任务id列表
     * @return java.util.List<com.coolcollege.intelligent.model.unifytask.TaskStoreDO>
     */
    List<TaskStoreDO> selectDisplayTaskStore(
            @Param("enterpriseId") String enterpriseId,
            @Param("unifyTaskIds") List<Long> unifyTaskIds,
            @Param("ids") List<Long> ids,
            @Param("returnLimit") Integer returnLimit
    );

    /**
     * 根据门店任务id更新 门店任务节点信息 清空处理人
     * @param enterpriseId 企业id
     * @param id 门店任务id
     * @param extendInfo 门店任务扩展信息
     * @param ccUserIds 抄送人id集合
     * @param clearHandlerUser 是否清空处理人
     */
    void updateExtendAndCcInfoAndClearHandlerUser(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("extendInfo") String extendInfo, @Param("ccUserIds") String ccUserIds, @Param("clearHandlerUser") boolean clearHandlerUser);

    UnifySubStatisticsDTO getDisplayTaskDBCount(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreLoopQuery query);

    List<TaskStoreDO> selectDisplayTaskStoreDBList(@Param("enterpriseId") String enterpriseId, @Param("query") TaskStoreLoopQuery query);
}
