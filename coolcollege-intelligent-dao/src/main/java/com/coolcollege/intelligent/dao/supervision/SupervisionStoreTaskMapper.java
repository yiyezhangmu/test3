package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionStoreDataDTO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionStoreTaskBasicDataDTO;
import com.coolcollege.intelligent.model.supervision.request.SupervisionStoreTaskQueryRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-02-27 03:03
 */
public interface SupervisionStoreTaskMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-02-27 03:03
     */
    int insertSelective(@Param("record") SupervisionStoreTaskDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-27 03:03
     */
    SupervisionStoreTaskDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-27 03:03
     */
    int updateByPrimaryKeySelective(SupervisionStoreTaskDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-27 03:03
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 门店任务取消
     * @param enterpriseId
     * @param supervisionTaskId
     * @param id
     * @return
     */
    int storeTaskCancel(@Param("enterpriseId") String enterpriseId,
                        @Param("taskParentId") Long taskParentId,
                        @Param("supervisionTaskId") Long supervisionTaskId,
                        @Param("id") Long id);

    /**
     * 未取消的门店数量
     * @param enterpriseId
     * @param supervisionTaskId
     * @return
     */
    Integer notCancelCountByParentId(@Param("enterpriseId") String enterpriseId,
                                     @Param("supervisionTaskId") Long supervisionTaskId);


    /**
     * 门店任务删除
     * @param enterpriseId
     * @param parentId
     * @return
     */
    int storeTaskDel(@Param("enterpriseId") String enterpriseId, @Param("parentId") Long parentId);

    List<SupervisionStoreTaskDO> listByParentId(@Param("enterpriseId") String enterpriseId,
                                                @Param("parentId") Long parentId,
                                                @Param("supervisionTaskId") Long supervisionTaskId,
                                                @Param("storeIds") List<String> storeIds,
                                                @Param("regionIds") List<String> regionIds,
                                                @Param("userName") String userName,
                                                @Param("completeStatusList")List<SupervisionSubTaskStatusEnum> completeStatusList,
                                                @Param("handleOverTimeStatus") Integer handleOverTimeStatus);

    /**
     * 数量
     * @param enterpriseId
     * @param parentId
     * @param storeIds
     * @param completeStatus
     * @return
     */
    Long countByParentId(@Param("enterpriseId") String enterpriseId,
                         @Param("parentId") Long parentId,
                         @Param("supervisionTaskId") Long supervisionTaskId,
                         @Param("storeIds") List<String> storeIds,
                         @Param("regionIds") List<String> regionIds,
                         @Param("userName") String userName,
                         @Param("completeStatusList")List<SupervisionSubTaskStatusEnum> completeStatusList,
                         @Param("handleOverTimeStatus") Integer handleOverTimeStatus);



    List<SupervisionStoreTaskDO> getSupervisionStoreList(@Param("enterpriseId") String enterpriseId,
                                                         @Param("taskId") Long taskId,
                                                         @Param("userId") String userId,
                                                         @Param("taskState") Integer taskState,
                                                         @Param("handleOverTimeStatus") Integer handleOverTimeStatus,
                                                         @Param("storeName") String storeName);

    List<SupervisionStoreDataDTO> getStoreIdList(@Param("enterpriseId") String enterpriseId,
                                                 @Param("userId") String userId);


    Long noCompleteListByTaskId(@Param("enterpriseId") String enterpriseId,
                                @Param("taskId") Long taskId);

    SupervisionStoreTaskDO completeStatus(@Param("enterpriseId") String enterpriseId,
                                @Param("taskId") Long taskId);

    List<SupervisionStoreTaskDO> listMySupervisionStoreTask(@Param("enterpriseId") String enterpriseId,
                                                            @Param("query") SupervisionStoreTaskQueryRequest query,
                                                            @Param("startTime") String startTime,
                                                            @Param("endTime") String endTime,
                                                            @Param("priority") String priority,
                                                            @Param("taskStatus") Integer taskStatus,
                                                            @Param("handleOverTimeStatus") Integer handleOverTimeStatus);


    int batchInsert(@Param("enterpriseId") String enterpriseId,@Param("records") List<SupervisionStoreTaskDO> records);

    int batchUpdateTaskStatus(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionStoreTaskDO> idList);

    int batchUpdateStoreTask(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionStoreTaskDO> idList);

    int updateTaskStatus(@Param("enterpriseId")String enterpriseId, @Param("id")Long id, @Param("status")Integer status,Integer currentNode);

    List<SupervisionStoreTaskDO> listSupervisionStoreTask(@Param("enterpriseId") String enterpriseId,List<Long> ids);

    List<SupervisionStoreTaskDO> listSupervisionStoreTaskBySupervisionTaskId(@Param("enterpriseId") String enterpriseId, List<Long> ids,
                                                                             @Param("filterCancel") Boolean filterCancel,@Param("filterTaskState") Boolean filterTaskState);


    /**
     * 按门店任务明细
     * @param enterpriseId
     * @param formId
     * @return
     */
    List<SupervisionStoreTaskDO> listSupervisionStoreTaskByFormId(@Param("enterpriseId") String enterpriseId, @Param("formId") String formId, @Param("taskParentIds") List<Long> taskParentIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Long countSupervisionStoreTaskByFormId(@Param("enterpriseId") String enterpriseId, @Param("formId") String formId, @Param("taskParentIds") List<Long> taskParentIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    int batchUpdateStateBySupervisionTaskId(@Param("enterpriseId")String enterpriseId, @Param("idList")List<Long> idList, @Param("status")Integer status);

    int updateByParentId(@Param("enterpriseId") String enterpriseId,
                         @Param("taskName") String taskName,
                         @Param("taskGrouping") String taskGrouping,
                         @Param("taskEndTime") Date taskEndTime,
                         @Param("parentId") Long parentId,
                         @Param("reminderTimeBeforeEnd") Date reminderTimeBeforeEnd);


    List<SupervisionStoreTaskDO> listStoreTaskBySupervisionTaskId(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("ids") List<Long> ids,
                                                                  @Param("storeName") String storeName,
                                                                  @Param("filterTransferReassign") Boolean filterTransferReassign,
                                                                  @Param("filterCancel") Boolean filterCancel,
                                                                  @Param("filterComplete") Boolean filterComplete);

    Integer countStoreTaskBySupervisionTaskId(@Param("enterpriseId") String enterpriseId,
                                                                  @Param("ids") List<Long> ids);

    /**
     * 按人任务明细
     * @param enterpriseId
     * @return
     */
    List<SupervisionStoreTaskDO> listReminderBeforeSupervisionTask(@Param("enterpriseId") String enterpriseId,@Param("reminderTimeBeforeStarting") String reminderTimeBeforeStarting);

    List<SupervisionStoreTaskDO> listReminderAfterSupervisionTask(@Param("enterpriseId") String enterpriseId,@Param("reminderTimeAfterEnd") String reminderTimeAfterEnd);

    /**
     * 查询按人任务完成情况
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    List<SupervisionStoreTaskBasicDataDTO> supervisionStoreTaskBasicData(@Param("enterpriseId") String enterpriseId, @Param("taskIds") List<Long> taskIds);

    /**
     * 批量修改任务状态
     * @param enterpriseId
     * @param records
     * @return
     */
    Integer batchUpdateStoreTaskStatus(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionStoreTaskDO> records);

    List<SupervisionStoreTaskDO> listSupervisionStoreTaskIdList(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);

    /**
     * 带处理的数据 逾期 数据定时订正
     * @param enterpriseId
     * @return
     */
    int updateHandleOverTimeData(@Param("enterpriseId") String enterpriseId);

    List<SupervisionStoreTaskDO> correctData(@Param("enterpriseId") String enterpriseId);
}