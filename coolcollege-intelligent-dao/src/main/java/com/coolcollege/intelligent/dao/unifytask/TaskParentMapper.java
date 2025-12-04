package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyParentCount;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyParentStatisticsDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskParentQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import com.coolcollege.intelligent.model.unifytask.query.TbDisplayQuery;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.TaskParentCycleVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 16:19
 */
@Mapper
public interface TaskParentMapper {

    /**
     * 新增父任务
     * @param enterpriseId
     * @param taskParentDO
     */
    void insertTaskParent(@Param("enterpriseId") String enterpriseId, @Param("parent") TaskParentDO taskParentDO);

    /***
     * 根据主键ID更新主表
     * @param enterpriseId
     * @param taskParentDO
     * @param id
     */
    void updateParentTaskById(@Param("enterpriseId") String enterpriseId, @Param("parent") TaskParentDO taskParentDO, @Param("id") Long id);

    void updateLoopCountById(@Param("enterpriseId") String enterpriseId, @Param("loopCount") long loopCount, @Param("id") Long id);

    /**
     * 通过ID查询任务
     * @param enterpriseId 企业ID
     * @param taskId 任务ID
     * @return TaskParentDO
     */
    TaskParentDO selectTaskById(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 查询我的父任务列表
     * @param enterpriseId
     * @param userId
     * @param query
     * @return
     */
    List<TaskParentDO> selectParentTaskByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,
                                                @Param("query") DisplayQuery query);

    /**
     * 根据任务id列表查询我的父任务列表
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    List<TaskParentDO> selectParentTaskByTaskIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> taskIds);

    /**
     * 查询我的父任务列表-管理员
     * @param enterpriseId
     * @param query
     * @return
     */
    List<TaskParentDO> selectParentTaskByAdmin(@Param("enterpriseId") String enterpriseId,
                                               @Param("query") DisplayQuery query);

    /**
     * 查询我的父任务列表
     * @param enterpriseId

     * @return
     */
    List<TaskParentDO> selectTaskQuestionList(@Param("enterpriseId") String enterpriseId,
                                               @Param("taskType") String taskType,@Param("userIdList") List<String> userIdList,@Param("beginTime") Long beginTime,
                                              @Param("endTime") Long endTime);

    Long selectTaskQuestionListCount(@Param("enterpriseId") String enterpriseId,
                                              @Param("taskType") String taskType,@Param("userIdList") List<String> userIdList, @Param("beginTime") Long beginTime,
                                              @Param("endTime") Long endTime);
    // 中间页
    List<TaskParentCycleVO> getParentMiddlePageData(@Param("enterpriseId") String enterpriseId,
                                                    @Param("query") TbDisplayQuery query);
    // 数据报表页
    List<TbDisplayTaskDataVO> getTbDisplayReportPageData(@Param("enterpriseId") String enterpriseId,
                                                         @Param("query") TbDisplayReportQueryParam query);
    /**
     * 父任务详情
     * @param enterpriseId
     * @param taskId
     * @return
     */
    TaskParentDO selectParentTaskById(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 查询我的父任务列表通过子任务id
     * @param enterpriseId
     * @param taskId
     * @return
     */
    TaskParentDO selectParentTaskByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 查询父任务开始时间
     * @param enterpriseId
     * @param taskId
     * @return
     */
    Long selectParentBeginTimeByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 统计父信息
     * @param enterpriseId
     * @param userId
     * @param taskType
     * @param status
     * @return
     */
    Integer selectDisplayParentStatistics(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId,
                                          @Param("taskType") String taskType,  @Param("status") String status);

    /**
     * 统计父信息-admin
     * @param enterpriseId
     * @param taskType
     * @param status
     * @return
     */
    List<Long> selectDisplayParentStatisticsByAdmin(@Param("enterpriseId") String enterpriseId,
                                             @Param("taskType") String taskType,  @Param("status") String status);

    /**
     * 统计父信息数量
     * @param enterpriseId
     * @param taskType
     * @param status
     * @return
     */
    Integer selectParentStatistics(@Param("enterpriseId") String enterpriseId,
                                                    @Param("taskType") String taskType,  @Param("status") String status ,
                                   @Param("overdueTaskContinue") Boolean overdueTaskContinue);

    UnifyParentStatisticsDTO selectParentStatisticsCount(@Param("enterpriseId") String enterpriseId,
                                                         @Param("taskType") String taskType,
                                                         @Param("overdueTaskContinue") Boolean overdueTaskContinue);
    /**
     * 查询父任务完成数量
     * @param enterpriseId
     * @param taskList
     * @return
     */
    List<UnifyParentCount> selectEndTaskCount(@Param("enterpriseId") String enterpriseId, @Param("taskList") List<Long> taskList);

    /**
     * 删除我的父任务通过子任务id
     * @param enterpriseId
     * @param taskId
     */
    void delParentTaskByTaskId(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long taskId);

    /**
     * 获取企业下所有陈列任务
     * @param enterpriseId
     */
    List<TaskParentDO> selectByEnterpriseId(@Param("enterpriseId") String enterpriseId, @Param("taskType") String taskType);

    /**
     * 根据父任务ids获取父任务列表
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    List<TaskParentDO> selectTaskByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> taskIds);

    /**
     * 根据父任务ids获取父任务列表
     * @param enterpriseId
     * @param taskIds
     * @return
     */
    List<TaskParentDO> selectTaskByIdsForMap(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> taskIds);

    /**
     * 批量获取父任务信息
     * @param enterpriseId
     * @param idList
     * @return
     */
    List<TaskParentDO> selectParentTaskBatch(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);

    void updateParentByDO(@Param("enterpriseId") String enterpriseId, @Param("parent") TaskParentDO parentDO);

    /**
     * 更新taskInfo
     * @param enterpriseId
     * @param id
     * @param taskInfo
     * @param updateTime
     */
    void updateTaskInfoById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,
                                  @Param("taskInfo") String taskInfo, @Param("updateTime") Long updateTime);

    /**
     * 更新
     * @param enterpriseId
     * @param id
     * @param updateTime
     */
    void clearScheduleIdByTaskId(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("updateTime") Long updateTime);

    /**
     * 查询我的父任务列表
     * @param enterpriseId
     * @param userId
     * @param query
     * @return
     */
    List<TaskParentDO> selectParentTaskList(@Param("enterpriseId") String enterpriseId,
                                            @Param("userId") String userId,
                                            @Param("query") TaskParentQuery query,
                                            @Param("isAdmin") Boolean isAdmin,
                                            @Param("unifyTaskIds") List<String> unifyTaskIds);

    // 任务报表
    Long countTaskReport(@Param("enterpriseId") String enterpriseId, @Param("query") TaskReportQuery query);

    // 任务报表
    List<TaskReportVO> listTaskReport(@Param("enterpriseId") String enterpriseId
                            , @Param("query") TaskReportQuery query);

    // 父任务结束时间 倒序前200个
    List<Long> listTaskIdsOrderByEndTime(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);

    Long getExportTotal(@Param("enterpriseId") String enterpriseId, @Param("taskId") Long unifyTaskId);

    /**
     * 查询父任务列表（按人任务）
     * @param enterpriseId
     * @param request
     * @return
     */
    List<TaskParentDO> selectTaskParentForPerson(@Param("enterpriseId") String enterpriseId, @Param("params") GetTaskByPersonRequest request);

    void updateParentStatusByTaskId(@Param("enterpriseId") String enterpriseId, @Param("parentStatus") String parentStatus,@Param("taskId") Long taskId);


    void copyTaskParent(@Param("enterpriseId") String enterpriseId, @Param("parent") TaskParentDO taskParentDO);

    /**
     * 编辑督导父任务
     * @param enterpriseId
     * @param taskParentDO
     * @return
     */
    Boolean updateSuperVisionParent(@Param("enterpriseId") String enterpriseId, @Param("parent") TaskParentDO taskParentDO);

    String getProductNoBySubTaskId(@Param("enterpriseId") String enterpriseId,
                                   @Param("parentTaskId") String parentTaskId);

    void stopTask(@Param("enterpriseId") String enterpriseId,
                        @Param("parentTaskId") Long parentTaskId);

    // 根据工单实例id，
    TaskParentDO getByExtraParam(@Param("enterpriseId") String enterpriseId, @Param("extraParam") String extraParam);

    List<TaskParentDO> getByRuleIds(@Param("enterpriseId") String enterpriseId,
                                    @Param("storeOpenRuleIds") List<Long> storeOpenRuleIds);

    List<TaskParentDO> selectSxTask(@Param("eid") String enterpriseId);
    /**
     * 根据父任务名称查询父任务id
     * @param name 任务名称
     * @return 父任务id列表
     */
    List<Long> selectIdByName(
            @Param("enterpriseId") String enterpriseId,
            @Param("name") String name
    );
}
