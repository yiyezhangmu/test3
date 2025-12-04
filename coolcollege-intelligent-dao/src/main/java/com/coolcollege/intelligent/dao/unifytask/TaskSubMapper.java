package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.enterprise.dto.ApproveDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonNodeNoDTO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubInfoVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 16:20
 */
@Mapper
public interface TaskSubMapper {

    /**
     * 批量新增
     *
     * @param enterpriseId
     *            企业ID
     * @param saveList
     *            saveList
     */
    Integer batchInsertTaskSub(@Param("enterpriseId") String enterpriseId, @Param("list") List<TaskSubDO> saveList);

    /**
     * 新增
     *
     * @param enterpriseId 企业ID
     * @param taskSubDO
     */
    void insertTaskSub(@Param("enterpriseId") String enterpriseId, @Param("item") TaskSubDO taskSubDO);

    /**
     * 查询我的子任务列表
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    List<TaskSubVO> selectSubTaskDataNew(@Param("enterpriseId") String enterpriseId, @Param("query") DisplayQuery query);

    /**
     * 查询我的子任务详情
     *
     * @param enterpriseId
     * @param id
     * @return
     */
    TaskSubVO selectSubTaskDetailByIdNew(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);
    /**
     * 查询我的子任务列表-已完成/全部
     *
     * @param enterpriseId
     * @param query
     * @param storeList
     * @param selectType
     *            :all complete
     * @return
     */
    List<TaskSubVO> selectAllOrEndSubTaskDataNew(@Param("enterpriseId") String enterpriseId, @Param("query") DisplayQuery query,
                                              @Param("list") List<String> storeList, @Param("selectType") String selectType);

    List<TaskSubVO> selectBySubTaskCodeLoopCounts(@Param("enterpriseId") String enterpriseId, @Param("subTaskCodes") List<String> subTaskCodes, @Param("loopCounts")List<Long> loopCounts);
    /**
     * 本人有关子任务统计（全部/完成除外）---基于门店
     *
     * @param enterpriseId
     * @param taskId
     * @param userId
     * @param queryType
     * @param storeList
     * @return
     */
    Integer selectDisplaySubStatistics(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId,
                                       @Param("userId") String userId, @Param("queryType") String queryType, @Param("loopCount") Long loopCount, @Param("list") List<String> storeList);

    /**
     * 查询全部子任务数量(根据门店id统计)
     *
     * @param enterpriseId
     * @param taskId
     * @param storeList
     * @return
     */
    Integer selectDisplayAllSubStatistics(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId, @Param("loopCount") Long loopCount,
        @Param("list") List<String> storeList);


    /**
     * 通过父任务id获取子任务详情
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<TaskSubDO> getTaskSubDOList(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    List<TaskSubDO> getTaskSubDOListByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);



    /**
     * 通过父任务id获取子任务详情
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<TaskSubDO> getTaskSubDOListForSend(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                            @Param("storeId") String storeId, @Param("loopCount") Long loopCount,
                                            @Param("nodeNo") String nodeNo);


    /**
     * 通过子任务id获取子任务详情
     *
     * @param enterpriseId
     * @param taskSubId
     * @return
     */
    TaskSubDO getTaskSubDOListById(@Param("enterpriseId") String enterpriseId, @Param("taskSubId") Long taskSubId);

    TaskSubDO getSimpleTaskSubDOListById(@Param("enterpriseId") String enterpriseId, @Param("taskSubId") Long taskSubId);

    /**
     * 通过子任务id批量获取子任务详情
     *
     * @param enterpriseId
     * @param taskSubIdList
     * @return
     */
    List<TaskSubDO> getDOByIdList(@Param("enterpriseId") String enterpriseId,
        @Param("taskSubIdList") List<Long> taskSubIdList);

    /**
     * 通过子任务id批量获取子任务详情
     *
     * @param enterpriseId
     * @param taskSubIdList
     * @return
     */
    List<TaskSubDO> getDOByIdListForMap(@Param("enterpriseId") String enterpriseId,
                                        @Param("taskSubIdList") List<Long> taskSubIdList);

    /***
     * 修改除自己以外的其他同批次节点的状态
     * @param enterpriseId
     * @param queryDO
     * @param taskSubDO
     */
    void updateSubDetailExclude(@Param("enterpriseId") String enterpriseId, @Param("queryDO") TaskSubDO queryDO,
                                @Param("taskSubDO") TaskSubDO taskSubDO, @Param("subTaskId") Long subTaskId);

    void updateSubStatusComplete(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId
            , @Param("storeId") String storeId, @Param("loopCount") Long loopCount);

    List<TaskSubDO> selectSubDetailExclude(@Param("enterpriseId") String enterpriseId, @Param("queryDO") TaskSubDO queryDO,
                                @Param("taskSubDO") TaskSubDO taskSubDO, @Param("subTaskId") Long subTaskId);

    Integer updateSubDetailExcludeById(@Param("enterpriseId") String enterpriseId, @Param("list")List<TaskSubDO> list);

    /**
     * 通过id修改子任务
     *
     * @param enterpriseId
     * @param taskSubDO
     */
    void updateSubDetailById(@Param("enterpriseId") String enterpriseId, @Param("taskSubDO") TaskSubDO taskSubDO);

    /**
     * 主键查详情
     *
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    TaskSubDO selectSubTaskById(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId);

    /**
     * 主键查详情
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    TaskSubDO selectSubTaskByTaskIdAndStoreId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId
            , @Param("storeId") String storeId, @Param("loopCount") Long loopCount, @Param("userId") String userId,
            @Param("nodeNo") String nodeNo);

    /**
     * 批量查询父任务所有子任务id
     *
     * @param enterpriseId
     * @param unifyTaskIds
     * @return
     */
    List<TaskSubReportVO> selectSubTaskByParentIdBatch(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> unifyTaskIds);

    /**
     * 批量查询父任务所有子任务id
     *
     * @param enterpriseId
     * @param unifyTaskIds
     * @return
     */
    List<TaskSubVO> selectSubTaskByTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> unifyTaskIds);

    /**
     * 批量查询父任务所有子任务id
     *
     * @param enterpriseId
     * @param taskSubIdList
     * @return
     */
    List<TaskSubVO> selectSubQuestionTaskByTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> taskSubIdList);


    /**
     * 批量查询父任务所有子任务id
     *
     * @param enterpriseId
     * @param unifyTaskIds
     * @return
     */
    List<Long> selectSubQuestionTaskIdByTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> unifyTaskIds);

    /**
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    @Deprecated
    List<TaskSubVO> selectSubTaskByTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    List<TaskSubVO> selectSubTaskByTaskIdAndStoreIdAndLoop(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId, @Param("storeId") String storeId,
                                          @Param("loopCount") Long loopCount);

    /**
     * 子任务id查详情-含无门店名称
     *
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    TaskSubInfoVO selectTaskBySubId(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId);

    /**
     * 删除子任务根据taskId
     *
     * @param enterpriseId
     * @param unifyTaskId
     */
    void delSubTaskByTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 删除子任务根据taskId
     *
     * @param enterpriseId
     * @param unifyTaskId
     */
    void delSubTaskByTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                                  @Param("storeId") String storeId,  @Param("loopCount") Long loopCount);


    void batchDelSubTaskByTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskStoreList")List<TaskStoreDO> taskStoreList);

    List<TaskSubVO> selectSubTaskByTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("taskStoreList")List<TaskStoreDO> taskStoreList);

    /**
     * 根据父任务id和门店id删除子任务
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     */
    void delSubTaskByTaskIdAndStoreId(@Param("enterpriseId") String enterpriseId,
        @Param("unifyTaskId") Long unifyTaskId, @Param("storeId") String storeId);

    /**
     * 获取父任务下完成的门店
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<String> selectEndStoreByTaskId(@Param("enterpriseId") String enterpriseId,
        @Param("unifyTaskId") Long unifyTaskId);

    List<String> selectAllStoreByTaskId(@Param("enterpriseId") String enterpriseId,
                                        @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 通过父任务id获取子任务idList
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<Long> getSubIdListByTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 获取未处理的人
     *
     * @param enterpriseId
     * @param taskList
     * @param loopCount
     * @return
     */
    List<UnifyParentUser> selectUnCompleteUser(@Param("enterpriseId") String enterpriseId, @Param("taskList") List<Long> taskList,
        @Param("node") String node, @Param("groupItem") Long groupItem, @Param("loopCount") Long loopCount,
        @Param("storeId") String storeId,                                            @Param("overdueTask") Boolean overdueTask,
                                               @Param("handlerOvertimeTaskContinue") Boolean handlerOvertimeTaskContinue,
                                               @Param("approverOvertimeTaskContinue") Boolean approverOvertimeTaskContinue);

    /**
     * 查询历史
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @return
     */
    List<UnifySubHistoryDTO> selectSubTaskHistoryByTaskId(@Param("enterpriseId") String enterpriseId,
        @Param("unifyTaskId") Long unifyTaskId, @Param("storeId") String storeId);

    /**
     * 通过父任务idList获取子任务详情
     *
     * @param enterpriseId
     * @param unifyTaskIdList
     * @return
     */
    List<TaskSubVO> getByTaskIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> unifyTaskIdList);

    /**
     * 获取企业下的所有子任务
     *
     * @param enterpriseId
     * @return
     */
    List<TaskSubVO> getByEnterpriseId(@Param("enterpriseId") String enterpriseId);

    /**
     * 通过父任务id获取子任务列表
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    List<TaskSubVO> getByTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    List<TaskSubVO> getByTaskIdLimit1(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 查询全表数量
     *
     * @param enterpriseId
     */
    Long selectCount(@Param("enterpriseId") String enterpriseId);

    /**
     * 根据子任务标识码获取子任务列表
     *
     * @param enterpriseId
     * @param strings
     * @return
     */
    List<TaskSubVO> selectSubTaskBySubTaskCodes(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<String> strings);

    /**
     * 暂时使用-初始化
     *
     * @param enterpriseId
     */
    List<TaskSubVO> selectAllInfo(@Param("enterpriseId") String enterpriseId);

    /**
     * 暂时使用-初始化
     *
     * @param enterpriseId
     * @param taskList
     */
    void batchUpdateTaskTime(@Param("enterpriseId") String enterpriseId,
        @Param("list") List<TaskSubVO> taskList);

    /**
     * 根据父任务获取完成的子任务
     */
    List<TaskSubDO> selectDoneByTaskIds(@Param("enterpriseId") String enterpriseId,
        @Param("taskIdList") List<Long> taskIdList);

    /**
     * 运营数据统计接口
     *
     * @param enterpriseId
     * @param storeId
     * @param taskType
     * @param status
     * @return
     */
    List<TaskSubVO> selectTaskNum(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId,
                                  @Param("taskType") String taskType, @Param("status") String status);

    /**
     * 查询或签的子任务ids
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param nodeNo
     * @param groupItem
     * @return
     */
    List<Long> selectOrSubTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
        @Param("storeId") String storeId, @Param("nodeNo") String nodeNo, @Param("groupItem") Long groupItem,
        @Param("loopCount") Long loopCount);

    /**
     * 查询这个批次的处理任务
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param nodeNo
     * @param loopCount
     * @return
     */
    List<String> selectUserIdByLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                  @Param("storeId") String storeId, @Param("nodeNo") String nodeNo,
                                  @Param("loopCount") Long loopCount);


    /**
     * 查询这个批次的处理任务
     * @param enterpriseId
     * @param unifyTaskId
     * @param loopCount
     * @return
     */
    List<PersonNodeNoDTO> selectUserIdByLoopCountAndStoreIdList(@Param("enterpriseId") String enterpriseId,
                                                                @Param("unifyTaskId") Long unifyTaskId,
                                                                @Param("storeIdList") List<String> storeIdList,
                                                                @Param("loopCount") Long loopCount,
                                                                @Param("nodeNo") String nodeNo);

    List<PersonNodeNoDTO> selectUserIdByLoopCountAndStoreIdListAndUnifyTaskList(@Param("enterpriseId") String enterpriseId,
                                                                @Param("unifyTaskIds") List<String> unifyTaskIds,
                                                                @Param("storeIdList") List<String> storeIdList,
                                                                @Param("loopCount") Long loopCount,
                                                                @Param("nodeNo") String nodeNo);

    /**
     * 查询最新的一条
     *
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    TaskSubVO getLatestSubId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                             @Param("storeId") String storeId, @Param("loopCount") Long loopCount, @Param("userId") String userId,
                             @Param("subStatus") String subStatus, @Param("nodeNo") String nodeNo);

    TaskSubDO getByTbDisplayTableRecordInfo(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId
                            ,@Param("storeId") String storeId, @Param("loopCount") Long loopCount, @Param("userId") String userId
                            , @Param("nodeNo") String nodeNo);

    TaskSubDO getSubBeginTimeEndTimeByTaskIdAndLoopCount(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId, @Param("loopCount") Long loopCount);

    void updateHandleTimeByParentId(@Param("enterpriseId") String enterpriseId, @Param("endTime") Long endTime,@Param("taskId") Long taskId);

    Integer countByTaskIdAndStoreId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                    @Param("storeId") String storeId, @Param("loopCount") Long loopCount);


    /**
     * 通过子任务id获取子任务详情
     *
     * @param enterpriseId
     * @param taskSubId
     * @return
     */
    TaskSubDO getTaskSubDOById(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long taskSubId);

    /**
     * 根据门店id与父任务Id查询数据（节点为endNode的数据）
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @return
     */
    List<ApproveDTO> taskSubList(String enterpriseId, String taskId, String storeId);

    List<Long> getRemoveSubTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                             @Param("storeId") String storeId, @Param("loopCount") Long loopCount, @Param("removeUserIdList") List<String> removeUserIdList,
                             @Param("subStatus") String subStatus, @Param("nodeNo") String nodeNo);

    void updateNeedRemoveSubTaskStatus(@Param("enterpriseId") String enterpriseId, @Param("subTaskIdList") List<Long> subTaskIdList, @Param("handleTime") Long handleTime, @Param("subStatus") String subStatus,
                                       @Param("actionKey") String actionKey, @Param("flowState") String flowState);

    TaskSubVO getCompleteSubTaskByReallocate(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                             @Param("storeId") String storeId, @Param("loopCount") Long loopCount, @Param("userId") String userId,
                             @Param("subStatus") String subStatus, @Param("actionKey") String actionKey,@Param("nodeNo") String nodeNo);

    /**
     * 查询处理人已完成的任务
     * @param enterpriseId 企业id
     * @param storeId 门店id
     * @param taskType 任务类型
     * @param unifyTaskId 父任务id
     * @return TaskSubDO
     */
    TaskSubDO selectHandlerCompletedSubTask(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId, @Param("taskType") String taskType, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据父任务获得子任务id
     * @param enterpriseId
     * @param unifyTaskId
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/10 11:19
     */
    List<Long> getSubTaskIdListByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 根据用户和状态查询子任务id
     * @param enterpriseId
     * @param userId
     * @param subStatus
     * @return
     */
    List<Long> getSubTaskIdsByUserIdAndStatus(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId, @Param("subStatus") String subStatus);

    void updateSubStatusBySubTaskId(@Param("enterpriseId") String enterpriseId, @Param("subStatus") String subStatus,@Param("subTaskId") Long subTaskId);


    /**
     * 获取待处理的用户
     * @param enterpriseId
     * @param unifyTaskId
     * @param storeId
     * @param loopCount
     * @return
     */
    List<String> getPendingUserByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                             @Param("storeId") String storeId, @Param("loopCount") Long loopCount);

    /**
     * 获取待处理的用户
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    UnifySuToDoDTO getCountByUnifyTaskId(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                         @Param("handleUserId") String handleUserId);


    List<TaskSubDO> getUnFinishHandleUserIds(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    String getTaskBySubTaskId(@Param("enterpriseId") String enterpriseId,
                              @Param("subTaskId") String subTaskId);

    void updateSubStatusByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);


    List<TaskSubDO> listCombineStore(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskId") Long unifyTaskId,
                                     @Param("storeIdList") List<String> storeIdList, @Param("loopCount") Long loopCount,
                                     @Param("handleUserId") String handleUserId, @Param("nodeNo") String nodeNo);

    void updateOperateOverdue(@Param("enterpriseId") String enterpriseId,
                              @Param("unifyTaskId") Long id,
                              @Param("isOperateOverdue") int isOperateOverdue);

    Boolean getIsOperateOverdueByStoreIdAndTaskId(@Param("enterpriseId") String enterpriseId,
                                                  @Param("taskId")Long taskId,
                                                  @Param("storeId")String storeId);

    /**
     * 过滤存在其他门店子任务的用户
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param storeId 门店id
     * @param loopCount 轮次
     * @param userIds 用户id
     * @return 用户id
     */
    List<String> filterExistOtherStoreSubTask(@Param("enterpriseId") String enterpriseId,
                                              @Param("unifyTaskId") Long unifyTaskId,
                                              @Param("storeId") String storeId,
                                              @Param("loopCount") Long loopCount,
                                              @Param("userIds") List<String> userIds);
}
