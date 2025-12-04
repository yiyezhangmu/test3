package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayTaskVO;
import com.coolcollege.intelligent.model.common.IdListDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaTableDTO;
import com.coolcollege.intelligent.model.metatable.vo.TaskStoreMetaDataVO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.*;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreLoopQuery;
import com.coolcollege.intelligent.model.unifytask.query.TaskStoreQuery;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskClearRequest;
import com.coolcollege.intelligent.model.unifytask.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author byd
 * @date 2021-02-22 10:33
 */
public interface UnifyTaskStoreService {


    void updateTaskStoreDOBySubTask(String enterpriseId, TaskSubDO taskSubDO);


    void batchInsertTaskStore(String enterpriseId, List<TaskStoreDO> taskStoreList);


    int delTaskStoreByParentTaskId(String enterpriseId, Long parentTaskId);


    /**
     * 查询列表
     *
     * @param enterpriseId 企业id
     * @param query        查询条件
     * @return
     */
    TaskStoreDayVO selectStoreClearList(String enterpriseId, TaskStoreQuery query);


    List<TaskStoreClearVO> getStoreClearListNew(String enterpriseId, String dbName, StoreTaskClearRequest request);

    TaskSubVO jumpDetail(String enterpriseId, Long taskStoreId, CurrentUser currentUser);

    /**
     * 门店任务列表
     * @param enterpriseId
     */
    PageInfo taskStoreList(String enterpriseId, TaskStoreLoopQuery query);

    /**
     * 门店任务列表
     * @param enterpriseId
     */
    TaskStoreStageCount taskStoreListCount(String enterpriseId, TaskStoreLoopQuery query);

    /**
     * 阶段任务列表
     * @param enterpriseId
     */
    List<TaskStoreStageVO> taskStageList(String enterpriseId, Long unifyTaskId, String status);

    /**
     * 阶段任务列表
     * @param enterpriseId
     */
    TaskStoreStageCount taskStageListCount(String enterpriseId, Long unifyTaskId);

    /**
     * 陈列门店任务列表
     * @param enterpriseId
     * @param query
     * @return
     */
    SubTaskDTO displayStoreTaskList(String enterpriseId, TaskStoreLoopQuery query);

    /**
     * 陈列门店任务列表db
     * @param enterpriseId
     * @param query
     * @return
     */
    SubTaskDTO displayStoreTaskNewList(String enterpriseId, TaskStoreLoopQuery query);

    /**
     * 获取门店任务当前节点新增、删除的人，
     * @param enterpriseId
     * @param taskStoreDO
     * @param handerUserList
     * @param approveUserList
     * @param recheckUserList
     * @return
     */
    Map<String, List<String>> getCurrentNodePersonChangeMap(String enterpriseId, TaskStoreDO taskStoreDO, List<String> handerUserList,
                                                            List<String> approveUserList, List<String> recheckUserList, List<String> thirdApproveUserList,
                                                            List<String> fourApproveUserList, List<String> fiveApproveUserList);

    /**
     * 查找门店任务所有节点人员
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @return
     */
    Map<String, List<String>> selectTaskStorAllNodePerson(String enterpriseId, Long taskId, String storeId, Long loopCount);

    /**
     * 转交替换相应节点人员
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @param node
     * @param fromUserId
     * @param toUserId
     * @return TaskStoreDO
     */
    TaskStoreDO replaceTaskStoreNodePerson(String enterpriseId, Long taskId, String storeId, Long loopCount, String node, String fromUserId, String toUserId);

    /**
     * 分页获取需要订正的门店任务
     * @param enterpriseId
     * @return
     */
    List<TaskStoreDO> listTaskStoreByEid(String enterpriseId, Boolean isRunIncrement, Long maxId);

//    /**
//     * 订正门店任务节点人员
//     * @param enterpriseId
//     * @param taskStoreDO
//     */
//    void correctionTaskStoreNodePerson(String enterpriseId, TaskStoreDO taskStoreDO, String dbName);

    List<String> selectCcPersonInfoByTaskList(String enterpriseId, Long taskId, String storeId, Long loopCount);

    List<String> selectAuditUserIdList(String enterpriseId, Long taskId, String storeId, Long loopCount);

    /**
     * 查找指定任务 指定门店集合下的人员  包括 创建人、处理人、抄送人、审批人。。。 单个分享、批量分享、巡店分享
     * @param enterpriseId
     * @param storeIdList
     * @param taskId
     * @return
     */
    List<StorePersonDto> selectTaskPersonByTaskAndStore(String enterpriseId, List<String> storeIdList, Long taskId);
    /**
     * 散开门店任务节点人员信息
     * @param enterpriseId
     * @param taskIdList
     * @param storeIdList
     * @return
     */
    List<UnifyPersonDTO> selectALLNodeUserInfoList(String enterpriseId, List<Long> taskIdList, List<String> storeIdList, Long loopCount);

    /**
     * 更新重新分配后的人员
     * @param enterpriseId
     * @param taskStoreDO
     * @param handerUserList
     * @param approveUserList
     * @param recheckUserList
     */
    String updateReallocateNodePerson(String enterpriseId, TaskStoreDO taskStoreDO, List<String> handerUserList, List<String> approveUserList, List<String> recheckUserList ,
                                    List<String> thirdApproveUserList , List<String> fourApproveUserList, List<String> fiveApproveUserList,
                                      TaskSubVO taskSubVO, List<String> newaddPersonList);

    void fillSingleTaskStoreExtendAndCcInfo(String enterpriseId, TaskStoreDO  taskStoreDO);

    void updateHandlerUserAfterReject(String enterpriseId, TaskSubDO taskSubDO, String handerUser);

    /**
     * 获得门店最新工单详情
     * @param enterpriseId
     * @param storeId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.unifytask.vo.TaskStoreQuestionDataVO
     * @date: 2022/3/1 16:14
     */
    TaskStoreQuestionDataVO getLastTaskStoreQuestion(String enterpriseId, String storeId);

    /**
     * 获得门店最新检查表使用情况
     * @param enterpriseId
     * @param storeId
     * @param query
     * @param user
     * @author: xugangkun
     * @return void
     * @date: 2022/3/8 10:59
     */
    List<TaskStoreMetaDataVO> getStoreMetaTableData(String enterpriseId, String storeId, List<TbMetaTableDTO> query, CurrentUser user);

    /**
     * 门店任务-查询当前任务节点的处理人
     * @param enterpriseId 企业id
     * @param taskStoreId 门店任务id
     * @param nodeNo 任务节点
     * @return List<PersonDTO>
     */
    List<PersonDTO> getCurrentNodePerson(String enterpriseId, Long taskStoreId, String nodeNo);

    /**
     * 门店任务-查询任务所有节点的处理人
     * @param enterpriseId 企业id
     * @param taskStoreId 门店任务id
     * @return TaskPersonVO
     */
    TaskPersonVO getNodePersonForReallocate(String enterpriseId, Long taskStoreId);

    Map<String, List<String>> getNodePersonByTaskStore(TaskStoreDO taskStoreDO);

    TaskStoreDO getTaskStoreDetail(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);

    /**
     * 对增加的人员进行任务补发
     * @param enterpriseId
     * @param taskId
     * @param storeId
     * @param loopCount
     * @param unifySubTaskForStoreData
     */
    void taskReissue(String enterpriseId, Long taskId, String storeId, Long loopCount, UnifySubTaskForStoreData unifySubTaskForStoreData);

    void taskReissue(String enterpriseId, Long taskId, String storeId, Long loopCount, List<TaskMappingDO> personList);


    Map<Long, List<String>> getTaskStoreUserIdMap(List<TaskStoreDO> taskStoreDOList);

    List<TaskStoreDO> selectByUnifyTaskId(String enterpriseId, Long parentTaskId);

    CombineTaskStoreDTO combineTaskList(String enterpriseId, TaskStoreLoopQuery query);

    String getTaskHandlerUserId(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);

    Map<String, List<PersonDTO>> getTaskPerson(String enterpriseId, List<TaskStoreDO> taskStoreDOList);

    /**
     * 入参用户为处理人/审批人的陈列任务列表
     * @param enterpriseId 企业id
     * @param reqDTO 陈列任务DTO
     * @return 陈列任务VO列表
     */
    List<DisplayTaskVO> getDisplayStoreTaskList(String enterpriseId, DisplayTaskDTO reqDTO);

    /**
     *
     * @param enterpriseId
     * @param idListDTO
     */
    void batchStopTask(String enterpriseId, IdListDTO idListDTO, EnterpriseConfigDO enterpriseConfig, CurrentUser user);

    /**
     *
     * @param enterpriseId
     * @param postponeTaskDTO
     */
    void batchPostponeTask(String enterpriseId, PostponeTaskDTO postponeTaskDTO, EnterpriseConfigDO enterpriseConfig, CurrentUser user);
}
