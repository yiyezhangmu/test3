package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.dto.CommissionTotalDTO;
import com.coolcollege.intelligent.model.unifytask.query.TaskAgencyQuery;
import com.coolcollege.intelligent.model.unifytask.vo.PatrolPlanVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskAgencyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/12/10 21:22
 */
@Mapper
public interface AgencyMapper {

    // 查询我的代办-待处理  只查子任务     新方法
    List<TaskAgencyVO> selectAgencyPendingListNew(@Param("enterpriseId") String enterpriseId,
                                                  @Param("handleUserId") String handleUserId,
                                                  @Param("overdueTask") Boolean overdueTask,
                                                  @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                                  @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                                  @Param("storeId") String storeId,
                                                  @Param("taskType") String taskType,
                                                  @Param("storeIdList") List<String> storeIdList);

    /**
     * 查询待办
     * @param enterpriseId
     * @param handleUserId
     * @param overdueTask
     * @param handlerOvertimeTaskContinue
     * @param approverOvertimeTaskContinue
     * @param storeId
     * @param taskTypes
     * @param storeIdList
     * @return
     */
    List<TaskAgencyVO> selectTodoTaskList(@Param("enterpriseId") String enterpriseId,
                                          @Param("handleUserId") String handleUserId,
                                          @Param("overdueTask") Boolean overdueTask,
                                          @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                          @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                          @Param("storeId") String storeId,
                                          @Param("taskTypes") List<String> taskTypes,
                                          @Param("storeIdList") List<String> storeIdList);

    List<TaskAgencyVO> selectNewTodoTaskList(@Param("enterpriseId") String enterpriseId,
                                          @Param("handleUserId") String handleUserId,
                                           @Param("overdueTask") Boolean overdueTask,
                                          @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                          @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                          @Param("storeId") String storeId,
                                          @Param("taskTypes") List<String> taskTypes,
                                          @Param("storeIdList") List<String> storeIdList);


    List<TaskAgencyVO> selectBeforeTimeNewTodoTaskList(@Param("enterpriseId") String enterpriseId,
                                          @Param("handleUserId") String handleUserId,
                                          @Param("overdueTask")Boolean overdueTaskContinue,
                                          @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                          @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                          @Param("storeId") String storeId,
                                          @Param("taskTypes") List<String> taskTypes,
                                          @Param("storeIdList") List<String> storeIdList);

    /**
     * 分页查门店任务表  抄送我的会用到 多个类型查询
     * @param enterpriseId
     * @param ccUserId
     * @param storeId
     * @param taskTypes
     * @param createTime
     * @param storeIdList
     * @return
     */
    List<TaskStoreDO> selectCreateOrCCTodoTaskList(@Param("enterpriseId") String enterpriseId,
                                                   @Param("ccUserId") String ccUserId,
                                                   @Param("storeId") String storeId,
                                                   @Param("taskTypes") List<String> taskTypes,
                                                   @Param("createTime") Date createTime,
                                                   @Param("storeIdList") List<String> storeIdList);
    /**
     * 根据父任务id获得进行中的子任务
     * @param enterpriseId
     * @param handleUserId
     * @param overdueTask
     * @param handlerOvertimeTaskContinue
     * @param approverOvertimeTaskContinue
     * @param unifyTaskId
     * @return
     */
    List<TaskAgencyVO> selectUnifyTaskPendingSub(@Param("enterpriseId") String enterpriseId,
                                                 @Param("handleUserId") String handleUserId,
                                                 @Param("overdueTask") Boolean overdueTask,
                                                 @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                                 @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                                 @Param("unifyTaskId") Long unifyTaskId,
                                                 @Param("loopCount") Long loopCount);

    /**
     * 查询我的代办-我创建/抄送
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    List<TaskAgencyVO> selectAgencyCreateOrCCList(@Param("enterpriseId") String enterpriseId, @Param("list")  List<Long> taskIds,
                                                  @Param("storeId") String storeId, @Param("taskType") String taskType);

    // 分页查门店任务表  抄送我的会用到
    List<TaskStoreDO> selectAgencyCreateOrCCTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("ccUserId") String ccUserId,
                                                       @Param("storeId") String storeId, @Param("taskType") String taskType, @Param("createTime") Date createTime,
                                                       @Param("storeIdList") List<String> storeIdList);
    // 抄送我的  和  我创建的用到   subTaskCodeLoopCounts
    List<TaskAgencyVO> selectAgencyCreateOrCCListByTaskIds(@Param("enterpriseId") String enterpriseId,
                                                           @Param("list")  List<String> subTaskCodeLoopCounts,
                                                           @Param("subTaskCodes")  List<String> subTaskCodes);

    /**
     * 查询我的代办-全部
     * @param enterpriseId
     * @param query
     * @param taskIds
     * @param storeIdList 门店列表
     * @return
     */
    List<TaskAgencyVO> selectAgencyAllListNew(@Param("enterpriseId") String enterpriseId, @Param("query") TaskAgencyQuery query, @Param("list")  List<Long> taskIds
                    , @Param("storeIdList") List<String> storeIdList);


    /**
     * 查询用户含待处理的任务的门店列表
     * @param enterpriseId
     * @param userId
     * @param overdueTaskContinue
     * @param taskType
     * @return
     */
    List<String> selectStoreAgencyTaskPendingList(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,
                                                  @Param("overdueTask") Boolean overdueTaskContinue, @Param("taskType") String taskType);


    /**
     * 查询待办
     * @param enterpriseId
     * @param handleUserId
     * @param overdueTask
     * @param handlerOvertimeTaskContinue
     * @param approverOvertimeTaskContinue
     * @param storeId
     * @param taskTypes
     * @param storeIdList
     * @return
     */
    CommissionTotalDTO selectTodoTaskListCount(@Param("enterpriseId") String enterpriseId,
                                               @Param("handleUserId") String handleUserId,
                                               @Param("overdueTask") Boolean overdueTask,
                                               @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                               @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue,
                                               @Param("storeId") String storeId,
                                               @Param("taskTypes") List<String> taskTypes,
                                               @Param("storeIdList") List<String> storeIdList);


    /**
     * 查询指定类型的待办 支持多种类型同时查询
     * @param enterpriseId
     * @param handleUserId
     * @param taskTypes
     * @return
     */
    List<PatrolPlanVO> getPatrolPlanList(@Param("enterpriseId") String enterpriseId,
                                         @Param("handleUserId") String handleUserId,
                                         @Param("taskTypes") List<String> taskTypes,
                                         @Param("completeFlag") Integer completeFlag);
}
