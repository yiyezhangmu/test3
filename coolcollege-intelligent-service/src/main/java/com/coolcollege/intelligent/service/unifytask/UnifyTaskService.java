package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.ApproveDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.TaskParentQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskQuestionQuery;
import com.coolcollege.intelligent.model.unifytask.request.BuildByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.request.GetTaskDetailByPersonRequest;
import com.coolcollege.intelligent.model.unifytask.vo.GetTaskByPersonVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskProcessVO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.model.workFlow.WorkflowDealDTO;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 15:26
 */
public interface UnifyTaskService {

    /**
     * 添加任务人员信息
     * @param taskId
     * @param fromUserId
     * @param toUserId
     * @param storeId
     * @param enterpriseId
     * @param node
     * @param loopCount
     */
    void addAboutTurnPeople(Long taskId, String fromUserId, String toUserId, String storeId, String enterpriseId, String node, Long loopCount);

    /**
     * 任务新增
     * @param enterpriseId 企业ID
     * @param task 数据
     * @param userId 创建人id
     * @param createTime
     */
    TaskMessageDTO insertUnifyTask(String enterpriseId, UnifyTaskBuildDTO task, String userId, long createTime);

    void reissueSubTask(String enterpriseId, Long taskId, String storeIds, Long loopCount);

    /**
     * 任务分解
     * @param enterpriseId
     * @param taskId
     * @param dbName
     * @param isReissue 是否补发 基于当前数据进行补发
     * @param isRefresh 是否刷新  刷新是基于全局刷新任务配置 做对应刷新
     */
    void taskParentResolve(String enterpriseId, Long taskId, String dbName, boolean isReissue, boolean isRefresh);

    /**
     * 任务刷新
     * @param enterpriseId 企业id
     * @param taskId 父任务id
     * @param dbName 数据库名称
     */
    void taskRefresh(String enterpriseId, Long taskId, String dbName);


    Set<String> getStoreIdList(String enterpriseId, List<GeneralDTO> storeGeneralList, String userId, boolean filterStoresWthoutPersonnel);

    /**
     * 流程引擎推动任务引擎
     * @param corpId
     * @param flow
     * @param enterpriseId
     * @param checkSettingDO
     * @param appType
     */
    void sendTask(String corpId, WorkflowDealDTO flow, String enterpriseId, EnterpriseStoreCheckSettingDO checkSettingDO, String appType);

    /**
     * 父任务删除
     *
     * @param enterpriseId 企业ID
     * @param unifyTaskId 父任务id
     */
    void delUnifyTask(String enterpriseId, Long unifyTaskId,String operateType);

    /**
     * 批量删除父任务
     * @param enterpriseId
     * @param unifyTaskIdList
     */
    void batchDelUnifyTask(String enterpriseId, List<Long> unifyTaskIdList, String dingCorpId,String appType);

    /**
     * 任务编辑
     *  @param enterpriseId
     * @param taskId
     * @param task
     */
    TaskParentDO changeUnifyTask(String enterpriseId, Long taskId, UnifyTaskBuildDTO task, CurrentUser user, String dingCorpId,String appType);

    /**
     * 任务转交
     * @param enterpriseId
     * @param task
     * @param user
     */
    void turnTask(String enterpriseId, UnifyTaskTurnDTO task, CurrentUser user);

    /**
     * 子任务完成
     * @param enterpriseId 企业id
     * @param subTaskId 子任务id
     * @param remark 非必填：备注
     * @param taskData 非必填：统一子任务审批数据
     */
    String completeSubTask(String enterpriseId, Long subTaskId,String remark, String taskData);

    /**
     * 子任务id获取消息通知相关
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    TaskMessageDTO getMessageBySubTaskId(String enterpriseId, Long subTaskId);

    /**
     * 根据子任务获取或签的所有子任务
     * 
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    List<Long> getOrSubTaskIds(String enterpriseId, Long subTaskId);


    /**
     * 发送未完任务消息
     * @param enterpriseId 企业id
     * @param taskId 父任务id
     */
    String sendUnifyTaskDing(String enterpriseId, Long taskId, Boolean isSentMsg);

    /**
     * 发送未完任务消息
     * @param enterpriseId 企业id
     * @param taskId 父任务id
     */
    String sendUnifyTaskTestDing(String enterpriseId, Long taskId);


    /**
     * 补发工作通知
     * @param enterpriseId
     * @param taskId
     * @param isSentMsg
     */
    void reissueDingNotice(String enterpriseId, Long taskId, String storeId, Long loopCount, Boolean isSentMsg);

    /**
     * 查看任务是否有检查表权限
     * @param enterpriseId
     * @param unifyTaskId
     * @param userId
     * @return
     */
    boolean hasCheckTableAuth(String enterpriseId, Long unifyTaskId, String userId);

    TaskMessageDTO buildSubTaskBySingleStore(String enterpriseId, String storeId, Set<String> userSet
            , Long parentTaskId, Long newLoopCount, Long createTime, Set<String> ccUserSet, UnifySubTaskForStoreData subTaskForStoreData);

    void sendNotice(String enterpriseId, TaskStoreDO taskStore, TaskParentDO parentDO, List<TaskSubDO> subTaskList, Boolean isCC);

    /**
     * 发送待办通知、抄送人通知
     * @param enterpriseId 企业id
     * @param parentDO 父任务
     * @param taskStoreDO 门店任务
     * @param taskSubDOList 新增子任务列表
     * @param ccTaskSubId 抄送用子任务id
     */
    void sendTaskJms(String enterpriseId, TaskParentDO parentDO, TaskStoreDO taskStoreDO, List<TaskSubDO> taskSubDOList, Boolean isCc, List<String> ccUserIds, Long ccTaskSubId);

    /**
     * 发送待办通知
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param storeId 门店id
     * @param storeName 门店名称
     * @param loopCount 轮次
     * @param nodeNo 任务节点
     * @param taskType 任务类型
     * @param userIds 新增用户id
     * @param taskSubList 新增子任务列表
     * @param createUserName 创建人名称
     * @param taskName 任务名称
     * @param subBeginTime 审批链任务开始时间
     * @param subEndTime 审批链任务结束时间
     */
    void sendTaskJms(String enterpriseId, Long unifyTaskId, String storeId, String storeName, Long loopCount, String nodeNo,
                     String taskType, List<String> userIds, List<TaskSubDO> taskSubList, String createUserName, String taskName, Long subBeginTime, Long subEndTime);
    /**
     * 获取合并outBusinessId
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param loopCount 轮次
     * @param nodeNo 节点
     * @return outBusinessId
     */
    String getCombineOutBusinessId(String enterpriseId, Long unifyTaskId, Long loopCount, String nodeNo);


    PageInfo taskQuestionReportList(String enterpriseId, List<String> userIdList, Long timeBegin, Long timeEnd, Integer pageNum, Integer pageSize);

    ImportTaskDO taskQuestionReportListExport(String enterpriseId, TaskQuestionQuery query);

    /**
     * 统计任务状态数量
     * @param enterpriseId
     * @param taskType
     * @return
     */
    UnifyParentStatisticsDTO getParentCount(String enterpriseId, String taskType, Boolean overdueTaskContinue);

    PageInfo selectParentTaskList(String enterpriseId, String userId, TaskParentQuery query);

    UnifyParentBuildDTO sendTaskToDayJudge(String enterpriseId, Long taskId);

    /**
     * 单次任务到时间才发起
     * @param enterpriseId
     * @param taskId
     * @param beginTime
     * @return
     */
    Boolean setSchedulerForOnce(String enterpriseId, Long taskId, Date beginTime,int isOperateOverdue);

    void sendDingNotice(String enterpriseId, Long taskId, Long loopCount);

    /**
     * 根据门店id与父任务Id查询数据（节点为endNode的数据）
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @return
     */
    List<ApproveDTO> taskSubList(String enterpriseId, String taskId, String storeId);

    /**
     * 查询指定任务id、门店id、轮次 各个节点的处理人、审批人
     * @param enterpriseId
     * @param taskId
     * @param storeIds
     * @param loopCount
     * @return
     */
    Map<String, List<UnifyPersonDTO>> getTaskPersonFromSubTask(String enterpriseId, Long taskId, List<String> storeIds, Long loopCount, String subStatus);

    /**
     * 检查用户有没有门店任务轮次的处理权限
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @param node
     * @param userId
     * @return
     */
    boolean checkHasHandleAuth(String enterpriseId, Long taskId, String storeId, Long loopCount,String node, String userId);



    /**
     * 任务转交
     * @param enterpriseId
     * @param task
     * @param user
     */
    void turnStoreTask(String enterpriseId, UnifyStoreTaskTurnDTO task, CurrentUser user);

    /**
     * 任务转交
     * @param enterpriseId
     * @param task
     * @param user
     */
    List<UnifyStoreTaskBatchErrorDTO> batchTurnStoreTask(String enterpriseId, UnifyStoreTaskBatchTurnDTO task, CurrentUser user);

    /**
     * 任务重新分配
     * @param enterpriseId
     * @param task
     * @param user
     */
    void reallocateStoreTask(String enterpriseId, ReallocateStoreTaskDTO task, String dingCorpId, CurrentUser user,String appType,Boolean isFill);

    /**
     * 批量任务重新分配
     * @param enterpriseId
     * @param task
     * @param user
     */
    List<UnifyStoreTaskBatchErrorDTO> batchReallocateStoreTask(String enterpriseId, ReallocateStoreTaskListDTO task, String dingCorpId, CurrentUser user,String appType);

    void sendTaskMessage(TaskMessageDTO taskMessage);

    ResponseResult getResponseResult(String enterpriseId, String taskId);

    /**
     * 新建按人任务
     * @param enterpriseId 企业id
     * @param user CurrentUser
     * @param request BuildStaffPlanRequest
     */
    void insertUnifyTaskByPerson(String enterpriseId, CurrentUser user, BuildByPersonRequest request);

    /**
     * 新建子任务（按人）
     * @param enterpriseId
     * @param userId
     * @param taskParentDO
     */
    void buildSubTaskByPerson(String enterpriseId, String userId,  TaskParentDO taskParentDO);

    /**
     * 查询按人任务（分页）
     * @param enterpriseId
     * @param request
     * @return
     */
    PageInfo<GetTaskByPersonVO> getTaskByPerson(String enterpriseId, GetTaskByPersonRequest request);

    /**
     * 查询按人任务详情
     * @param enterpriseId
     * @param request
     * @return
     */
    GetTaskByPersonVO getTaskDetailByPerson(String enterpriseId, GetTaskDetailByPersonRequest request);

    void getPerson(List<TaskProcessDTO> process, Long taskId, List<TaskMappingDO> personList,
                          Set<String> storeList, String enterpriseId, String createUserId,String taskType, Boolean addCreateUser ,Boolean userAuth);

    /**
     * 构建工单任务
     * @param enterpriseId
     * @param taskId
     */
    void buildTaskStoreQuestionOrder(String enterpriseId, Long taskId, Boolean isFilterUserAuth, boolean isRefresh);

    void setNotCompleteCache(SmallVideoParam smallVideoParam, SmallVideoDTO smallVideo, Long taskId, String enterpriseId);
    /**
     * 停止循环任务
     * @param enterpriseId
     * @param taskId
     */
    void stopTaskRun(String enterpriseId, Long taskId);

    void saveTaskParentUser(String enterpriseId, Long unifyTaskId, String taskType, List<String> personList);

    Map<Long, TaskProcessVO> dealTaskProcess(String enterpriseId, List<TaskProcessVO> taskProcessList);

    List<Long> reallocateStoreTaskPersonByNodeNew(String enterpriseId, TaskStoreDO taskStoreDO, TaskParentDO parentDO, List<String> newaddPersonList, List<String> removePersonList, Long createTime, String operUserId, TaskSubVO taskSubVO, boolean sendNotice);

    Map<String, Object> reallocateStoreTask(String enterpriseId, TaskStoreDO taskStoreDO, TaskParentDO parentDO, List<String> newaddPersonList, List<String> removePersonList, Long createTime, String operUserId, TaskSubVO taskSubVO, boolean sendNotice);

    List<GeneralDTO> productDeal(String eid,UnifyTaskBuildDTO task);

    TaskParentDO getByExtraParam(String enterpriseId, String extraParam);

    int updateQuestionRecordFinish(String enterpriseId,Date approveTime, String approveUserId, String approveUserName, String approveActionKey,
                                   Date handleTime,String handleUserId, String handleUserName,String handleActionKey,Long unifyTaskId);

    /**
     * 任务催办
     * @param enterpriseId
     * @param param
     * @return
     */
    void taskReminder(String enterpriseId, ParentTaskReminderDTO param);

    void cancelUpcoming(String enterpriseId, List<Long> subTaskIdList, String dingCorpId, String appType);

    /**
     * 取消合并待办
     */
    void cancelCombineUpcoming(String enterpriseId, Long unifyTaskId, Long loopCount, String storeId, String nodeNo, List<String> userIds, String dingCorpId, String appType);
}
