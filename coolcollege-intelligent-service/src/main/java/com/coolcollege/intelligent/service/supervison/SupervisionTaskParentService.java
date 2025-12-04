package com.coolcollege.intelligent.service.supervison;

import com.coolcollege.intelligent.common.enums.supervison.SupervisionTaskPriorityEnum;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.SupervisionParentStatusEnum;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.supervision.request.AddSupervisionTaskParentRequest;
import com.coolcollege.intelligent.model.supervision.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 15:25
 * @Version 1.0
 */
public interface SupervisionTaskParentService {


    /**
     * 督导任务定义
     * @param enterpriseId
     * @param user
     * @param request
     * @return
     */
    Boolean addSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request);


    /**
     * 校验父任务是否有效
     * @param enterpriseId
     * @param request
     * @return
     */
    Integer checkSupervisionTaskParent(String enterpriseId,CurrentUser currentUser,AddSupervisionTaskParentRequest request);


    /**
     * 暂存督导助手任务定义
     * @param enterpriseId
     * @param user
     * @param request
     * @return
     */
    Boolean stagingSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request);

    /**
     * 查询暂存的数据
     * @param enterpriseId
     * @return
     */
    SupervisionTaskParentDetailVO getStagingSupervisionTaskParent(String enterpriseId,CurrentUser user);

    /**
     * 任务编辑
     * @param enterpriseId
     * @param user
     * @param request
     * @return
     */
    Boolean editSupervisionTaskParent(String enterpriseId, CurrentUser user, AddSupervisionTaskParentRequest request);

    /**
     * 任务分解
     * @param enterpriseId
     * @param currentUser
     * @param parentId
     */
    void  splitSupervisionTaskForPerson(String enterpriseId, CurrentUser currentUser,Long parentId,Long taskStartTime) throws Exception;

    /**
     * 任务列表
     * @param enterpriseId
     * @param taskName
     * @param startTime
     * @param endTime
     * @param statusEnumList
     * @param pageSize
     * @param pageNum
     * @param supervisionTaskPriorityEnums
     * @param taskGroupingList
     * @param tags
     * @return
     */
    PageInfo<SupervisionTaskParentVO> getSupervisionTaskParentList(String enterpriseId, String taskName, Long startTime, Long endTime, List<SupervisionParentStatusEnum> statusEnumList,
                                                                   Integer pageSize, Integer pageNum, List<SupervisionTaskPriorityEnum> supervisionTaskPriorityEnums, List<String> taskGroupingList,List<String> tags);

    /**
     * 任务取消
     * @param enterpriseId
     * @param taskId
     * @return
     */
    Boolean taskCancel(String enterpriseId, Long taskId, EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 任务删除
     * @param enterpriseId
     * @param taskId
     * @return
     */

    Boolean taskDel(String enterpriseId,Long taskId,EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 按任务 数据列表
     * @param enterpriseId
     * @param parentId
     * @param userIds
     * @param completeStatus
     * @return
     */
    PageInfo<SupervisionTaskDataVO> listSupervisionTaskByParentId(String enterpriseId, Long parentId, String  userName, List<SupervisionSubTaskStatusEnum> completeStatusList, Integer pageSize, Integer pageNum,Integer handleOverTimeStatus);


    ImportTaskDO listSupervisionTaskByParentIdExport(String enterpriseId, Long parentId,String userName,List<SupervisionSubTaskStatusEnum> completeStatusList,CurrentUser user,Integer handleOverTimeStatus);

    SupervisionTaskParentDetailVO selectDetailById(String enterpriseId,Long id,CurrentUser user);

    /**
     * 按门店 数据列表
     * @param enterpriseId
     * @param parentId
     * @param storeIds
     * @param completeStatus
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionStoreTaskDataVO> listSupervisionStoreTaskByParentId(String enterpriseId, Long parentId, Long supervisionTaskId,List<String>  storeIds,List<String>  regionIds,String  userName,
                                                                            List<SupervisionSubTaskStatusEnum> completeStatusList, Integer pageSize, Integer pageNum,Integer handleOverTimeStatus);


    /**
     * 按门店 数据导出
     * @param enterpriseId
     * @param parentId
     * @param storeIds
     * @param completeStatus
     * @param user
     * @return
     */
    ImportTaskDO listSupervisionStoreTaskByParentIdExport(String enterpriseId, Long parentId, List<String> storeIds,String userName,  List<SupervisionSubTaskStatusEnum> completeStatusList,
                                                          CurrentUser user,Long taskId,List<String> regionIds,Integer handleOverTimeStatus);

    /**
     * 任务明细
     * @param enterpriseId
     * @param tbMetaTableId
     * @param parentIds
     * @param submitStartTime
     * @param submitEndTime
     * @param type
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<SupervisionStoreTaskDataVO> taskDetail(String enterpriseId, Long tbMetaTableId, List<Long> parentIds, Long submitStartTime, Long submitEndTime, String type, Integer pageSize, Integer pageNum);


    ImportTaskDO taskDetailExport(String enterpriseId, Long tbMetaTableId, List<Long> parentIds, Long submitStartTime, Long submitEndTime, String type,CurrentUser user);

    List<SupervisionHistoryHandleVO> getSupervisionHistoryHandleVO(String eid,Long taskId,String type);


    Integer getTaskHandleOverTimeStatus(Integer handleOverTimeStatus,Long taskEndTime,Integer taskState);

    /**
     * 历史数据订正
     * @param enterpriseId
     * @return
     */
    Boolean SupervisionHistoryCorrect(String enterpriseId);
}
