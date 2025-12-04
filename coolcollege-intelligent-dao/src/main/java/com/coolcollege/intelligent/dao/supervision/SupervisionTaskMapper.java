package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import io.swagger.models.auth.In;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskHandleRequest;
import com.coolcollege.intelligent.model.supervision.request.SupervisionTaskQueryRequest;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2023-02-01 02:19
 */
public interface SupervisionTaskMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-02-01 02:19
     */
    int insertSelective(@Param("record") SupervisionTaskDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-01 02:19
     */
    SupervisionTaskDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-01 02:19
     */
    int updateByPrimaryKeySelective(@Param("record") SupervisionTaskDO record, @Param("enterpriseId") String enterpriseId);

    int updateStatus(@Param("taskState") Integer taskState , @Param("completeTime") Date completeTime, @Param("id") Long id, @Param("enterpriseId") String enterpriseId);


    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-01 02:19
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     * @param enterpriseId
     * @param records
     * @return
     */
    int batchInsert(@Param("enterpriseId") String enterpriseId,@Param("records") List<SupervisionTaskDO> records);

    /**
     * 修改任务名称与结束时间
     * @param enterpriseId
     * @param taskName
     * @param taskEndTime
     * @param parentId
     * @return
     */
    int updateByParentId(@Param("enterpriseId") String enterpriseId,
                         @Param("taskName") String taskName,
                         @Param("taskGrouping") String taskGrouping,
                         @Param("taskEndTime") Date taskEndTime,
                         @Param("parentId") Long parentId,
                         @Param("reminderTimeBeforeEnd") Date reminderTimeBeforeEnd);




    List<SupervisionTaskDO> listMySupervisionTask(@Param("enterpriseId") String enterpriseId,
                                                  @Param("query") SupervisionTaskQueryRequest query,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime,
                                                  @Param("priorityList") List<String> priorityList,
                                                  @Param("taskGroupingList") List<String> taskGroupingList,
                                                  @Param("handleOverTimeStatus") Integer handleOverTimeStatus,
                                                  @Param("taskStatusEnumList") List<SupervisionSubTaskStatusEnum> taskStatusEnumList);

    int updateTaskCompleteInfo(@Param("enterpriseId") String enterpriseId,
                               @Param("request") SupervisionTaskHandleRequest request,
                               @Param("currentNode") Integer currentNode,
                               @Param("handleOverTimeStatus") Integer handleOverTimeStatus );

    int batchUpdateTaskStatus(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionTaskDO> idList);

    int batchUpdateTaskStatusAndCancelStatus(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionTaskDO> idList);

    List<SupervisionTaskDO> listByParentId(@Param("enterpriseId") String enterpriseId,
                                           @Param("parentId") Long parentId,
                                           @Param("userName")String userName,
                                           @Param("completeStatusList")List<SupervisionSubTaskStatusEnum> completeStatusList,
                                           @Param("handleOverTimeStatus") Integer handleOverTimeStatus);

    Long countByParentId(@Param("enterpriseId") String enterpriseId,
                         @Param("parentId") Long parentId,
                         @Param("userName")String userName,
                         @Param("completeStatusList")List<SupervisionSubTaskStatusEnum> completeStatusList,
                         @Param("handleOverTimeStatus") Integer handleOverTimeStatus);


    int taskCancel(@Param("enterpriseId") String enterpriseId,
                   @Param("taskId") Long taskId,
                   @Param("id") Long id);

    int taskDel(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 根据父任务ID查询未完成的列表
     * @param enterpriseId
     * @param parentId
     * @return
     */
    List<SupervisionTaskDO> noCompleteListByParentId(@Param("enterpriseId") String enterpriseId,
                                                     @Param("parentId") Long parentId);

    /**
     * 根据父任务ID查询未取消的数量
     * @param enterpriseId
     * @param parentId
     * @return
     */
    Integer notCancelCountByParentId(@Param("enterpriseId") String enterpriseId,
                                     @Param("parentId") Long parentId);


    /**
     * 按人任务明细
     * @param enterpriseId
     * @param formId
     * @return
     */
    List<SupervisionTaskDO> listSupervisionTaskByFormId(@Param("enterpriseId") String enterpriseId,@Param("formId") String formId, @Param("taskParentIds") List<Long> taskParentIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    Long countSupervisionTaskByFormId(@Param("enterpriseId") String enterpriseId,@Param("formId") String formId, @Param("taskParentIds") List<Long> taskParentIds, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    List<SupervisionTaskDO> listByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    List<SupervisionTaskDO> listReminderBeforeSupervisionTask(@Param("enterpriseId") String enterpriseId,@Param("reminderTimeBeforeStarting") String reminderTimeBeforeStarting);

    List<SupervisionTaskDO> listReminderAfterSupervisionTask(@Param("enterpriseId") String enterpriseId,@Param("reminderTimeAfterEnd") String reminderTimeAfterEnd);

    int batchUpdateTask(@Param("enterpriseId")String enterpriseId, @Param("records")List<SupervisionTaskDO> supervisionTaskDOS);

    /**
     * 根据父任务ID与执行人名称查询任务
     * @param enterpriseId
     * @param parentId
     * @param userId
     * @return
     */
    SupervisionTaskDO selectSupervisionTask(@Param("enterpriseId") String enterpriseId, @Param("parentId") Long parentId, @Param("userId") String userId);


    int updateHandleOverTimeData(@Param("enterpriseId") String enterpriseId);


    List<SupervisionTaskDO> correctData(@Param("enterpriseId") String enterpriseId);

}